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

@SuppressWarnings("serial")
public class ImageGrid extends JPanel {
    public static final String PATH = "./resources";

    private DefaultListModel<Car> carModel = new DefaultListModel<>();
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
                carModel.addElement(new Car(name, icon));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        ShowGridAction showAction = new ShowGridAction("Car Grid", carModel);
        JButton showGridBtn = new JButton(showAction);
        add(showGridBtn);
        add(textField);
    }

    private class ShowGridAction extends AbstractAction {
        private CarGridPanel carGridPanel;

        public ShowGridAction(String name, DefaultListModel<Car> carModel) {
            super(name);
            carGridPanel = new CarGridPanel(carModel);
        }

        public CarGridPanel getCarGridPanel() {
            return carGridPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Window win = SwingUtilities.getWindowAncestor((Component) e.getSource());
            JDialog dialog = new JDialog(win, "Cars", ModalityType.APPLICATION_MODAL);
            dialog.add(carGridPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            int x = dialog.getLocation().x;
            int y = dialog.getLocation().y - 150;
            dialog.setLocation(x, y);
            dialog.setVisible(true);

            Car selectedCar = carGridPanel.getSelectedCar();
            if (selectedCar != null) {
                textField.setText(selectedCar.getName());
            }

        }
    }

    public static void createAndShowGui(ArrayList<String> imagePathList) {
        JFrame frame = new JFrame("ImageGrid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ImageGrid(imagePathList));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                createAndShowGui();
//            }
//        });
//    }
}



class Car {
    String name;
    Icon icon;

    public Car(String name, Icon icon) {
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
    private JList<Car> carList = new JList<>();
    private Car selectedCar;

    public CarGridPanel(ListModel<Car> model) {
        carList.setModel(model);
        carList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        carList.setVisibleRowCount(2);
        carList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value != null) {
                    Car carValue = (Car) value;
                    value = carValue.getIcon();
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

    public Car getSelectedCar() {
        return selectedCar;
    }

    private class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            selectedCar = carList.getSelectedValue();

            Window win = SwingUtilities.getWindowAncestor(CarGridPanel.this);
            win.dispose();
        }

    }
}