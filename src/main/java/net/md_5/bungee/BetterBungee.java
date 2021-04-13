package net.md_5.bungee;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.RestAPI;
import net.md_5.bungee.api.RestAPIResponse;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BetterBungee {

	String betterbungee = "http://betterbungee.skydb.de";

	String uuid = "";

	String password = "";

	String session = "";

	public String Version = "0.82";

	long lastupdatecheck = 0;

	int updatecheckfrequency = 0;

	@Getter
	boolean updated = false;

	@Getter
	boolean snapshotupdate = false;

	@Getter
	boolean restartonupdate = false;

	@Getter
	int snapshotupdatecountdown = 10;

	@Getter
	int periplimit = 2;

	@Getter
	int globallimit = 100;

	@Getter
	boolean protection = false;
	

	@Getter
	boolean disablebungeecommands = false;
	

	@Getter
	@Setter
	boolean ProxyProtocol = false;

	@Getter
	@Setter
	boolean denyVPNonJoin = false;


	@Getter
	@Setter
	String denyVPNkickmessage = "Kicked by AntiVPN";

	@Getter
	@Setter
	String denyVPNbypasspermission = "antivpn.bypass";



	@Getter
	ArrayList<DownloadablePlugin> pluginlist = new ArrayList<>();
	
	
	public BetterBungee() {
		createConfigs();
		Thread betterbungeethread = new Thread(() -> {
			BungeeCordLauncher.crashed = false;
			sleep(1500);
			NotifyManager.Instance = new NotifyManager();
			onStart();
			while (BungeeCord.getInstance().isRunning) {
				if (alive()) {
					if (snapshotupdate) {
						if (update()) {
							if (restartonupdate) {
								ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§eSnapshot§7 Update Found"));
								for (int i = snapshotupdatecountdown; i > 0; i--) {
									sleep(1000);
									ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Restart in §c" + i + "§7 seconds"));
								}
								sleep(1000);
								ProxyServer.getInstance().stop("§6Updated BetterCord");
							}
						}
					} else if (lastupdatecheck < System.currentTimeMillis() - (1000 * 60 * updatecheckfrequency)) {
						if (update()) {
							if (restartonupdate) {
								ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§aStable§7 Update Found"));
								for (int i = snapshotupdatecountdown; i > 0; i--) {
									sleep(1000);
									ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(BungeeCord.PREFIX + "§7Restart in §c" + i + "§7 seconds"));
								}
								sleep(1000);
								ProxyServer.getInstance().stop("§aUpdated BetterCord");
							}
						}
					}
					sleep();
				} else {
					System.out.println("Session Expired");
					sleep(120000);
					login();
				}
			}
		});
		betterbungeethread.setPriority(Thread.MIN_PRIORITY);
		betterbungeethread.start();
	}

	public void onStart() {
		if (update()) {
			ProxyServer.getInstance().stop("§6Updated BetterCord");
		}
		login();
	}

	private void addDefault(Configuration conf, String test, String test1) {
		if (!conf.contains(test)) {
			conf.set(test, test1);
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

			String snapshotupdatercountdown= "serversettings.snapshotupdatercountdown";

			String protection = "serversettings.protection";

			String denyvpns = "serversettings.denyvpnjoins";
			
			String denyvpnkickmessage = "serversettings.denyvpnkickmessage";
			
			String denyvpnbypasspermission = "serversettings.denyvpnbypasspermission";

			String disablebungeecommands = "serversettings.disablebungeecommands";

			String globallimit = "serversettings.globalcpslimit";

			String limitperip = "serversettings.limitcpsperip";

			String updatecheckfrequencysetting = "serversettings.updatecheckfrequencyinminutes";

			addDefault(config, prefix, "&6BetterBungee &7- &e ");

			addDefault(config, snapshotupdater, "false");

			addDefault(config, restartonupdate, "true");

			addDefault(config, snapshotupdatercountdown, "10");

			addDefault(config, disablebungeecommands, "false");

			addDefault(config, protection, "false");

			addDefault(config, denyvpns, "false");

			addDefault(config, denyvpnkickmessage, "Kicked by AntiVPN");

			addDefault(config, denyvpnbypasspermission, "antivpn.bypass");
			
			addDefault(config, globallimit, "100");

			addDefault(config, limitperip, "3");

			addDefault(config, updatecheckfrequencysetting, "15");

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

			this.denyVPNonJoin = config.getString(denyvpns).equalsIgnoreCase("true");

			this.denyVPNkickmessage = config.getString(denyvpnkickmessage);
			
			this.denyVPNbypasspermission = config.getString(denyvpnbypasspermission);
			
			this.globallimit = Integer.valueOf(config.getString(globallimit));
			
			this.periplimit = Integer.valueOf(config.getString(limitperip));

			this.disablebungeecommands = config.getString(disablebungeecommands).equalsIgnoreCase("true");
			
			this.updatecheckfrequency = Integer.valueOf(config.getString(limitperip));
			
			
			BungeeCord.PREFIX = config.getString(prefix).replaceAll("&", "§");

			Blacklist.getInstance().setProtection(this.protection);
			
			if (snapshotupdate) {
				Version = String.valueOf(new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()).length());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
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
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean update() {
		if (snapshotupdate) {
			RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/update");
			if (!response.getFailed()) {
				try {
					String newestnapshotid = response.getText().replaceAll("\n", "").split(":")[1];
//					System.out.println("Newest-Snapshot ID: "+newestnapshotid);
//					System.out.println("Snapshot ID: "+String.valueOf(new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()).length()));
					if (!newestnapshotid.equals(String.valueOf(new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()).length()))) {
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
			RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/update?version=" + Version);
			if (!response.getFailed()) {
				try {
					String newestupdateid = response.getText().replaceAll("\n", "").split(":")[1];
					if (!newestupdateid.equals(String.valueOf(new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()).length()))) {
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
				new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()).delete();
				new File("UpdatedBungeeCord.jar").renameTo(new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
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
		RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/login?uuid=" + uuid + "&password=" + password);
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
		RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/register?uuid=" + uuid + "&password=" + password);
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

	private boolean alive() {
		if (session == null) {
			return false;
		}
					
		RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/alive?session=" + session);
		
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
}
