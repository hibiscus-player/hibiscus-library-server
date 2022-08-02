package me.mrgazdag.hibiscus.library.playback.source;

import java.io.IOException;

public interface AudioSource {
    int read(byte[] buffer, int frameCount) throws IOException;
}
