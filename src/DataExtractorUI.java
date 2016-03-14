import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Pdf files", "pdf");
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println(selectedFile.getName());
                    final DataExtractor dataExtractor = new DataExtractor();

                    //dataExtractor.extractDataTemp("./resources/try2.png");
                    ArrayList<String> imageFileList = dataExtractor.getGraphImages(selectedFile.getPath());

                    ImageGrid imageGrid = new ImageGrid(imageFileList);
                    newOne = imageGrid.createAndShowGui(imageFileList);
                    final JFrame jFrame = new JFrame();
                    final JPanel bigPanel = new JPanel();
                    JPanel buttonPanel = new JPanel();
                    final JButton nextButton = new JButton("Next");

                    buttonPanel.add(nextButton);
                    bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.PAGE_AXIS));
                    bigPanel.add(buttonPanel);
                    jFrame.setContentPane(bigPanel);

                    jFrame.pack();
                    jFrame.setVisible(true);
                    final Iterator iterator = imageFileList.iterator();

                    String imageFile = iterator.next().toString();
                    GraphData graphData = dataExtractor.extractDataForImage(imageFile);

                    final ImagePanel[] imagePanel = {new ImagePanel("./resources" + imageFile, graphData)};

                    jFrame.add(imagePanel[0].container);

                    jFrame.pack();

                    nextButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (iterator.hasNext()) {
                                String imageFile = iterator.next().toString();
                                GraphData graphData = dataExtractor.extractDataForImage(imageFile);


                                // bigPanel.remove(1);
                                // bigPanel.add(imagePanel.container);
                                jFrame.remove(imagePanel[0].container);
                                imagePanel[0] = new ImagePanel("./resources" + imageFile, graphData);
                                jFrame.setContentPane(bigPanel);
                                jFrame.add(imagePanel[0].container);

                                jFrame.pack();


                                //progressBar1.se
                            }
                        }
                    });

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
