package net.homelinux.mck.slideshow;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.util.Timer;

public abstract class SSView extends Component implements ViewInterface {
	private static final long serialVersionUID = -8633607810693739885L;

	protected SSController controller;
	protected SSImage image;
	protected Message message;
	protected boolean showFileName=false;
	protected boolean showMetadata=false;
	protected boolean showMemory=false;
	Timer timer;

	protected Frame frame = null;

	private boolean fullScreen = false;
	
	public SSView(SSController controller) {
		this.controller = controller;
		this.setMessage("Initialized");
		
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(800,600);
    }


	public void setMessage(Message message) {
		this.message = message;
		this.refresh();
	}
	
    public void setMessage(String message) {
    	this.setMessage(new Message(message));
    }
    
    public void setMessage(String message, long duration) {
    	this.setMessage(new Message(message, duration));
    }
   
    public void setImage(SSImage img) {
    	image = img;
    }
    
    public SSImage getImage() {
    	return image;
    }
    
    public Message getMessage() {
    	return message;
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

	public void toogleFullScreen() {
		setFullScreen(!fullScreen);
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(boolean fs) {
		if(frame==null) return;
		if( !fullScreen && fs ) {
			fullScreen = true;
	
			frame.dispose();
			frame.setUndecorated(true);
			
			DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
			
			frame.setLocation(0, 0);
			frame.setSize(mode.getWidth(), mode.getHeight());
			
			// switching to fullscreen mode
			if(!System.getProperty("os.name").contains("Windows")) {
				GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().setFullScreenWindow(frame);
			}
	
			
			frame.setVisible(true);
			frame.repaint();
		} else if(fullScreen && !fs){
			fullScreen = false;
			
			if(!System.getProperty("os.name").contains("Windows")) {
				GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().setFullScreenWindow(null);
			}
			
			frame.dispose();
			frame.setSize(800, 600);
	
			frame.setUndecorated(false);
			frame.setVisible(true);
			frame.repaint();
		}
	}
}
