package me.mrgazdag.hibiscus.library.users.networking.protocol;

import me.mrgazdag.hibiscus.library.users.networking.client.ClientChangePagePacket;
import me.mrgazdag.hibiscus.library.users.networking.client.ClientHelloPacket;
import me.mrgazdag.hibiscus.library.users.networking.client.ClientIdentityCompletePacket;
import me.mrgazdag.hibiscus.library.users.networking.client.ClientPingPacket;
import me.mrgazdag.hibiscus.library.users.networking.server.*;

public class DefaultProtocol extends Protocol {
    public static DefaultProtocol INSTANCE = new DefaultProtocol();
    public DefaultProtocol() {
        // Client Packets
        register(ClientHelloPacket::new);
        register(ClientIdentityCompletePacket::new);
        register(ClientPingPacket::new);
        register(ClientChangePagePacket::new);

        // Server Packets
        register(ServerHelloPacket.class);
        register(ServerIdentityRequestPacket.class);
        register(ServerKickPacket.class);
        register(ServerPongPacket.class);
        register(ServerWelcomePacket.class);
        register(ServerPageListChangePacket.class);
        register(ServerChangePagePacket.class);
        register(ServerUpdatePagePacket.class);
    }
}
