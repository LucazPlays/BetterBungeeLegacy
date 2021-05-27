package net.md_5.bungee.api;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import net.md_5.bungee.BetterBungee;
import net.md_5.bungee.BungeeCord;

public class ServerListAPI {
	
	public static ServerListAPI getInstance() {
		return Instance;
	}

	private static ServerListAPI Instance = new ServerListAPI();

	private Set<String> pinged = ConcurrentHashMap.newKeySet();

	public ServerListAPI() {}

	public void pinged(String ip) {
		if (!pinged.contains(ip)) {
			pinged.add(ip);
		}
	}

	public boolean pingedbefore(String ip) {
		return pinged.contains(ip);
	}
	

	public int getUsersinServerList() {
		return pinged.size();
	}
	
}
