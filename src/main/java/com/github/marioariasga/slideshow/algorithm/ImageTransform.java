package com.github.marioariasga.slideshow.algorithm;

public class ImageTransform {

	/*
		public void rotate(int pos) {
			SSImage imfi = get(currentIndex);
			File f = imfi.getFile();
			BufferedImage img = imfi.getImg();
			 
			StopWatch st = new StopWatch();
			BufferedImage img2 = new BufferedImage(img.getHeight(), img.getWidth(),BufferedImage.TYPE_3BYTE_BGR);
			   // rotate the image

	        AffineTransform rotation = new AffineTransform();

	        if(pos == 0)
	            rotation.rotate( (-1) * Math.PI / 2, img.getWidth() / 2, img.getHeight() / 2);
	        else
	            rotation.rotate( Math.PI / 2, img.getWidth() / 2, img.getHeight() / 2);
	 
	        Graphics2D g2d = img2.createGraphics();
	        g2d.transform(rotation);
	 
	        if(pos == 0)
	            g2d.drawImage( img, null, (img.getWidth() / 2 - img2.getWidth() / 2 ) * -1, (img.getHeight() / 2 - img2.getHeight() / 2));
	        else
	            g2d.drawImage( img, null, (img.getWidth() / 2 - img2.getWidth() / 2 ), (img.getHeight() / 2 - img2.getHeight() / 2) * -1);
	        
	        System.out.println("Rotated in: "+st.stopAndShow());
	        
			try {
				ImageIO.write(img2, "jpeg", f);
				get(currentIndex).setImg(img2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		/*
		public void rotate3(int pos){
			SSImage imfi = get(currentIndex);
			File f = imfi.getFile();
			BufferedImage img = imfi.getImg();
			 
			   // rotate the image
			long a = System.currentTimeMillis();	
	        // Create bitmasks
	        int masks[] = new int[4];
	              masks[0] = 0xff0000; // red
	              masks[1] = 0x00ff00; // green
	              masks[2] = 0x0000ff; // blue
	              masks[3] = 0xff000000; // alpha
	 
	        // Create simple pixel samplemodel
	        SampleModel sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, img.getHeight(), img.getWidth(), masks);
	 
	 
	        // Create int array for raw data -> databuffer & samplemodel -> raster
	        int [] rawData = new int[img.getWidth() * img.getHeight()];
	        DataBufferInt db = new DataBufferInt(rawData, rawData.length);
	        WritableRaster ras = Raster.createWritableRaster(sm, db, null);
	 
	 
	        // Create colorspace -> directcolormodel (int)
	        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	        ColorModel cm = new DirectColorModel(cs,
	                                            32,
	                                            masks[0],
	                                            masks[1],
	                                            masks[2],
	                                            masks[3],
	                                            false,
	                                            DataBuffer.TYPE_INT);
	 
	     
	        // Create the bufferedimage
	        BufferedImage img2 = new BufferedImage(cm, ras, false, null);
			
			int [] iArray1=null;
			int [] iArray2=null;
			
			iArray1 = img.getData().getPixels(0, 0, img.getWidth(), img.getHeight(), iArray1);
			
			for(int i=0;i < img.getWidth(); i++) {
				for(int j=0;j<img.getHeight();j++) {
					rawData[i*img.getHeight()+j] = i+j;
				}
			}
			
			long b = System.currentTimeMillis();
			System.out.println("Tiempo: "+(b-a));
			
			try {
				ImageIO.write(img2, "jpeg", new File(f.getParentFile(),"test.jpg"));
//				get(currentIndex).setImg(img2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	*/
}
