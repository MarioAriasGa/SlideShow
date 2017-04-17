package com.github.marioariasga.slideshow.finder;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.github.marioariasga.slideshow.SSController;
import com.github.marioariasga.slideshow.SSImage;
import com.github.marioariasga.slideshow.utils.StopWatch;

public class ImageCache {
	private String uri;
	private File path;
	private FinderInterface rf;
	private Map<Integer, SSImage> map = new ConcurrentHashMap<Integer, SSImage>();
	private Map<Integer, SSImage> weakMap = new WeakHashMap<Integer, SSImage>();
	
	int lastCurrent = -1;
	int currentIndex = 0;
	int search = 0;
	
	// WARNING: KEEP should be always >= PRELOAD
	public static final int KEEP_PREVIOUS = 8;
	public static final int KEEP_NEXT = 8;
	
	public static final int PRELOAD_PREVIOUS = 3;
	public static final int PRELOAD_NEXT = 3;
	
	private static ImageCache active=null;
	
	public ImageCache(String uri) {
		this.uri = uri;
		this.path = new File(uri);
		rf = new RecursiveFind();
		rf.findAll(uri);
		ImageCache.setActive(this);
		CacheThreads.ensureStarted(Runtime.getRuntime().availableProcessors());
		updateAll();
	}
	
	public ImageCache(File path) {
		this.path = path;
		rf = new RecursiveFind();
		rf.findAll(path.getAbsolutePath());
		ImageCache.setActive(this);
		CacheThreads.ensureStarted(Runtime.getRuntime().availableProcessors());
		updateAll();
	}
	
	public File getPath() {
		return this.path;
	}
	
	public FinderInterface getRecursiveFind() {
		return rf;
	}

	public boolean isAvailable(int num) {
		Integer intVal = new Integer(num);
		return map.containsKey(intVal) || weakMap.containsKey(intVal) ;
	}

