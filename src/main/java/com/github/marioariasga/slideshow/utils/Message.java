package com.github.marioariasga.slideshow.utils;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class Message {
	private String message;
	private long messageTime=Long.MAX_VALUE;
	
	public Message(String message) {
		this.message=message;
	}
	
	public Message(String message, long duration) {
		this.message=message;
		this.messageTime = System.currentTimeMillis()+duration;
	}
	
	public void paint(Graphics g) {
		
		int width = g.getClip().getBounds().width;
//		int height = g.getClip().getBounds().width;
		
		// Mensaje
		if(messageTime>System.currentTimeMillis()) {
			FontMetrics m = g.getFontMetrics();
			int msgwidth = m.stringWidth(message);
			int msgheight = m.getHeight();
			int space = 8;

			g.setColor(Color.black);
			g.fillRect(width-msgwidth-(2*space), 0, width, msgheight+space);

			g.setColor(Color.white);
			g.drawString(message, width-msgwidth-space, msgheight);
		}
	}
}
