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
    	String Version = "";
    	if (BungeeCord.getInstance().getBetterbungee().isSnapshotupdate()) {
    		Version = "§7Snapshot§8(§c"+ BungeeCord.getInstance().getBetterbungee().Version+"§8)";
    	} else {
    		Version ="§7Stable§8(§a"+ BungeeCord.getInstance().getBetterbungee().Version+"§8)";
    	}
        sender.sendMessage("§7This server is running §eBetterBungee§7 version §a" + Version + "§7 by §bLuca_zPlays");
    }
}