package me.mrgazdag.hibiscus.library.song;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;
import me.mrgazdag.hibiscus.library.GlobalDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Entity("tracks")
public class Track {
    private final String trackId;
    private String name;
    private final List<ArtistData> artists;

    public Track(String trackId) {
        this.trackId = trackId;
        this.artists = new ArrayList<>();
    }

    public String getTrackID() {
        return trackId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addArtist(ArtistData data) {
        artists.add(data);
    }

    public CompletableFuture<Void> save() {
        return GlobalDatabase.save(this);
    }

    public static class ArtistData {
        private boolean main;
        @Reference
        private Artist artist;
        private ArtistType type;

        public ArtistData(boolean main, Artist artist, ArtistType type) {
            this.main = main;
            this.artist = artist;
            this.type = type;
        }

        public boolean isMain() {
            return main;
        }

        public Artist getArtist() {
            return artist;
        }

        public ArtistType getType() {
            return type;
        }

        public void setMain(boolean main) {
            this.main = main;
        }

        public void setArtist(Artist artist) {
            this.artist = artist;
        }

        public void setType(ArtistType type) {
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ArtistData that = (ArtistData) o;

            if (main != that.main) return false;
            if (!artist.equals(that.artist)) return false;
            return type == that.type;
        }

        @Override
        public int hashCode() {
            int result = (main ? 1 : 0);
            result = 31 * result + artist.hashCode();
            return result;
        }
    }
}
