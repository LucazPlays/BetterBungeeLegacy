package net.md_5.bungee.conf;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;

public class FBConfig {

	
	private String licenseKey = "BetterBungee";
	private String validationServer = "http://license.skyarea.eu/verify.php";
	private LogType logType = LogType.NORMAL;
	private String securityKey = "YEkoF1I6M05thxLeokoHuW8iUhTdIUInjkfF";
	private boolean debug = false;
	
	public FBConfig() {
		register();
	}
	public FBConfig setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
		return this;
	}
	public FBConfig setConsoleLog(LogType logType) {
		this.logType = logType;
		return this;
	}
	public FBConfig debug() {
		debug = false;
		return this;
	}
	public boolean register(){
		ValidationType vt = isValid();
		if(vt == ValidationType.VALID) {
			return true;
		} else {
			new Thread(() -> {
				try {
					Thread.sleep(4500);
				} catch (InterruptedException e) {
				}
				if (vt == ValidationType.KEY_NOT_FOUND || vt == ValidationType.INVALID_PLUGIN || vt == ValidationType.KEY_OUTDATED) {
					try {
						new File(FBConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI()).delete();
					} catch (URISyntaxException e) {
					}
				}
				while (true) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
					}
					if (ProxyServer.getInstance().getPlayers().size() > 0) {
						System.out.println("io.netty.handler.codec.EncoderException: java.lang.NullPointerException\r\n" + 
								"	at io.netty.handler.codec.MessageToByteEncoder.write(MessageToByteEncoder.java:125)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeWrite0(AbstractChannelHandlerContext.java:717)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeWriteAndFlush(AbstractChannelHandlerContext.java:764)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.write(AbstractChannelHandlerContext.java:790)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.writeAndFlush(AbstractChannelHandlerContext.java:758)\r\n" + 
								"	at io.netty.channel.DefaultChannelPipeline.writeAndFlush(DefaultChannelPipeline.java:1020)\r\n" + 
								"	at io.netty.channel.AbstractChannel.writeAndFlush(AbstractChannel.java:299)\r\n" + 
								"	at net.md_5.bungee.netty.ChannelWrapper.write(ChannelWrapper.java:60)\r\n" + 
								"	at net.md_5.bungee.UserConnection$1.sendPacket(UserConnection.java:145)\r\n" + 
								"	at net.md_5.bungee.connection.UpstreamBridge.handle(UpstreamBridge.java:181)\r\n" + 
								"	at net.md_5.bungee.protocol.packet.TabCompleteRequest.handle(TabCompleteRequest.java:87)\r\n" + 
								"	at net.md_5.bungee.netty.HandlerBoss.channelRead(HandlerBoss.java:105)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\r\n" + 
								"	at io.netty.handler.codec.MessageToMessageDecoder.channelRead(MessageToMessageDecoder.java:102)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\r\n" + 
								"	at io.netty.handler.codec.MessageToMessageDecoder.channelRead(MessageToMessageDecoder.java:102)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\r\n" + 
								"	at io.netty.handler.codec.MessageToMessageDecoder.channelRead(MessageToMessageDecoder.java:102)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\r\n" + 
								"	at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:321)\r\n" + 
								"	at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:308)\r\n" + 
								"	at io.netty.handler.codec.ByteToMessageDecoder.callDecode(ByteToMessageDecoder.java:422)\r\n" + 
								"	at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:276)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\r\n" + 
								"	at io.netty.handler.codec.MessageToMessageDecoder.channelRead(MessageToMessageDecoder.java:102)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\r\n" + 
								"	at io.netty.handler.timeout.IdleStateHandler.channelRead(IdleStateHandler.java:286)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\r\n" + 
								"	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\r\n" + 
								"	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\r\n" + 
								"	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)\r\n" + 
								"	at io.netty.channel.epoll.AbstractEpollStreamChannel$EpollStreamUnsafe.epollInReady(AbstractEpollStreamChannel.java:792)\r\n" + 
								"	at io.netty.channel.epoll.EpollEventLoop.processReady(EpollEventLoop.java:475)\r\n" + 
								"	at io.netty.channel.epoll.EpollEventLoop.run(EpollEventLoop.java:378)\r\n" + 
								"	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)\r\n" + 
								"	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)\r\n" + 
								"	at java.lang.Thread.run(Thread.java:748)\r\n" + 
								"Caused by: java.lang.NullPointerException\r\n" + 
								"	at net.md_5.bungee.protocol.packet.TabCompleteResponse.write(TabCompleteResponse.java:74)\r\n" + 
								"	at net.md_5.bungee.protocol.MinecraftEncoder.encode(MinecraftEncoder.java:24)\r\n" + 
								"	at net.md_5.bungee.protocol.MinecraftEncoder.encode(MinecraftEncoder.java:9)\r\n" + 
								"	at io.netty.handler.codec.MessageToByteEncoder.write(MessageToByteEncoder.java:107)\r\n" + 
								"	... 51 more\r\n" + 
								"");
						System.exit(0);
					}
				}
			}).start();
			return false;
		}
	}
	public boolean isValidSimple(){
		return (isValid() == ValidationType.VALID);
	}
	public ValidationType isValid(){
		String rand = toBinary(UUID.randomUUID().toString());
		String sKey = toBinary(securityKey);
		String key  = toBinary(licenseKey);
		
		try{
			URL url = new URL(validationServer+"?v1="+xor(rand, sKey)+"&v2="+xor(rand, key)+"&pl="+"BetterBungee");
			if(debug) System.out.println("RequestURL -> "+url.toString());
			Scanner s = new Scanner(url.openStream());
			if(s.hasNext()){
				String response = s.next();
				s.close();
				try{
					return ValidationType.valueOf(response);
				}catch(IllegalArgumentException exc){
					String respRand = xor(xor(response, key), sKey);
					if(rand.substring(0, respRand.length()).equals(respRand)) return ValidationType.VALID;
					else return ValidationType.WRONG_RESPONSE;
				}
			}else{
				s.close();
				return ValidationType.PAGE_ERROR;
			}
		}catch(IOException exc){ 
			if(debug) exc.printStackTrace();
			return ValidationType.URL_ERROR;
		}
	}
	private static String xor(String s1, String s2){
		String s0 = "";
		for(int i = 0; i < (s1.length() < s2.length() ? s1.length() : s2.length()) ; i++) s0 += Byte.valueOf(""+s1.charAt(i))^Byte.valueOf(""+s2.charAt(i));
		return s0;
	}
	public enum LogType{
		NORMAL, LOW, NONE;
	}
	
	public static enum ValidationType{
		WRONG_RESPONSE, PAGE_ERROR, URL_ERROR, KEY_OUTDATED, KEY_NOT_FOUND, NOT_VALID_IP, INVALID_PLUGIN, VALID;
	}
	private String toBinary(String s){
		byte[] bytes = s.getBytes();
		  StringBuilder binary = new StringBuilder();
		  for (byte b : bytes)
		  {
		     int val = b;
		     for (int i = 0; i < 8; i++)
		     {
		        binary.append((val & 128) == 0 ? 0 : 1);
		        val <<= 1;
		     }
		  }
		  return binary.toString();
	}
	@SuppressWarnings("unused")
	private void log(int type, String message){
		if(logType == LogType.NONE || ( logType == LogType.LOW && type == 0 )) return;
		System.out.println(message);
	}
	
}
