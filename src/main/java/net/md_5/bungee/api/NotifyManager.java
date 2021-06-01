package net.md_5.bungee.api;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeTitle;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NotifyManager {

	public static NotifyManager Instance;

	private ChatMessageType messagetype = ChatMessageType.ACTION_BAR;
	private String prefix = BungeeCord.PREFIX;
	private String defaultnomessage = "§cKeine";
	private boolean async = false;
	private boolean consoleoutput = false;

	@Setter
	private boolean discord = false;
	
	private ProxyServer server = ProxyServer.getInstance();

	private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<String>();

	
	
	
	private ConcurrentLinkedQueue<String> discordmessages = new ConcurrentLinkedQueue<String>();;

	
	private ConcurrentHashMap<String,ChatMessageType> players = new ConcurrentHashMap<String,ChatMessageType>();

	private Set<String> titleplayer = ConcurrentHashMap.newKeySet();

	
	public NotifyManager() {
		loop();
	}

	private void run(Runnable run) {
		if (async) {
			new Thread(run).start();
		} else {
			run.run();
		}
	}

	private void run(Runnable run, boolean async) {
		if (async) {
			new Thread(run).start();
		} else {
			run.run();
		}
	}

	public NotifyManager addmessage(String s) {
		messages.add(s);
		if (discord) {
			discordmessages.add(s);
		}
		return this;
	}

	public NotifyManager send() {
		this.run(() -> {
			if (messages.size() > 0) {
				String message = "§cNone";
				message = messages.poll();
				if (consoleoutput) {
					System.out.println(message.replaceAll("§", ""));
				}
				for (ProxiedPlayer all : server.getPlayers()) {
					if (players.containsKey(all.getName())) {
						all.sendMessage(players.get(all.getName()), TextComponent.fromLegacyText(prefix + message));
					}
				}
			}
			if (Blacklist.getInstance().isUnderattack()) {
				for (ProxiedPlayer all : server.getPlayers()) {
					if (titleplayer.contains(all.getName())) {
						ProxyServer.getInstance().createTitle().title(TextComponent.fromLegacyText("§cUnder Attack")).subTitle(TextComponent.fromLegacyText("§eCPS §8- §e"+Blacklist.getInstance().getAveragecps())).send(all);
					}
				}
			}
			
		});
		return this;
	}

	public NotifyManager loop() {
		this.run(() -> {
			while (true) {
				send();
				try {
					if (messages.size() > 3000) {
						
					} else if (messages.size() > 1750) {
						Thread.sleep(1);
					} else if (messages.size() > 350) {
						Thread.sleep(3);
					} else if (messages.size() > 250) {
						Thread.sleep(5);
					} else if (messages.size() > 125) {
						Thread.sleep(10);
					} else if (messages.size() > 50) {
						Thread.sleep(25);
					} else if (messages.size() > 10) {
						Thread.sleep(250);
					} else if (messages.size() > 6) {
						Thread.sleep(500);
					} else if (messages.size() <= 3) {
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, true);
		return this;
	}

	public ChatMessageType getMessagetype() {
		return messagetype;
	}

	public NotifyManager setMessagetype(ChatMessageType messagetype) {
		this.messagetype = messagetype;
		return this;
	}

	public String getPrefix() {
		return prefix;
	}
	
	
	
	
	public ConcurrentLinkedQueue<String> getMessages() {
		return messages;
	}
	
	
	

	public NotifyManager setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public String getDefaultnomessage() {
		return defaultnomessage;
	}

	public NotifyManager setDefaultnomessage(String defaultnomessage) {
		this.defaultnomessage = defaultnomessage;
		return this;
	}

	public boolean isConsoleoutput() {
		return consoleoutput;
	}

	public NotifyManager setAsync(boolean async) {
		this.async = async;
		return this;
	}

	public boolean isAsync() {
		return async;
	}

	public NotifyManager setConsoleoutput(boolean consoleoutput) {
		this.consoleoutput = consoleoutput;
		return this;
	}

	public static NotifyManager getInstance() {
		return Instance;
	}

	public ConcurrentLinkedQueue<String> getDiscordmessages() {
		return discordmessages;
	}


	public void deleteDiscordmessages() {
		discordmessages.clear();
	}

	public ConcurrentHashMap<String, ChatMessageType> getPlayers() {
		return players;
	}

	public Set<String> getTitlePlayers() {
		return titleplayer;
	}
}
