package tracer;

import java.io.IOException;

/**
 * Holds shading parameters.
 */
public class Material {
	
	public Material() {
		color = new Vec3( 1, 1, 1 );
		ambient = 0.2f;
		diffuse	= 0.8f;
		specular = 0.0f;
		specularPower = 16.0f;
		reflectance = 0.0f;
		reflectionJitter = 0.0f;
		numReflections = 1;
		refraction = 0.0f;
		refractionIndex = 1.0f;
		perlin = color;
		noshadow = false;
	}
	
	public Vec3 color;	
	public float ambient;
	public float diffuse;
	public float specular;
	public float specularPower;
	public float reflectance;
	public float reflectionJitter;
	public int numReflections;
	public float refraction;
	public float refractionIndex;
	public Vec3 perlin;
	public boolean noshadow;
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("color") ) {
				color.parse( p );
				perlin = color;
			} else if( p.tryKeyword("ambient") ) {
				ambient = p.parseFloat();
			} else if( p.tryKeyword("diffuse") ) {
				diffuse = p.parseFloat();
			} else if( p.tryKeyword("specular") ) {
				specular = p.parseFloat();
			} else if( p.tryKeyword("specularpower") ) {
				specularPower = p.parseFloat();
			} else if( p.tryKeyword("reflectance") ) {
				reflectance = p.parseFloat();
			} else if( p.tryKeyword("reflections") ) {
				numReflections = (int) p.parseFloat();
			} else if( p.tryKeyword("jitter") ) {
				reflectionJitter = p.parseFloat();
			} else if( p.tryKeyword("refraction") ) {
				refraction = p.parseFloat();
			} else if( p.tryKeyword("refractionindex") ) {
				refractionIndex = p.parseFloat();
			} else if( p.tryKeyword("perlin") ) {
				color.parse( p );
				perlin = new Vec3();
				perlin.parse( p );
			} else if( p.tryKeyword("noshadow") ) {
				noshadow = true;
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
}