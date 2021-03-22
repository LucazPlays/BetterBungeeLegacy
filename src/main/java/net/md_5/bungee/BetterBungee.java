package net.md_5.bungee;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.RestAPI;
import net.md_5.bungee.api.RestAPIResponse;
import net.md_5.bungee.api.config.ConfigurationAdapter;
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
			File file = new File("BetterBungee");
			file.mkdirs();
			file = new File("BetterBungee/data.yml");
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Created Config File");
			}
			

			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			if (config.getString("accountdata.uuid") == null) {
				config.set("accountdata.uuid", UUID.randomUUID().toString());
				config.set("accountdata.password", DatatypeConverter.parseAnySimpleType(UUID.randomUUID().toString()));
				System.out.println("Creating Config");
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
				while (!register(config.getString("accountdata.uuid"), config.getString("accountdata.password"))) {
					sleep();
					System.out.println("Recreating UUID and Password");
					config.set("accountdata.uuid", UUID.randomUUID().toString());
					config.set("accountdata.password", DatatypeConverter.parseAnySimpleType(UUID.randomUUID().toString()));
					ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
				}
			}
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
			System.out.println("Save Config File");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void sleep() {
		try {
			Thread.sleep(12500);
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
					FileUtils.copyURLToFile(new URL(betterbungee + "/downloadupdate"), new File("BungeeCordUpdate.jar"),
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

	private void login() {
		System.out.println("Login to API");
		RestAPIResponse response = RestAPI.getInstance()
				.info(betterbungee + "/login?uuid=" + uuid + "?password=" + password);
		if (!response.getFailed()) {
			session = response.getText();
		}
	}

	private boolean register(String uuid, String password) {
		System.out.println("Register on API");
		RestAPIResponse response = RestAPI.getInstance().info(betterbungee + "/register?uuid=" + uuid + "?password=" + password);
		if (!response.getFailed()) {
			if (response.getText().contains("Succeed")) {
				return true;
			}
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
		}
		return false;
	}
}
