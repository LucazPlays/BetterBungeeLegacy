package net.md_5.bungee.api.chat;

import net.md_5.bungee.api.*;

public final class KeybindComponent extends BaseComponent
{
    private String keybind;
    
    public KeybindComponent(final KeybindComponent original) {
        super(original);
        this.setKeybind(original.getKeybind());
    }
    
    public KeybindComponent(final String keybind) {
        this.setKeybind(keybind);
    }
    
    @Override
    public BaseComponent duplicate() {
        return new KeybindComponent(this);
    }
    
    protected void toPlainText(final StringBuilder builder) {
        builder.append(this.getKeybind());
        super.toPlainText(builder);
    }
    
    protected void toLegacyText(final StringBuilder builder) {
        builder.append(this.getColor());
        if (this.isBold()) {
            builder.append(ChatColor.BOLD);
        }
        if (this.isItalic()) {
            builder.append(ChatColor.ITALIC);
        }
        if (this.isUnderlined()) {
            builder.append(ChatColor.UNDERLINE);
        }
        if (this.isStrikethrough()) {
            builder.append(ChatColor.STRIKETHROUGH);
        }
        if (this.isObfuscated()) {
            builder.append(ChatColor.MAGIC);
        }
        builder.append(this.getKeybind());
        super.toLegacyText(builder);
    }
    
    public String getKeybind() {
        return this.keybind;
    }
    
    public void setKeybind(final String keybind) {
        this.keybind = keybind;
    }
    
    @Override
    public String toString() {
        return "KeybindComponent(keybind=" + this.getKeybind() + ")";
    }
    
    public KeybindComponent() {
    }
}
