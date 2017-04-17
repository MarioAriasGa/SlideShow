package com.github.marioariasga.slideshow.view;

import com.github.marioariasga.slideshow.SSImage;

public interface ViewInterface {

	void toggleShowFileName();
	void toogleHistogram();
	void toogleCache();
	void refresh();

	void setImage(SSImage img);

	SSImage getImage();

	void close();

	void setMessage(String string, long i);
	void setMessage(String string);

	void toogleMemory();

	void toogleVerbose();

	void toogleFullScreen();
	void setFullScreen(boolean value);
	boolean isFullScreen();
	boolean getShowHistogram();

}
