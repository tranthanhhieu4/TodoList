package todolist;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class TodoListApp {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField taskField;
    private JComboBox<String> priorityComboBox;
    private final String fileName = "tasks.txt";

    public TodoListApp() {
        frame = new JFrame("To-Do List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{"Task", "Status", "Priority", "Deadline"}, 0);
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        JScrollPane scrollPane = new JScrollPane(table);

        loadTasks();

        // Input and action panels
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        taskField = new JTextField();
        priorityComboBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        JButton addButton = new JButton("Add Task");
        inputPanel.add(taskField);
        inputPanel.add(priorityComboBox);
        inputPanel.add(addButton);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton deleteButton = new JButton("Delete Task");
        JButton markDoneButton = new JButton("Mark as Done");
        JButton editButton = new JButton("Edit Task");
        buttonPanel.add(deleteButton);
        buttonPanel.add(markDoneButton);
        buttonPanel.add(editButton);

        // Assemble panels
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);

        // Add listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskField.getText().trim();
                String priority = (String) priorityComboBox.getSelectedItem();
                String deadline = "-"; // Future: integrate a date picker
                if (!task.isEmpty()) {
                    tableModel.addRow(new Object[]{task, "Pending", priority, deadline});
                    taskField.setText("");
                    saveTasks();
                    JOptionPane.showMessageDialog(frame, "Task added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Task cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                    saveTasks();
                    JOptionPane.showMessageDialog(frame, "Task deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a task to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        markDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.setValueAt("Done", selectedRow, 1);
                    saveTasks();
                    JOptionPane.showMessageDialog(frame, "Task marked as done!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a task to mark as done!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String currentTask = (String) tableModel.getValueAt(selectedRow, 0);
                    String currentPriority = (String) tableModel.getValueAt(selectedRow, 2);

                    JPanel editPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                    JTextField editTaskField = new JTextField(currentTask);
                    JComboBox<String> editPriorityComboBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
                    editPriorityComboBox.setSelectedItem(currentPriority);

                    editPanel.add(new JLabel("Task:"));
                    editPanel.add(editTaskField);
                    editPanel.add(new JLabel("Priority:"));
                    editPanel.add(editPriorityComboBox);

                    int result = JOptionPane.showConfirmDialog(frame, editPanel, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        tableModel.setValueAt(editTaskField.getText().trim(), selectedRow, 0);
                        tableModel.setValueAt(editPriorityComboBox.getSelectedItem(), selectedRow, 2);
                        saveTasks();
                        JOptionPane.showMessageDialog(frame, "Task edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a task to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                tableModel.addRow(new Object[]{parts[0], parts[1], parts.length > 2 ? parts[2] : "-", parts.length > 3 ? parts[3] : "-"});
            }
        } catch (IOException e) {
            System.out.println("No saved tasks found, starting fresh.");
        }
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(tableModel.getValueAt(i, 0) + "\t" + tableModel.getValueAt(i, 1) + "\t" + tableModel.getValueAt(i, 2) + "\t" + tableModel.getValueAt(i, 3));
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error saving tasks!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoListApp::new);
    }
}

