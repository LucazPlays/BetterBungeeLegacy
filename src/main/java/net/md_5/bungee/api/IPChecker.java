package net.md_5.bungee.api;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.netty.ChannelWrapper;

public class IPChecker {
	
	@Getter
	private static IPChecker Instance = new IPChecker();

	private boolean serviceonline = false;
	
	CopyOnWriteArrayList<String> checklist = new CopyOnWriteArrayList<String>();
	
	ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	
	public IPChecker() {
		new Thread(() -> {
			while (true) {
				try {
					RestAPIResponse ipcheckeralive = RestAPI.getInstance().info("http://ipcheck.skydb.de/alive");
					if (ipcheckeralive.getFailed()) {
						serviceonline = false;
					} else {
						serviceonline = true;
					}
					Thread.sleep(60000);
					checkthemall();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public boolean isipresidental(String ip) {
		if (serviceonline) {
			RestAPIResponse ipcheckeralive = RestAPI.getInstance().info("http://ipcheck.skydb.de/residental?ip="+ip);
			if (ipcheckeralive.getFailed()) {
				serviceonline = false;
			} else {
				return (!ipcheckeralive.getText().contains("false"));
			}
			
		}
		return true;
	}

	public void start(Runnable run) {
		threads.execute(run);
	}

	public void addtocheck(String ip) {
		if (!checklist.contains(ip)) {
			checklist.add(ip);
		}
	}

	public void checkthemall() {
		if (serviceonline) {
			if (checklist.size() > 0) {
				
				String ips = "";
				
				for (String ip : checklist) {
					ips += ip + ",";
				}

				
				RestAPIResponse ipcheckeralive = RestAPI.getInstance().info("http://ipcheck.skydb.de/check?ips="+ips);
				
				checklist.clear();
			}
		}
	}
	
	
}
