package me.jtrenaud1s.phas.overlaytest.view;

import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Getter
public class SettingsView extends JFrame {
    private final JTable keybindTable;

    public SettingsView() {
        setTitle("Settings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Keybinds Tab
        JPanel keybindsTab = new JPanel(new BorderLayout());
        keybindTable = createNonEditableTable();
        JScrollPane scrollPane = new JScrollPane(keybindTable);

        keybindsTab.add(scrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Keybinds", keybindsTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void updateKeybindTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        keybindTable.setModel(model);
    }

    private JTable createNonEditableTable() {
        JTable table = new JTable();
        table.setDefaultEditor(Object.class, null); // Disable editing
        return table;
    }
}