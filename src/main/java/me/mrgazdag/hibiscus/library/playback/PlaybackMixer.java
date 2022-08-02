package me.mrgazdag.hibiscus.library.playback;

import me.mrgazdag.hibiscus.library.playback.target.MixerTarget;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaybackMixer {

    private final UUID uuid;
    private MixerTarget target;
    private final Object lock;
    private MusicTrackMixerThread thread;
    private Status status;

    private final List<MusicChannel> tracks;
    private final LinePositionManager linePositionManager;

    public PlaybackMixer(UUID uuid) {
        this.uuid = uuid;
        this.lock = new Object();
        this.status = Status.PAUSED;

        this.tracks = new ArrayList<>();
        this.linePositionManager = new LinePositionManager();
    }

    public void setTarget(MixerTarget target) {
        boolean first = this.target == null;
        this.target = target;
        if (first) {
            this.thread = new MusicTrackMixerThread();
        }
    }

    public void addTrack(MusicChannel track) {
        synchronized (this.tracks) {
            this.tracks.add(track);
        }
    }

    public void removeTrack(MusicChannel track) {
        synchronized (this.tracks) {
            this.tracks.remove(track);
        }
    }

    public LinePositionManager getLinePositionManager() {
        return linePositionManager;
    }

    public void start() throws LineUnavailableException {
        target.start();
        thread.start();
    }

    public void play() {
        target.play();
        this.status = Status.PLAYING;
        synchronized (lock) {
            lock.notify();
        }
    }

    public void pause() {
        this.status = Status.PAUSED;
        synchronized (lock) {
            lock.notify();
        }
        target.pause();
    }

    public void shutdown() {
        this.status = Status.SHUTDOWN;
        synchronized (lock) {
            lock.notify();
        }
        target.stop();
    }

    private enum Status {
        PAUSED,PLAYING,SHUTDOWN
    }
    private class MusicTrackMixerThread extends Thread {
        private final byte[] emptyBuffer;
        private final int[] emptyIntBuffer;
        private final byte[] trackBuffer;
        private final byte[] mixerBuffer;
        private final int[] mixerIntBuffer;
        private static final int BUFFER_FRAME_COUNT = 4096;
        private int toReadFrameCount;

        public MusicTrackMixerThread() {
            super("Mixer Thread");
            this.emptyBuffer = new byte[BUFFER_FRAME_COUNT * target.getFormat().getFrameSize()];
            this.emptyIntBuffer = new int[BUFFER_FRAME_COUNT * target.getFormat().getChannels()];
            this.trackBuffer = new byte[BUFFER_FRAME_COUNT * target.getFormat().getFrameSize()];
            this.mixerBuffer = new byte[BUFFER_FRAME_COUNT * target.getFormat().getFrameSize()];
            this.mixerIntBuffer = new int[BUFFER_FRAME_COUNT * target.getFormat().getChannels()];
        }

        @Override
        public void run() {
            while (true) {
                if (status == Status.PLAYING) {
                    mixBatch();
                    doEvents();
                    tryPlay();
                } else if (status == Status.PAUSED) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ignored) {}
                    }
                } else if (status == Status.SHUTDOWN) {
                    //shut down
                    return;
                } else return; //???
            }
        }
        private int framesToBytes(long frames) {
            return (int) (frames * target.getFormat().getFrameSize());
        }
        private void writeEmpty() {
            System.arraycopy(emptyIntBuffer, 0, mixerIntBuffer, 0, mixerIntBuffer.length);
            System.arraycopy(emptyBuffer, 0, mixerBuffer, 0, mixerBuffer.length);
        }
        private int readSample(byte[] array, int offset, int bytesPerSample, boolean bigEndian) {
            int sample = 0;
            if (bigEndian) {
                for (int b = 0; b < bytesPerSample; b++) {
                    int byteValue = array[offset + b] & 0xff;
                    sample += byteValue << (8 * (bytesPerSample - b - 1));
                }
            } else {
                for (int b = 0; b < bytesPerSample; b++) {
                    int byteValue = array[offset + b] & 0xff;
                    sample += byteValue << 8 * b;
                }
            }
            return sample;
        }
        private void writeSample(byte[] array, int offset, int bytesPerSample, boolean bigEndian, int sample) {
            if (bigEndian) {
                for (int b = 0; b < bytesPerSample; b++) {
                    array[offset + b] = (byte)((sample >>> (8 * (bytesPerSample - b - 1))) & 0xFF);
                }
            } else {
                for (int b = 0; b < bytesPerSample; b++) {
                    array[offset + b] = (byte)((sample >>> (8 * b)) & 0xFF);
                }
            }
        }
        private void mixBatch() {
            toReadFrameCount = (int) Math.min(BUFFER_FRAME_COUNT, linePositionManager.framesUntilNext());
            //System.out.println("mixing " + readFrameCount + " frames");
            if (tracks.size() == 1) {
                try {
                    MusicChannel track = tracks.get(0);
                    if (!track.isPlaying()) {
                        writeEmpty();
                    } else {
                        long trackToRead = track.getLinePositionManager().framesUntilNext();
                        if (trackToRead < toReadFrameCount) {
                            toReadFrameCount = (int) trackToRead;
                            System.out.println("- no wait its " + toReadFrameCount + " frames");
                        }
                        int read = track.read(trackBuffer, toReadFrameCount);
                        System.arraycopy(trackBuffer, 0, mixerBuffer, 0, framesToBytes(read));
                        if (read != toReadFrameCount) {
                            //fill with zeroes, premature file end
                            System.arraycopy(emptyBuffer, 0, mixerBuffer, read, framesToBytes(toReadFrameCount - read));
                            track.pause();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    writeEmpty();
                }
            } else {
                writeEmpty(); //clean the buffer for addition
                for (MusicChannel track : tracks) {
                    if (!track.isPlaying()) continue;
                    long trackToRead = track.getLinePositionManager().framesUntilNext();
                    if (trackToRead < toReadFrameCount) {
                        toReadFrameCount = (int) trackToRead;
                        //System.out.println("- no wait its " + readFrameCount + " frames");
                    }
                }
                for (MusicChannel track : tracks) {
                    if (!track.isPlaying()) continue;
                    //System.out.println("running track: " + track);
                    int readFrames;
                    try {
                        readFrames = track.read(trackBuffer, toReadFrameCount);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (readFrames < toReadFrameCount) {
                        //premature file end
                        track.pause();
                    }
                    int bytesPerSample = track.getFormat().getSampleSizeInBits()/8;
                    boolean bigEndian = track.getFormat().isBigEndian();
                    int max = (int) ((long) Math.pow(2, bytesPerSample*8) - 1);
                    int min = -max - 1;
                    //TODO: move trackBuffer into MusicTrack
                    for (int i = 0; i < readFrames*track.getFormat().getChannels(); i++) {
                        short sample = (short) readSample(trackBuffer, i*bytesPerSample, bytesPerSample, bigEndian);
                        short original = (short) mixerIntBuffer[i];
                        int r = original + sample;
                        //clamp
                        mixerIntBuffer[i] = r > Short.MAX_VALUE ? Short.MAX_VALUE : r < Short.MIN_VALUE ? Short.MIN_VALUE : r;
                        /*
                        if (((original ^ r) & (sample ^ r)) < 0) {
                            mixerIntBuffer[i] = r < 0 ? max : min;
                        } else mixerIntBuffer[i] = r;
                        */
                        /*
                        if (max-sample > mixerIntBuffer[i]) mixerIntBuffer[i] = (int) max;
                        else if (min-sample > mixerIntBuffer[i]) mixerIntBuffer[i] = (int) min;
                        else mixerIntBuffer[i] += sample;
                         */
                    }
                }
                int bytesPerSample = target.getFormat().getSampleSizeInBits() / 8;
                boolean bigEndian = target.getFormat().isBigEndian();
                for (int i = 0; i < mixerIntBuffer.length; i++) {
                    int value = mixerIntBuffer[i];
                    writeSample(mixerBuffer, i*bytesPerSample, bytesPerSample, bigEndian, value);
                }
            }
        }
        private void doEvents() {
            for (MusicChannel track : tracks) {
                track.getLinePositionManager().completePendingEvents();
            }
        }
        private void tryPlay() {
            //System.out.println("writing " + readFrameCount + " frames (" + framesToBytes(readFrameCount) + " bytes)");
            int written = 0;
            do {
                written += target.write(mixerBuffer, written, framesToBytes(toReadFrameCount));
                if (status == Status.PAUSED) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException ignored) {}
                    }
                }
                //System.out.println("successfully written " + written);
            } while (written < framesToBytes(toReadFrameCount));
        }
    }
}
