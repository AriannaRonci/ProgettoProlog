package progettoprolog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;


public class Statistiche{

    private DefaultPieDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private JDialog dialog;
    private String userlogin, spese;
    public Statistiche(JFrame frame, String user, String spese) {

        this.userlogin = user;
        this.spese = spese;
        dataset = createDataset(spese);
        chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);

        dialog = new JDialog(frame);
        dialog.setVisible(true);
        dialog.setLayout(null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double h1 = screenSize.height/1.8;
        int h = (int) h1;
        int w =  screenSize.width/2;

        int x = (screenSize.width-w)/2;
        int y = (screenSize.height-h)/2;

        dialog.setSize(w,h);
        dialog.setLocation(x,y);
        dialog.setContentPane(chartPanel);
        dialog.setTitle("Spese per categoria");
    }


    private DefaultPieDataset createDataset(String r) {

        DefaultPieDataset dataset = new DefaultPieDataset();

        /*Query q_consult = new Query("consult", new Term[] {new Atom("prolog.pl")});
        if(q_consult.hasSolution()) {

            Query q = new Query("connessione, spese_di_utente(L," + userlogin + "), somma_totale_per_cat(L,Result),chiusura");
            Map<String, Term>[] result = q.allSolutions();
            String r = "[]";
            if (result.length > 0)
                r = result[0].get("Result").toString();*/

            if (!r.equals("[]")) {
                String[] spese_per_cat = r.split("],");

                for (int i = 0; i < spese_per_cat.length; i++) {
                    String[] singola_categoria = spese_per_cat[i].split(", ");
                    String categoria = singola_categoria[0].replace("[", "");
                    String spesa = singola_categoria[1].replace("]", "");
                    dataset.setValue(categoria, Double.parseDouble(spesa));
                }
            }
        //}
        return dataset;
    }

    private JFreeChart createChart(DefaultPieDataset dataset) {


        JFreeChart pieChart = ChartFactory.createPieChart(
                "Spese per categoria",
                dataset,
                true, true, false);

        return pieChart;
    }
}