package net.md_5.bungee.connection;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.BetterBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.BungeeServerInfo;
import net.md_5.bungee.EncryptionUtil;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.Blacklist;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.IPChecker;
import net.md_5.bungee.api.NotifyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerListAPI;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.StatisticsAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.http.HttpClient;
import net.md_5.bungee.jni.cipher.BungeeCipher;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.netty.cipher.CipherDecoder;
import net.md_5.bungee.netty.cipher.CipherEncoder;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.Protocol;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.EncryptionRequest;
import net.md_5.bungee.protocol.packet.EncryptionResponse;
import net.md_5.bungee.protocol.packet.Handshake;
import net.md_5.bungee.protocol.packet.Kick;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;
import net.md_5.bungee.protocol.packet.LoginPayloadResponse;
import net.md_5.bungee.protocol.packet.LoginRequest;
import net.md_5.bungee.protocol.packet.LoginSuccess;
import net.md_5.bungee.protocol.packet.PingPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;
import net.md_5.bungee.protocol.packet.StatusRequest;
import net.md_5.bungee.protocol.packet.StatusResponse;
import net.md_5.bungee.protocol.packet.Title;
import net.md_5.bungee.util.AllowedCharacters;
import net.md_5.bungee.util.BufUtil;
import net.md_5.bungee.util.QuietException;

@RequiredArgsConstructor
public class InitialHandler extends PacketHandler implements PendingConnection {

	private final BungeeCord bungee;

	private ChannelWrapper ch;

	@Getter
	private final ListenerInfo listener;
	@Getter
	private Handshake handshake;
	@Getter
	private LoginRequest loginRequest;

	private EncryptionRequest request;

	@Getter
	private PluginMessage brandMessage;

	@Getter
	private final Set<String> registeredChannels = new HashSet<>();

	private State thisState = State.HANDSHAKE;

	private final Unsafe unsafe = new Unsafe() {
		@Override
		public void sendPacket(DefinedPacket packet) {
			ch.write(packet);
		}
	};
	@Getter
	private boolean onlineMode = BungeeCord.getInstance().config.isOnlineMode();

	@Getter
	private InetSocketAddress virtualHost;

	private String name;
	
	@Getter
	private UUID uniqueId;
	@Getter
	private UUID offlineId;
	@Getter
	private LoginResult loginProfile;
	@Getter
	private boolean legacy;
	@Getter
	private String extraDataInHandshake = "";

	@Getter
	private String proxyip;

	@Getter
	private long startedhandshake;

	@Override
	public boolean shouldHandle(PacketWrapper packet) throws Exception {
		return !ch.isClosing();
	}

	private enum State {

		HANDSHAKE, STATUS, PING, USERNAME, ENCRYPT, FINISHING;
	}

	private boolean canSendKickMessage() {
		return thisState == State.USERNAME || thisState == State.ENCRYPT || thisState == State.FINISHING;
	}

	@Override
	public void connected(ChannelWrapper channel) throws Exception {
		this.ch = channel;
	}

	@Override
	public void exception(Throwable t) throws Exception {
		if (canSendKickMessage()) {
			disconnect(ChatColor.RED + Util.exception(t));
		} else {
			ch.close();
		}
	}

	@Override
	public void handle(PacketWrapper packet) throws Exception {
		try {
			if (packet.packet == null) {
				cancelcrash("QuietException");
//				throw new QuietException( "Unexpected packet received during login process! " + BufUtil.dump(packet.buf, 16));
			}
		} catch (Throwable e) {
			if (BetterBungee.getInstance().isDevdebugmode()) {
				e.printStackTrace();
			}
		}
	}

	private void cancelcrash() {
		cancelcrash("NullPing");
	}

