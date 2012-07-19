package org.swiftle.network;

import java.text.DecimalFormat;

public enum SizeUnit {

	B( 1, 1 ),
	KB( 2, 1024 ),
	MB( 3, 1024 ),
	GB( 4, 1024 );

	private final int factor;

	private final int order;

	private SizeUnit( final int order, final int factor ) {
		this.order = order;
		this.factor = factor;
	}

	public int factor() {
		return factor;
	}

	public int order() {
		return order;
	}

	/** Transform received input from an unit to another according to unit conversion factor defined. */
	public static double transform( final long size, final SizeUnit from, final SizeUnit to ) {
		if( size < 0 )
			throw new IllegalArgumentException( "Invalid file size: " + size );

		if( to.order() > from.order() )
			return size / ( double ) ( Math.pow( 1024, to.order() - from.order() ) );

		if( to.order() < from.order() )
			return size * ( double ) ( Math.pow( 1024, from.order() - to.order() ) );

		return size;
	}

	public static String asString( final double byteSize ) {
		if( byteSize < 0 )
			throw new IllegalArgumentException( "Invalid file size: " + byteSize );

		if( byteSize < 1024 )
			return format( byteSize, B );

		double reduced = byteSize / 1024;
		if( reduced < 1024 )
			return format( reduced, KB );

		reduced = reduced / 1024;
		if( reduced < 1024 )
			return format( reduced, MB );

		return format( reduced / 1024, GB );
	}

	private static String format( final double size, final SizeUnit unit ) {
		return new DecimalFormat( "0.##" ).format( size ) + " " + unit.name();
	}

}
