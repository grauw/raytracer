package tracer;
import java.io.IOException;

/**
 * Represents a 4-dimensional vector.
 * Note that all the `operator' methods return a <i>new</i> Vec4 object.
 */
public class Vec4 {
	
	public float x, y, z, w;
		
	/**
	 * Reads a vector in the form "&lt;number, number, number&gt;" from the Parser p.
	 * Changes this vector.
	 * @see toString
	 */
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "<" );
		x = p.parseFloat();
		p.parseKeyword( "," );
		y = p.parseFloat();
		p.parseKeyword( "," );
		z = p.parseFloat();
		p.parseKeyword( "," );
		w = p.parseFloat();
		p.parseKeyword( ">" );
	}
	
	/**
	 * Vector subtraction. Returns a new Vec4 equal to this - that.
	 */
	public Vec4 minus( Vec4 that ) {
		return new Vec4( x - that.x
		               , y - that.y
		               , z - that.z
		               , w - that.w
		               );	
	}
	/**
	 * Vector addition. Returns a new Vec4 equal to this + that.
	 */
	public Vec4 add( Vec4 that ) {
		return new Vec4( x + that.x
		               , y + that.y
		               , z + that.z
		               , w + that.w
		               );	
	}
	/**
	 * Multiplication by a scalar. Returns a new Vec4 equals to f * this.
	 */
	public Vec4 times( float f ) {
		return new Vec4( f*x
		               , f*y
		               , f*z
		               , f*w
		               );
	}
	
	/**
	 * Component-wise multiply
	 */
	 public Vec4 times( Vec4 that ) {
	 	return new Vec4( x*that.x
	 	               , y*that.y
	 	               , z*that.z
	 	               , w*that.w
	 	               );
	 	                 	
	 }
	
	/**
	 * Returns the length of this vector.
	 * Use lengthSquared() instead if you're going to square the result anyway:
	 * lengthSquared() is more efficient.
	 * @see lengthSquared
	 */
	public float length() {
		return (float)Math.sqrt( lengthSquared() );
	}
	/**
	 * Return the square of the length of this vector.
	 * @see length
	 **/
	public float lengthSquared() {
		return x*x + y*y + z*z + w*w;
	}
	
	/**
	 * Vector dot-product. Returns a new Vec3 equal to this dot that.
	 */
	public float dot( Vec4 that ) {
		return   x*that.x
		       + y*that.y
		       + z*that.z
		       + w*that.w;
	}

	
	/**
	 * Normalizes this vector.
	 * Warning: this actually changes this instance, contrary to most methods.
	 */
	public void normalize() {
		float invLength = 1.0f / length();
		x *= invLength;
		y *= invLength;
		z *= invLength;
		w *= invLength;
	}
	
	/**
	 * Copy constructor
	 */
	public Vec4( Vec4 that ) {
		x = that.x;
		y = that.y;
		z = that.z;
		w = that.w;
	}
	/**
	 * Constructs a Vec4 from a Vec by appending a 0.
	 */
	public Vec4( Vec3 v ) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = 0;
	}
	/**
	 * Constructor from floats.
	 */
	public Vec4( float x, float y, float z, float w ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	/**
	 * Default constructor. Makes the vector (0,0,0,0)
	 */
	public Vec4() {
		x = 0;	
		y = 0;
		z = 0;
		w = 0;
	}
	
	/**
	 * Returns a string representation of this vector, of the form
	 * "&lt;number, number, number&gt;" as can be parsed by the parse(Parser) method.
	 * @see parse
	 */
	public String toString() {
		return "<" + x + ", " + y + ", " + z + ", " + w + ">";	
	}
	
}