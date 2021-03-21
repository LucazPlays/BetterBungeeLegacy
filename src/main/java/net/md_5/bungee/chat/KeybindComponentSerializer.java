package net.md_5.bungee.chat;

import java.lang.reflect.*;
import net.md_5.bungee.api.chat.*;
import com.google.gson.*;

public class KeybindComponentSerializer extends BaseComponentSerializer implements JsonSerializer<KeybindComponent>, JsonDeserializer<KeybindComponent>
{
    @Override
    public KeybindComponent deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final KeybindComponent component = new KeybindComponent();
        final JsonObject object = json.getAsJsonObject();
        this.deserialize(object, component, context);
        component.setKeybind(object.get("keybind").getAsString());
        return component;
    }
    
    @Override
    public JsonElement serialize(final KeybindComponent src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject object = new JsonObject();
        this.serialize(object, src, context);
        object.addProperty("keybind", src.getKeybind());
        return object;
    }
}
