package progettoprolog;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Inserisci_Spesa implements ActionListener {

    private JFrame frame;
    private JPanel panel;
    protected JLabel descrizione_label, costo_label, categoria_label, data_label, label;
    protected JTextArea descrizione;
    private JTextField costo;
    private JComboBox categoria;
    private JButton inserisci;
    private JLabel desc_error, costo_error, data_error;
    private UtilDateModel model;
    private Properties p;
    private JDatePanelImpl datePanel;
    private JDatePickerImpl datePicker;
    private JDialog dialog;

    private String userlogin;


    public Inserisci_Spesa(JFrame frame, String user) {

        this.frame = frame;
        userlogin = user;

        dialog = new JDialog(frame);
        dialog.setVisible(true);
        dialog.setLayout(null);

        panel = new JPanel(new BorderLayout());
        panel.setLayout(null);
        panel.setBackground(new Color(230,250,255));


        // Descrizione
        descrizione_label = new JLabel();
        descrizione_label.setText("Descrizione");
        descrizione_label.setBounds(30, 25, 100, 28);
        panel.add(descrizione_label);

        descrizione = new JTextArea();
        descrizione.setBounds(30, 50, 380, 90);
        descrizione.setLineWrap(true);
        descrizione.setWrapStyleWord(true);

        panel.add(descrizione);

        label = new JLabel();
        label.setText("0/250");
        label.setBounds(380, 140, 75, 28);
        label.setForeground(Color.gray);
        panel.add(label);

        int max_chrs = 250;

        descrizione.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
                if(descrizione.getText().length() >= max_chrs){
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                label.setText( Integer.toString(descrizione.getText().length()) +"/250");
            }
        });

        desc_error = new JLabel();
        desc_error.setText("");
        desc_error.setForeground(Color.RED);
        desc_error.setBounds(30, 140, 350, 28);
        panel.add(desc_error);


        // Costo
        costo_label = new JLabel();
        costo_label.setText("Costo");
        costo_label.setBounds(140, 170, 70, 28);
        panel.add(costo_label);

        costo = new JTextField();
        costo.setBounds(140, 195, 170, 28);
        panel.add(costo);

        costo_error = new JLabel();
        costo_error.setText("");
        costo_error.setForeground(Color.RED);
        costo_error.setBounds(140, 220, 250, 28);
        panel.add(costo_error);


        // Categoria
        categoria_label = new JLabel();
        categoria_label.setText("Categoria");
        categoria_label.setBounds(140, 250, 70, 28);
        panel.add(categoria_label);

        String[] choices = {"alimentari","svago","abbigliamento","utenze","cura della persona","casa","viaggi","trasporti","sport","cultura","altro"};
        categoria = new JComboBox(choices);
        categoria.setBounds(140, 275, 170, 28);
        panel.add(categoria);


        // Data
        data_label = new JLabel();
        data_label.setText("Data");
        data_label.setBounds(140, 330, 70, 28);
        panel.add(data_label);

        //Create the date picker
        model = new UtilDateModel();
        p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        datePanel = new JDatePanelImpl(model, p);
        model.setSelected(true);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setBounds(140, 355, 170, 28);
        panel.add(datePicker);

        data_error = new JLabel();
        data_error.setText("");
        data_error.setForeground(Color.RED);
        data_error.setBounds(140, 380, 250, 28);
        panel.add(data_error);


        // Button
        inserisci = new JButton("Inserisci");
        inserisci.setBounds(170, 425, 100, 35);
        inserisci.setForeground(Color.BLACK);
        inserisci.setBackground(new Color(240,128,128));
        inserisci.addActionListener(this);
        inserisci.setUI(new StyledButtonUI());
        panel.add(inserisci);


        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double h1 = screenSize.height/1.62;
        int h = (int) h1;
        int w = (int) (screenSize.width/3.4);

        int x = (screenSize.width-w)/2;
        int y = (screenSize.height-h)/2;

        dialog.setContentPane(panel);
        dialog.setSize(w,h);
        dialog.setResizable(false);
        dialog.setLocation(x,y);
        dialog.setTitle("Inserisci una nuova spesa");
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String desc = "\'" +descrizione.getText().stripTrailing() + "\'";
        String cat = "\'" +categoria.getSelectedItem().toString()+"\'";
        String cost = costo.getText();
        String dat = "\'" + datePicker.getJFormattedTextField().getText() + "\'";

        //regex
        String cost_regex = "^[0-9]{1,6}+[.]{0,1}+[0-9]{0,2}$";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(cost_regex);
        matcher = pattern.matcher(cost);

        Boolean check_descrizione = true;
        Boolean check_costo = true;
        Boolean check_data = true;

        if (desc.equals("\'\'")) {
            desc_error.setText("");
            desc_error.setText("Il campo descrizione non può essere vuoto");
            check_descrizione = false;
        }
        else if(Character.isDigit(desc.charAt(0))) {
            desc_error.setText("");
            desc_error.setText("La descrizione non può iniziare con un numero");
            check_descrizione = false;
        }
        else if(desc.length()>=253){
            desc_error.setText("");
            desc_error.setText("La descrizione è troppo lunga");
            check_descrizione = false;
        }
        else{
            desc_error.setText("");
            check_descrizione = true;
        }

        if (costo.getText().equals("")) {
            costo_error.setText("Il campo costo non può essere vuoto");
            check_costo = false;
        }
        else if(!matcher.matches()){
            costo_error.setText("");
            costo_error.setText("Il costo inserito non è valido");
            check_costo = false;
        }
        else if(!Character.isDigit(cost.charAt(cost.length()-1))) {
            costo_error.setText("");
            costo_error.setText("Il costo inserito non è valido");
            check_costo = false;
        }
        else {
            costo_error.setText("");
            check_costo = true;
        }

        if(dat.equals("\'\'")){
            data_error.setText("Il campo data non può essere vuoto");
            check_data = false;
        }
        else{
            check_data = true;
        }

        if(check_descrizione && check_costo && check_data){

            Query q_consult = new Query("consult", new Term[] {new Atom("prolog.pl")});
            if(q_consult.hasSolution()) {

                Query q = new Query("connessione, insert_spesa(" + userlogin + "," + (String) desc.replace(",", "") + ",'" + (String) cost + "'," + (String) cat + "," + (String) dat + ",Result), chiusura");
                Map<String, Term>[] result = q.allSolutions();
                String r = result[0].get("Result").toString();

                if (r.equals("1")) {
                    this.frame.dispose();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(null, "Ci dispiace ma non è stato possibile inserire la spesa.");
                    Menu menuPage = new Menu(userlogin);
                } else {
                    this.frame.dispose();
                    dialog.dispose();
                    Menu menuPage = new Menu(userlogin);
                }
            }
        }
    }
}
