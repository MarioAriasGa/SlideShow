package com.github.marioariasga.slideshow;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import com.jogamp.opengl.GLProfile;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class SSImageTexture extends SSImage {

//	TextureData td[][];
	TextureData td;
	int horTiles, verTiles, tileWidth, tileHeight;
	

	public SSImageTexture(File file, BufferedImage image) throws IOException {
		super(file);
		td = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, true);
	}
	
	public SSImageTexture(File file) throws IOException {
		super(file);
		td = TextureIO.newTextureData(GLProfile.getDefault(), file, true, null);
		
		Buffer b = td.getBuffer();
		System.out.println(b.getClass().getName());
		/*TextureData orig = TextureIO.newTextureData(file, false, null);
		
		horTiles = (orig.getWidth()/SSViewJOGL.maxTextureSize)+1;
		verTiles = (orig.getHeight()/SSViewJOGL.maxTextureSize)+1;
	
		tileWidth = orig.getWidth()/horTiles;
		tileHeight = orig.getHeight()/verTiles;
		
		td = new TextureData[horTiles][verTiles];
		
		for(int h=0;h<verTiles;h++) {
			for(int v=0;v<horTiles;v++) {
				//IntBuffer buf= BufferUtil.newIntBuffer(tileWidth*tileHeight*3);
				//td[v][h] = new TextureData(orig.getInternalFormat(), tileWidth,tileHeight,0, orig.getPixelFormat(), orig.getPixelType(), orig.getMipmap(), orig.isDataCompressed(), orig.getMustFlipVertically(), buf, null );
			}
		}
		*/
	}
	
	public TextureData getTextureData() {
		return td;
	}

	public int getHeight() {
		return td.getHeight();
	}

	public int getWidth() {
		return td.getWidth();
	}
}