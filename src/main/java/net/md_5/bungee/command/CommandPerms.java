package net.md_5.bungee.command;

import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.*;
import java.util.*;

public class CommandPerms extends Command
{
    public CommandPerms() {
        super("perms");
    }
    
    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Set<String> permissions = new HashSet<String>();
        for (final String group : sender.getGroups()) {
            permissions.addAll(ProxyServer.getInstance().getConfigurationAdapter().getPermissions(group));
        }
        sender.sendMessage(ProxyServer.getInstance().getTranslation("command_perms_groups", Util.csv(sender.getGroups())));
        for (final String permission : permissions) {
            sender.sendMessage(ProxyServer.getInstance().getTranslation("command_perms_permission", permission));
        }
    }
}
