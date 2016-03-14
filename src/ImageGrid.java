import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The Imagegrid class is used to display plots in the grid format.(resizable).
 */

public class ImageGrid extends JPanel {
    public static final String PATH = "./resources";

    private DefaultListModel<ImageName> imageModel = new DefaultListModel<>();
    final JTextField textField = new JTextField(20);

    public ImageGrid(ArrayList<String> images) {
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

        ShowGridAction showAction = new ShowGridAction("ImageName Grid", imageModel);
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
            JPanel jPanel = new JPanel();
            jPanel.add(carGridPanel);
            JFrame jFrame = new JFrame("Images");

            jFrame.setContentPane(jPanel);
            jFrame.pack();
            jFrame.setLocationRelativeTo(null);
            int x = jFrame.getLocation().x;
            int y = jFrame.getLocation().y -150;
            jFrame.setLocation(x,y);
            jFrame.setVisible(true);

            ImageName selectedImageName = carGridPanel.getSelectedImageName();
            if (selectedImageName != null) {
                textField.setText(selectedImageName.getName());
            }

        }
    }

    public static JFrame createAndShowGui(ArrayList<String> imagePathList) {
        JFrame frame = new JFrame("ImageGrid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ImageGrid(imagePathList));
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