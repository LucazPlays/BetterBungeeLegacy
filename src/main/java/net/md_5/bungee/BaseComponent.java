package net.md_5.bungee;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

public abstract class BaseComponent
{
    BaseComponent parent;
    private ChatColor color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private String insertion;
    private List<BaseComponent> extra;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    
    BaseComponent(final BaseComponent old) {
        this.copyFormatting(old, ComponentBuilder.FormatRetention.ALL, true);
        if (old.getExtra() != null) {
            for (final BaseComponent extra : old.getExtra()) {
                this.addExtra(extra.duplicate());
            }
        }
    }
    
    public void copyFormatting(final BaseComponent component) {
        this.copyFormatting(component, ComponentBuilder.FormatRetention.ALL, true);
    }
    
    public void copyFormatting(final BaseComponent component, final boolean replace) {
        this.copyFormatting(component, ComponentBuilder.FormatRetention.ALL, replace);
    }
    
    public void copyFormatting(final BaseComponent component, final ComponentBuilder.FormatRetention retention, final boolean replace) {
        if (retention == ComponentBuilder.FormatRetention.EVENTS || retention == ComponentBuilder.FormatRetention.ALL) {
            if (replace || this.clickEvent == null) {
                this.setClickEvent(component.getClickEvent());
            }
            if (replace || this.hoverEvent == null) {
                this.setHoverEvent(component.getHoverEvent());
            }
        }
        if (retention == ComponentBuilder.FormatRetention.FORMATTING || retention == ComponentBuilder.FormatRetention.ALL) {
            if (replace || this.color == null) {
                this.setColor(component.getColorRaw());
            }
            if (replace || this.bold == null) {
                this.setBold(component.isBoldRaw());
            }
            if (replace || this.italic == null) {
                this.setItalic(component.isItalicRaw());
            }
            if (replace || this.underlined == null) {
                this.setUnderlined(component.isUnderlinedRaw());
            }
            if (replace || this.strikethrough == null) {
                this.setStrikethrough(component.isStrikethroughRaw());
            }
            if (replace || this.obfuscated == null) {
                this.setObfuscated(component.isObfuscatedRaw());
            }
            if (replace || this.insertion == null) {
                this.setInsertion(component.getInsertion());
            }
        }
    }
    
    public void retain(final ComponentBuilder.FormatRetention retention) {
        if (retention == ComponentBuilder.FormatRetention.FORMATTING || retention == ComponentBuilder.FormatRetention.NONE) {
            this.setClickEvent(null);
            this.setHoverEvent(null);
        }
        if (retention == ComponentBuilder.FormatRetention.EVENTS || retention == ComponentBuilder.FormatRetention.NONE) {
            this.setColor(null);
            this.setBold(null);
            this.setItalic(null);
            this.setUnderlined(null);
            this.setStrikethrough(null);
            this.setObfuscated(null);
            this.setInsertion(null);
        }
    }
    
    public abstract BaseComponent duplicate();
    
    @Deprecated
    public BaseComponent duplicateWithoutFormatting() {
        final BaseComponent component = this.duplicate();
        component.retain(ComponentBuilder.FormatRetention.NONE);
        return component;
    }
    
    public static String toLegacyText(final BaseComponent... components) {
        final StringBuilder builder = new StringBuilder();
        for (final BaseComponent msg : components) {
            builder.append(msg.toLegacyText());
        }
        return builder.toString();
    }
    
    public static String toPlainText(final BaseComponent... components) {
        final StringBuilder builder = new StringBuilder();
        for (final BaseComponent msg : components) {
            builder.append(msg.toPlainText());
        }
        return builder.toString();
    }
    
    public ChatColor getColor() {
        if (this.color != null) {
            return this.color;
        }
        if (this.parent == null) {
            return ChatColor.WHITE;
        }
        return this.parent.getColor();
    }
    
    public ChatColor getColorRaw() {
        return this.color;
    }
    
