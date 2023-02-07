package progettoprolog;

import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Menu extends JFrame implements ActionListener {

    private JPanel buttonpanel, tablepanel;
    private DefaultTableModel defaultTableModel;
    private JTable table;
    private JScrollPane scrollPane;
    private JButton elimina_button, inserisci_button, stats_button, visualizza_button, logout_button, reset_button,
            flitra_categoria_button, flitra_prezzo_button, flitra_date_button;
    private Image icon_add, icon_stats, icon_logout, icon_visualizza, icon_elimina;
    private String userlogin;
    private ArrayList<Integer> id;
    public Menu (String user){

        userlogin = user;
        buttonpanel = new JPanel();
        tablepanel = new JPanel();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h = (int) (screenSize.height/1.6);
        int w = (int) (screenSize.width/1.8);

        {
            try {
                icon_add = ImageIO.read(getClass().getResource("shopping-cart.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        {
            try {
                icon_stats = ImageIO.read(getClass().getResource("pie-chart.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        {
            try {
                icon_logout = ImageIO.read(getClass().getResource("logout.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        {
            try {
                icon_visualizza = ImageIO.read(getClass().getResource("search.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        {
            try {
                icon_elimina = ImageIO.read(getClass().getResource("delete.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        icon_add = icon_add.getScaledInstance(55, 55,  java.awt.Image.SCALE_SMOOTH);
        icon_stats = icon_stats.getScaledInstance(52, 52,  java.awt.Image.SCALE_SMOOTH);
        icon_visualizza = icon_visualizza.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
        icon_elimina = icon_elimina.getScaledInstance(52, 52,  java.awt.Image.SCALE_SMOOTH);
        icon_logout = icon_logout.getScaledInstance(25, 25,  java.awt.Image.SCALE_SMOOTH);

        inserisci_button = new JButton(new ImageIcon(icon_add));
        inserisci_button.setToolTipText("Inserisci spesa");

        stats_button = new JButton(new ImageIcon(icon_stats));
        stats_button.setToolTipText("Statistiche");

        visualizza_button = new JButton(new ImageIcon(icon_visualizza));
        visualizza_button.setToolTipText("Visualizza spesa");

        elimina_button = new JButton(new ImageIcon(icon_elimina));
        elimina_button.setToolTipText("Elimina spesa");

        logout_button = new JButton(new ImageIcon(icon_logout));
        logout_button.setToolTipText("Elimina spesa");


        logout_button.setBounds(5,5,30,30);
        logout_button.addActionListener(this);
        logout_button.setUI(new StyledButtonUI());

        inserisci_button.setBounds(25,100,65,65);
        inserisci_button.addActionListener(this);
        inserisci_button.setUI(new StyledButtonUI());

        stats_button.setBounds(25, 185, 65,65);
        stats_button.addActionListener(this);
        stats_button.setUI(new StyledButtonUI());

        elimina_button.setBounds(110, 185, 65,65);
        elimina_button.addActionListener(this);
        elimina_button.setUI(new StyledButtonUI());

        visualizza_button.setBounds(110, 100, 65,65);
        visualizza_button.addActionListener(this);
        visualizza_button.setUI(new StyledButtonUI());

        flitra_categoria_button = new JButton("Filtra per categoria");
        flitra_categoria_button.setBounds(25, 320, 160,40);
        flitra_categoria_button.setBackground(new Color(240,128,128));
        flitra_categoria_button.addActionListener(this);
        flitra_categoria_button.setUI(new StyledButtonUI());

        flitra_prezzo_button = new JButton("Filtra per prezzo");
        flitra_prezzo_button.setBounds(25, 370, 160,40);
        flitra_prezzo_button.setBackground(new Color(240,128,128));
        flitra_prezzo_button.addActionListener(this);
        flitra_prezzo_button.setUI(new StyledButtonUI());

        flitra_date_button = new JButton("Filtra spese future");
        flitra_date_button.setBounds(25, 420, 160,40);
        flitra_date_button.setBackground(new Color(240,128,128));
        flitra_date_button.addActionListener(this);
        flitra_date_button.setUI(new StyledButtonUI());

        reset_button = new JButton("Reset filtro");
        reset_button.setBounds(25, 420, 160,40);
        reset_button.setBackground(new Color(240,128,128));
        reset_button.addActionListener(this);
        reset_button.setVisible(false);
        reset_button.setUI(new StyledButtonUI());

        buttonpanel.setLayout(null);
        buttonpanel.setBackground(new Color(150,200,240));
        buttonpanel.setBounds(0,0,200,1000);
        buttonpanel.add(inserisci_button);
        buttonpanel.add(stats_button);
        buttonpanel.add(elimina_button);
        buttonpanel.add(visualizza_button);
        buttonpanel.add(logout_button);
        buttonpanel.add(flitra_categoria_button);
        buttonpanel.add(flitra_prezzo_button);
        buttonpanel.add(flitra_date_button);
        buttonpanel.add(reset_button);
        this.add(buttonpanel);


        String [] campi = {"", "descrizione", "costo", "categoria", "data"};

        defaultTableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (int i = 0; i < campi.length; i++)
            defaultTableModel.addColumn(campi[i]);

        tablepanel.setLayout(null);
        tablepanel.setBackground(new Color(230,250,255));
        tablepanel.setBounds(200,0,w-200,h);

        table = new JTable(defaultTableModel);
        table.getTableHeader().setBackground(new Color(255,180,180));
        table.getTableHeader().setFont(new Font("Helvetica", Font.BOLD,12));
        table.setOpaque(true);
        table.getTableHeader().setForeground(new Color(255,255,255));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth((int) ((w-220)*0.05));
        columnModel.getColumn(1).setPreferredWidth((int) ((w-220)*0.55));
        columnModel.getColumn(2).setPreferredWidth((int) ((w-220)*0.1));
        columnModel.getColumn(3).setPreferredWidth((int) ((w-220)*0.15));
        columnModel.getColumn(4).setPreferredWidth((int) ((w-220)*0.15));


        scrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tablepanel.add(scrollPane);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setPreferredSize(new Dimension(w-220,h));
        this.getSpese(userlogin);

        this.add(tablepanel);

        this.setSize(w,h);
        this.setLocation((screenSize.width-w)/2, (screenSize.height-h)/2);
        this.setTitle("Le mie spese");
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    private void getSpese(String userlogin) {

        defaultTableModel.setRowCount(0);

        Query q_consult = new Query("consult", new Term[] {new Atom("prolog.pl")});
        if(q_consult.hasSolution()) {

            Query q = new Query("connessione, spese_di_utente(Result,\'" + userlogin + "\'), chiusura");
            Map<String, Term>[] result = q.allSolutions();
            String r = result[0].get("Result").toString();

            if (!r.equals("[]")) {
                String[] spese = r.split("],");

                id = new ArrayList<Integer>(spese.length);

                for (int i = 0; i < spese.length; i++) {
                    String[] spesa = spese[i].split(", ");
                    String descrizione = spesa[2].replace("\'", "");
                    String costo = spesa[3].replace("\'", "") + "€";
                    String categoria = spesa[4].replace("\'", "");
                    String data = spesa[5].replace("date(", "") + "-" + spesa[6] + "-" +
                            spesa[7].replace(")", "").replace("]]", "");
                    String[] elemento = {String.valueOf(i + 1), descrizione, costo, categoria, data};
                    defaultTableModel.addRow(elemento);

                    String identificatore = spesa[0].replace("[", "").replace(" ", "");

                    id.add(Integer.valueOf(identificatore));
                }
            }
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h = (int) (screenSize.height/1.6);
        int w = (int) (screenSize.width/1.8);

        if(16*table.getRowCount()+23 < h)
            scrollPane.setBounds(5, 5, w-225, 16*table.getRowCount()+23);
        else
            scrollPane.setBounds(5, 5, w-225, h-45);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==logout_button){
            this.dispose();
            JOptionPane.showMessageDialog(null, "Arrivederci "+userlogin+",\nci vediamo alla prossima spesa!");
            Login loginPage = new Login();
        }

        if(e.getSource()==inserisci_button){
            Inserisci_Spesa inserimentoPage = new Inserisci_Spesa(this,userlogin);
        }

        if(e.getSource()==stats_button){
            Statistiche statistichePage;
            if(table.getRowCount()>0)
                statistichePage = new Statistiche(this, userlogin);
            else JOptionPane.showMessageDialog(null, "Non hai ancora nessuna spesa. \n" +
                    "Inizia ad inserire le tue spese e poi potrai consultare le statistiche ad esse relative.");
        }

        if(e.getSource()==elimina_button) {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Seleziona una riga");
            }
            if (table.getSelectedRow() != -1) {

                int reply = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler eliminare questa spesa?",
                        "Elimina spesa", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {

                    String riga = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
                    int i = Integer.parseInt(riga);

                    Query q_consult = new Query("consult('prolog.pl')");
                    if (q_consult.hasSolution()) {
                        Query q = new Query("connessione, delete_spesa(" + (int) id.get(i - 1) + ",Result), chiusura");
                        Map<String, Term>[] result = q.allSolutions();
                        String r = result[0].get("Result").toString();

                        if (r.equals("1")) {
                            defaultTableModel.removeRow(table.getSelectedRow());
                            JOptionPane.showMessageDialog(null, "Eliminazione avvenuta con successo");
                            this.getSpese(userlogin);
                        } else if (r.equals("0")) {
                            JOptionPane.showMessageDialog(null, "Nessuna spesa eliminata");
                        }

                        flitra_prezzo_button.setVisible(true);
                        flitra_categoria_button.setVisible(true);
                        flitra_date_button.setVisible(true);
                        reset_button.setVisible(false);
                    }
                }
            }
        }

        if(e.getSource()==visualizza_button) {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Seleziona una riga");
            }

            if(table.getSelectedRow() != -1) {
                String descrizione = (String) table.getModel().getValueAt(table.getSelectedRow(),1);

                StringBuilder sb = new StringBuilder(descrizione);
                int i = 0;
                while ((i = sb.indexOf(" ", i + 70)) != -1) {
                    sb.replace(i, i + 1, "\n                       ");
                    i = i+70;
                }

                JOptionPane.showMessageDialog(null, "Descrizione: " + sb + '\n' +
                        "Costo: " + table.getModel().getValueAt(table.getSelectedRow(),2) + '\n' +
                        "Categoria: " + table.getModel().getValueAt(table.getSelectedRow(),3) + '\n' +
                        "Data: " + table.getModel().getValueAt(table.getSelectedRow(),4),
                        "Spesa",1);
            }
        }

        if(e.getSource()==flitra_date_button) {

            defaultTableModel.setRowCount(0);

            Query q_consult = new Query("consult", new Term[] {new Atom("prolog.pl")});
            if(q_consult.hasSolution()) {

                Query q = new Query("connessione, filtra_spese_future(SpeseFuture,\'" + userlogin + "\'), " +
                        "filtra_spese_inscadenza(SpeseInScadenza,\'" + userlogin + "\'), chiusura");
                Map<String, Term>[] result = q.allSolutions();
                String spese_future = result[0].get("SpeseFuture").toString();
                String spese_in_scadenza = result[0].get("SpeseInScadenza").toString();

                if (!spese_future.equals("[]")) {
                    String[] spese = spese_future.split("],");

                    id = new ArrayList<Integer>(spese.length);

                    for (int i = 0; i < spese.length; i++) {
                        String[] spesa = spese[i].split(", ");
                        String descrizione = spesa[2].replace("\'", "");
                        String costo = spesa[3].replace("\'", "") + "€";
                        String categoria = spesa[4].replace("\'", "");
                        String data = spesa[5].replace("date(", "") + "-" + spesa[6] + "-" +
                                spesa[7].replace(")", "").replace("]]", "");
                        String[] elemento = {String.valueOf(i + 1), descrizione, costo, categoria, data};
                        defaultTableModel.addRow(elemento);

                        String identificatore = spesa[0].replace("[", "").replace(" ", "");

                        id.add(Integer.valueOf(identificatore));
                    }
                }
            }

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int h = (int) (screenSize.height/1.6);
            int w = (int) (screenSize.width/1.8);

            if(16*table.getRowCount()+23 < h)
                scrollPane.setBounds(5, 5, w-225, 16*table.getRowCount()+23);
            else
                scrollPane.setBounds(5, 5, w-225, h-45);

            flitra_prezzo_button.setVisible(false);
            flitra_categoria_button.setVisible(false);
            flitra_date_button.setVisible(false);
            reset_button.setVisible(true);
        }

        if(e.getSource()==reset_button){
            this.getSpese(userlogin);
            flitra_prezzo_button.setVisible(true);
            flitra_categoria_button.setVisible(true);
            flitra_date_button.setVisible(true);
            reset_button.setVisible(false);
        }
    }
}
