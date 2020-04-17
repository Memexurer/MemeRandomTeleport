package pl.memexurer.randomtp.rtp;

import org.bukkit.Location;

public class RandomTeleportAction {
    private Location location;
    private ActionType actionType;

    RandomTeleportAction(Location location) {
        this.location = location;
        this.actionType = ActionType.SUCCESS;
    }

    RandomTeleportAction(ActionType type) {
        this.actionType = type;
    }

    public Location getLocation() {
        return location;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setLocation(Location loc) {
        this.location = loc;
    }

    public enum ActionType {
        SUCCESS,
        GUILD,
        INVALID_BIOME
    }
}
