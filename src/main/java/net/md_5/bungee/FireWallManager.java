package net.md_5.bungee;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import net.md_5.bungee.api.RestAPI;
import net.md_5.bungee.api.RestAPIResponse;

public class FireWallManager {

	static String betterbungee = "http://betterbungeeapi.skydb.de";

	public static String uuid = "";

	public static String key = "";

	public static String session = "";

	public static boolean syncing = false;

	public static Set<String> blacklist = ConcurrentHashMap.newKeySet();

	public static Set<String> whitelist = ConcurrentHashMap.newKeySet();

	public static Runtime run = Runtime.getRuntime();

	public static ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newCachedThreadPool();

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("java -jar "
					+ new File(FireWallManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName()
					+ " <uuid> <key>");
			return;
		}

		uuid = args[0];

		key = args[1];

		try {
			run.exec("sudo apt install ipset iptables sudo -y");

			sleep();
			
			run.exec("sudo ipset destroy whitelist");
			sleep(200);
			run.exec("sudo ipset flush whitelist");
			sleep(200);
			run.exec("sudo ipset create whitelist nethash");
			sleep(200);

			run.exec("sudo ipset destroy blacklist");
			sleep(200);
			run.exec("sudo ipset flush blacklist");
			sleep(200);
			run.exec("sudo ipset create blacklist nethash");

			sleep(200);
			run.exec("sudo iptables -A INPUT -m set --match-set whitelist src -j ACCEPT");
			sleep(200);
			run.exec("sudo iptables -A FORWARD -m set --match-set whitelist src -j ACCEPT");
			
			sleep(200);
			run.exec("sudo iptables -A INPUT -m set --match-set blacklist src -j DROP");
			sleep(200);
			run.exec("sudo iptables -A FORWARD -m set --match-set blacklist src -j DROP");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		run.addShutdownHook(new Thread(() -> {
			try {
				run.exec("sudo iptables -D INPUT -m set --match-set blacklist src -j DROP");
				sleep(200);
				run.exec("sudo iptables -D FORWARD -m set --match-set blacklist src -j DROP");
				sleep(200);
				run.exec("sudo iptables -D INPUT -m set --match-set whitelist src -j DROP");
				sleep(200);
				run.exec("sudo iptables -D FORWARD -m set --match-set whitelist src -j DROP");
				sleep(200);
				run.exec("sudo ipset destroy blacklist");
				sleep(200);
				run.exec("sudo ipset destroy whitelist");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		while (!login()) {
			System.out.println("Login Failed");

			sleep(60000);
			
			System.out.println("Reconnecting in 60Seconds");
		}

		System.out.println("Login Success");
		while (true) {
			sleep(10000);
			if (alive()) {
				if (!syncing) {
					threads.execute(() -> {
						syncing = true;
						getAPIBlacklist();
						getAPIWhitelist();
						syncing = false;
					});
				}
			} else {
//				System.out.println("Session Expired");
//				System.out.println("Reconnecting in 120Seconds");
				sleep(60000);
				login();
			}
		}

	}

	public static boolean login() {
		System.out.println("Login to API");
		RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/login?uuid=" + uuid + "&password=" + key);
		if (!response.getFailed()) {
			if (!response.getText().contains("Invalid")) {
				session = response.getText();
				return true;
			} else {
				System.out.println("wrong uuid or key");
			}
		} else {
			System.out.println("API Timed Out");
		}
		System.out.println(response.getText());
		return false;
	}

	public static boolean alive() {
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

	public static void getAPIBlacklist() {
		RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/getblacklist?session=" + session);

		if (!response.getFailed()) {
			String message = response.getText();
			if (message.contains(" ")) {
				return;
			}
			Set<String> list = ConcurrentHashMap.newKeySet();
			if (message.contains(",")) {
				list.addAll(Arrays.asList(message.split(",")));
			} else {
				list.add(message);
			}

			for (String ip : list) {
				if (!blacklist.contains(ip)) {
					blacklist.add(ip);
					try {
						run.exec("sudo ipset add blacklist " + ip);
//						System.out.println("Added " + ip + " to blacklist");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			for (String ip : blacklist) {
				if (!list.contains(ip)) {
					blacklist.remove(ip);
					try {
						run.exec("sudo ipset del blacklist " + ip);
//						System.out.println("Removed " + ip + " from blacklist");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public static void getAPIWhitelist() {
		RestAPIResponse response = RestAPI.getInstance().get(betterbungee + "/getwhitelist?session=" + session);

		if (!response.getFailed()) {
			String message = response.getText();
			if (message.contains(" ")) {
				return;
			}
			Set<String> list = ConcurrentHashMap.newKeySet();
			if (message.contains(",")) {
				list.addAll(Arrays.asList(message.split(",")));
			} else {
				list.add(message);
			}

			for (String ip : list) {
				if (!whitelist.contains(ip)) {
					whitelist.add(ip);
					try {
						run.exec("sudo ipset add whitelist " + ip);
//						System.out.println("Added " + ip + " to whitelist");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			for (String ip : whitelist) {
				if (!list.contains(ip)) {
					whitelist.remove(ip);
					try {
						run.exec("sudo ipset del whitelist " + ip);
//						System.out.println("Removed " + ip + " from whitelist");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	public static void sleep() {
		sleep(7500);
	}

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
