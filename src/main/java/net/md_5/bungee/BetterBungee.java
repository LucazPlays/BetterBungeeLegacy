package net.md_5.bungee;

import java.awt.desktop.ScreenSleepEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.FileUtils;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.RestAPI;
import net.md_5.bungee.api.RestAPIResponse;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BetterBungee {

	public ServerInfo getLimboserver() {
		return limboserver;
	}

	public void setLimboserver(ServerInfo limboserver) {
		this.limboserver = limboserver;
	}

	public boolean isSendafkstolimbo() {
		return sendafkstolimbo;
	}

	public void setSendafkstolimbo(boolean sendafkstolimbo) {
		this.sendafkstolimbo = sendafkstolimbo;
	}

	public void setLimbomode(boolean limbomode) {
		this.limbomode = limbomode;
	}

	public boolean isBotchecks() {
		return botchecks;
	}

	public void setBotchecks(boolean botchecks) {
		this.botchecks = botchecks;
	}

	public boolean isLimbomode() {
		return limbomode;
	}

	public boolean isApiconnection() {
		return apiconnection;
	}

	public void setApiconnection(boolean apiconnection) {
		this.apiconnection = apiconnection;
	}

	public boolean isFirewallsync() {
		return firewallsync;
	}

	public int getPacketsizelimitsize() {
		return packetsizelimitsize;
	}

	public boolean isPacketsizelimit() {
		return packetsizelimit;
	}

	public boolean isManuelupdates() {
		return manuelupdates;
	}

	public ConcurrentLinkedQueue<String> getAddwhitelist() {
		return addwhitelist;
	}

	public void setAddwhitelist(ConcurrentLinkedQueue<String> addwhitelist) {
		this.addwhitelist = addwhitelist;
	}

	public ConcurrentLinkedQueue<String> getAddblacklist() {
		return addblacklist;
	}

	public void setAddblacklist(ConcurrentLinkedQueue<String> addblacklist) {
		this.addblacklist = addblacklist;
	}

	public ConcurrentLinkedQueue<String> getRemovewhitelist() {
		return removewhitelist;
	}

	public void setRemovewhitelist(ConcurrentLinkedQueue<String> removewhitelist) {
		this.removewhitelist = removewhitelist;
	}

	public ConcurrentLinkedQueue<String> getRemoveblacklist() {
		return removeblacklist;
	}

	public void setRemoveblacklist(ConcurrentLinkedQueue<String> removeblacklist) {
		this.removeblacklist = removeblacklist;
	}

	public boolean isDiscordintegration() {
		return discordintegration;
	}

	public void setDiscordintegration(boolean discordintegration) {
		this.discordintegration = discordintegration;
	}

	public String getDenyVPNbypasspermission() {
		return denyVPNbypasspermission;
	}

	public void setDenyVPNbypasspermission(String denyVPNbypasspermission) {
		this.denyVPNbypasspermission = denyVPNbypasspermission;
	}

	public String getDenyVPNkickmessage() {
		return denyVPNkickmessage.replaceAll("&", "§");
	}

	public void setDenyVPNkickmessage(String denyVPNkickmessage) {
		this.denyVPNkickmessage = denyVPNkickmessage;
	}

	public boolean isDenyVPNonJoin() {
		return denyVPNonJoin;
	}

	public void setDenyVPNonJoin(boolean denyVPNonJoin) {
		this.denyVPNonJoin = denyVPNonJoin;
	}

//	public boolean isProxyProtocol() {
//		return ProxyProtocol;
//	}
//
//	public void setProxyProtocol(boolean proxyProtocol) {
//		ProxyProtocol = proxyProtocol;
//	}

	public boolean isDisablebungeecommands() {
		return disablebungeecommands;
	}

	public boolean isProtection() {
		return protection;
	}

	public int getGloballimit() {
		return globallimit;
	}

	public int getPeriplimit() {
		return periplimit;
	}

	public int getSnapshotupdatecountdown() {
		return snapshotupdatecountdown;
	}

	public boolean isRestartonupdate() {
		return restartonupdate;
	}

	public boolean isSnapshotupdate() {
		return snapshotupdate;
	}

	public ConcurrentHashMap<UUID, Long> getAfkList() {
		return afk;
	}

	public ConcurrentHashMap<UUID, ServerInfo> getReconnectServer() {
		return reconnectserver;
	}

	String betterbungee = "http://betterbungeeapi.skydb.de";

	String uuid = "";

	String password = "";

	String session = "";

	public String Version = "1.06";

	public String BungeeCordVersion = "6613aaea95f4894ea19c31e0d564d45fcf43456f";

	long lastfirewallsync = 0;

	long lastupdatecheck = 0;

	int updatecheckfrequency = 0;

	boolean updated = false;

	boolean snapshotupdate = false;

	boolean restartonupdate = false;

	int snapshotupdatecountdown = 10;

	int periplimit = 3;

	boolean blacklistconnectionspam = false;
	
	int blacklistconnectionslimit = 30;

	int globallimit = 100;

	boolean protection = false;

	boolean disablebungeecommands = false;

//	boolean ProxyProtocol = false;

	boolean denyVPNonJoin = false;

	String denyVPNkickmessage = "Kicked by AntiVPN";

	String denyVPNbypasspermission = "antivpn.bypass";

	boolean discordintegration = false;

	private ConcurrentLinkedQueue<String> removewhitelist = new ConcurrentLinkedQueue<String>();

	private ConcurrentLinkedQueue<String> removeblacklist = new ConcurrentLinkedQueue<String>();

	private ConcurrentLinkedQueue<String> addblacklist = new ConcurrentLinkedQueue<String>();

	private ConcurrentLinkedQueue<String> addwhitelist = new ConcurrentLinkedQueue<String>();

	ArrayList<String> pluginlist = new ArrayList<>();

	boolean manuelupdates = false;

	private boolean packetsizelimit = false;

	private int packetsizelimitsize = 8000;

	private boolean firewallsync;

	boolean apiconnection = false;

	private int faviconlimit = 7;

	private boolean devdebugmode = false;

	private Set<String> hostnames = ConcurrentHashMap.newKeySet();

	private boolean hostprotectionnames = false;

	private boolean proxycheckonauth = false;

	private Integer startdenyproxyauthlimit = 2;

	private boolean pingcheck = false;

	private Integer pingcheckonconnectlimit = 20;

	public String discordwebhook = "none";

	public String pathtotemplatejar = "none";

	private boolean allowselfconnect = false;

	private boolean limbomode = false;

	private boolean sendafkstolimbo = false;

	private boolean botchecks = false;

	private boolean preblacklistproxies = false;

	private ConcurrentHashMap<UUID, Long> afk = new ConcurrentHashMap<UUID, Long>();

	private ConcurrentHashMap<UUID, ServerInfo> reconnectserver = new ConcurrentHashMap<UUID, ServerInfo>();

	private ServerInfo limboserver;

	private static BetterBungee instance;

	
	private ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	
	
	public BetterBungee() {
		instance = this;
		
		createConfigs();
		
		threads.execute(() -> {
			
			BungeeCordLauncher.crashed = false;
			
			sleep(1500);
			NotifyManager.Instance = new NotifyManager();
			onStart();
			
			while (BungeeCord.getInstance().isRunning) {
				sleep(3500);
				if (alive()) {
					apiconnection = true;
					if (snapshotupdate) {
						if (update()) {
							if (restartonupdate) {
								ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§eSnapshot§7 Update Found"));
								for (int i = snapshotupdatecountdown; i > 0; i--) {
									sleep(1000);
									ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Restart in §c" + i + "§7 seconds"));
								}
								sleep(1000);
								ProxyServer.getInstance().stop(updatemessage);
							}
						}
					} else if (lastupdatecheck < System.currentTimeMillis() - (1000 * 60 * updatecheckfrequency)) {
						lastupdatecheck = System.currentTimeMillis();
						if (update()) {
							if (restartonupdate) {
								ProxyServer.getInstance().broadcast(
										TextComponent.fromLegacyText(BungeeCord.PREFIX + "§aStable§7 Update Found"));
								for (int i = snapshotupdatecountdown; i > 0; i--) {
									sleep(1000);
									ProxyServer.getInstance().broadcast(TextComponent
											.fromLegacyText(BungeeCord.PREFIX + "§7Restart in §c" + i + "§7 seconds"));
								}
								sleep(1000);
								ProxyServer.getInstance().stop(updatemessage);
							}
						}
					}
					sleep();
				} else {
					apiconnection = false;
					System.out.println("Session Expired");
					sleep(120000);
					login();
				}
			}
		});
	}

	public static final String updatemessage = "§7Restarting §eProxy-Server§7 due to a §aUpdate§7 from §6BetterBungee";

	public void onStart() {
		if (update()) {
			ProxyServer.getInstance().stop(updatemessage);
		}
		login();
	}

	private void addDefault(Configuration conf, String path, String value) {
		if (!conf.contains(path)) {
			conf.set(path, value);
		}
	}

	public void createConfigs() {
		try {
			File file = new File("betterbungeeconfig.yml");

			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Created Config File");
			}
			
			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

			String prefix = "serversettings.prefix";

			String snapshotupdater = "serversettings.snapshotupdater";

			String restartonupdate = "serversettings.restartonupdate";

			String snapshotupdatercountdown = "serversettings.snapshotupdatercountdown";

			String protection = "serversettings.protection";

			String firewallsync = "serversettings.firewallsync";

			String firewall = "serversettings.firewallsync";

			String denyvpns = "serversettings.denyvpnjoins";

			String denyvpnkickmessage = "serversettings.denyvpnkickmessage";

			String denyvpnbypasspermission = "serversettings.denyvpnbypasspermission";

			String disablebungeecommands = "serversettings.disablebungeecommands";

			String globallimit = "serversettings.globalcpslimit";

			String limitperip = "serversettings.limitcpsperip";

			String blacklistconnectionspam = "serversettings.blacklistconnectionspam";

			String blacklistconnectionslimit = "serversettings.blacklistconnectionslimit";

			String faviconlimit = "serversettings.faviconspersecond";

			String hostnameprotection = "serversettings.onlyhostname";

			String hostnames = "serversettings.hostnames";

			String updatecheckfrequencysetting = "serversettings.updatecheckfrequencyinminutes";

			String discordwebhook = "serversettings.discordwebhook";

			String manuelupdates = "serversettings.manuelupdates";

			String pathtotemplatejar = "serversettings.pathtotemplatejar";

			String packetsizelimit = "serversettings.packetsizelimit";

			String packetsizelimitsize = "serversettings.packetsizelimitsize";

			String devdebugmode = "serversettings.devdebugmode";

			String forcewhitelistedips = "serversettings.forcewhitelistedips";

			String proxycheckonauth = "serversettings.denyproxyauths";

			String startdenyproxyauthlimit = "serversettings.startdenyproxyauthlimit";

			String pingcheck = "serversettings.checkifserverlistpinged";

			String pingcheckonconnectlimit = "serversettings.denyifnotpingedlimit";

			String allowselfconnect = "serversettings.allowselfconnect";

			String limbomode = "serversettings.limbomode";

			String sendafkstolimbo = "serversettings.sendafkstolimbo";

			String botchecks = "serversettings.botchecks";

			String preblacklistproxies = "serversettings.preblacklistproxies";

//			String impossibelnamecheck = "serversettings.impossibelnamecheck";
//
//			String whitelistedcharacters = "serversettings.whitelistedcharacters";

			addDefault(config, prefix, "&6BetterBungee &7- &e ");

			addDefault(config, snapshotupdater, "false");

			addDefault(config, restartonupdate, "true");

			addDefault(config, snapshotupdatercountdown, "10");

			addDefault(config, disablebungeecommands, "false");

			addDefault(config, protection, "true");

			addDefault(config, firewallsync, "true");

//			addDefault(config, impossibelnamecheck, "false");
//
//			addDefault(config, whitelistedcharacters, "*");

			addDefault(config, hostnameprotection, "false");

			addDefault(config, hostnames, "play.domain.com,domain.com");

			addDefault(config, denyvpns, "true");

			addDefault(config, denyvpnkickmessage, "&cKicked by AntiVPN");

			addDefault(config, denyvpnbypasspermission, "antivpn.bypass");

			addDefault(config, globallimit, "300");

			addDefault(config, faviconlimit, "7");

			addDefault(config, limitperip, "3");

			addDefault(config, blacklistconnectionspam, "true");
			
			addDefault(config, blacklistconnectionslimit, "30");

			addDefault(config, proxycheckonauth, "false");

			addDefault(config, startdenyproxyauthlimit, "6");

			addDefault(config, pingcheck, "true");

			addDefault(config, pingcheckonconnectlimit, "50");

			addDefault(config, updatecheckfrequencysetting, "15");

			addDefault(config, discordwebhook, "none");

			addDefault(config, manuelupdates, "false");

			addDefault(config, pathtotemplatejar, "none");

			addDefault(config, packetsizelimit, "false");

			addDefault(config, packetsizelimitsize, "8000");

			addDefault(config, devdebugmode, "false");

			addDefault(config, forcewhitelistedips, "127.0.0.2,127.0.0.3");

			addDefault(config, allowselfconnect, "false");

			addDefault(config, limbomode, "false");

			addDefault(config, sendafkstolimbo, "false");

			addDefault(config, botchecks, "false");

			addDefault(config, preblacklistproxies, "true");


			String configuuid = "serverdata.uuid";

			String configkey = "serverdata.key";

			if (!config.contains(configuuid)) {
				config.set(configuuid, UUID.randomUUID().toString());
				config.set(configkey, generatepw());
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
				System.out.println("Creating Config");
				while (!register(config.getString(configuuid), config.getString(configkey))) {
					sleep();
					System.out.println("Recreating UUID and Password");
					config.set(configuuid, UUID.randomUUID().toString());
					config.set(configkey, generatepw());
				}
			}
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);

			this.uuid = config.getString(configuuid);

			this.password = config.getString(configkey);

			this.snapshotupdate = config.getString(snapshotupdater).equalsIgnoreCase("true");

			this.restartonupdate = config.getString(restartonupdate).equalsIgnoreCase("true");

			this.snapshotupdatecountdown = Integer.valueOf(config.getString(snapshotupdatercountdown));

			this.protection = config.getString(protection).equalsIgnoreCase("true");

			this.firewallsync = config.getString(firewallsync).equalsIgnoreCase("true");

			this.denyVPNonJoin = config.getString(denyvpns).equalsIgnoreCase("true");

			this.denyVPNkickmessage = config.getString(denyvpnkickmessage);

			this.denyVPNbypasspermission = config.getString(denyvpnbypasspermission);

			this.globallimit = Integer.valueOf(config.getString(globallimit));

			this.periplimit = Integer.valueOf(config.getString(limitperip));

			this.blacklistconnectionspam = config.getString(blacklistconnectionspam).equalsIgnoreCase("true");

			this.blacklistconnectionslimit = Integer.valueOf(config.getString(blacklistconnectionslimit));

			this.faviconlimit = Integer.valueOf(config.getString(faviconlimit));

			this.disablebungeecommands = config.getString(disablebungeecommands).equalsIgnoreCase("true");

			this.updatecheckfrequency = Integer.valueOf(config.getString(limitperip));

			this.proxycheckonauth = config.getString(proxycheckonauth).equalsIgnoreCase("true");

			this.startdenyproxyauthlimit = Integer.valueOf(config.getString(startdenyproxyauthlimit));

			this.discordwebhook = config.getString(discordwebhook);

			this.manuelupdates = config.getString(manuelupdates).equalsIgnoreCase("true");

			this.packetsizelimit = config.getString(packetsizelimit).equalsIgnoreCase("true");

			this.packetsizelimitsize = Integer.valueOf(config.getString(packetsizelimitsize));

			this.devdebugmode = config.getString(devdebugmode).equalsIgnoreCase("true");

			this.hostprotectionnames = config.getString(hostnameprotection).equalsIgnoreCase("true");

			this.hostnames.addAll(Arrays.asList(config.getString(hostnames).toLowerCase(Locale.ROOT).split(",")));

			Blacklist.getInstance().getForcewhitelistedips().addAll(Arrays.asList(config.getString(forcewhitelistedips).split(",")));

			this.pingcheck = config.getString(pingcheck).equalsIgnoreCase("true");

			this.pingcheckonconnectlimit = Integer.valueOf(config.getString(pingcheckonconnectlimit));

			this.pathtotemplatejar = config.getString(pathtotemplatejar);

			this.allowselfconnect = config.getString(allowselfconnect).equalsIgnoreCase("true");

			this.limbomode = config.getString(limbomode).equalsIgnoreCase("true");

			this.sendafkstolimbo = config.getString(sendafkstolimbo).equalsIgnoreCase("true");

			this.botchecks = config.getString(botchecks).equalsIgnoreCase("true");

			this.preblacklistproxies = config.getString(preblacklistproxies).equalsIgnoreCase("true");

			BungeeCord.PREFIX = config.getString(prefix).replaceAll("&", "§");

			Blacklist.getInstance().setProtection(this.protection);

			Blacklist.getInstance().setGlobalratelimit(this.globallimit);

			Blacklist.getInstance().setPerIPratelimit(this.periplimit);

			Blacklist.getInstance().setBlacklistonconnectionlimit(this.blacklistconnectionspam);
			
			Blacklist.getInstance().setMaxcpsperip(this.blacklistconnectionslimit);

			Blacklist.getInstance().setGlobalfaviconlimit(this.faviconlimit);

			if (snapshotupdate) {
				Version = String.valueOf(
						new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI())
								.length());
			}

