package org.swiftle.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snow.util.Platform;
import org.swiftle.util.Constants;

@Root(name = "settings")
public class Configuration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class.getName());

	private static final String configFilename = Constants.getAppDir() + Platform.getPathSeparator() + Constants.SWIFTLE.toLowerCase() + ".xml";

	private static Configuration instance = new Configuration();

	@Element
	private Layout layout;
	
	@ElementMap(entry = "property", key = "name", attribute = true, inline = true, required=false)
	private Map<String, Server> serverMap;

	public static Configuration getInstance() {
		return instance;
	}

	public Configuration() {
		layout = new Layout();
		serverMap = new HashMap<String, Server>();
	}

	public Layout getLayout() {
		return layout;
	}
	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public void addServer(final String name, final Server server) {
		serverMap.put(name, server);
	}
	public Map<String, Server> getServerMap() {
		return serverMap;
	}
	public void setServerMap(final Map<String, Server> serverMap) {
		this.serverMap = serverMap;
	}

	public static boolean load() {
		final File configFile = new File(configFilename);
		if (! configFile.exists())
			return false;

		try {
			final Serializer serializer = new Persister();
			instance = serializer.read(Configuration.class, configFile);

			return true;

		} catch (Exception e) {
			logger.error("Error serializing configuration to file", e);
		}

		return false;
	}

	public static boolean save() {
		try {
			final Serializer serializer = new Persister();
			serializer.write(instance, new File(configFilename));

			return true;

		} catch (Exception e) {
			logger.error("Error serializing configuration to file", e);
		}

		return false;
	}

}
