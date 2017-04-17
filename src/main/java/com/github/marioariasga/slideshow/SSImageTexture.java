package com.github.marioariasga.slideshow;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GLProfile;

import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class SSImageTexture extends SSImage {

	TextureData td;
	int horTiles, verTiles, tileWidth, tileHeight;
	

	public SSImageTexture(File file, BufferedImage image) throws IOException {
		super(file);
		td = AWTTextureIO.newTextureData(GLProfile.getDefault(), image, true);
	}
	
	public SSImageTexture(File file) throws IOException {
		super(file);
		td = TextureIO.newTextureData(GLProfile.getDefault(), file, true, null);
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