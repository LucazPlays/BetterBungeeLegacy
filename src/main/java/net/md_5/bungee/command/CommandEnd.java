package net.md_5.bungee.command;

import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.*;
import com.google.common.base.*;

public class CommandEnd extends Command
{
    public CommandEnd() {
        super("end", "bungeecord.command.end", new String[0]);
    }
    
    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            BungeeCord.getInstance().stop();
        } else {
            BungeeCord.getInstance().stop( ChatColor.translateAlternateColorCodes( '&', Joiner.on( ' ' ).join( args ) ) );
        }
    }
}
