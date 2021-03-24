package net.md_5.bungee.command;

import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.*;

public class CommandBungee extends Command
{
    public CommandBungee() {
        super("bungee");
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void execute(final CommandSender sender, final String[] args) {
        sender.sendMessage("§7This server is running §eBetterBungee§7 version §a" + BungeeCord.getInstance().getBetterbungee().Version + "§7 by §bLuca_zPlays");
    }
}
