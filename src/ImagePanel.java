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
    private JTextField textFieldXScale;
    private JTextField textFieldYScale;
    private JTextField textFieldCaption;
    private JLabel picLabel;
    private JPanel picPanel;
    private JTextField textFieldMaxX;
    private JTextField textFieldMinX;
    private JTextField textFieldMaxY;
    private JTextField textFieldMinY;
    private JPanel variablePanel;
    static String xLabel,yLabel,captionLabel;
    static GraphData graphData;
    static  Image img;
    static JFrame jFrame;

    public ImagePanel(String imageFile, GraphData graphData) {

        this.graphData = graphData;
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
    }


    public JTextField getTextFieldCaption() {
        return textFieldCaption;
    }

    public JTextField getTextFieldMaxX() {
        return textFieldMaxX;
    }

    public JTextField getTextFieldMaxY() {
        return textFieldMaxY;
    }

    public JTextField getTextFieldMinX() {
        return textFieldMinX;
    }

    public JTextField getTextFieldMinY() {
        return textFieldMinY;
    }

    public JTextField getTextFieldXScale() {
        return textFieldXScale;
    }

    public JTextField getTextFieldYScale() {
        return textFieldYScale;
    }


    public GraphData getGraphData()
    {
        ArrayList<String> labels = new ArrayList<>();
        labels.add(textFieldMinX.getText() + " " + textFieldMaxX.getText());
        labels.add(textFieldXScale.getText());
        labels.add(textFieldMinY.getText() + " " + textFieldMaxY.getText());
        labels.add(textFieldYScale.getText());
        labels.add(textFieldCaption.getText());

        GraphData gdata = new GraphData( graphData.minmaxValues,labels,graphData.ScaleMat);

        return gdata;
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
