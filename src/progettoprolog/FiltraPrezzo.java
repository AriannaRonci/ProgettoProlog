package progettoprolog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FiltraPrezzo {

    private JDialog dialogFiltraPrezzo;
    private JPanel rangePanel, filtraPanel;
    public JButton filtraPrezzo;
    private JLabel rangeSliderLabel1, rangeSliderValue1, rangeSliderLabel2, rangeSliderValue2;
    private RangeSlider rangeSlider;

    public FiltraPrezzo(Menu m){

       /* menu = m;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h1 = screenSize.height/3;
        double w1 =  screenSize.width/3.5;
        int w = (int) w1;

        int x = (screenSize.width-w)/2;
        int y = (screenSize.height-h1)/2;

        dialog = new JDialog(frame);
        dialog.setVisible(true);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(new Color(230,250,255));
        dialog.setSize(w,h1);
        dialog.setLocation(x,y);
        dialog.setTitle("Filtra spese per categoria");

        filtraPanel = new JPanel();
        filtraPanel.setBackground(new Color(230,250,255));
        filtraPanel.setBounds(0,200,w,h1/4);

        rangePanel = new JPanel();
        rangePanel.setLayout(new GridBagLayout());
        rangePanel.setBackground(new Color(230,250,255));
        rangePanel.setBounds(0,40,w,h1/4);

        rangeSliderLabel1 = new JLabel();
        rangeSliderValue1 = new JLabel();
        rangeSliderLabel2 = new JLabel();
        rangeSliderValue2 = new JLabel();

        rangeSliderLabel1.setText("Lower value:");
        rangeSliderLabel2.setText("Upper value:");
        rangeSliderValue1.setHorizontalAlignment(JLabel.LEFT);
        rangeSliderValue2.setHorizontalAlignment(JLabel.LEFT);

        rangeSlider = new RangeSlider();
        rangeSlider.setPreferredSize(new Dimension(350, (int) (rangeSlider.getPreferredSize().height*1.5)));
        rangeSlider.setMinimum(0);
        rangeSlider.setMaximum(1000);
        rangeSlider.setValue(1);
        rangeSlider.setUpperValue(1000);
        rangeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RangeSlider slider = (RangeSlider) e.getSource();
                rangeSliderValue1.setText(String.valueOf(slider.getValue()));
                rangeSliderValue2.setText(String.valueOf(slider.getUpperValue()));
            }
        });

        rangePanel.add(rangeSliderLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
        rangePanel.add(rangeSliderValue1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 0), 0, 0));
        rangePanel.add(rangeSliderLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
        rangePanel.add(rangeSliderValue2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 6, 0), 0, 0));
        rangePanel.add(rangeSlider, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));


        filtra = new JButton("Filtra");
        filtra.addActionListener(this);
        filtra.setBounds(25, 420, 160,40);
        filtra.setBackground(new Color(240,128,128));
        filtra.setUI(new StyledButtonUI());

        filtraPanel.add(filtra);
        dialog.add(filtraPanel);
        dialog.add(rangePanel);
    }

    public String getMinPrezzo() {
        return String.valueOf(rangeSlider.getValue());
    }

    public String getMaxPrezzo() {
        return String.valueOf(rangeSlider.getUpperValue());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.dispose();
        menu.minPrezzo  = getMinPrezzo();
        menu.maxPrezzo = getMaxPrezzo();*/
    }

}
