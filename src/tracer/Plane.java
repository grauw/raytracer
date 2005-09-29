package tracer;

import java.io.IOException;

/**
 * 3D flat, onbounded, traceable thingy, represented by normal vector and distance from the origin
 */
public class Plane extends Traceable {

	Vec3 normal;
	float offset;
	
	public Plane( Vec3 n, float o ) {
		normal = new Vec3(n);
		normal.normalize();
		offset = o;
		material = new Material();
	}
	public Plane() {
		normal = new Vec3(0,1,0);
		offset = 0.0f;
		material = new Material();
	}
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("normal") ) {
				normal.parse( p );
			} else if( p.tryKeyword("offset") ) {
				offset = p.parseFloat();
			} else if( p.tryKeyword("material") ) {
				material.parse( p );
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
	public IntersectionInfo intersect( Ray r ) {
		float x = intersectionScalar(r);
		if (x < 0) return new IntersectionInfo(false);
		
		// calculate location, normal and distance based on scalar
		IntersectionInfo info = new IntersectionInfo(true);
		info.location = r.direction.times(x).add(r.origin);
		info.normal = normal;
		info.distance = r.direction.times(x).length();
		info.object = this;
		
		return info;
	}
	
	public boolean hit( Ray r ) {
		float x = intersectionScalar(r);
		
		// if no intersection with 0 <= scalar <= 1
		if (0 <= x && x <= 1) return true;
		
		return false;
	}
	
	private float intersectionScalar(Ray r) {
		//(o - a*px - b*py - c*pz) / (a*vx + b*vy + c*vz) = t
		float m = -offset;
		m -= normal.x * r.origin.x;
		m -= normal.y * r.origin.y;
		m -= normal.z * r.origin.z;
		float n = normal.x * r.direction.x;
		n += normal.y * r.direction.y;
		n += normal.z * r.direction.z;
		float t = m / n;
		
		return t;
	}
	
}