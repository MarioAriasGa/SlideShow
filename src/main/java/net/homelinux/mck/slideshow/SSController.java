package net.homelinux.mck.slideshow;

import net.homelinux.mck.slideshow.finder.ImageManagerInterface;
import net.homelinux.mck.slideshow.finder.ImageFinder;
import net.homelinux.mck.slideshow.utils.Config;
import net.homelinux.mck.slideshow.utils.GUIUtils;
import net.homelinux.mck.slideshow.view.SSViewAWT;
import net.homelinux.mck.slideshow.view.SSViewJOGL;
import net.homelinux.mck.slideshow.view.ViewInterface;

import org.apache.log4j.Logger;

public class SSController {
	Logger log = Logger.getLogger(getClass().getName());
	
	private static SSController instance;
	
	public static SSController getInstance() {
		if(instance==null)
			instance = new SSController();
		return instance;
	}

	private EventController eventController;
	private ViewInterface view;
	private ImageFinder finder;
	private SlideshowTimer timer;
	
	private SSController() {
		// Create instances

		eventController = new EventController(this);
		timer = new SlideshowTimer(this);
		
		if(Config.getInstance().typeIsAWT()) {
			view = new SSViewAWT(this);
		} else if(Config.getInstance().typeIsJogl()) {
			view = new SSViewJOGL(this);
		}
		
		eventController.loadInstances();
	}
	
	public void startWithoutPath() {
		String path = GUIUtils.showDirSelect();
		if(path!=null) {
			startWithURI(path);
		} else {
			System.exit(0);
		}
	}
	
	public void startWithURI(String uri) {
		log.debug("setPath: "+uri);
		view.setMessage("Searching...");
		finder = new ImageFinder(uri);
		eventController.loadInstances();
		timer.start();
        refresh();
	}
	
	public void refresh() {
		SSImage img = finder.getCurrent();
		if(img!=null && img!=view.getImage()) {
			//System.out.println("Show image: "+img.getFile().getAbsolutePath());
			view.setImage(img);
		}
		view.refresh();
	}
	
	public void quit() {
		log.debug("QUIT");
		if(view!=null) {
			view.close();
		}
		System.exit(0);
	}

	public EventController getEventController() {
		return eventController;
	}

	public ViewInterface getView() {
		return view;
	}

	public ImageManagerInterface getFinder() {
		return finder;
	}

	public SlideshowTimer getTimer() {
		return timer;
	}

}
