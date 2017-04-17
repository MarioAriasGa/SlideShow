package net.homelinux.mck.slideshow.finder;

import net.homelinux.mck.slideshow.SSImage;


public interface ImageManagerInterface {

	public SSImage getCurrent();
	public int getSize();
	public int getTotalSize();
	public void goPrevious(int num);
	
	public void goNext(int num);
	public void previousGallery();
	public void nextGallery();
	
	public void enter();
	public void exit();
	public void randomize();
	public void deleteImage();
	public void deleteGallery();
	public void gotoRandom();
}
