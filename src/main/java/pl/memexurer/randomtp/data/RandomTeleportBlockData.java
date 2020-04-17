package pl.memexurer.randomtp.data;

import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.memexurer.randomtp.rtp.RandomTeleportType;
import pl.memexurer.randomtp.rtp.RandomTeleporter;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RandomTeleportBlockData {
    private final RandomTeleporter teleporter;
    private final ConcurrentHashMap<Location, RandomTeleportBlock> blockMap;
    private final File configurationFile;
    private final YamlConfiguration configuration;

    public RandomTeleportBlockData(File dataFolder, RandomTeleporter teleporter) {
        this.teleporter = teleporter;
        this.blockMap = new ConcurrentHashMap<>();
        this.configurationFile = new File(dataFolder, "data.yml");
        if (!configurationFile.exists()) {
            if (!configurationFile.getParentFile().exists()) configurationFile.getParentFile().mkdirs();
            try {
                configurationFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.configuration = YamlConfiguration.loadConfiguration(configurationFile);
    }

    public void loadBlocks() {
        ConfigurationSection teleportSection = configuration.getConfigurationSection("teleports");
        if (teleportSection == null) return;

        for (String key : teleportSection.getKeys(false)) {
            RandomTeleportBlock block = new RandomTeleportBlock(teleporter, teleportSection.getConfigurationSection(key));

            blockMap.put(block.getLocation(), block);
        }
    }

    public void createBlock(Location location, RandomTeleportType type) {
        RandomTeleportBlock block = new RandomTeleportBlock(teleporter, location, type);
        configuration.createSection(getConfigPath(location)).set("type", type.name());
        blockMap.put(location, block);
        save();
    }
    
    public boolean deleteBlock(Location location) {
        if(!blockMap.containsKey(location)) return false;
        configuration.set(getConfigPath(location), null);
        blockMap.remove(location);
        save();

        return true;
    }

    private void save() {
        try {
            configuration.save(configurationFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getConfigPath(Location location) {
        return "teleports." + location.getWorld().getName() + "&" + location.getBlockX() + "&" + location.getBlockY() + "&" + location.getBlockZ();
    }

    public Optional<RandomTeleportBlock> getBlock(Location location) {
        return Optional.ofNullable(blockMap.get(location));
    }
}
