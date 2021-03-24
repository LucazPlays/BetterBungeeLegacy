package net.md_5.bungee.api;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

public class Blacklist {

	private static Blacklist Instance = new Blacklist();

    private ConcurrentHashMap<String, Integer> ratelimit = new ConcurrentHashMap<String,Integer>();
    
    
    
    private int globalratelimit = 0;
    
    @Getter
    private boolean protection = false;

	private CopyOnWriteArrayList<String> blacklist = new CopyOnWriteArrayList<String>();

	private CopyOnWriteArrayList<String> whitelist = new CopyOnWriteArrayList<String>();

	public CopyOnWriteArrayList<String> getBlacklist() {
		return blacklist;
	}

	public boolean isBlacklisted(String stg) {
		if (!protection)
			return false;
		return blacklist.contains(stg);
	}

	public void addBlacklist(String stg) {
		if (!protection)
			return;
		blacklist.add(stg);
		return;
	}

	public boolean isBlacklisted(InetAddress inet) {
		if (!protection)
			return false;
		String ip = inet.toString();
		return blacklist.contains(ip);
	}

	public boolean addBlacklist(InetAddress inet) {
		if (!protection)
			return false;
		String ip = inet.toString();
		return blacklist.add(ip);
	}

	public void clearBlacklist() {
		if (!protection)
			return;
		blacklist.clear();
	}
	
	public boolean removeBlacklist(String stg) {
		if (!protection)
			return false;
		return blacklist.remove(stg);
	}

	public void setBlacklist(CopyOnWriteArrayList<String> blacklist) {
		this.blacklist = blacklist;
	}

	public static Blacklist getInstance() {
		return Instance;
	}

	public Blacklist() {
		new Thread(() -> {
			while (true) {
				for (Entry<String, Integer> es : ratelimit.entrySet()) {
					if (es.getValue() > 0) {
						ratelimit.put(es.getKey(), es.getValue()-1);
					}
				}
				globalratelimit = 0;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
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
			ratelimit.put(ip, 2);
		}
	}
	
	public void addlimit(String ip) {
		ratelimit.put(ip, ratelimit.get(ip)+1);
	}
	
	public void removelimit(String ip) {
		ratelimit.put(ip, ratelimit.get(ip)-1);
	}

	public CopyOnWriteArrayList<String> getWhitelist() {
		return whitelist;
	}

	public void setWhitelist(CopyOnWriteArrayList<String> whitelist) {
		this.whitelist = whitelist;
	}

	public void addWhitelist(String stg) {
		if (!whitelist.contains(stg)) {
			this.whitelist.add(stg);
		}
	}
	
	public boolean containswhitelist(String stg) {
		return this.whitelist.contains(stg);
	}

	public void removeWhitelist(String stg) {
		if (whitelist.contains(stg)) {
			this.whitelist.remove(stg);
		}
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

	public String getRealAdress(ChannelHandlerContext ctx) {
        final SocketAddress remote = ctx.channel().remoteAddress();
        final String addr = remote != null ? remote.toString() : "";
		return addr.split("/")[1].split(":")[0];
	}
	
}
