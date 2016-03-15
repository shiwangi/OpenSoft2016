import org.apache.pdfbox.exceptions.COSVisitorException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class DataExtractorUI {
    private JTextField textField1;
    private JButton browseButton;
    private JButton okButton;
    private JPanel jpanel;
    private JProgressBar progressBar1;

    static JFrame newOne;
    static GraphData graphData;

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
                    newOne = imageGrid.createAndShowGui(imageFileList, null);
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
                     graphData = dataExtractor.extractDataForImage(imageFile);

                    final ImagePanel[] imagePanel = {new ImagePanel("./resources" + imageFile, graphData)};

                    jFrame.add(imagePanel[0].container);

                    jFrame.pack();

                    nextButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String xScale,yScale,caption,minX,maxX,minY,maxY;
                            minX = imagePanel[0].getTextFieldMinX().getText();
                            maxX = imagePanel[0].getTextFieldMaxX().getText();
                            minY = imagePanel[0].getTextFieldMinY().getText();
                            maxY = imagePanel[0].getTextFieldMaxY().getText();
                            xScale = imagePanel[0].getTextFieldXScale().getText();
                            yScale = imagePanel[0].getTextFieldYScale().getText();
                            caption = imagePanel[0].getTextFieldCaption().getText();
//                            if(dataExtractor.plotJframe!=null)
//                                .dispose();
//                            //Should actually be the the new values;
                            dataExtractor.getPlotsAndLegend(graphData.ScaleMat, (ArrayList<Double>) graphData.minmaxValues);

                            if(!isDouble(minX) || !isDouble(maxX) ||!isDouble(minY) || !isDouble(maxY) )
                            {
                                JOptionPane.showMessageDialog(new JFrame(), "give double values for min-max values");
                            }
                            System.out.println(imagePanel[0].getTextFieldXScale().getText());
                            if (iterator.hasNext()) {
                                System.out.println(imagePanel[0].container.getComponent(1));
                                String imageFile = iterator.next().toString();
                                graphData = dataExtractor.extractDataForImage(imageFile);
                                jFrame.remove(imagePanel[0].container);
                                imagePanel[0] = new ImagePanel("./resources" + imageFile, graphData);
                                jFrame.setContentPane(bigPanel);
                                jFrame.add(imagePanel[0].container);
                                jFrame.pack();
                            }
                            else{
                                jFrame.dispose();
                                PDFTableGenerator pdfTableGenerator = new PDFTableGenerator();
                                try {
                                    pdfTableGenerator.generatePDF(dataExtractor.tableList);
                                } catch (COSVisitorException e1) {
                                    e1.printStackTrace();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });

                }
            }
        });
    }


    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static void main(String[] args) {

        JFrame frame = new JFrame("DataExtractorUI");
        frame.setContentPane(new DataExtractorUI().jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
