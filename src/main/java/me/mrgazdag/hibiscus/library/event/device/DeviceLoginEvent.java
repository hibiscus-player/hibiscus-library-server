package me.mrgazdag.hibiscus.library.event.device;

import me.mrgazdag.hibiscus.library.event.CallableEvent;
import me.mrgazdag.hibiscus.library.event.ResultingEvent;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

/**
 * This event is called after a device successfully logs in to the server.
 */
@CallableEvent
public class DeviceLoginEvent extends AbstractUserDeviceEvent implements ResultingEvent<DeviceLoginEvent.Result> {
    private Result result;
    private String kickMessage;

    public DeviceLoginEvent(ConnectedDevice device) {
        super(device);
        this.result = Result.ALLOWED;
    }

    @Override
    public Result getResult() {
        return result;
    }

    public String getKickMessage() {
        return kickMessage;
    }
    public void kick(String message) {
        if (result != Result.KICKED) {
            kickMessage = message;
            this.result = Result.KICKED;
        }
    }

    public void allow() {
        this.result = Result.ALLOWED;
    }

    @Override
    public String toString() {
        return "DeviceLoginEvent{" +
                "result=" + result +
                ", kickMessage='" + kickMessage + '\'' +
                ", device=" + device +
                '}';
    }

    public enum Result {
        ALLOWED,
        KICKED,
    }
}
