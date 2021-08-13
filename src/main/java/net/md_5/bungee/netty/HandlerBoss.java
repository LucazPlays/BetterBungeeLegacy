package net.md_5.bungee.netty;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.unix.Errors.NativeIoException;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.timeout.ReadTimeoutException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import net.md_5.bungee.BetterBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.IPChecker;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.StatisticsAPI;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.PingHandler;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.OverflowPacketException;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.util.QuietException;

/**
 * This class is a primitive wrapper for {@link PacketHandler} instances tied to
 * channels to maintain simple states, and only call the required, adapted
 * methods when the channel is connected.
 */
public class HandlerBoss extends ChannelInboundHandlerAdapter {

	private ChannelWrapper channel;
	private PacketHandler handler;

	private Blacklist list = Blacklist.getInstance();
	private NotifyManager notify = NotifyManager.getInstance();

	public void setHandler(PacketHandler handler) {
		Preconditions.checkArgument(handler != null, "handler");
		this.handler = handler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (!BungeeCord.getInstance().getBetterBungee().isProxyProtocol()) {
			filter(ctx);
		}
		if (handler != null) {
			channel = new ChannelWrapper(ctx);
			handler.connected(channel);
			if (!(handler instanceof InitialHandler || handler instanceof PingHandler)) {
				ProxyServer.getInstance().getLogger().log(Level.INFO, "{0} has connected", handler);
			}
		}
	}

