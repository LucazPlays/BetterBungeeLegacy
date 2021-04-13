package net.md_5.bungee.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.Multimap;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginClassloader;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;

public class FieldUtils {

	public static Plugin findPlugin(String pluginname) {
		for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
			if (plugin.getDescription().getName().equalsIgnoreCase(pluginname))
				return plugin;
		}
		return null;
	}

	public static File findFile(String pluginname) {
		File folder = ProxyServer.getInstance().getPluginsFolder();
		if (folder.exists()) {
			byte b;
			int i;
			File[] arrayOfFile;
			for (i = (arrayOfFile = folder.listFiles()).length, b = 0; b < i;) {
				File file = arrayOfFile[b];
				if (file.isFile() && file.getName().endsWith(".jar"))
					try {
						Exception exception2, exception1 = null;
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
				b++;
			}
		}
		return new File(folder, String.valueOf(pluginname) + ".jar");
	}

	public static void unloadPlugin(Plugin plugin) {
		PluginManager pluginmanager = ProxyServer.getInstance().getPluginManager();
		ClassLoader pluginclassloader = plugin.getClass().getClassLoader();
		try {
			plugin.onDisable();
			byte b;
			int i;
			Handler[] arrayOfHandler;
			for (i = (arrayOfHandler = plugin.getLogger().getHandlers()).length, b = 0; b < i;) {
				Handler handler = arrayOfHandler[b];
				handler.close();
				b++;
			}
		} catch (Throwable t) {
			severe("Exception disabling plugin", t, plugin.getDescription().getName());
		}
		pluginmanager.unregisterListeners(plugin);
		pluginmanager.unregisterCommands(plugin);
		ProxyServer.getInstance().getScheduler().cancel(plugin);
		plugin.getExecutorService().shutdownNow();
		for (Thread thread : Thread.getAllStackTraces().keySet()) {
			if (thread.getClass().getClassLoader() == pluginclassloader)
				try {
					thread.interrupt();
					thread.join(2000L);
					if (thread.isAlive())
						thread.stop();
				} catch (Throwable t) {
					severe("Failed to stop thread that belong to plugin", t, plugin.getDescription().getName());
				}
		}
		PluginmanagerEventBus.completeIntents(plugin);
		try {
			Map<String, Command> commandMap = Reflection.<Map<String, Command>>getFieldValue(pluginmanager,
					"commandMap");
			Iterator<Map.Entry<String, Command>> iterator = commandMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Command> entry = iterator.next();
				if (((Command) entry.getValue()).getClass().getClassLoader() == pluginclassloader)
					iterator.remove();
			}
		} catch (Throwable t) {
			severe("Failed to cleanup commandMap", t, plugin.getDescription().getName());
		}
		try {
			Map<String, Plugin> pluginsMap = Reflection.<Map<String, Plugin>>getFieldValue(pluginmanager, "plugins");
			pluginsMap.values().remove(plugin);
			Multimap<Plugin, Command> commands = Reflection.<Multimap<Plugin, Command>>getFieldValue(pluginmanager,
					"commandsByPlugin");
			commands.removeAll(plugin);
			Multimap<Plugin, Listener> listeners = Reflection.<Multimap<Plugin, Listener>>getFieldValue(pluginmanager,
					"listenersByPlugin");
			listeners.removeAll(plugin);
		} catch (Throwable t) {
			severe("Failed to cleanup bungee internal maps from plugin refs", t, plugin.getDescription().getName());
		}
		if (pluginclassloader instanceof URLClassLoader)
			try {
				((URLClassLoader) pluginclassloader).close();
			} catch (Throwable t) {
				severe("Failed to close the classloader for plugin", t, plugin.getDescription().getName());
			}
		Set<PluginClassloader> allLoaders = Reflection
				.<Set<PluginClassloader>>getStaticFieldValue(PluginClassloader.class, "allLoaders");
		allLoaders.remove(pluginclassloader);
	}

	public static boolean loadPlugin(final File pluginfile) {
		try {
			Throwable t2 = null;
			try {
				final JarFile jar = new JarFile(pluginfile);
				try {
					JarEntry pdf = jar.getJarEntry("bungee.yml");
					if (pdf == null) {
						pdf = jar.getJarEntry("plugin.yml");
					}
					try {
						final InputStream in = jar.getInputStream(pdf);
						try {
							final PluginDescription desc = new Yaml().loadAs(in, PluginDescription.class);
							desc.setFile(pluginfile);
							final HashSet<String> plugins = new HashSet<String>();
							for (final Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
								plugins.add(plugin.getDescription().getName());
							}
							for (final String dependency : desc.getDepends()) {
								if (!plugins.contains(dependency)) {
									ProxyServer.getInstance().getLogger().log(Level.WARNING,
											"{0} (required by {1}) is unavailable",
											new Object[] { dependency, desc.getName() });
									return false;
								}
							}
							
							@SuppressWarnings("resource")
							URLClassLoader loader = (URLClassLoader) new PluginClassloader(new URL[] {pluginfile.toURI().toURL()});

							final Class<?> mainclazz = loader.loadClass(desc.getMain());
							
							final Plugin plugin2 = (Plugin) mainclazz.getDeclaredConstructor((Class<?>[]) new Class[0])
									.newInstance(new Object[0]);
							Reflection.invokeMethod((Object) plugin2, "init",
									new Object[] { ProxyServer.getInstance(), desc });
							final Map<String, Plugin> pluginsMap = (Map<String, Plugin>) Reflection
									.getFieldValue((Object) ProxyServer.getInstance().getPluginManager(), "plugins");
							pluginsMap.put(desc.getName(), plugin2);
							plugin2.onLoad();
							plugin2.onEnable();
							return true;
						} finally {
							if (in != null) {
								in.close();
							}
						}
					} finally {
						return false;
					}
				} finally {
					if (jar != null) {
						jar.close();
					}
				}
			} finally {
				if (t2 == null) {
					final Throwable t3 = null;
					t2 = t3;
				} else {
					final Throwable t3 = null;
					if (t2 != t3) {
						t2.addSuppressed(t3);
					}
				}
			}
		} catch (Throwable t) {
			severe("Failed to load plugin", t, pluginfile.getName());
			return false;
		}
	}

	static void severe(String message, Throwable t, String pluginname) {
		ProxyServer.getInstance().getLogger().log(Level.SEVERE, String.valueOf(message) + " " + pluginname, t);
	}
}
