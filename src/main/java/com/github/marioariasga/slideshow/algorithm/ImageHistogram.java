package com.github.marioariasga.slideshow.algorithm;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import com.github.marioariasga.slideshow.utils.StopWatch;

public class ImageHistogram {
	int data[][];
	int numLayers;
	int numBands;
	int maxvalue;
	int minvalue;
	long loadTime;
	int period;
	int average[];
	int deviation[];
	
	public static double fun(double val, double max) {
		double norm = val*100/max;
//		return Math.log(norm+1);
		return Math.log10(norm+1);
//		return val;
	}
	
	public ImageHistogram(BufferedImage image) {
		StopWatch s1 = new StopWatch();
		numLayers = 3;
		numBands = 256;
		data = new int[numLayers][numBands];
		minvalue=0;
		maxvalue=0;
		int red,green,blue;
			
		// Clear histogram
		/*for(int i=0;i<numlayers; i++) {
			for(int j=0;j<numBands;j++) {
				data[i][j] = 0;
			}
		}*/
		
		WritableRaster r = image.getRaster();
		
		period = image.getWidth() > image.getHeight() ? image.getWidth() : image.getHeight();
		period /= 1000;
		period = period<1 ? 1 : period;
		
		long faverage[] = { 0, 0, 0};
		long fdeviation[] = { 0, 0, 0 };
		long number = 0;
		
		for(int x=0;x<image.getWidth();x+=period) {
			for(int y=0;y<image.getHeight();y+=period) {
				int color = 0;
				if(image.getType()==BufferedImage.TYPE_INT_RGB) {
					int[] arr = (int[]) r.getDataElements(x, y, null);
					color = arr[0];
				} else {
					color = image.getRGB(x, y);
				}
				
				red = (color&0xFF0000)>>16;
				green = (color&0x00FF00)>>8;
			    blue = color&0x0000FF;
				data[0][red]++;
				data[1][green]++;
				data[2][blue]++;
				faverage[0]+=red;
				faverage[1]+=green;
				faverage[2]+=blue;
				fdeviation[0]+=red*red;
				fdeviation[1]+=green*green;
				fdeviation[2]+=blue*blue;
				number++;
			}
		}

		
		// Get max
		for(int i=0;i<numLayers; i++) {
			for(int j=0;j<numBands;j++) {
				maxvalue = Math.max(data[i][j], maxvalue);
			}
		}

		for(int i=0;i<numLayers;i++) {
			faverage[i] = faverage[i]/number;
			fdeviation[i] = fdeviation[i]/number - faverage[i] * faverage[i];
//			System.out.println("Average: "+faverage[i]);
//			System.out.println("Deviation: "+Math.sqrt(fdeviation[i]));
		}
		// CHECK
		/*for(int j=0;j<numBands;j++) {
			System.out.println(j+": "+ data[0][j]+" "+data[1][j]+" "+data[2][j]);
		}
	
		System.out.println("Histogram time: "+s1+ " ");
		System.out.println("Max value: "+maxvalue);
		System.out.println("Period: "+period);
		*/
		
		this.loadTime = s1.stopAndGet();
	}

	public int getNumLayers() {
		return numLayers;
	}

	public int getNumBands() {
		return numBands;
	}

	public int getMaxvalue() {
		return maxvalue;
	}

	public int getMinvalue() {
		return minvalue;
	}
	public int getPixel(int x, int y) {
		return data[x][y];
	}

	public String getLoadTime() {
		return StopWatch.toHuman(this.loadTime);
	}
	
	public int getPeriod() {
		return period;
	}
}
