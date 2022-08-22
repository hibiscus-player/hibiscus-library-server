package me.mrgazdag.hibiscus.library.users.permissions;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionNode implements PermissionHolder {
    private final Set<PermissionGroup> inherit;
    private final Set<String> permissions;

    public PermissionNode() {
        this.inherit = new HashSet<>();
        this.permissions = new HashSet<>();
    }

    public PermissionNode(PermissionNode other) {
        this.inherit = new HashSet<>(other.inherit);
        this.permissions = new HashSet<>(other.permissions);
    }
    public static PermissionNode orNew(PermissionNode other) {
        return other == null ? new PermissionNode() : other;
    }

    @Override
    public void addGroup(PermissionGroup group) {
        this.inherit.add(group);
    }
    @Override
    public void removeGroup(PermissionGroup group) {
        this.inherit.remove(group);
    }

    @Override
    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    @Override
    public void addPermissions(String... permissions) {
        this.permissions.addAll(List.of(permissions));
    }

    @Override
    public void addPermissions(Collection<String> permissions) {
        this.permissions.addAll(permissions);
    }

    @Override
    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    @Override
    public void removePermissions(String... permissions) {
        List.of(permissions).forEach(this.permissions::remove);
    }

    @Override
    public void removePermissions(Collection<String> permissions) {
        this.permissions.removeAll(permissions);
    }

    @Override
    public void clearPermissions() {
        this.permissions.clear();
        this.inherit.clear();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (PermissionNode.checkWildcards(permissions, permission)) return true;
        for (PermissionGroup permissionGroup : inherit) {
            if (permissionGroup.hasPermission(permission)) return true;
        }
        return false;
    }

    public static boolean checkWildcards(Set<String> permissions, String permission) {
        if (permissions.contains(permission)) return true;

        int index = permission.length();
        while ((index = permission.lastIndexOf(".", index-1)) > 0) {
            if (permissions.contains(permission.substring(0, index) + ".*")) return true;
        }
        return permissions.contains("*");
    }
}
