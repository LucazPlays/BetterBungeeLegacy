package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.SocketAddress;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.ProxyServer;
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
				if (slice.readableBytes() > 8000) {
					
					ProxiedPlayer player = getPlayer(ctx.channel().remoteAddress());

					NotifyManager.getInstance().addmessage("§cBlocked §8- §e" + player.getName() + " §8- §dto big packet " + packetId);
					
					Blacklist.getInstance().addlimit(Blacklist.getInstance().getRealAdress(ctx), 30);
					
					ctx.close();

					slice = null;
					
					if (slice != null) {
						slice.release();
					}
					
					return;
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
