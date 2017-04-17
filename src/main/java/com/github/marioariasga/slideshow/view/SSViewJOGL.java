package net.homelinux.mck.slideshow.view;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.TraceGL2;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import net.homelinux.mck.slideshow.EventController;
import net.homelinux.mck.slideshow.SSController;
import net.homelinux.mck.slideshow.SSImage;
import net.homelinux.mck.slideshow.SSImageTexture;
import net.homelinux.mck.slideshow.algorithm.ImageHistogram;
import net.homelinux.mck.slideshow.finder.ImageCache;
import net.homelinux.mck.slideshow.finder.ImageFinder;
import net.homelinux.mck.slideshow.finder.ImageManagerInterface;
import net.homelinux.mck.slideshow.finder.ImportantExif;
import net.homelinux.mck.slideshow.utils.FpsCounter;
import net.homelinux.mck.slideshow.utils.StopWatch;
import net.homelinux.mck.slideshow.utils.Utils;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.TextureCoords;


public class SSViewJOGL extends SSView implements GLEventListener {
	private static final long serialVersionUID = 904378501605668274L;
	
    private static final GLU glu = new GLU();
    private static final GLUT glut = new GLUT();
    
    private FpsCounter fps = new FpsCounter();
	
    private String imagePath=null;
	//private Texture tex=null;
    public static int maxTextureSize = 1024;
	private TextureGroup tex1= null;
	private TextureGroup texOld= null;
	
	private double currentAlpha = 1.0f;
	private double currentZoom = 1.0f;
	private double currentAngle = 0.0f;
	private double currentOffsetX = 0.0f;
	private double currentOffsetY = 0.0f;
	
	private GLCanvas canvas;

	private int windowWidth = 0;
	private int windowHeight = 0;
	
	
	public SSViewJOGL(SSController controller) {
		super(controller);
		createWindow();
	}

	public void refresh() {
		
	}
	
    public void display(GLAutoDrawable gLDrawable) {
    	fps.newFrame();
        final GL2 gl = (GL2) gLDrawable.getGL();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        //gl.glTranslatef(0.0f, 0.0f, -1f);
 
        if(getImage()==null) return;
        
        if(!getImage().getFile().getAbsolutePath().equals(imagePath)) {
        	try{
        		StopWatch s = new StopWatch();
        		if(texOld!=null) {
        			texOld.dispose(gl);
        		}
        		texOld = tex1;
        		
            	currentAlpha = 0.0f;
        		getImage().calcFitZoom(windowWidth, windowHeight);
        		getImage().setAdjust();
        		
        		// Disable transition
        		/*currentZoom = getImage().getZoom();
        		currentAlpha = 1.0f;*/
        		
        		tex1 = new TextureGroup(gl, (SSImageTexture) getImage());
        		imagePath = getImage().getFile().getAbsolutePath();
        		System.out.println("Texture loaded: "+s.stopAndShow());
        	} catch (Exception e) {
        		tex1 = null;
        		System.out.println("Error loading texture: "+e);
        	}
        }
        
    	currentZoom = offset(currentZoom, getImage().getZoom(), 0.2f);
    	currentAngle = offset(currentAngle, getImage().getAngle(), 0.08f);
    	currentOffsetX = offset(currentOffsetX, getImage().getOffsetX(), 0.2f);
    	currentOffsetY = offset(currentOffsetY, getImage().getOffsetY(), 0.2f);
        //currentAlpha = offset(currentAlpha, 1.0f, 0.25f);
        if(currentAlpha<0.95f) {
        	currentAlpha+=0.05f;
        } else {
        	currentAlpha=1.0f;
        }
     	
        if(currentAlpha!=1.0f) {
        	drawImage(gl, texOld, 1-currentAlpha);
        }
        drawImage(gl, tex1, currentAlpha);
        
		if(showMemory) paintMemory(gl);
		if(showFileName) paintFileName(gl);
		if(showMetadata) paintMetaData(gl);
		if(showHistogram) paintHistogram(gl);
		if(showCache) paintCacheStatus(gl);
		
//		paintHistogram2(gl);
		//testRound(gl);
		//paintText(gLDrawable);
		
		//gLDrawable.swapBuffers();
		gl.glFlush();
    }
    
    private double offset(double current, double destiny, double factor) {
    	double diff = destiny-current;
		if(Math.abs(diff)>0.002) {
			return current + diff * factor;
		}
		return destiny;
    }
    
