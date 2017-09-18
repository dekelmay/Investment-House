package com.SEM.InvestmentHoustSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by roie on 26/05/2017.
 */
public class RegistrationWindow extends JFrame{
	private static final long serialVersionUID = -4169782859695475278L;
	private JLabel firstNameLabel = new JLabel("First Name:");
    private JTextField firstNameField = new JTextField();
    private JLabel lastNameLabel = new JLabel("Last Name:");
    private JTextField lastNameField = new JTextField();
    private JLabel emailLabel = new JLabel("Email:");
    private JTextField emailField = new JTextField();
    private JLabel passwordLabel = new JLabel("Password:");
    private JPasswordField passwordField = new JPasswordField();
    private JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
    private JPasswordField confirmPasswordField = new JPasswordField();
    private JLabel bankAccountLabel = new JLabel("Bank Account:");
    private JFormattedTextField bankAccountField;
    private JLabel moneyLabel = new JLabel("Initial Account Balance(USD):");
    private JFormattedTextField moneyField;

    private JLabel submitButton = new JLabel("Submit");
    private JLabel goBackButton = new JLabel(" ◀︎");
    private Color hunPlusBlue = new Color(46, 53, 137);

    private JPanel topPanel = new JPanel(new BorderLayout(0,0));
    private JPanel centerPanel = new JPanel(new GridLayout(8,2,20,5));
    private JPanel bottomPanel = new JPanel();
    private JLabel logo = new JLabel(new ImageIcon("100PlusLogo.png"));


