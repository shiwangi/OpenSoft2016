import com.sun.deploy.panel.JavaPanel;

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
    private JLabel picLabel ;
    private JPanel picPanel;
    static String xLabel,yLabel,captionLabel;
    static  Image img;

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

//        picLabel.setIcon(new ImageIcon(img));
//        picPanel.add(picLabel);
//        //picPanel.setLayout(new BoxLayout(PicPanel,BoxLayout.PAGE_AXIS));
//        picPanel.setVisible(true);
        okayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xLabel = textFieldXScale.getText();
                yLabel = textFieldYScale.getText();
                captionLabel = textFieldCaption.getText();
            }
        });
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here




    }
}
