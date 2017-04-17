package net.homelinux.mck.slideshow;

public class AnimationObject {
	double value;
	double startValue;
	double endValue;
	
	double time;
	double initialTime;
	
	AnimationObject(double start, double end, long time) {
		setAnimation(start, end, time);
	}
	
	void setAnimation(double start, double end, long time) {
		this.value = start;
		this.startValue = start;
		this.endValue = end;
		this.time = time;
		this.initialTime = System.currentTimeMillis();
	}
	
	double getValue() {
		double now = System.currentTimeMillis();
		return ((now-startValue)/time)*(endValue-startValue);
	}
}
