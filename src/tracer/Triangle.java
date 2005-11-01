package tracer;

import java.io.IOException;

public class Triangle extends Traceable {

	Vec3 vertice1;
	Vec3 vertice2;
	Vec3 vertice3;
	
	public Triangle( Vec3 v1, Vec3 v2, Vec3 v3 ) {
		vertice1 = new Vec3(v1);
		vertice2 = new Vec3(v2);
		vertice3 = new Vec3(v3);
		material = new Material();
	}
	public Triangle() {
		vertice1 = new Vec3();
		vertice2 = new Vec3();
		vertice3 = new Vec3();
		material = new Material();	
	}
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("vertice1") ) {
				vertice1.parse( p );
			} else if( p.tryKeyword("vertice2") ) {
				vertice2.parse( p );
			} else if( p.tryKeyword("vertice3") ) {
				vertice3.parse( p );
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
		
		IntersectionInfo info = new IntersectionInfo(true);
		info.location = r.direction.times(x).add(r.origin);
		info.normal = (vertice2.minus(vertice1)).cross(vertice3.minus(vertice1)).normalize();
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
	
		float a, b, c, d, e, f, g, h, i, j, k, l;
		double beta, gamma, t, M;
		
		a = vertice1.x - vertice2.x; d = vertice1.x - vertice3.x; g = r.direction.x;
		b = vertice1.y - vertice2.y; e = vertice1.y - vertice3.y; h = r.direction.y;
		c = vertice1.z - vertice2.z; f = vertice1.z - vertice3.z; i = r.direction.z;
		
		j = vertice1.x - r.origin.x;
		k = vertice1.y - r.origin.y;
		l = vertice1.z - r.origin.z;
		
		M = a * (e*i - h*f) + b * (g*f - d*i) + c * (d*h - e*g);
		
		beta  = ( j * (e*i - h*f) + k * (g*f - d*i) + l * (d*h - e*g) ) / M ;
		gamma = ( i * (a*k - j*b) + h * (j*c - a*l) + g * (b*l - k*c) ) / M ;
		t	  = -( f * (a*k - j*b) + e * (j*c - a*l) + d * (b*l - k*c) ) / M ;
		
		if ( gamma < 0 || gamma > 1) return Float.NEGATIVE_INFINITY;
		if ( beta < 0 || beta > (1 - gamma)) return Float.NEGATIVE_INFINITY;
		else return (float) t;
	}
	
}
