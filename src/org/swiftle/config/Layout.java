package org.swiftle.config;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

@Root(name = "layout")
public class Layout {

	@ElementMap(entry = "property", key = "name", attribute = true, inline = true)
	private Map<String, String> map;

	public Layout() {
		map = new HashMap<String, String>();
		map.put("width", "960");
		map.put("height", "640");
	}

	public void put(final String key, final String value) {
		map.put(key, value);
	}

	public void put(final String key, final int value) {
		map.put(key, Integer.toString(value));
	}
	
	public void put(final String key, final boolean value) {
		map.put(key, value ? "true" : "false");
	}

	public String get(final String key) {
		return map.get(key);
	}

	public int getInt(final String key) {
		if (! map.containsKey(key))
			return 0;

		try {
			return Integer.parseInt(map.get(key));

		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public boolean getBoolean(final String key) {
		if (! map.containsKey(key))
			return false;

		return map.get(key).equalsIgnoreCase("true");
	}

}
