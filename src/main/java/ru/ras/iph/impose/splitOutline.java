package ru.ras.iph.impose;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class splitOutline {
	
  
    /**
     * Main method.
     * @param    args    no arguments needed
     * @throws DocumentException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, DocumentException {
    	if (args.length != 1) {
			System.err.println("Usage: java -jar double.jar input_file1.pdf");
			}
    	File inputFile1 = null;

    	if (args.length == 1){
    		String inputFileName1 = args[0];
    		

      	inputFile1 = new File(inputFileName1);	
   
    	} else {
    		inputFile1 = FileChooser.chooseFile("");

    	}
    	
    	if (inputFile1 == null || !inputFile1.exists()){
    		System.err.println("Input file1 not found!");
			System.exit(1);
    	}

    	FileInputStream inputStream1 = new FileInputStream(inputFile1);

    	String parentFolder = "";
    	if (inputFile1.getParent() != null){
    		parentFolder = inputFile1.getParent() + File.separator;
    	}
    	File outputFile = new File( parentFolder  + "double_" + inputFile1.getName());
    	FileOutputStream outputStream = new FileOutputStream(outputFile);
    	
        PdfReader reader1 = new PdfReader(inputStream1);
        Rectangle pageDoc1 = reader1.getPageSize(1);
        float newWidth = pageDoc1.getWidth();
        float newHeight;
        
       	newHeight = pageDoc1.getHeight();
        
        Rectangle newPageSize = new Rectangle(newWidth, newHeight );
        Document document = new Document(newPageSize);

        Rectangle pagesize = document.getPageSize();
        
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte canvas = writer.getDirectContent();
        PdfImportedPage page1doc = writer.getImportedPage(reader1, 1);
        
  
			  // step 5
        document.close();
        reader1.close();
    }
}
 