package me.mrgazdag.hibiscus.library.song.sources;

import dev.morphia.annotations.Entity;
import me.mrgazdag.hibiscus.library.playback.source.AudioSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public abstract class SongSource {
    private final String type;
    private final List<SourceFlag> flags;

    public SongSource(String type, SourceFlag...flags) {
        this.type = type;
        this.flags = new ArrayList<>();
        this.flags.addAll(Arrays.asList(flags));
    }

    public SongSource(String type) {
        this.type = type;
        this.flags = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public List<SourceFlag> getFlags() {
        return flags;
    }

    public boolean hasFlag(SourceFlag flag) {
        return flags.contains(flag);
    }

    public void addFlag(SourceFlag flag) {
        if (!hasFlag(flag)) {
            flags.add(flag);
        }
    }
    public void removeFlag(SourceFlag flag) {
        if (hasFlag(flag)) {
            flags.add(flag);
        }
    }
    public abstract String getName();

    public abstract AudioSource createAudioSource();
}
