package net.md_5.bungee.chat;

import java.lang.reflect.*;
import net.md_5.bungee.api.chat.*;
import com.google.gson.*;

public class SelectorComponentSerializer extends BaseComponentSerializer implements JsonSerializer<SelectorComponent>, JsonDeserializer<SelectorComponent>
{
    @Override
    public SelectorComponent deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject object = element.getAsJsonObject();
        final SelectorComponent component = new SelectorComponent(object.get("selector").getAsString());
        this.deserialize(object, component, context);
        return component;
    }
    
    @Override
    public JsonElement serialize(final SelectorComponent component, final Type type, final JsonSerializationContext context) {
        final JsonObject object = new JsonObject();
        this.serialize(object, component, context);
        object.addProperty("selector", component.getSelector());
        return object;
    }
}
