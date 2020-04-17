package pl.memexurer.randomtp;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.randomtp.commands.RandomTeleportCommand;
import pl.memexurer.randomtp.data.BlockClickData;
import pl.memexurer.randomtp.data.RandomTeleportBlock;
import pl.memexurer.randomtp.data.RandomTeleportBlockData;
import pl.memexurer.randomtp.rtp.RandomTeleporter;

import java.util.Optional;

public final class RandomTeleportPlugin extends JavaPlugin implements Listener {
    private final BlockClickData clickData = new BlockClickData();
    private RandomTeleportBlockData blockData;

    @Override
    public void onEnable() {
        RandomTeleporter randomTeleporter = new RandomTeleporter(getConfig().getConfigurationSection("randomtp"));
        this.blockData = new RandomTeleportBlockData(this.getDataFolder(), randomTeleporter);
        this.blockData.loadBlocks();

        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("randomtp").setExecutor(new RandomTeleportCommand(clickData, blockData));
    }

    @EventHandler(ignoreCancelled = true)
    public void rtpClickHandler(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;

        Optional<RandomTeleportBlock> block = blockData.getBlock(e.getClickedBlock().getLocation());
        if (!block.isPresent()) return;

        block.get().teleport(e.getPlayer());
    }

    @EventHandler
    public void rtpBlockAddHamdler(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null || !e.getPlayer().hasPermission("memertp.addteleport")) return;

        clickData.call(e.getPlayer().getUniqueId()).ifPresent(blockClickCallback -> {
            e.setCancelled(true);
            clickData.unregister(e.getPlayer());
            blockClickCallback.click(e.getClickedBlock());
        });
    }

}
