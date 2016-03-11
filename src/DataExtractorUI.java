import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by rajitha on 9/3/16.
 */
public class DataExtractorUI {
    private JTextField textField1;
    private JButton browseButton;
    private JButton okButton;
    private JPanel jpanel;
    private JProgressBar progressBar1;

    static JFrame newOne;

    public DataExtractorUI() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter( "Pdf files", "pdf");
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(null);
//                DataExtractor dataExtractor = new DataExtractor();
////
////                dataExtractor.extractDataTemp("./resources/try2.png");

                if (returnValue == JFileChooser.APPROVE_OPTION)
                {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println(selectedFile.getName());
                    DataExtractor dataExtractor = new DataExtractor();

                    //dataExtractor.extractDataTemp("./resources/try2.png");
                    ArrayList<String> imageFileList = dataExtractor.getGraphImages(selectedFile.getPath());

                    ImageGrid imageGrid = new ImageGrid(imageFileList);
                    newOne = imageGrid.createAndShowGui(imageFileList);
                    dataExtractor.extractData();
                    //progressBar1.se
                }
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newOne.remove(newOne.getContentPane());
            }
        });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("DataExtractorUI");
        frame.setContentPane(new DataExtractorUI().jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
