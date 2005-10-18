package tracer;
import java.io.IOException;

/**
 * Represents a 4x4 matrix.
 * Note that all the `operator' methods return a <i>new</i> Mat4 object.
 */
public class Mat4 {
	
	public Vec4 r1, r2, r3, r4; // the for rows of the matrix, represented as Vec4s.
	
	public Mat4 add( Mat4 that ) {
		return new Mat4( r1.add(that.r1)
					   , r2.add(that.r2)
					   , r3.add(that.r3)
					   , r4.add(that.r4)
					   );
	}

	public Mat4 minus( Mat4 that ) {
		return new Mat4( r1.minus(that.r1)
					   , r2.minus(that.r2)
					   , r3.minus(that.r3)
					   , r4.minus(that.r4)
					   );
	}

	public Mat4 times( Mat4 that ) {
		Mat4 t = that.transposed();
		return new Mat4(
					t.transform(r1),
					t.transform(r2),
					t.transform(r3),
					t.transform(r4)
				);
	}

	public Vec4 transform( Vec4 v ) {
		return new Vec4(
					r1.dot(v),
					r2.dot(v),
					r3.dot(v),
					r4.dot(v)
				);
	}
	
	public Mat4 inverse() {
		// complete this method
		return new Mat4();
	}
	
	public Mat4 transposed() {
		return new Mat4( new Vec4(r1.x, r2.x, r3.x, r4.x)
		               , new Vec4(r1.y, r2.y, r3.y, r4.y)
		               , new Vec4(r1.z, r2.z, r3.z, r4.z)
		               , new Vec4(r1.w, r2.w, r3.w, r4.w)
		               );
	}

	public Mat4( Vec4 r1, Vec4 r2, Vec4 r3, Vec4 r4 ) {
		this.r1 = new Vec4(r1);
		this.r2 = new Vec4(r2);
		this.r3 = new Vec4(r3);
		this.r4 = new Vec4(r4);
	}

	public Mat4() {
		r1 = new Vec4( 1, 0, 0, 0 );
		r2 = new Vec4( 0, 1, 0, 0 );
		r3 = new Vec4( 0, 0, 1, 0 );
		r4 = new Vec4( 0, 0, 0, 1 );
	}
	
	/**
	 * Returns a string representation of this matrix
	 */
	public String toString() {
		return r1.toString() + "\n"
			 + r2.toString() + "\n"
			 + r3.toString() + "\n"
			 + r4.toString() + "\n";	
	}
	
}