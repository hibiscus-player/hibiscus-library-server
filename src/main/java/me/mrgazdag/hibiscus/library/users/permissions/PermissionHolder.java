package me.mrgazdag.hibiscus.library.users.permissions;

import java.util.Collection;
import java.util.Iterator;

public interface PermissionHolder {
    void addGroup(PermissionGroup group);
    void removeGroup(PermissionGroup group);
    Iterator<PermissionGroup> getGroups();
    Iterator<PermissionGroup> getAllGroups();
    void addPermission(String permission);
    void addPermissions(String...permissions);
    void addPermissions(Collection<String> permissions);
    boolean hasPermission(String permission);
    Iterator<String> getPermissions();
    Iterator<String> getAllPermissions();
    void removePermission(String permission);
    void removePermissions(String...permissions);
    void removePermissions(Collection<String> permissions);
    void clearPermissions();
}
