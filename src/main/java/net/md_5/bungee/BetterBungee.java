package net.md_5.bungee;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import net.md_5.bungee.api.RestAPI;
import net.md_5.bungee.api.RestAPIResponse;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BetterBungee {

	String betterbungee = "http://betterbungee.skydb.de";

	String uuid = "";

	String password = "";

	String session = "";

	public BetterBungee() {
		Thread betterbungeethread = new Thread(() -> {
			sleep(1500);
			createConfigs();
			onStart();
			while (BungeeCord.getInstance().isRunning) {
				if (alive()) {
					sleep();
				} else {
					return;
				}
			}
		});
		betterbungeethread.setPriority(Thread.MIN_PRIORITY);
		betterbungeethread.start();
	}

	public void createConfigs() {
		try {
			File file = new File("betterbungeeconfig.yml");
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Created Config File");
			}
			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			if (!config.contains("uuid")) {
				config.set("uuid", UUID.randomUUID().toString());
				config.set("key", generatepw());
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
				System.out.println("Creating Config");
				while (!register(config.getString("uuid"), config.getString("key"))) {
					sleep();
					System.out.println("Recreating UUID and Password");
					config.set("uuid", UUID.randomUUID().toString());
					config.set("key", generatepw());
				}
			}
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
			this.uuid = config.getString("uuid");
			this.password = config.getString("key");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String generatepw() {
		String stg = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123457890";
		String pw = "";
		int i = 32;
		while (i-- > 0) {
			pw += stg.charAt((int) (stg.length()*Math.random()));
		}
		return pw;
	}

	private void sleep() {
		try {
			Thread.sleep(12500);
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

	String Version = "0.2";

	public void onStart() {
		System.out.println("Checking for Updates");
		RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/update?version=" + Version);
		if (!response.getFailed()) {
			if (response.getText().contains("Update Available")) {
				System.out.println("Updated Available");
				try {
					FileUtils.copyURLToFile(new URL(betterbungee + "/downloadupdate"), new File("BungeeCord.jar"),
							30000, 30000);
					System.out.println("Updated BungeeCord");
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		login();
	}

	private boolean login() {
		System.out.println("Login to API");
		RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/login?uuid=" + uuid + "&password=" + password);
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
		System.out.println("Register on API");
		RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/register?uuid=" + uuid + "&password=" + password);
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
