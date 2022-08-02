package me.mrgazdag.hibiscus.library.song.sources;

import me.mrgazdag.hibiscus.library.playback.source.AudioSource;

import java.io.File;

public class SongSourceFile extends SongSource {
    private String filePath;
    private transient File file;
    public SongSourceFile(File file, String type) {
        super(type);
        this.filePath = file.getAbsolutePath();
    }
    public SongSourceFile(File file, String type, SourceFlag...flags) {
        super(type, flags);
        this.filePath = file.getAbsolutePath();
    }

    public void setFile(File file) {
        this.filePath = file.getAbsolutePath();
        this.file = file;
    }

    public File getFile() {
        if (file == null) file = new File(filePath);
        return file;
    }

    @Override
    public String getName() {
        return "File";
    }

    @Override
    public AudioSource createAudioSource() {
        return null;
    }
}
