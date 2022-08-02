package me.mrgazdag.hibiscus.library.song;

import dev.morphia.annotations.Entity;
import me.mrgazdag.hibiscus.library.GlobalDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Entity("artists")
public class Artist {
    private final String artistId;
    private String name;
    private String imageURL;
    private final List<String> connections;

    public Artist(String artistId) {
        this.artistId = artistId;
        this.connections = new ArrayList<>();
    }

    public String getArtistID() {
        return artistId;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void addConnection(String url) {
        connections.add(url);
    }

    public void removeConnection(String url) {
        connections.remove(url);
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public CompletableFuture<Void> save() {
        return GlobalDatabase.save(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        return artistId.equals(artist.artistId);
    }

    @Override
    public int hashCode() {
        return artistId.hashCode();
    }
}
