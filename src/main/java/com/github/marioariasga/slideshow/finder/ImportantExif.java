package net.homelinux.mck.slideshow.finder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.homelinux.mck.slideshow.SSImage;
import net.homelinux.mck.slideshow.algorithm.ImageHistogram;
import net.homelinux.mck.slideshow.utils.Utils;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;

public class ImportantExif {
	private static ImportantExif instance=null;
	
	public static ImportantExif getInstance() {
		if(instance==null) {
			instance = new ImportantExif();
		}
		return instance;
	}
	
	private Set<Integer> map;
	
	public ImportantExif() {
		map = new HashSet<Integer>();
		map.add(new Integer(ExifDirectory.TAG_MAKE));
		map.add(new Integer(ExifDirectory.TAG_MODEL));
		map.add(new Integer(ExifDirectory.TAG_DATETIME_DIGITIZED));
		map.add(new Integer(ExifDirectory.TAG_FLASH));
		map.add(new Integer(ExifDirectory.TAG_FOCAL_LENGTH));
		map.add(new Integer(ExifDirectory.TAG_EXPOSURE_TIME));
		map.add(new Integer(ExifDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH));
		map.add(new Integer(ExifDirectory.TAG_APERTURE));
		map.add(new Integer(ExifDirectory.TAG_METERING_MODE));
		map.add(new Integer(ExifDirectory.TAG_ISO_EQUIVALENT));
		map.add(new Integer(ExifDirectory.TAG_SUBJECT_DISTANCE));
	}
	
