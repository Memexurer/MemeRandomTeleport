package pl.memexurer.randomtp.data;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class BlockClickData {
    private final HashMap<UUID, BlockClickCallback> clickMap = new HashMap<>();

    public Optional<BlockClickCallback> call(UUID uniqueId) {
        return Optional.ofNullable(clickMap.get(uniqueId));
    }

    public void registerCallback(Player player, BlockClickCallback callback) {
        this.clickMap.put(player.getUniqueId(), callback);
    }

    public void unregister(Player player) {
        this.clickMap.remove(player.getUniqueId());
    }
}
