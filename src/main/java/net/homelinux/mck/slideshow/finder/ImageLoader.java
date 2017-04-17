package net.homelinux.mck.slideshow.finder;

import it.tidalwave.imageio.raw.RAWImageReadParam;
import it.tidalwave.imageio.raw.Source;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import net.homelinux.mck.slideshow.SSImage;
import net.homelinux.mck.slideshow.SSImageBuffered;
import net.homelinux.mck.slideshow.SSImageTexture;
import net.homelinux.mck.slideshow.algorithm.ImageHistogram;
import net.homelinux.mck.slideshow.utils.Config;
import net.homelinux.mck.slideshow.utils.StopWatch;

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
		if(Config.getInstance().typeIsAWT()) {
			if(fileIsJpeg(path)) {
				newSSImage = openJpeg(file);
			} else if(fileIsRAW(path)) {
				newSSImage = openRAW(file);
			} else {
				newSSImage = openGeneric(file);
			}
//			newSSImage.setImg(resize(newSSImage.getImg(), 4096));
		} else if(Config.getInstance().typeIsJogl()) {
			newSSImage = openJogl(file);
		}
		System.out.println(" -"+Thread.currentThread().getName()+" Read ok "+path+" in "+st.stopAndShow());
		newSSImage.setLoadTime(st.getMeasure());
		return newSSImage;
	}
	
	private static SSImage openGeneric(File file) throws Exception {
		BufferedImage newImg = ImageIO.read(file);			
		SSImage SSImage = new SSImageBuffered(file, newImg);
		return SSImage;
	}
	
	private static SSImage openJogl(File file) throws Exception {
		JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(file));
		BufferedImage image = jpegDecoder.decodeAsBufferedImage();
	
//		 now you can use the image
		JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
		Metadata metadata = JpegMetadataReader.readMetadata(decodeParam);
//		showExif(metadata);
		
		SSImage ssImage = new SSImageTexture(file,image);
		ssImage.setMeta(metadata);
//		if(SSController.getInstance().getView().getShowHistogram()) {
			ssImage.setHistogram(new ImageHistogram(image));
//		}
		
		return ssImage;
	}
	
	private static SSImage openJpeg(File file) throws Exception {
		
		JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(file));
		BufferedImage image = jpegDecoder.decodeAsBufferedImage();
	
//		 now you can use the image
		JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
		Metadata metadata = JpegMetadataReader.readMetadata(decodeParam);
//		showExif(metadata);
		
		SSImage ssImage = new SSImageBuffered(file,image);
		ssImage.setMeta(metadata);
//		if(SSController.getInstance().getView().getShowHistogram()) {
			ssImage.setHistogram(new ImageHistogram(image));
//		}
		return ssImage;
	}
	
	private static SSImage openRAW(File file) throws IOException {
		
		try {
			//		File file = new File("/Users/mck/Pictures/test/IMG_8859.CR2");
			System.out.println("Reading RAW: "+file.getCanonicalPath());
			ImageReader reader = (ImageReader) ImageIO.getImageReaders(file).next();
			reader.setInput(ImageIO.createImageInputStream(file));
			BufferedImage image = reader.read(0, new RAWImageReadParam(Source.FULL_SIZE_PREVIEW));
			SSImage ssImage = new SSImageBuffered(file,image);
			return ssImage;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	
	private static boolean fileIsJpeg(String path) {
		String lcPath = path.toLowerCase();
		return ( lcPath.endsWith(".jpg") || lcPath.endsWith(".jpeg"));
	}
	
	private static boolean fileIsRAW(String path) {
		String lcPath = path.toLowerCase();
		return ( lcPath.endsWith(".cr2"));
	}
	
	/*public static Histogram getHistogram(BufferedImage image) {
		StopWatch st = new StopWatch();
		ParameterBlock pb = new ParameterBlock();
        
        int[] bins = { 256 };
        double[] low = { 0.0D };
        double[] high = { 256.0D };
        
        pb.addSource(image);
        pb.add(null);
        pb.add(1);  // Muestras por periodo
        pb.add(100);	// Periodo en pixeles
        pb.add(bins);
        pb.add(low);
        pb.add(high);
        
        RenderedOp op = JAI.create("histogram", pb, null);
        Histogram histogram = (Histogram) op.getProperty("histogram");

        System.out.println("Histogram took: "+st.stopAndShow());
        return histogram;
	}*/
	
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
