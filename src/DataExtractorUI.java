import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;


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

                if (returnValue == JFileChooser.APPROVE_OPTION)
                {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println(selectedFile.getName());
                    DataExtractor dataExtractor = new DataExtractor();

                    //dataExtractor.extractDataTemp("./resources/try2.png");
                    ArrayList<String> imageFileList = dataExtractor.getGraphImages(selectedFile.getPath());

                    ImageGrid imageGrid = new ImageGrid(imageFileList);
                    newOne = imageGrid.createAndShowGui(imageFileList);
                    final JFrame jFrame = new JFrame();
                    final JPanel bigPanel = new JPanel();
                    JPanel buttonPanel = new JPanel();
                    JButton nextButton = new JButton("Next");

                    buttonPanel.add(nextButton);
                    bigPanel.setLayout(new BoxLayout(bigPanel,BoxLayout.X_AXIS));
                    bigPanel.add(buttonPanel);
                    jFrame.setContentPane(bigPanel);

                    jFrame.pack();
                    jFrame.setVisible(true);
                    Iterator iterator = imageFileList.iterator();

                    nextButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(iterator.hasNext())
                            {
                                String imageFile = iterator.next().toString();
                                GraphData graphData = dataExtractor.extractDataForImage(imageFile);
                                ImagePanel imagePanel = new ImagePanel("./resources"+imageFile,graphData);
                                bigPanel.add(imagePanel.container);
//                                jFrame.remove(bigPanel);
//                                jFrame.add(imagePanel.container);


                            }
                        }
                    });

                    //progressBar1.se
                }
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
