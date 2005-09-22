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
		
		
		// For now, simply return "no hit". Replace the line below by meaningful code
		return new IntersectionInfo(false);
	}
	
	public boolean hit( Ray r ) {
		// replace the line below by meaningful code
		return false;	
	}
	
}