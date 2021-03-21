package net.md_5.bungee.util;

public enum EnumPackets {
	UNKNOWN(-1), KEEPALIVE(0), PacketPlayerPosition(6), PACKETPLAYER(3), CHAT(2), PACKETPLAYERLOOK(5), CHUNK(33),
	CLOSE(13), OPEN(53), ABILITIES(11), CREATIVEINVENTORYACTION(16), INTERACTBLOCKPLACE(8), PING(1), DIGBREAK(7),
	USEENTITY(2), HANDSHAKE(63), CUSTOMPAYLOAD(23), UPDATESIGN(18),SETTINGS(21);

	private int id = 0;

	public int getId() {
		return id;
	}

	EnumPackets(int i) {
		this.id = i;
	}

	public static EnumPackets getVersion(int i) {
		for (EnumPackets v : EnumPackets.values()) {
			if (v.getId() == i) {
				return v;
			}
		}
		return UNKNOWN;
	}
}
