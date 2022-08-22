package me.mrgazdag.hibiscus.library.users.permissions;

import java.util.Collection;

public interface PermissionHolder {
    void addGroup(PermissionGroup group);
    void removeGroup(PermissionGroup group);
    void addPermission(String permission);
    void addPermissions(String...permissions);
    void addPermissions(Collection<String> permissions);
    boolean hasPermission(String permission);
    void removePermission(String permission);
    void removePermissions(String...permissions);
    void removePermissions(Collection<String> permissions);
    void clearPermissions();
}
