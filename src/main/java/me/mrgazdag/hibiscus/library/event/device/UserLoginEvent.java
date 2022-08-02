package me.mrgazdag.hibiscus.library.event.device;

import me.mrgazdag.hibiscus.library.event.CallableEvent;
import me.mrgazdag.hibiscus.library.event.ResultingEvent;
import me.mrgazdag.hibiscus.library.users.ConnectedDevice;

/**
 * This event is called after a device successfully logs in with
 * a user that is not currently connected. Note that this only occurs
 * when the user's first device connects. If the user disconnects
 * with every device from the server, and then a device belonging
 * to this user connects, then this event will be called again.
 */
@CallableEvent
public class UserLoginEvent extends AbstractUserDeviceEvent implements ResultingEvent<UserLoginEvent.Result> {
    private Result result;
    private String kickMessage;

    public UserLoginEvent(ConnectedDevice device) {
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
        return "UserLoginEvent{" +
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
