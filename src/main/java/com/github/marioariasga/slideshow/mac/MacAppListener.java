package com.github.marioariasga.slideshow.mac;

import com.github.marioariasga.slideshow.SSController;


/* OSX

import org.apache.log4j.Logger;

import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;


public class MacAppListener implements ApplicationListener {
	
	private Logger log = Logger.getLogger(getClass().getName());
	
	public void handleAbout(ApplicationEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void handleOpenApplication(ApplicationEvent arg0) {
		log.debug("OpenApplication: "+arg0.getFilename());
		SSController.getInstance().startWithoutPath();
	}

	public void handleOpenFile(ApplicationEvent arg0) {
		log.debug("Openfile: "+arg0.getFilename());
		SSController.getInstance().startWithURI(arg0.getFilename());
	}

	public void handlePreferences(ApplicationEvent arg0) {

	}

	public void handlePrintFile(ApplicationEvent arg0) {

	}

	public void handleQuit(ApplicationEvent arg0) {
		log.debug("Quit: "+arg0.getFilename());
	}

	public void handleReOpenApplication(ApplicationEvent arg0) {
		log.debug("ReOpenFile: "+arg0.getFilename());
	}

}
*/