package ru.ras.iph.impose;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SimpleBookmark;

public class splitByOutline {
	
	static int a5Height = Math.round(PageSize.A5.getHeight());
	static int a5Width = Math.round(PageSize.A5.getWidth());
	static int a4Height = Math.round(PageSize.A4.getHeight());
	static int a4Width = Math.round(PageSize.A4.getWidth());
	static float lineThickness = 2f;
	
    /**
     * Main method.
     * @param    args    no arguments needed
     * @throws DocumentException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, DocumentException {
    	if (args.length != 1) {
			System.err.println("Usage: java -jar split.jar input_file.pdf");
			}
    	File inputFile = null;
    	if (args.length == 1){
    		String inputFileName = args[0];
      	inputFile = new File(inputFileName);	
    	} else {
    		inputFile = FileChooser.chooseFile();
    	}
    	
    	if (inputFile == null || !inputFile.exists()){
    		System.err.println("Input file not found!");
			System.exit(1);
    	}
    	FileInputStream is = new FileInputStream(inputFile);
    	String parentFolder = "";
    	if (inputFile.getParent() != null){
    		parentFolder = inputFile.getParent() + File.separator;
    	}
        PdfReader reader = new PdfReader(is);

        TreeSet<Integer> pageNums = new TreeSet();
        
        List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(reader);
        Object page = null;
        String pageValue = null;
        Integer pageNum = null;
        if (list != null) {
        	for (HashMap<String, Object> hashMap : list) {
        		page = hashMap.get("Page");
        		if (page instanceof java.lang.String) {
        			pageValue = (String) page;
        			
				}
        		if (pageValue != null && pageValue.contains(" ")) {
        			pageValue = pageValue.substring(0, pageValue.indexOf(" "));
        			if (isInt(pageValue)) {
        				pageNum = Integer.parseInt(pageValue);
            			pageNums.add(pageNum);	
        			}
        		}
            	           	
            	 List<HashMap<String, Object>> level2map =  (List<HashMap<String, Object>>) hashMap.get("Kids");
            	 if (level2map != null) {
            		 for (HashMap<String, Object> hashMap1 : level2map) {
                 		page = hashMap1.get("Page");
                		if (page instanceof java.lang.String) {
                			pageValue = (String) page;
                			
        				}
                		if (pageValue != null && pageValue.contains(" ")) {
                			pageValue = pageValue.substring(0, pageValue.indexOf(" "));
                			if (isInt(pageValue)) {
                				pageNum = Integer.parseInt(pageValue);
                    			pageNums.add(pageNum);	
                			}
                			
                		}
                 	 }	 
            	 }
    		}	
        }
        
        Document document = new Document(reader.getPageSizeWithRotation(1));
        File outputFile = new File( parentFolder  + "Title_" + inputFile.getName());
    	FileOutputStream os = new FileOutputStream(outputFile);
       // PdfWriter writer = PdfWriter.getInstance(document, os);
        PdfCopy writer = new PdfCopy(document, os);
   
            
        document.open();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
        	if (pageNums.contains(i)) {
        		//close current document
        		document.close();
        		outputFile = new File( parentFolder  + i + "_" + inputFile.getName());
        		document = new Document(reader.getPageSizeWithRotation(i));
           		os = new FileOutputStream(outputFile);
           		writer.close();
        		writer =  new PdfCopy(document, os);
        		document.open();
        	}
            PdfImportedPage page1 = writer.getImportedPage(reader, i);
            writer.addPage(page1);
        }
        document.close();
        
        /*for (Integer num : pageNums) {
        	System.out.println(num);
			
		}*/


        
        reader.close();
    }

    public static boolean isInt(String string) {
        Pattern doublePattern = Pattern.compile("\\d+");
        return doublePattern.matcher(string).matches();
    }

	
}

