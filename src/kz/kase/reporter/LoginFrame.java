package kz.kase.reporter;

import kz.kase.reporter.ui.MainFrame;
import kz.kase.reporter.util.GlobalVars;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        getContentPane().setLayout(new MigLayout("", "5[fill,grow]5", "10[]10[]10"));
        setSize(new Dimension(250, 130));
        setResizable(false);

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        Point myCenter = new Point((int) center.getX() - getWidth() / 2,
                (int) center.getY() - getHeight() / 2);
        setLocation(myCenter);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel nameLab = new JLabel("Имя");
        //JLabel passLAb = new JLabel("пароль");

        final JTextField nameField = new JTextField(100);
        //JTextField passField = new JTextField();
        nameField.setText("Centras");
        JButton acceptBut = new JButton("Войти");

        acceptBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GlobalVars.setFirm(nameField.getText());
                MainFrame mf = new MainFrame();
                StatusSelector.getInstance().addListener(mf);
                dispose();
            }
        });

        JButton exitBut = new JButton("Выход");

        exitBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        add(nameLab, "split 2");
        add(nameField, "wrap");
        add(acceptBut, " w 90!, split 2");
        add(exitBut, "w 90!, wrap");

        setVisible(true);


        setVisible(true);
    }

//    public static void main(String[] args) {
//        LoginFrame lf = new LoginFrame();
//    }


}
