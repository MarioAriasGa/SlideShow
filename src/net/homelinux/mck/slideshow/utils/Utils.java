package net.homelinux.mck.slideshow.utils;


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
	
	public static String getMegapixel(int width, int height) {
		double total = width * height;
		int round = (int)(total / 100000.0);
		return Double.toString(round/10.0);
	}
}
