package tracer;

/**
 * Used for returning information about intersections from Traceable.intersect.
 */
public class IntersectionInfo {
	
	/**
	 * Constructor indicating there is no intersection.
	 * @param hit Should be false. I know this is ugly, at least it saves us
	 *   from having to type `new IntersectionInfo(false,null,null,0,null)' for
	 *   indicating no hit.
	 **/
	public IntersectionInfo( boolean hit ) {
		this.hit = hit;
	}
	/**
	 * Constructor indicating there was an intersection.
	 * @param location The location of the intersection
	 * @param normal The normal of the object at the point of intersection.
	 * @param distance The distance from ray origin to the intersection point.
	 * @param object The object that was hit. Supply `this' here from the
	 *    intersect method of your Traceable.
	 */
	public IntersectionInfo( Vec3 location, Vec3 normal, float distance, Traceable object ) {
		this.hit = true;
		this.location = location;
		this.normal = normal;
		this.distance = distance;
		this.object = object;
	}
	
	public boolean hit;
	public Vec3 location;
	public Vec3 normal;
	public float distance;		
	public Traceable object;
	
}