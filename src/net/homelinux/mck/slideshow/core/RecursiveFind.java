package net.homelinux.mck.slideshow.core;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RecursiveFind {
	List<String> l = null;
	long totalSize;
	
	public RecursiveFind() {
		l = new ArrayList<String>();
	}
	
	public void findAll(File path) {
		if(!path.isDirectory())
			path = path.getParentFile();
		totalSize = visitAllDirsAndFiles(path);
		System.out.println("Read "+l.size()+ " files.");
		System.out.println("Total size: "+getTotalSize());
	}
	
	public String getTotalSize() {
		return Utils.tidyFileSize(totalSize);
	}
	
	public void randomize() {
		Collections.shuffle(l);
	}

	public String get(int num) {
		return l.get(num);
	}
    
    public int getSize() {
    	return l.size();
    }
    
    public void remove(int num){
    	l.remove(num);
    }
    
    public void remove(File f){
    	l.remove(f.getAbsolutePath());
    }
    
    public void print() {
    	for (int i = 0; i < l.size(); i++) {
			System.out.println(l.get(i));
		}
    }
	
	   // Process all files and directories under dir
    private long visitAllDirsAndFiles(File dir) {
    	long size = 0;
    	if(dir.isFile()) {
    		size += dir.length();
    		process(dir);
    	}
    
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                size += visitAllDirsAndFiles(new File(dir, children[i]));
            }
        }
        return size;
    }
    
    private void process(File file) {
    	if(acceptFile(file)) {
    		l.add(file.getAbsolutePath());
//  		System.out.println(file.getAbsolutePath());
    	}
    }
    
    private boolean acceptFile(File file) {
    	String name = file.getAbsolutePath();
    	name = name.toLowerCase();
    	if(name.contains("iphoto library/data")) {
    		return false;
    	}
    	if(name.contains("jpg") || name.contains("jpeg")|| name.contains("png")) {
    		return true;
    	}
    	return false;
    }


	public void close() {
		l=null;
	}
}
