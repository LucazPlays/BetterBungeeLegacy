// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.api.plugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.InputStream;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.io.File;
import net.md_5.bungee.api.ProxyServer;

public class Plugin
{
    private PluginDescription description;
    private ProxyServer proxy;
    private File file;
    private Logger logger;
    private ExecutorService service;
    
    public org.slf4j.Logger getSLF4JLogger() {
        return LoggerFactory.getLogger(this.logger.getName());
    }
    
    public void onLoad() {
    }
    
    public void onEnable() {
    }
    
    public void onDisable() {
    }
    
    public final File getDataFolder() {
        return new File(this.getProxy().getPluginsFolder(), this.getDescription().getName());
    }
    
    public final InputStream getResourceAsStream(final String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }
    
    final void init(final ProxyServer proxy, final PluginDescription description) {
        this.proxy = proxy;
        this.description = description;
        this.file = description.getFile();
        this.logger = Logger.getLogger(description.getName());
    }
    
    @Deprecated
    public ExecutorService getExecutorService() {
        if (this.service == null) {
            final String name = (this.getDescription() == null) ? "unknown" : this.getDescription().getName();
            this.service = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(name + " Pool Thread #%1$d").setThreadFactory(new GroupedThreadFactory(this, name)).build());
        }
        return this.service;
    }
    
    public PluginDescription getDescription() {
        return this.description;
    }
    
    public ProxyServer getProxy() {
        return this.proxy;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public Logger getLogger() {
        return this.logger;
    }
}
