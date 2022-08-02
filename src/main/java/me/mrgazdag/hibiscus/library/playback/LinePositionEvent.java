package me.mrgazdag.hibiscus.library.playback;

public class LinePositionEvent {
    private long framePosition;
    private Runnable callback;

    public LinePositionEvent(long framePosition, Runnable callback) {
        this.framePosition = framePosition;
        this.callback = callback;
    }

    public void setFramePosition(long framePosition) {
        this.framePosition = framePosition;
    }

    public long getFramePosition() {
        return framePosition;
    }

    public Runnable getCallback() {
        return callback;
    }

    @Override
    public String toString() {
        return "LinePositionEvent{" +
                "framePosition=" + framePosition +
                ", callback=" + callback +
                '}';
    }
}
