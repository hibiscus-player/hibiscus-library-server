package me.mrgazdag.hibiscus.library.users.networking.server;

import me.mrgazdag.hibiscus.library.ui.page.Page;
import me.mrgazdag.hibiscus.library.ui.page.PageGroup;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerPageListChangePacket extends ServerPacket {
    private static final byte PAGE_GROUPS_ADDED_BIT = 0x01;
    private static final byte PAGE_GROUPS_REMOVED_BIT = 0x02;
    private static final byte PAGE_GROUPS_UPDATED_BIT = 0x04;
    private static final byte PAGES_ADDED_BIT = 0x08;
    private static final byte PAGES_REMOVED_BIT = 0x10;
    private static final byte PAGES_UPDATED_BIT = 0x20;
    private final ByteBuffer buffer;
    @SuppressWarnings("StatementWithEmptyBody")
    public ServerPageListChangePacket(ConnectedDevice device, PageGroup groupAdded, PageGroup groupRemoved, PageGroup groupUpdated, Page pageAdded, Page pageRemoved, Page pageUpdated) {
        byte mask = 0;
        int length = 1 + 1; // Packet ID + Mask

        ByteBuffer groupAddedBuffer;
        if (groupAdded != null) {
            groupAddedBuffer = groupAdded.serialize(device);
            if (groupAddedBuffer == null) {
                // Group is not visible to device
            } else {
                mask |= PAGE_GROUPS_ADDED_BIT;
                length += 4; // List size (will be 1)
                length += groupAddedBuffer.position();
                groupAddedBuffer.position(0);
            }
        } else groupAddedBuffer = null;

        String groupRemovedId;
        if (groupRemoved != null) {
            mask |= PAGE_GROUPS_REMOVED_BIT;
            length += 4; // List size (will be 1)
            groupRemovedId = groupRemoved.getId();
            length += 4 + groupRemovedId.length() * 2; // Group ID length + Group ID
        } else groupRemovedId = null;

        ByteBuffer groupUpdatedBuffer;
        if (groupUpdated != null) {
            groupUpdatedBuffer = groupUpdated.serialize(device);
            if (groupUpdatedBuffer == null) {
                // Group is not visible to device
            } else {
                mask |= PAGE_GROUPS_UPDATED_BIT;
                length += 4; // List size (will be 1)
                length += groupUpdatedBuffer.position();
                groupUpdatedBuffer.position(0);
            }
        } else groupUpdatedBuffer = null;

        ByteBuffer pageAddedBuffer;
        if (pageAdded != null) {
            pageAddedBuffer = pageAdded.serialize(device);
            if (pageAddedBuffer == null) {
                // Page is not visible to device
            } else {
                mask |= PAGES_ADDED_BIT;
                length += 4; // List size (will be 1)
                length += pageAddedBuffer.position();
                pageAddedBuffer.position(0);
            }
        } else pageAddedBuffer = null;

        String pageRemovedId;
        if (pageRemoved != null) {
            mask |= PAGES_REMOVED_BIT;
            length += 4; // List size (will be 1)
            pageRemovedId = pageRemoved.getPageId();
            length += 4 + pageRemovedId.length()*2; // Page ID length + Page ID
        } else pageRemovedId = null;

        ByteBuffer pageUpdatedBuffer;
        if (pageUpdated != null) {
            pageUpdatedBuffer = pageUpdated.serialize(device);
            if (pageUpdatedBuffer == null) {
                // Page is not visible to device
            } else {
                mask |= PAGES_UPDATED_BIT;
                length += 4; // List size (will be 1)
                length += pageUpdatedBuffer.position();
                pageUpdatedBuffer.position(0);
            }
        } else pageUpdatedBuffer = null;

        buffer = ByteBuffer.allocateDirect(length);
        buffer.put(getID());
        buffer.put(mask);
        if (groupAdded != null) {
            buffer.putInt(1); // List size
            buffer.put(groupAddedBuffer);
        }
        if (groupRemoved != null) {
            buffer.putInt(1); // List size
            buffer.putInt(groupRemovedId.length());
            buffer.put(groupRemovedId.getBytes(StandardCharsets.UTF_16BE));
        }
        if (groupUpdated != null) {
            buffer.putInt(1); // List size
            buffer.put(groupUpdatedBuffer);
        }
        if (pageAdded != null) {
            buffer.putInt(1); // List size
            buffer.put(pageAddedBuffer);
        }
        if (pageRemoved != null) {
            buffer.putInt(1); // List size
            buffer.putInt(pageRemovedId.length());
            buffer.put(pageRemovedId.getBytes(StandardCharsets.UTF_16BE));
        }
        if (pageUpdated != null) {
            buffer.putInt(1); // List size
            buffer.put(pageUpdatedBuffer);
        }

        buffer.position(0);
    }

    public ServerPageListChangePacket(ConnectedDevice device, Collection<PageGroup> groupsAdded, Collection<PageGroup> groupsRemoved, Collection<PageGroup> groupsUpdated, Collection<Page> pagesAdded, Collection<Page> pagesRemoved, Collection<Page> pagesUpdated) {
        byte mask = 0;
        int length = 1 + 1; // Packet ID + Mask

        List<ByteBuffer> groupAddedBuffers = null;
        if (groupsAdded != null && groupsAdded.size() > 0) {
            boolean found = false;
            for (PageGroup groupAdded : groupsAdded) {
                ByteBuffer buffer = groupAdded.serialize(device);
                if (buffer == null) {
                    // Group is not visible to device
                } else {
                    if (!found) {
                        found = true;
                        groupAddedBuffers = new ArrayList<>();
                        mask |= PAGE_GROUPS_ADDED_BIT;
                        length += 4; // List size
                    }
                    length += buffer.position();
                    buffer.position(0);
                    groupAddedBuffers.add(buffer);
                }
            }
        }

        List<String> groupRemovedIds;
        if (groupsRemoved != null && groupsRemoved.size() > 0) {
            mask |= PAGE_GROUPS_REMOVED_BIT;
            groupRemovedIds = new ArrayList<>();
            length += 4; // List size (will be 1)
            for (PageGroup pageGroup : groupsRemoved) {
                String id = pageGroup.getId();
                groupRemovedIds.add(id);
                length += 4 + id.length() * 2; // Group ID length + Group ID
            }
        } else groupRemovedIds = null;

        List<ByteBuffer> groupUpdatedBuffers = null;
        if (groupsUpdated != null && groupsUpdated.size() > 0) {
            boolean found = false;
            for (PageGroup groupUpdated : groupsUpdated) {
                ByteBuffer buffer = groupUpdated.serialize(device);
                if (buffer == null) {
                    // Group is not visible to device
                } else {
                    if (!found) {
                        found = true;
                        groupUpdatedBuffers = new ArrayList<>();
                        mask |= PAGE_GROUPS_ADDED_BIT;
                        length += 4; // List size
                    }
                    length += buffer.position();
                    buffer.position(0);
                    groupUpdatedBuffers.add(buffer);
                }
            }
        }

        List<ByteBuffer> pageAddedBuffers = null;
        if (pagesAdded != null && pagesAdded.size() > 0) {
            boolean found = false;
            for (Page pageAdded : pagesAdded) {
                ByteBuffer buffer = pageAdded.serialize(device);
                if (buffer == null) {
                    // Page is not visible to device
                } else {
                    if (!found) {
                        found = true;
                        pageAddedBuffers = new ArrayList<>();
                        mask |= PAGES_ADDED_BIT;
                        length += 4; // List size
                    }
                    length += buffer.position();
                    buffer.position(0);
                    pageAddedBuffers.add(buffer);
                }
            }
        }

        List<String> pageRemovedIds;
        if (pagesRemoved != null && pagesRemoved.size() > 0) {
            mask |= PAGES_REMOVED_BIT;
            pageRemovedIds = new ArrayList<>();
            length += 4; // List size (will be 1)
            for (Page page : pagesRemoved) {
                String id = page.getPageId();
                pageRemovedIds.add(id);
                length += 4 + id.length()*2; // Page ID length + Page ID
            }
        } else pageRemovedIds = null;

        List<ByteBuffer> pageUpdatedBuffers = null;
        if (pagesUpdated != null && pagesUpdated.size() > 0) {
            boolean found = false;
            for (Page pageUpdated : pagesUpdated) {
                ByteBuffer buffer = pageUpdated.serialize(device);
                if (buffer == null) {
                    // Page is not visible to device
                } else {
                    if (!found) {
                        found = true;
                        pageUpdatedBuffers = new ArrayList<>();
                        mask |= PAGES_UPDATED_BIT;
                        length += 4; // List size
                    }
                    length += buffer.position();
                    buffer.position(0);
                    pageUpdatedBuffers.add(buffer);
                }
            }
        }

        buffer = ByteBuffer.allocateDirect(length);
        buffer.put(getID());
        buffer.put(mask);
        if (groupAddedBuffers != null) {
            buffer.putInt(groupAddedBuffers.size()); // List size
            for (ByteBuffer groupAddedBuffer : groupAddedBuffers) {
                buffer.put(groupAddedBuffer);
            }
        }
        if (groupRemovedIds != null) {
            buffer.putInt(groupRemovedIds.size()); // List size
            for (String groupRemovedId : groupRemovedIds) {
                buffer.putInt(groupRemovedId.length());
                buffer.put(groupRemovedId.getBytes(StandardCharsets.UTF_16BE));
            }
        }
        if (groupUpdatedBuffers != null) {
            buffer.putInt(groupUpdatedBuffers.size()); // List size
            for (ByteBuffer groupUpdatedBuffer : groupUpdatedBuffers) {
                buffer.put(groupUpdatedBuffer);
            }
        }
        if (pageAddedBuffers != null) {
            buffer.putInt(pageAddedBuffers.size()); // List size
            for (ByteBuffer pageAddedBuffer : pageAddedBuffers) {
                buffer.put(pageAddedBuffer);
            }
        }
        if (pageRemovedIds != null) {
            buffer.putInt(pageRemovedIds.size()); // List size
            for (String pageRemovedId : pageRemovedIds) {
                buffer.putInt(pageRemovedId.length());
                buffer.put(pageRemovedId.getBytes(StandardCharsets.UTF_16BE));
            }
        }
        if (pageUpdatedBuffers != null) {
            buffer.putInt(pageUpdatedBuffers.size()); // List size
            for (ByteBuffer pageUpdatedBuffer : pageUpdatedBuffers) {
                buffer.put(pageUpdatedBuffer);
            }
        }

        buffer.position(0);
    }

    @Override
    public ByteBuffer compress() {
        return buffer;
    }

    @Override
    protected void compress(ByteBuffer buffer) {}

    @Override
    protected int calculateLength() {
        return -1;
    }

    @Override
    public int getLength() {
        return buffer.capacity();
    }

    @Override
    public String toString() {
        return "ServerUpdatePageContentsPacket{" +
                "buffer=" + buffer +
                '}';
    }
}
