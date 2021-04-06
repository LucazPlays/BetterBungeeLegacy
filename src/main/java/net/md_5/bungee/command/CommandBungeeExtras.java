// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.command;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.util.FieldUtils;

public class CommandBungeeExtras extends PlayerCommand implements TabExecutor {

	public static String bungeename = "betterbungee";

	public CommandBungeeExtras() {
		super(bungeename, "bungeecord.command.betterbungee", new String[] { "bungeeutil", "bbungee" });
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {

		if (args.length >= 1) {

			if (args[0].equalsIgnoreCase("blacklist")) {
				if (!sender.hasPermission("bungeecord.command." + bungeename + ".blacklist")) {
					sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
					return;
				}

				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("clear")) {
						Blacklist.getInstance().clearBlacklist();
						sender.sendMessage(BungeeCord.PREFIX + "§cBlacklist§7 gecleart!");
						return;
					} else if (args[1].equalsIgnoreCase("list")) {
						sender.sendMessage("§7--- §4Blacklist§7 ---");
						
						int i = 0;
						for (String ip : Blacklist.getInstance().getBlacklist()) {
							sender.sendMessage("§c"+(i++) + "§8  -  §e"+ip);
						}
						sender.sendMessage("§7--- §4Blacklist§7 ---");
						return;
					}
				} else if (args.length >= 3) {
					if (args[1].equalsIgnoreCase("add")) {
						Blacklist.getInstance().addBlacklist(args[2]);
						sender.sendMessage(BungeeCord.PREFIX + "§cBlacklist§7 " + args[2] + " wurde geaddet!");
						return;
					} else if (args[1].equalsIgnoreCase("remove")) {
						Blacklist.getInstance().removeBlacklist(args[2]);
						sender.sendMessage(BungeeCord.PREFIX + "§cBlacklist§7 " + args[2] + " wurde removed!");
						return;
					}
				}
				sender.sendMessage(BungeeCord.PREFIX + "§7/betterbungee blacklist list");
				sender.sendMessage(BungeeCord.PREFIX + "§7/betterbungee blacklist clear");
				sender.sendMessage(BungeeCord.PREFIX + "§7/betterbungee blacklist add <ip>");
				sender.sendMessage(BungeeCord.PREFIX + "§7/betterbungee blacklist remove <ip>");
				return;
			}

			if (args[0].equalsIgnoreCase("playerproxys")) {
				sender.sendMessage(BungeeCord.PREFIX + "§aSpielerliste");
				for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
					if (all.getProxyAddress() != null) {
						sender.sendMessage(TextComponent.fromLegacyText("   §8 - §a" + all.getName() + " §8- §e"
								+ all.getAddress().getAddress().getHostAddress() + " §8- §d" + all.getProxyAddress()));
					} else {
						sender.sendMessage(TextComponent.fromLegacyText("   §8 - §a" + all.getName() + " §8- §e"
								+ all.getAddress().getAddress().getHostAddress() + " §8- §d"));
					}
				}
				return;
			}

			if (args[0].equalsIgnoreCase("notifications")) {
				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("chat")) {
						sender.sendMessage(BungeeCord.PREFIX + "§9Notifications §7wurden auf Chat gestellt§7!");
						NotifyManager.getInstance().players.put(sender.getName(), ChatMessageType.CHAT);
						return;
					} else if (args[1].equalsIgnoreCase("actionbar")) {
						sender.sendMessage(BungeeCord.PREFIX + "§9Notifications §7wurden auf ActionBar gestellt§7!");
						NotifyManager.getInstance().players.put(sender.getName(), ChatMessageType.ACTION_BAR);
						return;
					}
					sender.sendMessage(BungeeCord.PREFIX + "§7Mache /" + bungeename + " notifications chat/actionbar");
					return;
				}
				if (sender.hasPermission("bungeecord.command." + bungeename + ".notifications")) {
					if (NotifyManager.getInstance().players.containsKey(sender.getName())) {
						NotifyManager.getInstance().players.remove(sender.getName());
						sender.sendMessage(BungeeCord.PREFIX + "§9Notifications §7wurden §cDeaktiviert§7!");
					} else {
						NotifyManager.getInstance().players.put(sender.getName(), ChatMessageType.ACTION_BAR);
						sender.sendMessage(BungeeCord.PREFIX + "§9Notifications §7wurden §aAktiviert§7!");
					}
					return;
				}
				sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
				return;
			}
			if (args[0].equalsIgnoreCase("pluginmanager")) {
				if (!sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager")) {
					sender.sendMessage(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!");
					return;
				}
				if (args.length == 2) {
					String cmd = args[1];
					if (cmd.equalsIgnoreCase("list")) {
						if (!sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager.list")) {
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
					sender.sendMessage(
							BungeeCord.PREFIX + "§7Mache /" + bungeename + " pluginmanager <un/re/load> <PLName>");
					return;
				}
				String cmd = args[1];
				String pl = args[2];
				if (cmd.equalsIgnoreCase("reload")) {
					if (!sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager.reload")) {
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
					if (!sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager.unload")) {
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
					if (!sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager.load")) {
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
		sender.sendMessage(BungeeCord.PREFIX + "§eMache /" + bungeename + " <cmd> <args>");

	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		ArrayList<String> sug = new ArrayList<String>();
		String complete = "" + args[args.length - 1];

		if (args.length == 0) {
			if (sender.hasPermission("bungeecord." + bungeename)) {
				sug.add("betterbungee");
				sug.add("bungeeutil");
			}
		}

		if (args.length == 4) {
			for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
				if (p.getName().toLowerCase().startsWith(complete.toLowerCase())) {
					sug.add(p.getName());
				}
			}
		}

		if (args.length == 1) {
			if (sender.hasPermission("bungeecord.command." + bungeename + ".notifications")) {
				sug.add("notifications");
			}
			if (sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager")) {
				sug.add("pluginmanager");
			}
			if (sender.hasPermission("bungeecord.command." + bungeename + ".playerproxys")) {
				sug.add("playerproxys");
			}
			if (sender.hasPermission("bungeecord.command." + bungeename + ".blacklist")) {
				sug.add("blacklist");
			}

		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("pluginmanager")) {
				if (sender.hasPermission("bungeecord.command." + bungeename + ".notifications")) {
					sug.add("chat");
					sug.add("actionbar");
				}
			}
			if (args[0].equalsIgnoreCase("blacklist")) {
				if (sender.hasPermission("bungeecord.command." + bungeename + ".blacklist")) {
					sug.add("list");
					sug.add("clear");
					sug.add("add");
					sug.add("remove");
				}
			}
			if (args[0].equalsIgnoreCase("pluginmanager")) {
				if (sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager")) {
					sug.add("list");
					sug.add("reload");
					sug.add("load");
					sug.add("unload");
				}
			}
		}

		if (args.length == 3) {
			if (args[1].equalsIgnoreCase("reload") || args[1].equalsIgnoreCase("unload")) {
				if (sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager.reload")
						|| sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager.unload")) {
					for (Plugin pl : BungeeCord.getInstance().pluginManager.getPlugins()) {
						sug.add(pl.getDescription().getName());
					}
				}
			}
		}

		if (sug.isEmpty()) {
			for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
				if (p.getName().toLowerCase().startsWith(complete.toLowerCase())) {
					sug.add(p.getName());
				}
			}
		}

		ArrayList<String> ret = new ArrayList<>();
		for (String s : sug) {
			if (s.startsWith(complete)) {
				ret.add(s);
			}
		}
		return ret;
	}
}
