package net.md_5.bungee.command;

import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.BetterBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandBungee extends Command
{
    public CommandBungee() {
        super("bungee");
    }
    
    @Override
    public void execute(final CommandSender sender, final String[] args) {
		String Version = "";
		if (BungeeCord.getInstance().getBetterBungee().isGithub()) {
			Version = "§7Github§8(§f" + BungeeCord.getInstance().getBetterBungee().Version + "§8)";
		} else if (BungeeCord.getInstance().getBetterBungee().isSnapshotupdate()) {
			Version = "§7Snapshot§8(§c" + BungeeCord.getInstance().getBetterBungee().Version + "§8)";
		} else {
			Version = "§7Stable§8(§a" + BungeeCord.getInstance().getBetterBungee().Version + "§8)";
		}
        TextComponent text1 = new TextComponent();
        
        text1.addExtra("§7This server is running §eBetterBungee§7 version §a" + Version);
        text1.addExtra("§7 by §bLuca_zPlays");
        text1.setClickEvent(new ClickEvent(Action.OPEN_URL, "https://github.com/LucazPlays/BetterBungee/"));
        text1.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Link to Github")));

        sender.sendMessage(text1);
        
        TextComponent text2 = new TextComponent();
        text2.addExtra("§7More Infos at §dhttps://dsc.gg/betterbungee");
        text2.setClickEvent(new ClickEvent(Action.OPEN_URL, "https://dsc.gg/betterbungee"));
        text2.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Link to Discord")));

        sender.sendMessage(text2);

        
        TextComponent text3 = new TextComponent();
       
        text3.addExtra("§7BungeeCord Version §f#" + BetterBungee.getInstance().getBungeeCordVersion().substring(0, 7));
        text3.setClickEvent(new ClickEvent(Action.OPEN_URL, "https://github.com/SpigotMC/BungeeCord/tree/"+BetterBungee.getInstance().getBungeeCordVersion()));
        text3.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Link to Github")));
        
        sender.sendMessage(text3);

        return;
    }
}