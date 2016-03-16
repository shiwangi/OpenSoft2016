import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PDFTableGenerator {

    // Generates document from Table object
    public void generatePDF(List<Table> table, List<List<String>> captionList, String inputFilePath,List<String> imageFilePath) throws IOException, COSVisitorException {
        PDDocument doc = null;
        PDDocument inputDoc = null;
        try {
           doc = PDDocument.load("./resources/InitPage.pdf");

            inputDoc = PDDocument.load(inputFilePath);

            List<PDPage> pages = inputDoc.getDocumentCatalog().getAllPages();
           // doc = new PDDocument();
            int i=0,pageNumber=0,count=0;
            for(Table t:table) {

                if(count==0) {

                    while (count == 0 && pageNumber<pages.size()) {
                        count = getCountOfImages(pageNumber, imageFilePath);
                        doc.addPage(pages.get(pageNumber));

                        pageNumber++;
                    }


                }
//                if(t.getNumberOfRows()<2 || t.getNumberOfColumns()<2){
//
//                    count--;
//                    i++;
//                    continue;
//                }
                drawTable(doc, t, captionList.get(i));
                count--;
                i++;
            }
            doc.save("./output/plot_data.pdf");
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    private int getCountOfImages(int i, List<String> imageFilePath) {

        String fileName = "/roi" + i;
            int count=0;
            for(String name: imageFilePath)
            {
                if(name.contains(fileName))
                {
                    count++;
                }
            }

        return count;
    }

    // Configures basic setup for the table and draws it page by page
    public void drawTable(PDDocument doc, Table table, List<String> captions) throws IOException {
        // Calculate pagination
        Integer rowsPerPage = new Double(Math.floor(table.getHeight() / table.getRowHeight())).intValue() - 1; // subtract
        Integer numberOfPages = new Double(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();

        // Generate each page, get the content and draw it
        for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
            PDPage page = generatePage(doc, table);
            PDPageContentStream contentStream = generateContentStream(doc, page, table);
            String[][] currentPageContent = getContentForCurrentPage(table, rowsPerPage, pageCount);
            drawCurrentPage(table, currentPageContent, contentStream,captions);
        }
    }

    // Draws current page table grid and border lines and content
    private void drawCurrentPage(Table table, String[][] currentPageContent, PDPageContentStream contentStream, List<String> captions)
            throws IOException {
        float tableTopY = table.isLandscape() ? table.getPageSize().getWidth() - table.getMargin() : table.getPageSize().getHeight() - table.getMargin();

        // Draws grid and borders
        drawTableGrid(table, currentPageContent, contentStream, tableTopY);

        // Position cursor to start drawing content
        float nextTextX = table.getMargin() + table.getCellMargin();
        // Calculate center alignment for text in cell considering font height
        float nextTextY = tableTopY - (table.getRowHeight() / 2)
                - ((table.getTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * table.getFontSize()) / 4);

        // Write column headers
        String[] arrayHeader = table.getColumnsNamesAsArray();

        //x-label , y-label
        String[] caption = new String [arrayHeader.length];
        caption[0]   = captions.get(0);
        caption[1] = captions.get(1);
        writeContentLine(caption, contentStream, nextTextX, nextTextY, table, 1);

        //colours
        arrayHeader[0] = "";

        nextTextY -= table.getRowHeight();
        nextTextX = table.getMargin() + table.getCellMargin();
        writeContentLine(arrayHeader, contentStream, nextTextX, nextTextY, table, 3);

        nextTextY -= table.getRowHeight();
        nextTextX = table.getMargin() + table.getCellMargin();

        // Write content
        for (int i = 0; i < currentPageContent.length; i++) {
            writeContentLine(currentPageContent[i], contentStream, nextTextX, nextTextY, table, 0);
            nextTextY -= table.getRowHeight();
            nextTextX = table.getMargin() + table.getCellMargin();
        }
        String figCaption[] = new String[arrayHeader.length];
        String figureCap = "Figure: " + captions.get(2);
        figCaption[0]=figureCap;

        writeContentLine(figCaption, contentStream, nextTextX, nextTextY, table, 2);
//
//        nextTextY -= table.getRowHeight();
//        nextTextX = table.getMargin() + table.getCellMargin();
        contentStream.close();
    }

    private boolean isColor(String s) {
        String[] values = s.split(" ");
        if(values.length==3){
            for(int i=0;i<3;i++){
                if(!isDouble(values[i])){
                    return false;
                }
            }
        }
        else {
            return false;
        }
        return true;

    }
    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // Writes the content for one line
    private void writeContentLine(String[] lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
                                  Table table, int isHeader) throws IOException {
        for (int i = 0; i < table.getNumberOfColumns(); i++) {
            if(lineContent!=null) {
                String text = lineContent[i];
                contentStream.beginText();
                if(isHeader==1)
                    contentStream.setNonStrokingColor(Color.BLUE);
                if(isHeader==2)
                    contentStream.setNonStrokingColor(Color.MAGENTA);
                if(isHeader==3) {
                    if(isColor(text)){
                        String []tokens = text.split(" ");
                        int []color = new int[3];
                        int count=0;
                        for(String c :tokens){
                            color[count] =(int) Double.parseDouble(c);
                            count++;
                        }
                        contentStream.setNonStrokingColor(color[0],color[1],color[2]);
                    }
                }
                contentStream.moveTextPositionByAmount(nextTextX, nextTextY);
                contentStream.drawString(text != null ? text : "");
                contentStream.endText();
                contentStream.setNonStrokingColor(Color.BLACK);
                nextTextX += table.getColumns().get(i).getWidth();
            }

        }
    }

    private void drawTableGrid(Table table, String[][] currentPageContent, PDPageContentStream contentStream, float tableTopY)
            throws IOException {
        // Draw row lines
        float nextY = tableTopY;
        for (int i = 0; i <= currentPageContent.length + 1; i++) {
            contentStream.drawLine(table.getMargin(), nextY, table.getMargin() + table.getWidth(), nextY);
            nextY -= table.getRowHeight();
        }

        // Draw column lines
        final float tableYLength = table.getRowHeight() + (table.getRowHeight() * currentPageContent.length);
        final float tableBottomY = tableTopY - tableYLength;
        float nextX = table.getMargin();
        for (int i = 0; i < table.getNumberOfColumns(); i++) {
            contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
            nextX += table.getColumns().get(i).getWidth();
        }
        contentStream.drawLine(nextX, tableTopY, nextX, tableBottomY);
    }

    private String[][] getContentForCurrentPage(Table table, Integer rowsPerPage, int pageCount) {
        int startRange = pageCount * rowsPerPage;
        int endRange = (pageCount * rowsPerPage) + rowsPerPage;
        if (endRange > table.getNumberOfRows()) {
            endRange = table.getNumberOfRows();
        }
        return Arrays.copyOfRange(table.getContent(), startRange, endRange);
    }

    private PDPage generatePage(PDDocument doc, Table table) {
        PDPage page = new PDPage();
        page.setMediaBox(table.getPageSize());
        page.setRotation(table.isLandscape() ? 90 : 0);
        doc.addPage(page);
        return page;
    }

    private PDPageContentStream generateContentStream(PDDocument doc, PDPage page, Table table) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(doc, page, false, false);
        // User transformation matrix to change the reference when drawing.
        // This is necessary for the landscape position to draw correctly
        if (table.isLandscape()) {
            contentStream.concatenate2CTM(0, 1, -1, 0, table.getPageSize().getWidth(), 0);
        }
        contentStream.setFont(table.getTextFont(), table.getFontSize());
        return contentStream;
    }
}
