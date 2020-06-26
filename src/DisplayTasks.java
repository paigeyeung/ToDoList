import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class DisplayTasks extends javax.swing.JFrame {
    private JList<Task> taskList;
    private int unnamedTaskNum = 1;
    private JButton addTask;
    private JButton editTask;
    private JButton deleteTask;
    private JButton clearAll;
    private JButton clearOverdue;
    private JButton clearCompleted;
    private JButton clearSearch;
    private JTextField searchBar;
    private GroupLayout layout;
    private DefaultListModel<Task> model;
    private JComboBox<String> sortBy;
    private static java.awt.Dialog d;

    public DisplayTasks() {
        addTask = new JButton();
        editTask = new JButton();
        deleteTask = new JButton();
        clearAll = new JButton();
        clearOverdue = new JButton();
        clearCompleted = new JButton();
        clearSearch = new JButton();
        searchBar = new JTextField();
        model = new DefaultListModel<>();
        sortBy = new JComboBox<>();
        initComponents();
    }

    private void initComponents()
    {
        taskList = new JList<>(model);
        taskList.setFixedCellWidth(300);
        taskList.setFixedCellHeight(25);

        //task preview
        JLabel taskName = new JLabel();
        JLabel taskDate = new JLabel();
        JLabel taskDateCreated = new JLabel();

        JTextField taskNote = new JTextField();
        taskNote.setUI(new HintTextFieldUI("Add optional note...", true));
        taskNote.setMaximumSize(new Dimension(360,30));
        taskNote.setVisible(false);
        taskNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                model.getElementAt(taskList.getSelectedIndex()).setNote(taskNote.getText());
            }
        });

        JCheckBox cb = new JCheckBox();
        cb.setText("Completed");
        cb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                model.getElementAt(taskList.getSelectedIndex()).setComplete(cb.isSelected());
                changeTaskPreview(taskName,taskDate,taskDateCreated,cb,taskNote);
            }
        });
        cb.setVisible(false);

        //Changes the task preview based on which task is selected
        taskList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                changeTaskPreview(taskName,taskDate,taskDateCreated,cb,taskNote);
            }
        });

        //Allows the user to add new task
        addTask.setText("New Task");
        addTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTaskActionPerformed(evt);
                changeTaskPreview(taskName,taskDate,taskDateCreated,cb,taskNote);
            }
        });

        //Allows the user to edit the selected task
        editTask.setText("Edit");
        editTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(!taskList.isSelectionEmpty()) {
                    editTaskActionPerformed(evt);
                    changeTaskPreview(taskName,taskDate,taskDateCreated,cb,taskNote);
                }
                //model.addElement((Task) tasks.get(tasks.size()-1));
            }
        });

        //Deletes selected task
        deleteTask.setText("Delete");
        deleteTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(!taskList.isSelectionEmpty()) {
                    int index = taskList.getSelectedIndex();
                    model.removeElementAt(taskList.getSelectedIndex());
                    if(index < model.getSize())
                    {
                        taskList.setSelectedIndex(index);
                    }
                    else if(index - 1 < model.getSize())
                    {
                        taskList.setSelectedIndex(index-1);
                    }
                }
            }
        });

        //Deletes all tasks
        clearAll.setText("Clear All");
        clearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to clear all tasks?",
                        "Clear All Tasks",  JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                {
                    model.clear();
                    unnamedTaskNum = 1;
                }
            }
        });

        //Deletes overdue tasks
        clearOverdue.setText("Clear Overdue");
        clearOverdue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to clear all overdue tasks?",
                        "Clear Overdue Tasks",  JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                {
                    for(int i = 0; i < model.size(); i++)
                    {
                        if(model.getElementAt(i).getDateOfTask().before(new Date()))
                        {
                            model.removeElementAt(i);
                            i--;
                        }
                    }
                }
            }
        });

        //Deletes completed tasks
        clearCompleted.setText("Clear Completed");
        clearCompleted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int reply = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to clear all completed tasks?",
                        "Clear Completed Tasks",  JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION)
                {
                    for(int i = 0; i < model.size(); i++)
                    {
                        if(model.getElementAt(i).isComplete())
                        {
                            model.removeElementAt(i);
                            i--;
                        }
                    }
                }
            }
        });


        //Clears and removes focus from the search bar; sorts the task list
        clearSearch.setText("Clear Search");
        clearSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBar.setText("");
                requestFocusInWindow();
                sort();
            }
        });

        //Allows user to use arrow keys to navigate the list of tasks
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_UP) {
                    if(!taskList.isSelectionEmpty() && taskList.getSelectedIndex()+1 < model.size())
                    {
                        taskList.setSelectedIndex(taskList.getSelectedIndex()+1);
                    }
                }
                else if(evt.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    if(!taskList.isSelectionEmpty() && taskList.getSelectedIndex()-1 >= 0)
                    {
                        taskList.setSelectedIndex(taskList.getSelectedIndex()-1);
                    }
                }
            }
        });

        String[] categories = {"Name (A-Z)","End Date (Earliest First)","Date Created (Earliest First)"};
        sortBy = new JComboBox<String>(categories);
        sortBy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sort();
            }
        });

        searchBar.setToolTipText("Search");
        searchBar.setUI(new HintTextFieldUI("Search", true));

        searchBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search();
            }
        });

        layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        //Display elements
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(taskList)
                                        .addGap(300))
                                .addGap(15)
                                .addGroup(layout.createParallelGroup()
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(editTask)
                                                .addComponent(deleteTask)
                                                .addGap(97)
                                                .addComponent(clearSearch))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(clearOverdue)
                                                .addComponent(clearCompleted)
                                                .addComponent(clearAll))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(7)
                                                .addGroup(layout.createParallelGroup()
                                                        .addComponent(taskName)
                                                        .addComponent(taskDate)
                                                        .addComponent(taskDateCreated)
                                                        .addComponent(taskNote)
                                                        .addComponent(cb)))))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(addTask)
                                .addComponent(sortBy)
                                .addComponent(searchBar)
                                .addGap(10)));


        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(addTask)
                                .addComponent(sortBy)
                                .addComponent(searchBar))
                        .addGap(5)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(taskList))
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(editTask)
                                                .addComponent(deleteTask)
                                                .addComponent(clearSearch))
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(clearOverdue)
                                                .addComponent(clearCompleted)
                                                .addComponent(clearAll))
                                        .addGap(10)
                                        .addComponent(taskName)
                                        .addGap(5)
                                        .addComponent(taskDate)
                                        .addGap(2)
                                        .addComponent(taskDateCreated)
                                        .addGap(10)
                                        .addComponent(taskNote)
                                        .addGap(10)
                                        .addComponent(cb))));


        pack();
    }

    //Displays a new window in which the user enters a name, date, and time to create a new task
    private void addTaskActionPerformed(java.awt.event.ActionEvent evt)
    {
        Frame f = new Frame();
        d = new java.awt.Dialog(f, "Task Editor", true);
        d.setLayout(new FlowLayout());
        java.awt.Button b = new java.awt.Button("Add");
        b.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent e )
            {
                DisplayTasks.d.setVisible(false);
            }
        });
        JTextField name = new JTextField(10);
        name.setUI(new HintTextFieldUI("Name", true));

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String[] monthChoices = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep",
                "Oct","Nov","Dec"};
        JComboBox<String> monthPicker = new JComboBox<String>(monthChoices);
        monthPicker.setSelectedIndex(cal.get(Calendar.MONTH));

        Integer[] dayChoices = new Integer[31];
        for(int i = 1; i <= 31; i++)
        {
            dayChoices[i-1] = i;
        }

        JComboBox<Integer> dayPicker = new JComboBox<Integer>(dayChoices);
        dayPicker.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH)-1);

        Integer[] yearChoices = new Integer[50];
        int y = cal.get(Calendar.YEAR);
        for(int i = y; i < y+50; i++)
        {
            yearChoices[i-y] = i;
        }
        JComboBox<Integer> yearPicker = new JComboBox<Integer>(yearChoices);

        SpinnerDateModel sdm = new SpinnerDateModel();
        sdm.setValue(cal.getTime());
        JSpinner spinner = new JSpinner(sdm);

        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        DateFormatter formatter = (DateFormatter)editor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);

        d.add(name);
        d.add(monthPicker);
        d.add(dayPicker);
        d.add(yearPicker);
        d.add(editor);

        d.add(b);
        d.setSize(700,300);
        d.setVisible(true);

        String month = monthChoices[monthPicker.getSelectedIndex()];
        int monthNumber;
        switch (month.toLowerCase()) {
            case "jan":
                monthNumber = 1;
                break;
            case "feb":
                monthNumber = 2;
                break;
            case "mar":
                monthNumber = 3;
                break;
            case "apr":
                monthNumber = 4;
                break;
            case "may":
                monthNumber = 5;
                break;
            case "jun":
                monthNumber = 6;
                break;
            case "jul":
                monthNumber = 7;
                break;
            case "aug":
                monthNumber = 8;
                break;
            case "sep":
                monthNumber = 9;
                break;
            case "oct":
                monthNumber = 10;
                break;
            case "nov":
                monthNumber = 11;
                break;
            case "dec":
                monthNumber = 12;
                break;
            default:
                monthNumber = 1;
                break;
        }
        int day = dayChoices[dayPicker.getSelectedIndex()].intValue();
        int year = yearChoices[yearPicker.getSelectedIndex()].intValue();
        int hour = Integer.parseInt(editor.getTextField().getText().substring(0,2));
        int min = Integer.parseInt(editor.getTextField().getText().substring(3));

        Calendar c = new GregorianCalendar(year,monthNumber-1,day,hour,min);
        if(!name.getText().equals("")) {
            model.addElement(new Task(name.getText(), c.getTime()));
        }
        else
        {
            if(unnamedTaskNum != 1) {
                model.addElement(new Task("Task " + unnamedTaskNum, c.getTime()));
            }
            else
            {
                model.addElement(new Task("Task", c.getTime()));
            }
            unnamedTaskNum++;
        }
        sort();
    }

    //Displays a new window in which user can edit the name, date, and time of a task
    private void editTaskActionPerformed(java.awt.event.ActionEvent evt)
    {
        Frame f = new Frame();
        d = new java.awt.Dialog(f, "Task Editor", true);
        d.setLayout(new FlowLayout());
        java.awt.Button b = new java.awt.Button("Apply Changes");
        b.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent e )
            {
                DisplayTasks.d.setVisible(false);
            }
        });
        JTextField name = new JTextField(10);
        name.setUI(new HintTextFieldUI("Name", true));
        name.setText(model.get(taskList.getSelectedIndex()).getName());

        Date date = model.get(taskList.getSelectedIndex()).getDateOfTask();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String[] monthChoices = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep",
                "Oct","Nov","Dec"};
        JComboBox<String> monthPicker = new JComboBox<String>(monthChoices);
        monthPicker.setSelectedIndex(cal.get(Calendar.MONTH));

        Integer[] dayChoices = new Integer[31];
        for(int i = 1; i <= 31; i++)
        {
            dayChoices[i-1] = i;
        }

        JComboBox<Integer> dayPicker = new JComboBox<Integer>(dayChoices);
        dayPicker.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH)-1);

        Integer[] yearChoices = new Integer[50];
        int y = cal.get(Calendar.YEAR);
        for(int i = y; i < y+50; i++)
        {
            yearChoices[i-y] = i;
        }
        JComboBox<Integer> yearPicker = new JComboBox<Integer>(yearChoices);

        SpinnerDateModel sdm = new SpinnerDateModel();
        sdm.setValue(cal.getTime());
        JSpinner spinner = new JSpinner(sdm);

        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        DateFormatter formatter = (DateFormatter)editor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);

        d.add(name);
        d.add(monthPicker);
        d.add(dayPicker);
        d.add(yearPicker);
        d.add(editor);

        d.add(b);
        d.setSize(700,300);
        d.setVisible(true);

        String month = monthChoices[monthPicker.getSelectedIndex()];
        int monthNumber;
        switch (month.toLowerCase()) {
            case "jan":
                monthNumber = 1;
                break;
            case "feb":
                monthNumber = 2;
                break;
            case "mar":
                monthNumber = 3;
                break;
            case "apr":
                monthNumber = 4;
                break;
            case "may":
                monthNumber = 5;
                break;
            case "jun":
                monthNumber = 6;
                break;
            case "jul":
                monthNumber = 7;
                break;
            case "aug":
                monthNumber = 8;
                break;
            case "sep":
                monthNumber = 9;
                break;
            case "oct":
                monthNumber = 10;
                break;
            case "nov":
                monthNumber = 11;
                break;
            case "dec":
                monthNumber = 12;
                break;
            default:
                monthNumber = 1;
                break;
        }
        int day = dayChoices[dayPicker.getSelectedIndex()].intValue();
        int year = yearChoices[yearPicker.getSelectedIndex()].intValue();
        int hour = Integer.parseInt(editor.getTextField().getText().substring(0,2));
        int min = Integer.parseInt(editor.getTextField().getText().substring(3));

        Calendar c = new GregorianCalendar(year,monthNumber-1,day,hour,min);
        if(!name.getText().equals("")) {
            model.setElementAt(new Task(name.getText(), c.getTime()),taskList.getSelectedIndex());
            sort();
        }
    }

    private void sort()
    {
        //Sort based on what is selected in sortBy
        switch (sortBy.getItemAt(sortBy.getSelectedIndex())) {
            case "Name (A-Z)":
                for(int i = 1; i < model.getSize(); i++)
                {
                    Task currentElement = model.getElementAt(i);
                    int k;
                    for(k = i - 1; k >= 0 && model.getElementAt(k).compareByName(currentElement) > 0; k--)
                    {
                        model.setElementAt(model.getElementAt(k),k+1);
                    }
                    model.setElementAt(currentElement,k+1);
                }
                break;
            case "End Date (Earliest First)":
                for(int i = 1; i < model.getSize(); i++)
                {
                    Task currentElement = model.getElementAt(i);
                    int k;
                    for(k = i - 1; k >= 0 && model.getElementAt(k).after(currentElement); k--)
                    {
                        model.setElementAt(model.getElementAt(k),k+1);
                    }
                    model.setElementAt(currentElement,k+1);
                }
                break;
            case "Date Created (Earliest First)":
                for(int i = 1; i < model.getSize(); i++)
                {
                    Task currentElement = model.getElementAt(i);
                    int k;
                    for(k = i - 1; k >= 0 && model.getElementAt(k).createdAfter(currentElement); k--)
                    {
                        model.setElementAt(model.getElementAt(k),k+1);
                    }
                    model.setElementAt(currentElement,k+1);
                }
                break;
        }
    }

    public void changeTaskPreview(JLabel taskName,JLabel taskDate,JLabel taskDateCreated,JCheckBox cb,JTextField taskNote)
    {
        if(!taskList.isSelectionEmpty()) {
            taskName.setText(model.getElementAt(taskList.getSelectedIndex()).getName());
            Font font = taskName.getFont();
            taskName.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
            if(model.getElementAt(taskList.getSelectedIndex()).isComplete())
            {
                taskName.setForeground(new Color(0, 127,0));
                taskName.setText(taskName.getText() + " (Complete)");
            }
            else if(model.getElementAt(taskList.getSelectedIndex()).getDateOfTask().before(new Date()))
            {
                taskName.setForeground(Color.RED);
                taskName.setText(taskName.getText() + " (Overdue)");
            }
            else
            {
                taskName.setForeground(Color.BLACK);
            }
            taskDate.setText("Scheduled for " +
                    model.getElementAt(taskList.getSelectedIndex()).getDateOfTask().toString());
            taskDateCreated.setText("Created on " +
                    model.getElementAt(taskList.getSelectedIndex()).getDateCreated().toString());
            cb.setVisible(true);
            cb.setSelected(model.getElementAt(taskList.getSelectedIndex()).isComplete());
            taskNote.setVisible(true);
            taskNote.setText(model.getElementAt(taskList.getSelectedIndex()).getNote());
        }
        else
        {
            taskName.setText("");
            taskDate.setText("");
            taskDateCreated.setText("");
            cb.setVisible(false);
            taskNote.setVisible(false);
        }
    }

    public void search()
    {
        boolean found = false;
        String text = searchBar.getText();
        int index = 0;
        for(int i = 0; i < model.getSize(); i++)
        {
            if(model.getElementAt(i).getName().toLowerCase().indexOf(text.toLowerCase()) != -1)
            {
                Task task1 = new Task(model.getElementAt(i));
                Task task2 = new Task(model.getElementAt(index));
                model.setElementAt(task1,index);
                model.setElementAt(task2,i);
                index++;
                found = true;
            }
        }
        if(!found)
        {
            JOptionPane.showConfirmDialog(null,
                    "No results found",
                    "Search results",  JOptionPane.DEFAULT_OPTION);
        }
    }

}



