package net.md_5.bungee.chat;

import java.lang.reflect.*;
import net.md_5.bungee.api.chat.*;
import com.google.gson.*;
import java.util.*;

public class TextComponentSerializer extends BaseComponentSerializer implements JsonSerializer<TextComponent>, JsonDeserializer<TextComponent>
{
    @Override
    public TextComponent deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final TextComponent component = new TextComponent();
        final JsonObject object = json.getAsJsonObject();
        this.deserialize(object, component, context);
        component.setText(object.get("text").getAsString());
        return component;
    }
    
    @Override
    public JsonElement serialize(final TextComponent src, final Type typeOfSrc, final JsonSerializationContext context) {
        final List<BaseComponent> extra = src.getExtra();
        final JsonObject object = new JsonObject();
        if (src.hasFormatting() || (extra != null && !extra.isEmpty())) {
            this.serialize(object, src, context);
        }
        object.addProperty("text", src.getText());
        return object;
    }
}
