import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImagePanel {
    public JPanel container;
    private JButton okayButton;
    private JTextField textFieldXScale;
    private JTextField textFieldYScale;
    private JTextField textFieldCaption;
    private JLabel picLabel;
    private JPanel picPanel;
    private JTextField textFieldMaxX;
    private JTextField textFieldMinX;
    private JTextField textFieldMaxY;
    private JTextField textFieldMinY;
    static String xLabel,yLabel,captionLabel;
    static  Image img;
    static JFrame jFrame;

    public ImagePanel(String imageFile, GraphData graphData) {

        textFieldXScale.setText(graphData.xLabel);
        //textFieldCaption.
        textFieldYScale.setText(graphData.yLabel);
        textFieldCaption.setText(graphData.caption);
        textFieldMinX.setText(graphData.minmaxValues.get(0).toString());
        textFieldMaxX.setText(graphData.minmaxValues.get(1).toString());
        textFieldMinY.setText(graphData.minmaxValues.get(2).toString());
        textFieldMaxY.setText(graphData.minmaxValues.get(3).toString());



        try {
            img = ImageIO.read(new File(imageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        img =  img.getScaledInstance(500,500,1);

        picLabel.setIcon(new ImageIcon(img));
        ArrayList<Double> minmaxValues = new ArrayList<>();
        okayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xLabel = textFieldXScale.getText();
                yLabel = textFieldYScale.getText();
                captionLabel = textFieldCaption.getText();
                if(isDouble(textFieldMinX.getText()) && isDouble(textFieldMaxX.getText()) && isDouble(textFieldMinY.getText()) && isDouble(textFieldMaxY.getText()))
                {
                    minmaxValues.set(0,Double.parseDouble(textFieldMinX.getText()));
                    minmaxValues.set(1,Double.parseDouble(textFieldMaxX.getText()));
                    minmaxValues.set(2,Double.parseDouble(textFieldMinY.getText()));
                    minmaxValues.set(3,Double.parseDouble(textFieldMaxY.getText()));
                    minmaxValues.set(4,(Double.parseDouble(textFieldMaxX.getText())-Double.parseDouble(textFieldMinX.getText()))/5.0);


                    DataExtractor dataExtractor = new DataExtractor();
                    dataExtractor.getPlotsAndLegend(graphData.ScaleMat, minmaxValues);
                    jFrame.dispose();

                }
                else
                {
                    JOptionPane.showMessageDialog(new JFrame(), "Please enter Valid numbers");

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

    private void createUIComponents() {
        // TODO: place custom component creation code here

        picPanel = new JPanel();
        picLabel = new JLabel();




    }
}
