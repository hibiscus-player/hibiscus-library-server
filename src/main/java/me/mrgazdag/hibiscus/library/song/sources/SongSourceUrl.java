package me.mrgazdag.hibiscus.library.song.sources;

import me.mrgazdag.hibiscus.library.playback.source.AudioSource;

import java.net.MalformedURLException;
import java.net.URL;

public class SongSourceUrl extends SongSource {
    private static final String TYPE_STRING = "core:url";
    private String url;
    private transient URL cachedUrl;
    public SongSourceUrl(String url) throws MalformedURLException {
        super(TYPE_STRING);
        setUrl(url);
    }

    @Override
    public String getName() {
        return "URL";
    }

    public SongSourceUrl(String url, SourceFlag...flags) throws MalformedURLException {
        super(TYPE_STRING, flags);
        setUrl(url);
    }

    public void setUrl(String url) throws MalformedURLException {
        this.url = url;
        this.cachedUrl = new URL(url);
    }

    public URL getUrl() {
        return cachedUrl;
    }

    @Override
    public AudioSource createAudioSource() {
        return null;
    }
}
