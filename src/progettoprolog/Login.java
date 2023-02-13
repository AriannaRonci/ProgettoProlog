package progettoprolog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;
import progettoprolog.utils.StyledButtonUI;

public class Login extends JFrame implements ActionListener {

 	private static final long serialVersionUID = 1L;
   private JPanel panel;
   private JLabel user_label, password_label, register;
   private JTextField username;
   private JPasswordField password;
   private JButton submit;
   
   public Login() {
	   
	   panel = new JPanel(new BorderLayout());
	   panel.setLayout(null);
       panel.setBackground(new Color(230,250,255));

       // Username Label
      user_label = new JLabel();
      user_label.setText("Username");
      user_label.setBounds(100, 30, 70, 20);
      panel.add(user_label);
      
      username = new JTextField();
      username.setBounds(100, 55, 193, 28);
      panel.add(username);
      
      // Password Label
      password_label = new JLabel();
      password_label.setText("Password");
      password_label.setBounds(100, 100, 70, 20);
      panel.add(password_label);
      
      password = new JPasswordField();
      password.setBounds(100, 125, 193, 28);
      panel.add(password);
      
      register = new JLabel("Non hai ancora un account? Registrati!");
      register.setCursor(new Cursor(Cursor.HAND_CURSOR));
      register.setBounds(90, 225, 250, 25);
      register.addMouseListener(new MouseAdapter(){
         public void mouseClicked(MouseEvent me) {
      	     Registrazione registrationFrame = new Registrazione();
         }
      });
      
      panel.add(register);

      // Submit
      submit = new JButton("LOGIN");
      submit.setBounds(155, 180, 75, 35);
      submit.setForeground(Color.BLACK);
      submit.setBackground(new Color(240,128,128));
      submit.addActionListener(this);
      submit.setUI(new StyledButtonUI());
      panel.add(submit);
      

	  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	  double height = screenSize.height/2.7;
      int h = (int) height;
	  double width = screenSize.width/3.75;
      int w = (int) width;
	   
	  this.setLocation( (screenSize.width-w)/2,(screenSize.height-h)/2);
      this.add(panel, BorderLayout.CENTER);
      this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	  this.setSize(w,h);
      this.setTitle("GESTORE DELLE SPESE");
      this.setResizable(false);
	  this.setVisible(true);
   }
   
   public static void main(String[] args) {
      Login loginPage = new Login();
   }
   
   @Override
   public void actionPerformed(ActionEvent ae) {
	   
	   String user = username.getText();
       String pass = password.getText();

       String regex_user = "[a-z0-9_.]{1,20}$";
       Pattern pattern_user = Pattern.compile(regex_user);
       Matcher matcher_user = pattern_user.matcher(user);

       String regex_password = "[a-zA-Z0-9_.!?@#$%^&(){}:;<>,.?/~_+-=|]{8,20}$";
       Pattern pattern_pass = Pattern.compile(regex_password);
       Matcher matcher_password = pattern_pass.matcher(pass);

       if (matcher_user.matches() && matcher_password.matches()) {

            Query q_consult = new Query("consult", new Term[] {new Atom("prolog.pl")});
            if(q_consult.hasSolution()) {

               Query q = new Query("connessione, check_login(\'" + (String) user + "\',\'" + (String) pass + "\',Result), chiusura");
               Map<String, Term>[] result = q.allSolutions();
               String r = result[0].get("Result").toString();

               if (r.equals("1")) {
                   this.dispose();
                   Menu menuPage = new Menu(user);
               } else {
                   JOptionPane.showMessageDialog(null, "Username o Password errate");
               }
           }
       } else JOptionPane.showMessageDialog(null, "Username o Password errate");
	}

}