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
		Vec3 color = perlin(info.object.material, info.location);
		/* diffuse component */
		// vector w towards light source
		Vec3 lightvector = light.location.minus(info.location).normalize();
		// cos ? = (v · w) / (|v| * |w|)
		float ianglecos = info.normal.dot(lightvector);
//		if (ianglecos < 0) ianglecos = 0;
		// light color * angle * intensity * diffuse property
		Vec3 diffuse = new Vec3(light.color
				.times(color)
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
			Vec3 color = perlin(nearestHit.object.material, nearestHit.location)
					.times( material.ambient ); 	// ambient
			
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
				Vec3 incidence = direction.normalize();
				// calculate reflection vector
				Vec3 reflection = reflect(incidence, nearestHit.normal);
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
				Vec3 refractionColor = null;	// final colour of refraction
				Vec3 trans = new Vec3();		// transmitted vector (filled in later)
				Vec3 k;							// light attenuation factor
				float c = 0;
				Vec3 incidence = direction.normalize();	// incidence light vector
				Vec3 normal = nearestHit.normal;		// normal vector
				Vec3 refl = reflect(incidence, normal);	// reflection vector
				boolean full_reflectance = false;
				
				// if from air into object
				if (incidence.dot(normal) < 0) {
					if (!refract(incidence, normal, material.refractionIndex, trans)) {
						full_reflectance = true;
					} else {
						c = incidence.times(-1).dot(normal);	// cos theta
					}
					k = new Vec3(1,1,1);
				}
				// if from object into air
				else {
					// distance-dependent attenuation (not implemented)
					k = new Vec3(1,1,1);
					if (refract(incidence, normal.times(-1), 1/material.refractionIndex, trans)) {
						c = trans.dot(normal);				// cos 180-phi
					} else {
						Ray reflRay = new Ray(nearestHit.location, refl); 
						refractionColor = k.times(reflRay.trace(nearestHit.object, maxReflectionsLeft - 1));
					}
				}
				// if no refrectionColor determined earlier
				if (refractionColor == null) {
					Ray reflRay = new Ray(nearestHit.location, refl);
					Vec3 reflColor = reflRay.trace(nearestHit.object, maxReflectionsLeft - 1);
					
					// if full reflectance
					if (full_reflectance) {
						refractionColor = reflColor;
					}
					else
					{
						// add a bit to the transmitted ray to avoid infinite reflections
						Ray transRay = new Ray(nearestHit.location.add(trans.times(0.0001f)), trans);
						Vec3 transColor = transRay.trace(null, maxReflectionsLeft - 1);
						float R0 = (material.refractionIndex - 1) / (material.refractionIndex + 1);
						R0 = R0*R0;
						float R = R0 + (1 - R0) * (float)Math.pow(1 - c, 5);
						refractionColor = k.times(reflColor.times(R).add(transColor.times(1-R)));
					}
				}
				// return colour with refraction factor applied 
				color = color.times(1 - material.refraction);
				color = color.add(refractionColor.times(material.refraction));
			}
			
			return color;
			
			
		} else {
			// hit nothing; return background color
			return new Vec3( 0.0f, 0.0f, 0.0f );
		}
		
	}
	
	/** Calculate reflection vector
	 * @param incidence Light incidence vector (normalised)
	 * @param normal Normal vector (normalised)
	 * @return reflection vector
	 */
	private Vec3 reflect(Vec3 incidence, Vec3 normal) {
		return incidence.add(normal.times(incidence.times(-1).dot(normal)).times(2));
	}
	
	/** Calculate refraction vector
	 * @param incidence Light incidence vector (normalised)
	 * @param normal Normal vector (normalised)
	 * @param lt Transmission object refraction index
	 * @param trans [output] Vec3 object to contain vector
	 * @return false if total internal refraction 
	 */
	private boolean refract(Vec3 incidence, Vec3 normal, float lt, Vec3 trans) {
		float ls = 1.0f;
		float costheta = incidence.dot(normal);
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
	
	public static float noise( Vec3 v ) {
		int i,j,k;
		float sum = 0.0f;
		int p = (int) Math.floor(v.x);
		int q = (int) Math.floor(v.y);
		int r = (int) Math.floor(v.z);
		for (i=p; i<p+2;i++) {
			for (j=q; j<q+2; j++) {
				for (k=r; k<r+2; k++) {
		        sum += big_omega( v.x-i, v.y-j, v.z-k, i, j, k );
		      }
		    }
		  }
		  return sum;
	}		
	//interpolation function
	public static float big_omega( float u, float v, float w, float i, float j, float k) {
		Vec3 v3 = new Vec3( u, v, w );
		return small_omega(u)*small_omega(v)*small_omega(w)*hash((int)i,(int)j,(int)k).dot(v3);
	}
	//cubic weight function
	public static float small_omega( float t ) {
		float s = Math.abs(t);
		if ( s < 1 ) return (2*s*s*s - 3*s*s + 1);
		else return 0;
	}
	//hash function for perlin noise
	public static Vec3 hash( int i, int j, int k ) {
		Vec3[]G = Tracer.G;
		int[]P = Tracer.P;
		int n = P.length;
		int a = P[ modulo(k, n) ];
		int b = P[ modulo(j + a, n) ];
		int c = P[ modulo(i + b, n) ];
		return G[ c ];
	}
	//modulo operation that makes -1 -> n-1.
	public static int modulo(int dividend, int divisor) {
		return Tracer.P[ (dividend < 0 ? divisor : 0) + dividend % divisor ];
	}
	// calculates colour on material given a location, applies Perlin if there is.
	public static Vec3 perlin(Material material, Vec3 location) {
		if (material.color == material.perlin) {
			return material.color;
		}
		float noise = Math.abs(noise(location));
		return material.color.times(1-noise).add(material.perlin.times(noise));
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
			if( t != ignoreObject && !t.material.noshadow ) {
				if (t.hit(this)) return true;
			}
		}
		return false;
	}
	
}