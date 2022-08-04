package fun.kaituo.npcman.io;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "unchecked" })
public class PagesParser {
    private HashMap<String, ArrayList<ArrayList<HashMap<String, Object>>>> pages;
    
    public PagesParser(File pagesFile) {
        if (!pagesFile.isFile()) {
            throw new IllegalArgumentException("Pages file is not a file");
        }
        if (!(pagesFile.getName().endsWith(".yml") || pagesFile.getName().endsWith(".yaml"))) {
            throw new IllegalArgumentException("Pages file must be YAML");
        }
        if (!pagesFile.exists()) {
            throw new IllegalArgumentException("Pages file does not exist");
        }
        Yaml yaml = new Yaml();
        pages = null;
        try {
            pages = yaml.loadAs(new FileReader(pagesFile), HashMap.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load pages file", e);
        }
        if (pages == null) {
            throw new IllegalArgumentException("Failed to load pages file");
        }
    }
    
    public List<BaseComponent[]> getPage(String pageName) {
        if (pages == null) {
            throw new IllegalStateException("Pages file is not loaded");
        }
        ArrayList<ArrayList<HashMap<String, Object>>> page = pages.get(pageName);
        if (page == null) {
            throw new IllegalArgumentException("Page " + pageName + " does not exist");
        }
        return page.stream().map(this::parseRawComponents).collect(Collectors.toList());
    }
    
    private BaseComponent[] parseRawComponents(ArrayList<HashMap<String, Object>> components) {
        List<BaseComponent> outputComponents = new ArrayList<>();
        ArrayList<RawComponent> lineOfComponents = components.stream().map(RawComponent::of).collect(Collectors.toCollection(ArrayList::new));
        for (RawComponent component : lineOfComponents) {
            BaseComponent baseComponent;
            if (component.text != null) {
                baseComponent = new TextComponent(component.text);
            } else if (component.keybind != null) {
                baseComponent = new KeybindComponent(component.keybind);
            } else if (component.translate != null) {
                baseComponent = new TranslatableComponent(component.translate);
            } else {
                throw new IllegalArgumentException("Component is not a text, keybind, or translate");
            }
            if (component.color != null) {
                baseComponent.setColor(ChatColor.of(component.color));
            } else if (outputComponents.size() > 0) {
                baseComponent.setColor(outputComponents.get(outputComponents.size() - 1).getColor());
            } else {
                baseComponent.setColor(ChatColor.WHITE);
            }
            baseComponent.setBold(component.bold);
            baseComponent.setItalic(component.italic);
            baseComponent.setUnderlined(component.underlined);
            baseComponent.setStrikethrough(component.strikethrough);
            baseComponent.setObfuscated(component.obfuscated);
            if (component.clickEvent != null) {
                baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(component.clickEvent.action.toUpperCase()), component.clickEvent.value));
            }
            if (component.hoverEvent != null) {
                HoverEvent.Action hoverAction = HoverEvent.Action.valueOf(component.hoverEvent.action.toUpperCase());
                Content hoverContent;
                if (component.hoverEvent.value instanceof RawHoverString) {
                    hoverContent = new Text(((RawHoverString)component.hoverEvent.value).text);
                } else if (component.hoverEvent.value instanceof RawItem) {
                    hoverContent = new Item(((RawItem)component.hoverEvent.value).id, ((RawItem)component.hoverEvent.value).count, ItemTag.ofNbt(((RawItem)component.hoverEvent.value).nbt));
                } else if (component.hoverEvent.value instanceof RawEntity) {
                    hoverContent = new Entity(((RawEntity)component.hoverEvent.value).type, ((RawEntity)component.hoverEvent.value).id, new TextComponent(((RawEntity)component.hoverEvent.value).name));
                } else {
                    throw new IllegalArgumentException("Hover event value is not a string, item or entity");
                }
                baseComponent.setHoverEvent(new HoverEvent(hoverAction, hoverContent));
            }
            outputComponents.add(baseComponent);
        }
        return outputComponents.toArray(new BaseComponent[0]);
    }
    
    static class RawComponent {
        String text;
        String keybind;
        String translate;
        String color;
        boolean bold;
        boolean italic;
        boolean underlined;
        boolean strikethrough;
        boolean obfuscated;
        RawClickEvent clickEvent;
        RawHoverEvent hoverEvent;
        
        static RawComponent of(HashMap<String, Object> map) {
            RawComponent component = new RawComponent();
            if (!(map.get("text") instanceof String || map.get("keybind") instanceof String || map.get("translate") instanceof String)) {
                throw new IllegalArgumentException("Component is not a text, keybind, or translate");
            }
            if (!(checkNullValid(map, String.class, "color") && checkNullValid(map, Boolean.class, "bold", "italic", "underlined", "strikethrough", "obfuscated") && checkNullValid(map, HashMap.class, "clickEvent", "hoverEvent"))) {
                throw new IllegalArgumentException("Component has invalid fields");
            }
            component.text = (String)map.get("text");
            component.keybind = (String)map.get("keybind");
            component.translate = (String)map.get("translate");
            component.color = (String)map.get("color");
            component.bold = Boolean.parseBoolean((String)map.get("bold"));
            component.italic = Boolean.parseBoolean((String)map.get("italic"));
            component.underlined = Boolean.parseBoolean((String)map.get("underlined"));
            component.strikethrough = Boolean.parseBoolean((String)map.get("strikethrough"));
            component.obfuscated = Boolean.parseBoolean((String)map.get("obfuscated"));
            if (map.containsKey("clickEvent")) {
                component.clickEvent = RawClickEvent.of((HashMap<String, String>)map.get("clickEvent"));
            }
            if (map.containsKey("hoverEvent")) {
                component.hoverEvent = RawHoverEvent.of((HashMap<String, Object>)map.get("hoverEvent"));
            }
            return component;
        }
    }
    
    static class RawClickEvent {
        String action;
        String value;
        
        static RawClickEvent of(HashMap<String, String> map) {
            RawClickEvent event = new RawClickEvent();
            if (!(map.get("action") != null && map.get("value") != null)) {
                throw new IllegalArgumentException();
            }
            event.action = map.get("action");
            event.value = map.get("value");
            return event;
        }
    }
    
    static class RawHoverEvent {
        String action;
        RawHoverValue value;
        
        static RawHoverEvent of(HashMap<String, Object> map) {
            RawHoverEvent event = new RawHoverEvent();
            if (!(map.get("action") instanceof String && map.get("value") instanceof HashMap)) {
                throw new IllegalArgumentException();
            }
            event.action = (String)map.get("action");
            event.value = RawHoverValue.of((HashMap<String, Object>)map.get("value"));
            return event;
        }
    }
    
    static class RawHoverValue {
        static RawHoverValue of(HashMap<String, Object> map) {
            if (!(checkNullValid(map, String.class, "text") || checkNullValid(map, HashMap.class, "item") || checkNullValid(map, HashMap.class, "entity"))) {
                throw new IllegalArgumentException("Hover value is not a string, item, or entity");
            }
            if (map.containsKey("text")) {
                return RawHoverString.ofRaw((String)map.get("text"));
            } else if (map.containsKey("item")) {
                return RawItem.ofRaw((HashMap<String, String>)map.get("item"));
            } else if (map.containsKey("entity")) {
                return RawEntity.ofRaw((HashMap<String, String>)map.get("entity"));
            } else {
                throw new IllegalArgumentException("Hover value is not a string, item, or entity");
            }
        }
    }
    
    static class RawHoverString extends RawHoverValue {
        String text;
        
        static RawHoverString ofRaw(String text) {
            RawHoverString string = new RawHoverString();
            string.text = text;
            return string;
        }
    }
    
    static class RawItem extends RawHoverValue {
        String id;
        int count;
        String nbt;
        
        static RawItem ofRaw(HashMap<String, String> map) {
            RawItem item = new RawItem();
            if (map.get("id") == null) {
                throw new IllegalArgumentException("Item has invalid fields");
            }
            item.id = map.get("id");
            item.count = Integer.parseInt(map.get("count"));
            item.nbt = map.get("nbt");
            return item;
        }
    }
    
    static class RawEntity extends RawHoverValue {
        String id;
        String type;
        String name;
        
        static RawEntity ofRaw(HashMap<String, String> map) {
            RawEntity entity = new RawEntity();
            if (map.get("id") == null && map.get("type") == null) {
                throw new IllegalArgumentException("Entity has invalid fields");
            }
            entity.id = map.get("id");
            entity.type = map.get("type");
            entity.name = map.get("name");
            return entity;
        }
    }
    
    static <K, V> boolean checkNullValid(HashMap<K, V> map, Class<?> type, K... keys) {
        boolean result = true;
        for (K key : keys) {
            result = result && (map.get(key) == null || type.isAssignableFrom(map.get(key).getClass()));
        }
        return result;
    }
}
