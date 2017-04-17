package com.github.marioariasga.slideshow.utils;

public class Config {
	private static Config instance=null;
	
	public static Config getInstance() {
		if(instance==null) instance = new Config();
		return instance;
	}
	
	private static int TYPE_AWT = 1;
	private static int TYPE_JOGL = 2;
	
	private static int type = TYPE_AWT;
	
	public boolean typeIsAWT() {
		return type == TYPE_AWT;
	}	
	
	public boolean typeIsJogl() {
		return type == TYPE_JOGL;
	}
}
