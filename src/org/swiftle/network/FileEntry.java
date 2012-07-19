package org.swiftle.network;

public class FileEntry implements Entry {

	private final String name;

	private final String absolutePath;

	private final long size;

	public FileEntry( final String name, final String absolutePath, final long size ) {
		this.name = name;
		this.absolutePath = absolutePath;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public boolean isDirectory() {
		return false;
	}
	
	public boolean isFile() {
		return true;
	}

	public long size() {
		return size;
	}
	
}
