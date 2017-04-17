package net.homelinux.mck.slideshow;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import net.homelinux.mck.slideshow.core.StopWatch;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;


public class SSViewJOGL extends SSView implements GLEventListener {
	private static final long serialVersionUID = 904378501605668274L;
    private static final GLU glu = new GLU();
	private String imagePath=null;
	//private Texture tex=null;
	private TextureGroup tex= null;
	private float currentZoom = 1.0f;
	private float currentAngle = 0.0f;
	private float currentOffsetX = 0.0f;
	private float currentOffsetY = 0.0f;
	
	public SSViewJOGL(SSController controller) {
		super(controller);
		createWindow();
	}

	public void refresh() {
		
	}


	
    public void display(GLAutoDrawable gLDrawable) {
        final GL gl = gLDrawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -1f);
 
        if(getImage()==null) return;
        
        if(!getImage().getFile().getAbsolutePath().equals(imagePath)) {
        	try{
        		if(tex!=null) {
        			//tex.disable();
        			for(int i=0;i<4;i++)
        				tex.get(i).dispose();
        		}
        		StopWatch s = new StopWatch();
        		//tex = TextureIO.newTexture(getImage().getImg(), false);
        		//tex = TextureIO.newTexture(getImage().getFile(), false);
        		tex = new TextureGroup(gl, getImage().getImg());
        		
        		imagePath = getImage().getFile().getAbsolutePath();
        		System.out.println("Texture loaded: "+s.stopAndShow());
        	} catch (Exception e) {
        		tex = null;
        		System.out.println("Error loading texture: "+e);
        	}
        }
        drawImage(gl, tex);
    }
    
    private float offset(float current, float destiny) {
    	float diff = destiny-current;
		if(Math.abs(diff)>0.01) {
			return current + diff * 0.3f;
		}
		return current;
    }
    
    private void drawImage(GL gl, TextureGroup tex)
    {
    	if(tex==null) return;

    	// Calc size
		TextureCoords tc = tex.get(0).getImageTexCoords();
		float aspect = tex.get(0).getAspectRatio();
    	float halfWidth = 0.5f;
    	float halfHeight = 0.5f;

    	if(aspect>0) {
    		halfWidth *= aspect;
    	} else {
    		halfHeight *= aspect;
    	}
		
		// Calc zoom
    	currentZoom = offset(currentZoom, getImage().getZoom());
    	currentAngle = offset(currentAngle, getImage() .getAngle());
    	currentOffsetX = offset(currentOffsetX, getImage().getOffsetX()/500);
    	currentOffsetY = offset(currentOffsetY, getImage().getOffsetY()/500);
    	
		float diffZoom = getImage().getZoom()-currentZoom;
		if(Math.abs(diffZoom)>0.01) {
			currentZoom+= diffZoom * 0.3;
		}
		
		float diffAngle = getImage().getAngle()-currentAngle;
		if(Math.abs(diffAngle)>0.01) {
			currentAngle+= diffAngle * 0.3;
		}
		
		
		
		
		gl.glPushMatrix();
			
			gl.glRotatef(currentAngle, 0.0f, 0.0f, 1.0f);
			gl.glScalef(currentZoom, currentZoom, 1.0f);
			gl.glTranslatef(-currentOffsetX , currentOffsetY, 0.0f);
					
    		//gl.glTranslatef( -halfFaceSize, -halfFaceSize2,  0.001f ); // move position of the texture to this coord

			float top, left, right, bottom;
			
			for(int i=0;i<4;i++) {
	    		tex.get(i).bind();
	    		
	    		if(i==0 || i==2) {
	    			left = -halfWidth;
	    			right = 0;
	    		} else {
	    			left = 0;
	    			right = halfWidth;
	    		}
	    		
	    		if(i<2) {
	    			top = 0;
	    			bottom = -halfHeight;
	    		} else {
	    			top = halfHeight;
	    			bottom = 0;
	    		}
	    		
	    		gl.glBegin(GL.GL_QUADS);
	    			gl.glNormal3f(0.0f, 0.0f, -1.0f);
	    			//gl.glColor3f(1.0f, 0.0f, 0.0f);
		    		gl.glTexCoord2f(tc.left(), tc.bottom());
		    		gl.glVertex3f( left, bottom, 0);
		    		
		    		//gl.glColor3f(1.0f, 1.0f, 0.0f);
		    		gl.glTexCoord2f(tc.right(), tc.bottom());
		    		gl.glVertex3f( right, bottom, 0);
		    		
		    		//gl.glColor3f(0.0f, 0.0f, 1.0f);
		    		gl.glTexCoord2f(tc.right(), tc.top());
		    		gl.glVertex3f( right, top, 0);
		    		
		    		//gl.glColor3f(0.0f, 1.0f, 0.0f);
		    		gl.glTexCoord2f(tc.left(), tc.top());
		    		gl.glVertex3f( left, top, 0);
		    	gl.glEnd();
			}
	    	
		gl.glPopMatrix();
    }
 
    public void displayChanged(GLAutoDrawable gLDrawable, 
      boolean modeChanged, boolean deviceChanged) {
    }
 
    public void init(GLAutoDrawable gLDrawable) {
        final GL gl = gLDrawable.getGL();
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_TEXTURE_2D);
        
        //tex1 = TextureLoader.load("E:\\dudoso\\fayelog\\99\\017.jpg");
        //tex2 = TextureLoader.load("E:\\dudoso\\fayelog\\99\\002.jpg");
        //tex = TextureLoader.load("src/tutorial/Textures/jogl.png");
        
        //gLDrawable.addKeyListener(this);
    }
 
    public void reshape(GLAutoDrawable gLDrawable, int x, 
    int y, int width, int height) {
        final GL gl = gLDrawable.getGL();
        if(height <= 0) {
            height = 1;
        }
        final float h = (float)width / (float)height;
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(50.0f, h, 1.0, 1000.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public void setFullScreen(boolean fs) {
    	imagePath = null;
    	super.setFullScreen(fs);
    }
	private void createWindow() {
		frame = new Frame("Slideshow");
		GLCanvas canvas = new GLCanvas();

		// DEBUG
		canvas.setGL(new DebugGL(canvas.getGL()));
		canvas.addGLEventListener(this);

		frame.add(canvas);
		frame.setSize(800, 600);
		/*frame.setUndecorated(true);
    	int size = frame.getExtendedState();
    	size |= Frame.MAXIMIZED_BOTH;
    	frame.setExtendedState(size);*/

		FPSAnimator animator = new FPSAnimator(canvas,30);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});	
		
        EventController eventController = controller.getEventController();
		canvas.addKeyListener(eventController);
		canvas.addMouseListener(eventController);
		canvas.addMouseMotionListener(eventController);
		canvas.addMouseWheelListener(eventController);
		
		frame.setVisible(true);
		canvas.requestFocus();
		animator.start();
	}
}
