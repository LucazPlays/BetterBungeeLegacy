package net.md_5.bungee.command;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.connection.*;

@SuppressWarnings("deprecation")
public class CommandIP extends PlayerCommand
{
    public CommandIP() {
        super("ip", "bungeecord.command.ip", new String[0]);
    }
    
    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ProxyServer.getInstance().getTranslation("username_needed", new Object[0]));
            return;
        }
        final ProxiedPlayer user = ProxyServer.getInstance().getPlayer(args[0]);
        if (user == null) {
            sender.sendMessage(ProxyServer.getInstance().getTranslation("user_not_online", new Object[0]));
        }
        else {
            sender.sendMessage(ProxyServer.getInstance().getTranslation("command_ip", args[0], user.getSocketAddress()));
        }
    }
}
