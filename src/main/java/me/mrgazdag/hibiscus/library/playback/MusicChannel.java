package me.mrgazdag.hibiscus.library.playback;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

public class MusicChannel {
    private AudioInputStream stream;

    private final LinePositionManager linePositionManager;

    private volatile boolean playing;
    public MusicChannel(AudioInputStream stream) {
        this.stream = stream;
        this.linePositionManager = new LinePositionManager();
        this.playing = false;
    }
    private int getFrameSizeBytes() {
        return stream.getFormat().getFrameSize();
    }

    public void play() {
        playing = true;
    }
    public void pause() {
        playing = false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void skip(long frameCount) {
        try {
            stream.skipNBytes(frameCount * getFrameSizeBytes());
            linePositionManager.addPosition(frameCount);
        } catch (IOException ignored) {}
    }

    public int read(byte[] buffer, int frameCount) throws IOException {
        linePositionManager.addPosition(frameCount);
        int bytesToRead = frameCount*getFrameSizeBytes();
        int readBytesSoFar = this.stream.read(buffer, 0, bytesToRead);
        if (readBytesSoFar == -1) return 0;
        else if (readBytesSoFar == frameCount) return frameCount;
        int currentlyRead;
        while (readBytesSoFar < bytesToRead) {
            //check end of file possibilities
            currentlyRead = this.stream.read(buffer, 0, bytesToRead - readBytesSoFar);
            if (currentlyRead == -1)  break; //end of file
            readBytesSoFar += currentlyRead;
        }
        return readBytesSoFar / getFrameSizeBytes();
    }

    public AudioFormat getFormat() {
        return stream.getFormat();
    }

    public LinePositionManager getLinePositionManager() {
        return linePositionManager;
    }
}
