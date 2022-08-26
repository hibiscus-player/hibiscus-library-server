package me.mrgazdag.hibiscus.library.users.permissions;

import java.util.*;

public class PermissionIterator implements Iterator<String> {
    private final Deque<Iterator<PermissionGroup>> groups;
    private final Set<PermissionGroup> visitedGroups;
    private Iterator<String> nextPerm;

    public PermissionIterator(PermissionNode source) {
        this.groups = new ArrayDeque<>();
        this.groups.addLast(source.getGroups());
        this.visitedGroups = new HashSet<>();
        this.nextPerm = source.getPermissions();
        stepNextGroup();
    }

    private void stepNextGroup() {
        if (nextPerm.hasNext()) return;
        while (!groups.isEmpty()) {
            Iterator<PermissionGroup> it = groups.peekLast();
            if (it.hasNext()) {
                PermissionGroup node = it.next();
                if (visitedGroups.contains(node)) continue;
                visitedGroups.add(node);
                nextPerm = node.getPermissions();
                if (nextPerm.hasNext()) {
                    groups.addLast(node.getGroups());
                    return;
                }
            } else {
                groups.removeLast();
            }
        }
        this.nextPerm = null;
    }

    @Override
    public String next() {
        String perm = nextPerm.next();
        stepNextGroup();
        return perm;
    }

    @Override
    public boolean hasNext() {
        return nextPerm != null;
    }
}
