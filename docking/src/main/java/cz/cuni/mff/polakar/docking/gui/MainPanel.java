package cz.cuni.mff.polakar.docking.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Hlavní panel, který drží strukuturu aplikace.
 * Na jedné straně je vždy JMolPanel pro vizualizaci
 * a na druhé se panely mohou měnit.
 */
public class MainPanel extends JPanel {

    public P2RankPanel p2RankPanel;
    public VinaPanel vinaPanel;
    public JMolPanel jMolPanel;
    public enum Services {Vina, P2Rank};
    public MainPanel(){
        this.setLayout(new GridLayout(1,2));

        p2RankPanel = new P2RankPanel();
        vinaPanel = new VinaPanel();
        jMolPanel = new JMolPanel();

        this.add(p2RankPanel, 0);
        this.add(jMolPanel, 1);
    }

    /**
     * Změní panel, který je zobrazován vpravo
     * @param s Vina/P2Rank
     */
    public void changeUtilPanel(Services s){
        this.remove(0);

        if(s==Services.Vina){this.add(vinaPanel,0);}
        else{this.add(p2RankPanel,0);}

        this.revalidate();
        this.repaint();
    }

    /**
     * Vytovří nový panel na místě starého - smaže všechny vložené data
     * @param s Vina/P2Rank
     */
    public void resetUtilPanel(Services s){
        this.remove(0);

        if(s==Services.Vina){this.add(new VinaPanel(),0);}
        else{this.add(new P2RankPanel(),0);}


        this.revalidate();
        this.repaint();
    }

}
