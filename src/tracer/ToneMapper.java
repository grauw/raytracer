package tracer;

/**
 * Used for mapping floating point representations of colors into packed-int
 * colors as used by Java. This implementation also supports gamma correction.
 */
public class ToneMapper {
	
	public ToneMapper( float gamma ) {
		invGamma = 1.0f/gamma;	
	}

	public float invGamma;
	
	public int map( float r, float g, float b ) {
		
		// apply gamma correction
		// r = 
		// g = 
		// b = 
		
		// add code for color clamping below this line

		// convert to int
		int intR = (int)( r*255.0f );
		int intG = (int)( g*255.0f );
		int intB = (int)( b*255.0f );
		int intA = 255;
		
		// pack the colours into an int
		return (intA<<24) | (intR<<16) | (intG<<8) | intB;

	}	
	
}