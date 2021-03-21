package net.md_5.bungee.command;

import jline.console.completer.*;
import net.md_5.bungee.api.*;
import java.util.*;

public class ConsoleCommandCompleter implements Completer
{
    private final ProxyServer proxy;
    
    @Override
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        final List<String> suggestions = new ArrayList<String>();
        this.proxy.getPluginManager().dispatchCommand(this.proxy.getConsole(), buffer, suggestions);
        candidates.addAll(suggestions);
        final int lastSpace = buffer.lastIndexOf(32);
        return (lastSpace == -1) ? (cursor - buffer.length()) : (cursor - (buffer.length() - lastSpace - 1));
    }
    
    public ConsoleCommandCompleter(final ProxyServer proxy) {
        this.proxy = proxy;
    }
}
