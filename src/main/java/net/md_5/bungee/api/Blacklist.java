package net.md_5.bungee.api;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.BetterBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.DiscordWebhook.EmbedObject;
import net.md_5.bungee.netty.ChannelWrapper;

public class Blacklist {

	private static Blacklist Instance = new Blacklist();

	private ConcurrentHashMap<String, Integer> ratelimit = new ConcurrentHashMap<String, Integer>();

	private ConcurrentHashMap<String, Integer> auth = new ConcurrentHashMap<String, Integer>();

	private int globalratelimit = 0;

	private int globalfaviconlimit = 0;

	private int connectionratelimit = 0;

	private int connectionspersecond = 0;

	private int peripratelimit = 0;

	private int blockedipadresses = 0;

	private int blockedconnections = 0;

	@Getter
	private int averagecps = 0;

	@Getter
	private boolean underattack = false;

	@Setter
	@Getter
	private boolean protection = false;

	private Set<String> blacklist = ConcurrentHashMap.newKeySet();

	private Set<String> whitelist = ConcurrentHashMap.newKeySet();

	public Set<String> getBlacklist() {
		return blacklist;
	}

	public boolean isBlacklisted(String stg) {
		if (!protection) {
			return false;
		}
		return blacklist.contains(stg);
	}

	public void addBlacklist(String stg) {
		if (!protection) {
			return;
		}
		StatisticsAPI.getInstance().addblockedConnection();
		blacklist.add(stg);
		return;
	}

	public boolean isBlacklisted(InetAddress inet) {
		if (!protection) {
			return false;
		}
		String ip = inet.toString();
		return blacklist.contains(ip);
	}

	public boolean addBlacklist(InetAddress inet) {
		if (!protection) {
			return false;
		}
		String ip = inet.toString();
		BungeeCord.getInstance().getBetterBungee().getAddblacklist().add(ip);
		return blacklist.add(ip);
	}

	public void clearBlacklist() {
		if (!protection) {
			return;
		}
		BungeeCord.getInstance().getBetterBungee().getRemoveblacklist().addAll(getBlacklist());
		blacklist.clear();
	}

	public boolean removeBlacklist(String stg) {
		if (!protection) {
			return false;
		}
		BungeeCord.getInstance().getBetterBungee().getRemoveblacklist().add(stg);
		return blacklist.remove(stg);
	}

	public void setBlacklist(Set<String> blacklist) {
		this.blacklist = blacklist;
	}

	public static Blacklist getInstance() {
		return Instance;
	}

