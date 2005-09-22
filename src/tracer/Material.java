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
	}
	
	public Vec3 color;	
	public float ambient;
	public float diffuse;
	public float specular;
	public float specularPower;
	public float reflectance;
	
	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("color") ) {
				color.parse( p );
			} else if( p.tryKeyword("ambient") ) {
				ambient = p.parseFloat();
			} else if( p.tryKeyword("diffuse") ) {
				diffuse = p.parseFloat();
			} else if( p.tryKeyword("specular") ) {
				specular = p.parseFloat();
			} else if( p.tryKeyword("specularPower") ) {
				specularPower = p.parseFloat();
			} else if( p.tryKeyword("reflectance") ) {
				reflectance = p.parseFloat();
			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
}