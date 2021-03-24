package net.md_5.bungee.api;

import java.util.concurrent.CopyOnWriteArrayList;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NotifyManager {

	private static NotifyManager Instance = new NotifyManager();

	private ChatMessageType messagetype = ChatMessageType.ACTION_BAR;
	private String prefix = BungeeCord.PREFIX;
	private String defaultnomessage = "§cKeine";
	private boolean async = false;
	private boolean consoleoutput = false;
	private ProxyServer server = ProxyServer.getInstance();
	private CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<String>();

	public CopyOnWriteArrayList<String> players = new CopyOnWriteArrayList<String>();

	public NotifyManager() {}

	private NotifyManager run(Runnable run) {
		if (async) {
			new Thread(run).start();
		} else {
			run.run();
		}
		return this;
	}

	private NotifyManager run(Runnable run, boolean async) {
		if (async) {
			new Thread(run).start();
		} else {
			run.run();
		}
		return this;
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
				if (consoleoutput)
					System.out.println(message.replaceAll("§", ""));
				for (ProxiedPlayer all : server.getPlayers()) {
					if (players.contains(all.getName())) {
						all.sendMessage(messagetype, TextComponent.fromLegacyText(prefix + message));
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
					Thread.sleep(500);
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
