package net.homelinux.mck.slideshow;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

public class TextureGroup {
	Texture[] texs = new Texture[4];
	
	TextureGroup(GL gl, BufferedImage orig) {
		int width = orig.getWidth();
		int height = orig.getHeight();
		
		BufferedImage buf = new BufferedImage(width/2, height/2, orig.getType());
		Graphics g = buf.getGraphics();
		
/*		TextureData data = TextureIO.newTextureData(buf, false);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, data.getWidth(), 
                data.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data.getBuffer());
*/
		g.drawImage(orig, 0, 0, width/2, height/2, 0, height/2, width/2, height, null);
		texs[0] = TextureIO.newTexture(buf, false);
		
		g.drawImage(orig, 0, 0, width/2, height/2, width/2, height/2, width, height, null);
		texs[1] = TextureIO.newTexture(buf, false);
		
		g.drawImage(orig, 0, 0, width/2, height/2, 0, 0, width/2, height/2, null);
		texs[2] = TextureIO.newTexture(buf, false);
		
		g.drawImage(orig, 0, 0, width/2, height/2, width/2, 0, width, height/2, null);
		texs[3] = TextureIO.newTexture(buf, false);
		
		for(int i=0;i<4;i++) {
			texs[i].setTexParameteri(GL.GL_TEXTURE_BORDER, 0);
			texs[i].setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
			texs[i].setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
			texs[i].setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			texs[i].setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		}
	}

	public Texture get(int i) {
		return texs[i];
	}
}
