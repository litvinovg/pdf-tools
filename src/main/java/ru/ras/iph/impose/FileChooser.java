package ru.ras.iph.impose;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
public class FileChooser extends JFrame {
	public static void main(String [] args){
		chooseFile();
	}
	public static File chooseFile(){
		File selected = null;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == fileChooser.APPROVE_OPTION){
			selected = fileChooser.getSelectedFile();
			System.out.println("Selected file " + selected.getAbsolutePath());
		}
		return selected;
		
	}
	
}
