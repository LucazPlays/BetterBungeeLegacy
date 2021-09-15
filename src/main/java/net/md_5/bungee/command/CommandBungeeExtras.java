package net.md_5.bungee.command;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

import net.md_5.bungee.BetterBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerListAPI;
import net.md_5.bungee.api.StatisticsAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.util.FieldUtils;

public class CommandBungeeExtras extends PlayerCommand implements TabExecutor {

	public static String bungeename = "betterbungee";

	public CommandBungeeExtras() {
		super(bungeename, null, new String[] { "bungeeutil", "bbungee" });
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		if (!sender.hasPermission("bungeecord.command.betterbungee")) {
			String Version = "";
			if (BungeeCord.getInstance().getBetterBungee().isSnapshotupdate()) {
				Version = "§7Snapshot§8(§c" + BungeeCord.getInstance().getBetterBungee().Version + "§8)";
			} else {
				Version = "§7Stable§8(§a" + BungeeCord.getInstance().getBetterBungee().Version + "§8)";
			}
			sender.sendMessage(TextComponent.fromLegacyText(
					"§7This server is running §eBetterBungee§7 version §a" + Version + "§7 by §bLuca_zPlays"));
			sender.sendMessage(TextComponent.fromLegacyText("§7Infos at §dhttps://discord.gg/EUVYjafMC8"));
			return;
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("statistics")) {
				if (!sender.hasPermission("bungeecord.command." + bungeename + ".statistics")) {
					sender.sendMessage(
							TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!"));
				}
				sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§eStatistics"));
				sender.sendMessage(TextComponent.fromLegacyText(" "));
				sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Potential User: §e" + ServerListAPI.getInstance().getUsersinServerList()));
				sender.sendMessage(TextComponent.fromLegacyText(" "));
				sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Blocked Connections: §e" + StatisticsAPI.getInstance().getBlockedConnections()));
				sender.sendMessage(TextComponent.fromLegacyText(" "));
				sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Blocked Crash Attempt: §e" + StatisticsAPI.getInstance().getBlockedCrashAttempts()));
				sender.sendMessage(TextComponent.fromLegacyText(" "));
				sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§eStatistics"));
				return;
			}
			if (args[0].equalsIgnoreCase("firewall")) {
				if (!sender.hasPermission("bungeecord.command." + bungeename + ".firewall")) {
					sender.sendMessage(
							TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7You don't have the Permission for this Command§7!"));
					return;
				}
				if (args.length >= 2) {
					if (args[1].equalsIgnoreCase("info")) {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§eSettings"));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Protection: §e" + Blacklist.getInstance().isProtection()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Global Rate Limit: §e" + Blacklist.getInstance().getGlobalratelimit()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Per IP Rate Limit: §e" + Blacklist.getInstance().getPerIPratelimit()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7FaviconLimit: §e" + Blacklist.getInstance().getGlobalfaviconlimit()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7ProxyOnAuthCheck: §e" + BetterBungee.getInstance().isProxycheckonauth()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7ProxyOnAuthCheckActivationLimit: §e" + BetterBungee.getInstance().getStartdenyproxyauthlimit()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Only Connections for Serverlist: §e" + BetterBungee.getInstance().isPingcheck()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7ServerListCheckActivationLimit: §e" + BetterBungee.getInstance().getPingcheckonconnectlimit()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7OnlyHostnames: §e" + BetterBungee.getInstance().isHostprotectionnames()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Hostnames: §e" + BetterBungee.getInstance().getHostnames().toString()));
						sender.sendMessage(TextComponent.fromLegacyText(" "));
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§eSettings"));
						return;
					}
					if (args.length >= 3) {
						if (args[1].equalsIgnoreCase("setfaviconlimit")) {
							try {
								int integer = Integer.valueOf(args[2]);
								BetterBungee.getInstance().setPingcheckonconnectlimit(integer);;
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eFavicon Limit§7 set to §a" + integer + "§7!"));
							} catch (Exception ex) {
								sender.sendMessage(
										TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Please Provide a Number"));
							}
							return;
						}
						if (args[1].equalsIgnoreCase("setproxyauthlimit")) {
							try {
								int integer = Integer.valueOf(args[2]);
								BetterBungee.getInstance().setStartdenyproxyauthlimit(integer);;
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eProxyAuths Limit§7 set to §a" + integer + "§7!"));
							} catch (Exception ex) {
								sender.sendMessage(
										TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Please Provide a Number"));
							}
							return;
						}
						if (args[1].equalsIgnoreCase("onlyserverlistlimit")) {
							try {
								int integer = Integer.valueOf(args[2]);
								BetterBungee.getInstance().setPingcheckonconnectlimit(integer);;
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eOnlyServerList Limit§7 set to §a" + integer + "§7!"));
							} catch (Exception ex) {
								sender.sendMessage(
										TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Please Provide a Number"));
							}
							return;
						}
						if (args[1].equalsIgnoreCase("setgloballimit")) {
							try {
								int integer = Integer.valueOf(args[2]);
								Blacklist.getInstance().setGlobalratelimit(integer);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eGlobal Rate Limit§7 set to §a" + integer + "§7!"));
							} catch (Exception ex) {
								sender.sendMessage(
										TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Please Provide a Number"));
							}
							return;
						}
						if (args[1].equalsIgnoreCase("setperiplimit")) {
							try {
								int integer = Integer.valueOf(args[2]);
								Blacklist.getInstance().setPerIPratelimit(integer);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§ePer IP Limit§7 set to §a" + integer + "§7!"));
							} catch (Exception ex) {
								sender.sendMessage(
										TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Please Provide a Number"));
							}
							return;
						}
						
						if (args[1].equalsIgnoreCase("setprotection")) {
							if (args[2].equalsIgnoreCase("true")) {
								Blacklist.getInstance().setProtection(true);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eBasic Protection§7 set to §a" + true + "§7!"));
							} else if (args[2].equalsIgnoreCase("false")) {
								Blacklist.getInstance().setProtection(false);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eBasic Protection§7 set to §a" + false + "§7!"));
							} else {
								sender.sendMessage(TextComponent
										.fromLegacyText(BungeeCord.PREFIX + "§7Please write true or false"));
							}
							return;
						}
						
						if (args[1].equalsIgnoreCase("sethostnameprotection")) {
							if (args[2].equalsIgnoreCase("true")) {
								BetterBungee.getInstance().setHostprotectionnames(true);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eHostname Protection§7 set to §a" + true + "§7!"));
							} else if (args[2].equalsIgnoreCase("false")) {
								BetterBungee.getInstance().setHostprotectionnames(false);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eHostname Protection§7 set to §a" + false + "§7!"));
							} else {
								sender.sendMessage(TextComponent
										.fromLegacyText(BungeeCord.PREFIX + "§7Please write true or false"));
							}
							return;
						}
						
						if (args[1].equalsIgnoreCase("setproxyauthprotection")) {
							if (args[2].equalsIgnoreCase("true")) {
								BetterBungee.getInstance().setProxycheckonauth(true);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eProxyAuth Protection§7 set to §a" + true + "§7!"));
							} else if (args[2].equalsIgnoreCase("false")) {
								BetterBungee.getInstance().setProxycheckonauth(false);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eProxyAuth Protection §7 set to §a" + false + "§7!"));
							} else {
								sender.sendMessage(TextComponent
										.fromLegacyText(BungeeCord.PREFIX + "§7Please write true or false"));
							}
							return;
						}
						
						if (args[1].equalsIgnoreCase("setserverlistprotection")) {
							if (args[2].equalsIgnoreCase("true")) {
								BetterBungee.getInstance().setPingcheck(true);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eServerList Protection§7 set to §a" + true + "§7!"));
							} else if (args[2].equalsIgnoreCase("false")) {
								BetterBungee.getInstance().setPingcheck(false);
								sender.sendMessage(TextComponent.fromLegacyText(
										BungeeCord.PREFIX + "§eServerList Protection§7 set to §a" + false + "§7!"));
							} else {
								sender.sendMessage(TextComponent
										.fromLegacyText(BungeeCord.PREFIX + "§7Please write true or false"));
							}
							return;
						}
					}
					return;
				}
				sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7/betterbungee firewall info"));
				sender.sendMessage(TextComponent.fromLegacyText(
						BungeeCord.PREFIX + "§7/betterbungee firewall setgloballimit <0-" + Integer.MAX_VALUE + ">"));
				sender.sendMessage(TextComponent.fromLegacyText(
						BungeeCord.PREFIX + "§7/betterbungee firewall setperiplimit <0-" + Integer.MAX_VALUE + ">"));
				sender.sendMessage(TextComponent
						.fromLegacyText(BungeeCord.PREFIX + "§7/betterbungee firewall setprotection <false/true>"));
				return;
			}

			if (args[0].equalsIgnoreCase("blacklist")) {
				if (!sender.hasPermission("bungeecord.command." + bungeename + ".blacklist")) {
					sender.sendMessage(
							TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Dazu hast du keine §9Rechte§7!"));
					return;
				}

				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("clear")) {
						Blacklist.getInstance().clearBlacklist();
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§cBlacklist§7 cleared!"));
						return;
					} else if (args[1].equalsIgnoreCase("list")) {
						sender.sendMessage(TextComponent.fromLegacyText("§7--- §4Blacklist§7 ---"));

						int i = 0;
						for (String ip : Blacklist.getInstance().getBlacklist()) {
							sender.sendMessage(TextComponent.fromLegacyText("§c" + (i++) + "§8  -  §e" + ip));
						}
						sender.sendMessage(TextComponent.fromLegacyText("§7--- §4Blacklist§7 ---"));
						return;
					}
				} else if (args.length >= 3) {
					if (args[1].equalsIgnoreCase("add")) {
						Blacklist.getInstance().addBlacklist(args[2]);
						sender.sendMessage(TextComponent
								.fromLegacyText(BungeeCord.PREFIX + "§cBlacklist§7 " + args[2] + " added!"));
						return;
					} else if (args[1].equalsIgnoreCase("remove")) {
						Blacklist.getInstance().removeBlacklist(args[2]);
						sender.sendMessage(TextComponent
								.fromLegacyText(BungeeCord.PREFIX + "§cBlacklist§7 " + args[2] + " removed!"));
						return;
					}
				}
			}

			if (args[0].equalsIgnoreCase("whitelist")) {
				if (!sender.hasPermission("bungeecord.command." + bungeename + ".whitelist")) {
					sender.sendMessage(
							TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7You don't have the Permission for this Command§7!"));
					return;
				}

				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("clear")) {
						Blacklist.getInstance().clearWhitelist();
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§aWhitelist§7 cleared!"));
						return;
					} else if (args[1].equalsIgnoreCase("list")) {
						sender.sendMessage(TextComponent.fromLegacyText("§7--- §2Whitelist§7 ---"));

						int i = 0;
						for (String ip : Blacklist.getInstance().getWhitelist()) {
							sender.sendMessage(TextComponent.fromLegacyText("§a" + (i++) + "§8  -  §e" + ip));
						}

						sender.sendMessage(TextComponent.fromLegacyText("§7--- §2Whitelist§7 ---"));
						return;
					}
				} else if (args.length >= 3) {
					if (args[1].equalsIgnoreCase("add")) {
						Blacklist.getInstance().addWhitelist(args[2]);
						sender.sendMessage(TextComponent
								.fromLegacyText(BungeeCord.PREFIX + "§aWhitelist§7 " + args[2] + " added!"));
						return;
					} else if (args[1].equalsIgnoreCase("remove")) {
						Blacklist.getInstance().removeWhitelist(args[2]);
						sender.sendMessage(TextComponent
								.fromLegacyText(BungeeCord.PREFIX + "§aWhitelist§7 " + args[2] + " removed!"));
						return;
					}
				}
				sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7/betterbungee whitelist list"));
				sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7/betterbungee whitelist clear"));
				sender.sendMessage(
						TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7/betterbungee whitelist add <ip>"));
				sender.sendMessage(
						TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7/betterbungee whitelist remove <ip>"));
				return;
			}

			if (args[0].equalsIgnoreCase("playerproxys")) {
				sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§aPlayerlist"));
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
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§9Notifications §7set to Chat!"));
						NotifyManager.getInstance().getPlayers().put(sender.getName(), ChatMessageType.CHAT);
						return;
					} else if (args[1].equalsIgnoreCase("actionbar")) {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§9Notifications §7set to ActionBar!"));
						NotifyManager.getInstance().getPlayers().put(sender.getName(), ChatMessageType.ACTION_BAR);
						return;
					} else if (args[1].equalsIgnoreCase("showcps")) {
						if (NotifyManager.getInstance().getTitlePlayers().contains(sender.getName())) {
							sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7removed CPS Title §9Notifications"));
							NotifyManager.getInstance().getTitlePlayers().remove(sender.getName());
							return;
						} else {
							sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7added CPS Title §9Notifications"));
							NotifyManager.getInstance().getTitlePlayers().add(sender.getName());
							return;
						}
					}
					sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Use /" + bungeename + " notifications chat/actionbar/showcps"));
					return;
				}
				if (NotifyManager.getInstance().getPlayers().containsKey(sender.getName())) {
					NotifyManager.getInstance().getPlayers().remove(sender.getName());
					sender.sendMessage(TextComponent
							.fromLegacyText(BungeeCord.PREFIX + "§9Notifications §cDisabled§7!"));
				} else {
					NotifyManager.getInstance().getPlayers().put(sender.getName(), ChatMessageType.ACTION_BAR);
					sender.sendMessage(TextComponent
							.fromLegacyText(BungeeCord.PREFIX + "§9Notifications §aEnabled§7!"));
				}
				return;
			}
			
			if (args[0].equalsIgnoreCase("pluginmanager")) {
				if (!sender.hasPermission("bungeecord.command." + bungeename + ".pluginmanager")) {
					sender.sendMessage(
							TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7You don't have the Permission for this Command§7!"));
					return;
				}
				if (args.length == 2) {
					String cmd = args[1];
					if (cmd.equalsIgnoreCase("list")) {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Loaded §9Plugins§7:"));
						for (Plugin pl : BungeeCord.getInstance().pluginManager.getPlugins()) {
							sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§9"
									+ pl.getDescription().getName() + (pl.getDescription().getAuthor() == null ? ""
											: "§7 from §a" + pl.getDescription().getAuthor())));

						}
						return;
					}
				}
				if (args.length != 3) {
					sender.sendMessage(TextComponent.fromLegacyText(
							BungeeCord.PREFIX + "§7Use /" + bungeename + " pluginmanager <un/re/load> <pluginname>"));
					return;
				}
				String cmd = args[1];
				String pl = args[2];
				if (cmd.equalsIgnoreCase("reload")) {
					Plugin plugin = FieldUtils.findPlugin(pl);
					if (plugin == null) {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Invalid Plugin!"));
						return;
					}
					File pluginfile = plugin.getFile();
					FieldUtils.unloadPlugin(plugin);
					FieldUtils.loadPlugin(pluginfile);
					sender.sendMessage(TextComponent.fromLegacyText(
							BungeeCord.PREFIX + " §9" + plugin.getDescription().getName() + " §7is Reloaded§7!"));
					return;
				}
				if (cmd.equalsIgnoreCase("unload")) {
					Plugin plugin = FieldUtils.findPlugin(pl);
					if (plugin == null) {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Invalid Plugin!"));
						return;
					}
					String name = plugin.getDescription().getName();
					FieldUtils.unloadPlugin(plugin);
					sender.sendMessage(
							TextComponent.fromLegacyText(BungeeCord.PREFIX + " §9" + name + " §7is now unloaded!"));
					return;
				}
				if (cmd.equalsIgnoreCase("load")) {
					Plugin plugin = FieldUtils.findPlugin(pl);
					if (plugin != null) {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7already loaded!!"));
						return;
					}

					File pluginfile = FieldUtils.findFile(pl);
					if (!pluginfile.exists()) {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Invalid Plugin!"));
						return;
					}

					boolean success = FieldUtils.loadPlugin(pluginfile);
					if (!success) {
						sender.sendMessage(
								TextComponent.fromLegacyText(BungeeCord.PREFIX + " §9" + pl + " §7is now Enabled§7!"));
					} else {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§cError!"));
					}
					return;
				}
			}
			sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§cError§7!"));
		}
		sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§eUse /" + bungeename + " <cmd> <args>"));

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
			if (sender.hasPermission("bungeecord.command." + bungeename + ".firewall")) {
				sug.add("firewall");
			}
			if (sender.hasPermission("bungeecord.command." + bungeename + ".statistics")) {
				sug.add("statistics");
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
			if (sender.hasPermission("bungeecord.command." + bungeename + ".whitelist")) {
				sug.add("whitelist");
			}

		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("notifications")) {
				if (sender.hasPermission("bungeecord.command." + bungeename + ".notifications")) {
					sug.add("chat");
					sug.add("actionbar");
					sug.add("showcps");
				}
			}
			if (args[0].equalsIgnoreCase("firewall")) {
				if (sender.hasPermission("bungeecord.command." + bungeename + ".firewall")) {
					sug.add("info");
					sug.add("setprotection");
					sug.add("sethostnameprotection");
					sug.add("setproxyauthprotection");
					sug.add("setserverlistprotection");
					sug.add("setgloballimit");
					sug.add("setperiplimit");
					sug.add("onlyserverlistlimit");
					sug.add("setproxyauthlimit");
					sug.add("setfaviconlimit");
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
			if (args[0].equalsIgnoreCase("whitelist")) {
				if (sender.hasPermission("bungeecord.command." + bungeename + ".whitelist")) {
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
			if (args[1].equalsIgnoreCase("setprotection")) {
				sug.add("true");
				sug.add("false");
			}
			if (args[1].equalsIgnoreCase("sethostnameprotection")) {
				sug.add("true");
				sug.add("false");
			}
			if (args[1].equalsIgnoreCase("setserverlistprotection")) {
				sug.add("true");
				sug.add("false");
			}
			if (args[1].equalsIgnoreCase("setproxyauthprotection")) {
				sug.add("true");
				sug.add("false");
			}
			if (args[1].equalsIgnoreCase("setprotection")) {
				sug.add("true");
				sug.add("false");
			}
			if (args[1].equalsIgnoreCase("reload") || args[1].equalsIgnoreCase("unload")) {
				for (Plugin pl : BungeeCord.getInstance().pluginManager.getPlugins()) {
					sug.add(pl.getDescription().getName());
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
