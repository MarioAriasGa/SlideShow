package com.github.marioariasga.slideshow;

import java.io.IOException;
import java.util.Properties;

import com.github.marioariasga.slideshow.mac.MacAppListener;

import org.apache.log4j.BasicConfigurator;

import com.apple.eawt.Application;


public class SSLauncher {
	//private static Logger log = Logger.getLogger(SSLauncher.class);
	
	public static void main(String[] args) throws IOException {
		/*try {
			String logConfig = "config/logs/traces.xml";
			DOMConfigurator.configure(logConfig);
		} catch (Exception e) {
			BasicConfigurator.configure();
		}*/
		//BasicConfigurator.configure();
		//log.debug("Log initialized");
		//System.in.read();

		if(args.length>=1) {
			System.out.println("Initialized with URI: "+args[0]);
			SSController.getInstance().startWithURI(args[0]);
		} else {
			System.out.println("Initialized without path");
			SSController.getInstance().startWithoutPath();
//			log.debug("Initializing with mac listener");
			Application.getApplication().addApplicationListener(new MacAppListener());
		}
	}
	
}
