package me.mrgazdag.hibiscus.library.users.permissions;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class PermissionIterator implements Iterator<String> {
    private Deque<Iterator<PermissionGroup>> groups;
    private Iterator<String> nextPerm;

    public PermissionIterator(PermissionNode source) {
        this.groups = new ArrayDeque<>();
        this.groups.addLast(source.getGroups());
        this.nextPerm = source.getPermissions();
        stepNextGroup();
        //TODO fix inheritance resulting in the same perms multiple times
        //     like: A -> B -> D       results in        A, B, D, C, D
        //             -> C -> D
    }

    private void stepNextGroup() {
        if (nextPerm.hasNext()) return;
        while (!groups.isEmpty()) {
            Iterator<PermissionGroup> it = groups.peekLast();
            if (it.hasNext()) {
                PermissionGroup node = it.next();
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
