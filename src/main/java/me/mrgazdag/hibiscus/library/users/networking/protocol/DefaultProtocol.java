package me.mrgazdag.hibiscus.library.users.networking.protocol;

import me.mrgazdag.hibiscus.library.users.networking.client.*;
import me.mrgazdag.hibiscus.library.users.networking.server.*;

public class DefaultProtocol extends Protocol {
    public static DefaultProtocol INSTANCE = new DefaultProtocol();
    public DefaultProtocol() {
        // Client Packets
        register(ClientHelloPacket::new);
        register(ClientIdentityCompletePacket::new);
        register(ClientPingPacket::new);
        register(ClientChangePagePacket::new);
        register(ClientPageActionPacket::new);

        // Server Packets
        register(ServerHelloPacket.class);
        register(ServerIdentityRequestPacket.class);
        register(ServerKickPacket.class);
        register(ServerPongPacket.class);
        register(ServerWelcomePacket.class);
        register(ServerPageListChangePacket.class);
        register(ServerChangePagePacket.class);
        register(ServerUpdatePagePacket.class);
        register(ServerPageActionPacket.class);
    }
}
