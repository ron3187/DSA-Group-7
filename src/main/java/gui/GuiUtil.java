package gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Small helpers so every panel can create components in one line (all use absolute bounds). */
public class GuiUtil {

    public static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 13);

    private GuiUtil() {
    }

    public static JLabel makeLabel(String text, int x, int y, int w, int h) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, w, h);
        label.setFont(LABEL_FONT);
        return label;
    }

    public static JTextField makeField(int x, int y, int w, int h) {
        JTextField field = new JTextField();
        field.setBounds(x, y, w, h);
        field.setFont(FIELD_FONT);
        return field;
    }

    public static JButton makeButton(String text, int x, int y, int w, int h, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBounds(x, y, w, h);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    /** Table model whose cells cannot be edited by typing in the table. */
    public static DefaultTableModel makeModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JTable makeTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(LABEL_FONT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    public static JScrollPane makeScroll(JComponent view, int x, int y, int w, int h) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setBounds(x, y, w, h);
        return scroll;
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Fitness Tracker", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Please confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
