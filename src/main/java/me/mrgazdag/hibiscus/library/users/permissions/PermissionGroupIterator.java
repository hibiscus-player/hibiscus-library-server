package me.mrgazdag.hibiscus.library.users.permissions;

import java.util.*;

public class PermissionGroupIterator implements Iterator<PermissionGroup> {
    private final Deque<Iterator<PermissionGroup>> groups;
    private final Set<PermissionGroup> visitedGroups;
    private PermissionGroup nextGroup;

    public PermissionGroupIterator(Iterator<PermissionGroup> source) {
        this.groups = new ArrayDeque<>();
        this.groups.addLast(source);
        this.visitedGroups = new HashSet<>();
        stepNextGroup();
    }

    private void stepNextGroup() {
        while (!groups.isEmpty()) {
            Iterator<PermissionGroup> it = groups.peekLast();
            if (it.hasNext()) {
                PermissionGroup group = it.next();
                if (!visitedGroups.contains(group)) {
                    visitedGroups.add(group);
                    nextGroup = group;
                    groups.addLast(nextGroup.getGroups());
                    return;
                } else {
                    groups.removeLast();
                }
            } else {
                groups.removeLast();
            }
        }
        this.nextGroup = null;
    }

    @Override
    public PermissionGroup next() {
        PermissionGroup group = nextGroup;
        stepNextGroup();
        return group;
    }

    @Override
    public boolean hasNext() {
        return nextGroup != null;
    }
}
