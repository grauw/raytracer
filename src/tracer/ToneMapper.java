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
		r = (float) Math.pow(r, invGamma);
		g = (float) Math.pow(g, invGamma);
		b = (float) Math.pow(b, invGamma);
		
		// add code for color clamping below this line
		if (r > 1.0f) r = 1.0f;
		else if (r < 0.0f) r = 0.0f;
		if (g > 1.0f) g = 1.0f;
		else if (g < 0.0f) g = 0.0f;
		if (b > 1.0f) b = 1.0f;
		else if (b < 0.0f) b = 0.0f;

		// convert to int
		int intR = (int)( r*255.0f );
		int intG = (int)( g*255.0f );
		int intB = (int)( b*255.0f );
		int intA = 255;
		
		// pack the colours into an int
		return (intA<<24) | (intR<<16) | (intG<<8) | intB;

	}	
	
}