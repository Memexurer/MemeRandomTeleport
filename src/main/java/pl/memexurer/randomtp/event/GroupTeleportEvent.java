package pl.memexurer.randomtp.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.memexurer.randomtp.data.RandomTeleportBlock;
import pl.memexurer.randomtp.rtp.RandomTeleportAction;

import java.util.List;

public class GroupTeleportEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private boolean isCancelled;
    private RandomTeleportAction teleportAction;
    private RandomTeleportBlock teleportBlock;
    private List<Player> teleportedPlayers;

    public GroupTeleportEvent(RandomTeleportAction teleportAction, RandomTeleportBlock teleportBlock, List<Player> teleportedPlayers) {
        this.teleportAction = teleportAction;
        this.teleportBlock = teleportBlock;
        this.teleportedPlayers = teleportedPlayers;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    public List<Player> getTeleportedPlayers() {
        return teleportedPlayers;
    }

    public RandomTeleportBlock getTeleportBlock() {
        return teleportBlock;
    }

    public RandomTeleportAction.ActionType getTeleportAction() {
        return this.teleportAction.getActionType();
    }

    public void setTeleportLocation(Location loc) {
        this.teleportAction.setLocation(loc);
    }
}