	public static void showExif(Metadata metadata) {
		
		Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
		String cameraMake = exifDirectory.getString(ExifDirectory.TAG_MAKE);
		System.out.println(cameraMake);
		String cameraModel = exifDirectory.getString(ExifDirectory.TAG_MODEL);
		System.out.println(cameraModel);
		
//		 iterate through metadata directories
		Iterator directories = metadata.getDirectoryIterator();
		while (directories.hasNext()) {
		    Directory directory = (Directory)directories.next();
		    System.out.println("Directory: "+directory.getName());
		    // iterate through tags and print to System.out
		    Iterator tags = directory.getTagIterator();
		    while (tags.hasNext()) {
		        Tag tag = (Tag)tags.next();
		        // use Tag.toString()
		        try {
					System.out.println(tag.getTagName()+" | "+tag.getDescription()+"| ");
				} catch (MetadataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
	}
	
    public List<String> getImportantMeta(SSImage imgFile) {
    	Metadata meta = imgFile.getMeta();
    	List<String> lista = new ArrayList<String>();
    	
    	// Origin URL
    	File urlFile = new File(imgFile.getFile().getParentFile().getAbsolutePath()+"/url.txt");
    	if(urlFile.exists()) {
    		try {
    			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(urlFile), "UTF-8"));
    			String line;

    			if( (line=in.readLine()) != null) {
    				lista.add("URL: "+line);
    			}
    			in.close();
    		} catch (Throwable e) {	
    		}
    	}
    	
    	// Main Info
    	lista.add("Dimensiones: "+imgFile.getWidth()+"x"+imgFile.getHeight());
    	lista.add("Megapixel: "+Utils.getMegapixel(imgFile.getWidth(), imgFile.getHeight()));
    	
    	long diskSize = imgFile.getFile().length();
    	long memSize = imgFile.estimateSize();
    	lista.add("Taman disco: "+Utils.tidyFileSize(diskSize)+" ("+diskSize+" bytes)");
    	lista.add("Taman memoria: "+Utils.tidyFileSize(memSize)+" ("+memSize+" bytes)");
    	lista.add("Load time: "+imgFile.getLoadTime());
    	ImageHistogram h = imgFile.getHistogram();
    	if(h!=null) {
    		lista.add("Histogram time: "+h.getLoadTime());
    		lista.add("Histogram period: "+h.getPeriod());
    	}
    	
    	HashMap<Integer, Tag> hash = new HashMap<Integer, Tag>();

    	if(meta!=null) {
    		Directory exifDirectory = meta.getDirectory(ExifDirectory.class);
    		
    		lista.add("---");
			try {
				
	    		addListString(lista, exifDirectory, "Make", ExifDirectory.TAG_MAKE);
	    		addListString(lista, exifDirectory, "Model", ExifDirectory.TAG_MODEL);
	    			    		
	    		addListFloat(lista, exifDirectory, "Focal length", ExifDirectory.TAG_FOCAL_LENGTH);
	    		addListFloat(lista, exifDirectory, "Focal length 35mm", ExifDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);
	    		
	    		addListString(lista, exifDirectory, "Aperture", ExifDirectory.TAG_APERTURE);
	    		addListString(lista, exifDirectory, "Exposure time", ExifDirectory.TAG_SHUTTER_SPEED);
	    		addListString(lista, exifDirectory, "ISO", ExifDirectory.TAG_ISO_EQUIVALENT);
	    		
	    		addListString(lista, exifDirectory, "Flash", ExifDirectory.TAG_FLASH);
	    		addListString(lista, exifDirectory, "Metering", ExifDirectory.TAG_METERING_MODE);
	    		addListString(lista, exifDirectory, "White balance", ExifDirectory.TAG_WHITE_BALANCE);
	    		addListString(lista, exifDirectory, "White balance mode", ExifDirectory.TAG_WHITE_BALANCE_MODE);
	    		addListString(lista, exifDirectory, "White point", ExifDirectory.TAG_WHITE_POINT);
	    		addListString(lista, exifDirectory, "Sensing method", ExifDirectory.TAG_SENSING_METHOD);
	    		addListString(lista, exifDirectory, "Program", ExifDirectory.TAG_EXPOSURE_PROGRAM);
	    		
	    		addListString(lista, exifDirectory, "Color space", ExifDirectory.TAG_COLOR_SPACE);
//	    		addListString(lista, exifDirectory, "Focus mode", );
	    		addListFloat(lista, exifDirectory, "Subject distance", ExifDirectory.TAG_SUBJECT_DISTANCE);
	    					
				if(exifDirectory.containsTag(ExifDirectory.TAG_APERTURE) &&
						exifDirectory.containsTag(ExifDirectory.TAG_EXPOSURE_TIME) &&
						exifDirectory.containsTag(ExifDirectory.TAG_ISO_EQUIVALENT)) {
					double ap = exifDirectory.getDouble(ExifDirectory.TAG_APERTURE);
					double time = exifDirectory.getDouble(ExifDirectory.TAG_EXPOSURE_TIME);
					double iso = exifDirectory.getDouble(ExifDirectory.TAG_ISO_EQUIVALENT);
		    		double ev = calcEV(iso, time, ap);
		    		lista.add("Abs EV: "+NumberFormat.getInstance().format(ev));
				}
	    		
	    	    
			} catch (MetadataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			lista.add("---");
    		
    		
    		
    		Iterator tags = exifDirectory.getTagIterator();
    		    		
    		while (tags.hasNext()) {
		        Tag tag = (Tag) tags.next();
		        hash.put(tag.getTagType(), tag);
//		        System.out.println(tag);
		        //if(map.containsKey(new Integer(tag.getTagType()))) {
		        	try {
						lista.add(tag.getTagName()+": "+tag.getDescription());
					} catch (MetadataException e) {

					}
		        //}
		    }
    		

//    		lista.add(hash.get(ExifDirectory.TAG_MAKE));
//    		lista.add(hash.get(ExifDirectory.TAG_MODEL));
//    		lista.add(hash.get(ExifDirectory.TAG_DATETIME_DIGITIZED));
//    		lista.add(hash.get(ExifDirectory.TAG_FLASH));
//    		lista.add(hash.get(ExifDirectory.TAG_FOCAL_LENGTH));
//    		lista.add(hash.get(ExifDirectory.TAG_EXPOSURE_TIME));
//    		lista.add(hash.get(ExifDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH));
//    		lista.add(hash.get(ExifDirectory.TAG_APERTURE));
//    		lista.add(hash.get(ExifDirectory.TAG_METERING_MODE));
//    		lista.add(hash.get(ExifDirectory.TAG_ISO_EQUIVALENT));
//    		lista.add(hash.get(ExifDirectory.TAG_SUBJECT_DISTANCE));
    	}
    	
    	return lista;
    }
    
    public void addListDate(List<String> list, Directory dir, String desc, int item) {
    	try {
    		if(dir.containsTag(item)) {
    			list.add(desc+": "+dir.getDate(item));
    		} else {
    			list.add(desc+": --");
    		}
    	} catch (Exception e) {
    	}
    }
    
    public void addListString(List<String> list, Directory dir, String desc, int item) {
    	try {
    		if(dir.containsTag(item)) {
    			list.add(desc+": "+dir.getDescription(item));
    		} else {
    			list.add(desc+": --");
    		}
    	} catch (Exception e) {
    	}
    }
    
    public void addListFloat(List<String> list, Directory dir, String desc, int item) {
    	try {
    		if(dir.containsTag(item)) {
    			list.add(desc+": "+dir.getFloat(item));
    		} else {
    			list.add(desc+": --");
    		}
    	} catch (Exception e) {
    	}
    }
    
    public static double calcEV(double iso, double time, double ap) {
    	double ev = Math.log( ap*ap *iso / 100.0*time) / -Math.log(2.0);
    	return ev;
    }
    
    public static void main(String[] args) {
    	System.out.println("Caso luminoso: "+ImportantExif.calcEV(100, 0.001, 5.0));
    	System.out.println("Caso oscuro: "+ImportantExif.calcEV(1600, 2, 1.8));
		for(double iso = 100; iso<1610; iso*=2) {
			for(double time = 128; time > 0.001; time/=2) {
				double ap = 1.0;
				double ev = ImportantExif.calcEV(iso,time,ap);
				System.out.println("ISO: "+iso+"\t Aperture: "+ap+"\t Time: "+time+ "\t EV: "+ev);
			}
		}
	}
}
