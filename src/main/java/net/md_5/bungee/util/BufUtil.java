package net.md_5.bungee.util;

import io.netty.buffer.*;

public class BufUtil
{
    public static String dump(final ByteBuf buf, final int maxLen) {
        return ByteBufUtil.hexDump(buf, 0, Math.min(buf.writerIndex(), maxLen));
    }
}
