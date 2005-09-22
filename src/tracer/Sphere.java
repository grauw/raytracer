package tracer;

import java.io.IOException;

/**
 * A 3D roundish traceable thingy with a center and a radius
 */
public class Sphere extends Traceable {

	Vec3 origin;
	float radius;
	
	public Sphere( Vec3 o, float r ) {
		origin = new Vec3(o);
		radius = r;	
		material = new Material();
	}
	public Sphere() {
		origin = new Vec3();
		material = new Material();	
	}
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("origin") ) {
				origin.parse( p );
			} else if( p.tryKeyword("radius") ) {
				radius = p.parseFloat();
			} else if( p.tryKeyword("material") ) {
				material.parse( p );
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
	public IntersectionInfo intersect( Ray r ) {
		// delete the line of code below, and properly compute if the Ray r hits the Spere, and if so, 
		// record the nearest intersection point in the IntersectionInfo record, as well as the distance
		// of this intersection point to the origin of the ray. Initially, you may put a zero normal vector
		// in the IntersectionInfo, but as soon as we compute the local lighting model, you have to compute
		// a proper normal vector of the sphere at the intersection point. 
		
		float a = r.direction.x*r.direction.x + r.direction.y*r.direction.y + r.direction.z*r.direction.z;
		float b = 2*r.direction.x*(r.origin.x-origin.x) + 2*r.direction.y*(r.origin.y-origin.y)+ 2*r.direction.z*(r.origin.z-origin.z);
		float c = (r.origin.x-origin.x)*(r.origin.x-origin.x) + (r.origin.y-origin.y)*(r.origin.y-origin.y) + (r.origin.z-origin.z)*(r.origin.z-origin.z) - radius*radius;
		
		float D = b*b - 4*a*c;
		
		if (D < 0) return new IntersectionInfo(false);
		
		float x1 = -b - (float)Math.sqrt(D) / 2*a;
		float x2 = -b + (float)Math.sqrt(D) / 2*a;	// TODO: optimize away when D=0...
		
		// TODO: check for negative values!
		
		float x = r.direction.times(x1).lengthSquared()
				< r.direction.times(x2).lengthSquared() ? x1 : x2;
		
		return new IntersectionInfo(
				r.direction.times(x).minus(r.origin)
				, new Vec3()						// TODO: normal
				, r.direction.times(x).length()
				, this
				);
	}
	
	public boolean hit( Ray r ) {
		// replace the line below by meaningful code
		return false;	
	}
	
}