package com.github.marioariasga.slideshow;

import java.awt.Point;
import java.io.File;

import com.github.marioariasga.slideshow.algorithm.ImageHistogram;
import com.github.marioariasga.slideshow.utils.StopWatch;

import com.drew.metadata.Metadata;

public abstract class SSImage {

	private File file;
	private Metadata meta=null;
	private ImageHistogram histogram=null;
	
	public static final int MODE_FIT = 1;
	public static final int MODE_ZOOM = 2;
	protected int mode = MODE_FIT;

	protected int offsetx=0;
	protected int offsety=0;
	
	protected double fitZoom=1.0f;
	protected double zoom=1.0f;
	protected double angle=0.0f;
	
	protected int index;
	protected long loadtime=0;
	
	public SSImage(File file) {
		this.file = file;
	}
	
	public abstract int getWidth();

	public abstract int getHeight();
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int total) {
		this.index = total;
	}

	public Metadata getMeta() {
		return meta;
	}

	public void setMeta(Metadata meta) {
		this.meta = meta;
	}

	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}

	public void calcFitZoom(double screenWidth, double screenHeight) {
		double imageWidth = getWidth();
		double imageHeight = getHeight();
		double imgAspect = imageWidth/imageHeight;
		double scrAspect = screenWidth/screenHeight;
		if(imgAspect>scrAspect) {
			fitZoom = screenWidth/imageWidth;
		} else {
			fitZoom = screenHeight/imageHeight;
		}
	}

	public void setZoom(double i) {
		zoom = Math.max(i, 0.1);
		
		mode = MODE_ZOOM;
	}
	
	public void increaseZoom() {
		setZoom(getZoom() * 1.2f);
	}
	
	public void decreaseZoom() {
		setZoom(getZoom() / 1.2f);
	}
	
	public double getZoom() {
		if(isAdjust()) 
			return fitZoom;
		return zoom;
	}
	
	public void setAdjust() {
		mode = MODE_FIT;
		zoom = fitZoom;
		offsetx=0;
		offsety=0;
		angle=0;
	}
	
	public boolean isAdjust() {
		return mode == MODE_FIT;
	}
	
	private void tidiOffset() {
		/*0
		if(offsetx<0) offsetx = 0;
		if(offsety<0) offsety = 0;
		if(offsetx>getWidth()) 
			offsetx = getWidth();
		if(offsety>getHeight()) 
			offsety = getHeight();
		*/
	}

	public void goUp() {
		int width = getWidth();
		offsety-= width/10;
		tidiOffset();
		System.out.println("UP");
	}
	
	public void goDown() {
		int width = getWidth();
		offsety+= width/10;
		tidiOffset();
		System.out.println("DOWN");
	}
	
	public void goLeft() {
		int width = getWidth();
		offsetx-=width/10;
		tidiOffset();
		System.out.println("LEFT");
	}
	
	public void goRight() {
		int width = getWidth();
		offsetx+=width/10;
		tidiOffset();
		System.out.println("RIGHT");;
	}
	
	public void setOffset(Point p1, Point p2) {
		offsetx -= p2.x - p1.x;
		offsety -= p2.y - p1.y;
		tidiOffset();
	}

	public long estimateSize() {
		return getHeight()*getWidth()*3;
	}

	public void setLoadTime(long measure) {
		this.loadtime = measure;
	}

	public String getLoadTime() {
		return StopWatch.toHuman(this.loadtime);
	}

	public void rotateLeft() {
		angle += 90.0f;
		//if(angle>=360) angle = angle % 360;
	}
	
	public void rotateRight() {
		angle -= 90.0f;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	public double getAngle() {
		return angle;
	}

	public double getOffsetX() {
		return offsetx;
	}
	
	public double getOffsetY() {
		return offsety;
	}

	public ImageHistogram getHistogram() {
		return histogram;
	}

	public void setHistogram(ImageHistogram histogram) {
		this.histogram = histogram;
	}
}
