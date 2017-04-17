package com.github.marioariasga.slideshow.utils;

import java.awt.Frame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class GUIUtils {
	public static boolean confirm(String message) {
		
//		Custom button text
		Object[] options = {"Si",
		                    "No"};
		
		int n = JOptionPane.showOptionDialog(new Frame(),
		    message,
		    "Confirmar",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[0]);
		return n==0;
	}
	
	public static String showDirSelect() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       String path = chooser.getSelectedFile().getAbsolutePath();
	       return path;
	    }
	    return null;
	}
}
