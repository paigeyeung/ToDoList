import java.awt.*;
import javax.swing.*;
import javax.swing.JFrame;

public class ToDoList {
    public static void main(String[] args) {
        JFrame frame = new DisplayTasks();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(708, 650));
        frame.setResizable(false);
        frame.setVisible(true);
        frame.requestFocus();
    }
}