	public SSImage getCurrent() {
		return get(currentIndex);
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public void goPrevious(int num){
		System.out.println("\nPREVIOUS!");
		if(currentIndex>0) {
			currentIndex= currentIndex>=num ? currentIndex-num : 0;
		}
		updateAll();
	}
	
	public void goNext(int num) {
		System.out.println("\nNEXT!");
		int max = getSize()-1;
		if(currentIndex<max) {
			currentIndex = currentIndex< max-num ? currentIndex+num : max;
		}
		updateAll();
	}
	
	public void gotoRandom() {
		Random r = new Random();
		currentIndex = r.nextInt(getSize());
		updateAll();
	}

	
	public void previousGallery() {
		if(currentIndex<1) return;
		
		File currentGal = getCurrent().getFile().getParentFile();
		File prevFileGal = new File(rf.get(currentIndex-1)).getParentFile();
		File toCompare=null;
		
		if(currentGal.equals(prevFileGal)) {
			toCompare=currentGal;
		} else {
			toCompare=prevFileGal;
		}
		
		// Find previous gallery
		int nextGalId = currentIndex-1;
		while(nextGalId>=0) {
			File f = new File(rf.get(nextGalId));
			if(nextGalId==0) {
				currentIndex=0;
				break;
			} else if(!f.getParentFile().equals(toCompare)) {
				currentIndex=nextGalId+1;
				break;
			}
			nextGalId--;
		}
		updateAll();
	}
	
	public void nextGallery() {
		File currentGal = getCurrent().getFile().getParentFile();
		
		int nextGalId = currentIndex+1;
		while(nextGalId<getSize()) {
			File f = new File(rf.get(nextGalId));
			if(!f.getParentFile().equals(currentGal)) {
				currentIndex = nextGalId;
				break;
			}
			nextGalId++;
		}
		updateAll();
	}
	
	public int getSize() {
		return rf.getSize();
	}
	
	public SSImage get(int num) {
		currentIndex = num;
		Integer val = new Integer(num);

		//insertImage(num);

		SSImage item = map.get(val);
		if(item!=null) {
			item.setIndex(num);
			return item;	
		}
		
		item = weakMap.get(val);
		if(item!=null) {
			item.setIndex(num);
			return item;	
		}
		
		return null;
	}
	
	private boolean insertImage(int num) {
		Integer numInt = new Integer(num);
		if(!map.containsKey(numInt)){
			SSImage tmp = weakMap.get(numInt);
			if(tmp!=null) {
				map.put(numInt,tmp);
			} else if( (num>=0) && (num<rf.getSize()) && num>=currentIndex-KEEP_PREVIOUS && num<=currentIndex+KEEP_NEXT ) {
				SSImage newImg;
				try {
					System.out.println(" +"+Thread.currentThread().getName()+" Reading "+rf.get(num));
					newImg = ImageLoader.openImage(rf.get(num));
					map.put(numInt, newImg);
					weakMap.put(numInt, newImg);
					return true;
				} catch (Exception e) {
					rf.remove(num);
					updateAll();
				}	
			}
		} 
		return false;
	}
	
	public void estimateSize() {
		long size = 0;
		for(Integer index : new ArrayList<Integer>(map.keySet())) {
			SSImage image = map.get(index.intValue());
			size+=image.estimateSize();
		}
	}
	
	public void removeOld() {
		CacheThreads.queue.clear();
		StopWatch st = new StopWatch();
		// Delete old
		for(Integer index : new ArrayList<Integer>(map.keySet())) {
			int idx = index.intValue();
			if(idx<currentIndex-KEEP_PREVIOUS || idx>currentIndex+KEEP_NEXT ) {
				map.remove(index);
			}
		}
		//System.gc();
		System.out.println("Removed old in: "+st.stopAndShow());
		/*Utils.showMemory("REMOVED");
		estimateSize();
		System.out.println(Thread.currentThread().getName()+" REMOVED OLD: "+map.size());*/
	}
	
	private void addQueue(int idx) {
		Integer intIdx = new Integer(idx);
		
		if(!map.containsKey(intIdx)) {
			SSImage tmp = weakMap.get(intIdx);
			if(tmp!=null) {
				map.put(intIdx, tmp);
			} else {
				CacheThreads.queue.add(intIdx);
			}
		}
	}
	
	public void updateAll() {
		removeOld();
		
		// First current
		addQueue(currentIndex);
		
		// Then next
		for(int i=currentIndex+1; i<=currentIndex+PRELOAD_NEXT; i++) {
			addQueue(i);
		}
		
		// Finally previous
		for(int i=currentIndex-PRELOAD_PREVIOUS; i<currentIndex; i++) {
			addQueue(i);
		}
	}
	
	public void update(int num) {
		if( insertImage(num) && currentIndex == num) {
			SSController.getInstance().refresh();
		}
		SSController.getInstance().getView().refresh();
	}
	
	public static void setActive(ImageCache newActive) {
		ImageCache.active = newActive;
	}
	
	public static ImageCache getActive() {
		return ImageCache.active;
	}
	
	public void flushCache() {
//		SSImage current = getCurrent();
//		stopBackgroundThread();
		CacheThreads.queue.clear();
		map.clear();
		weakMap.clear();
	}
	
	public void reset() {
		map.clear();
		weakMap.clear();
		currentIndex=0;
		lastCurrent=-1;
	}

	public void randomize() {
		rf.randomize();
		reset();
		updateAll();
	}

	public void deleteFile() {
		rf.remove(currentIndex);
		get(currentIndex).getFile().delete();
		if(currentIndex>rf.getSize()) {
			currentIndex=rf.getSize();
		}
		map.clear();
		updateAll();
	}
	
	public void deleteGallery() {
		File baseDir = get(currentIndex).getFile().getParentFile();
		System.out.println("Deleting gallery: "+baseDir.getAbsolutePath());
		try {
			if(baseDir.isDirectory()) {
				for(File file : baseDir.listFiles()) {
					if(file.isFile()) {
						System.out.println("\tDeleting: "+file);
						rf.remove(file.getAbsolutePath());
						file.delete();
					}
				}
				System.out.println("\tDeleting: "+baseDir);
				baseDir.delete();
			}
		} catch (Throwable e) {
			System.out.println("Could not delete file: "+e);
		}
		map.clear();
		updateAll();
	}


	public void close() {
		map=null;
		rf=null;
	}


}
