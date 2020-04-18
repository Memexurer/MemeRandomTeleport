package pl.memexurer.randomtp.rtp;

import net.dzikoysk.funnyguilds.basic.guild.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.memexurer.randomtp.data.RandomTeleportBlock;
import pl.memexurer.randomtp.event.GroupTeleportEvent;
import pl.memexurer.randomtp.event.RandomTeleportEvent;
import pl.memexurer.randomtp.utils.ItemParsingUtils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomTeleporter {
    private int minSize;
    private int maxSize;
    private boolean clearEffects;
    private List<PotionEffect> effectList;
    private List<Biome> blockedBiomes;
    private boolean blockGuilds;
    private List<String> messages;
    private List<ItemStack> itemList;
    private int groupTeleportRadius;

    private Random random;

    public RandomTeleporter(ConfigurationSection section) {
        this.minSize = section.getInt("size.min");
        this.maxSize = section.getInt("size.max");
        this.clearEffects = section.getBoolean("remove_effects");
        this.effectList = section.getStringList("effects").stream().map(str -> new PotionEffect(
                PotionEffectType.getByName(str.split(" ")[0]),
                Integer.parseInt(str.split(" ")[1]),
                Integer.parseInt(str.split(" ")[3])
        )).collect(Collectors.toList());
        this.blockedBiomes = section.getStringList("randomtp.blocked_biomes").stream()
                .map(Biome::valueOf).collect(Collectors.toList());
        this.blockGuilds = section.getBoolean("guild_block") && areGuildsPresent();
        this.messages = section.getStringList("messages").stream().map(str -> ChatColor.translateAlternateColorCodes('&', str)).collect(Collectors.toList());
        this.itemList = section.getStringList("items").stream()
                .map(ItemParsingUtils::deserialize).collect(Collectors.toList());
        this.groupTeleportRadius = section.getInt("radius");

        this.random = new Random();
    }

    private boolean areGuildsPresent() {
        try {
            Class.forName("net.dzikoysk.funnyguilds.basic.guild.RegionUtils");
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public void teleport(Player player, RandomTeleportAction response, RandomTeleportBlock block) {
        RandomTeleportEvent event = new RandomTeleportEvent(player, response, block);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        if (clearEffects)
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        else player.addPotionEffects(effectList);
        for (String str : messages)
            player.sendMessage(str
                    .replace("{CoordX}", response.getLocation().getBlockX() + "")
                    .replace("{CoordZ}", response.getLocation().getBlockZ() + ""));
        for (ItemStack item : itemList) player.getInventory().addItem(item);

        player.teleport(response.getLocation());
    }

    public void groupTeleport(RandomTeleportAction action, RandomTeleportBlock teleportBlock) {
        List<Player> teleportedPlayers = teleportBlock.getLocation().getWorld().getNearbyEntities(teleportBlock.getLocation(), groupTeleportRadius, 1, groupTeleportRadius).stream()
                .filter(e -> e.getType() == EntityType.PLAYER)
                .map(e -> (Player) e)
                .collect(Collectors.toList());

        GroupTeleportEvent event = new GroupTeleportEvent(action, teleportBlock, teleportedPlayers);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        for (Player player : teleportedPlayers) {
            if (clearEffects)
                player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            else player.addPotionEffects(effectList);
            for (String str : messages)
                player.sendMessage(str
                        .replace("{CoordX}", action.getLocation().getBlockX() + "")
                        .replace("{CoordZ}", action.getLocation().getBlockZ() + ""));
            for (ItemStack item : itemList) player.getInventory().addItem(item);

            player.teleport(action.getLocation());
        }
    }

    public RandomTeleportAction findCoords(World world) {
        int x = getRandomMultiplier() * (random.nextInt(maxSize) + minSize);
        int z = getRandomMultiplier() * (random.nextInt(maxSize) + minSize);
        int y = world.getHighestBlockYAt(x, z);

        if (blockedBiomes.contains(world.getBiome(x, z)))
            return new RandomTeleportAction(RandomTeleportAction.ActionType.INVALID_BIOME);


        Location loc = new Location(world, x, y, z);

        if (blockGuilds)
            if (RegionUtils.getAt(loc) != null)
                return new RandomTeleportAction(RandomTeleportAction.ActionType.GUILD);


        return new RandomTeleportAction(loc);
    }

    private int getRandomMultiplier() {
        return (random.nextBoolean()) ? -1 : 1;
    }

    public int getGroupTeleportRadius() {
        return groupTeleportRadius;
    }
}
