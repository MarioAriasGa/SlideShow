package com.github.marioariasga.slideshow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import com.github.marioariasga.slideshow.algorithm.ImageHistogram;
import com.github.marioariasga.slideshow.finder.ImportantExif;
import com.github.marioariasga.slideshow.utils.StopWatch;

public class SSImageBuffered extends SSImage {
	private BufferedImage img;
	
	public SSImageBuffered(File file, BufferedImage img) {
		super(file);
		this.img = img;
	}
	
	public void paint(Component parent, Graphics g) {
	 	if(img==null) return;
	 	
    	int imageWidth = img.getWidth();
    	int imageHeight = img.getHeight();
    	int screenWidth = g.getClip().getBounds().width;
    	int screenHeight = g.getClip().getBounds().height;

    	int clipWidth;
    	int clipHeight;
    	int clipX;
    	int clipY;

    	int imageX=0;
    	int imageY=0;
    	
    	// Calcular recorte
    	if(mode!=MODE_FIT) {
    		// Coordenadas de la pantalla
    		clipX=0; clipY=0; clipWidth=screenWidth; clipHeight=screenHeight;

    		imageWidth=(int) (screenWidth/zoom);
    		imageHeight=(int) (screenHeight/zoom);

    		if(imageWidth>img.getWidth()) {
    			imageWidth=img.getWidth();
    			clipWidth=(int) (imageWidth*zoom);
    		}

    		if(imageHeight>img.getHeight()) {
    			imageHeight=img.getHeight();
    			clipHeight=(int) (imageHeight*zoom);
    		}

    		// Set initial offset
    		imageX=(int) (offsetx-(imageWidth/2));
    		imageY=(int) (offsety-(imageHeight/2));


    		// Check offset bounds
    		if(imageX<0) {
    			imageX=0;
    		}
    		if(imageY<0) {
    			imageY=0;
    		}
    		
    		if(imageX+imageWidth>img.getWidth()) {
    			imageX = img.getWidth()-imageWidth;			
    		}

    		if(imageY+imageHeight>img.getHeight()) {
    			imageY = img.getHeight()-imageHeight;
    		}
    		
    	} else {
    		double imgAspect = (double) imageWidth / (double)imageHeight;
    		double scrAspect = (double) screenWidth/ (double) screenHeight;

    		if(imgAspect>scrAspect) {
    			clipWidth =screenWidth;
    			clipHeight = (int) (((double)screenWidth)/imgAspect);
    			zoom = (double)screenWidth/(double)imageWidth;
    		} else {
    			clipHeight = screenHeight;
    			clipWidth = (int) (((double)screenHeight)*imgAspect);
    			zoom = (double)screenHeight/(double)imageHeight;
    		}
    	}
    	
		offsetx=imageX+imageWidth/2;
		offsety=imageY+imageHeight/2;
    	clipX = (screenWidth-clipWidth)/2;
    	clipY = (screenHeight-clipHeight)/2;
//  	System.out.println("("+cx+","+cy+") ("+cw+","+ch+")   -    ("+ix+","+iy+") ("+iw+","+ih+")");
    	
    	
//    	((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
//    	((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//    	
    	g.drawImage(img, 
    			clipX, clipY, clipX+clipWidth, clipY+clipHeight,	
    			imageX, imageY, imageX+imageWidth, imageY+imageHeight, parent
    	);
//    	
//    	((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	public void showMetadata(Graphics g) {
		List<String> metaStrings = ImportantExif.getInstance().getImportantMeta(this);

		FontMetrics m = g.getFontMetrics();
		int fontHeight = m.getHeight();
		int lineWidth=0;

		// Find background size
		for (int i = 0; i < metaStrings.size(); i++) {
			String current = metaStrings.get(i);
			int thisWidth = m.stringWidth(current);
			if(thisWidth>lineWidth)
				lineWidth = thisWidth;
		}

		// draw background
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

		g2d.setColor(Color.black);
		g2d.fillRoundRect(fontHeight, fontHeight*2, lineWidth+2*fontHeight, (metaStrings.size()+2)*fontHeight, 20, 20);

		g2d.setComposite(AlphaComposite.SrcOver);
		g2d.setColor(Color.white);
		g2d.drawRoundRect(fontHeight-1, fontHeight*2-1, lineWidth+2*fontHeight+1, (metaStrings.size()+2)*fontHeight+1, 20, 20);

		// draw strings

		int posx = 2*fontHeight;
		int posy = 3*fontHeight;

		g2d.drawString("Info:", posx, posy);
		g2d.drawLine(2*fontHeight, posy+3, 2*fontHeight+lineWidth, posy+3);

		posy+=fontHeight;

		for (int i = 0; i < metaStrings.size(); i++) {
			g2d.drawString(metaStrings.get(i), posx, posy);
			posy+=fontHeight;
		}

	}
	
	public void showHistogram(Graphics2D g) {
		StopWatch s = new StopWatch();
		ImageHistogram h = getHistogram();
		
	    int histWidth = 256*2;
        int histHeight = 200;
		
        int margin = 10;
		int offx = margin;
		int offy = (int)g.getClipBounds().getHeight()-histHeight-30;
		int max = h.getMaxvalue();
		     
		g.setColor(Color.RED);
		g.drawString("Avg", offx, histHeight);
        
        g.setColor(Color.WHITE);
        g.drawRect(offx-1, offy-1,histWidth+1,histHeight+1);
        
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		
        g.setColor(Color.BLACK);
        g.fillRect(offx, offy,histWidth,histHeight);
        
        Color colors[] = { Color.red, Color.green, Color.blue };

	    Polygon[] poly = new Polygon[h.getNumBands()];
        
        for(int j=0 ; j < h.getNumLayers() ; j++)
        {
        	double stepX = (double) histWidth / (double) h.getNumBands();
        	double stepY = (double) histHeight / ImageHistogram.fun(max,max);
        	 
            poly[j] = new Polygon();
            poly[j].addPoint(offx,offy+histHeight);
            
            for ( int i = 0; i < h.getNumBands(); i++ )
            {
                double x = ((double)i*stepX);
                double y = ((double)ImageHistogram.fun(h.getPixel(j,i),max)*stepY);
               
                poly[j].addPoint((int)(offx+x),(int)(offy+histHeight-y));
            }
            
            poly[j].addPoint(offx+histWidth,offy+histHeight);
            
            g.setColor(colors[j]);
            g.fill(poly[j]);
        }

		g.setComposite(AlphaComposite.SrcOver);
//		System.out.println("Render time: "+s.stopAndShow());
	}

	public int getHeight() {
		return img.getHeight();
	}

	public int getWidth() {
		return img.getWidth();
	}
}
