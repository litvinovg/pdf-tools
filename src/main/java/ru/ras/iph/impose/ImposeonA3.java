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

public class ImposeonA3 {

	static int a5Height = Math.round(PageSize.A5.getHeight());
	static int a5Width = Math.round(PageSize.A5.getWidth());
	static int a4Height = Math.round(PageSize.A4.getHeight());
	static int a4Width = Math.round(PageSize.A4.getWidth());
	static float lineThickness = 2f;

	/**
	 * Main method.
	 * 
	 * @param args
	 *          no arguments needed
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, DocumentException {
		if (args.length != 1) {
			System.err.println("Usage: java -jar impose.jar input_file.pdf");
		}
		File inputFile = null;
		if (args.length == 1) {
			String inputFileName = args[0];
			inputFile = new File(inputFileName);
		} else {
			inputFile = FileChooser.chooseFile();
		}

		if (inputFile == null || !inputFile.exists()) {
			System.err.println("Input file not found!");
			System.exit(1);
		}
		FileInputStream is = new FileInputStream(inputFile);
		String parentFolder = "";
		if (inputFile.getParent() != null) {
			parentFolder = inputFile.getParent() + File.separator;
		}
		File outputFile = new File(parentFolder + "A3_" + inputFile.getName());
		FileOutputStream os = new FileOutputStream(outputFile);

		PdfReader reader = new PdfReader(is);
		Document document = new Document(PageSize.A3);

		PdfWriter writer = PdfWriter.getInstance(document, os);
		document.open();
		PdfImportedPage page;
		int layout = 1;
		for (int i = 1; i <= reader.getNumberOfPages(); i = i + 8) {
			page = writer.getImportedPage(reader, i);

			int curHeight = Math.round(page.getHeight());
			int curWidth = Math.round(page.getWidth());

			if ((curWidth <= a5Width && curHeight <= a5Height) || (curHeight <= a5Width && curWidth <= a5Height)) {

				layout = imposeA5onA3(reader, document, writer, page, layout, i);

			} else if ((curWidth <= a4Width && curHeight <= a4Height) || (curHeight <= a4Width && curWidth <= a4Height)) {

				layout = imposeA4onA3(reader, document, writer, page, layout, i);

			}

		}

		document.close();
		reader.close();
	}

	private static int imposeA4onA3(PdfReader reader, Document document, PdfWriter writer, PdfImportedPage page, int layout, int i) {

		PdfContentByte canvas = writer.getDirectContent();
		float HeightA3 = document.getPageSize().getHeight();
		float WidthA3 = document.getPageSize().getWidth();

		float deltaWLayout = 1f;
		float deltaHLayout = 1f;
		float deltaW = 1f;
		float deltaH = 1f;
		if (page.getHeight() < page.getWidth()) {
			deltaW = (WidthA3 - page.getWidth()) / 2;
			deltaH = HeightA3 / 2 - page.getHeight();
			deltaWLayout = (WidthA3 - page.getWidth()) / 2;
			deltaHLayout = HeightA3 / 2 - page.getHeight();

		} else {
			deltaW = (WidthA3 - page.getHeight()) / 2;
			deltaH = HeightA3 / 2 - page.getWidth();
			deltaWLayout = (WidthA3 - page.getHeight()) / 2;
			deltaHLayout = HeightA3 / 2 - page.getWidth();
		}

		drawRectangle2(canvas);
		drawFoldLines(canvas);
		drawSerifs2(canvas, layout);
		// 1 Page
		if (inputIsAlbum(page)) {
			canvas.addTemplate(page, 1f, 0, 0, 1f, deltaWLayout, deltaHLayout);
		} else {
			canvas.addTemplate(page, 0, -1f, 1f, 0, deltaW, HeightA3 / 2);
		}
		// 4 Page
		if (i + 3 <= reader.getNumberOfPages()) {
			page = writer.getImportedPage(reader, i + 3);
			if (inputIsAlbum(page)) {
				canvas.addTemplate(page, -1f, 0, 0, -1f, WidthA3 - deltaWLayout, HeightA3 - deltaHLayout);
			} else {
				canvas.addTemplate(page, 0, -1f, 1f, 0, deltaW, HeightA3 - deltaH);
			}
		}
		// Second layout
		// If there is at least one more page to place in new layout
		if (i + 1 <= reader.getNumberOfPages()) {
			document.newPage();
			// next layout
			layout++;
			drawFoldLines(canvas);
			// 3 Page
			if (i + 2 <= reader.getNumberOfPages()) {
				page = writer.getImportedPage(reader, i + 2);
				if (inputIsAlbum(page)) {
					canvas.addTemplate(page, 1f, 0, 0, 1f, deltaWLayout, deltaHLayout);
				} else {
					canvas.addTemplate(page, 0, -1f, 1f, 0, deltaW, HeightA3 / 2);
				}
			}
			// 2 Page
			page = writer.getImportedPage(reader, i + 1);
			if (inputIsAlbum(page)) {
				canvas.addTemplate(page, -1f, 0, 0, -1f, WidthA3 - deltaWLayout, HeightA3 - deltaHLayout);
			} else {
				canvas.addTemplate(page, 0, -1f, 1f, 0, deltaW, HeightA3 - deltaH);
			}
		}
		// Third layout
		// If there is at least one more page to place in new layout
		if (i + 4 <= reader.getNumberOfPages()) {
			document.newPage();
			// next layout
			layout++;
			drawRectangle2(canvas);
			drawFoldLines(canvas);
			drawSerifs2(canvas, layout);
			// 5 Page
			page = writer.getImportedPage(reader, i + 4);
			if (inputIsAlbum(page)) {
				canvas.addTemplate(page, 1f, 0, 0, 1f, deltaWLayout, deltaHLayout);
			} else {
				canvas.addTemplate(page, 0, -1f, 1f, 0, deltaW, HeightA3 / 2);
			}
			// 8 page
			if (i + 7 <= reader.getNumberOfPages()) {
				page = writer.getImportedPage(reader, i + 7);
				if (inputIsAlbum(page)) {
					canvas.addTemplate(page, -1f, 0, 0, -1f, WidthA3 - deltaWLayout, HeightA3 - deltaHLayout);
				} else {
					canvas.addTemplate(page, 0, -1f, 1f, 0, deltaW, HeightA3 - deltaH);
				}
			}
		}
		// Fourth layout
		if (i + 5 <= reader.getNumberOfPages()) {
			document.newPage();
			// next layout
			layout++;
			drawFoldLines(canvas);
			// Page 7
			if (i + 6 <= reader.getNumberOfPages()) {
				// 3
				page = writer.getImportedPage(reader, i + 6);
				if (inputIsAlbum(page)) {
					canvas.addTemplate(page, 1f, 0, 0, 1f, deltaWLayout, deltaHLayout);
				} else {
					canvas.addTemplate(page, 0, -1f, 1f, 0, deltaW, HeightA3 / 2);
				}
			}
			// Page 6
			page = writer.getImportedPage(reader, i + 5);
			if (inputIsAlbum(page)) {
				canvas.addTemplate(page, -1f, 0, 0, -1f, WidthA3 - deltaWLayout, HeightA3 - deltaHLayout);
			} else {
				canvas.addTemplate(page, 0, -1f, 1f, 0, deltaW, HeightA3 - deltaH);
			}

		}
		// New layout if any further pages exists
		if (i + 8 <= reader.getNumberOfPages()) {
			document.newPage();
			// next layout
			layout++;
		}
		return layout;
	}

	private static int imposeA5onA3(PdfReader reader, Document document, PdfWriter writer, PdfImportedPage input, int layout, int i) {
		PdfContentByte canvas = writer.getDirectContent();
		float outHeight = document.getPageSize().getHeight();
		float outWidth = document.getPageSize().getWidth();

		drawFoldLines(canvas);
		drawRectangle4(canvas);
		drawSerifs4(canvas, layout);
		
		// 1 Page 7
		if (inputIsAlbum(input)) {
			canvas.addTemplate(input, 0, 1f, -1f, 0, outWidth/2 + input.getHeight(), outWidth/2 - input.getHeight());
		} else {
			canvas.addTemplate(input, 1f, 0, 0, 1, outWidth/2, outHeight/2 - input.getHeight());
		}
		// 8 Page
		if (i + 7 <= reader.getNumberOfPages()) {
			input = writer.getImportedPage(reader, i + 7);
			if (inputIsAlbum(input)) {
				canvas.addTemplate(input, 0, 1f, -1f, 0, input.getHeight(), outHeight/2 - input.getWidth());
			} else {
				canvas.addTemplate(input, 1f, 0, 0, 1f, outWidth/2 - input.getWidth(), outHeight/2 - input.getHeight());
			}
		}
		// 5 Page
		if (i + 4 <= reader.getNumberOfPages()) {
			input = writer.getImportedPage(reader, i + 4);
			if (inputIsAlbum(input)) {
				canvas.addTemplate(input, 0, -1f, 1f, 0, outWidth/2 - input.getHeight(), outHeight/2 + input.getWidth());
			} else {
				canvas.addTemplate(input, -1f, 0, 0, -1f, outWidth / 2, outHeight/2 + input.getHeight());
			}
		}
		// 4 Page
		if (i + 3 <= reader.getNumberOfPages()) {
			input = writer.getImportedPage(reader, i + 3);
			if (inputIsAlbum(input)) {
				canvas.addTemplate(input, 0, -1f, 1f, 0, input.getHeight(), outHeight / 2 + input.getWidth());
			} else {
				canvas.addTemplate(input, -1f, 0, 0, -1f, outWidth/2 + input.getWidth(), outHeight/2 + input.getHeight());

			}
		}

		// Second layout
		// If there is at least one more page to place in new layout
		if (i + 1 <= reader.getNumberOfPages()) {
			document.newPage();
			// next layout
			layout++;
			// Draw fold lines
			drawFoldLines(canvas);
			// 7 Page
			if (i + 6 <= reader.getNumberOfPages()) {
				input = writer.getImportedPage(reader, i + 6);
				if (inputIsAlbum(input)) {
					canvas.addTemplate(input, 0, 1f, -1f, 0, outWidth / 2 + input.getHeight(), outHeight / 2 - input.getWidth());
				} else {
					canvas.addTemplate(input, 1f, 0, 0, 1, outWidth / 2, outHeight/2 -input.getHeight());
				}
			}
			// Page 2
			input = writer.getImportedPage(reader, i + 1);
			if (inputIsAlbum(input)) {
				canvas.addTemplate(input, 0, 1f, -1f, 0, input.getHeight(), outHeight / 2 - input.getWidth());
			} else {
				canvas.addTemplate(input, 1f, 0, 0, 1f, outWidth/2 - input.getWidth(), outHeight/2 - input.getHeight());
			}
			// Page 3
			if (i + 2 <= reader.getNumberOfPages()) {
				input = writer.getImportedPage(reader, i + 2);
				if (inputIsAlbum(input)) {
					canvas.addTemplate(input, 0, -1f, 1f, 0, outWidth / 2 - input.getHeight(), outHeight/2 + input.getWidth());
				} else {
					canvas.addTemplate(input, -1f, 0, 0, -1f, outWidth / 2, outHeight/2 + input.getHeight());
				}
			}
			// Page 6
			if (i + 5 <= reader.getNumberOfPages()) {
				input = writer.getImportedPage(reader, i + 5);
				if (inputIsAlbum(input)) {
					canvas.addTemplate(input, 0, -1f, 1f, 0, input.getHeight(), outHeight/2 + input.getWidth());
				} else {
					canvas.addTemplate(input, -1f, 0, 0, -1f, outWidth/2 + input.getWidth(), outHeight/2 + input.getHeight());
				}
			}
		}
		// New layout if any further pages exists
		if (i + 8 <= reader.getNumberOfPages()) {
			document.newPage();
			// next layout
			layout++;
		}
		return layout;
	}

	private static boolean inputIsAlbum(PdfImportedPage importedPage) {
		return importedPage.getWidth() > importedPage.getHeight();
	}

	/**
	 * @param canvas
	 */
	private static void drawSerifs(PdfContentByte canvas) {

		canvas.stroke();
	}

