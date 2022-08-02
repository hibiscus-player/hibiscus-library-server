package me.mrgazdag.hibiscus.library.song;

import dev.morphia.annotations.Entity;
import me.mrgazdag.hibiscus.library.GlobalDatabase;

import java.util.concurrent.CompletableFuture;

@Entity("tags")
public class Tag {
    private final String tagId;
    private String name;
    private String description;

    public Tag(String tagId, String name, String description) {
        this.tagId = tagId;
        this.name = name;
        this.description = description;
    }

    public String getTagId() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompletableFuture<Void> save() {
        return GlobalDatabase.save(this);
    }
}
