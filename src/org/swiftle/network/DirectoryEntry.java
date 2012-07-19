package org.swiftle.network;

public class DirectoryEntry implements Entry {

	private final String name;

	private final String absolutePath;

	public DirectoryEntry( final String name, final String absolutePath ) {
		this.name = name;
		this.absolutePath = absolutePath;
	}

	public String getName() {
		return name;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public boolean isDirectory() {
		return true;
	}
	
	public boolean isFile() {
		return false;
	}

	public long size() {
		return 0;
	}
	
}
