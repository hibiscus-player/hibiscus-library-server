package me.mrgazdag.hibiscus.library.event.device;

import me.mrgazdag.hibiscus.library.event.CallableEvent;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

@CallableEvent
public class DeviceConnectEvent extends AbstractDeviceEvent {
    private String serverName;
    private String serverMotd;

    public DeviceConnectEvent(ConnectedDevice device, String serverName, String serverMotd) {
        super(device);
        this.serverName = serverName;
        this.serverMotd = serverMotd;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public void setServerMotd(String serverMotd) {
        this.serverMotd = serverMotd;
    }
    public String getServerName() {
        return serverName;
    }
    public String getServerMotd() {
        return serverMotd;
    }
    public boolean isAttemptingToLogin() {
        return device.isAttemptingToLogin();
    }

    @Override
    public String toString() {
        return "DeviceConnectEvent{" +
                "serverName='" + serverName + '\'' +
                ", serverMotd='" + serverMotd + '\'' +
                ", device=" + device +
                '}';
    }
}
