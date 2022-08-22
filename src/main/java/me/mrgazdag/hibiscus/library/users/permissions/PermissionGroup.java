package me.mrgazdag.hibiscus.library.users.permissions;

public class PermissionGroup extends PermissionNode {
    private String groupName;

    public PermissionGroup(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
