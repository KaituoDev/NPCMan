package fun.kaituo.npcman.entity;

import com.comphenix.protocol.events.PacketContainer;
import com.mojang.authlib.GameProfile;
import fun.kaituo.npcman.NPCMan;
import fun.kaituo.npcman.listener.PlayerJoinListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.logging.Level;

import static fun.kaituo.npcman.NPCMan.*;

public class NPCPlayer extends ServerPlayer {
    private String npcDisplayName;
    private String npcSkin;
    private PlayerJoinListener playerJoinListener;
    
    private NPCPlayer(ServerLevel world, GameProfile profile, String displayName) {
        super(world.getServer(), world, profile);
        this.npcDisplayName = displayName;
        CREATED_NPCS.put(profile.getId(), this);
        playerJoinListener = (event) -> {
            try {
                sendPacket(event.getPlayer());
            } catch (InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "Failed to send packet to player", e);
            }
        };
    }
    
    public static NPCPlayer create(World world, String skin, String displayName) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), displayName);
        return new NPCPlayer(((CraftWorld)world).getHandle(), profile, displayName);
    }
    
    public void spawn(Location location) {
        spawn(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
    public void spawn(double x, double y, double z) {
        spawn(x, y, z, 0, 0);
    }
    
    public void spawn(double x, double y, double z, float yaw, float pitch) {
        moveTo(x, y, z);
        setRot(yaw, pitch);
        for (Player p: level.getWorld().getPlayers()) {
            try {
                sendPacket(p);
            } catch (InvocationTargetException e) {
                LOGGER.log(Level.WARNING, "Failed to send packet to player", e);
            }
        }
        Bukkit.getPluginManager().registerEvents(playerJoinListener, NPCMan.getInstance());
    }
    
    private void sendPacket(Player p) throws IllegalArgumentException, InvocationTargetException {
        if (!p.getWorld().getName().equals(level.getWorld().getName())) {
            throw new IllegalArgumentException("Player is in a different world");
        }
        ClientboundPlayerInfoPacket playerInfoPacket = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this);
        PacketContainer packet = PacketContainer.fromPacket(playerInfoPacket);
        PROTOCOL_MANAGER.sendServerPacket(p, packet);
    }
    
    public void remove() {
        die(DamageSource.GENERIC);
        HandlerList.unregisterAll(playerJoinListener);
    }
    
    public void dispose() {
        remove();
        CREATED_NPCS.remove(getGameProfile().getId());
    }
    
    public String getNPCDisplayName() {
        return npcDisplayName;
    }
    
    public void setNPCDisplayName(String displayName) {
        this.npcDisplayName = displayName;
    }
    
    public String getNPCSkin() {
        return npcSkin;
    }
    
    public void setNPCSkin(String skin) {
        this.npcSkin = npcSkin;
    }
}
