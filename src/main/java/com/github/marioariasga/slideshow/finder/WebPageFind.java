package com.github.marioariasga.slideshow.finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebPageFind implements FinderInterface {
	List<String> l = null;
	
	public WebPageFind() {
		l = new ArrayList<String>();
	}
	
	public void findAll(String url) {
		l = getLinks(url);
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
    
    public synchronized void add(String path) {
    	l.add(path);
    }
    
    public synchronized String getLast() {
    	return l.get(l.size());
    }
    
    public void print() {
    	for (int i = 0; i < l.size(); i++) {
			System.out.println(l.get(i));
		}
    }
	
	private List<String> getLinks(String url) {
//		List<String> links = new ArrayList<String>();
//		
//		try {	
//			// GET
//			String content = Fetcher.fetchPage(url);
//			if(content.contains("Advertencia de contenido")) {
//				int posini = content.indexOf("?guestAuth=");
//				int posfin = content.indexOf("\"",posini);
//				String auth = content.substring(posini,posfin);		
//				content = Fetcher.fetchPage(url+auth);
//			}
//			
////			System.out.println(content);
//			
//			// MATCH
//			String regex="<a[^>]*href=[\"'](http:\\/\\/[^\"']*)[\"']";
////			String regex="<a[^>]*href=\"(/image[^\"]*)\"";
//			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
//			Matcher matcher = pattern.matcher(content);
//			
//			// Find all ocurrences
//			while(matcher.find()) {
//				String foundUrl = matcher.group(1);
//				links.add(foundUrl);
//			}
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return links;
		return null;
	}


	public void close() {
		l=null;
	}
}
