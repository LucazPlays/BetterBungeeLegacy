package net.md_5.bungee.conf;

import com.google.common.base.Charsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

public class YamlConfig2 {

	/**
	 * The default tab list options available for picking.
	 */
	@RequiredArgsConstructor
	private enum DefaultTabList {

		GLOBAL(), GLOBAL_PING(), SERVER();
	}

	private final Yaml yaml;
	private Map<String, Object> config;
	private final File file = new File("betterbungee.yml");

	public YamlConfig2() {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(options);
	}

	public String getPrefix() {
		return get("permissions.default", null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public YamlConfig2 load() {
		try {
			file.createNewFile();

			try (InputStream is = new FileInputStream(file)) {
				try {
					config = (Map) yaml.load(is);
				} catch (YAMLException ex) {
					throw new RuntimeException(
							"Invalid configuration encountered - this is a configuration error and NOT a bug! Please attempt to fix the error or ask Luca zPlays#9059 for help.",ex);
				}
			}

			
			

            set( "permissions.default", Arrays.asList("betterbungee.settings.prefix", "&8•● &a&lF&2&lB &8➢ "));
			
			
			
			
			
			
			
			
			if (config == null) {
				config = new CaseInsensitiveMap<>();
			} else {
				config = new CaseInsensitiveMap<>(config);
			}
		} catch (IOException ex) {
			throw new RuntimeException("Could not load configuration!", ex);
		}
		return this;
	}

	@SuppressWarnings("unused")
	private <T> T get(String path, T def) {
		return get(path, def, config);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> T get(String path, T def, Map submap) {
		int index = path.indexOf('.');
		if (index == -1) {
			Object val = submap.get(path);
			if (val == null && def != null) {
				val = def;
				submap.put(path, def);
				save();
			}
			return (T) val;
		} else {
			String first = path.substring(0, index);
			String second = path.substring(index + 1, path.length());
			Map sub = (Map) submap.get(first);
			if (sub == null) {
				sub = new LinkedHashMap();
				submap.put(first, sub);
			}
			return get(second, def, sub);
		}
	}

	private void set(String path, Object val) {
		set(path, val, config);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void set(String path, Object val, Map submap) {
		int index = path.indexOf('.');
		if (index == -1) {
			if (val == null) {
				submap.remove(path);
			} else {
				submap.put(path, val);
			}
			save();
		} else {
			String first = path.substring(0, index);
			String second = path.substring(index + 1, path.length());
			Map sub = (Map) submap.get(first);
			if (sub == null) {
				sub = new LinkedHashMap();
				submap.put(first, sub);
			}
			set(second, val, sub);
		}
	}

	private void save() {
		try {
			try (Writer wr = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
				yaml.dump(config, wr);
			}
		} catch (IOException ex) {
			ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not save config", ex);
		}
	}
}
