package me.mrgazdag.hibiscus.library.users.networking.handler;

import me.mrgazdag.hibiscus.library.users.ConnectedDevice;
import me.mrgazdag.hibiscus.library.users.networking.ClientPacket;
import me.mrgazdag.hibiscus.library.users.networking.ServerPacket;
import me.mrgazdag.hibiscus.library.users.networking.protocol.DefaultProtocol;
import me.mrgazdag.hibiscus.library.users.networking.protocol.Protocol;

public class LoggingPacketHandler extends PacketHandler {
    public LoggingPacketHandler(ConnectedDevice device) {
        super(device);
    }

    @Override
    protected Protocol getProtocol() {
        return DefaultProtocol.INSTANCE;
    }

    @Override
    public void onJoin() {
        log("joined");
    }

    @Override
    public void onError(Throwable e) {
        log(e);
    }

    @Override
    public void onDisconnect(int code, String reason, boolean remote) {
        super.onDisconnect(code, reason, remote);
        if (remote) log("disconnected");
    }

    @Override
    public ServerPacket onPacketSend(ServerPacket packet) {
        log(packet);
        return packet;
    }

    @Override
    protected void unexpectedPacket(ClientPacket packet) {
        log(packet);
    }

    @Override
    protected void unknownPacket(int packetId) {
        System.err.println("WARNING! Unknown packet received from device " + device.getDeviceId() + ": " + packetId);
    }

    @Override
    protected void invalidPacket(String message) {
        System.err.println("WARNING! Packet error on device " + device.getDeviceId() + ": " + message);
    }
}
