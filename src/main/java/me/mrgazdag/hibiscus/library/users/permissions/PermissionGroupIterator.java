package me.mrgazdag.hibiscus.library.users.permissions;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class PermissionGroupIterator implements Iterator<PermissionGroup> {
    private Deque<Iterator<PermissionGroup>> groups;
    private PermissionGroup nextGroup;

    public PermissionGroupIterator(Iterator<PermissionGroup> source) {
        this.groups = new ArrayDeque<>();
        this.groups.addLast(source);
        stepNextGroup();
        //TODO fix inheritance resulting in the same groups multiple times
        //     like: A -> B -> D       results in        A, B, D, C, D
        //             -> C -> D
    }

    private void stepNextGroup() {
        while (!groups.isEmpty()) {
            Iterator<PermissionGroup> it = groups.peekLast();
            if (it.hasNext()) {
                nextGroup = it.next();
                groups.addLast(nextGroup.getGroups());
                return;
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
