package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.SocketAddress;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.StatisticsAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;
import net.md_5.bungee.util.EnumPackets;

@AllArgsConstructor
public class MinecraftDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Setter
	private Protocol protocol;
	private final boolean server;
	@Setter
	private int protocolVersion;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// See Varint21FrameDecoder for the general reasoning. We add this here as
		// ByteToMessageDecoder#handlerRemoved()
		// will fire any cumulated data through the pipeline, so we want to try and stop
		// it here.
		if (!ctx.channel().isActive()) {
			return;
		}

		Protocol.DirectionData prot = (server) ? protocol.TO_SERVER : protocol.TO_CLIENT;
		ByteBuf slice = in.copy(); // Can't slice this one due to EntityMap :(

		try {

			int packetId = DefinedPacket.readVarInt(in);

			if (prot.getDirection() == Direction.TO_SERVER) {

				if (packetId == EnumPackets.HANDSHAKE.getId()) {
					if (BungeeCord.getInstance().getBetterBungee().isDevdebugmode()) {
						NotifyManager.getInstance().addmessage("§aDebug #122");
					}
				}

				if (packetId == EnumPackets.SETTINGS.getId()) {
					if (BungeeCord.getInstance().getBetterBungee().isDevdebugmode()) {
						NotifyManager.getInstance().addmessage("§aDebug #323");
					}
				}

				if (Blacklist.getInstance().isProtection()) {
					if (packetId > Protocol.MAX_PACKET_ID) {
						String ip = Blacklist.getInstance().getRealAdress(ctx);
						Blacklist.getInstance().addBlacklist(ip);
						NotifyManager.getInstance().addmessage("§cBlocked §8- §e" + ip + " §8- §cBadpacket");
						
						ctx.close();

						slice = null;

						if (slice != null) {
							slice.release();
						}
						in.release();
						return;
					}
				}
				if (BungeeCord.getInstance().getBetterBungee().isPacketsizelimit()) {
					if (slice.readableBytes() > BungeeCord.getInstance().getBetterBungee().getPacketsizelimitsize()) {

						ProxiedPlayer player = getPlayer(ctx.channel().remoteAddress());

						NotifyManager.getInstance().addmessage("§cBlocked §8- §e" + player.getName() + " §8- §dto big packet " + packetId);

						StatisticsAPI.getInstance().addBlockedCrashAttempts();

						if (Blacklist.getInstance().isProtection()) {
							Blacklist.getInstance().addlimit(Blacklist.getInstance().getRealAdress(ctx), 32);
						}
						
						ctx.close();

						if (slice != null) {
							slice.release();
						}
						return;
					}
				}
			}

			DefinedPacket packet = prot.createPacket(packetId, protocolVersion);

			if (packet != null) {

				packet.read(in, prot.getDirection(), protocolVersion);

				if (in.isReadable()) {
					throw new BadPacketException("Did not read all bytes from packet " + packet.getClass() + " " + packetId + " Protocol " + protocol + " Direction " + prot.getDirection());
				}

			} else {
				in.skipBytes(in.readableBytes());
			}

			out.add(new PacketWrapper(packet, slice));

			slice = null;

		} finally {
			if (slice != null) {
				slice.release();
			}
		}
	}

	private ProxiedPlayer getPlayer(SocketAddress socket) {
		for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
			if (socket.equals(all.getSocketAddress())) {
				return all;
			}
		}
		return null;
	}
}
