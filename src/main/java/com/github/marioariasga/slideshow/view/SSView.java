package com.github.marioariasga.slideshow.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.util.Timer;

/* OSX
import com.apple.eawt.Application;
import com.apple.eawt.FullScreenUtilities;
*/

import com.github.marioariasga.slideshow.SSController;
import com.github.marioariasga.slideshow.SSImage;
import com.github.marioariasga.slideshow.utils.Message;

public abstract class SSView extends Component implements ViewInterface {
	private static final long serialVersionUID = -8633607810693739885L;

	protected SSController controller;
	protected SSImage image;
	protected Message message;
	protected Point windowPos = null;
	protected int windowScreenWidth = 640;
	protected int windowsScreenHeight = 480;
	protected boolean showFileName=false;
	protected boolean showMetadata=false;
	protected boolean showMemory=false;
	protected boolean showHistogram=false;
	protected boolean showCache=false;
	Timer timer;

	protected Frame frame = null;

	protected boolean fullScreen = false;
	
	public SSView(SSController controller) {
		this.controller = controller;
		this.setMessage("Initialized");
		
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(windowScreenWidth,windowsScreenHeight);
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
	
	public void toogleHistogram() {
		showHistogram=!showHistogram;
	}
	
	public void toogleCache() {
		showCache = !showCache;
	}
	
	public boolean getShowCache() {
		return showCache;
	}
	
	public boolean getShowHistogram() {
		return showHistogram;
	}

	public boolean isFullScreen() {
		return fullScreen;
	}

	public void setFullScreen(boolean fs) {
		if(frame==null) return;
		if( !fullScreen && fs ) {
			fullScreen = true;
			String os = System.getProperty("os.name");
			if(os.equals("Mac OS X")) {
				// OSX Application.getApplication().requestToggleFullScreen(frame);
			} else {
				windowScreenWidth = frame.getWidth();
				windowsScreenHeight = frame.getHeight();
				windowPos = frame.getLocationOnScreen();

				frame.dispose();
				frame.setUndecorated(true);
				frame.setAlwaysOnTop(true);
				//			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

				DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();

				frame.setLocation(0, 0);
				frame.setSize(mode.getWidth(), mode.getHeight());

				// switching to fullscreen mode
				if(!os.contains("Windows")) {
					GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().setFullScreenWindow(frame);
				}

				frame.setVisible(true);
				frame.repaint();
			}
		} else if(fullScreen && !fs){
			fullScreen = false;
			
			String os = System.getProperty("os.name");
			if(os.equals("Mac OS X")) {
				//OSX Application.getApplication().requestToggleFullScreen(frame);
			} else {
				if(!System.getProperty("os.name").contains("Windows")) {
					GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().setFullScreenWindow(null);
				}

				frame.dispose();
				frame.setLocation(windowPos);
				frame.setSize(windowScreenWidth, windowsScreenHeight);

				frame.setUndecorated(false);
				frame.setVisible(true);
				frame.repaint();
			}
		}
	}
}
