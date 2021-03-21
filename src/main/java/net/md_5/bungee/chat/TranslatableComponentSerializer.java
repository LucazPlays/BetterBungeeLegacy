package net.md_5.bungee.chat;

import java.lang.reflect.*;
import net.md_5.bungee.api.chat.*;
import java.util.*;
import com.google.gson.*;

public class TranslatableComponentSerializer extends BaseComponentSerializer implements JsonSerializer<TranslatableComponent>, JsonDeserializer<TranslatableComponent>
{
    @Override
    public TranslatableComponent deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final TranslatableComponent component = new TranslatableComponent();
        final JsonObject object = json.getAsJsonObject();
        this.deserialize(object, component, context);
        component.setTranslate(object.get("translate").getAsString());
        if (object.has("with")) {
            component.setWith(Arrays.asList((BaseComponent[])context.deserialize(object.get("with"), BaseComponent[].class)));
        }
        return component;
    }
    
    @Override
    public JsonElement serialize(final TranslatableComponent src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject object = new JsonObject();
        this.serialize(object, src, context);
        object.addProperty("translate", src.getTranslate());
        if (src.getWith() != null) {
            object.add("with", context.serialize(src.getWith()));
        }
        return object;
    }
}
