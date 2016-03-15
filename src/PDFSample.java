import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.util.ArrayList;
import java.util.List;

public class PDFSample {

    // Page configuration
    private static final PDRectangle PAGE_SIZE =  PDPage.PAGE_SIZE_A2;
    private static final float MARGIN = 20;
    private static final boolean IS_LANDSCAPE = false;

    // Font configuration
    private static final PDFont TEXT_FONT = PDType1Font.HELVETICA;
    private static final float FONT_SIZE = 10;

    // Table configuration
    private static final float CELL_MARGIN = 2;
    


    public static Table createContent(List<String> heading, String[][] content) {
        // Total size of columns must not be greater than table width.
        List<Column> columns = new ArrayList<Column>();
        int colWidth = (int) ((PAGE_SIZE.getWidth()-2*MARGIN)/heading.size());
        for(String head:heading) {

            columns.add(new Column(head, colWidth));
        }



        float tableHeight = IS_LANDSCAPE ? PAGE_SIZE.getWidth() - (2 * MARGIN) : PAGE_SIZE.getHeight() - (2 * MARGIN);

         final float ROW_HEIGHT = tableHeight/content.length;
        Table table = new TableBuilder()
            .setCellMargin(CELL_MARGIN)
            .setColumns(columns)
            .setContent(content)
            .setHeight(tableHeight)
            .setNumberOfRows(content.length)
            .setRowHeight(ROW_HEIGHT)
            .setMargin(MARGIN)
            .setPageSize(PAGE_SIZE)
            .setLandscape(IS_LANDSCAPE)
            .setTextFont(TEXT_FONT)
            .setFontSize(FONT_SIZE)
            .build();
       return table;

    }   
}