    private void drawImage(GL2 gl, TextureGroup tex, double alpha)
    {
    	if(tex==null) return;
    	
    	SSImage img = tex.getImage();
    	if(img==null) return;

    	// Calc size
    	double imageWidth = img.getWidth();
    	double imageHeight = img.getHeight();
    	double clipWidth = imageWidth/tex.numTile();
    	double clipHeight = imageHeight/tex.numTile();
				
    	// DRAW
		TextureCoords tc = tex.get(0).getImageTexCoords();
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glPushMatrix();
			
			gl.glTranslated(-currentOffsetX, currentOffsetY, 0.0);
		
			gl.glTranslated(windowWidth/2, windowHeight/2, 0.0);
			gl.glRotated(currentAngle, 0.0, 0.0, 1.0);
			gl.glScaled(currentZoom, currentZoom, 1.0);
		
			
			gl.glTranslated(-imageWidth/2, -imageHeight/2, 0.0);

			double top, left, right, bottom;
			
			gl.glColor4d(1.0, 1.0, 1.0, alpha);
			//gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT,GL2.GL_NICEST);
			gl.glEnable(GL2.GL_POLYGON_SMOOTH);
			for(int i=0;i<tex.numTile();i++) {
				for(int j=0;j<tex.numTile();j++) {
		    		tex.get(j*tex.numTile()+i).bind(gl);
		    		
		    		left = clipWidth*i;
		    		right = left+clipWidth;
		    		bottom = clipHeight*j;
		    		top = bottom+clipHeight;
		    		
		    		gl.glBegin(GL2.GL_QUADS);
		    			gl.glNormal3f(0.0f, 0.0f, -1.0f);
		    			//gl.glColor3f(1.0f, 0.0f, 0.0f);
			    		gl.glTexCoord2f(tc.left(), tc.bottom());
			    		gl.glVertex3d( left, bottom, 0);
			    		
			    		//gl.glColor3f(1.0f, 1.0f, 0.0f);
			    		gl.glTexCoord2f(tc.right(), tc.bottom());
			    		gl.glVertex3d( right, bottom, 0);
			    		
			    		//gl.glColor3f(0.0f, 0.0f, 1.0f);
			    		gl.glTexCoord2f(tc.right(), tc.top());
			    		gl.glVertex3d( right, top, 0);
			    		
			    		//gl.glColor3f(0.0f, 1.0f, 0.0f);
			    		gl.glTexCoord2f(tc.left(), tc.top());
			    		gl.glVertex3d( left, top, 0);
			    	gl.glEnd();
				}
			}
	    	
		gl.glPopMatrix();
		gl.glDisable(GL2.GL_TEXTURE_2D);
    }
    
	public void paintMemory(GL2 gl) {
		String label = Utils.getMemory()+ " FPS: "+fps.getFPS()+" Angle: "+currentAngle+" Zoom: "+currentZoom+"/"+getImage().getZoom()+" Alpha: "+currentAlpha;
		int msgwidth = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, label);

		gl.glPushMatrix();
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(0f, 25, 0f);
			gl.glVertex3f(msgwidth+10, 25, 0f);
			gl.glVertex3f(msgwidth+10, 0f, 0f);
		gl.glEnd();
		
