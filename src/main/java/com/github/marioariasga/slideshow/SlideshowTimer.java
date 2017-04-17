package com.github.marioariasga.slideshow;

import javax.swing.Timer;

public class SlideshowTimer {
	private SSController controller;
	private Timer timer;
	private boolean pause=true;
	
	public SlideshowTimer(SSController controller) {
		this.controller = controller;
		timer = new Timer(4000,controller.getEventController());
	}
	
	public void resetTimer() {
		if(!pause) timer.restart();
	}
	
	public void start() {
		pause=false;
		timer.start();
		controller.getView().setMessage("Play",2000);
	}
	
	public void stop() {
		pause=true;
		timer.stop();
		controller.getView().setMessage("Pause");
	}

	public void tooglePause() {
		if(pause) {
			start();
		} else {
			stop();
		}
	}
	
	public boolean isPaused() {
		return pause;
	}
}
