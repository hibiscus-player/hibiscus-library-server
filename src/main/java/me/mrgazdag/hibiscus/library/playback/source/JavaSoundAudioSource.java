package me.mrgazdag.hibiscus.library.playback.source;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

public class JavaSoundAudioSource {
    private AudioInputStream stream;

    public JavaSoundAudioSource(AudioInputStream stream) {
        this.stream = stream;
    }

    public int read(byte[] buffer, int frameCount) throws IOException {
        //linePositionManager.addPosition(frameCount);
        int bytesToRead = frameCount * stream.getFormat().getFrameSize();
        int read = this.stream.read(buffer, 0, bytesToRead);
        if (read == -1) return 0;
        else if (read == frameCount) return frameCount;
        while (read < frameCount) {
            int readByte = this.stream.read();
            if (readByte == -1) break; //end of file detected
            else {
                buffer[read] = (byte) readByte;
                read++;
                read += this.stream.read(buffer, 0, bytesToRead - read);
            }
        }
        return read;
    }
}
