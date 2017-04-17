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
    		Directory exifDirectory = meta.getFirstDirectoryOfType(ExifDirectoryBase.class);
    		
    		lista.add("---");
			try {
				
	    		addListString(lista, exifDirectory, "Make", ExifDirectoryBase.TAG_MAKE);
	    		addListString(lista, exifDirectory, "Model", ExifDirectoryBase.TAG_MODEL);
	    			    		
	    		addListFloat(lista, exifDirectory, "Focal length", ExifDirectoryBase.TAG_FOCAL_LENGTH);
	    		addListFloat(lista, exifDirectory, "Focal length 35mm", ExifDirectoryBase.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);
	    		
	    		addListString(lista, exifDirectory, "Aperture", ExifDirectoryBase.TAG_APERTURE);
	    		addListString(lista, exifDirectory, "Exposure time", ExifDirectoryBase.TAG_SHUTTER_SPEED);
	    		addListString(lista, exifDirectory, "ISO", ExifDirectoryBase.TAG_ISO_EQUIVALENT);
	    		
	    		addListString(lista, exifDirectory, "Flash", ExifDirectoryBase.TAG_FLASH);
	    		addListString(lista, exifDirectory, "Metering", ExifDirectoryBase.TAG_METERING_MODE);
	    		addListString(lista, exifDirectory, "White balance", ExifDirectoryBase.TAG_WHITE_BALANCE);
	    		addListString(lista, exifDirectory, "White balance mode", ExifDirectoryBase.TAG_WHITE_BALANCE_MODE);
	    		addListString(lista, exifDirectory, "White point", ExifDirectoryBase.TAG_WHITE_POINT);
	    		addListString(lista, exifDirectory, "Sensing method", ExifDirectoryBase.TAG_SENSING_METHOD);
	    		addListString(lista, exifDirectory, "Program", ExifDirectoryBase.TAG_EXPOSURE_PROGRAM);
	    		
	    		addListString(lista, exifDirectory, "Color space", ExifDirectoryBase.TAG_COLOR_SPACE);
//	    		addListString(lista, exifDirectory, "Focus mode", );
	    		addListFloat(lista, exifDirectory, "Subject distance", ExifDirectoryBase.TAG_SUBJECT_DISTANCE);
	    					
				if(exifDirectory.containsTag(ExifDirectoryBase.TAG_APERTURE) &&
						exifDirectory.containsTag(ExifDirectoryBase.TAG_EXPOSURE_TIME) &&
						exifDirectory.containsTag(ExifDirectoryBase.TAG_ISO_EQUIVALENT)) {
					double ap = exifDirectory.getDouble(ExifDirectoryBase.TAG_APERTURE);
					double time = exifDirectory.getDouble(ExifDirectoryBase.TAG_EXPOSURE_TIME);
					double iso = exifDirectory.getDouble(ExifDirectoryBase.TAG_ISO_EQUIVALENT);
		    		double ev = calcEV(iso, time, ap);
		    		lista.add("Abs EV: "+NumberFormat.getInstance().format(ev));
				}
	    		
	    	    
			} catch (MetadataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			lista.add("---");
    		
			for(Tag tag : exifDirectory.getTags()) {
		        hash.put(tag.getTagType(), tag);
//		        System.out.println(tag);
		        //if(map.containsKey(new Integer(tag.getTagType()))) {
						lista.add(tag.getTagName()+": "+tag.getDescription());
		        //}
		    }
    		

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
