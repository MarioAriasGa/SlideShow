package net.homelinux.mck.slideshow.core;

import java.util.Iterator;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;

public class Utils {
	public static String tidyFileSize(long size ){
		long calcSize;
		String str;
		if (size >= 1024 * 1024 * 1024)
		{
			calcSize = (long) (((double)size) / (1024 * 1024 * 1024));
			str = ""+calcSize +"GB";
		}
		else if (size>= 1024 * 1024) {
			calcSize = (long) (((double)size) / (1024 * 1024 ));
			str = ""+ calcSize +"MB";
		}
		else if (size>= 1024) {
			calcSize = (long) (((double)size) / (1024));
			str = ""+ calcSize +"KB";
		}
		else {
			calcSize = size;
			str = ""+ calcSize +"GB";
		}
		return str;
	}
	
	public static String fechaExif(String fecha) {
		return fecha;
	}
	
	public static void showMemory(String label) {
    	System.out.println(label+": "+Utils.getMemory());
    			
	}
	
	public static String getMemory() {
		return Utils.tidyFileSize(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+" / "+
		Utils.tidyFileSize(Runtime.getRuntime().totalMemory())+" / "+
		Utils.tidyFileSize(Runtime.getRuntime().maxMemory());
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
}
