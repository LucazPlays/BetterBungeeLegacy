package net.md_5.bungee.command;

import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandBungee extends Command
{
    public CommandBungee() {
        super("bungee");
    }
    
    @Override
    public void execute(final CommandSender sender, final String[] args) {
		String Version = "";
		if (BungeeCord.getInstance().getBetterBungee().isSnapshotupdate()) {
			Version = "§7Snapshot§8(§c" + BungeeCord.getInstance().getBetterBungee().Version + "§8)";
		} else {
			Version = "§7Stable§8(§a" + BungeeCord.getInstance().getBetterBungee().Version + "§8)";
		}
		sender.sendMessage(TextComponent.fromLegacyText("§7This server is running §eBetterBungee§7 version §a" + Version + "§7 by §bLuca_zPlays"));
        sender.sendMessage(TextComponent.fromLegacyText("§7Infos at §bhttp://betterbungee.tk"));
		return;
    }
}