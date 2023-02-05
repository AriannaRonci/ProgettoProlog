package progettoprolog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

public class Registrazione extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JLabel user_label, password_label, checkpassword_label, user_error, user_error2, password_error, password_error2, checkpassword_error;
	private JTextField username;
	private JPasswordField password, checkpassword;
	private JButton submit, reset, back;
	private Image icon_back, resizedImage;
		   	
	public Registrazione(){

		panel = new JPanel(new BorderLayout());
		panel.setLayout(null);
		panel.setBackground(new Color(230,250,255));

		{
			try {
				icon_back = ImageIO.read(getClass().getResource("back.png"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		resizedImage = icon_back.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
		back = new JButton(new ImageIcon(resizedImage));
		back.setBounds(5,5,30,30);
		back.addActionListener(this);
		back.setUI(new StyledButtonUI());
		panel.add(back);

		// Username
		user_label = new JLabel();
		user_label.setText("Username");
		user_label.setBounds(100, 40, 70, 20);
		panel.add(user_label);
	      
		username = new JTextField();
		username.setBounds(100, 60, 193, 28);
		panel.add(username);

		user_error = new JLabel();
		user_error.setText("");
		user_error.setForeground(Color.RED);
		user_error.setBounds(80, 90, 250, 15);
		panel.add(user_error);
		user_error2 = new JLabel();
		user_error2.setText("");
		user_error2.setForeground(Color.RED);
		user_error2.setBounds(65, 105, 300, 15);
		panel.add(user_error2);
      
	      
		// Password
		password_label = new JLabel();
		password_label.setText("Password");
		password_label.setBounds(100, 120, 70, 20);
		panel.add(password_label);

		password = new JPasswordField();
		password.setBounds(100, 145, 193, 28);
		panel.add(password);

		password_error = new JLabel();
		password_error.setText("");
		password_error.setForeground(Color.RED);
		password_error.setBounds(80, 170, 260, 15);
		panel.add(password_error);
		password_error2 = new JLabel();
		password_error2.setText("");
		password_error2.setForeground(Color.RED);
		password_error2.setBounds(140, 185, 200, 15);
		panel.add(password_error2);
	      
	      
		// Check Password Label
		checkpassword_label = new JLabel();
		checkpassword_label.setText("Conferma Password");
		checkpassword_label.setBounds(100, 205, 150, 20);
		panel.add(checkpassword_label);

		checkpassword = new JPasswordField();
		checkpassword.setBounds(100, 235, 193, 28);
		panel.add(checkpassword);

		checkpassword_error = new JLabel();
		checkpassword_error.setText("");
		checkpassword_error.setForeground(Color.RED);
		checkpassword_error.setBounds(100, 260, 250, 20);
		panel.add(checkpassword_error);
	      

		// Submit
		submit = new JButton("Registrati");
		submit.setBounds(85, 310, 105, 35);
		submit.setForeground(Color.BLACK);
		submit.setBackground(new Color(240,128,128));
		submit.addActionListener(this);
		submit.setUI(new StyledButtonUI());
		panel.add(submit);


		//Reset
		reset = new JButton("Reset");
		reset.setBounds(210, 310, 100, 35);
		reset.setForeground(Color.BLACK);
		reset.setBackground(new Color(240,128,128));
		reset.addActionListener(this);
		reset.setUI(new StyledButtonUI());
		panel.add(reset);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double height = screenSize.height/2;
		int h = (int) height;
		double width = screenSize.width/3.6;
		int w = (int) width;

		this.setLocation( (screenSize.width-w)/2,(screenSize.height-h)/2);
	      
		this.add(panel, BorderLayout.CENTER);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(w,h);
		this.setTitle("Registrazione");
		this.setResizable(false);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource()==back){
			this.dispose();
			Login loginPage = new Login();
		}

		 String user = username.getText();
		 String pass = password.getText();
		 String checkpass = checkpassword.getText();

		String regex_user = "[a-z0-9_.]{1,20}$";
		Pattern pattern_user = Pattern.compile(regex_user);

		Matcher matcher_user = pattern_user.matcher(user);

		String regex_password = "[a-zA-Z0-9_.!?@#$%^&(){}:;<>,.?/~_+-=|]{8,20}$";
		Pattern pattern_pass = Pattern.compile(regex_password);

		Matcher matcher_password = pattern_pass.matcher(pass);

		if(user.equals(""))
			user_error.setText("Il campo Username non può essere vuoto.");
		else if(!matcher_user.matches()) {
			user_error.setText("L'username inserito non rispetta il pattern.");
			user_error2.setText("(max 20 caratteri, no maiuscole e caratteri speciali)");
		} else {
			user_error.setText("");
			user_error2.setText("");
		}

		if(pass.equals("")) {
			password_error.setText("Il campo Password non può essere vuoto.");
			password_error2.setText("");
			checkpassword.setText("");
		}else if(!matcher_password.matches()) {
			password_error.setText("La password inserita non rispetta il pattern.");
			password_error2.setText("(tra 8 e 20 caratteri)");
			password.setText("");
			checkpassword.setText("");
		} else if (checkpass.equals("")) {
			checkpassword_error.setText("Il campo Password non può essere vuoto.");
			password_error.setText("");
			password_error2.setText("");
		} else {
			password_error.setText("");
			password_error2.setText("");
			checkpassword_error.setText("");
		}

		if (!(user.equals("") || pass.equals("") || checkpass.equals("")) && e.getSource() == submit && matcher_user.matches() && matcher_password.matches()) {

			Query q_consult = new Query("consult", new Term[]{new Atom("prolog.pl")});
			if (q_consult.hasSolution()) {

				Query q = new Query("connessione, check_registration(\'" + (String) user + "\',\'" + (String) pass + "\',\'" + (String) checkpass
						+ "\',Result), chiusura");
				Map<String, Term>[] result = q.allSolutions();
				String r = result[0].get("Result").toString();

				switch (r) {
					case "0":
						JOptionPane.showMessageDialog(null, "Registrazione avvenuta con successo!");
						this.dispose();
						Login loginFrame = new Login();
						break;
					case "1":
						user_error.setText("      Username già in uso.");
						break;
					case "2":
						checkpassword_error.setText("Password non coincidenti.");
						checkpassword.setText("");
						break;
					default:
						break;
				};
			}

			if (e.getSource() == reset) {
				username.setText("");
				password.setText("");
				checkpassword.setText("");
			}
		}
	}
}
