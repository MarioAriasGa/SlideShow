package com.github.marioariasga.slideshow;

import java.io.IOException;

//OSX: import com.github.marioariasga.slideshow.mac.MacAppListener;
//OSX: import com.apple.eawt.Application;


public class SSLauncher {
	
	public static void main(String[] args) throws IOException {
		if(args.length>=1) {
			System.out.println("Initialized with URI: "+args[0]);
			SSController.getInstance().startWithURI(args[0]);
		} else {
			System.out.println("Initialized without path");
			SSController.getInstance().startWithoutPath();
//			log.debug("Initializing with mac listener");
			//OSX Application.getApplication().addApplicationListener(new MacAppListener());
		}
	}
	
}
