package fun.kaituo.npcman.util;

import fun.kaituo.npcman.entity.NPCPlayer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import static fun.kaituo.npcman.NPCMan.*;

@SuppressWarnings("unused")
public interface PlayerUtil {
    static String formatUUID(String uuidString) {
        if (uuidString.length() == 32) {
            return uuidString.substring(0, 8) + "-" + uuidString.substring(8, 12) + "-" + uuidString.substring(12, 16) + "-" + uuidString.substring(16, 20) + "-" + uuidString.substring(20, 32);
        } else if (uuidString.length() != 36) {
            throw new IllegalArgumentException("UUID string is not valid");
        } else {
            return uuidString;
        }
    }
    
    static UUID getUUIDFromPlayerName(String skinPlayerName) {
        HttpGet uuidGet = new HttpGet("https://api.mojang.com/users/profiles/minecraft/" + skinPlayerName);
        HttpResponse uuidResponse;
        try {
            uuidResponse = HTTP_CLIENT.execute(uuidGet);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get UUID for player " + skinPlayerName, e);
            return null;
        }
        if (uuidResponse == null) {
            LOGGER.log(Level.WARNING, "Failed to get UUID for player " + skinPlayerName);
            return null;
        }
        if (uuidResponse.getStatusLine().getStatusCode() != 200) {
            LOGGER.log(Level.WARNING, "Failed to get UUID for player " + skinPlayerName + ": " + uuidResponse.getStatusLine().getReasonPhrase());
            return null;
        }
        if (uuidResponse.getEntity() == null) {
            LOGGER.log(Level.WARNING, "Failed to get UUID for player " + skinPlayerName);
            return null;
        }
        HttpEntity uuidEntity = uuidResponse.getEntity();
        String uuidString;
        try {
            InputStream uuidStream = uuidEntity.getContent();
            byte[] uuidBytes = new byte[uuidStream.available()];
            uuidStream.read(uuidBytes);
            String uuidRawJson = new String(uuidBytes);
            uuidString = GSON.fromJson(uuidRawJson, UUIDResponse.class).id;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get UUID for player " + skinPlayerName, e);
            return null;
        }
        if (uuidString == null) {
            LOGGER.log(Level.WARNING, "Failed to get UUID for player " + skinPlayerName);
            return null;
        }
        return UUID.fromString(formatUUID(uuidString));
    }
    
    static URI getSkinURLFromUUID(UUID uuid) {
        HttpGet skinGet = new HttpGet("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""));
        HttpResponse skinResponse;
        try {
            skinResponse = HTTP_CLIENT.execute(skinGet);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get skin URL for player " + uuid, e);
            return null;
        }
        if (skinResponse == null) {
            LOGGER.log(Level.WARNING, "Failed to get skin URL for player " + uuid);
            return null;
        }
        if (skinResponse.getStatusLine().getStatusCode() != 200) {
            LOGGER.log(Level.WARNING, "Failed to get skin URL for player " + uuid + ": " + skinResponse.getStatusLine().getReasonPhrase());
            return null;
        }
        if (skinResponse.getEntity() == null) {
            LOGGER.log(Level.WARNING, "Failed to get skin URL for player " + uuid);
            return null;
        }
        HttpEntity skinEntity = skinResponse.getEntity();
        String skinRawString;
        try {
            InputStream skinStream = skinEntity.getContent();
            byte[] skinBytes = new byte[skinStream.available()];
            skinStream.read(skinBytes);
            String skinRawJson = new String(skinBytes);
            skinRawString = GSON.fromJson(skinRawJson, SkinResponse.class).properties[0].value;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get skin URL for player " + uuid, e);
            return null;
        }
        if (skinRawString == null) {
            LOGGER.log(Level.WARNING, "Failed to get skin URL for player " + uuid);
            return null;
        }
        String skinString = new String(Base64.getDecoder().decode(skinRawString));
        return URI.create(GSON.fromJson(skinString, TextureObject.class).textures.get("SKIN").url);
    }
    
    class UUIDResponse {
        @SuppressWarnings("unused")
        public String name;
        public String id;
    }
    
    class SkinResponse {
        public String name;
        public String id;
        public Property[] properties;
        static class Property {
            public String name;
            public String value;
            public String signature;
        }
    }
    
    class TextureObject {
        public long timestamp;
        public String profileId;
        public String profileName;
        public boolean signatureRequired;
        public HashMap<String, Texture> textures;
        class Texture {
            public String url;
        }
    }
}
