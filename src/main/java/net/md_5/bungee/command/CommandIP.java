package net.md_5.bungee.command;

import java.util.ArrayList;
import java.util.regex.Pattern;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CommandIP extends PlayerCommand {

	public CommandIP() {
		super("ip", "bungeecord.command.ip", new String[] { "bip", "betterip" });
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ProxyServer.getInstance().getTranslation("username_needed", new Object[0]));
			return;
		}

		IPChecker.getInstance().start(() -> {
			try {
				final ProxiedPlayer user = ProxyServer.getInstance().getPlayer(args[0]);
	
				if (user == null) {
					String ip = args[0];
					if (isValidInet4Address(ip)) {
						IPCheckerResult result = IPChecker.getInstance().getIPInfo(ip);
						if (result == null || !IPChecker.getInstance().isServiceonline()) {
							sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.getInstance().PREFIX + "§8[§6IPINFO§8]"));
							sender.sendMessage(TextComponent.fromLegacyText("§8 - §7IP: §c" + ip));
							sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.getInstance().PREFIX + "§8[§6IPINFO§8]"));
							return;
						}
						sendipmessage(sender, result, true);
					} else {
						sender.sendMessage(ProxyServer.getInstance().getTranslation("user_not_online", new Object[0]));
					}
				} else {
					IPCheckerResult result = IPChecker.getInstance().getIPInfo(user.getAddress().getAddress().getHostAddress());

					if (result == null || !IPChecker.getInstance().isServiceonline()) {
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.getInstance().PREFIX + "§8[§6IPINFO§8]"));
						sender.sendMessage(TextComponent.fromLegacyText("§8 - §7IP: §e" + user.getAddress().getAddress().getHostAddress()));
						sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.getInstance().PREFIX + "§8[§6IPINFO§8]"));
						return;
					}
	
					sendipmessage(sender, result, sender.hasPermission("bungeecord.command.ip.uncensored"));
				}
			} catch (Throwable t) {
				sender.sendMessage(t.getMessage());
			}
		});
	}

	public static void sendipmessage(final CommandSender sender, IPCheckerResult result, boolean uncensored) {
		sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.getInstance().PREFIX + "§8[§6IPINFO§8]"));
		if (uncensored) {
			sender.sendMessage(TextComponent.fromLegacyText("§8 - §7IP: §e" + result.getIP()));
		} else {
			sender.sendMessage(TextComponent.fromLegacyText("§8 - §7IP: §c" + "§lCENSORED"));
		}
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Country: §e" + result.getCountry()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7CountryCode: §e" + result.getCountryCode()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7City: §e" + result.getCity()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7ASN: §e" + result.getASN()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Company: §e" + result.getCompany()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Hosting: §e" + result.isHosting()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7VPN: §e" + result.isVPN()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Proxy: §e" + result.isProxy()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7TOR: §e" + result.isTOR()));
		sender.sendMessage(TextComponent.fromLegacyText("§8 - §7Residental: §e" + result.isResidental()));
		sender.sendMessage(TextComponent.fromLegacyText(BungeeCord.getInstance().PREFIX + "§8[§6IPINFO§8]"));
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		ArrayList<String> sug = new ArrayList<String>();
		String complete = "" + args[args.length - 1];
		if (args.length == 1) {
			if (sender.hasPermission("bungeecord.command.ip")) {
				for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
					if (all.getName().toLowerCase().startsWith(complete.toLowerCase())) {
						sug.add(all.getName());
					}
				}
			}
		}
		return sug;
	}

	private static final String IPV4_REGEX = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";

	private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

	public static boolean isValidInet4Address(String ip) {
		if (ip == null) {
			return false;
		}

		if (!IPv4_PATTERN.matcher(ip).matches()) {
			return false;
		}

		String[] parts = ip.split("\\.");

		// verify that each of the four subgroups of IPv4 addresses is legal
		try {
			for (String segment : parts) {
				// x.0.x.x is accepted but x.01.x.x is not
				if (Integer.parseInt(segment) > 255 || (segment.length() > 1 && segment.startsWith("0"))) {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}
}