	private void filter(ChannelHandlerContext ctx) {
		String ip = null;
		list.addConnectionspersecond(1);
		if (BungeeCord.getInstance().getBetterBungee().isProxyProtocol()) {
			ip = list.getRealAdress(channel);
		} else {
			ip = list.getRealAdress(ctx);
		}

		if (list.isProtection()) {
			if (list.isBlacklisted(ip)) {
				if (BetterBungee.getInstance().isDevdebugmode()) {
					notify.addmessage("§cBlocked §8- §e" + ip + " §8- §4Blacklisted");
				}
				ctx.close();
				StatisticsAPI.getInstance().addblockedConnection();
				return;
			}

			list.createlimit(ip);

			list.addlimit(ip);

			int rate = list.ratelimit(ip);

			if (rate > list.getPerIPratelimit()) {
				if (BetterBungee.getInstance().isDevdebugmode()) {
					notify.addmessage("§cBlocked §8- §e" + ip + " §8- §cPerIPRate Limit");
				}
				ctx.close();
				if (list.containswhitelist(ip)) {
					list.removeWhitelist(ip);
				}
				StatisticsAPI.getInstance().addblockedConnection();
				;
				return;
			}

			if (!list.containswhitelist(ip)) {
				list.addConnectionratelimit(1);
				if (list.getGlobalratelimit() < list.getConnectionratelimit()) {
					if (BetterBungee.getInstance().isDevdebugmode()) {
						notify.addmessage("§cBlocked §8- §e" + ip + " §8- §cGlobal Ratelimit");
					}
					ctx.close();
					StatisticsAPI.getInstance().addblockedConnection();
					return;
				}
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (handler != null) {
			channel.markClosed();
			handler.disconnected(channel);
			if (!(handler instanceof InitialHandler || handler instanceof PingHandler)) {
				ProxyServer.getInstance().getLogger().log(Level.INFO, "{0} has disconnected", handler);
			}
		}
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		if (handler != null) {
			handler.writabilityChanged(channel);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HAProxyMessage) {

			HAProxyMessage proxy = (HAProxyMessage) msg;


			try {
				if (proxy.sourceAddress() != null) {
					channel.setProxyAddress(list.getRealAdress(ctx.channel().remoteAddress()));
					
					
					InetSocketAddress newAddress = new InetSocketAddress(proxy.sourceAddress(), proxy.sourcePort());

					ProxyServer.getInstance().getLogger().log(Level.FINE, "Set remote address via PROXY {0} -> {1}", new Object[] { channel.getRemoteAddress(), newAddress });

					channel.setRemoteAddress(newAddress);

					if (BungeeCord.getInstance().getBetterBungee().isProxyProtocol()) {
						filter(ctx);
					}

				}
			} finally {
				proxy.release();
			}
			return;
		}

		if (handler != null) {

			PacketWrapper packet = (PacketWrapper) msg;
			boolean sendPacket = handler.shouldHandle(packet);

			try {
				if (sendPacket && packet.packet != null) {
					try {
						packet.packet.handle(handler);
					} catch (CancelSendSignal ex) {
						sendPacket = false;
					}
				}

				if (sendPacket) {
					handler.handle(packet);
				}
			} finally {
				packet.trySingleRelease();
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		try {
		if (ctx.channel().isActive()) {
			boolean logExceptions = !(handler instanceof PingHandler);

			if (logExceptions) {
				if (cause instanceof ReadTimeoutException) {
					ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - read timed out", handler);
				} else if (cause instanceof DecoderException) {
					if (cause instanceof CorruptedFrameException) {
						if (Blacklist.getInstance().isProtection()) {
							String ip = null;
							if (BungeeCord.getInstance().getBetterBungee().isProxyProtocol()) {
								ip = list.getRealAdress(channel);
							} else {
								ip = list.getRealAdress(ctx);
							}

							Blacklist.getInstance().addBlacklist(ip);
							if (BetterBungee.getInstance().isDevdebugmode()) {
								NotifyManager.getInstance().addmessage("§cBlocked §8- §e" + ip + " §8- §cCorruptedFrame");
							}
							ctx.close();
							return;
						}
						ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - corrupted frame: {1}",new Object[] { handler, cause.getMessage() });
					} else if (cause.getCause() instanceof BadPacketException) {
						ProxyServer.getInstance().getLogger().log(Level.WARNING,
								"{0} - bad packet ID, are mods in use!? {1}",
								new Object[] { handler, cause.getCause().getMessage() });
					} else if (cause.getCause() instanceof OverflowPacketException) {
						ProxyServer.getInstance().getLogger().log(Level.WARNING,
								"{0} - overflow in packet detected! {1}",
								new Object[] { handler, cause.getCause().getMessage() });
					}
				} else if (cause instanceof IOException
						|| (cause instanceof IllegalStateException && handler instanceof InitialHandler)) {
					if (cause instanceof IllegalStateException) {
						if (Blacklist.getInstance().isProtection()) {
							String ip = null;
							if (BungeeCord.getInstance().getBetterBungee().isProxyProtocol()) {
								ip = list.getRealAdress(channel);
							} else {
								ip = list.getRealAdress(ctx);
							}

							Blacklist.getInstance().addBlacklist(ip);
							if (BetterBungee.getInstance().isDevdebugmode()) {
								NotifyManager.getInstance().addmessage("§cBlocked §8- §e" + ip + " §8- §cIllegalState");
							}
							ctx.close();
							return;
						}
					}
					if (cause instanceof NativeIoException) {
						if (BungeeCord.getInstance().getBetterBungee().isProxyProtocol()) {
							ctx.close();
							return;
						}
					}

					ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} - {1}: {2}", new Object[] { handler, cause.getClass().getSimpleName(), cause.getMessage() + "" });
				} else if (cause instanceof QuietException) {
					ProxyServer.getInstance().getLogger().log(Level.SEVERE, "{0} - encountered exception: {1}",
							new Object[] { handler, cause });
				} else {
					ProxyServer.getInstance().getLogger().log(Level.SEVERE, handler + " - encountered exception",
							cause);
				}
			}

			if (handler != null) {
				try {
					handler.exception(cause);
				} catch (Exception ex) {
					if (ex instanceof NativeIoException) {
						if (BungeeCord.getInstance().getBetterBungee().isProxyProtocol()) {
							ctx.close();
							return;
						}
					}
					ProxyServer.getInstance().getLogger().log(Level.SEVERE, handler + " - exception processing exception", ex);
				}
			}
			ctx.close();
		}
		} catch (Throwable ex) {
			ex.printStackTrace();
			String ip = null;
			if (BungeeCord.getInstance().getBetterBungee().isProxyProtocol()) {
				ip = list.getRealAdress(channel);
			} else {
				ip = list.getRealAdress(ctx);
			}

//			Blacklist.getInstance().addBlacklist(ip);
//			if (BetterBungee.getInstance().isDevdebugmode()) {
				NotifyManager.getInstance().addmessage("§cBlocked §8- §e" + ip + " §8- §cUnknownCrasher");
//			}
			ctx.close();
		}
	}
}
