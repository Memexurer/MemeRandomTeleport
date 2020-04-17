package pl.memexurer.randomtp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.memexurer.randomtp.data.BlockClickData;
import pl.memexurer.randomtp.data.RandomTeleportBlockData;
import pl.memexurer.randomtp.rtp.RandomTeleportType;

import java.util.Optional;

public class RandomTeleportCommand implements CommandExecutor {
    private RandomTeleportBlockData blockData;
    private BlockClickData clickData;

    public RandomTeleportCommand(BlockClickData clickData, RandomTeleportBlockData blockData) {
        this.blockData = blockData;
        this.clickData = clickData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda jest dostepna tylko dla graczy.");
            return true;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("memertp.addteleport")) {
            sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczajacych permisji do uzycia tej komendy.");
            return true;
        }

        if (args.length == 0) return printUsage(sender, label);

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " add (single/group)");
                return true;
            }

            Optional<RandomTeleportType> type = findTeleportType(args[1]);
            if (!type.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Nie znaleziono takiego typu teleportu! Dozwolone typy: single, group");
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Kliknij na jakis blok, aby zarejestrowac go do listy blokow losowej teleportacji.");
            clickData.registerCallback(player, block -> {
                blockData.createBlock(block.getLocation(), type.get());
                sender.sendMessage(ChatColor.GREEN + "Pomyślnie zarejestrowano blok losowej teleportacji!");
            });
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " remove");
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Kliknij na jakis blok, aby wyrejestrowac go z listy blokow losowej teleportacji.");
            clickData.registerCallback(player, block -> {
                if (blockData.deleteBlock(block.getLocation()))
                    sender.sendMessage(ChatColor.GREEN + "Pomyślnie usunieto blok losowej teleportacji!");
                else
                    sender.sendMessage(ChatColor.RED + "Ten blok nie byl wczesniej zarejestrowany!");
            });
        } else printUsage(sender, label);
        return true;
    }

    private boolean printUsage(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " add (single/group)");
        sender.sendMessage(ChatColor.RED + "/" + label + " remove");
        return true;
    }

    private Optional<RandomTeleportType> findTeleportType(String name) {
        for (RandomTeleportType type : RandomTeleportType.values()) {
            if (type.name().equalsIgnoreCase(name)) return Optional.of(type);
        }
        return Optional.empty();
    }
}
