package fun.kaituo.npcman.command;

import static fun.kaituo.npcman.NPCMan.*;
import fun.kaituo.npcman.entity.NPCPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NPCTabExecutor implements TabExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("npc")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            if (!sender.hasPermission("npcman.npc")) {
                sender.sendMessage("You don't have permission to use this command.");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Usage: /npc <create|remove|list>");
                return true;
            }
            switch (args[0]) {
                case "list":
                    CREATED_NPCS.forEach((uuid, npc) -> {
                        sender.sendMessage(npc.getDisplayName() + " (" + uuid.toString() + ")");
                    });
                    break;
                case "create":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /npc create <name>");
                        return true;
                    }
                    String name = args[1];
                    NPCPlayer npc = NPCPlayer.create(((Player)sender).getWorld(), args[1], args[1]);
                    if (npc == null) {
                        sender.sendMessage(ChatColor.RED + "Failed to create NPC " + name);
                        return true;
                    }
                    sender.sendMessage(ChatColor.GREEN + "Created NPC " + name);
                    break;
            }
        }
        return false;
    }
    
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
