package net.md_5.bungee.api.chat;

import java.beans.*;

public final class ClickEvent
{
    private final Action action;
    private final String value;
    
    public Action getAction() {
        return this.action;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "ClickEvent(action=" + this.getAction() + ", value=" + this.getValue() + ")";
    }
    
    @ConstructorProperties({ "action", "value" })
    public ClickEvent(final Action action, final String value) {
        this.action = action;
        this.value = value;
    }
    
    public enum Action
    {
        OPEN_URL, 
        OPEN_FILE, 
        RUN_COMMAND, 
        SUGGEST_COMMAND, 
        CHANGE_PAGE;
    }
}
