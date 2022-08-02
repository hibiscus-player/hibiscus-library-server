package me.mrgazdag.hibiscus.library.playback;

public class Marker {
    private long startFramePosition;
    private long endFramePosition;
    private Type type;
    private String name;
    public Marker(long startFramePosition, Type type, String name) {
        this(startFramePosition, startFramePosition, type, name);
    }

    public Marker(long startFramePosition, long endFramePosition, Type type, String name) {
        this.startFramePosition = startFramePosition;
        this.endFramePosition = endFramePosition;
        this.type = type;
        this.name = name;
    }

    public void setStartFramePosition(long startFramePosition) {
        this.startFramePosition = startFramePosition;
    }

    public void setEndFramePosition(long endFramePosition) {
        this.endFramePosition = endFramePosition;
    }

    public void setDuration(long duration) {
        this.endFramePosition = startFramePosition + duration;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartFramePosition() {
        return startFramePosition;
    }

    public long getEndFramePosition() {
        return endFramePosition;
    }

    public long getDuration() {
        return this.endFramePosition - this.startFramePosition;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public enum Type {
        STANDARD
    }
}