    public boolean isBold() {
        if (this.bold == null) {
            return this.parent != null && this.parent.isBold();
        }
        return this.bold;
    }
    
    public Boolean isBoldRaw() {
        return this.bold;
    }
    
    public boolean isItalic() {
        if (this.italic == null) {
            return this.parent != null && this.parent.isItalic();
        }
        return this.italic;
    }
    
    public Boolean isItalicRaw() {
        return this.italic;
    }
    
    public boolean isUnderlined() {
        if (this.underlined == null) {
            return this.parent != null && this.parent.isUnderlined();
        }
        return this.underlined;
    }
    
    public Boolean isUnderlinedRaw() {
        return this.underlined;
    }
    
    public boolean isStrikethrough() {
        if (this.strikethrough == null) {
            return this.parent != null && this.parent.isStrikethrough();
        }
        return this.strikethrough;
    }
    
    public Boolean isStrikethroughRaw() {
        return this.strikethrough;
    }
    
    public boolean isObfuscated() {
        if (this.obfuscated == null) {
            return this.parent != null && this.parent.isObfuscated();
        }
        return this.obfuscated;
    }
    
    public Boolean isObfuscatedRaw() {
        return this.obfuscated;
    }
    
    public void setExtra(final List<BaseComponent> components) {
        for (final BaseComponent component : components) {
            component.parent = this;
        }
        this.extra = components;
    }
    
    public void addExtra(final TextComponent textComponent) {
        this.addExtra(new TextComponent(textComponent));
    }
    
    public void addExtra(final BaseComponent component) {
        if (this.extra == null) {
            this.extra = new ArrayList<BaseComponent>();
        }
        component.parent = this;
        this.extra.add(component);
    }
    
    public boolean hasFormatting() {
        return this.color != null || this.bold != null || this.italic != null || this.underlined != null || this.strikethrough != null || this.obfuscated != null || this.insertion != null || this.hoverEvent != null || this.clickEvent != null;
    }
    
    public String toPlainText() {
        final StringBuilder builder = new StringBuilder();
        this.toPlainText(builder);
        return builder.toString();
    }
    
    void toPlainText(final StringBuilder builder) {
        if (this.extra != null) {
            for (final BaseComponent e : this.extra) {
                e.toPlainText(builder);
            }
        }
    }
    
    public String toLegacyText() {
        final StringBuilder builder = new StringBuilder();
        this.toLegacyText(builder);
        return builder.toString();
    }
    
    void toLegacyText(final StringBuilder builder) {
        if (this.extra != null) {
            for (final BaseComponent e : this.extra) {
                e.toLegacyText(builder);
            }
        }
    }
    
    public void setColor(final ChatColor color) {
        this.color = color;
    }
    
    public void setBold(final Boolean bold) {
        this.bold = bold;
    }
    
    public void setItalic(final Boolean italic) {
        this.italic = italic;
    }
    
    public void setUnderlined(final Boolean underlined) {
        this.underlined = underlined;
    }
    
    public void setStrikethrough(final Boolean strikethrough) {
        this.strikethrough = strikethrough;
    }
    
    public void setObfuscated(final Boolean obfuscated) {
        this.obfuscated = obfuscated;
    }
    
    public void setInsertion(final String insertion) {
        this.insertion = insertion;
    }
    
    public void setClickEvent(final ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }
    
    public void setHoverEvent(final HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
    }
    
    @Override
    public String toString() {
        return "BaseComponent(color=" + this.getColor() + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", strikethrough=" + this.strikethrough + ", obfuscated=" + this.obfuscated + ", insertion=" + this.getInsertion() + ", extra=" + this.getExtra() + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ")";
    }
    
    public BaseComponent() {
    }
    
    public String getInsertion() {
        return this.insertion;
    }
    
    public List<BaseComponent> getExtra() {
        return this.extra;
    }
    
    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }
    
    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }
}
