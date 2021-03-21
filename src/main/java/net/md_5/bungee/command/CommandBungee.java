package net.md_5.bungee.command;

import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.*;

public class CommandBungee extends Command
{
    public CommandBungee() {
        super("bungee");
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void execute(final CommandSender sender, final String[] args) {
        sender.sendMessage(ChatColor.GOLD + "This server is running BetterBungee version " + 1 + " by Luca_zPlays");
    }
}
