package net.homelinux.mck.slideshow.finder;

import java.io.File;
import java.util.Stack;

import net.homelinux.mck.slideshow.SSImage;
import net.homelinux.mck.slideshow.utils.GUIUtils;

public class ImageFinder implements ImageManagerInterface {

	Stack<ImageCache> finders = new Stack<ImageCache>();

	ImageCache cache;
	private File basePath;
	
	public ImageFinder(String path) {
		if(!path.contains("http://")) {
			File base =  new File(path);
			if(base.isFile()) {
				this.basePath = base.getParentFile();
			} else {
				this.basePath = base;
			}
			path = basePath.getAbsolutePath();
		}
		createCache(path);
		ImageCache.setActive(cache);
	}
	
	private void createCache(String uri) {
		cache = new ImageCache(uri);
	}
	
	public ImageCache getCache() {
		return cache;
	}

	public SSImage getCurrent() {
		return cache.getCurrent();
	}
	
	public int getSize() {
		return cache.getSize();
	}
	
	public int getTotalSize() {
		return cache.getRecursiveFind().getSize();
	}
	
	public void goPrevious(int num){
		cache.goPrevious(num);
	}
	
	public void goNext(int num) {
		cache.goNext(num);
	}
	
	public void previousGallery() {
		cache.previousGallery();
	}

	public void nextGallery() {
		cache.nextGallery();
	}
	
	
	public void enter() {
		File old = cache.getPath();
		File newer = getCurrent().getFile().getParentFile();

		if(!old.getPath().equals(newer.getPath())) {
			System.out.println("ENTER: "+newer);
			// Deactivate outter
			cache.flushCache();
			finders.push(cache);
			
			// Create inners
			createCache(newer.getAbsolutePath());
		}
	}
	
	public void exit() {
		if(finders.size()>0) {
			System.out.println("EXIT");
			
			// Destroy inner
			cache.flushCache();
			
			// Activate outer
			cache = finders.pop();
			ImageCache.setActive(cache);
			cache.updateAll();
			System.out.println("Exit OK: "+finders.size());
		}
	}

	public void randomize() {
		cache.randomize();
	}

	public void deleteImage() {
		File f = getCurrent().getFile();
		if(GUIUtils.confirm("Desea borrar IMAGEN: "+f)) {
			System.out.println("BORRANDO: "+f);
			cache.deleteFile();
		}
	}
	
	public void deleteGallery() {
		File f = getCurrent().getFile().getParentFile();
		if(GUIUtils.confirm("Desea borrar GALERIA: "+f)) {
			System.out.println("BORRANDO: "+f);
			cache.deleteGallery();
		}
	}

	public void gotoRandom() {
		cache.gotoRandom();
	}

}
