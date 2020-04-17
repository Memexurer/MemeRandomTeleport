package pl.memexurer.randomtp.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import pl.memexurer.randomtp.rtp.RandomTeleportAction;
import pl.memexurer.randomtp.rtp.RandomTeleportType;
import pl.memexurer.randomtp.rtp.RandomTeleporter;

public class RandomTeleportBlock {
    private RandomTeleporter teleporter;
    private RandomTeleportType randomTeleportType;
    private Location location;

    RandomTeleportBlock(RandomTeleporter teleporter, ConfigurationSection section) {
        this.teleporter = teleporter;
        this.randomTeleportType = RandomTeleportType.valueOf(section.getString("type"));
        String[] splittedLocation = section.getName().split("&");
        this.location = new Location(Bukkit.getWorld(splittedLocation[0]), Integer.parseInt(splittedLocation[1]), Integer.parseInt(splittedLocation[2]), Integer.parseInt(splittedLocation[3]));
    }

    public RandomTeleportBlock(RandomTeleporter teleporter, Location location, RandomTeleportType type) {
        this.teleporter = teleporter;
        this.location = location;
        this.randomTeleportType = type;
    }

    public void teleport(Player player) {
        RandomTeleportAction action = teleporter.findCoords(location.getWorld());
        if (action.getLocation() == null) {
            if (action.getActionType() == RandomTeleportAction.ActionType.INVALID_BIOME)
                player.sendMessage(ChatColor.RED + "Trafiles na zly biom!");
            else if (action.getActionType() == RandomTeleportAction.ActionType.GUILD)
                player.sendMessage(ChatColor.RED + "Trafiles na teren gildyjny!");

            return;
        }

        if (randomTeleportType == RandomTeleportType.SINGLE)
            teleporter.teleport(player, action, this);
        else if(randomTeleportType == RandomTeleportType.GROUP)
            teleporter.groupTeleport(action, this);
    }

    public RandomTeleportType getRandomTeleportType() {
        return randomTeleportType;
    }

    public Location getLocation() {
        return location;
    }
}