	public Blacklist() {
		new Thread(() -> {

			List<Integer> averagecpslist = new ArrayList<Integer>();

			while (true) {
				try {
					for (Entry<String, Integer> es : ratelimit.entrySet()) {
						if (es.getValue() > 0) {
							ratelimit.put(es.getKey(), es.getValue() - 1);
						} else {
							ratelimit.remove(es.getKey());
						}
					}

					int average = 0;

					averagecpslist.add(connectionspersecond);

					if (averagecpslist.size() > 10) {
						averagecpslist.remove(0);
						for (Integer integer : averagecpslist) {
							average += integer;
						}
					}

					if (average == 0) {
						averagecps = 0;
					} else {
						averagecps = average / averagecpslist.size();
					}
					if (!BetterBungee.getInstance().discordwebhook.equals("none")) {
						if (underattack) {
							if (averagecps < 12) {
								new Thread(() -> {
									try {
										DiscordWebhook webhook = new DiscordWebhook(
												BetterBungee.getInstance().discordwebhook);

										EmbedObject object2 = new EmbedObject();

										object2.setColor(Color.GREEN);
										
										object2.addField("Attack Stopped", "BetterBungee", true);
										
										object2.addField("IPAdresses Blocked", "" + (Blacklist.getInstance().getBlacklist().size() - blockedipadresses),
												true);
										object2.addField("Connections Blocked", "" + (StatisticsAPI.getInstance().getBlockedConnections()
														- blockedconnections),
												true);
										
										object2.setThumbnail("https://s20.directupload.net/images/210808/2c6o8nwx.jpg");

										blockedipadresses = Blacklist.getInstance().getBlacklist().size();
										blockedconnections = StatisticsAPI.getInstance().getBlockedConnections();

										webhook.addEmbed(object2);

										try {
											webhook.execute();
										} catch (IOException e) {
											e.printStackTrace();
										}

									} catch (Throwable e) {
										e.printStackTrace();
									}
								}).start();
							}
						}

						if (!underattack) {
							if (averagecps > 12) {
								new Thread(() -> {
									try {

										blockedipadresses = Blacklist.getInstance().getBlacklist().size();
										blockedconnections = StatisticsAPI.getInstance().getBlockedConnections();

										DiscordWebhook webhook = new DiscordWebhook(
												BetterBungee.getInstance().discordwebhook);

										EmbedObject object2 = new EmbedObject();

										object2.setColor(Color.RED);
										object2.addField("Attack Detected", "BetterBungee", true);
										object2.addField("Connections Per Second",
												"" + Blacklist.getInstance().getAveragecps(), true);
										object2.setThumbnail("https://s20.directupload.net/images/210808/2c6o8nwx.jpg");

										webhook.addEmbed(object2);

										try {
											webhook.execute();
										} catch (IOException e) {
											e.printStackTrace();
										}

									} catch (Throwable e) {
										e.printStackTrace();
									}

								}).start();
							}
						}
					}
					underattack = averagecps > 12;

					connectionratelimit = 0;

					connectionspersecond = 0;

					Thread.sleep(1000);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public int ratelimit(String ip) {
		return ratelimit.get(ip);
	}

	public void createlimit(String ip) {
		if (!ratelimit.containsKey(ip)) {
			ratelimit.put(ip, 0);
		}
	}

	public void addlimit(String ip) {
		ratelimit.put(ip, ratelimit.get(ip) + 1);
	}

	public void addlimit(String ip, int i) {
		ratelimit.put(ip, ratelimit.get(ip) + i);
	}

	public void removelimit(String ip) {
		ratelimit.put(ip, ratelimit.get(ip) - 1);
	}

	public Set<String> getWhitelist() {
		return whitelist;
	}

	public void setWhitelist(Set<String> whitelist) {
		this.whitelist = whitelist;
	}

	public void addWhitelist(String stg) {
		if (!whitelist.contains(stg)) {
			BungeeCord.getInstance().getBetterBungee().getAddwhitelist().add(stg);
			this.whitelist.add(stg);
		}
	}

	public boolean containswhitelist(String stg) {
		return this.whitelist.contains(stg);
	}

	public void clearWhitelist() {
		BungeeCord.getInstance().getBetterBungee().getRemovewhitelist().addAll(getWhitelist());
		this.whitelist.clear();
	}

	public void removeWhitelist(String stg) {
		if (whitelist.contains(stg)) {
			BungeeCord.getInstance().getBetterBungee().getRemovewhitelist().add(stg);
			this.whitelist.remove(stg);
		}
	}

	public int getConnectionratelimit() {
		return connectionratelimit;
	}

	public void setConnectionratelimit(int globalratelimit) {
		this.connectionratelimit = globalratelimit;
	}

	public void addConnectionratelimit(int add) {
		this.connectionratelimit += add;
	}

	public void addConnectionspersecond(int add) {
		this.connectionspersecond += add;
	}

	public int getGlobalratelimit() {
		return globalratelimit;
	}

	public void setGlobalratelimit(int globalratelimit) {
		this.globalratelimit = globalratelimit;
	}

	public void addGlobalratelimit(int add) {
		this.globalratelimit += add;
	}

	public int getPerIPratelimit() {
		return peripratelimit;
	}

	public void setPerIPratelimit(int globalratelimit) {
		this.peripratelimit = globalratelimit;
	}

	public void addPerIPratelimit(int add) {
		this.peripratelimit += add;
	}

	public String getRealAdress(ChannelHandlerContext ctx) {
		final SocketAddress remote = ctx.channel().remoteAddress();
		final String addr = remote != null ? remote.toString() : "";
		return addr.split("/")[1].split(":")[0];
	}

	public String getRealAdress(SocketAddress socketaddress) {
		final String addr = socketaddress.toString() != null ? socketaddress.toString() : "";
		return addr.split("/")[1].split(":")[0];
	}

	public String getRealAdress(ChannelWrapper channel) {
		final SocketAddress remote = channel.getRemoteAddress();
		final String addr = remote != null ? remote.toString() : "";
		return addr.split("/")[1].split(":")[0];
	}

	public int getGlobalfaviconlimit() {
		return globalfaviconlimit;
	}

	public void setGlobalfaviconlimit(int globalfaviconlimit) {
		this.globalfaviconlimit = globalfaviconlimit;
	}

}
