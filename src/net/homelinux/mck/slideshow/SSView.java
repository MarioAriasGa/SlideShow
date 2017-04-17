package net.homelinux.mck.slideshow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.timer.TimerMBean;

import net.homelinux.mck.slideshow.core.ImageCache;
import net.homelinux.mck.slideshow.core.StopWatch;
import net.homelinux.mck.slideshow.core.Utils;

public class SSView extends Component {

	private static final long serialVersionUID = -8633607810693739885L;

	private SSController controller;
	private SSImage image;
	private String message;
	private long messageTime;
	private boolean showFileName=false;
	private boolean showMetadata=false;
	private boolean showMemory=false;
	Timer timer;
	
	public SSView(SSController controller) {
		this.controller = controller;
		this.setMessage("Initialized");
		
	}
	
	public void refresh() {
		this.repaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension(800,600);
    }
	
	
	int frames = 0;
	int fps = 0;
	long fpsTime = System.currentTimeMillis();
	
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		
		frames++;
		long now = System.currentTimeMillis();
		
		if( (now-fpsTime) > 1000) {
			fpsTime = now;
			fps = frames;
			frames = 0;
		}
    	//System.out.println("*");
    	
    	g.setColor(Color.black);
    	g.fillRect(0,0,3000,3000);
    	
    	
    	paintImage(g);
    	
    	paintMessage(g);
    	
    	paintMemory(g);
    	
    	paintCacheStatus(g);
    	
    	//paintBall(g);
    }
	
	
	int ballx = 0;
	private void paintBall(Graphics g) {
		ballx = (int) (Math.sin( ((double)System.currentTimeMillis())/200)*100);
		g.setColor(Color.red);
		g.fillOval(ballx+500, 300-ballx, 20, 20);
	}


    public void setMessageTemp(String message) {
    	this.setMessage(message, 4000);
    }

    
    public void setMessage(String message) {
    	this.message = message;
    	this.messageTime = Long.MAX_VALUE;
    	this.refresh();
    }
    
    public void setMessage(String message, long duration) {
    	this.message = message;
    	this.messageTime = System.currentTimeMillis() + duration;
    }
    
    private void paintMessage(Graphics g) {
    	// Mensaje
    	if(messageTime>System.currentTimeMillis()) {
    		FontMetrics m = g.getFontMetrics();
    		int msgwidth = m.stringWidth(message);
    		int msgheight = m.getHeight();
    		int space = 8;

    		g.setColor(Color.black);
    		g.fillRect(getWidth()-msgwidth-(2*space), 0, getWidth(), msgheight+space);

    		g.setColor(Color.white);
    		g.drawString(message, getWidth()-msgwidth-space, msgheight);

    	}
    }
    
    Color c1 = new Color(0.3f, 1.0f, 0.3f); // Light Green
	Color c2 = new Color(0.1f, 0.5f, 0.1f);	// Dark green
	Color c3 = new Color(1.0f, 0.2f, 0.2f); // Light red
	Color c4 = new Color(0.4f, 0.0f, 0.0f); // Dark red
    
    private void paintCacheStatus(Graphics g) {
    	try {
    		Graphics2D g2d = (Graphics2D) g;
    		//g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

    		ImageCache cache = SSController.getInstance().getFinder().getCache();

    		int current = cache.getCurrentIndex();
    		int min = ImageCache.KEEP_PREVIOUS;
    		int max = ImageCache.KEEP_NEXT;
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
    					g2d.setColor(c3);
    				} else {
    					g2d.setColor(c4);
    				}
    			} else {
    				if(cache.isAvailable(index)) {
    					g2d.setColor(c1);
    				} else {
    					g2d.setColor(c2);
    				}
    			}    			
    			g2d.fillOval(posx+(i*20), posy, 10, 10);
    		}
    	} catch(Throwable e) {

    	}
    }
    
	public void paintFileName(Graphics g) {
    		String label = image.getFile().getAbsolutePath();
    		int index = image.getIndex()+1;
    		int total = SSController.getInstance().getFinder().getSize();
    		String totalSize = SSController.getInstance().getFinder().getCache().getRecursiveFind().getTotalSize();
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
	
	int memposx = 0;
	AnimationObject obj = null;
	public void paintMemory(Graphics2D g) {
		
		String label = Utils.getMemory()+ " FPS: "+fps;
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
    
    public void setImage(SSImage img) {
    	image = img;
    }
    
    public SSImage getImage() {
    	return image;
    }
    
    public void paintImage(Graphics g) {
    	if(image!=null) {
    		image.paint(g);
    		if(showFileName) {
    			paintFileName(g);
    			if(showMetadata) {
    				image.showMetadata(g);
    			}
    		}
    	}
    }
    
    public void close() {
    	
    }

	public void toggleShowFileName() {
		showFileName=!showFileName;
	}

	public void toogleVerbose() {
		showMetadata=!showMetadata;
		showFileName = showMetadata;
	}
	
	public void toogleMemory() {
		showMemory=!showMemory;
	}
}
