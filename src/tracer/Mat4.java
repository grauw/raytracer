package tracer;

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
		
		float a1, a2, a3, a4, b1, b2, b3, b4;
	    float c1, c2, c3, c4, d1, d2, d3, d4;
	    double det;
	    Mat4 adj = new Mat4();

	    adj = adjoint( this );
	    det = det4x4( this );
	    
	    System.out.println(det);
	    
	    a1 = (float) (adj.r1.x / det); b1 = (float) (adj.r2.x / det); 
		a2 = (float) (adj.r1.y / det); b2 = (float) (adj.r2.y / det); 
		a3 = (float) (adj.r1.z / det); b3 = (float) (adj.r2.z / det); 
		a4 = (float) (adj.r1.w / det); b4 = (float) (adj.r2.w / det); 
		c1 = (float) (adj.r3.x / det); d1 = (float) (adj.r4.x / det);
		c2 = (float) (adj.r3.y / det); d2 = (float) (adj.r4.y / det);	
		c3 = (float) (adj.r3.z / det); d3 = (float) (adj.r4.z / det);
		c4 = (float) (adj.r3.w / det); d4 = (float) (adj.r4.w / det);
		
		return new Mat4( new Vec4( a1, b1, c1, d1 )
				       , new Vec4( a2, b2, c2, d2 )
				       , new Vec4( a3, b3, c3, d3 )
				       , new Vec4( a4, b4, c4, d4 )
				       );
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
	
public Mat4 adjoint( Mat4 m ) {
	
	    double a1, a2, a3, a4, b1, b2, b3, b4;
	    double c1, c2, c3, c4, d1, d2, d3, d4;
	    Mat4 out = new Mat4();

	    a1 = m.r1.x; b1 = m.r2.x; c1 = m.r3.x; d1 = m.r4.x;
		a2 = m.r1.y; b2 = m.r2.y; c2 = m.r3.y; d2 = m.r4.y;		
		a3 = m.r1.z; b3 = m.r2.z; c3 = m.r3.z; d3 = m.r4.z;
		a4 = m.r1.w; b4 = m.r2.w; c4 = m.r3.w; d4 = m.r4.w;

	    out.r1.x  = (float)   det3x3( b2, b3, b4, c2, c3, c4, d2, d3, d4);
	    out.r2.x  = (float) - det3x3( a2, a3, a4, c2, c3, c4, d2, d3, d4);
	    out.r3.x  = (float)   det3x3( a2, a3, a4, b2, b3, b4, d2, d3, d4);
	    out.r4.x  = (float) - det3x3( a2, a3, a4, b2, b3, b4, c2, c3, c4);
	        
	    out.r1.y  = (float) - det3x3( b1, b3, b4, c1, c3, c4, d1, d3, d4);
	    out.r2.y  = (float)   det3x3( a1, a3, a4, c1, c3, c4, d1, d3, d4);
	    out.r3.y  = (float) - det3x3( a1, a3, a4, b1, b3, b4, d1, d3, d4);
	    out.r4.y  = (float)   det3x3( a1, a3, a4, b1, b3, b4, c1, c3, c4);
	        
	    out.r1.z  = (float)   det3x3( b1, b2, b4, c1, c2, c4, d1, d2, d4);
	    out.r2.z  = (float) - det3x3( a1, a2, a4, c1, c2, c4, d1, d2, d4);
	    out.r3.z  = (float)   det3x3( a1, a2, a4, b1, b2, b4, d1, d2, d4);
	    out.r4.z  = (float) - det3x3( a1, a2, a4, b1, b2, b4, c1, c2, c4);
	        
	    out.r1.w  = (float) - det3x3( b1, b2, b3, c1, c2, c3, d1, d2, d3);
	    out.r2.w  = (float)   det3x3( a1, a2, a3, c1, c2, c3, d1, d2, d3);
	    out.r3.w  = (float) - det3x3( a1, a2, a3, b1, b2, b3, d1, d2, d3);
	    out.r4.w  = (float)   det3x3( a1, a2, a3, b1, b2, b3, c1, c2, c3);
	    
	    return out;
	}
	
	public double det4x4( Mat4 m ) {
		
		double ans;
	    double a1, a2, a3, a4, b1, b2, b3, b4, c1, c2, c3, c4, d1, d2, d3, d4;

		a1 = m.r1.x; b1 = m.r1.x; c1 = m.r1.x; d1 = m.r1.x;
		a2 = m.r2.y; b2 = m.r2.y; c2 = m.r2.y; d2 = m.r2.y;		
		a3 = m.r3.z; b3 = m.r3.z; c3 = m.r3.z; d3 = m.r3.z;
		a4 = m.r4.w; b4 = m.r4.w; c4 = m.r4.w; d4 = m.r4.w;

	    ans = a1 * det3x3( b2, b3, b4, c2, c3, c4, d2, d3, d4)
	        - b1 * det3x3( a2, a3, a4, c2, c3, c4, d2, d3, d4)
	        + c1 * det3x3( a2, a3, a4, b2, b3, b4, d2, d3, d4)
	        - d1 * det3x3( a2, a3, a4, b2, b3, b4, c2, c3, c4);
	    
	    return ans;    
	}
	
	public double det3x3( double a1, double a2, double a3, 
			  			  double b1, double b2, double b3, 
			  			  double c1, double c2, double c3 ) {
		
		double ans;

		ans = a1 * det2x2( b2, b3, c2, c3 )
			- b1 * det2x2( a2, a3, c2, c3 )
			+ c1 * det2x2( a2, a3, b2, b3 );

		return ans;
}
	
	public double det2x2( double a, double b, 
			  			  double c, double d) {
		double ans;
		ans = (a * d - b * c);
		
		return ans;
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