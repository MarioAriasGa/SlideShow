package net.homelinux.mck.slideshow;

public class FpsCounter {
	int frames=0;
	int fps=0;
	long fpsTime=System.currentTimeMillis();
	
	void newFrame() {
		frames++;
		long now = System.currentTimeMillis();
		
		if( (now-fpsTime) > 1000) {
			fpsTime = now;
			fps = frames;
			frames = 0;
		}
	}
	
	int getFPS() {
		return fps;
	}
}
