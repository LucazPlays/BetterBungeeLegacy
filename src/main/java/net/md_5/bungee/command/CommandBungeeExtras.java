// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.command;

import java.io.File;
import java.util.ArrayList;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.util.FieldUtils;

@SuppressWarnings("deprecation")
public class CommandBungeeExtras extends PlayerCommand implements TabExecutor {

	public static String bungeename = "betterbungee";
	
	public CommandBungeeExtras() {
		super(bungeename, "bungeecord.command.betterbungee", new String[] {"bungeeutil"});
	}


	@Override
	public void execute(final CommandSender sender, final String[] args) {

		if (args.length >= 1) {

//			if (args[0].equalsIgnoreCase("reload")) {
//				if (!sender.hasPermission("bungeecord.command."+bungeename+".reload")) {
//					sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
//					return;
//				}
//				BungeeCord.getInstance().config.load();
//				BungeeCord.getInstance().reloadMessages();
//				BungeeCord.getInstance().stopListeners();
//				BungeeCord.getInstance().startListeners();
//				BungeeCord.getInstance().getPluginManager().callEvent(new ProxyReloadEvent(sender));
//				sender.sendMessage(BungeeCord.PREFIX + " &6Betterbungee §7wurde neugeladen!");
//
//				return;
//			}
			
			if (args[0].equalsIgnoreCase("notifications")) {
				if (sender.hasPermission("bungeecord.command."+bungeename+".notifications")) {
					if (NotifyManager.getInstance().players.contains(sender.getName())) {
						NotifyManager.getInstance().players.remove(sender.getName());
						sender.sendMessage(BungeeCord.PREFIX + "§9Notifications §7wurden §cDeaktiviert§7!");
					} else {
						NotifyManager.getInstance().players.add(sender.getName());
						sender.sendMessage(BungeeCord.PREFIX + "§9Notifications §7wurden §aAktiviert§7!");
					}
					return;
				}
				sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
				return;
			}
			if (args[0].equalsIgnoreCase("pluginmanager")) {
				if (!sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager")) {
					sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
					return;
				}
				if (args.length == 2) {
					String cmd = args[1];
					if (cmd.equalsIgnoreCase("list")) {
						if (!sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager.list")) {
							sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
							return;
						}
						sender.sendMessage(BungeeCord.PREFIX + "§7Loaded §9Plugins§7:");
						for (Plugin pl : BungeeCord.getInstance().pluginManager.getPlugins()) {
							sender.sendMessage(BungeeCord.PREFIX + "§9" + pl.getDescription().getName()
									+ (pl.getDescription().getAuthor() == null ? ""
											: "§7 von §a" + pl.getDescription().getAuthor()));
							
						}
						return;
					}
				}
				if (args.length != 3) {
					sender.sendMessage(BungeeCord.PREFIX + "§7Mache /"+bungeename+" pluginmanager <un/re/load> <PLName>");
					return;
				}
				String cmd = args[1];
				String pl = args[2];
				if (cmd.equalsIgnoreCase("reload")) {
					if (!sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager.reload")) {
						sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
						return;
					}
					Plugin plugin = FieldUtils.findPlugin(pl);
					if (plugin == null) {
						sender.sendMessage(BungeeCord.PREFIX + "§7Ungültiges Plugin!");
						return;
					}
					File pluginfile = plugin.getFile();
					FieldUtils.unloadPlugin(plugin);
					FieldUtils.loadPlugin(pluginfile);
					sender.sendMessage(BungeeCord.PREFIX + "§7Das Plugin §9" + plugin.getDescription().getName()
							+ " §7wurde §aNeugeladen§7!");
					return;
				}
				if (cmd.equalsIgnoreCase("unload")) {
					if (!sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager.unload")) {
						sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
						return;
					}
					Plugin plugin = FieldUtils.findPlugin(pl);
					if (plugin == null) {
						sender.sendMessage(BungeeCord.PREFIX + "§7Ungültiges Plugin!");
						return;
					}
					String name = plugin.getDescription().getName();
					FieldUtils.unloadPlugin(plugin);
					sender.sendMessage(BungeeCord.PREFIX + "§7Das Plugin §9" + name + " §7wurde §aEntladen§7!");
					return;
				}
				if (cmd.equalsIgnoreCase("load")) {
					if (!sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager.load")) {
						sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
						return;
					}
					Plugin plugin = FieldUtils.findPlugin(pl);
					if (plugin != null) {
						sender.sendMessage(BungeeCord.PREFIX + "§7Bereits geladen!!");
						return;
					}

					File pluginfile = FieldUtils.findFile(pl);
					if (!pluginfile.exists()) {
						sender.sendMessage(BungeeCord.PREFIX + "§7Ungültiges Plugin!");
						return;
					}

					boolean success = FieldUtils.loadPlugin(pluginfile);
					if (!success) {
						sender.sendMessage(BungeeCord.PREFIX + "§7Das Plugin §9" + pl + " §7wurde §aGeladen§7!");
					} else {
						sender.sendMessage(BungeeCord.PREFIX + "§cFehler!");
					}
					return;
				}
			}
			sender.sendMessage(BungeeCord.PREFIX + "§cFehler§7!");
		}
		sender.sendMessage(BungeeCord.PREFIX + "§eMache /"+bungeename+" <cmd> <args>");

	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		ArrayList<String> sug = new ArrayList<String>();
		String complete = "" + args[args.length - 1];
		if (args.length == 4) {
			for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers())
				if (p.getName().toLowerCase().startsWith(complete.toLowerCase()))
					sug.add(p.getName());
		}
		if (args.length == 1) {
			if (sender.hasPermission("bungeecord.command."+bungeename+".notifications"))
				sug.add("notifications");
//			if (sender.hasPermission("bungeecord.command."+bungeename+".reload"))
//				sug.add("reload");
			if (sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager"))
				sug.add("pluginmanager");

		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("pluginmanager")) {
				if (sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager")) {
					sug.add("list");
					sug.add("reload");
					sug.add("load");
					sug.add("unload");
				}
			}
		}
		if (args.length == 3) {
			if (args[1].equalsIgnoreCase("reload") || args[1].equalsIgnoreCase("unload")) {
				if (sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager.reload")
						|| sender.hasPermission("bungeecord.command."+bungeename+".pluginmanager.unload"))
					for (Plugin pl : BungeeCord.getInstance().pluginManager.getPlugins()) {
						sug.add(pl.getDescription().getName());
					}
			}
		}
		if (sug.isEmpty()) {
			for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers())
				if (p.getName().toLowerCase().startsWith(complete.toLowerCase()))
					sug.add(p.getName());
		}
		
		ArrayList<String> ret = new ArrayList<>();
		for (String s : sug)
			if (s.startsWith(complete))
				ret.add(s);

		return ret;
	}
}
