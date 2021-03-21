package net.md_5.bungee.command;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import java.util.*;

public final class ConsoleCommandSender implements CommandSender
{
    private static final ConsoleCommandSender instance;
    
    @Override
    public void sendMessage(final String message) {
        ProxyServer.getInstance().getLogger().info(message);
    }
    
    @Override
    public void sendMessages(final String... messages) {
        for (final String message : messages) {
            this.sendMessage(message);
        }
    }
    
    @Override
    public void sendMessage(final BaseComponent... message) {
        this.sendMessage(BaseComponent.toLegacyText(message));
    }
    
    @Override
    public void sendMessage(final BaseComponent message) {
        this.sendMessage(message.toLegacyText());
    }
    
    @Override
    public String getName() {
        return "CONSOLE";
    }
    
    @Override
    public Collection<String> getGroups() {
        return Collections.emptySet();
    }
    
    @Override
    public void addGroups(final String... groups) {
        throw new UnsupportedOperationException("Console may not have groups");
    }
    
    @Override
    public void removeGroups(final String... groups) {
        throw new UnsupportedOperationException("Console may not have groups");
    }
    
    @Override
    public boolean hasPermission(final String permission) {
        return true;
    }
    
    @Override
    public void setPermission(final String permission, final boolean value) {
        throw new UnsupportedOperationException("Console has all permissions");
    }
    
    @Override
    public Collection<String> getPermissions() {
        return Collections.emptySet();
    }
    
    private ConsoleCommandSender() {
    }
    
    public static ConsoleCommandSender getInstance() {
        return ConsoleCommandSender.instance;
    }
    
    static {
        instance = new ConsoleCommandSender();
    }
}
