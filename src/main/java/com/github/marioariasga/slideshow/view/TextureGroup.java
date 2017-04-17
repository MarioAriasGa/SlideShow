package com.github.marioariasga.slideshow.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.github.marioariasga.slideshow.SSImage;
import com.github.marioariasga.slideshow.SSImageTexture;
import com.github.marioariasga.slideshow.utils.StopWatch;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;


public class TextureGroup {
	SSImage img;
	
	Texture[] texs = new Texture[4];
	
	TextureGroup(GL gl, SSImageTexture img) {
		this.img = img;
		texs[0] = TextureIO.newTexture(img.getTextureData());
	}
	
	SSImage getImage() {
		return img;
	}
	
	TextureGroup(GL2 gl, BufferedImage orig) {
		StopWatch st = new StopWatch();
		int width = orig.getWidth();
		int height = orig.getHeight();
		
		//texs[0] = TextureIO.newTexture(orig,false);

		/*
		BufferedImage img1 = orig.getSubimage(0, 0, width/2, height/2);
		BufferedImage img2 = orig.getSubimage(0, height/2, width/2, height/2);
		BufferedImage img3 = orig.getSubimage(width/2, 0, width/2, height/2);
		BufferedImage img4 = orig.getSubimage(width/2, height/2, width/2, height/2);
		
		texs[0] = TextureIO.newTexture(img1, false);
		texs[1] = TextureIO.newTexture(img2, false);
		texs[2] = TextureIO.newTexture(img3, false);
		texs[3] = TextureIO.newTexture(img4, false);
		*/
		
		
		BufferedImage buf = new BufferedImage(width/2, height/2, BufferedImage.TYPE_INT_RGB);
		Graphics g = buf.getGraphics();
				
		g.drawImage(orig, 0, 0, width/2, height/2, 0, height/2, width/2, height, null);
		texs[0] = AWTTextureIO.newTexture(GLProfile.getDefault(), buf, false);
		
		g.drawImage(orig, 0, 0, width/2, height/2, width/2, height/2, width, height, null);
		texs[1] = AWTTextureIO.newTexture(GLProfile.getDefault(), buf, false);
		
		g.drawImage(orig, 0, 0, width/2, height/2, 0, 0, width/2, height/2, null);
		texs[2] = AWTTextureIO.newTexture(GLProfile.getDefault(), buf, false);
		
		g.drawImage(orig, 0, 0, width/2, height/2, width/2, 0, width, height/2, null);
		texs[3] = AWTTextureIO.newTexture(GLProfile.getDefault(), buf, false);
		
		
		/*
		System.out.println("HERE");
		texs[0] = TextureIO.newTexture(orig.getSubimage(0, 0, width/2, height/2),false);
		texs[1] = TextureIO.newTexture(orig.getSubimage(width/2, 0, width/2,height/2),false);
		texs[2] = TextureIO.newTexture(orig.getSubimage(0, height/2, width/2, height/2),false);
		texs[3] = TextureIO.newTexture(orig.getSubimage(width/2, height/2, width/2,height/2),false);
		System.out.println("DOS");
		*/
		
		for(int i=0;i<size();i++) {
			//g.drawImage(orig.getSubimage(width/2, 0, width/2, height/2), 0, 0, width/2, height/2, 0, 0, width/2, height/2, null);
			//texs[i] = TextureIO.newTexture(buf, false);
			//texs[i] = TextureIO.newTexture(orig.getSubimage(width/2, 0, width/2, height/2),false);
			
			//TextureData td = TextureIO.newTextureData(orig.getSubimage(width/2, 0, width/2, height/2), false);
			//texs[i]=TextureIO.newTexture(td);
			
			
			texs[i].setTexParameteri(gl, GL2.GL_TEXTURE_BORDER, 0);
			texs[i].setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			texs[i].setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
			texs[i].setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
			texs[i].setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		}
	}

	public Texture get(int i) {
		return texs[i];
	}
	
	public int numTile() {
		return 1;
	}
	
	public int size() {
		return numTile()*numTile();
	}
	
	public void dispose(GL2 gl) {
		for(int i=0;i<size();i++) {
			texs[i].destroy(gl);
		}
	}
}
