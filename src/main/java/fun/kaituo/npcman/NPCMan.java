package fun.kaituo.npcman;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import fun.kaituo.npcman.entity.NPCPlayer;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class NPCMan extends JavaPlugin {
    public static HttpClient HTTP_CLIENT;
    public static Logger LOGGER;
    public static HashMap<UUID, NPCPlayer> CREATED_NPCS;
    public static Gson GSON;
    public static ProtocolManager PROTOCOL_MANAGER;
    private File npcSave;
    
    @Override
    public void onEnable() {
        HTTP_CLIENT = HttpClients.createDefault();
        LOGGER = Bukkit.getLogger();
        CREATED_NPCS = new HashMap<>();
        GSON = new Gson();
        PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        try {
            npcSave = new File(getDataFolder(), "npc.json");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to create NPC save file", e);
        }
        if (npcSave == null) {
            LOGGER.log(Level.WARNING, "Failed to create NPC save file");
        }
        NPCSave save = null;
        try {
            save = GSON.fromJson(new FileReader(npcSave), NPCSave.class);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "Failed to load NPC save file", e);
        }
        if (save == null) {
            LOGGER.log(Level.WARNING, "Failed to load NPC save file");
        }
        for (Map.Entry<UUID, NPCSave.NPCPlayerSave> entry: save.npcs.entrySet()) {
            NPCPlayer npc = NPCPlayer.create(Bukkit.getWorld(entry.getValue().world), entry.getValue().skin, entry.getValue().displayName);
            if (npc == null) {
                LOGGER.log(Level.WARNING, "Failed to create NPC " + entry.getValue().displayName + ": " + entry.getKey().toString());
            } else {
                CREATED_NPCS.put(entry.getKey(), npc);
            }
        }
    }
    
    @Override
    public void onDisable() {
        if (npcSave.exists()) {
            npcSave.delete();
            try {
                npcSave.createNewFile();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to create NPC save file", e);
            }
        }
        NPCSave save = new NPCSave();
        for (Map.Entry<UUID, NPCPlayer> entry: CREATED_NPCS.entrySet()) {
            NPCSave.NPCPlayerSave npc = new NPCSave.NPCPlayerSave();
            npc.world = entry.getValue().level.getWorld().getName();
            npc.skin = entry.getValue().getNPCSkin();
            npc.displayName = entry.getValue().getNPCDisplayName();
            npc.x = entry.getValue().getX();
            npc.y = entry.getValue().getY();
            npc.z = entry.getValue().getZ();
            npc.yaw = entry.getValue().getBukkitEntity().getPlayer().getLocation().getYaw();
            npc.pitch = entry.getValue().getBukkitEntity().getPlayer().getLocation().getPitch();
            save.npcs.put(entry.getKey(), npc);
        }
    }
    
    public static NPCMan getInstance() {
        return (NPCMan) Bukkit.getPluginManager().getPlugin("NPCMan");
    }
    
    @SuppressWarnings("unused")
    private static class NPCSave {
        public HashMap<UUID, NPCPlayerSave> npcs;
        static class NPCPlayerSave {
            public String displayName;
            public String skin;
            public String world;
            public double x;
            public double y;
            public double z;
            public float yaw;
            public float pitch;
        }
    }
}
