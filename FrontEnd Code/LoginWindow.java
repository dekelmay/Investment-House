package com.SEM.InvestmentHoustSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.EventObject;


/**
 * Created by roie on 26/05/2017.
 */
public class LoginWindow extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2773488730639665418L;
	private JPanel topPanel = new JPanel(new BorderLayout(10,10));
    private JPanel textFieldsPanel = new JPanel(new GridLayout(2,1,30,10));
    private JPanel centerPanel = new JPanel(new BorderLayout(10,10));
    private JPanel loginPanel = new JPanel(new BorderLayout(30,60));
    private JPanel bottomPanel = new JPanel(new BorderLayout(30,60));
    private JLabel logo = new JLabel(new ImageIcon("100PlusLogo.png"));
    private JTextField userField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JLabel signUpButton = new JLabel("  Sign-up");
    private JLabel loginButton = new JLabel("   Log-in");

    private String email,password;
    private Color hunPlusBlue = new Color(46, 53, 137);
    private boolean needToCleanUser=true,needToCleanPassword=true;
    enum textBoxId {USERNAME,PASSWORD}

    public LoginWindow(){

        setLayout(new BorderLayout());
        setTitle("100Plus");
        topPanel.setBackground(Color.white);
        topPanel.add(logo,BorderLayout.CENTER);

        setUserDefaultText();
        setPasswordDefaultText();


        signUpButtonSettings();
        loginButtonSettings();
        signUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                /** Open Here Registration Window*/
                new RegistrationWindow(LoginWindow.this);
                setVisible(false);
            }
        });
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    loginButtonPressed();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        });


        textFieldsPanelSettings();
        loginPanelSettings();
        centerPanelSettings();
        bottomPanelSettings();

        setup();
    }

    private void errorField(JTextField jtf){
        jtf.setForeground(Color.RED);
        jtf.setBorder(new LineBorder(Color.RED));
        jtf.setText("Missing");
    }
    private void loginButtonPressed() throws IOException, ClassNotFoundException {
        boolean isFilled=true;
        email = userField.getText();
        password = new String(passwordField.getPassword());
        if (email.isEmpty()) {
            errorField(userField);
            needToCleanUser = true;
            isFilled=false;
        }
        if(password.isEmpty()) {
            errorField(passwordField);
            needToCleanPassword = true;
            isFilled=false;
        }

        if(isFilled&&!needToCleanUser&&!needToCleanPassword){
            InvestmentHouseFacad facad = new InvestmentHouseFacadImpl();
            Account account = facad.login(email,password).getAccount();
            if(account == null){
                setAlwaysOnTop(false);
                System.out.println("ACCOUNT ERROR!1");
                JOptionPane.showMessageDialog(null,
                        "Account And Password Do Not Match!",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                System.out.println("ACCOUNT ERROR!");
                facad.logOut();
            }else {
                setVisible(false);
                MainWindow m = new MainWindow(facad, account, LoginWindow.this);
                m.setVisible(true);
            }
        }
    }

    private void setup(){
        final int WIDTH = 400, HEIGHT = 400;
        add(centerPanel,BorderLayout.CENTER);
        add(topPanel,BorderLayout.NORTH);
        add(bottomPanel,BorderLayout.SOUTH);

        setSize(new Dimension(WIDTH,HEIGHT));
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void signUpButtonSettings(){
        signUpButton.setOpaque(true);
        signUpButton.setBackground(new Color(46, 53, 137));
        signUpButton.setForeground(Color.white);
        signUpButton.setBorder(new EmptyBorder(10,20,10,20));
        signUpButton.setFont(new Font("Chalkboard SE",Font.BOLD,13));
        bottomPanel.setBorder(new EmptyBorder(10,20,10,20));
        signUpButton.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    private void bottomPanelSettings(){
        bottomPanel.setBackground(new Color(233, 235, 235));
        bottomPanel.add(signUpButton,BorderLayout.CENTER);
        bottomPanel.add(new JLabel("                       "),BorderLayout.WEST);
        bottomPanel.add(new JLabel("                         "),BorderLayout.EAST);
        bottomPanel.setBackground(new Color(233, 235, 235));
    }

    private void textFieldsPanelSettings(){
        textFieldsPanel.setBorder(new EmptyBorder(45,20,45,20));
        textFieldsPanel.setBackground(hunPlusBlue);
        textFieldsPanel.add(userField);
        textFieldsPanel.add(passwordField);
        userField.addKeyListener(new TypingListener(textBoxId.USERNAME));
        passwordField.addKeyListener(new TypingListener(textBoxId.PASSWORD));
        userField.addMouseListener(new ClickingTextFieldListener(textBoxId.USERNAME));
        passwordField.addMouseListener(new ClickingTextFieldListener(textBoxId.PASSWORD));

    }

    private void centerPanelSettings(){
        centerPanel.addMouseListener(new ClickingAroundListener());
        centerPanel.add(textFieldsPanel,BorderLayout.CENTER);
        centerPanel.add(loginPanel,BorderLayout.SOUTH);
        centerPanel.setBackground(hunPlusBlue);

    }

    private void loginPanelSettings(){
        loginPanel.setBorder(new EmptyBorder(0,20,40,20));
        loginPanel.add(loginButton,BorderLayout.CENTER);
        loginPanel.add(new JLabel("                         "),BorderLayout.EAST);
        loginPanel.add(new JLabel("                       "),BorderLayout.WEST);
        loginPanel.setBackground(hunPlusBlue);
    }

    private void loginButtonSettings(){
        loginButton.setOpaque(true);
        loginButton.setBackground(Color.white);
        loginButton.setForeground(hunPlusBlue);
        loginButton.setBorder(new EmptyBorder(10,20,10,20));
        loginButton.setFont(new Font("Chalkboard SE",Font.BOLD,13));
    }

    private void setUserDefaultText(){
        userField.setForeground(Color.gray);
        userField.setText("Email");
        userField.setCaretPosition(0);
        needToCleanUser =true;
    }

    private void setPasswordDefaultText(){
        passwordField.setForeground(Color.gray);
        passwordField.setText("Password");
        passwordField.setCaretPosition(0);
        needToCleanPassword =true;
    }


    class ClickingAroundListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if(userField.getText().isEmpty())
                setUserDefaultText();
            if(new String(passwordField.getPassword()).isEmpty())
                setPasswordDefaultText();
        }
    }
    class TypingListener extends KeyAdapter {

        textBoxId boxId;
        public TypingListener(textBoxId id){
            boxId = id;
        }
        @Override
        public void keyTyped(KeyEvent e) {

            if(e.getKeyChar() == KeyEvent.VK_ENTER){
                try {
                    loginButtonPressed();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

            cleanFieldIfNeeded(boxId,e);
        }
    }
    private void cleanFieldIfNeeded(textBoxId boxId, EventObject e){
        if((boxId.equals(textBoxId.USERNAME)&&needToCleanUser) || (boxId.equals(textBoxId.PASSWORD)&& needToCleanPassword)) {
            ((JTextField) e.getSource()).setText("");
            ((JTextField) e.getSource()).setForeground(Color.BLACK);
            if(boxId.equals(textBoxId.USERNAME))
                needToCleanUser = false;
            else
                needToCleanPassword = false;
        }
    }
    class ClickingTextFieldListener extends MouseAdapter{
        textBoxId boxId;
        private ClickingTextFieldListener(textBoxId id){
            boxId = id;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            cleanFieldIfNeeded(boxId,e);
        }
    }
}
