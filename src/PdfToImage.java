
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.contentstream.operator.Operator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PdfToImage
{

    /**
     * This will remove all text from a PDF document.
     *
     * @param args The command line arguments.
     *
     * @throws IOException If there is an error parsing the document.
     */
    public static void main( String[] args ) throws IOException
    {

        PDDocument document = null;
        try
        {
            document = PDDocument.load( new File("/home/shiwangi/OpenSoft_Problem_February_23_2016.pdf") );
            if( document.isEncrypted() )
            {
                System.err.println( "Error: Encrypted documents are not supported for this example." );
                System.exit( 1 );
            }
            for( PDPage page : document.getPages() )
            {
                PDFStreamParser parser = new PDFStreamParser(page);
                parser.parse();
                List<Object> tokens = parser.getTokens();
                List<Object> newTokens = new ArrayList<Object>();
                for (Object token : tokens)
                {
                    if( token instanceof Operator)
                    {
                        Operator op = (Operator)token;
                        if( op.getName().equals( "TJ") || op.getName().equals( "Tj" ))
                        {
                            //remove the one argument to this operator
                            newTokens.remove( newTokens.size() -1 );
                            continue;
                        }
                    }
                    newTokens.add( token );
                }
                PDStream newContents = new PDStream( document );
                OutputStream out = newContents.createOutputStream(COSName.FLATE_DECODE);
                ContentStreamWriter writer = new ContentStreamWriter( out );
                writer.writeTokens( newTokens );
                out.close();
                page.setContents( newContents );
            }
            document.save( "/home/shiwangi/output.pdf");
        }
        finally
        {
            if( document != null )
            {
                document.close();
            }
        }

    }



}