		gl.glPopMatrix();
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	
		gl.glRasterPos2f(5f, 8f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, label);
	}
	
	public void paintFileName(GL2 gl) {
		String label = getImage().getFile().getAbsolutePath();
		int index = getImage().getIndex()+1;
		int total = SSController.getInstance().getFinder().getSize();
		int totalSize = SSController.getInstance().getFinder().getTotalSize();
		label = "( "+index+" / "+total+" ) "+totalSize+"  "+label;
		frame.setTitle(label);
		int msgwidth = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, label);

		gl.glPushMatrix();
		gl.glTranslatef(0, windowHeight-22, 0.0f);
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.4f);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0f, 0f, 0.1f);
			gl.glVertex3f(0f, 25, 0.1f);
			gl.glVertex3f(msgwidth+10, 25, 0.1f);
			gl.glVertex3f(msgwidth+10, 0f, 0.1f);
		gl.glEnd();
		
		gl.glPopMatrix();
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	
		gl.glRasterPos2f(+5.0f, windowHeight - 15);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, label);
	}
	
	public void write(GL2 gl, int width, int height, int rounded, int corner) {
		// Draw rectangles
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glEnd();
		// Draw corners
	}
	
	public void testRound(GL2 gl) {
	
		gl.glColor3f(0.0f, 0.0f, 0.5f);

		int steps = 12;

		double sc = 15.0;

		double I1x = 100.0;
		double I1y = 20.0;

		double delta = ((Math.PI/2.0) / (double)steps);
		double lastX = I1x - sc;
		double lastY = I1y;			

		for( double w = delta, i = 1; i <= steps; w += delta, ++i ) 
		{
			double x = -Math.cos(w);
			double y = -Math.sin(w);
			double x1 = I1x + (x * sc);
			double y1 = I1y + (y * sc);


			gl.glBegin(GL2.GL_TRIANGLES);
			gl.glVertex2d(lastX, lastY);
			gl.glVertex2d(x1, y1);
			gl.glVertex2d(I1x, I1y);
			gl.glEnd();

			lastX = x1;
			lastY = y1;
		}
	}
	
	public void paintMetaData(GL2 gl) {
		List<String> metaStrings = ImportantExif.getInstance().getImportantMeta(image);
		int maxWidth = 0;
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		
		for(int i=0;i<metaStrings.size();i++){
			String str = metaStrings.get(i);
			//gl.glRasterPos2f(-screenWidth/2+5, screenHeight/2 - 15*i - 35);
			gl.glRasterPos2f(5.0f, windowHeight - 15*i -35);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, str);
			int width = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, str);
			if(width>maxWidth) { maxWidth = width; }
		}
	}
	/*
	private double fun(double val, double max) {
		double norm = val*100/max;
//		return Math.log(norm+1);
		return Math.log10(norm+1);
//		return val;
	}

	private double fun2(double val, double max) {
//		double norm = val*100/max;
//		return Math.log(norm+1);
		return val;
	}
	
	public void paintHistogram(GL gl) {
		Histogram h = image.getHistogram();
		
		double histWidth = 512.0f;
		double histHeight = 200.0f;
		
		double max = 0;
		
	    for (int i=0; i< h.getNumBins(0); i++) {
//		    	 System.out.println(i+" - "+h.getBinSize(0, i) + " " + h.getBinSize(1, i) + " " + h.getBinSize(2, i));
	    		
	    	max = Math.max(h.getBinSize(0, i), max);
	    	max = Math.max(h.getBinSize(1, i), max);
	    	max = Math.max(h.getBinSize(2, i), max);
	     }
	    
	      
		
		gl.glPushMatrix();
		gl.glTranslatef(10.0f, 30.0f, 0.0f);
		
		// Background
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.6f);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(0, histHeight, 0);
			gl.glVertex3d(histWidth, histHeight, 0);
			gl.glVertex3d(histWidth, 0, 0);
		gl.glEnd();
		
		// White square
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(0, histHeight, 0);
			gl.glVertex3d(histWidth, histHeight, 0);
			gl.glVertex3d(histWidth, 0, 0);
			gl.glVertex3d(0, 0, 0);
		gl.glEnd();
		
		 for(int j=0 ; j < h.getNumBands() ; j++)
	        {
	            
	            gl.glColor4f(j==0 ? 1 : 0, j==1 ? 1 : 0, j==2 ? 1 : 0, 0.4f);
	            
//	            max = h.getNumBins(j);
	            double stepY = histHeight / fun(max,max);  
	            double stepX = histWidth / (h.getNumBins(j)-1);
//			        System.out.println("StepX: "+stepX+ "StepY: "+stepY);
	            
	            
	            gl.glBegin(GL2.GL_QUAD_STRIP);
	            gl.glVertex3d(0, 0, 0);
	            
//	            System.out.println("test");
//	            System.out.println("Fun1: " +fun(0,max));
//	            System.out.println("Fun2: " +fun(max/2,max));
//	            System.out.println("Fun3: " +fun(max,max));
 
	            for ( int i = 0; i < h.getNumBins(j); i++ )
	            {
	                double x = (i*stepX);
	                double y = (fun((double)h.getBinSize(j,i),max)*stepY);
//	                double y = fun(i,max)*stepY;
//		                System.out.println(h.getBinSize(j,i)+ "   x="+x+" y="+y);
	            
	                gl.glVertex3d(x, y, 0);
	                gl.glVertex3d(x, 0, 0);
	            }
	            
	            gl.glVertex3d(histWidth, 0, 0);
	            gl.glEnd();	          
	        }
		 gl.glPopMatrix();
	}
	
	public void paintHistogram2(GL gl) {
		Histogram h = image.getHistogram();
		
		double histWidth = 512.0f;
		double histHeight = 200.0f;
		
		double max = 0;
		
	    for (int i=0; i< h.getNumBins(0); i++) {
//		    	 System.out.println(i+" - "+h.getBinSize(0, i) + " " + h.getBinSize(1, i) + " " + h.getBinSize(2, i));
	    		
	    	max = Math.max(h.getBinSize(0, i), max);
	    	max = Math.max(h.getBinSize(1, i), max);
	    	max = Math.max(h.getBinSize(2, i), max);
	     }
	    
	      
		
		gl.glPushMatrix();
		gl.glTranslatef(10.0f*3 +512, 30.0f, 0.0f);
		
		// Background
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.6f);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(0, histHeight, 0);
			gl.glVertex3d(histWidth, histHeight, 0);
			gl.glVertex3d(histWidth, 0, 0);
		gl.glEnd();
		
		// White square
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(0, histHeight, 0);
			gl.glVertex3d(histWidth, histHeight, 0);
			gl.glVertex3d(histWidth, 0, 0);
			gl.glVertex3d(0, 0, 0);
		gl.glEnd();
		
		 for(int j=0 ; j < h.getNumBands() ; j++)
	        {
	            
	            if(j==0) 
	            {
	                gl.glColor4f(1.0f, 0.0f, 0.f, 0.4f);
	            }
	            else if(j==1)
	            {
	            	gl.glColor4f(0.0f, 1.0f, 0.f, 0.4f);
	            }
	            else if(j==2)
	            {
	            	gl.glColor4f(0.0f, 0.0f, 1.f, 0.4f);	              
	            }
	            
//	            max = h.getNumBins(j);
	            double stepY = histHeight / fun2(max,max);  
	            double stepX = histWidth / (h.getNumBins(j)-1);
//			        System.out.println("StepX: "+stepX+ "StepY: "+stepY);
	            
	            
	            gl.glBegin(GL2.GL_QUAD_STRIP);
	            gl.glVertex3d(0, 0, 0);
	            
//	            System.out.println("test");
//	            System.out.println("Fun1: " +fun(0,max));
//	            System.out.println("Fun2: " +fun(max/2,max));
//	            System.out.println("Fun3: " +fun(max,max));
 
	            for ( int i = 0; i < h.getNumBins(j); i++ )
	            {
	                double x = (i*stepX);
	                double y = (fun2((double)h.getBinSize(j,i),max)*stepY);
//	                double y = fun2(i,max)*stepY;
//		                System.out.println(h.getBinSize(j,i)+ "   x="+x+" y="+y);
	            
	                gl.glVertex3d(x, y, 0);
	                gl.glVertex3d(x, 0, 0);
	            }
	            
	            gl.glVertex3d(histWidth, 0, 0);
	            gl.glEnd();	          
	        }
		 gl.glPopMatrix();
	}*/
 

	
	public void paintHistogram(GL2 gl) {
		ImageHistogram h = image.getHistogram();
		
		double histWidth = 512.0f;
		double histHeight = 200.0f;
		
		double max = h.getMaxvalue();

	   		
		gl.glPushMatrix();
		gl.glTranslatef(10.0f, 30.0f, 0.0f);
		
		// Background
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.6f);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(0, histHeight, 0);
			gl.glVertex3d(histWidth, histHeight, 0);
			gl.glVertex3d(histWidth, 0, 0);
		gl.glEnd();
		
		// White square
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_LINE_STRIP);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(0, histHeight, 0);
			gl.glVertex3d(histWidth, histHeight, 0);
			gl.glVertex3d(histWidth, 0, 0);
			gl.glVertex3d(0, 0, 0);
		gl.glEnd();
		
		 for(int j=0 ; j < h.getNumLayers() ; j++)
	        {
	            
	            gl.glColor4f(j==0 ? 1 : 0, j==1 ? 1 : 0, j==2 ? 1 : 0, 0.4f);
	            
	            double stepY = histHeight / ImageHistogram.fun(max,max);  
	            double stepX = histWidth / (h.getNumBands()-1);
	            
	            gl.glBegin(GL2.GL_QUAD_STRIP);
	            gl.glVertex3d(0, 0, 0);
	            
	            for ( int i = 0; i < h.getNumBands(); i++ )
	            {
	                double x = (i*stepX);
	                double y = (ImageHistogram.fun((double)h.getPixel(j,i),max)*stepY);
//	                double y = fun(i,max)*stepY;
//		            System.out.println(h.getBinSize(j,i)+ "   x="+x+" y="+y);
	            
	                gl.glVertex3d(x, y, 0);
	                gl.glVertex3d(x, 0, 0);
	            }
	            
	            gl.glVertex3d(histWidth, 0, 0);
	            gl.glEnd();	          
	        }
		 gl.glPopMatrix();
	}
	
    private void paintCacheStatus(GL2 gl) {
    	try {
    		ImageManagerInterface finder = SSController.getInstance().getFinder();
    		if (finder instanceof ImageFinder) {
				ImageFinder imgfinder = (ImageFinder) finder;
				
	    		ImageCache cache = imgfinder.getCache();

	    		int current = cache.getCurrentIndex();
	    		int min = 10; //ImageCache.KEEP_PREVIOUS;
	    		int max = 10; //ImageCache.KEEP_NEXT;
	    		int total = max + min;
	    		
	    		gl.glPushMatrix();
	    		gl.glTranslatef(windowWidth - (total*20), 5.0f, 0.0f);
	    		/*
	    		g2d.setColor(Color.black);
	    		g2d.fillRoundRect(posx-25, posy-5, ((total+1)*20), 20, 20, 20);
	    		g2d.setColor(Color.white);
	    		g2d.drawRoundRect(posx-25, posy-5, ((total+1)*20), 20, 20, 20);
	    		*/
	    		
	    		for(int i=-min;i<=max;i++) {
	    			int index = current+i;
	    			double posx = (i*20)+total*10;
	    			
	    			if(i==0) {
	    				if(cache.isAvailable(index)) {
	    					gl.glColor4f(0.3f, 1.0f, 0.3f, 1.0f); // Light Green
	    					
	    				} else {
	    					gl.glColor4f(0.1f, 0.5f, 0.1f, 1.0f);	// Dark green
	    				}
	    			} else {
	    				if(cache.isAvailable(index)) {
	    					gl.glColor4f(1.0f, 0.2f, 0.2f, 1.0f); // Light red
	    				} else {
	    					gl.glColor4f(0.4f, 0.0f, 0.0f, 1.0f); // Dark red
	    				}
	    			} 
	    			gl.glBegin(GL2.GL_QUADS);
	    			gl.glVertex3d(posx, 0, 0);
	    			gl.glVertex3d(posx+10, 0, 0);
	    			gl.glVertex3d(posx+10, 10 , 0);
	    			gl.glVertex3d(posx, 10, 0);
	    			gl.glEnd();
//	    			g2d.fillOval(posx+(i*20)+(total*10), posy, 10, 10);
	    		}
	    		gl.glPopMatrix();
			}

    	} catch(Throwable e) {

    	}
    }

	
    public void displayChanged(GLAutoDrawable gLDrawable, 
      boolean modeChanged, boolean deviceChanged) {
    }
 
    public void init(GLAutoDrawable gLDrawable) {
        final GL2 gl = (GL2) gLDrawable.getGL();
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_BLEND);                    //activate blending mode
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
//        gl.glBlendFunc(GL2.GL_DST_ALPHA, GL2.GL_ZERO);
        gl.setSwapInterval(1);
        
        int buf[] = new int[1];
        gl.glGetIntegerv(GL2.GL_MAX_TEXTURE_SIZE, buf, 0);
        maxTextureSize = buf[0];
        System.out.println("Texture size: "+buf[0]);
    }
 
    public void reshape(GLAutoDrawable gLDrawable, int x, 
    int y, int width, int height) {
    	System.out.println("reshape("+x+","+y+" "+width+","+height+")");
        final GL2 gl = (GL2) gLDrawable.getGL();
        windowWidth = width;
        windowHeight = height;
        
        gl.glMatrixMode (GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        /*if(height <= 0) {height = 1;}
        final double h = width / height;
        glu.gluPerspective(50.0f, h, 1.0, 1000.0);*/
        
        gl.glOrtho(0, width, 0, height, -1, 1);
        gl.glMatrixMode (GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        if(getImage()!=null) {
    		getImage().calcFitZoom(windowWidth, windowHeight);	
    	}
    }
    
    public void setFullScreen(boolean fs) {
    	super.setFullScreen(fs);
//		canvas.requestFocus();
		texOld = null;
		tex1 = null;
		imagePath = null;
    }
	private void createWindow() {
		frame = new Frame("Slideshow");
		GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);
		canvas = new GLCanvas(caps);

		// DEBUG
		canvas.setGL(new TraceGL2((GL2) canvas.getGL(),System.out));
		canvas.addGLEventListener(this);

		frame.add(canvas);
		frame.setSize(windowScreenWidth, windowsScreenHeight);


		Animator animator = new Animator(canvas);
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

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}
