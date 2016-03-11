import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ImageGridWithLabels extends JPanel {
    public static final String PATH = "./resources";

    private DefaultListModel<ImageName> imageModel = new DefaultListModel<>();
    final JTextField textField = new JTextField(20);
    static String xLabel,yLabel,captionLabel;

    public ImageGridWithLabels(ArrayList<String> images,GraphData graphData) {
        this.xLabel = graphData.xLabel;
        this.yLabel = graphData.yLabel;
        this.captionLabel = graphData.caption;
        for (String image : images) {
            String path = PATH + image;
            try {
                Image img =ImageIO.read(new File(path));
                img =  img.getScaledInstance(500,500,1);
                ImageIcon icon = new ImageIcon(img);
                String name = image;
               // name = name.substring(1, name.lastIndexOf("-"));
                imageModel.addElement(new ImageName(name, icon));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        ShowGridAction showAction = new ShowGridAction("Graph with Labels", imageModel);
        JButton showGridBtn = new JButton(showAction);
        add(showGridBtn);
        add(textField);
    }

    private class ShowGridAction extends AbstractAction {
        private CarGridPanel carGridPanel;

        public ShowGridAction(String name, DefaultListModel<ImageName> carModel) {
            super(name);
            carGridPanel = new CarGridPanel(carModel);
        }

        public CarGridPanel getCarGridPanel() {
            return carGridPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Window win = SwingUtilities.getWindowAncestor((Component) e.getSource());
            JPanel superPanel = new JPanel();
            superPanel.setLayout(new BoxLayout(superPanel,BoxLayout.Y_AXIS));
            JPanel jPanel = new JPanel();

            jPanel.add(carGridPanel);

            JPanel jPanelLabels = new JPanel();

            JTextField jTextFieldXscale = new JTextField(xLabel);
            jPanel.add(jTextFieldXscale);
            JTextField jTextFieldYscale = new JTextField(yLabel);
            jPanel.add(jTextFieldYscale);
            JTextField jTextFieldCaption = new JTextField(captionLabel);
            jPanel.add(jTextFieldCaption);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
            JButton jButtonOkay = new JButton();
            jButtonOkay.setText("Okay");
            buttonPanel.add(jPanelLabels);
            buttonPanel.add(jButtonOkay);

            superPanel.add(jPanel);
            superPanel.add(buttonPanel);
            JFrame jFrame = new JFrame("Graph And Scales");

            jFrame.setContentPane(superPanel);
            jFrame.pack();
            jFrame.setLocationRelativeTo(null);
            int x = jFrame.getLocation().x;
            int y = jFrame.getLocation().y -150;
            jFrame.setLocation(x,y);
            jFrame.setVisible(true);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jButtonOkay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xLabel = jTextFieldXscale.getText();
                    yLabel = jTextFieldYscale.getText();
                    captionLabel = jTextFieldCaption.getText();

                }});

            ImageName selectedImageName = carGridPanel.getSelectedImageName();
            if (selectedImageName != null) {
                textField.setText(selectedImageName.getName());
            }

        }
    }


    public static JFrame createAndShowGui(ArrayList<String> imagePathList,GraphData graphData) {
        JFrame frame = new JFrame("ImageGrid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ImageGridWithLabels(imagePathList,graphData));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }


class ImageName {
    String name;
    Icon icon;

    public ImageName(String name, Icon icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

}

@SuppressWarnings("serial")
class CarGridPanel extends JPanel {
    private JList<ImageName> carList = new JList<>();
    private ImageName selectedImageName;

    public CarGridPanel(ListModel<ImageName> model) {
        carList.setModel(model);
        carList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        carList.setVisibleRowCount(2);
        carList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value != null) {
                    ImageName imageNameValue = (ImageName) value;
                    value = imageNameValue.getIcon();
                } else {
                    value = "";
                }
                return super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
            }
        });
        setLayout(new BorderLayout());
        add(new JScrollPane(carList));

        carList.addListSelectionListener(new ListListener());
    }

    public ImageName getSelectedImageName() {
        return selectedImageName;
    }

    private class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            selectedImageName = carList.getSelectedValue();

            Window win = SwingUtilities.getWindowAncestor(CarGridPanel.this);
            win.dispose();
        }

    }
}
}