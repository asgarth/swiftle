package org.swiftle.config;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

@Root(name = "connection")
public class Server {

	@Element(required=false)
	private String group;

	@ElementMap(entry = "property", key = "name", attribute = true, inline = true)
	private Map<String, String> map;

	public Server() {
		map = new HashMap<String, String>();
	}
	
	public Server(final String protocol) {
		map = new HashMap<String, String>();
		map.put("protocol", protocol);
	}
			
	public Server(final String protocol, final String host, final String port, final String user, final String password) {
		this(protocol);
		map.put("hostname", host);
		map.put("port", port);
		map.put("username", user);
		map.put("password", password);
	}

	public String getGroup() {
		return group;
	}
	public void setGroup(final String group) {
		this.group = group;
	}

	public String get(final String key) {
		return map.get(key);
	}
	
	public void set(final String key, final String value) {
		map.put(key, value);
	}

}
