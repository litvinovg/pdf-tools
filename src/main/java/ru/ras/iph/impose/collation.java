package ru.ras.iph.impose;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class collation {
	
  
    /**
     * Main method.
     * @param    args    no arguments needed
     * @throws DocumentException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, DocumentException {
    	if (args.length != 2) {
			System.err.println("Usage: java -jar collation.jar input_file1.pdf input_file2.pdf");
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
    		inputFile2 = FileChooser.chooseFile(inputFile1.getAbsolutePath());
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
    	File outputFile = new File( parentFolder  + "collation_" + inputFile1.getName());
    	FileOutputStream outputStream = new FileOutputStream(outputFile);
    	
        PdfReader reader1 = new PdfReader(inputStream1);
        PdfReader reader2 = new PdfReader(inputStream2);
        Rectangle newPageSize = getNewPageSize(reader1, reader2,1);
        Document document = new Document(newPageSize);
      
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte canvas = writer.getDirectContent();
        PdfImportedPage page2doc;
        for (int i = 1; isPageInAnyDoc(reader1, reader2, i) ; i++ ) {

  				//canvas.addTemplate(page1doc, 1f, 0, 0, 1f, deltaWLayout ,deltaHLayout );
          PdfImportedPage pdfImportedPage = writer.getImportedPage(reader1, i);
          
          
  				if (isPageInDoc(reader1, i)) {
          	pdfImportedPage = writer.getImportedPage(reader1, i);
  					canvas.addTemplate(pdfImportedPage, 1f, 0, 0, 1f, 0 ,0 );	
  				}
  				if (isPageInDoc(reader2, i)) {
          	page2doc = writer.getImportedPage(reader2, i);
  					canvas.addTemplate(page2doc, 1f, 0, 0, 1f, 0 ,0 );	
  				}
  				if (i+1<=reader1.getNumberOfPages()|| isPageInDoc(reader2, i) ) {
  					newPageSize = getNewPageSize(reader1, reader2,i+1);
            document.setPageSize(newPageSize);
  					document.newPage();	
  				}
        }
			  // step 5
        document.close();
        reader1.close();
    }

		private static boolean isPageInAnyDoc(PdfReader reader1, PdfReader reader2, int i) {
			return isPageInDoc(reader1, i) || isPageInDoc(reader2, i);
		}

		private static Rectangle getNewPageSize(PdfReader reader1, PdfReader reader2,int i) {
			float newWidth1 = 0.0f;
			float newHeight1 = 0.0f;
			float newWidth2 = 0.0f;
			float newHeight2 = 0.0f;
			if (isPageInDoc(reader1, i)) {
				Rectangle pageDoc1 = reader1.getPageSize(i);	
				newWidth1 = pageDoc1.getWidth();
				newHeight1 = pageDoc1.getHeight();
			}
			if (isPageInDoc(reader2, i)) {
				Rectangle pageDoc2 = reader2.getPageSize(i);
				newWidth2 = pageDoc2.getWidth();
				newHeight2 = pageDoc2.getHeight();
			}
			float newWidth = Math.max(newWidth1, newWidth2);
			float newHeight = Math.max(newHeight1, newHeight2);
			
			Rectangle newPageSize = new Rectangle(newWidth, newHeight );
			return newPageSize;
		}

		private static boolean isPageInDoc(PdfReader reader1, int i) {
			return i<=reader1.getNumberOfPages();
		}
}
 