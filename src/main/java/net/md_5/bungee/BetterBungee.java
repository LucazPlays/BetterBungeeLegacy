package net.md_5.bungee;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import lombok.Getter;
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

	public String Version = "0.4";

	long lastupdatecheck = 0;

	@Getter
	boolean updated = false;

	@Getter
	boolean snapshotupdate = false;

	@Getter
	int periplimit = 2;

	@Getter
	int globallimit = 100;

	@Getter
	boolean protection = false;

	public BetterBungee() {
		Thread betterbungeethread = new Thread(() -> {
			BungeeCordLauncher.crashed = false;
			sleep(1500);
			createConfigs();
			onStart();
			while (BungeeCord.getInstance().isRunning) {
				if (alive()) {
					if (snapshotupdate) {
						if (update()) {
							ProxyServer.getInstance().broadcast(
									TextComponent.fromLegacyText(BungeeCord.PREFIX + "§eSnapshot§7 Update Found"));
							for (int i = 10; i > 0; i--) {
								sleep(1000);
								ProxyServer.getInstance().broadcast(TextComponent
										.fromLegacyText(BungeeCord.PREFIX + "§7Restart in §c" + i + "§7 seconds"));
							}
							sleep(1000);
							ProxyServer.getInstance().stop("§6Updated BetterCord");
						}
					} else if (lastupdatecheck < System.currentTimeMillis() - 1000 * 60 * 60 * 2) {
						update();
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
			ProxyServer.getInstance().stop("§D6Updated BetterCord");
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

			String protection = "serversettings.protection";

			String globallimit = "serversettings.globalcpslimit";

			String limitperip = "serversettings.limitcpsperip";

			addDefault(config, prefix, "&6BetterBungee &7- &e ");

			addDefault(config, snapshotupdater, "true");

			addDefault(config, protection, "false");

			addDefault(config, globallimit, "100");

			addDefault(config, limitperip, "3");

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
			this.protection = config.getString(protection).equalsIgnoreCase("true");
			this.globallimit = Integer.valueOf(config.getString(globallimit));
			this.periplimit = Integer.valueOf(config.getString(limitperip));

			BungeeCord.PREFIX = config.getString(prefix).replaceAll("&", "§");

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
					if (!newestnapshotid.equals(String.valueOf(
							new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI())
									.length()))) {
						return updatefromlink(betterbungee + "/downloadsnapshot");
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		} else if (!updated) {
			lastupdatecheck = System.currentTimeMillis();
			System.out.println("Checking for Updates");
			RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/update?version=" + Version);
			if (!response.getFailed()) {
				if (response.getText().contains("Update Available")) {
					System.out.println("Updated Available");
					return updatefromlink(betterbungee + "/downloadupdate");
				}
			}
		}
		return false;
	}

	private boolean updatefromlink(String link) {
		try {
			FileUtils.copyURLToFile(new URL(link), new File("UpdatedBungeeCord.jar"), 30000, 30000);
			try {
				new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()).delete();
				new File("UpdatedBungeeCord.jar").renameTo(
						new File(BetterBungee.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
				return true;
			} catch (URISyntaxException e) {
			}
			System.out.println("Updated BungeeCord");
			updated = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean login() {
		System.out.println("Login to API");
		RestAPIResponse response = RestAPI.getInstance()
				.info(betterbungee + "/login?uuid=" + uuid + "&password=" + password);
		if (!response.getFailed()) {
			if (!response.getText().contains("Invalid")) {
				session = response.getText();
				return true;
			}
		}
		System.out.println(response.getText());
		return false;
	}

	private boolean register(String uuid, String password) {
		System.out.println("Register a new account on BetterBungeeAPI");
		RestAPIResponse response = RestAPI.getInstance()
				.info(betterbungee + "/register?uuid=" + uuid + "&password=" + password);
		if (!response.getFailed()) {
			if (response.getText().contains("Succeed")) {
				this.uuid = uuid;
				this.password = password;
				return true;
			}
			System.out.println(response.getText());
		}
		return false;
	}

	private boolean alive() {
		if (session == null)  {
			return false;
		}
		RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/alive?session=" + session);
		if (!response.getFailed()) {
			String text = response.getText();
			if (text.contains("Alive")) {
				return true;
			}
			System.out.println(response.getText());
		}
		return false;
	}
}
