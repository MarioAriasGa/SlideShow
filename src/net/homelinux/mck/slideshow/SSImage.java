package net.homelinux.mck.slideshow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.List;

import net.homelinux.mck.slideshow.core.ImportantExif;
import net.homelinux.mck.slideshow.core.StopWatch;

import com.drew.metadata.Metadata;

public class SSImage {
	private BufferedImage img;
	private File file;
	private Metadata meta=null;
	
	public static final int MODE_FIT = 1;
	public static final int MODE_ZOOM = 2;
	private int mode = MODE_FIT;

	private int offsetx=0;
	private int offsety=0;
	
	private double zoom=1.0f;
	private float angle=0.0f;
	
	private int index;
	private long loadtime=0;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int total) {
		this.index = total;
	}

	public Metadata getMeta() {
		return meta;
	}

	public void setMeta(Metadata meta) {
		this.meta = meta;
	}

	public SSImage(File file, BufferedImage img) {
		this.file = file;
		this.img = img;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public BufferedImage getImg() {
		return img;
	}
	public void setImg(BufferedImage img) {
		this.img = img;
	}
	
	public void paint(Component parent, Graphics g) {
	 	if(img==null) return;
	 	
    	int iw = img.getWidth();
    	int ih = img.getHeight();
    	int sw = g.getClip().getBounds().width;
    	int sh = g.getClip().getBounds().height;

    	int cw;
    	int ch;
    	int cx;
    	int cy;

    	int ix=0;
    	int iy=0;
    	
    	// Calcular recorte
    	if(mode!=MODE_FIT) {

    		// Coordenadas de la pantalla
    		cx =0; cy=0; cw=sw; ch=sh;

    		iw=(int) (sw/zoom);
    		ih=(int) (sh/zoom);

    		if(iw>img.getWidth()) {
    			iw=img.getWidth();
    			cw=(int) (iw*zoom);
    		}

    		if(ih>img.getHeight()) {
    			ih=img.getHeight();
    			ch=(int) (ih*zoom);
    		}

    		// Set initial offset
    		ix=(int) (offsetx-(iw/2));
    		iy=(int) (offsety-(ih/2));


    		// Check offset bounds
    		if(ix<0) {
    			ix=0;
    		}
    		if(iy<0) {
    			iy=0;
    		}
    		
    		if(ix+iw>img.getWidth()) {
    			ix = img.getWidth()-iw;			
    		}

    		if(iy+ih>img.getHeight()) {
    			iy = img.getHeight()-ih;
    		}
    		
    	} else {
    		double imgAspect = (double) iw / (double)ih;
    		double scrAspect = (double) sw/ (double) sh;

    		if(imgAspect>scrAspect) {
    			cw =sw;
    			ch = (int) (((double)sw)/imgAspect);
    			zoom = (float)sw/(float)iw;
    		} else {
    			ch = sh;
    			cw = (int) (((double)sh)*imgAspect);
    			zoom = (float)sh/(float)ih;
    		}
    	}
    	
		offsetx=ix+iw/2;
		offsety=iy+ih/2;
    	cx = (sw-cw)/2;
    	cy = (sh-ch)/2;
//  	System.out.println("("+cx+","+cy+") ("+cw+","+ch+")   -    ("+ix+","+iy+") ("+iw+","+ih+")");
    	
    	
//    	((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
//    	((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//    	
    	g.drawImage(img, 
    			cx, cy, cx+cw, cy+ch,	
    			ix, iy, ix+iw, iy+ih, parent
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

	public void setZoom(float i) {
		zoom = i;

		if(zoom<0.1)
			zoom=0.1f;
		
		mode = MODE_ZOOM;
	}
	
	public void increaseZoom() {
		setZoom(getZoom() * 1.2f);
	}
	
	public void decreaseZoom() {
		setZoom(getZoom() / 1.2f);
	}
	
	public float getZoom() {
		return zoom;
	}
	
	public void setAdjust() {
		mode = MODE_FIT;
	}
	
	private void tidiOffset() {
		/*
		if(offsetx<0) offsetx = 0;
		if(offsety<0) offsety = 0;
		if(offsetx>img.getWidth()) 
			offsetx = img.getWidth();
		if(offsety>img.getHeight()) 
			offsety = img.getHeight();
		*/
	}

	public void goUp() {
		int height = img.getWidth();
		offsety-= height/10/getZoom();
		tidiOffset();
		System.out.println("UP");
	}
	
	public void goDown() {
		int height = img.getWidth();
		offsety+= height/10/getZoom();
		tidiOffset();
		System.out.println("DOWN");
	}
	
	public void goLeft() {
		int width = img.getWidth();
		offsetx-=width/10/getZoom();
		tidiOffset();
		System.out.println("LEFT");
	}
	
	public void goRight() {
		int width = img.getWidth();
		offsetx+=width/10/getZoom();
		tidiOffset();
		System.out.println("RIGHT");;
	}
	
	public void setOffset(Point p1, Point p2) {
		offsetx -= p2.x - p1.x;
		offsety -= p2.y - p1.y;
		tidiOffset();
	}

	public long estimateSize() {
		if(img!=null) {
			return img.getHeight()*img.getWidth()*4;
		}
		return 0;
	}

	public void setLoadTime(long measure) {
		this.loadtime = measure;
	}

	public String getLoadTime() {
		return StopWatch.msToString(this.loadtime);
	}

	public void rotate() {
		angle += 90.0f;
		if(angle>=360) angle = angle % 360;
		System.out.println("Angle: "+angle);
	}
	
	public float getAngle() {
		return angle;
	}

	public float getOffsetX() {
		return offsetx;
	}
	
	public float getOffsetY() {
		return offsety;
	}
}
