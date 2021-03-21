package net.md_5.bungee.chat;

import java.lang.reflect.*;
import net.md_5.bungee.api.chat.*;
import com.google.gson.*;

public class ScoreComponentSerializer extends BaseComponentSerializer implements JsonSerializer<ScoreComponent>, JsonDeserializer<ScoreComponent>
{
    @Override
    public ScoreComponent deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject json = element.getAsJsonObject();
        if (!json.has("name") || !json.has("objective")) {
            throw new JsonParseException("A score component needs at least a name and an objective");
        }
        final String name = json.get("name").getAsString();
        final String objective = json.get("objective").getAsString();
        final ScoreComponent component = new ScoreComponent(name, objective);
        if (json.has("value") && !json.get("value").getAsString().isEmpty()) {
            component.setValue(json.get("value").getAsString());
        }
        this.deserialize(json, component, context);
        return component;
    }
    
    @Override
    public JsonElement serialize(final ScoreComponent component, final Type type, final JsonSerializationContext context) {
        final JsonObject root = new JsonObject();
        this.serialize(root, component, context);
        final JsonObject json = new JsonObject();
        json.addProperty("name", component.getName());
        json.addProperty("objective", component.getObjective());
        json.addProperty("value", component.getValue());
        root.add("score", json);
        return root;
    }
}
