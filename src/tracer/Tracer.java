package tracer;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.image.MemoryImageSource;
import java.io.IOException;

/**
 * The main class. Displays the window and coordinates the raytracing.
 */
public class Tracer extends Frame {
	static final long serialVersionUID = 12345;
	
	public Tracer() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}
	
	static int maxReflectionDepth = 0;
	static float gamma = 1.0f;
	
	MemoryImageSource imageSource;
	Image offscreenImage;
	int[] pixelBuffer;
	public static Camera camera;
	public static ArrayList scene;
	public static ArrayList lights;
	public static int width = 400;
	public static int height = 400;
	public static Vec3[] G = randomArray();
	public static int[] P = textureMap();
	
	/**
	 * Loads and parses the file "scene.txt". Adds the things it recognizes to
	 * the `scene' and `lights' ArrayLists.
	 */
	public void loadScene() {
		camera = new Camera();
		scene = new ArrayList();
		lights = new ArrayList();
		try {
			
			Parser p = new Parser( "scene.txt" );

			while( !p.endOfFile() ) {
				if( p.tryKeyword( "width" ) ) {
					width = (int)p.parseFloat();
				} else if( p.tryKeyword( "height" ) ) {
					height = (int)p.parseFloat();
				} else if( p.tryKeyword( "maxreflectiondepth" ) ) {
					maxReflectionDepth = (int)p.parseFloat();
				} else if( p.tryKeyword( "gamma" ) ) {
					gamma = p.parseFloat();
				} else if( p.tryKeyword( "sphere" ) ) {
					Sphere s = new Sphere();
					s.parse( p );
					scene.add( s );
				} else if( p.tryKeyword( "plane" ) ) {
					Plane pl = new Plane();
					pl.parse( p );
					scene.add( pl );
				} else if( p.tryKeyword( "triangle" ) ) {
					Triangle t = new Triangle();
					t.parse( p );
					scene.add( t );
				} else if( p.tryKeyword( "camera" ) ) {
					camera.parse( p );
				} else if( p.tryKeyword( "light" ) ) {
					Light l = new Light();
					l.parse( p );
					lights.add( l );
				} else {
					System.out.println( p.tokenWasUnexpected() );		
				}
			}			
			
			
		} catch( IOException e ) {}
	}
	
	/**
	 * Redraws the offscreenImage onto the screen.
	 */
	public void paint( Graphics gr ) {
		if( offscreenImage != null ) {
			if( pixelBuffer != null ) {
				imageSource.newPixels();
			}
			gr.drawImage( offscreenImage, 0, 50, null );
		}
	}
	
	/**
	 * The actual raytracing starts here.
	 * Initializes the pixel buffers and raytraces each pixel.
	 * Redraws the screen each time an additional 8 rows have been rendered.
	 */
	public void render() {
		
		pixelBuffer = new int[ width*height ];
		imageSource = new MemoryImageSource( width, height, pixelBuffer, 0, width );
		imageSource.setAnimated( true );
		offscreenImage = Toolkit.getDefaultToolkit().createImage( imageSource );
		
		
		ToneMapper toneMapper = new ToneMapper( gamma );
		
		System.out.println( "Starting raytracing." );
		
		for( int y=0; y<height; ++y ) {
			for( int x=0; x<width; ++x ) {
		
				int index = (height-y-1)*width + x;
				Vec3 color = subPixel( x, y );
				pixelBuffer[ index ] = toneMapper.map( color.x, color.y, color.z );
				
				
			}
			if( (y&7) == 0 ) paint( getGraphics() );
		}
		
		System.out.println( "Finished raytracing." );
		
	}
	
	public Vec3 subPixel( int x, int y ) {
		
		float sampleRoot = camera.sampleRoot;
		float samples = sampleRoot * sampleRoot;
		float u = 1.0f / (sampleRoot + 1), v = 1.0f / (sampleRoot + 1);
		Vec3 avgColor = new Vec3();
		
		for (int i = 1; i <= sampleRoot; i++ ) {
			for (int j = 1; j <= sampleRoot; j++ ) {
				avgColor = avgColor.add( tracePixel( x, y, i * u, j * v ) );
			}
		}
		
		return avgColor.times(1 / samples);
	}
	
	/**
	 * Given (x,y) coordinates of the pixel to be traced, constructs the primary
	 * ray, raytraces it (by calling Ray.trace) and returns the result (the
	 * color for the pixel).
	 */
	public Vec3 tracePixel( int x, int y, float a, float b ) {
		// compute a ray from the origin of the camera through the center of pixel (x,y)
		Vec3 direction = new Vec3(
				camera.left + ((x + a) / width) * (camera.right - camera.left),
				camera.bottom + ((y + b) / height) * (camera.top - camera.bottom),
				camera.near);
		
		// construct translation matrix for camera direction
		Vec3 w = camera.target.normalize();
		w.z = w.z*-1;
		w = w.normalize();
		Vec3 u = camera.viewUp.cross(w).normalize();
		Vec3 v = w.cross(u);
		Mat4 M1 = new Mat4(new Vec4(u), new Vec4(v), new Vec4(w), new Vec4(0,0,0,1));
		Mat4 M2 = new Mat4();
		M2.r1.w = -camera.origin.x;
		M2.r2.w = -camera.origin.y;
		M2.r3.w = -camera.origin.z;
		Mat4 Mv = M2.times(M1);
		
		Vec4 temp = Mv.transform(new Vec4(direction));
		direction.x = temp.x;
		direction.y = temp.y;
		direction.z = temp.z;
		
		Ray r = new Ray( camera.origin, direction );
		return r.trace( null, maxReflectionDepth );
		
	}

	//randomArray en randomVec3 maken het array G met random vectoren
	public static Vec3[] randomArray() {
		int i;
		Vec3[] r = new Vec3[256];	  
		for (i=0; i<r.length; i++) {
			r[i] = randomVec3();			
		}  
		return r;
	}
	
	public static Vec3 randomVec3() {
		float x, y, z;
		x = (float) (2*Math.random()-1);
		y = (float) (2*Math.random()-1);
		z = (float) (2*Math.random()-1);
		Vec3 v = new Vec3(x, y, z);
		if (v.lengthSquared()>=1) randomVec3();
		return v.normalize();
	}
	//textureMap maakt een array P met random gesorteerde integers van 0 tot 255.
	public static int[] textureMap() {
		int w;
		int[]a = new int[256];
		
		List randomList = new ArrayList(256);
		  for (w=0; w<256; w++) {
		    randomList.add(new Integer(w));
		  }
		  Collections.shuffle(randomList);
		  for (w=0; w<256; w++) {
			  a[w] = ((Integer) randomList.get(w)).intValue();
		  } 
		return a;
	}
	
	public static void main(String args[]) {
		Tracer mainFrame = new Tracer();
		mainFrame.setTitle("Tracer");
		
		System.out.println( "Parsing scene description." );
		mainFrame.loadScene();
		mainFrame.setSize(Tracer.width+10, Tracer.height+50);
		mainFrame.setVisible(true);
		mainFrame.render();
		
	}
}