	private void cancelcrash(String cause) {
		try {
			if (Blacklist.getInstance().isProtection()) {
				Blacklist.getInstance().addConnectionratelimit(-1);
				StatisticsAPI.getInstance().addblockedConnection();
				list.addBlacklist(list.getRealAdress(ch));
				if (BetterBungee.getInstance().isDevdebugmode()) {
					NotifyManager.getInstance()
							.addmessage("§cBlocked §8- §e" + list.getRealAdress(ch) + " §8- §c" + cause);
				}
				ch.close();
			}
		} catch (Throwable e) {
			if (BetterBungee.getInstance().isDevdebugmode()) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handle(PluginMessage pluginMessage) throws Exception {
		// TODO: Unregister?
		this.relayMessage(pluginMessage);
	}

	@Override
	public void handle(LegacyHandshake legacyHandshake) throws Exception {
		this.legacy = true;
		ch.close(bungee.getTranslation("outdated_client", bungee.getGameVersion()));
	}

    @Override
    public void handle(LegacyPing ping) throws Exception
    {
        this.legacy = true;
        final boolean v1_5 = ping.isV1_5();

        ServerInfo forced = AbstractReconnectHandler.getForcedHost( this );
        final String motd = ( forced != null ) ? forced.getMotd() : listener.getMotd();
        final int protocol = bungee.getProtocolVersion();

        Callback<ServerPing> pingBack = new Callback<ServerPing>()
        {
            @Override
            public void done(ServerPing result, Throwable error)
            {
                if ( error != null )
                {
                    result = getPingInfo( bungee.getTranslation( "ping_cannot_connect" ), protocol );
                    bungee.getLogger().log( Level.WARNING, "Error pinging remote server", error );
                }

                Callback<ProxyPingEvent> callback = new Callback<ProxyPingEvent>()
                {
                    @Override
                    public void done(ProxyPingEvent result, Throwable error)
                    {
                        if ( ch.isClosed() )
                        {
                            return;
                        }

                        ServerPing legacy = result.getResponse();
                        String kickMessage;

                        if ( v1_5 )
                        {
                            kickMessage = ChatColor.DARK_BLUE
                                    + "\00" + 127
                                    + '\00' + legacy.getVersion().getName()
                                    + '\00' + getFirstLine( legacy.getDescription() )
                                    + '\00' + ( ( legacy.getPlayers() != null ) ? legacy.getPlayers().getOnline() : "-1" )
                                    + '\00' + ( ( legacy.getPlayers() != null ) ? legacy.getPlayers().getMax() : "-1" );
                        } else
                        {
                            // Clients <= 1.3 don't support colored motds because the color char is used as delimiter
                            kickMessage = ChatColor.stripColor( getFirstLine( legacy.getDescription() ) )
                                    + '\u00a7' + ( ( legacy.getPlayers() != null ) ? legacy.getPlayers().getOnline() : "-1" )
                                    + '\u00a7' + ( ( legacy.getPlayers() != null ) ? legacy.getPlayers().getMax() : "-1" );
                        }

                        ch.close( kickMessage );
                    }
                };

                bungee.getPluginManager().callEvent( new ProxyPingEvent( InitialHandler.this, result, callback ) );
            }
        };

        if ( forced != null && listener.isPingPassthrough() )
        {
            ( (BungeeServerInfo) forced ).ping( pingBack, bungee.getProtocolVersion() );
        } else
        {
            pingBack.done( getPingInfo( motd, protocol ), null );
        }
    }

	private static String getFirstLine(String str) {
		int pos = str.indexOf('\n');
		return pos == -1 ? str : str.substring(0, pos);
	}

	private ServerPing getPingInfo(String motd, int protocol) {
		String Version = (BetterBungee.getInstance().isSnapshotupdate() ? "§c" : "§a")
				+ BetterBungee.getInstance().Version;

		ServerPing ping = new ServerPing(new ServerPing.Protocol("§eBetterBungee §8- " + Version, protocol),
				new ServerPing.Players(listener.getMaxPlayers(), bungee.getOnlineCount(), null), motd, (Favicon) null);

		if (Blacklist.getInstance().getConnectionratelimit() < Blacklist.getInstance().getGlobalfaviconlimit()) {
			ping.setFavicon(BungeeCord.getInstance().config.getFaviconObject());
		}

		return ping; // protection against ping spammer
	}

	@Override
	public void handle(StatusRequest statusRequest) throws Exception {
		Preconditions.checkState(thisState == State.STATUS, "Not expecting STATUS");

		ServerInfo forced = AbstractReconnectHandler.getForcedHost(this);
		final String motd = (forced != null) ? forced.getMotd() : listener.getMotd();
		final int protocol = (ProtocolConstants.SUPPORTED_VERSION_IDS.contains(handshake.getProtocolVersion()))
				? handshake.getProtocolVersion()
				: bungee.getProtocolVersion();

		Callback<ServerPing> pingBack = new Callback<ServerPing>() {
			@Override
			public void done(ServerPing result, Throwable error) {
				if (error != null) {
					result = getPingInfo(bungee.getTranslation("ping_cannot_connect"), protocol);
					bungee.getLogger().log(Level.WARNING, "Error pinging remote server", error);
				}

				Callback<ProxyPingEvent> callback = new Callback<ProxyPingEvent>() {
					@Override
					public void done(ProxyPingEvent pingResult, Throwable error) {
						Gson gson = BungeeCord.getInstance().gson;
						unsafe.sendPacket(new StatusResponse(gson.toJson(pingResult.getResponse())));
						if (bungee.getConnectionThrottle() != null) {
							bungee.getConnectionThrottle().unthrottle(getSocketAddress());
						}
					}
				};

				bungee.getPluginManager().callEvent(new ProxyPingEvent(InitialHandler.this, result, callback));
			}
		};

		if (forced != null && listener.isPingPassthrough()) {
			((BungeeServerInfo) forced).ping(pingBack, handshake.getProtocolVersion());
		} else {
			pingBack.done(getPingInfo(motd, protocol), null);
		}

		thisState = State.PING;
	}

	@Override
	public void handle(PingPacket ping) throws Exception {
		Preconditions.checkState(thisState == State.PING, "Not expecting PING");
		unsafe.sendPacket(ping);
		disconnect("");
	}

	@Override
	public void handle(Handshake handshake) throws Exception {

		Preconditions.checkState(thisState == State.HANDSHAKE, "Not expecting HANDSHAKE");

		this.handshake = handshake;
		
		ch.setVersion(handshake.getProtocolVersion());
		
        ch.getHandle().pipeline().remove( PipelineUtils.LEGACY_KICKER );

		boolean hostprotection = BetterBungee.getInstance().isHostprotectionnames();

		boolean pingprotection = BetterBungee.getInstance().isPingcheck();

		startedhandshake = System.currentTimeMillis();

		if (Blacklist.getInstance().getForcewhitelistedips().contains(list.getRealAdress(ch))) {
			hostprotection = false;
			pingprotection = false;
		}

		// Starting with FML 1.8, a "\0FML\0" token is appended to the handshake. This
		// interferes
		// with Bungee's IP forwarding, so we detect it, and remove it from the host
		// string, for now.
		// We know FML appends \00FML\00. However, we need to also consider that other
		// systems might
		// add their own data to the end of the string. So, we just take everything from
		// the \0 character
		// and save it for later.

		if (handshake.getHost().contains("\0")) {
			String[] split = handshake.getHost().split("\0", 2);
			handshake.setHost(split[0]);
			extraDataInHandshake = "\0" + split[1];
		}

		// SRV records can end with a . depending on DNS / client.
		if (handshake.getHost().endsWith(".")) {
			handshake.setHost(handshake.getHost().substring(0, handshake.getHost().length() - 1));
		}

		this.virtualHost = InetSocketAddress.createUnresolved(handshake.getHost(), handshake.getPort());

		bungee.getPluginManager().callEvent(new PlayerHandshakeEvent(InitialHandler.this, handshake));

		String ip = list.getRealAdress(ch);

		if (BetterBungee.getInstance().isDevdebugmode()) {
			bungee.getLogger().log(Level.INFO, "{0} Hostname: " + handshake.getHost(), this);
		}

		if (hostprotection) {
			if (!BetterBungee.getInstance().getHostnames().contains(handshake.getHost().toLowerCase(Locale.ROOT))) {
				Blacklist.getInstance().addlimit(ip, 1);
				Blacklist.getInstance().addConnectionratelimit(-1);
				StatisticsAPI.getInstance().addblockedConnection();
				ch.close();
				return;
			}
		}

		switch (handshake.getRequestedProtocol()) {
		case 1:
			// Ping
			if (bungee.getConfig().isLogPings()) {
				bungee.getLogger().log(Level.INFO, "{0} has pinged", this);
			}
			ServerListAPI.getInstance().pinged(ip);
			thisState = State.STATUS;
			ch.setProtocol(Protocol.STATUS);
			break;
		case 2:
			if (BetterBungee.getInstance().isPingcheck()) {
				if (Blacklist.getInstance().getAveragecps() > BetterBungee.getInstance()
						.getPingcheckonconnectlimit()) {
					if (!Blacklist.getInstance().containswhitelist(ip)) {
						if (!ServerListAPI.getInstance().pingedbefore(ip)) {
							Blacklist.getInstance().addConnectionratelimit(-1);
							StatisticsAPI.getInstance().addblockedConnection();
							Blacklist.getInstance().addlimit(ip, 1);
							ch.close();
							return;
						}
					}
				}
			}
			// Login
			bungee.getLogger().log(Level.INFO, "{0} has connected", this);
			thisState = State.USERNAME;
			ch.setProtocol(Protocol.LOGIN);
			if (!ProtocolConstants.SUPPORTED_VERSION_IDS.contains(handshake.getProtocolVersion())) {
				if (handshake.getProtocolVersion() > bungee.getProtocolVersion()) {
					disconnect(bungee.getTranslation("outdated_server", bungee.getGameVersion()));
				} else {
					disconnect(bungee.getTranslation("outdated_client", bungee.getGameVersion()));
				}
				return;
			}
			break;
		default:
			cancelcrash("QuietException");
			return;
//			throw new QuietException("Cannot request protocol " + handshake.getRequestedProtocol());
		}
	}

	@Override
	public void handle(LoginRequest loginRequest) throws Exception {
		Preconditions.checkState(thisState == State.USERNAME, "Not expecting USERNAME");

		if (!AllowedCharacters.isValidName(loginRequest.getData(), onlineMode)) {
			disconnect(bungee.getTranslation("name_invalid"));
			return;
		}
		
        if ( BungeeCord.getInstance().config.isEnforceSecureProfile() && getVersion() < ProtocolConstants.MINECRAFT_1_19_3 )
        {
            PlayerPublicKey publicKey = loginRequest.getPublicKey();
            if ( publicKey == null )
            {
                disconnect( bungee.getTranslation( "secure_profile_required" ) );
                return;
            }

            if ( Instant.ofEpochMilli( publicKey.getExpiry() ).isBefore( Instant.now() ) )
            {
                disconnect( bungee.getTranslation( "secure_profile_expired" ) );
                return;
            }

            if ( getVersion() < ProtocolConstants.MINECRAFT_1_19_1 )
            {
                if ( !EncryptionUtil.check( publicKey, null ) )
                {
                    disconnect( bungee.getTranslation( "secure_profile_invalid" ) );
                    return;
                }
            }
        }



		this.loginRequest = loginRequest;

//		if (getName().length() > 16) {
//			disconnect(bungee.getTranslation("name_too_long"));
//			cancelcrash("Name To Long");
//			return;
//		}

		int limit = BungeeCord.getInstance().config.getPlayerLimit();

		if (limit > 0 && bungee.getOnlineCount() >= limit && !BetterBungee.getInstance().isFullproxyjoin()) {
			disconnect(bungee.getTranslation("proxy_full"));
			return;
		}

		// If offline mode and they are already on, don't allow connect
		// We can just check by UUID here as names are based on UUID
		if (!isOnlineMode() && bungee.getPlayer(getUniqueId()) != null) {
			disconnect(bungee.getTranslation("already_connected_proxy"));
			return;
		}

		Callback<PreLoginEvent> callback = new Callback<PreLoginEvent>() {

			@Override
			public void done(PreLoginEvent result, Throwable error) {
				if (result.isCancelled()) {
                    BaseComponent[] reason = result.getCancelReasonComponents();
                    disconnect( ( reason != null ) ? reason : TextComponent.fromLegacyText( bungee.getTranslation( "kick_message" ) ) );
					return;
				}
				if (ch.isClosed()) {
					return;
				}
				if (onlineMode) {
					thisState = State.ENCRYPT;
					unsafe().sendPacket(request = EncryptionUtil.encryptRequest());
				} else {
					thisState = State.FINISHING;
					finish();
				}
			}
		};

		// fire pre login event
		bungee.getPluginManager().callEvent(new PreLoginEvent(InitialHandler.this, callback));
	}

	@Override
	public void handle(final EncryptionResponse encryptResponse) throws Exception {
		try {
			if (BungeeCord.getInstance().getBetterBungee().isDevdebugmode()) {
				NotifyManager.getInstance().addmessage((System.currentTimeMillis() - startedhandshake) + "ms");
			}

	         Preconditions.checkState( EncryptionUtil.check( loginRequest.getPublicKey(), encryptResponse, request ), "Invalid verification" );

			SecretKey sharedKey = EncryptionUtil.getSecret(encryptResponse, request);

			BungeeCipher decrypt = EncryptionUtil.getCipher(false, sharedKey);

			ch.addBefore(PipelineUtils.FRAME_DECODER, PipelineUtils.DECRYPT_HANDLER, new CipherDecoder(decrypt));

			BungeeCipher encrypt = EncryptionUtil.getCipher(true, sharedKey);

			ch.addBefore(PipelineUtils.FRAME_PREPENDER, PipelineUtils.ENCRYPT_HANDLER, new CipherEncoder(encrypt));

			if (BetterBungee.getInstance().isSessionchache()) {

				LoginResult cached = BungeeCord.getInstance().getSessionCache().getCachedResult(getSocketAddress());

				if (cached != null && cached.getName().equals(getName())) {
					BungeeCord.getInstance().getLogger().log(Level.FINE,
							() -> "Logged in cached " + cached + " from " + getSocketAddress());
					thisState = State.FINISHING;
					loginProfile = cached;
					name = cached.getName();
					uniqueId = Util.getUUID(cached.getId());
					finish();
					return;
				}

			}
			String encName = URLEncoder.encode(InitialHandler.this.getName(), "UTF-8");

			MessageDigest sha = MessageDigest.getInstance("SHA-1");

			for (byte[] bit : new byte[][] { request.getServerId().getBytes("ISO_8859_1"), sharedKey.getEncoded(),
					EncryptionUtil.keys.getPublic().getEncoded() }) {
				sha.update(bit);
			}
			String encodedHash = URLEncoder.encode(new BigInteger(sha.digest()).toString(16), "UTF-8");

			String preventProxy = (BungeeCord.getInstance().config.isPreventProxyConnections()
					&& getSocketAddress() instanceof InetSocketAddress)
							? "&ip=" + URLEncoder.encode(getAddress().getAddress().getHostAddress(), "UTF-8")
							: "";

			String authURL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + encName
					+ "&serverId=" + encodedHash + preventProxy;

			// Proxy Check before auth
			//

			Callback<String> handler = new Callback<String>() {
				@Override
				public void done(String result, Throwable error) {
					try {
						if (error == null) {
							LoginResult obj = BungeeCord.getInstance().gson.fromJson(result, LoginResult.class);
							if (obj != null && obj.getId() != null) {
								loginProfile = obj;
								name = obj.getName();
								uniqueId = Util.getUUID(obj.getId());

								if (BetterBungee.getInstance().isSessionchache()) {
									BungeeCord.getInstance().getSessionCache().cacheSession(getSocketAddress(), obj);
								}
								finish();
								return;
							}
							if (BetterBungee.getInstance().isProtection()) {
								if (!list.containswhitelist(list.getRealAdress(ch))) {
									list.addlimit(list.getRealAdress(ch), 1);
								}
							}
							disconnect(bungee.getTranslation("offline_mode_player"));
						} else {
							disconnect(bungee.getTranslation("mojang_fail"));
							bungee.getLogger().log(Level.SEVERE,
									"Error authenticating " + getName() + " with minecraft.net", error);
						}

					} catch (Throwable e) {
						if (BetterBungee.getInstance().isDevdebugmode()) {
							e.printStackTrace();
						}
					}
				}
			};
			thisState = State.FINISHING;
			HttpClient.get(authURL, ch.getHandle().eventLoop(), handler);

		} catch (Throwable e) {
			cancelcrash("Crypto Crasher? xD");
			if (BetterBungee.getInstance().isDevdebugmode()) {
				e.printStackTrace();
			}
			return;
		}
	}

	private void finish() {
		if (isOnlineMode()) {

			// Check for multiple connections
			// We have to check for the old name first
			ProxiedPlayer oldName = bungee.getPlayer(getName());
			if (oldName != null) {
				// TODO See #1218
				disconnect(bungee.getTranslation("already_connected_proxy"));
			}
			// And then also for their old UUID
			ProxiedPlayer oldID = bungee.getPlayer(getUniqueId());
			if (oldID != null) {
				// TODO See #1218
				disconnect(bungee.getTranslation("already_connected_proxy"));
			}
		} else {
			// In offline mode the existing user stays and we kick the new one
			ProxiedPlayer oldName = bungee.getPlayer(getName());
			if (oldName != null) {
				// TODO See #1218
				disconnect(bungee.getTranslation("already_connected_proxy"));
				return;
			}
		}

        offlineId = UUID.nameUUIDFromBytes( ( "OfflinePlayer:" + getName() ).getBytes( Charsets.UTF_8 ) );
        if ( uniqueId == null )
        {
            uniqueId = offlineId;
        }

        if ( getVersion() >= ProtocolConstants.MINECRAFT_1_19_1 && getVersion() < ProtocolConstants.MINECRAFT_1_19_3 ) {
            if ( getVersion() >= ProtocolConstants.MINECRAFT_1_19_1 )
            {
                boolean secure = false;
                try
                {
                    secure = EncryptionUtil.check( loginRequest.getPublicKey(), uniqueId );
                } catch ( GeneralSecurityException ex )
                {
                }

                if ( !secure )
                {
                    disconnect( bungee.getTranslation( "secure_profile_invalid" ) );
                    return;
                }
            }
        }



		Callback<LoginEvent> complete = new Callback<LoginEvent>() {
			@Override
			public void done(LoginEvent result, Throwable error) {
				if (result.isCancelled()) {
                    BaseComponent[] reason = result.getCancelReasonComponents();
                    disconnect( ( reason != null ) ? reason : TextComponent.fromLegacyText( bungee.getTranslation( "kick_message" ) ) );
					return;
				}

				if (ch.isClosed()) {
					return;
				}

				ch.getHandle().eventLoop().execute(new Runnable() {
					@Override
					public void run() {
						if (!ch.isClosing()) {
							String ip = list.getRealAdress(ch);

							UserConnection userCon = new UserConnection(bungee, ch, getName(), InitialHandler.this);

							userCon.setCompressionThreshold(BungeeCord.getInstance().config.getCompressionThreshold());
							userCon.init();

                            unsafe.sendPacket( new LoginSuccess( getUniqueId(), getName(), ( loginProfile == null ) ? null : loginProfile.getProperties() ) );
							ch.setProtocol(Protocol.GAME);

							ch.getHandle().pipeline().get(HandlerBoss.class)
									.setHandler(new UpstreamBridge(bungee, userCon));

							bungee.getPluginManager().callEvent(new PostLoginEvent(userCon));

							ServerInfo server;

							boolean fastjoin = list.containswhitelist(ip);

							if (!BetterBungee.getInstance().isLimbomode() || fastjoin) {
								if (bungee.getReconnectHandler() != null) {
									server = bungee.getReconnectHandler().getServer(userCon);
								} else {
									server = AbstractReconnectHandler.getForcedHost(InitialHandler.this);
								}
								if (server == null) {
									server = bungee.getServerInfo(listener.getDefaultServer());
								}
								userCon.connect(server, null, true, ServerConnectEvent.Reason.JOIN_PROXY);
							} else {
								server = BetterBungee.getInstance().getLimboserver();
								userCon.connect(server, null, true, ServerConnectEvent.Reason.JOIN_PROXY);
							}

							if (Blacklist.getInstance().isProtection() && BetterBungee.getInstance().isBotchecks() && !fastjoin) {
								if (!list.getJoinedlist().contains(ip)) {
									list.getJoinedlist().add(ip);
								}
							}

//							if (isOnlineMode()) {
//								if (Blacklist.getInstance().isProtection()) {
//									if (!list.containswhitelist(ip)) {
//										list.addWhitelist(ip);
//										NotifyManager.getInstance().addmessage("§aAdded §8- §e" + ip + " §8- §2Whitelist");
//									}
//								}
//							}
//							
//							userCon.connect(server, null, true, ServerConnectEvent.Reason.JOIN_PROXY);
//
//							thisState = State.FINISHED;
//						


							if (BetterBungee.getInstance().isFullproxyjoin()) {
								if (!BetterBungee.getInstance().getFullProxyJoinPermission().equals("none")) {
									if (!userCon.hasPermission(BetterBungee.getInstance().getFullProxyJoinPermission())) {
										int limit = BungeeCord.getInstance().config.getPlayerLimit();
										if (limit > 0 && bungee.getOnlineCount() >= limit) {
											disconnect(bungee.getTranslation("proxy_full"));
											return;
										}
									}
								}
							}
							
							IPChecker.getInstance().start(() -> {
								if (!fastjoin) {
									if (ch.isClosed()) {
										return;
									}
									
									if (!Blacklist.getInstance().isUnderattack()) {
										vpncheck(ip, userCon);
									}

									if (list.isBlacklisted(ip)) {
										ch.close();
										if (list.getJoinedlist().contains(ip)) {
											list.getJoinedlist().remove(ip);
										}
										return;
									}

									if (Blacklist.getInstance().isProtection()) {
										if (list.getJoinedlist().contains(ip)) {
											ch.close();
											list.getJoinedlist().remove(ip);
											if (Blacklist.getInstance().isUnderattack()) {
												Blacklist.getInstance().addBlacklist(ip);
											}
											list.removeWhitelist(ip);
											return;
										} else {
											if (!list.containswhitelist(ip)) {
												list.addWhitelist(ip);
												NotifyManager.getInstance().addmessage("§aAdded §8- §e" + ip + " §8- §2Whitelist");
											}
										}
									}

									if (BetterBungee.getInstance().isLimbomode()) {
										ServerInfo finalserver;
										if (bungee.getReconnectHandler() != null) {
											finalserver = bungee.getReconnectHandler().getServer(userCon);
										} else {
											finalserver = AbstractReconnectHandler.getForcedHost(InitialHandler.this);
										}
										if (finalserver == null) {
											finalserver = bungee.getServerInfo(listener.getDefaultServer());
										}
										userCon.connect(finalserver, null, true, ServerConnectEvent.Reason.LOBBY_FALLBACK);
									}
								} else {
									vpncheck(ip, userCon);
								}
							}, Blacklist.getInstance().isUnderattack() ? 750 : 2000);

//							if (BetterBungee.getInstance().isDenyVPNonJoin()) {
//								IPChecker.getInstance().start(() -> {
//									if (!IPChecker.getInstance().isipresidental(ip)) {
//										ProxiedPlayer player = userCon;
//										if (player != null) {
//											if (!player.hasPermission(BetterBungee.getInstance().getDenyVPNbypasspermission())) {
//												player.disconnect(TextComponent.fromLegacyText(BungeeCord.getInstance().getBetterBungee().getDenyVPNkickmessage()));
//												NotifyManager.getInstance().addmessage("§6Detected §8- §e" + player.getName() + " §8- §6VPN");
//											} else {
//												NotifyManager.getInstance().addmessage("§aDetected §8- §e" + player.getName() + " §8- §2VPN (bypassed)");
//											}
//										}
//										return;
//									}
//								});
//							}
						}
					}

					private void vpncheck(String ip, UserConnection userCon) {
						if (BetterBungee.getInstance().isDenyVPNonJoin()) {
							if (!IPChecker.getInstance().isipresidental(ip)) {
								ProxiedPlayer player = userCon;
								if (player != null) {
									if (!player
											.hasPermission(BetterBungee.getInstance().getDenyVPNbypasspermission())) {
										player.disconnect(TextComponent.fromLegacyText(
												BungeeCord.getInstance().getBetterBungee().getDenyVPNkickmessage()));
										NotifyManager.getInstance()
												.addmessage("§6Detected §8- §e" + player.getName() + " §8- §6VPN");
									} else {
										NotifyManager.getInstance().addmessage(
												"§aDetected §8- §e" + player.getName() + " §8- §2VPN (bypassed)");
									}
								}
								return;
							}
						}
					}

				});

			}
		};

		// fire login event
		bungee.getPluginManager().callEvent(new LoginEvent(InitialHandler.this, complete));

	}

	private ProxiedPlayer getPlayer(SocketAddress socket) {
		for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
			if (socket.equals(all.getSocketAddress())) {
				return all;
			}
		}
		return null;
	}

	public void relayMessage(PluginMessage input) throws Exception {
		if (input.getTag().equals("REGISTER") || input.getTag().equals("minecraft:register")) {
			String content = new String(input.getData(), StandardCharsets.UTF_8);

			for (String id : content.split("\0")) {
				Preconditions.checkState(registeredChannels.size() < 128, "Too many registered channels");
				Preconditions.checkArgument(id.length() < 128, "Channel name too long");

				registeredChannels.add(id);
			}
		} else if (input.getTag().equals("UNREGISTER") || input.getTag().equals("minecraft:unregister")) {
			String content = new String(input.getData(), StandardCharsets.UTF_8);

			for (String id : content.split("\0")) {
				registeredChannels.remove(id);
			}
		} else if (input.getTag().equals("MC|Brand") || input.getTag().equals("minecraft:brand")) {
			brandMessage = input;
		}
	}

	public Blacklist list = Blacklist.getInstance();

	@Override
	public void handle(LoginPayloadResponse response) throws Exception {
		disconnect("Unexpected custom LoginPayloadResponse");
	}

	@Override
	public void disconnect(String reason) {
		if (canSendKickMessage()) {
			disconnect(TextComponent.fromLegacyText(reason));
		} else {
			ch.close();
		}
	}

	@Override
	public void disconnect(final BaseComponent... reason) {
		if (canSendKickMessage()) {
			ch.delayedClose(new Kick(ComponentSerializer.toString(reason)));
		} else {
			ch.close();
		}
	}

	@Override
	public void disconnect(BaseComponent reason) {
		disconnect(new BaseComponent[] { reason });
	}

	@Override
	public String getName() {
		return (name != null) ? name : (loginRequest == null) ? null : loginRequest.getData();
	}

	@Override
	public int getVersion() {
		return (handshake == null) ? -1 : handshake.getProtocolVersion();
	}

	@Override
	public InetSocketAddress getAddress() {
		return (InetSocketAddress) getSocketAddress();
	}

	@Override
	public SocketAddress getSocketAddress() {
		return ch.getRemoteAddress();
	}

	@Override
	public String getProxyAddress() {
		return ch.getProxyAddress();
	}

	@Override
	public Unsafe unsafe() {
		return unsafe;
	}

	@Override
	public void setOnlineMode(boolean onlineMode) {
//		Preconditions.checkState(thisState == State.USERNAME, "Can only set online mode status whilst state is username");
		this.onlineMode = onlineMode;
	}

	@Override
	public void setUniqueId(UUID uuid) {
//		Preconditions.checkState(thisState == State.USERNAME, "Can only set uuid while state is username");
//		Preconditions.checkState(!onlineMode, "Can only set uuid when online mode is false");
		this.uniqueId = uuid;
	}

	@Override
	public String getUUID() {
		return uniqueId.toString().replace("-", "");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');

		String currentName = getName();
		if (currentName != null) {
			sb.append(currentName);
			sb.append(',');
		}

		sb.append(getSocketAddress());
		sb.append("] <-> InitialHandler");

		return sb.toString();
	}

	@Override
	public boolean isConnected() {
		return !ch.isClosed();
	}
}
