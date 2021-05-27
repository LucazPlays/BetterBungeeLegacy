// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.api.plugin;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.event.EventBus;

public class PluginManager {
	private final ProxyServer proxy;
	private final Yaml yaml;
	private final EventBus eventBus;
	private final Map<String, Plugin> plugins;
	private final Map<String, Command> commandMap;
	private Map<String, PluginDescription> toLoad;
	private final Multimap<Plugin, Command> commandsByPlugin;
	private final Multimap<Plugin, Listener> listenersByPlugin;

	public PluginManager(final ProxyServer proxy) {
		this.plugins = new LinkedHashMap<String, Plugin>();
		this.commandMap = new HashMap<String, Command>();
		this.toLoad = new HashMap<String, PluginDescription>();
		this.commandsByPlugin =  ArrayListMultimap.create();
		this.listenersByPlugin =  ArrayListMultimap.create();
		this.proxy = proxy;
		final Constructor yamlConstructor = new Constructor();
		final PropertyUtils propertyUtils = yamlConstructor.getPropertyUtils();
		propertyUtils.setSkipMissingProperties(true);
		yamlConstructor.setPropertyUtils(propertyUtils);
		this.yaml = new Yaml(yamlConstructor);
		this.eventBus = new EventBus(proxy.getLogger());
	}

	public void registerCommand(final Plugin plugin, final Command command) {
		this.commandMap.put(command.getName().toLowerCase(Locale.ROOT), command);
		for (final String alias : command.getAliases()) {
			this.commandMap.put(alias.toLowerCase(Locale.ROOT), command);
		}
		this.commandsByPlugin.put(plugin, command);
	}

	public void unregisterCommand(final Command command) {
		while (this.commandMap.values().remove(command)) {
		}
		this.commandsByPlugin.values().remove(command);
	}

	public void unregisterCommands(final Plugin plugin) {
		final Iterator<Command> it = this.commandsByPlugin.get(plugin).iterator();
		while (it.hasNext()) {
			final Command command = it.next();
			while (this.commandMap.values().remove(command)) {
			}
			it.remove();
		}
	}

	private Command getCommandIfEnabled(final String commandName, final CommandSender sender) {
		final String commandLower = commandName.toLowerCase(Locale.ROOT);
		if (sender instanceof ProxiedPlayer && this.proxy.getDisabledCommands().contains(commandLower)) {
			return null;
		}
		return this.commandMap.get(commandLower);
	}

	public boolean isExecutableCommand(final String commandName, final CommandSender sender) {
		return this.getCommandIfEnabled(commandName, sender) != null;
	}

	public boolean dispatchCommand(final CommandSender sender, final String commandLine) {
		return this.dispatchCommand(sender, commandLine, null);
	}

	public boolean dispatchCommand(final CommandSender sender, final String commandLine,
			final List<String> tabResults) {
		final String[] split = commandLine.split(" ", -1);
		if (split.length == 0 || split[0].isEmpty()) {
			return false;
		}
		final Command command = this.getCommandIfEnabled(split[0], sender);
		if (command == null) {
			return false;
		}
		if (!command.hasPermission(sender)) {
			if (tabResults == null) {
				sender.sendMessage(this.proxy.getTranslation("no_permission", new Object[0]));
			}
			return true;
		}
		final String[] args = Arrays.copyOfRange(split, 1, split.length);
		try {
			if (tabResults == null) {
				if (this.proxy.getConfig().isLogCommands()) {
					this.proxy.getLogger().log(Level.INFO, "{0} executed command: /{1}",
							new Object[] { sender.getName(), commandLine });
				}
				command.execute(sender, args);
			} else if (commandLine.contains(" ") && command instanceof TabExecutor) {
				for (final String s : ((TabExecutor) command).onTabComplete(sender, args)) {
					tabResults.add(s);
				}
			}
		} catch (Exception ex) {
			sender.sendMessage(ChatColor.RED
					+ "An internal error occurred whilst executing this command, please check the console log for details.");
			ProxyServer.getInstance().getLogger().log(Level.WARNING, "Error in dispatching command", ex);
		}
		return true;
	}

