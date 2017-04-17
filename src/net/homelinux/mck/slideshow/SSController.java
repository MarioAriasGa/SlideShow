package net.homelinux.mck.slideshow;

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
	private SSView view;
	private ImageFinder finder;
	private Window window;
	private SlideshowTimer timer;
	

	private boolean pause=true;
	
	
	private SSController() {
		// Create instances

		eventController = new EventController(this);
		timer = new SlideshowTimer(this);
		view = new SSView(this);
		window = new Window(this);
		window.createWindow();
		eventController.loadInstances();
	}
	
	public void startWithoutPath() {
		String path = window.showDirSelect();
		if(path!=null) {
			startWithPath(path);
		} else {
			System.exit(0);
		}
	}
	
	public void startWithPath(String path) {
		log.debug("setPath: "+path);
		view.setMessage("Searching...");
		finder = new ImageFinder(path);
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

	public SSView getView() {
		return view;
	}

	public ImageFinder getFinder() {
		return finder;
	}

	public Window getWindow() {
		return window;
	}

	public SlideshowTimer getTimer() {
		return timer;
	}

}
