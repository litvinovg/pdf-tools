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

public class doubleView {
	
  
    /**
     * Main method.
     * @param    args    no arguments needed
     * @throws DocumentException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, DocumentException {
    	if (args.length != 2) {
			System.err.println("Usage: java -jar double.jar input_file1.pdf input_file2.pdf");
			}
    	File inputFile1 = null;
    	File inputFile2 = null;

    	if (args.length == 2){
    		String inputFileName1 = args[0];
    		String inputFileName2 = args[1];

      	inputFile1 = new File(inputFileName1);	
      	inputFile2 = new File(inputFileName2);
    	} else {
    		inputFile1 = FileChooser.chooseFile("");
    		inputFile2 = FileChooser.chooseFile(inputFile2.getAbsolutePath());

    	}
    	
    	if (inputFile1 == null || !inputFile1.exists()){
    		System.err.println("Input file1 not found!");
			System.exit(1);
    	}
    	if (inputFile2 == null || !inputFile2.exists()){
    		System.err.println("Input file2 not found!");
			System.exit(1);
    	}
    	FileInputStream inputStream1 = new FileInputStream(inputFile1);
    	FileInputStream inputStream2 = new FileInputStream(inputFile2);

    	String parentFolder = "";
    	if (inputFile1.getParent() != null){
    		parentFolder = inputFile1.getParent() + File.separator;
    	}
    	File outputFile = new File( parentFolder  + "double_" + inputFile1.getName());
    	FileOutputStream outputStream = new FileOutputStream(outputFile);
    	
        PdfReader reader1 = new PdfReader(inputStream1);
        PdfReader reader2 = new PdfReader(inputStream2);
        Rectangle pageDoc1 = reader1.getPageSize(1);
        Rectangle pageDoc2 = reader2.getPageSize(1);
        float newWidth = pageDoc1.getWidth() + pageDoc2.getWidth();
        float newHeight;
        if (pageDoc1.getHeight() > pageDoc2.getHeight()) {
        	newHeight = pageDoc1.getHeight();
        } else {
        	newHeight = pageDoc2.getHeight();
        }
        Rectangle newPageSize = new Rectangle(newWidth, newHeight );
        Document document = new Document(newPageSize);

        Rectangle pagesize = document.getPageSize();
        
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte canvas = writer.getDirectContent();
        PdfImportedPage page1doc = writer.getImportedPage(reader1, 1);
        PdfImportedPage page2doc;
        for (int i = 1; i <= reader1.getNumberOfPages() || i <= reader2.getNumberOfPages() ; i++ ) {

  				//canvas.addTemplate(page1doc, 1f, 0, 0, 1f, deltaWLayout ,deltaHLayout );
  				if (i <= reader1.getNumberOfPages()) {
          	page1doc = writer.getImportedPage(reader1, i);
  					canvas.addTemplate(page1doc, 1f, 0, 0, 1f, 0 ,0 );	
  				}
  				if (i <= reader2.getNumberOfPages()) {
          	page2doc = writer.getImportedPage(reader2, i);
  					canvas.addTemplate(page2doc, 1f, 0, 0, 1f, page1doc.getWidth() ,0 );	
  				}
  				
					document.newPage();
        }
			  // step 5
        document.close();
        reader1.close();
    }
}
 