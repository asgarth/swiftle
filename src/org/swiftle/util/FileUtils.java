package org.swiftle.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Arrays;


public class FileUtils {

	/** Utility class, prevent instantiation */
	private FileUtils() { }


	public static String readFileAsString( final File file ) {
		try {
			return readStringFromBuffer( new BufferedReader( new FileReader( file ) ) );

		} catch( FileNotFoundException e ) {
			return null;
		}
	}

	public static String readStreamAsString( final InputStream stream ) {
		return readStringFromBuffer( new BufferedReader( new InputStreamReader( stream ) ) );
	}

	public static String readStringFromBuffer( final BufferedReader reader ) {
		final StringBuilder stringBuilder = new StringBuilder();

		try {
			String line;
			while( ( line = reader.readLine() ) != null )
				stringBuilder.append( line ).append( "\n" );

		} catch( IOException e ) {
			return null;
		}

		return stringBuilder.toString();
	}

	public static byte[] readFileAsByteArray( final File file ) throws IOException {
		final InputStream is = new FileInputStream( file );

		// get the size of the file
		final long length = file.length();

		if( length > Integer.MAX_VALUE )
			throw new IOException( "File is too large for an array" );

		// create the byte array to hold the data
		final byte[] bytes = new byte[( int ) length];

		// read in the bytes
		int offset = 0;
		int numRead = 0;
		while( offset < bytes.length && ( numRead = is.read( bytes, offset, bytes.length - offset ) ) >= 0 ) {
			offset += numRead;
		}

		// ensure all the bytes have been read in
		if( offset < bytes.length )
			throw new IOException( "Could not completely read file " + file.getName() );

		// close the input stream and return bytes
		is.close();

		return bytes;
	}
	
	public static byte[] readStreamAsByteArray( final InputStream stream, final long expectedSize, final int increment ) throws IOException {
		// check for valid expected size
		if( expectedSize > Integer.MAX_VALUE )
			throw new IllegalArgumentException( "Expected size is too large for an array" );

		// create the byte array to hold the data
		byte[] bytes = new byte[( int ) expectedSize];

		// read in the bytes
		int offset = 0;
		int numRead = 0;
		while( ( numRead = stream.read( bytes, offset, bytes.length - offset ) ) >= 0 ) {
			offset += numRead;
			if( offset == bytes.length )
				bytes = Arrays.copyOf( bytes, bytes.length + increment );
		}

		return Arrays.copyOf( bytes, offset );
	}

	public static String md5( final File file ) {
		try {
			final InputStream fin = new FileInputStream( file );
			final MessageDigest md5 = MessageDigest.getInstance( "MD5" );

			// read file into buffer and update digest
			byte[] buffer = new byte[1024];
			int read;
			while( ( read = fin.read( buffer ) ) > 0 ) {
				md5.update( buffer, 0, read );
			}
			fin.close();

			// format output
			byte[] digest = md5.digest();
			if( digest == null )
				return null;
			String hash = "";
			for( int i = 0; i < digest.length; i++ ) {
				hash += Integer.toString( ( digest[i] & 0xff ) + 0x100, 16 ).substring( 1 );
			}

			return hash;
		} catch( Exception e ) {
			return null;
		}
	}

	public static void copy( final File source, final File dest ) throws IOException {
		final FileChannel inChannel = new FileInputStream( source ).getChannel();
		final FileChannel outChannel = new FileOutputStream( dest ).getChannel();
		try {
			// magic number for Windows, 64Mb - 32Kb)
			final int maxCount = ( 64 * 1024 * 1024 ) - ( 32 * 1024 );
			final long size = inChannel.size();
			long position = 0;
			while( position < size )
				position += inChannel.transferTo( position, maxCount, outChannel );

		} finally {
			if( inChannel != null )
				inChannel.close();
			if( outChannel != null )
				outChannel.close();
		}
	}

	public static byte[] streamToByteArray( final InputStream source ) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// transfer bytes from in to out
		final byte[] buf = new byte[4096];
		int len;
		while( ( len = source.read( buf ) ) > 0 )
			baos.write( buf, 0, len );

		final byte[] out = baos.toByteArray();

		source.close();
		baos.close();

		return out;
	}

	public static String getFilenameExtension( final String filename ) {
		final int dot = filename.lastIndexOf( '.' );
		if( dot == -1 )
			return "";

		return filename.substring( dot + 1 );
	}

}
