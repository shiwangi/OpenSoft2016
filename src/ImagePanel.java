import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by rajitha on 12/3/16.
 */
public class ImagePanel {
    public JPanel container;
    private JButton okayButton;
    private JTextField textFieldXScale;
    private JTextField textFieldYScale;
    private JTextField textFieldCaption;
    private JLabel picLabel;
    private JPanel picPanel;
    static String xLabel,yLabel,captionLabel;
    static  Image img;
    static JFrame jFrame;

    public ImagePanel(String imageFile, GraphData graphData) {

        textFieldXScale.setText(graphData.xLabel);
        textFieldYScale.setText(graphData.yLabel);
        textFieldCaption.setText(graphData.caption);


        try {
            img = ImageIO.read(new File(imageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        img =  img.getScaledInstance(500,500,1);

        picLabel.setIcon(new ImageIcon(img));
        okayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xLabel = textFieldXScale.getText();
                yLabel = textFieldYScale.getText();
                captionLabel = textFieldCaption.getText();
                jFrame.dispose();
                DataExtractor dataExtractor = new DataExtractor();
                //dataExtractor.getPlotsAndLegend(graphData.ScaleMat, (ArrayList<Double>) graphData.minmaxValues);
            }
        });
    }


    public String createFrame()
    {
        jFrame = new JFrame();
        jFrame.setContentPane(this.container);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        return this.xLabel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        picPanel = new JPanel();
        picLabel = new JLabel();




    }
}
