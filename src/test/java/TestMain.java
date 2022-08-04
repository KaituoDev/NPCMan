import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import fun.kaituo.npcman.NPCMan;
import fun.kaituo.npcman.io.PagesParser;
import fun.kaituo.npcman.util.PlayerUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TestMain {
    static HttpClient HTTP_CLIENT = HttpClients.createDefault();
    static Gson GSON = new Gson();
    static Logger LOGGER = Logger.getLogger("TestMain");
    
    /*public static void main(String[] args) throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("https://api.mojang.com/user/profiles/kaituo/names");
        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[response.getEntity().getContent().available()];
        if (response != null) {
            response.getEntity().getContent().read(bytes);
        } else {
            System.out.println("response is null");
        }
        String bytesString = new String(bytes);
        System.out.println(bytesString);
        get.releaseConnection();
    }*/
    
    /*public static void main(String[] args) {
        UUID uuid = UUID.fromString("c9a8f8f8-f8f8-f8f8-f8f8-f8f8f8f8f8f8");
        System.out.println(getSkinURLFromUUID(uuid));
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
    }*/
    
    public static void main(String[] args) {
        File file = new File("/Users/gjt/Documents/idea/Minigame-server/NPCMan/src/main/resources/help.yml");
        PagesParser parser = new PagesParser(file);
        List<BaseComponent[]> list = parser.getPage("info");
        for (BaseComponent[] components : list) {
            for (BaseComponent component : components) {
                System.out.print(component.toLegacyText());
            }
            System.out.println();
        }
    }
    
    /*public static void main(String[] args) {
        System.out.println(null instanceof String);
    }*/
}
