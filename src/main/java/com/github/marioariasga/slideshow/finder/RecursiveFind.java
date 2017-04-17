package com.github.marioariasga.slideshow.finder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.marioariasga.slideshow.utils.Utils;


public class RecursiveFind implements FinderInterface {
	List<String> l = null;
	long totalSize;
	
	public RecursiveFind() {
		l = new ArrayList<String>();
	}
	
	public void findAll(String path) {
		File dir = new File(path);
		if(!dir.isDirectory())
			dir = dir.getParentFile();
		totalSize = visitAllDirsAndFiles(dir);
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
    
    public void remove(String value){
    	l.remove(value);
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
            if(children!=null) {
//            	System.out.println(dir.getAbsolutePath());
	            List<String> list = new ArrayList<String>(children.length);
	           
	            for (int i=0; i<children.length; i++) { 
	            	list.add(children[i]);
	            }
	            
	            Collections.sort(list);
	            
	            for(int i=0;i<list.size();i++) {
	            	size += visitAllDirsAndFiles(new File(dir, list.get(i)));
	            }
            } else {
            	System.out.println("Dir empty: "+dir.getAbsolutePath());
            }
        }
        return size;
    }
    
    private synchronized void add(String path) {
    	l.add(path);
    }
    
    private void process(File file) {
    	if(acceptFile(file)) {
    		add(file.getAbsolutePath());
//  		System.out.println(file.getAbsolutePath());
    	}
    }
    
    private boolean acceptFile(File file) {
    	String name = file.getName();
    	String fullName = file.getAbsolutePath();
    	fullName = fullName.toLowerCase();
    	System.out.println("Length: "+file.toString()+" / "+file.length());
    	if(file.length()<100000) {
    		return false;
    	}
    	
    	if(fullName.contains("iphoto library/data")||name.charAt(0)=='.') {
    		return false;
    	}
    	if(fullName.contains("jpg") || fullName.contains("jpeg")|| fullName.contains("png") || fullName.contains("cr2")) {
    		return true;
    	}
    	return false;
    }


	public void close() {
		l=null;
	}

}
