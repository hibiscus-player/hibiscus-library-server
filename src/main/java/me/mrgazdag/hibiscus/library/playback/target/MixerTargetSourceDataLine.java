package me.mrgazdag.hibiscus.library.playback.target;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class MixerTargetSourceDataLine implements MixerTarget {

    private final SourceDataLine line;

    public MixerTargetSourceDataLine(SourceDataLine line) {
        this.line = line;
    }

    @Override
    public int write(byte[] buffer, int offset, int length) {
        return line.write(buffer, offset, length);
    }

    @Override
    public void start() {
        try {
            line.open();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void play() {
        line.start();
    }

    @Override
    public void pause() {
        line.stop();
    }

    @Override
    public void stop() {
        line.drain();
        line.flush();
        line.close();
    }

    @Override
    public AudioFormat getFormat() {
        return line.getFormat();
    }
}