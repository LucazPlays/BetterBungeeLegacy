package net.md_5.bungee.api;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import net.md_5.bungee.BetterBungee;
import net.md_5.bungee.BungeeCord;

public class StatisticsAPI {

	public static StatisticsAPI getInstance() {
		return Instance;
	}

	private static StatisticsAPI Instance = new StatisticsAPI();

	private int blockedconnections = 0;

	private int blockedcrashattempts = 0;
	

	public StatisticsAPI() {
	}

	public void addblockedConnection() {
		blockedconnections += 1;
	}

	public int getBlockedConnections() {
		return blockedconnections;
	}


	public void addBlockedCrashAttempts() {
		blockedcrashattempts += 1;
	}

	public int getBlockedCrashAttempts() {
		return blockedcrashattempts;
	}
}
