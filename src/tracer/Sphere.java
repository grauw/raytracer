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
		// calculate a, b and c coefficients of the quadratic formula
		float a = r.direction.x*r.direction.x + r.direction.y*r.direction.y + r.direction.z*r.direction.z;
		float b = 2*r.direction.x*(r.origin.x-origin.x) + 2*r.direction.y*(r.origin.y-origin.y)+ 2*r.direction.z*(r.origin.z-origin.z);
		float c = (r.origin.x-origin.x)*(r.origin.x-origin.x) + (r.origin.y-origin.y)*(r.origin.y-origin.y) + (r.origin.z-origin.z)*(r.origin.z-origin.z) - radius*radius;
		
		// determinant
		float D = b*b - 4*a*c;
		
		// if no intersection
		if (D < 0) return new IntersectionInfo(false);
		
		// calculate possible scalars
		float x1 = (-b - (float)Math.sqrt(D)) / (2*a);
		float x2 = (-b + (float)Math.sqrt(D)) / (2*a);
		
		// choose smalles scalar > 0, or no intersection
		float x;
		if      (x1 >= 0 && x1 < x2) x = x1;
		else if (x2 >= 0 && x2 < x1) x = x2;
		else return new IntersectionInfo(false);
		
		// calculate location, normal and distance based on scalar
		IntersectionInfo info = new IntersectionInfo(true);
		info.location = r.direction.times(x).add(r.origin);
		info.normal = info.location.minus(origin);
		info.distance = r.direction.times(x).length();
		info.object = this;
		
		return info;
	}
	
	public boolean hit( Ray r ) {
		// replace the line below by meaningful code
		return false;	
	}
	
}