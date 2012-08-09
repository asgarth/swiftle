package org.swiftle.network.connection;

public enum Protocol {

	FTP("FTP", "./resources/themes/ftp.png"), SFTP("SFTP", "./resources/themes/sftp.png"), SAMBA("SAMBA",
			"./resources/themes/samba.png");

	private final String text;
	private final String image;

	private Protocol(final String text, final String image) {
		this.text = text;
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public String toString() {
		return text;
	}

}
