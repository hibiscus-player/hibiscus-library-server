package me.mrgazdag.hibiscus.library;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.MorphiaCursor;
import dev.morphia.query.Query;
import me.mrgazdag.hibiscus.library.exceptions.DatabaseErrorException;
import me.mrgazdag.hibiscus.library.song.Artist;
import me.mrgazdag.hibiscus.library.song.Tag;
import me.mrgazdag.hibiscus.library.song.Track;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GlobalDatabase {
    private static Datastore datastore;
    private static ExecutorService databaseThreads = Executors.newFixedThreadPool(2);
    private static DatabaseErrorException datastoreFail = new DatabaseErrorException("Not yet connected to the database");

    public static void init() {
        ConnectionString mongoURI = new ConnectionString("mongodb://localhost:27017");
        String mongoDatabaseName = "musicplayer";
        MongoClient client = MongoClients.create(MongoClientSettings.builder()
                .applyToConnectionPoolSettings(builder -> {
                    builder.minSize(5);
                    builder.applyConnectionString(mongoURI);
                    builder.maxSize(20);
                    builder.build();
                })
                .applyConnectionString(mongoURI)
                .build());
        datastore = Morphia.createDatastore(client, mongoDatabaseName);
    }
    public static CompletableFuture<MorphiaCursor<Track>> querySongs(Collection<Consumer<Query<Track>>> queryModifiers) {
        return CompletableFuture.supplyAsync(() -> {
            Query<Track> query = datastore.find(Track.class);
            for (Consumer<Query<Track>> modifier : queryModifiers) {
                modifier.accept(query);
            }
            return query.iterator();
        }, databaseThreads);
    }
    public static CompletableFuture<MorphiaCursor<Tag>> queryTags(Collection<Consumer<Query<Tag>>> queryModifiers) {
        if (datastore == null) return CompletableFuture.failedFuture(datastoreFail);
        return CompletableFuture.supplyAsync(() -> {
            Query<Tag> query = datastore.find(Tag.class);
            for (Consumer<Query<Tag>> modifier : queryModifiers) {
                modifier.accept(query);
            }
            return query.iterator();
        }, databaseThreads);
    }
    public static CompletableFuture<Void> save(Track track) {
        if (datastore == null) return CompletableFuture.failedFuture(new IllegalStateException("Could not save song \"" + track.getTrackID() + "\": ", datastoreFail));
        return CompletableFuture.supplyAsync(()->{
            datastore.save(track);
            return null;
        }, databaseThreads);
    }
    public static CompletableFuture<Void> save(Artist artist) {
        if (datastore == null) return CompletableFuture.failedFuture(new IllegalStateException("Could not save artist \"" + artist.getArtistID() + "\": ", datastoreFail));
        return CompletableFuture.supplyAsync(()->{
            datastore.save(artist);
            return null;
        }, databaseThreads);
    }
    public static CompletableFuture<Void> save(Tag tag) {
        if (datastore == null) return CompletableFuture.failedFuture(new IllegalStateException("Could not save artist \"" + tag.getTagId() + "\": ", datastoreFail));
        return CompletableFuture.supplyAsync(()->{
            datastore.save(tag);
            return null;
        }, databaseThreads);
    }
}
