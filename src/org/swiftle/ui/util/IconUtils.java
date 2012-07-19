package org.swiftle.ui.util;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.snow.util.cache.ImageCache;
import org.swiftle.network.Entry;
import org.swiftle.util.FileUtils;

public class IconUtils {

	/** Utility class, prevent instantiation */
	private IconUtils() { }

	public static Image getIcon( final Entry entry ) {
		if( entry.isDirectory() )
			return getDirectoryIcon();

		final Program program = Program.findProgram( FileUtils.getFilenameExtension( entry.getName() ) );
		if( program == null || program.getImageData() == null )
			return ImageCache.getInstance().getImage( "./resources/themes/unknown.png" );

		return ImageCache.getInstance().getIcon( program );
	}

	public static Image getDirectoryIcon() {
		return ImageCache.getInstance().getImage( "./resources/themes/folder.png" );
	}

}
