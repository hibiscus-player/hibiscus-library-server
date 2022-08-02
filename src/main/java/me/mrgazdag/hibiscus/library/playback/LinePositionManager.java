package me.mrgazdag.hibiscus.library.playback;

import java.util.*;

public class LinePositionManager {
    private final Set<LinePositionEvent> positionEvents;
    private final Queue<LinePositionEvent> pendingEvents;
    private final Deque<LinePositionEvent> toCompleteEvents;
    private final Deque<LinePositionEvent> completedEvents;

    private long currentPosition;

    public LinePositionManager() {
        positionEvents = new HashSet<>();
        pendingEvents = new PriorityQueue<>(Comparator.comparingLong(LinePositionEvent::getFramePosition));
        toCompleteEvents = new ArrayDeque<>();
        completedEvents = new ArrayDeque<>();

        currentPosition = 0;
    }

    public void addEvent(long framePosition, Runnable callback) {
        addEvent(new LinePositionEvent(framePosition, callback));
    }
    public void addEvent(LinePositionEvent event) {
        positionEvents.add(event);
        if (event.getFramePosition() <= currentPosition) {
            toCompleteEvents.add(event);
        } else {
            pendingEvents.add(event);
        }
    }
    public void removeEvent(LinePositionEvent event) {
        positionEvents.remove(event);
    }
    public void completePendingEvents() {
        while (!toCompleteEvents.isEmpty()) {
            LinePositionEvent event = toCompleteEvents.pop();
            System.out.println("Running event " + event);
            event.getCallback().run();
            completedEvents.add(event);
        }
    }
    public void addPosition(long position) {
        setPosition(this.currentPosition + position);
    }
    public void setPosition(long position) {
        if (position > currentPosition) {
            while (pendingEvents.size() > 0 && pendingEvents.peek().getFramePosition() <= position) {
                LinePositionEvent event = pendingEvents.poll();
                if (event == null) break;
                toCompleteEvents.add(event);
            }
        } else if (position < currentPosition) {
            while (completedEvents.size() > 0 && completedEvents.peekLast().getFramePosition() <= position) {
                LinePositionEvent event = completedEvents.pollLast();
                if (event == null) break;
                else if (event.getFramePosition() == position) toCompleteEvents.add(event);
                else pendingEvents.add(event);
            }
        } else return;
        this.currentPosition = position;
    }

    public long framesUntilNext() {
        if (pendingEvents.size() == 0) return Long.MAX_VALUE;
        return pendingEvents.peek().getFramePosition() - currentPosition;
    }

    public long position() {
        return currentPosition;
    }
}
