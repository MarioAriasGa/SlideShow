package net.homelinux.mck.slideshow.core;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import net.homelinux.mck.slideshow.SSController;
import net.homelinux.mck.slideshow.SSImage;

public class ImageCache {
	private File path;
	private RecursiveFind rf;
	private Map<Integer, SSImage> map = new ConcurrentHashMap<Integer, SSImage>();
	int lastCurrent = -1;
	int currentIndex = 0;
	int search = 0;
	
	// WARNING: KEEP should be always >= PRELOAD
	public static final int KEEP_PREVIOUS = 1;
	public static final int KEEP_NEXT = 3;
	
	public static final int PRELOAD_PREVIOUS = 1;
	public static final int PRELOAD_NEXT = 2;
	
	private static ImageCache active=null;
	
	
	
	public ImageCache(File path) {
		this.path = path;
		rf = new RecursiveFind();
		rf.findAll(path);
		ImageCache.setActive(this);
		CacheThreads.ensureStarted(Runtime.getRuntime().availableProcessors());
		updateAll();
	}
	
	public File getPath() {
		return this.path;
	}
	
	public RecursiveFind getRecursiveFind() {
		return rf;
	}

	public boolean isAvailable(int num) {
		return map.containsKey(new Integer(num));
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
		return null;
	}
	
	private boolean insertImage(int num) {
		Integer numInt = new Integer(num);
		if(!map.containsKey(numInt)){
			if( (num>=0) && (num<rf.getSize()) && num>=currentIndex-KEEP_PREVIOUS && num<=currentIndex+KEEP_NEXT ) {
				SSImage newImg;
				try {
					System.out.println(" +"+Thread.currentThread().getName()+" Reading "+rf.get(num));
					newImg = ImageLoader.openImage(rf.get(num));
					map.put(numInt, newImg);
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
			CacheThreads.queue.add(intIdx);
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
	}
	
	public void reset() {
		map.clear();
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
						rf.remove(file);
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
	


	public void rotate(int pos) {
		SSImage imfi = get(currentIndex);
		File f = imfi.getFile();
		BufferedImage img = imfi.getImg();
		 
		StopWatch st = new StopWatch();
		BufferedImage img2 = new BufferedImage(img.getHeight(), img.getWidth(),BufferedImage.TYPE_3BYTE_BGR);
		   // rotate the image

        AffineTransform rotation = new AffineTransform();

        if(pos == 0)
            rotation.rotate( (-1) * Math.PI / 2, img.getWidth() / 2, img.getHeight() / 2);
        else
            rotation.rotate( Math.PI / 2, img.getWidth() / 2, img.getHeight() / 2);
 
        Graphics2D g2d = img2.createGraphics();
        g2d.transform(rotation);
 
        if(pos == 0)
            g2d.drawImage( img, null, (img.getWidth() / 2 - img2.getWidth() / 2 ) * -1, (img.getHeight() / 2 - img2.getHeight() / 2));
        else
            g2d.drawImage( img, null, (img.getWidth() / 2 - img2.getWidth() / 2 ), (img.getHeight() / 2 - img2.getHeight() / 2) * -1);
        
        System.out.println("Rotated in: "+st.stopAndShow());
        
		try {
			ImageIO.write(img2, "jpeg", f);
			get(currentIndex).setImg(img2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	public void rotate3(int pos){
		SSImage imfi = get(currentIndex);
		File f = imfi.getFile();
		BufferedImage img = imfi.getImg();
		 
		   // rotate the image
		long a = System.currentTimeMillis();	
        // Create bitmasks
        int masks[] = new int[4];
              masks[0] = 0xff0000; // red
              masks[1] = 0x00ff00; // green
              masks[2] = 0x0000ff; // blue
              masks[3] = 0xff000000; // alpha
 
        // Create simple pixel samplemodel
        SampleModel sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, img.getHeight(), img.getWidth(), masks);
 
 
        // Create int array for raw data -> databuffer & samplemodel -> raster
        int [] rawData = new int[img.getWidth() * img.getHeight()];
        DataBufferInt db = new DataBufferInt(rawData, rawData.length);
        WritableRaster ras = Raster.createWritableRaster(sm, db, null);
 
 
        // Create colorspace -> directcolormodel (int)
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel cm = new DirectColorModel(cs,
                                            32,
                                            masks[0],
                                            masks[1],
                                            masks[2],
                                            masks[3],
                                            false,
                                            DataBuffer.TYPE_INT);
 
     
        // Create the bufferedimage
        BufferedImage img2 = new BufferedImage(cm, ras, false, null);
		
		int [] iArray1=null;
		int [] iArray2=null;
		
		iArray1 = img.getData().getPixels(0, 0, img.getWidth(), img.getHeight(), iArray1);
		
		for(int i=0;i < img.getWidth(); i++) {
			for(int j=0;j<img.getHeight();j++) {
				rawData[i*img.getHeight()+j] = i+j;
			}
		}
		
		long b = System.currentTimeMillis();
		System.out.println("Tiempo: "+(b-a));
		
		try {
			ImageIO.write(img2, "jpeg", new File(f.getParentFile(),"test.jpg"));
//			get(currentIndex).setImg(img2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/

	public void close() {
		map=null;
		rf.close();
		rf=null;
	}


}
