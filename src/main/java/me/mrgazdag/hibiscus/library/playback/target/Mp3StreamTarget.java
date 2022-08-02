package me.mrgazdag.hibiscus.library.playback.target;

import me.mrgazdag.hibiscus.library.playback.ByteQueue;
import net.sourceforge.lame.lowlevel.LameEncoder;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//TODO move into a module, right now its here for testing purposes
public class Mp3StreamTarget implements MixerTarget {

    private final LameEncoder encoder;
    private final AudioFormat sourceFormat;
    private final byte[] mp3SourceBuffer;
    private final byte[] mp3DestBuffer;
    private final ByteQueue queue;

    private final Thread readThread;
    private final List<OutputStream> targets;
    private final Object waitObject;
    private volatile boolean running;

    public Mp3StreamTarget(LameEncoder encoder, AudioFormat sourceFormat) {
        this.encoder = encoder;
        this.sourceFormat = sourceFormat;
        this.mp3SourceBuffer = new byte[(int) (sourceFormat.getFrameSize() * sourceFormat.getFrameRate())];
        this.mp3DestBuffer = new byte[(int) (sourceFormat.getFrameSize() * sourceFormat.getFrameRate())];
        this.targets = new ArrayList<>();

        //create a buffer, that can hold at most 5 seconds of data
        this.queue = new ByteQueue((int) (sourceFormat.getFrameSize() * sourceFormat.getFrameRate() * 5));

        this.waitObject = new Object();

        this.readThread = new Thread(() ->{
            long lastCheck = System.currentTimeMillis();
            long currentCheck;
            while (running) {
                queue.read(mp3SourceBuffer, 0, mp3SourceBuffer.length);
                int encodedLength = encoder.encodeBuffer(mp3SourceBuffer, 0, mp3SourceBuffer.length, this.mp3DestBuffer);
                synchronized (targets) {
                    Iterator<OutputStream> it = targets.iterator();
                    while (it.hasNext()) {
                        OutputStream out = it.next();
                        try {
                            out.write(mp3DestBuffer, 0, encodedLength);
                        } catch (IOException e) {
                            it.remove();
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException ignored) {}
                        }
                    }
                }
                currentCheck = System.currentTimeMillis();
                if (currentCheck - lastCheck < 1000) {
                    //wait one second
                    synchronized (waitObject) {
                        try {
                            waitObject.wait(1000-(currentCheck - lastCheck));
                        } catch (InterruptedException ignored) {}
                    }
                }
                lastCheck = lastCheck + 1000;
            }
        });
    }

    public void addOutputStream(OutputStream target) {
        synchronized (targets) {
            targets.add(target);
        }
    }

    public void onWrite(byte[] mp3buffer, int mp3Length, int sourceFrameCount) {}

    @Override
    public int write(byte[] buffer, int offset, int length) {
        queue.write(buffer, offset, length);
        return length;
    }

    @Override
    public int getBufferSize() {
        return encoder.getPCMBufferSize();
    }

    @Override
    public void start() {
        this.running = true;
        this.readThread.start();
    }

    @Override
    public void play() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public AudioFormat getFormat() {
        return sourceFormat;
    }

    public LameEncoder getEncoder() {
        return encoder;
    }
}