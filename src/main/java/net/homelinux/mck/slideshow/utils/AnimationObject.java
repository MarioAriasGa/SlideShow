package net.homelinux.mck.slideshow.utils;

public class AnimationObject {
	double value;
	double startValue;
	double endValue;
	
	double time;
	double initialTime;
	
	public AnimationObject(double start, double end, long time) {
		setAnimation(start, end, time);
	}
	
	public void setAnimation(double start, double end, long time) {
		this.value = start;
		this.startValue = start;
		this.endValue = end;
		this.time = time;
		this.initialTime = System.currentTimeMillis();
	}
	
	public double getValue() {
		double now = System.currentTimeMillis();
		return ((now-startValue)/time)*(endValue-startValue);
	}
}
