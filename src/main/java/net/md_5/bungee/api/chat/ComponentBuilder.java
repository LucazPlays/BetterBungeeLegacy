package net.md_5.bungee.api.chat;

import java.util.*;
import com.google.common.base.*;
import net.md_5.bungee.api.*;

public final class ComponentBuilder
{
    private BaseComponent current;
    private final List<BaseComponent> parts;
    
    public ComponentBuilder(final ComponentBuilder original) {
        this.parts = new ArrayList<BaseComponent>();
        this.current = original.current.duplicate();
        for (final BaseComponent baseComponent : original.parts) {
            this.parts.add(baseComponent.duplicate());
        }
    }
    
    public ComponentBuilder(final String text) {
        this.parts = new ArrayList<BaseComponent>();
        this.current = new TextComponent(text);
    }
    
    public ComponentBuilder(final BaseComponent component) {
        this.parts = new ArrayList<BaseComponent>();
        this.current = component.duplicate();
    }
    
    public ComponentBuilder append(final BaseComponent component) {
        return this.append(component, FormatRetention.ALL);
    }
    
    public ComponentBuilder append(final BaseComponent component, final FormatRetention retention) {
        this.parts.add(this.current);
        final BaseComponent previous = this.current;
        (this.current = component.duplicate()).copyFormatting(previous, retention, false);
        return this;
    }
    
    public ComponentBuilder append(final BaseComponent[] components) {
        return this.append(components, FormatRetention.ALL);
    }
    
    public ComponentBuilder append(final BaseComponent[] components, final FormatRetention retention) {
        Preconditions.checkArgument(components.length != 0, (Object)"No components to append");
        final BaseComponent previous = this.current;
        for (final BaseComponent component : components) {
            this.parts.add(this.current);
            (this.current = component.duplicate()).copyFormatting(previous, retention, false);
        }
        return this;
    }
    
    public ComponentBuilder append(final String text) {
        return this.append(text, FormatRetention.ALL);
    }
    
    public ComponentBuilder append(final String text, final FormatRetention retention) {
        this.parts.add(this.current);
        final BaseComponent old = this.current;
        (this.current = new TextComponent(text)).copyFormatting(old, retention, false);
        return this;
    }
    
    public ComponentBuilder append(final Joiner joiner) {
        return joiner.join(this, FormatRetention.ALL);
    }
    
    public ComponentBuilder append(final Joiner joiner, final FormatRetention retention) {
        return joiner.join(this, retention);
    }
    
    public ComponentBuilder color(final ChatColor color) {
        this.current.setColor(color);
        return this;
    }
    
    public ComponentBuilder bold(final boolean bold) {
        this.current.setBold(bold);
        return this;
    }
    
    public ComponentBuilder italic(final boolean italic) {
        this.current.setItalic(italic);
        return this;
    }
    
    public ComponentBuilder underlined(final boolean underlined) {
        this.current.setUnderlined(underlined);
        return this;
    }
    
    public ComponentBuilder strikethrough(final boolean strikethrough) {
        this.current.setStrikethrough(strikethrough);
        return this;
    }
    
    public ComponentBuilder obfuscated(final boolean obfuscated) {
        this.current.setObfuscated(obfuscated);
        return this;
    }
    
    public ComponentBuilder insertion(final String insertion) {
        this.current.setInsertion(insertion);
        return this;
    }
    
    public ComponentBuilder event(final ClickEvent clickEvent) {
        this.current.setClickEvent(clickEvent);
        return this;
    }
    
    public ComponentBuilder event(final HoverEvent hoverEvent) {
        this.current.setHoverEvent(hoverEvent);
        return this;
    }
    
    public ComponentBuilder reset() {
        return this.retain(FormatRetention.NONE);
    }
    
    public ComponentBuilder retain(final FormatRetention retention) {
        this.current.retain(retention);
        return this;
    }
    
    public BaseComponent[] create() {
        final BaseComponent[] result = this.parts.toArray(new BaseComponent[this.parts.size() + 1]);
        result[this.parts.size()] = this.current;
        return result;
    }
    
    public enum FormatRetention
    {
        NONE, 
        FORMATTING, 
        EVENTS, 
        ALL;
    }
    
    public interface Joiner
    {
        ComponentBuilder join(final ComponentBuilder p0, final FormatRetention p1);
    }
}
