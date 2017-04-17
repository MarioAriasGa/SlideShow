package com.github.marioariasga.slideshow.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


public class CacheThreads implements Runnable {
	private static List<Thread> threads= null;	
	public static ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10); 
	
	public void run() {
		while(true) {
			try {
				Integer value = queue.poll(1000, TimeUnit.SECONDS);
				ImageCache cache = ImageCache.getActive();
				if(value!=null && cache!=null) {
					cache.update(value.intValue());
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void ensureStarted(int numThreads) {
		if(threads==null) {
			threads = new ArrayList<Thread>();
			for(int i=0; i<numThreads;i++) {
				System.out.println("STARTING THREAD: "+i);
				Thread th = new Thread(new CacheThreads());
				th.setName("Cache thread "+i);
				th.setPriority(Thread.MIN_PRIORITY);
				threads.add(th);
				th.start();
			}
		}
	}
}

