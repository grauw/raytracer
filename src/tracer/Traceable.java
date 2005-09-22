package tracer;

/**
 * Base class for all visible objects.
 */
public abstract class Traceable {

	/**
	 * Returns an IntersectionInfo object indicating whether the ray r
	 * intersects this object or not.
	 * @see IntersectionInfo
	 */
	abstract public IntersectionInfo intersect( Ray r );
	
	/**
	 * Returns a boolean indicating whether the line segment (r.origin + 
	 * t*r.direction) with 0 <= t <= 1 intersects the object.
	 */
	abstract public boolean hit( Ray r );
	
	Material material;
	
}