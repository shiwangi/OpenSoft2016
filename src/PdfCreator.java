import org.apache.commons.io.monitor.FileEntry;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by rajitha on 6/3/16.
 */
public class PdfCreator {
    String Filename ;
    PDDocument document;
    public PdfCreator(String s) {
        this.Filename = s;
        try {
            document = PDDocument.load(new File(s));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void drawpdf(List<List<String>> content) throws IOException {



        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = null;
        contentStream = new PDPageContentStream(document, page);
        drawTable(page, contentStream, 700, 100, content);
        contentStream.close();
        document.save(Filename);
    }



    public void drawTable(PDPage page, PDPageContentStream contentStream,
                          float y, float margin,
                          List<List<String>> content) throws IOException {
        final int rows = content.size();
        final int cols = content.get(0).size();
        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth() - margin - margin;
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;

        //draw the rows
        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            contentStream.drawLine(margin, nexty, margin+tableWidth, nexty);
            nexty-= rowHeight;
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx, y, nextx, y-tableHeight);
            nextx += colWidth;
        }

        //now add the text
        contentStream.setFont( PDType1Font.HELVETICA_BOLD , 12 );

        float textx = margin+cellMargin;
        float texty = y-15;
        for(int i = 0; i < content.size(); i++){
            for(int j = 0 ; j < content.get(i).size(); j++){
                String text = content.get(i).get(j);
                contentStream.beginText();
                contentStream.moveTextPositionByAmount(textx,texty);
                contentStream.drawString(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty-=rowHeight;
            textx = margin+cellMargin;
        }
    }


}
