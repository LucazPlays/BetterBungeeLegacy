package net.md_5.bungee;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.RestAPI;
import net.md_5.bungee.api.RestAPIResponse;

public class FireWallManager {

	public static ConcurrentLinkedQueue<String> addblacklist = new ConcurrentLinkedQueue<String>();

	public static ConcurrentLinkedQueue<String> addwhitelist = new ConcurrentLinkedQueue<String>();

	public static ConcurrentLinkedQueue<String> remblacklist = new ConcurrentLinkedQueue<String>();

	public static ConcurrentLinkedQueue<String> remwhitelist = new ConcurrentLinkedQueue<String>();

	public static Runtime run = Runtime.getRuntime();

	public static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static void setup() {
		scheduler.execute(() -> {
			try {
				run.exec("apt install ipset iptables sudo -y");

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
				run.exec("sudo iptables -t mangle -I PREROUTING -m set --match-set blacklist src -j DROP");
				sleep(200);
				run.exec("sudo iptables -t mangle -I PREROUTING -m set --match-set blacklist src -j DROP");
				sleep(200);
				run.exec("sudo iptables -t mangle -I PREROUTING -m set --match-set whitelist src -j ACCEPT");
				sleep(200);
				run.exec("sudo iptables -t mangle -I PREROUTING -m set --match-set whitelist src -j ACCEPT");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			run.addShutdownHook(new Thread(() -> {
				try {
					run.exec("sudo iptables -D INPUT -m set --match-set blacklist src -j DROP");
					sleep(200);
					run.exec("sudo iptables -D PREROUTING -m set --match-set blacklist src -j DROP");
					sleep(200);
					run.exec("sudo iptables -D INPUT -m set --match-set whitelist src -j ACCEPT");
					sleep(200);
					run.exec("sudo iptables -D PREROUTING -m set --match-set whitelist src -j ACCEPT");
					sleep(200);
					run.exec("sudo ipset destroy blacklist");
					sleep(200);
					run.exec("sudo ipset destroy whitelist");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}));

			System.out.println("Login Success");
//		while (true) {
//			sleep(5000);
//			getAPIBlacklist();
//			getAPIWhitelist();
//		}

			scheduler.scheduleAtFixedRate(() -> {
				String ip = addblacklist.poll();
				if (ip != null) {
					exec("sudo ipset add blacklist " + ip);
				}
			}, 1000, 30, TimeUnit.MILLISECONDS);

			scheduler.scheduleAtFixedRate(() -> {
				String ip = addwhitelist.poll();
				if (ip != null) {
					exec("sudo ipset add whitelist " + ip);
				}
			}, 1000, 30, TimeUnit.MILLISECONDS);

			scheduler.scheduleAtFixedRate(() -> {
				String ip = remblacklist.poll();
				if (ip != null) {
					exec("sudo ipset del blacklist " + ip);
				}
			}, 1600, 160, TimeUnit.MILLISECONDS);

			scheduler.scheduleAtFixedRate(() -> {
				String ip = remwhitelist.poll();
				if (ip != null) {
					exec("sudo ipset del whitelist " + ip);
				}
			}, 1600, 160, TimeUnit.MILLISECONDS);
		});
	}

	private static void exec(String cmd) {
		try {
			run.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sleep() {
		sleep(750);
	}

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
