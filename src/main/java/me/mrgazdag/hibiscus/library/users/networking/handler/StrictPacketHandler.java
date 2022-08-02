package me.mrgazdag.hibiscus.library.users.networking.handler;

import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.protocol.DefaultProtocol;
import me.mrgazdag.hibiscus.library.users.networking.protocol.Protocol;

public class StrictPacketHandler extends PacketHandler {
    public StrictPacketHandler(ConnectedDevice device) {
        super(device);
    }

    @Override
    protected Protocol getProtocol() {
        return DefaultProtocol.INSTANCE;
    }

    @Override
    public void onJoin() {}

    @Override
    public void onError(Throwable e) {}

    @Override
    protected void unexpectedPacket(ClientPacket packet) {
        device.kick("Unexpected packet");
    }
}
