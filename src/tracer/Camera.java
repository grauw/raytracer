package tracer;

import java.io.IOException;

/**
 * Represents the camera:
 * viewing window = [left,bottom] x [right,top], with z-coordinate <b>near</b>
 */
public class Camera {
	
	public Camera() {
		origin = new Vec3();		
		left = -1;
		bottom = -1;
		right = 1;
		top = 1;
		near = -1;
	}
	
	public Vec3 origin;
	public float left;
	public float bottom;
	public float right;
	public float top;
	public float near;

	public void parse( Parser p ) throws IOException {
		p.parseKeyword( "{" );
		while( !p.tryKeyword("}") && !p.endOfFile() ) {
			
			if( p.tryKeyword("origin") ) {
				origin.parse( p );
			} else if( p.tryKeyword("window") ) {
				p.parseKeyword( "<" );
				left = p.parseFloat();
				p.parseKeyword( "," );
				bottom = p.parseFloat();
				p.parseKeyword( "," );
				right = p.parseFloat();
				p.parseKeyword( "," );
				top = p.parseFloat();
				p.parseKeyword( ">" );
			} else if( p.tryKeyword("near") ) {
				near = p.parseFloat();

			} else {
				System.out.println( p.tokenWasUnexpected() );	
			}
			
		}
	}
	
}