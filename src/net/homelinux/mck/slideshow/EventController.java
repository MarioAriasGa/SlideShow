package net.homelinux.mck.slideshow;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.apache.log4j.Logger;

public class EventController implements MouseListener, KeyListener, ActionListener,MouseMotionListener,MouseWheelListener {

	private Logger log = Logger.getLogger(getClass().getName());
	
	private SSController controller;
	private ViewInterface view;
	private ImageFinder finder;
	private SlideshowTimer timer;
	
	
	public EventController(SSController controller) {
		this.controller = controller;
		loadInstances();
	}
	
	public void loadInstances() {
		this.view = controller.getView();
		this.finder = controller.getFinder();
		this.timer = controller.getTimer();
	}

	public void mouseClicked(MouseEvent event) {
		log.debug("*MOUSECLICK: BTN("+event.getButton()+") LOCATION("+event.getX()+","+event.getY()+")");
		
		int boton = event.getButton();
		if(boton==1) {
			// Izquierdo
			finder.goNext(1);
		} else if(boton==3) {
			// Derecho
			finder.goPrevious(1);
		}
		controller.refresh();
	}

	public void mouseEntered(MouseEvent event) {
		
	}

	public void mouseExited(MouseEvent event) {
		
	}

	public void mousePressed(MouseEvent event) {
		log.debug("+MOUSEPRESSED: BTN("+event.getButton()+") LOCATION("+event.getX()+","+event.getY()+")");
		mousePoint = event.getPoint();
	}

	public void mouseReleased(MouseEvent event) {
		log.debug("-MOUSERELEASED: BTN("+event.getButton()+") LOCATION("+event.getX()+","+event.getY()+")");
	}

	public void keyPressed(KeyEvent event) {
		timer.resetTimer();
		char character = Character.toLowerCase(event.getKeyChar());
		int code = event.getKeyCode();
		
		System.out.println("KEYPRESS: "+code+" = "+character);
		
		
		if(code==27) {
			// Esc
			controller.quit();
		} else if(code==37) {
			// IZDA
			finder.getCurrent().goLeft();
		} else if(code==38) {
			// ARRIBA
			finder.getCurrent().goUp();
		} else if(code==39) {
			// DERECHA
			finder.getCurrent().goRight();
		} else if(code==40) {
			// ABAJO
			finder.getCurrent().goDown();

		} else if(character==' ') {
			timer.tooglePause();
		} else if(character=='-') {
			finder.goPrevious(1);
		} else if(character=='+') {
			finder.goNext(1);
		} else if(code==33) {
			finder.previousGallery();
		} else if(code==34) {
			finder.nextGallery();
		} else if(code==65) {
			finder.getCurrent().rotate();
		} else if(character=='f') {
			// f
			view.toogleFullScreen();
		} else if(character=='i'){
			view.toggleShowFileName();
		} else if(character=='r') {
			// r
			finder.randomize();
			view.setMessage("Randomize",4000);
		} else if(character=='t') {
			finder.gotoRandom();
			return;
		} else if(character=='m') {
			view.toogleMemory();
		} else if(character=='v') {
			// v
			view.toogleVerbose();
		} else if( character=='0') {
			finder.getCurrent().setAdjust();
		} else if( (code>=97) &&(code<=100)) {
			// 1 4
			finder.getCurrent().setZoom(code-96);
		} else if( code==101 ){
			finder.getCurrent().setZoom(0.5f);
		} else if( code==102 ){
			finder.getCurrent().setZoom(0.75f);
		} else if(code==103) {
			finder.getCurrent().setZoom(1.5f);
		} else if( (code>=49) &&(code<=52)) {
			// 1 4
			finder.getCurrent().setZoom(code-48);
		} else if(code==127) {
			// supr
			timer.stop();
			view.setFullScreen(false);
			finder.deleteImage();
			view.refresh();
		} else if(code==8) {
			// backspace
			timer.stop();
			view.setFullScreen(false);
			finder.deleteGallery();
			view.refresh();
		} else if(code==10) {
			// Enter
			view.setMessage("Enter", 5000);
			finder.enter();
			return;
		} else if(code==110) {
			view.setMessage("Exit", 5000);
			finder.exit();
			return;
		}
		controller.refresh();
	}

	public void keyReleased(KeyEvent event) {
//		log.debug("KEYRELEASE: "+event.getKeyCode());
	}

	public void keyTyped(KeyEvent event) {
		
	}

	public void actionPerformed(ActionEvent event) {
		finder.goNext(1);
		controller.refresh();
	}
	
	private Point mousePoint = null;

	public void mouseDragged(MouseEvent event) {
		timer.resetTimer();
		Point p = event.getPoint();
		if(mousePoint!=null) finder.getCurrent().setOffset(mousePoint, p);
		mousePoint = p;
		controller.refresh();
	}

	public void mouseMoved(MouseEvent event) {
//		log.debug("MOUSEMOVE: BTN("+event.getButton()+") LOCATION("+event.getX()+","+event.getY()+")");
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		timer.resetTimer();
		
		if(e.getWheelRotation()>0) {
			finder.getCurrent().decreaseZoom();
		} else {
			finder.getCurrent().increaseZoom();
		}
		controller.refresh();
	}

}
