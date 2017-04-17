package net.homelinux.mck.slideshow.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.homelinux.mck.slideshow.SSImage;

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
	
	private Map<Integer,Tag> map;
	
	public ImportantExif() {
		map = new HashMap<Integer, Tag>();
		map.put(new Integer(ExifDirectory.TAG_MAKE), null);
		map.put(new Integer(ExifDirectory.TAG_MODEL), null);
		map.put(new Integer(ExifDirectory.TAG_DATETIME_DIGITIZED), null);
		map.put(new Integer(ExifDirectory.TAG_FLASH), null);
		map.put(new Integer(ExifDirectory.TAG_FOCAL_LENGTH), null);
		map.put(new Integer(ExifDirectory.TAG_EXPOSURE_TIME), null);
		map.put(new Integer(ExifDirectory.TAG_APERTURE), null);
		map.put(new Integer(ExifDirectory.TAG_METERING_MODE), null);
		map.put(new Integer(ExifDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH), null);
		map.put(new Integer(ExifDirectory.TAG_SUBJECT_DISTANCE), null);
	}
	
	
    public List<String> getImportantMeta(SSImage imgFile) {
    	Metadata meta = imgFile.getMeta();
    	List<String> lista = new ArrayList<String>();
    	
    	lista.add("Dimensiones: "+imgFile.getImg().getWidth()+"x"+imgFile.getImg().getHeight());
    	
    	long diskSize = imgFile.getFile().length();
    	long memSize = imgFile.estimateSize();
    	lista.add("Taman disco: "+Utils.tidyFileSize(diskSize)+" ("+diskSize+" bytes)");
    	lista.add("Taman memoria: "+Utils.tidyFileSize(memSize)+" ("+memSize+" bytes)");
    	lista.add("Load time: "+imgFile.getLoadTime());

    	if(meta!=null) {
    		Directory exifDirectory = meta.getDirectory(ExifDirectory.class);
    		Iterator tags = exifDirectory.getTagIterator();
    		
    		while (tags.hasNext()) {
		        Tag tag = (Tag) tags.next();
//		        System.out.println(tag);
		        if(map.containsKey(new Integer(tag.getTagType()))) {
		        	try {
						lista.add(tag.getTagName()+": "+tag.getDescription());
					} catch (MetadataException e) {

					}
		        }
		    }
    	}
    	
    	return lista;
    }
}