    private boolean registerOk = true;
    private ArrayList<String> errors;
    public RegistrationWindow(LoginWindow lw){
        setTitle("100Plus - Sign up");
        topPanel.setBackground(Color.white);


        topPanel.add(logo,BorderLayout.CENTER);
        topPanel.add(goBackButton,BorderLayout.WEST);
        JLabel helper =new JLabel("      ");
        helper.setFont(new Font(null,Font.BOLD,40));
        topPanel.add(helper,BorderLayout.EAST);
        registrationFieldsPanelSettings();

        goBackButton.setFont(new Font(null,Font.BOLD,40));
        goBackButton.setForeground(hunPlusBlue);
        goBackButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                setVisible(false);
                lw.setVisible(true);
            }
        });



        submitButton.setOpaque(true);
        submitButton.setBackground(hunPlusBlue);
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(new EmptyBorder(13,35,13,35));
        submitButton.setFont(new Font("Chalkboard SE",Font.BOLD,17));

        submitButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                errors = new ArrayList<>();
                if(! new String(passwordField.getPassword()).equals(new String( confirmPasswordField.getPassword()))) {
                    errors.add("Passwords do not match");
                    registerOk = false;
                }
                Component comps[] = centerPanel.getComponents();
                for(int i=1;i<comps.length;i+=2){
                        if(((JTextField)comps[i]).getText().isEmpty()){
                            comps[i].setForeground(Color.RED);
                            ((JTextField)comps[i]).setBorder(new LineBorder(Color.RED));
                            ((JTextField)comps[i]).setText("Missing");
                            registerOk = false;
                        }
                }
                if(!emailField.getText().contains("@")){
                    errors.add("Invalid Email address");
                    registerOk = false;
                }
                InvestmentHouseFacad facad=null;
                if(registerOk){
                   try {
                       Account lead = new Account(firstNameField.getText() + " " + lastNameField.getText(), Double.parseDouble(moneyField.getText().replace(",", "")), emailField.getText());
                       lead.setPassword(new String(passwordField.getPassword()));
                       lead.setEmail(emailField.getText());
                       lead.setType(Account.AccountType.LEAD);
                    //   ClientServerRequest csr = new ClientServerRequest(ClientServerRequest.ClientServerRequestType.SIGNUP, lead);
                       facad = new InvestmentHouseFacadImpl();
                       Account account = facad.signup(lead);
                       System.out.println(account);
                       if(account == null){
                           setAlwaysOnTop(false);
                           System.out.println("ACCOUNT ERROR!1");
                           JOptionPane.showMessageDialog(null,
                                   "Account With That Email Already Exist!",
                                   "Registration Error",
                                   JOptionPane.ERROR_MESSAGE);
                           System.out.println("ACCOUNT ERROR!");
                       }else {
                           setAlwaysOnTop(false);
                           JOptionPane.showMessageDialog(null,
                                   "Congratulations, You Have Signed Up Successfully\nEmail Was Sent To "+account.getEmail(),
                                   "Sign Up Confirmation",JOptionPane.INFORMATION_MESSAGE,new ImageIcon("src/com/SEM/InvestmentHoustSystem/check.png"));
                           Thread sendMail = new Thread(new Runnable() {
                               @Override
                               public void run() {
                                   MessgingSystem.send(account.getEmail(), "Registration Succeeded!", "Hello " + account.getName() + ", Welcome to 100Plus Family!\n\n" +
                                           "We at 100Plus try to make investments simple and easy as we can, we hope you'll enjoy your investment experience.\n\nBest Regards,\nThe 100Plus Team");
                               }
                           });
                           sendMail.start();
                           setVisible(false);
                           lw.setVisible(true);
                       }
                   }catch (NullPointerException ex){
                       facad.logOut();
                   }

                }else{
                    repaint();
                }
            }
        });




        bottomPanel.setBackground(new Color(233, 235, 235));
        bottomPanel.setBorder(new EmptyBorder(25,3,25,3));
        bottomPanel.add(submitButton);

        setup();
    }

    private void setup(){
        final int WIDTH = 500, HEIGHT = 500;
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
    private void registrationFieldsPanelSettings(){
        centerPanel.setBackground(hunPlusBlue);
        centerPanel.setBorder(new EmptyBorder(30,40,30,80));

        firstNameLabel.setForeground(Color.WHITE);
        lastNameLabel.setForeground(Color.WHITE);
        emailLabel.setForeground(Color.WHITE);
        passwordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setForeground(Color.WHITE);
        bankAccountLabel.setForeground(Color.WHITE);
        moneyLabel.setForeground(Color.WHITE);


        NumberFormat bankNumFormat = NumberFormat.getIntegerInstance();
        bankNumFormat.setGroupingUsed(false);
        NumberFormatter bankNumFormatter = new NumberFormatter(bankNumFormat);
        bankNumFormatter.setValueClass(Integer.class);
        bankNumFormatter.setAllowsInvalid(false);
        bankNumFormatter.setMinimum(0);
        bankNumFormatter.setMaximum(Integer.MAX_VALUE);
        bankAccountField = new JFormattedTextField(bankNumFormatter);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setGroupingUsed(true);//CHANGE IF HAVING PROBLEMS

        moneyField = new JFormattedTextField(decimalFormat);

        centerPanel.add(firstNameLabel);
        centerPanel.add(firstNameField);
        centerPanel.add(lastNameLabel);
        centerPanel.add(lastNameField);
        centerPanel.add(emailLabel);
        centerPanel.add(emailField);
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(confirmPasswordLabel);
        centerPanel.add(confirmPasswordField);
        centerPanel.add(bankAccountLabel);
        centerPanel.add(bankAccountField);
        centerPanel.add(moneyLabel);
        centerPanel.add(moneyField);
        for(int i = 1; i< centerPanel.getComponents().length; i+=2){
            centerPanel.getComponents()[i].addMouseListener(new CleanErrorMessagesListener());
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.red);
        StringBuilder sb=new StringBuilder();
        if(errors!= null) {
            for(String error:errors) {
                g.setColor(Color.red);
                sb.append("*");
                sb.append(error);
                sb.append(" ");
            }
            g.drawString(sb.toString(),getWidth()/100,getHeight()-bottomPanel.getHeight() -5);
            errors.removeAll(errors.subList(0,errors.size()));
        }

    }

    class CleanErrorMessagesListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if(((JTextField)e.getSource()).getForeground().equals(Color.RED)){
                ((JTextField)e.getSource()).setText("");
                ((JTextField)e.getSource()).setForeground(Color.BLACK);
                ((JTextField)e.getSource()).setBorder(new LineBorder(Color.BLACK));
                repaint();
            }
        }
    }
}
