package me.mrgazdag.hibiscus.library.playback.target;

import javax.sound.sampled.AudioFormat;

public interface MixerTarget {
    public int write(byte[] buffer, int offset, int length);
    public void start();
    public void play();
    public void pause();
    public void stop();

    public AudioFormat getFormat();
    public default int getBufferSize() {
        return 4096;
    }
}