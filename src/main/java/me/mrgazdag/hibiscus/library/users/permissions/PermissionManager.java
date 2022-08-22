package me.mrgazdag.hibiscus.library.users.permissions;

import me.mrgazdag.hibiscus.library.LibraryServer;
import me.mrgazdag.hibiscus.library.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PermissionManager {
    private final LibraryServer libraryServer;
    private final Map<String, PermissionGroup> groups;
    private final Map<String, PermissionNode> users;
    private final Path filePath;
    private final PermissionGroup guestGroup;
    private final PermissionGroup userGroup;
    private final PermissionGroup defaultGroup;

    public PermissionManager(LibraryServer libraryServer, Path filePath) {
        this.libraryServer = libraryServer;
        this.groups = new HashMap<>();
        this.users = new HashMap<>();

        this.filePath = filePath;

        this.guestGroup = new PermissionGroup("Guest");
        this.userGroup = new PermissionGroup("User");
        this.defaultGroup = new PermissionGroup("Default");

        reload();
    }

    public void reload() {
        if (!Files.exists(filePath)) {
            System.out.println("Permissions file \"" + filePath + "\" does not exist.");
            return;
        } else {
            System.out.println("Loading permission file \"" + filePath + "\"...");
        }
        String contents;
        try {
            contents = Util.readPath(filePath);
        } catch (IOException e) {
            System.err.println("Failed to reload permission data:");
            e.printStackTrace();
            return;
        }
        for (PermissionNode node : users.values()) {
            node.clearPermissions();
        }
        for (PermissionGroup group : groups.values()) {
            group.clearPermissions();
        }

        int groupCount = 0;
        int userCount = 0;
        JSONObject data = new JSONObject(contents);
        if (data.has("groups")) {
            JSONObject groups = data.getJSONObject("groups");
            for (String key : groups.keySet()) {
                PermissionGroup group = this.groups.computeIfAbsent(key, PermissionGroup::new);
                loadPermissionGroup(groups.getJSONObject(key), group);
                this.groups.put(key, group);
                groupCount++;
            }
        }
        if (data.has("profiles")) {
            JSONObject profiles = data.getJSONObject("profiles");
            for (String key : profiles.keySet()) {
                PermissionNode node = this.users.computeIfAbsent(key, id->new PermissionNode());
                loadPermissionNode(profiles.getJSONObject(key), node);
                this.users.put(key, node);
                userCount++;
            }
        }
        if (data.has("default")) {
            JSONObject obj = data.getJSONObject("default");
            loadPermissionGroup(obj, defaultGroup);
            groupCount++;
        }
        if (data.has("guest")) {
            JSONObject obj = data.getJSONObject("guest");
            loadPermissionGroup(obj, guestGroup);
            groupCount++;
        }
        if (data.has("user")) {
            JSONObject obj = data.getJSONObject("user");
            loadPermissionGroup(obj, userGroup);
            groupCount++;
        }
        System.out.println("Loaded " + groupCount + " group and " + userCount + " user permissions.");
    }

    public void registerGroup(String groupId, PermissionGroup group) {
        this.groups.put(groupId, group);
    }

    public void unregisterGroup(String groupId) {
        this.groups.remove(groupId);
    }

    public PermissionGroup getGroup(String groupId) {
        return this.groups.get(groupId);
    }

    public PermissionNode getUser(String profileId) {
        PermissionNode node = this.users.get(profileId);
        if (node == null) {
            node = new PermissionNode();
        }
        node.addGroup(defaultGroup);
        node.addGroup(profileId == null ? guestGroup : userGroup);
        return node;
    }

    public PermissionGroup getGuestGroup() {
        return guestGroup;
    }

    public PermissionGroup getUserGroup() {
        return userGroup;
    }

    public PermissionGroup getDefaultGroup() {
        return defaultGroup;
    }

    private void loadPermissionGroup(JSONObject data, PermissionGroup group) {
        loadPermissionNode(data, group);
        if (data.has("name")) group.setGroupName(data.getString("name"));
    }
    private void loadPermissionNode(JSONObject data, PermissionNode node) {
        if (data.has("groups")) {
            JSONArray groups = data.getJSONArray("groups");
            for (Object o : groups) {
                if (o instanceof String str) {
                    PermissionGroup group = getGroup(str);
                    if (group == null) {
                        throw new IllegalStateException("Parent group not found: " + str);
                    }
                    node.addGroup(group);
                }
            }
        }
        if (data.has("perms")) {
            JSONArray perms = data.getJSONArray("perms");
            for (Object o : perms) {
                if (o instanceof String str) {
                    node.addPermission(str);
                }
            }
        }
    }
}
