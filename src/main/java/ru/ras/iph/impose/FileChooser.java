package ru.ras.iph.impose;

import java.awt.Dimension;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
public class FileChooser extends JFrame {
	private static String FILENAME_EXT = "pdf";
	public static void main(String [] args){
		chooseFile("");
	}
	
	public static File chooseFile(String lastPath){
		File selected = null;
		
		File currentDirectory = null;
		if (lastPath != null && !lastPath.isEmpty()) {
			File preselected = new File(lastPath);
			if (preselected.exists()) {
				if (preselected.isFile()) {
					currentDirectory = preselected.getParentFile();
				} else if (preselected.isDirectory()) {
					currentDirectory = preselected;
				}
			}
		}
		if (currentDirectory == null) {
			currentDirectory = new File(System.getProperty("user.home"));
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setPreferredSize(new Dimension(800,600));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(FILENAME_EXT.toUpperCase(), FILENAME_EXT.toLowerCase());
		fileChooser.setFileFilter(filter);
		fileChooser.setCurrentDirectory(currentDirectory);
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION){
			selected = fileChooser.getSelectedFile();
		}
		return selected;

	}
}
