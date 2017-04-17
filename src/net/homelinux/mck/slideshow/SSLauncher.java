package net.homelinux.mck.slideshow;

import java.util.Properties;

import org.apache.log4j.BasicConfigurator;


public class SSLauncher {
	//private static Logger log = Logger.getLogger(SSLauncher.class);
	
	public static void main(String[] args) {
		/*try {
			String logConfig = "config/logs/traces.xml";
			DOMConfigurator.configure(logConfig);
		} catch (Exception e) {
			BasicConfigurator.configure();
		}*/
		//BasicConfigurator.configure();
		//log.debug("Log initialized");

		if(args.length>=1) {
			System.out.println("Initialized with path: "+args[0]);
			SSController.getInstance().startWithPath(args[0]);
		} else {
			System.out.println("Initialized without path");
			SSController.getInstance().startWithoutPath();
//			log.debug("Initializing with mac listener");
//			Application.getApplication().addApplicationListener(new MacAppListener());
		}
	}
	
}
