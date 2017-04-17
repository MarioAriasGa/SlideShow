package com.github.marioariasga.slideshow.finder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.marioariasga.slideshow.SSImage;
import com.github.marioariasga.slideshow.algorithm.ImageHistogram;
import com.github.marioariasga.slideshow.utils.Utils;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

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
		map.add(new Integer(ExifDirectoryBase.TAG_MAKE));
		map.add(new Integer(ExifDirectoryBase.TAG_MODEL));
		map.add(new Integer(ExifDirectoryBase.TAG_DATETIME_DIGITIZED));
		map.add(new Integer(ExifDirectoryBase.TAG_FLASH));
		map.add(new Integer(ExifDirectoryBase.TAG_FOCAL_LENGTH));
		map.add(new Integer(ExifDirectoryBase.TAG_EXPOSURE_TIME));
		map.add(new Integer(ExifDirectoryBase.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH));
		map.add(new Integer(ExifDirectoryBase.TAG_APERTURE));
		map.add(new Integer(ExifDirectoryBase.TAG_METERING_MODE));
		map.add(new Integer(ExifDirectoryBase.TAG_ISO_EQUIVALENT));
		map.add(new Integer(ExifDirectoryBase.TAG_SUBJECT_DISTANCE));
	}
	
	public static void showExif(Metadata metadata) {
		
		Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
		String cameraMake = exifDirectory.getString(ExifDirectoryBase.TAG_MAKE);
		System.out.println(cameraMake);
		String cameraModel = exifDirectory.getString(ExifDirectoryBase.TAG_MODEL);
		System.out.println(cameraModel);
		
//		 iterate through metadata directories
		for( Directory directory : metadata.getDirectories() ){ 
		    System.out.println("Directory: "+directory.getName());
		    // iterate through tags and print to System.out
		    for(Tag tag : directory.getTags() ){
		        // use Tag.toString()
		    	System.out.println(tag.getTagName()+" | "+tag.getDescription()+"| ");
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
    	lista.add("Size: "+imgFile.getWidth()+"x"+imgFile.getHeight());
    	lista.add("Megapixel: "+Utils.getMegapixel(imgFile.getWidth(), imgFile.getHeight()));
    	
    	long diskSize = imgFile.getFile().length();
    	long memSize = imgFile.estimateSize();
    	lista.add("Disk size: "+Utils.tidyFileSize(diskSize)+" ("+diskSize+" bytes)");
    	lista.add("Mem size: "+Utils.tidyFileSize(memSize)+" ("+memSize+" bytes)");
    	lista.add("Load time: "+imgFile.getLoadTime());
    	ImageHistogram h = imgFile.getHistogram();
    	if(h!=null) {
    		lista.add("Histogram time: "+h.getLoadTime());
    		lista.add("Histogram period: "+h.getPeriod());
    	}
    	
    	HashMap<Integer, Tag> hash = new HashMap<Integer, Tag>();

    	ExifSubIFDDirectory subIFdir = meta.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
    	ExifIFD0Directory doDir = meta.getFirstDirectoryOfType(ExifIFD0Directory.class);
    	
    	if(meta!=null) {
    		lista.add("---");
			
	    		addListString(lista, doDir, "Make", ExifDirectoryBase.TAG_MAKE);
	    		addListString(lista, doDir, "Model", ExifDirectoryBase.TAG_MODEL);
	    		addListString(lista, subIFdir, "Lens", ExifDirectoryBase.TAG_LENS_SPECIFICATION);
	    			    		
	    		addListDate(lista, subIFdir, "Date taken", ExifDirectoryBase.TAG_DATETIME_ORIGINAL);
	    		addListFloat(lista, subIFdir, "Focal length", ExifDirectoryBase.TAG_FOCAL_LENGTH);
//	    		addListFloat(lista, subIFdir, "Focal length 35mm", ExifDirectoryBase.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);
	    		
	    		addListString(lista, subIFdir, "Aperture", ExifDirectoryBase.TAG_APERTURE);
	    		addListString(lista, subIFdir, "Exposure time", ExifDirectoryBase.TAG_SHUTTER_SPEED);
	    		addListString(lista, subIFdir, "Exposure bias", ExifDirectoryBase.TAG_EXPOSURE_BIAS);
	    		addListString(lista, subIFdir, "ISO", ExifDirectoryBase.TAG_ISO_EQUIVALENT);
	    		
	    		
	    		addListString(lista, subIFdir, "Flash", ExifDirectoryBase.TAG_FLASH);
	    		addListString(lista, subIFdir, "Metering", ExifDirectoryBase.TAG_METERING_MODE);
//	    		addListString(lista, subIFdir, "White balance", ExifDirectoryBase.TAG_WHITE_BALANCE);
	    		addListString(lista, subIFdir, "White balance", ExifDirectoryBase.TAG_WHITE_BALANCE_MODE);
//	    		addListString(lista, subIFdir, "White point", ExifDirectoryBase.TAG_WHITE_POINT);
//	    		addListString(lista, subIFdir, "Sensing method", ExifDirectoryBase.TAG_SENSING_METHOD);
	    		addListString(lista, subIFdir, "Program", ExifDirectoryBase.TAG_EXPOSURE_PROGRAM);
	    		
	    		
	    		
	    		addListString(lista, subIFdir, "Color space", ExifDirectoryBase.TAG_COLOR_SPACE);
//	    		addListFloat(lista, subIFdir, "Subject distance", ExifDirectoryBase.TAG_SUBJECT_DISTANCE);
	    					
				if(subIFdir.containsTag(ExifDirectoryBase.TAG_APERTURE) &&
						subIFdir.containsTag(ExifDirectoryBase.TAG_EXPOSURE_TIME) &&
						subIFdir.containsTag(ExifDirectoryBase.TAG_ISO_EQUIVALENT)) {
					try {
						double ap = subIFdir.getDouble(ExifDirectoryBase.TAG_APERTURE);
						double time = subIFdir.getDouble(ExifDirectoryBase.TAG_EXPOSURE_TIME);
						double iso = subIFdir.getDouble(ExifDirectoryBase.TAG_ISO_EQUIVALENT);
						double ev = calcEV(iso, time, ap);
						lista.add("Abs EV: "+NumberFormat.getInstance().format(ev));
					} catch (MetadataException e) {
						e.printStackTrace();
					}
				}
			
//			lista.add("---");
//    		
//			for(Tag tag : exifDirectory.getTags()) {
//		        hash.put(tag.getTagType(), tag);
////		        System.out.println(tag);
//		        //if(map.containsKey(new Integer(tag.getTagType()))) {
//						lista.add(tag.getTagName()+": "+tag.getDescription());
//		        //}
//		    }
    		

//    		lista.add(hash.get(ExifDirectoryBase.TAG_MAKE));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_MODEL));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_DATETIME_DIGITIZED));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_FLASH));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_FOCAL_LENGTH));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_EXPOSURE_TIME));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_APERTURE));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_METERING_MODE));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_ISO_EQUIVALENT));
//    		lista.add(hash.get(ExifDirectoryBase.TAG_SUBJECT_DISTANCE));
    	}
    	
    	return lista;
    }
    
    public void addListDate(List<String> list, Metadata meta, String desc, int item) {
    	for(Directory dir : meta.getDirectories())
    	{
    		if(dir.containsTag(item)) {
    			list.add(desc+": "+dir.getDate(item));
    			return;
    		}
    	}
    	list.add(desc+": --");
    }
    
    public void addListString(List<String> list, Metadata meta, String desc, int item) {
    	for(Directory dir : meta.getDirectories())
    	{
    		if(dir.containsTag(item)) {
    			list.add(desc+": "+dir.getString(item));
    			return;
    		}
    	}
    	list.add(desc+": --");
    }
    
    public void addListFloat(List<String> list, Metadata meta, String desc, int item) {
    	for(Directory dir : meta.getDirectories())
    	{
    		if(dir.containsTag(item)) {
    			try {
					list.add(desc+": "+dir.getFloat(item));
				} catch (MetadataException e) {
					e.printStackTrace();
				}
    			return;
    		}
    	}
    	list.add(desc+": --");
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