	/**
	 * @param canvas
	 */
	private static void drawFoldLines(PdfContentByte canvas) {
		canvas.moveTo(3, 595.5);
		canvas.lineTo(30, 595.5);
		canvas.moveTo(812, 595.5);
		canvas.lineTo(839, 595.5);
		canvas.moveTo(421, 3);
		canvas.lineTo(421, 30);
		canvas.moveTo(421, 1161);
		canvas.lineTo(421, 1188);
		canvas.stroke();

	}

	/**
	 * @param canvas
	 */
	private static void drawRectangle2(PdfContentByte canvas) {
		int startX = 720;
		int startY = 587;
		canvas.moveTo(startX, startY);
		canvas.lineTo(startX, startY + 17);
		canvas.lineTo(startX + 25, startY + 17);
		canvas.lineTo(startX + 25, startY);
		canvas.closePath();
		canvas.fillStroke();
		canvas.stroke();
	}

	/**
	 * @param canvas
	 * @param layout
	 */
	private static void drawSerifs2(PdfContentByte canvas, int layout) {
		float yt = 587f;
		float yb = 604f;
		int startX = 710;
		int endX = 50;
		int spacing = 28;
		float inc = 2.8f;
		int countSpaces = Math.abs(startX - endX) / spacing + 1;
		int position = (layout / 2) % countSpaces;
		int countSerifs = layout / (2 * countSpaces);
		for (int i = 0; i <= countSerifs; i++) {
			canvas.setLineWidth(lineThickness);
			canvas.moveTo(startX - position * spacing - i * inc, yt);
			canvas.lineTo(startX - position * spacing - i * inc, yb);
		}
		canvas.stroke();
	}

	/**
	 * @param canvas
	 */
	private static void drawRectangle4(PdfContentByte canvas) {
		int startX = 413;
		int startY = 545;
		canvas.moveTo(startX, startY);
		canvas.lineTo(startX + 17, startY);
		canvas.lineTo(startX + 17, startY - 25);
		canvas.lineTo(startX, startY - 25);
		canvas.closePath();
		canvas.fillStroke();
		canvas.stroke();
	}

	/**
	 * @param canvas
	 * @param layout
	 */
	private static void drawSerifs4(PdfContentByte canvas, int layout) {
		float xl = 413f;
		float xr = 430f;
		int startY = 500;
		int endY = 50;
		int spacing = 28;
		float inc = 2.8f;
		int countSpaces = Math.abs(startY - endY) / spacing + 1;
		int position = (layout / 2) % countSpaces;
		int countSerifs = layout / (2 * countSpaces);
		for (int i = 0; i <= countSerifs; i++) {
			canvas.setLineWidth(lineThickness);
			canvas.moveTo(xl, startY - position * spacing - i * inc);
			canvas.lineTo(xr, startY - position * spacing - i * inc);
		}
		canvas.stroke();

	}

}