	public List<String> tabCompleteCommand(final CommandSender sender, final String commandLine) {
		final List<String> suggestions = new ArrayList<String>();
		if (commandLine.indexOf(32) == -1) {
			for (final Command command : this.commandMap.values()) {
				if (command.getName().startsWith(commandLine)) {
					final String permission = command.getPermission();
					if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
						continue;
					}
					suggestions.add(command.getName());
				}
			}
		} else {
			this.dispatchCommand(sender, commandLine, suggestions);
		}
		return suggestions;
	}

	public Collection<Plugin> getPlugins() {
		return this.plugins.values();
	}

	public Plugin getPlugin(final String name) {
		return this.plugins.get(name);
	}

	public void loadPlugins() {
		final Map<PluginDescription, Boolean> pluginStatuses = new HashMap<PluginDescription, Boolean>();
		for (final Map.Entry<String, PluginDescription> entry : this.toLoad.entrySet()) {
			final PluginDescription plugin = entry.getValue();
			if (!this.enablePlugin(pluginStatuses, new Stack<PluginDescription>(), plugin)) {
				ProxyServer.getInstance().getLogger().log(Level.WARNING, "Failed to enable {0}", entry.getKey());
			}
		}
		this.toLoad.clear();
		this.toLoad = null;
	}

	public void enablePlugins() {
		for (final Plugin plugin : this.plugins.values()) {
			try {
				plugin.onEnable();
				ProxyServer.getInstance().getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}",
						new Object[] { plugin.getDescription().getName(), plugin.getDescription().getVersion(),
								plugin.getDescription().getAuthor() });
			} catch (Throwable t) {
				ProxyServer.getInstance().getLogger().log(Level.WARNING,
						"Exception encountered when loading plugin: " + plugin.getDescription().getName(), t);
			}
		}
	}

	private boolean enablePlugin(final Map<PluginDescription, Boolean> pluginStatuses,
			final Stack<PluginDescription> dependStack, final PluginDescription plugin) {
		if (pluginStatuses.containsKey(plugin)) {
			return pluginStatuses.get(plugin);
		}
		final Set<String> dependencies = new HashSet<String>();
		dependencies.addAll(plugin.getDepends());
		dependencies.addAll(plugin.getSoftDepends());
		boolean status = true;
		for (final String dependName : dependencies) {
			final PluginDescription depend = this.toLoad.get(dependName);
			Boolean dependStatus = (depend != null) ? pluginStatuses.get(depend) : Boolean.FALSE;
			if (dependStatus == null) {
				if (dependStack.contains(depend)) {
					final StringBuilder dependencyGraph = new StringBuilder();
					for (final PluginDescription element : dependStack) {
						dependencyGraph.append(element.getName()).append(" -> ");
					}
					dependencyGraph.append(plugin.getName()).append(" -> ").append(dependName);
					ProxyServer.getInstance().getLogger().log(Level.WARNING, "Circular dependency detected: {0}",
							dependencyGraph);
					status = false;
				} else {
					dependStack.push(plugin);
					dependStatus = this.enablePlugin(pluginStatuses, dependStack, depend);
					dependStack.pop();
				}
			}
			if (dependStatus == Boolean.FALSE && plugin.getDepends().contains(dependName)) {
				ProxyServer.getInstance().getLogger().log(Level.WARNING, "{0} (required by {1}) is unavailable",
						new Object[] { String.valueOf(dependName), plugin.getName() });
				status = false;
			}
			if (!status) {
				break;
			}
		}
		if (status) {
			try {
				@SuppressWarnings("resource")
				final URLClassLoader loader = new PluginClassloader(new URL[] { plugin.getFile().toURI().toURL() });
				final Class<?> main = loader.loadClass(plugin.getMain());
				final Plugin clazz = (Plugin) main.getDeclaredConstructor((Class<?>[]) new Class[0])
						.newInstance(new Object[0]);
				clazz.init(this.proxy, plugin);
				this.plugins.put(plugin.getName(), clazz);
				clazz.onLoad();
				ProxyServer.getInstance().getLogger().log(Level.INFO, "Loaded plugin {0} version {1} by {2}",
						new Object[] { plugin.getName(), plugin.getVersion(), plugin.getAuthor() });
			} catch (Throwable t) {
				this.proxy.getLogger().log(Level.WARNING, "Error enabling plugin " + plugin.getName(), t);
			}
		}
		pluginStatuses.put(plugin, status);
		return status;
	}

	public void detectPlugins(final File folder) {
		Preconditions.checkNotNull(folder, (Object) "folder");
		Preconditions.checkArgument(folder.isDirectory(), (Object) "Must load from a directory");
		for (final File file : folder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".jar")) {
				try (final JarFile jar = new JarFile(file)) {
					JarEntry pdf = jar.getJarEntry("bungee.yml");
					if (pdf == null) {
						pdf = jar.getJarEntry("plugin.yml");
					}
					Preconditions.checkNotNull(pdf, (Object) "Plugin must have a plugin.yml or bungee.yml");
					try (final InputStream in = jar.getInputStream(pdf)) {
						final PluginDescription desc = this.yaml.loadAs(in, PluginDescription.class);
						Preconditions.checkNotNull(desc.getName(), "Plugin from %s has no name", file);
						Preconditions.checkNotNull(desc.getMain(), "Plugin from %s has no main", file);
						desc.setFile(file);
						this.toLoad.put(desc.getName(), desc);
					}
				} catch (Exception ex) {
					ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not load plugin from file " + file,
							ex);
				}
			}
		}
	}

	public <T extends Event> T callEvent(final T event) {
		Preconditions.checkNotNull(event, (Object) "event");
		final long start = System.nanoTime();
		this.eventBus.post(event);
		event.postCall();
		final long elapsed = System.nanoTime() - start;
		if (elapsed > 250000000L) {
			ProxyServer.getInstance().getLogger().log(Level.WARNING, "Event {0} took {1}ms to process!",
					new Object[] { event, elapsed / 1000000L });
		}
		return event;
	}

	public void registerListener(final Plugin plugin, final Listener listener) {
		for (final Method method : listener.getClass().getDeclaredMethods()) {
			Preconditions.checkArgument(!method.isAnnotationPresent(Subscribe.class),
					"Listener %s has registered using deprecated subscribe annotation! Please update to @EventHandler.",
					listener);
		}
		this.eventBus.register(listener);
		if (plugin != null) {
			this.listenersByPlugin.put(plugin, listener);
		}
	}

	public void unregisterListener(final Listener listener) {
		this.eventBus.unregister(listener);
		this.listenersByPlugin.values().remove(listener);
	}

	public void unregisterListeners(final Plugin plugin) {
		final Iterator<Listener> it = this.listenersByPlugin.get(plugin).iterator();
		while (it.hasNext()) {
			this.eventBus.unregister(it.next());
			it.remove();
		}
	}

	public Collection<Map.Entry<String, Command>> getCommands() {
		return Collections
				.unmodifiableCollection((Collection<? extends Map.Entry<String, Command>>) this.commandMap.entrySet());
	}

	public PluginManager(final ProxyServer proxy, final Yaml yaml, final EventBus eventBus) {
		this.plugins = new LinkedHashMap<String, Plugin>();
		this.commandMap = new HashMap<String, Command>();
		this.toLoad = new HashMap<String, PluginDescription>();
		this.commandsByPlugin = ArrayListMultimap.create();
		this.listenersByPlugin = ArrayListMultimap.create();
		this.proxy = proxy;
		this.yaml = yaml;
		this.eventBus = eventBus;
	}
}