//			if (this.discordintegration) {
//				NotifyManager.getInstance().setDiscord(this.discordintegration);
//				new Thread(() -> {
//					sleep(5500);
//					while (BungeeCord.getInstance().isRunning) {
//						if (apiconnection) {
//							if (NotifyManager.getInstance().getDiscordmessages().size() > 0) {
//
//								Set<String> set = ConcurrentHashMap.newKeySet();
//								
//								if (NotifyManager.getInstance().getDiscordmessages().size() > 15) {
//									for (int i = 0; i < 15; i++) {
//										set.add(NotifyManager.getInstance().getDiscordmessages().poll());
//									}
//								} else {
//									set.addAll(NotifyManager.getInstance().getDiscordmessages());
//									NotifyManager.getInstance().deleteDiscordmessages();
//								}
//								discord(set);
//							}
//						}
//						sleep(1000);
//					}
//				}).start();
//			}

	        
			if (this.firewallsync) {

				threads.execute(() -> {

					sleep(5500);

					while (!apiconnection) {
						sleep(3000);
					}

					getAPIBlacklist();

					getAPIWhitelist();

					while (BungeeCord.getInstance().isRunning) {
						if (apiconnection) {
							if (lastfirewallsync < System.currentTimeMillis() - (1000 * 2)) {
								lastfirewallsync = System.currentTimeMillis();
								syncfirewallwithrestapi();
								sleep(500);
							}

						}
						sleep(3000);
					}
				});
			}
			
			threads.execute(() -> {
				while (ProxyServer.getInstance() == null) {
					sleep(500);
				}
				limboserver = ProxyServer.getInstance().constructServerInfo("betterbungee-limbo", new InetSocketAddress("51.195.101.127", 25565), "", false);
			});

			if (this.sendafkstolimbo) {
				threads.execute(() -> {
					sleep(5500);
					while (BungeeCord.getInstance().isRunning) {
						sleep(5000);
						for (Entry<UUID, Long> entry : afk.entrySet()) {
							UUID uuid = entry.getKey();
							ProxiedPlayer player = null;
							for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
								if (all.equals(uuid)) {
									player = all;
								}
							}
							if (player == null) {
								afk.remove(entry.getKey());
								if (reconnectserver.containsKey(uuid)) {
									reconnectserver.remove(uuid);
								}
							} else {

								if (entry.getValue().longValue() < System.currentTimeMillis() - 300000) {
									if (!player.getServer().getInfo().getName().equals(limboserver.getName())) {
										reconnectserver.put(uuid, player.getServer().getInfo());
										player.connect(BetterBungee.getInstance().getLimboserver());
									}
								}

							}
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getAPIBlacklist() {
		RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/getblacklist?session=" + session);
		if (!response.getFailed()) {
			String message = response.getText();
			if (message.contains(" ")) {
				return;
			}
			if (message.contains(",")) {
				for (String msg : message.split(",")) {
					Blacklist.getInstance().getBlacklist().add(msg);
				}
			} else {
				Blacklist.getInstance().getBlacklist().add(message);
			}
		}
	}

	private void getAPIWhitelist() {
		RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/getwhitelist?session=" + session);

		if (!response.getFailed()) {
			String message = response.getText();
			if (message.contains(" ")) {
				return;
			}
			if (message.contains(",")) {
				for (String msg : message.split(",")) {
					Blacklist.getInstance().getWhitelist().add(msg);
				}
			} else {
				Blacklist.getInstance().getWhitelist().add(message);
			}
		}
	}

	public void syncfirewallwithrestapi() {
		for (int i2 = 0; i2 < 2; i2++) {
			if (addwhitelist.size() > 0) {
				sleep(500);
				i2 -= 1;
				Set<String> set = ConcurrentHashMap.newKeySet();
				if (addwhitelist.size() > 20) {
					for (int i = 0; i < 20; i++) {
						set.add(addwhitelist.poll());
					}
				} else {
					set.addAll(addwhitelist);
					addwhitelist.clear();
				}
				addwhitelist(set);
			} else if (addblacklist.size() > 0) {
				sleep(500);
				i2 -= 1;
				Set<String> set = ConcurrentHashMap.newKeySet();
				if (addblacklist.size() > 20) {
					for (int i = 0; i < 20; i++) {
						set.add(addblacklist.poll());
					}
				} else {
					set.addAll(addblacklist);
					addblacklist.clear();
				}
				addblacklist(set);
			} else if (removewhitelist.size() > 0) {
				sleep(500);
				i2 -= 1;
				Set<String> set = ConcurrentHashMap.newKeySet();
				if (removewhitelist.size() > 20) {
					for (int i = 0; i < 20; i++) {
						set.add(removewhitelist.poll());
					}
				} else {
					set.addAll(removewhitelist);
					removewhitelist.clear();
				}
				removewhitelist(set);
			} else if (removeblacklist.size() > 0) {
				sleep(500);
				i2 -= 1;
				Set<String> set = ConcurrentHashMap.newKeySet();
				if (removeblacklist.size() > 20) {
					for (int i = 0; i < 20; i++) {
						set.add(removeblacklist.poll());
					}
				} else {
					set.addAll(removeblacklist);
					removeblacklist.clear();
				}
				removeblacklist(set);
			}
		}
	}

	private static String generatepw() {
		String stg = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123457890";
		String pw = "";
		int i = 32;
		while (i-- > 0) {
			pw += stg.charAt((int) (stg.length() * Math.random()));
		}
		return pw;
	}

	private void sleep() {
		sleep(15000);
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean update() {
		if (this.manuelupdates && !snapshotupdate) {
			return false;
		}
		if (snapshotupdate) {
			RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/update");
			if (!response.getFailed()) {
				try {
					String newestnapshotid = response.getText().replaceAll("\n", "").split(":")[1];
					if (!newestnapshotid.equals(String.valueOf(
							new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI())
									.length()))) {
						return updatefromlink(betterbungee + "/downloadsnapshot");
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("API Timed Out");
			}
		} else if (!updated) {
			lastupdatecheck = System.currentTimeMillis();
			System.out.println("Checking for Updates");
			RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/update?version=" + Version);
			if (!response.getFailed()) {
				try {
					String newestupdateid = response.getText().replaceAll("\n", "").split(":")[1];
					if (!newestupdateid.equals(String.valueOf(
							new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI())
									.length()))) {
						return updatefromlink(betterbungee + "/downloadupdate");
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("API Timed Out");
			}
		}
		return false;
	}

	private boolean updatefromlink(String link) {
		if (download(link)) {
			try {
				if (pathtotemplatejar.equalsIgnoreCase("none")) {
					new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()).delete();
					new File("UpdatedBungeeCord.jar").renameTo(
							new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
				} else {
					new File(pathtotemplatejar).delete();
					new File("UpdatedBungeeCord.jar").renameTo(new File(pathtotemplatejar));
				}
				return true;
			} catch (URISyntaxException e) {
			}
			System.out.println("Updated BungeeCord");
			updated = true;
		}
		return false;
	}

	private boolean download(String link) {
		try {
			FileUtils.copyURLToFile(new URL(link), new File("UpdatedBungeeCord.jar"), 30000, 30000);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean login() {
		System.out.println("Login to API");
		RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/login?uuid=" + uuid + "&password=" + password);
		if (!response.getFailed()) {
			if (!response.getText().contains("Invalid")) {
				session = response.getText();
				return true;
			}
		} else {
			System.out.println("API Timed Out");
		}
		System.out.println(response.getText());
		return false;
	}

	private boolean register(String uuid, String password) {
		System.out.println("Register a new account on BetterBungeeAPI");
		RestAPIResponse response = RestAPI.getInstance()
				.get(betterbungee + "/register?uuid=" + uuid + "&password=" + password);
		if (!response.getFailed()) {
			if (response.getText().contains("Succeed")) {
				this.uuid = uuid;
				this.password = password;
				return true;
			}
			System.out.println(response.getText());
		} else {
			System.out.println("API Timed Out");
		}
		return false;
	}

	private boolean discord(Set<String> set) {
		if (session == null) {
			return false;
		}

		String messages = "";

		for (String message : set) {
			if (message != null) {
				messages += stringtobase64(message) + ",";
			}
		}

		RestAPIResponse response = RestAPI.getInstance()
				.get(betterbungee + "/senddiscord?session=" + session + "&discordmessages=" + messages);
		return !response.getFailed();
	}

	private boolean addblacklist(Set<String> blacklist) {
		if (session == null) {
			return false;
		}

		String messages = "";

		for (String message : blacklist) {
			if (message != null) {
				messages += message + ",";
			}
		}

		RestAPIResponse response = RestAPI.getInstance()
				.get(betterbungee + "/addblacklist?session=" + session + "&blacklist=" + messages);
		return !response.getFailed();
	}

	private boolean removeblacklist(Set<String> removeblacklist2) {
		if (session == null) {
			return false;
		}

		String messages = "";

		for (String message : removeblacklist2) {
			if (message != null) {
				messages += message + ",";
			}
		}

		RestAPIResponse response = RestAPI.getInstance()
				.get(betterbungee + "/removeblacklist?session=" + session + "&blacklist=" + messages);
		return !response.getFailed();
	}

	private boolean addwhitelist(Set<String> addwhitelist2) {
		if (session == null) {
			return false;
		}

		String messages = "";

		for (String message : addwhitelist2) {
			if (message != null) {
				messages += message + ",";
			}
		}

		RestAPIResponse response = RestAPI.getInstance()
				.get(betterbungee + "/addwhitelist?session=" + session + "&whitelist=" + messages);
		return !response.getFailed();
	}

	private boolean removewhitelist(Set<String> removewhitelist2) {
		if (session == null) {
			return false;
		}

		String messages = "";

		for (String message : removewhitelist2) {
			if (message != null) {
				messages += message + ",";
			}
		}

		RestAPIResponse response = RestAPI.getInstance()
				.get(betterbungee + "/removewhitelist?session=" + session + "&whitelist=" + messages);
		return !response.getFailed();
	}

	private boolean alive() {
		if (session == null) {
			return false;
		}

		RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/alive?session=" + session);

		if (!response.getFailed()) {
			String text = response.getText();
			if (text.contains("Alive")) {
				return true;
			}
			System.out.println(response.getText());

		} else {
			System.out.println("API Timed Out");
		}
		return false;
	}

	private String stringtobase64(String stg) {
		return new String(Base64.getEncoder().encode(stg.getBytes()));
	}

	private String base64tostring(String stg) {
		return new String(Base64.getDecoder().decode(stg.getBytes()));
	}

	public boolean isDevdebugmode() {
		return devdebugmode;
	}

	public void setDevdebugmode(boolean devdebugmode) {
		this.devdebugmode = devdebugmode;
	}

	public Set<String> getHostnames() {
		return hostnames;
	}

	public void setHostnames(Set<String> hostnames) {
		this.hostnames = hostnames;
	}

	public boolean isHostprotectionnames() {
		return hostprotectionnames;
	}

	public void setHostprotectionnames(boolean hostprotectionnames) {
		this.hostprotectionnames = hostprotectionnames;
	}

	public static BetterBungee getInstance() {
		return instance;
	}

	public Integer getStartdenyproxyauthlimit() {
		return startdenyproxyauthlimit;
	}

	public void setStartdenyproxyauthlimit(Integer startdenyproxyauthlimit) {
		this.startdenyproxyauthlimit = startdenyproxyauthlimit;
	}

	public boolean isProxycheckonauth() {
		return proxycheckonauth;
	}

	public void setProxycheckonauth(boolean proxycheckonauth) {
		this.proxycheckonauth = proxycheckonauth;
	}

	public boolean isPingcheck() {
		return pingcheck;
	}

	public void setPingcheck(boolean pingcheck) {
		this.pingcheck = pingcheck;
	}

	public Integer getPingcheckonconnectlimit() {
		return pingcheckonconnectlimit;
	}

	public void setPingcheckonconnectlimit(Integer pingcheckonconnectlimit) {
		this.pingcheckonconnectlimit = pingcheckonconnectlimit;
	}

	public String getBungeeCordVersion() {
		return BungeeCordVersion;
	}

	public boolean isAllowSelfConnect() {
		return allowselfconnect;
	}
}
