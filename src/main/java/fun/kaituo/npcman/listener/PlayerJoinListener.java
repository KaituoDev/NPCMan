package fun.kaituo.npcman.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public interface PlayerJoinListener extends Listener {
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event);
}
