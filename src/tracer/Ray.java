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
		/* diffuse component */
		// vector w towards light source
		Vec3 lightvector = light.location.minus(info.location).normalize();
		// cos ? = (v · w) / (|v| * |w|)
		float ianglecos = info.normal.dot(lightvector);
//		if (ianglecos < 0) ianglecos = 0;
		// light color * angle * intensity * diffuse property
		Vec3 diffuse = new Vec3(light.color
				.times(info.object.material.color)
				.times(light.intensity)
				.times(ianglecos)
				.times(info.object.material.diffuse));
		
		/* specular (a.k.a. glossy) reflection */
		Vec3 cameravector = direction.times(-1).normalize();
		Vec3 h = lightvector.add(cameravector).normalize();
		float shininess = info.object.material.specular *
				(float) Math.pow( h.dot(info.normal), info.object.material.specularPower);
		Vec3 specular = light.color.times(shininess);
		
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
				Vec3 color2 = new Vec3();
				// multiple randomised shadow feelers (area lights)
				for (int j=0; j<light.numSamples; j++) {
					Ray shadowFeeler = new Ray( nearestHit.location,
							shadowFeelerDirection.add(Vec3.random(light.size)));
					if( !shadowFeeler.hit( nearestHit.object ) ) {
						color2 = color2.add(  localLight( nearestHit, light )  );
					}
				}
				color = color.add(color2.times(1.0f / light.numSamples));
			}
			
			// global illumination: add recursively computed reflection below this line
			if (maxReflectionsLeft > 0 && nearestHit.object.material.reflectance > 0.0f) {
				Vec3 cameravector = direction.times(-1).normalize();
				// calculate reflection vector
				Vec3 reflection = cameravector.times(-1).add(nearestHit.normal.times(cameravector.dot(nearestHit.normal)).times(2));
				Vec3 color2 = new Vec3();
				// multiple randomised reflections (monte carlo global illumination)
				for (int j=0; j<nearestHit.object.material.numReflections; j++) {
					Ray r = new Ray(nearestHit.location,
							reflection.add(Vec3.random(reflection.length() * nearestHit.object.material.reflectionJitter))); 
					Vec3 reflectionColor = r.trace(nearestHit.object, maxReflectionsLeft - 1);
					reflectionColor = reflectionColor.times(nearestHit.object.material.reflectance);
					color2 = color2.add(reflectionColor);
				}
				color = color.add(color2.times(1.0f / nearestHit.object.material.numReflections));
			}
			
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
		// test all Traceable object in the scene for collision.
		Iterator i = Tracer.scene.iterator();
		while( i.hasNext() ) {
			Traceable t = (Traceable)i.next();
			if( t != ignoreObject ) {
				if (t.hit(this)) return true;
			}
		}
		return false;
	}
	
}