package tracer;

import java.util.Iterator;

/**
 * Represents a ray: a ray can `trace' itself, calculating a color.
 */
public class Ray {

	public Vec3 origin;
	public Vec3 direction;
	
	public Ray( Vec3 o, Vec3 d ) {
		origin = new Vec3( o );
		direction = new Vec3( d );	
	}
	
	/**
	 * Calculates the local light term, given an IntersectionInfo and a Light.
	 * This method does <b>not</b> do an occlusion test; do this yourself (e.g.
	 * using a shadow feeler).
	 */
	public Vec3 localLight( IntersectionInfo info, Light light ) {
		// diffuse component
		// replace the line below by meaningful code
		Vec3 diffuse = new Vec3();
		
		// specular (a.k.a. glossy) reflection
		// replace the line below by meaningful code
		Vec3 specular = new Vec3();
		
		return diffuse.add( specular );
	}
	
	/**
	 * Does the actual `raytracing'. Returns the color this ray `hits.'
	 * @param currentObject This object is ignored in the intersection tests; in
	 *    effect, this object is `invisible' to the ray. This is useful for
	 *    reflection rays and shadow feelers: it avoids precision-errors by just
	 *    ignoring the object you've just bounced off of. If all objects are
	 *    convex (which they are in this tracer) this is actually not a hack but
	 *    completely correct.
	 * @param maxReflectionsLeft Maximum recursion depth for reflection
	 *    calculations .
	 */
	public Vec3 trace( Traceable currentObject, int maxReflectionsLeft ) {
		// test all Traceable object in the scene for collision.
		// store the nearest one.
		IntersectionInfo nearestHit = null;
		Iterator i = Tracer.scene.iterator();
		while( i.hasNext() ) {
			
			Traceable t = (Traceable)i.next();
			if( t != currentObject ) {
				IntersectionInfo info = t.intersect( this );
				if(  info.hit  &&  (nearestHit==null || info.distance<nearestHit.distance)  ) {
					nearestHit = info;
				}
			}
			
		}
		if( nearestHit != null ) {
			// actually hit something
			Material material = nearestHit.object.material;
			Vec3 color = material.color.times( material.ambient ); // ambient
			
			// local contribution of light
			Iterator lightIter = Tracer.lights.iterator();
			while( lightIter.hasNext() ) {
				Light light = (Light)lightIter.next();
				Vec3 shadowFeelerDirection = light.location.minus( nearestHit.location );
				Ray shadowFeeler = new Ray( nearestHit.location, shadowFeelerDirection );
				if( !shadowFeeler.hit( nearestHit.object ) ) {
					color = color.add(  localLight( nearestHit, light )  );
				}
			}
			
			// global illumination: add recursively computed reflection below this line
			
			
			return color;
			
			
		} else {
			// hit nothing; return background color
			return new Vec3( 0.0f, 0.0f, 0.0f );
		}
		
	}
	
	/**
	 * Checks if the ray (origin + t*direction) hits the scene with 0 <= t <= 1.
	 * @param ignoreObject Similar to trace's currentObject parameter.
	 *    @see trace.
	 */
	public boolean hit( Traceable ignoreObject ) {
		// replace the line below by meaningful code
		return false;
	}
	
}