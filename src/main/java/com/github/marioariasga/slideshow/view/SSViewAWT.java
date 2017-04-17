package com.github.marioariasga.slideshow.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

// OSX import com.apple.eawt.FullScreenUtilities;

import com.github.marioariasga.slideshow.EventController;
import com.github.marioariasga.slideshow.SSController;
import com.github.marioariasga.slideshow.SSImageBuffered;
import com.github.marioariasga.slideshow.finder.ImageManagerInterface;
import com.github.marioariasga.slideshow.finder.ImageCache;
import com.github.marioariasga.slideshow.finder.ImageFinder;
import com.github.marioariasga.slideshow.utils.AnimationObject;
import com.github.marioariasga.slideshow.utils.FpsCounter;
import com.github.marioariasga.slideshow.utils.Utils;


public class SSViewAWT extends SSView {
	FpsCounter fps = new FpsCounter();
	private static final long serialVersionUID = -7915018569324234464L;

	public SSViewAWT(SSController controller) {
		super(controller);
		createWindow();
	}
	
	public void refresh() {
		this.repaint();
	}
	
	public void paint(Graphics g1) {
		update(g1);
    }
	
	public void update(Graphics g1) {
		fps.newFrame();
		
		Graphics2D g = (Graphics2D) g1;
		
    	//System.out.println("*");
    	
    	g.setColor(Color.black);
    	g.fillRect(0,0,4000,4000);
    	
    	
    	paintImage(g);
    	
    	getMessage().paint(g);
    	
    	if(showMemory)
    		paintMemory(g);
    	
    	if(showCache)
    		paintCacheStatus(g);
    	
    	//paintBall(g);	
	}

	
	
	static Color[] cacheColors = {
		new Color(0.3f, 1.0f, 0.3f), // Light Green
		new Color(0.1f, 0.5f, 0.1f), // Dark green
		new Color(1.0f, 0.2f, 0.2f), // Light red
		new Color(0.4f, 0.0f, 0.0f)  // Dark red	
	};
    
    private void paintCacheStatus(Graphics g) {
    	try {
    		Graphics2D g2d = (Graphics2D) g;
    		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

    		ImageManagerInterface finder = SSController.getInstance().getFinder();
    		if (finder instanceof ImageFinder) {
				ImageFinder imgfinder = (ImageFinder) finder;
				
	    		ImageCache cache = imgfinder.getCache();

	    		int current = cache.getCurrentIndex();
	    		int min = 10; //ImageCache.KEEP_PREVIOUS;
	    		int max = 10; //ImageCache.KEEP_NEXT;
	    		int total = max + min;
	    		
	    		int posy = getHeight()-20;
	    		int posx = getWidth()-(total*20);
	    		
	    		g2d.setColor(Color.black);
	    		g2d.fillRoundRect(posx-25, posy-5, ((total+1)*20), 20, 20, 20);
	    		g2d.setColor(Color.white);
	    		g2d.drawRoundRect(posx-25, posy-5, ((total+1)*20), 20, 20, 20);
	    		
	    		for(int i=-min;i<=max;i++) {
	    			int index = current+i;
	    			if(i==0) {
	    				if(cache.isAvailable(index)) {
	    					g2d.setColor(cacheColors[2]);
	    				} else {
	    					g2d.setColor(cacheColors[3]);
	    				}
	    			} else {
	    				if(cache.isAvailable(index)) {
	    					g2d.setColor(cacheColors[0]);
	    				} else {
	    					g2d.setColor(cacheColors[1]);
	    				}
	    			}    			
	    			g2d.fillOval(posx+(i*20)+(total*10), posy, 10, 10);
	    		}
			}

    	} catch(Throwable e) {

    	}
    }
    
	int memposx = 0;
	AnimationObject obj = null;
	public void paintMemory(Graphics2D g) {
		
		String label = Utils.getMemory()+ " FPS: "+fps.getFPS();
		FontMetrics m = g.getFontMetrics();
		int msgwidth = m.stringWidth(label);
		int msgheight = m.getHeight();
		int space = 8;
		
		AffineTransform t = g.getTransform();
		if(obj!=null) {
			memposx = (int)obj.getValue();
		}
		g.translate(memposx, 0);	

		g.setColor(Color.black);
		g.fillRect(0, getHeight()-msgheight-space, msgwidth+2*space, getHeight());
		g.setColor(Color.white);
		g.drawString(label, space, getHeight()-space);
	
		g.setTransform(t);
	}
	
    public void paintImage(Graphics g) {
    	SSImageBuffered image = (SSImageBuffered) getImage();
    	if(image!=null) {
    		image.paint(this, g);
    		if(showFileName) {
    			paintFileName(g);
    			if(showMetadata) {
    				image.showMetadata(g);
    			}
    		}
    		
    		if(showHistogram) {
    			image.showHistogram((Graphics2D)g);
    		}
    	}
    }
    
	public void paintFileName(Graphics g) {
		String label = getImage().getFile().getAbsolutePath();
		int index = getImage().getIndex()+1;
		int total = SSController.getInstance().getFinder().getSize();
		long totalSize = SSController.getInstance().getFinder().getTotalSize();
		label = "( "+index+" / "+total+" ) "+totalSize+"  "+label;
		FontMetrics m = g.getFontMetrics();
		int msgwidth = m.stringWidth(label);
		int msgheight = m.getHeight();
		int space = 8;
		g.setColor(Color.black);
		g.fillRect(0, 0, msgwidth+(2*space), msgheight+space);
		g.setColor(Color.white);
		
		g.drawString(label, space, msgheight);
	}
	
	public void createWindow() {	
        frame = new JFrame("SlideShow");
        EventController eventController = controller.getEventController();
        
// OSX
//        if(System.getProperty("os.name").equals("Mac OS X")) {
//        	FullScreenUtilities.setWindowCanFullScreen(frame, true);
//        }
        
		// Exitig program on mouse click
		frame.addKeyListener(eventController);
		frame.addMouseListener(eventController);
		frame.addMouseMotionListener(eventController);
		frame.addMouseWheelListener(eventController);
        
        frame.add("Center", this);
        frame.pack();

        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
	}
}
