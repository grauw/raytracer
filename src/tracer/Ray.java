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
			if (maxReflectionsLeft > 0 && material.reflectance > 0.0f) {
				Vec3 cameravector = direction.times(-1).normalize();
				// calculate reflection vector
				Vec3 reflection = reflect(cameravector, nearestHit.normal);
				Vec3 color2 = new Vec3();
				// multiple randomised reflections (monte carlo global illumination)
				for (int j=0; j < material.numReflections; j++) {
					Ray r = new Ray(nearestHit.location,
							reflection.add(Vec3.random(reflection.length() * nearestHit.object.material.reflectionJitter))); 
					Vec3 reflectionColor = r.trace(nearestHit.object, maxReflectionsLeft - 1);
					reflectionColor = reflectionColor.times(material.reflectance);
					color2 = color2.add(reflectionColor);
				}
				color = color.add(color2.times(1.0f / material.numReflections));
			}
			
			// refraction
			if (material.refraction > 0) {
				Vec3 refractionColor = null;
				Vec3 trans = new Vec3();
				Vec3 k;										// light attenuation factor
				float c = 0;
				Vec3 incidence = direction.normalize();
				Vec3 normal = nearestHit.normal;
				Vec3 refl = reflect(incidence.times(-1), normal);	// reflection vector
				// if from air into object
				if (incidence.dot(normal) < 0) {
					refract(incidence, normal, material.refraction, trans);
					c = incidence.times(-1).dot(normal);	// cos theta
					k = new Vec3(1,1,1);
				}
				// if from object into air
				else {
					k = new Vec3(1,1,1);
					if (refract(incidence, normal.times(-1), 1/material.refraction, trans)) {
						c = trans.dot(normal);				// cos phi
					} else {
						Ray reflRay = new Ray(nearestHit.location, refl); 
						refractionColor = k.times(reflRay.trace(nearestHit.object, maxReflectionsLeft - 1));
					}
				}
				if (refractionColor == null) {
					float R0 = ((material.refraction - 1)*(material.refraction - 1)) /
			           ((material.refraction - 1)*(material.refraction + 1));
					float R = (float) (R0 + (1 - R0)*Math.pow(1 - c, 5));
					
					Ray reflRay = new Ray(nearestHit.location, refl); 
					Vec3 reflColor = reflRay.trace(nearestHit.object, maxReflectionsLeft - 1);
					Ray transRay = new Ray(nearestHit.location, trans); 
					Vec3 transColor = transRay.trace(nearestHit.object, maxReflectionsLeft - 1);
					refractionColor = k.times(reflColor.times(R).add(transColor.times(1-R)));
				}
				color = color.times(1 - material.refraction);
				color = color.add(refractionColor.times(material.refraction));
			}
			
			return color;
			
			
		} else {
			// hit nothing; return background color
			return new Vec3( 0.0f, 0.0f, 0.0f );
		}
		
	}
	
	// calculate reflection ray, given incidence & normal (both normalised)
	private Vec3 reflect(Vec3 incidence, Vec3 normal) {
		return incidence.times(-1).add(normal.times(incidence.dot(normal)).times(2));
	}
	
	// return false if total internal reflaction,
	private boolean refract(Vec3 incidence, Vec3 normal, float ls, Vec3 trans) {
		float lt = 1.0f;
		float costheta = incidence.times(-1).dot(normal);
		// calculate refracted direction
		float cos2phi = 1 - (ls*ls*(1 - costheta*costheta)/(lt*lt));
		if (cos2phi < 0) {
			return false;
		} else {
			Vec3 xx = incidence.minus(normal.times(costheta)).times(ls/lt);
			Vec3 t = xx.minus(normal.times((float) Math.sqrt(cos2phi)));
			trans.x = t.x;
			trans.y = t.y;
			trans.z = t.z;
			return true;
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