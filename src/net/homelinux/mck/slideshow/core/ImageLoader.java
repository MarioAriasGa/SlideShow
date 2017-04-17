package net.homelinux.mck.slideshow.core;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import net.homelinux.mck.slideshow.SSImage;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class ImageLoader {
	
	public static SSImage openImage(String path) throws Exception {
//		System.out.println("Reading "+path);
		StopWatch st = new StopWatch();
		File file = new File(path);
		SSImage newSSImage=null;
		if(fileIsJpeg(path)) {
			newSSImage = openJpeg(file);
		} else {
			newSSImage = openGeneric(file);
		}
		newSSImage.setImg(resize(newSSImage.getImg(), 4096));
		System.out.println(" -"+Thread.currentThread().getName()+" Read ok "+path+" in "+st.stopAndShow());
		newSSImage.setLoadTime(st.getMeasure());
		return newSSImage;
	}
	
	private static SSImage openGeneric(File file) throws Exception {
		BufferedImage newImg = ImageIO.read(file);			
		SSImage SSImage = new SSImage(file, newImg);
		return SSImage;
	}
	
	private static SSImage openJpeg(File file) throws Exception {
		JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(file));
		BufferedImage image = jpegDecoder.decodeAsBufferedImage();
	
//		 now you can use the image
		JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
		Metadata metadata = JpegMetadataReader.readMetadata(decodeParam);
//		showExif(metadata);
		
		SSImage SSImage = new SSImage(file,image);
		SSImage.setMeta(metadata);
		return SSImage;
	}
	
	private static boolean fileIsJpeg(String path) {
		String lcPath = path.toLowerCase();
		return ( lcPath.endsWith(".jpg") || lcPath.endsWith(".jpeg"));
	}
	
	private static BufferedImage resize(BufferedImage orig, int maxComponent) {
		double width = orig.getWidth();
		double height = orig.getHeight();
		double destWidth=orig.getWidth();
		double destHeight=orig.getHeight();
		
		if(width<maxComponent && height<maxComponent)
			return orig;
		
		if(width>height) {
			destWidth *= maxComponent / width;
			destHeight *= maxComponent / width; 
		} else {
			destWidth *= maxComponent / height;
			destHeight *= maxComponent / height;
		}
		System.out.println("Resize to: "+destWidth+"x"+destHeight);
		BufferedImage dest = new BufferedImage((int)destWidth,(int)destHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = dest.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance( destWidth/orig.getWidth(), destHeight/orig.getHeight());
		g.drawRenderedImage(orig,at);
		
		return dest;
	}
}
