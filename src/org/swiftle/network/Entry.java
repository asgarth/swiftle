package org.swiftle.network;

public interface Entry {

	/** Returns the basename denoted by this entry. */
	public String getName();

	/** Returns the absolute path for this entry. */
	public String getAbsolutePath();

	/** Returns true if this entry is a directory, false if this entry denote a file. */
	public boolean isDirectory();
	
	/** Returns true if this entry is a file, false if this entry denote a directory. */
	public boolean isFile();

	/** Returns the size in byte of this entry. If this entry is a directory the size is always 0. */
	public long size();
	
}
