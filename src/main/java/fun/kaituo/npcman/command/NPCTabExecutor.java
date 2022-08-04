package fun.kaituo.npcman.command;

import static fun.kaituo.npcman.NPCMan.*;

import fun.kaituo.npcman.entity.NPCPlayer;
import net.md_5.bungee.api.chat.TextComponent;
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
                sender.sendMessage(ChatColor.RED + "Only players can use this command");
                return true;
            }
            if (!sender.hasPermission("npcman.npc")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                return true;
            }
            Player player = (Player)sender;
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /npc info|help|create|spawn|modify|list|remove|dispose");
                player.spigot().sendMessage(new TextComponent("Use /npc help for more info"));
                return true;
            }
            switch (args[0]) {
                case "info":
                    player.sendMessage();
                case "list":
                    CREATED_NPCS.forEach((uuid, npc) -> {
                        player.sendMessage();
                    });
                    break;
                case "create":
                    if (args.length < 2) {
                        player.sendMessage(ChatColor.RED + "Usage: /npc create <name>");
                        return true;
                    }
                    String name = args[1];
                    NPCPlayer npc = NPCPlayer.create(player.getWorld(), args[1], args[1]);
                    if (npc == null) {
                        player.sendMessage(ChatColor.RED + "Failed to create NPC " + name);
                        return true;
                    }
                    player.sendMessage(ChatColor.GREEN + "Created NPC " + name);
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
