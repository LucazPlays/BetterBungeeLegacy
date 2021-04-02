package net.md_5.bungee.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NotifyManager {

	public static NotifyManager Instance;

	private ChatMessageType messagetype = ChatMessageType.ACTION_BAR;
	private String prefix = BungeeCord.PREFIX;
	private String defaultnomessage = "§cKeine";
	private boolean async = false;
	private boolean consoleoutput = false;
	
	private ProxyServer server = ProxyServer.getInstance();
	
	private CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<String>();

	public ConcurrentHashMap<String,ChatMessageType> players = new ConcurrentHashMap<String,ChatMessageType>();

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
		return this;
	}

	public NotifyManager send() {
		this.run(() -> {
			if (messages.size() > 0) {
				String message = "§cNone";
				message = messages.get(0);
				messages.remove(message);
				if (consoleoutput) {
					System.out.println(message.replaceAll("§", ""));
				}
				for (ProxiedPlayer all : server.getPlayers()) {
					if (players.containsKey(all.getName())) {
						all.sendMessage(players.get(all.getName()), TextComponent.fromLegacyText(prefix + message));
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
					if (messages.size() > 12500) {
					} else if (messages.size() > 1750) {
						Thread.sleep(1);
					} else if (messages.size() > 1000) {
						Thread.sleep(5);
					} else if (messages.size() > 500) {
						Thread.sleep(10);
					} else if (messages.size() > 250) {
						Thread.sleep(25);
					} else if (messages.size() > 100) {
						Thread.sleep(75);
					} else if (messages.size() > 35) {
						Thread.sleep(250);
					} else if (messages.size() > 10) {
						Thread.sleep(350);
					} else if (messages.size() <= 10) {
						Thread.sleep(2000);
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
	
	
	
	
	public CopyOnWriteArrayList<String> getMessages() {
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
}
