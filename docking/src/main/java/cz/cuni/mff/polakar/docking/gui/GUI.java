package cz.cuni.mff.polakar.docking.gui;

import cz.cuni.mff.polakar.docking.controls.OpenBabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Tvoří hlavní část GUI <br>
 * - MenuBar a hlavní panel, kde jsou zobrazovány další komponenty
 */
public class GUI extends JFrame {
    static Container container;
    static MainPanel mainPanel;
    public GUI(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        container = this.getContentPane();
        this.setLayout(new BorderLayout());

        this.setTitle("Docking application");

        container.add(createMenuBar(),BorderLayout.NORTH);
        mainPanel = new MainPanel();
        container.add(mainPanel, BorderLayout.CENTER);

        this.pack();
        this.setSize(1080,720);
        this.setVisible(true);
    }

    static JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(addP2RankMenu());
        menuBar.add(addVinaMenu());
        menuBar.add(addJmolMenu());
        menuBar.add(addObabelMenu());

        return menuBar;
    }

    private static JMenu addVinaMenu(){
        JMenu vinaMenu = new JMenu("AutoDock Vina");

        JMenuItem openVinaItem = new JMenuItem(new AbstractAction("Open Vina panel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.changeUtilPanel(MainPanel.Services.Vina);
            }
        });
        // načte výsledky z P2RANK - jednotlivé pockety a vyplní center
        JMenuItem loadPrankItem = new JMenuItem(new AbstractAction("Load from P2Rank") {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooser predictionFile =  new FileChooser();
                predictionFile.addFileExt("csv");
                predictionFile.setMultiSelect(false);


                int result = JOptionPane.showConfirmDialog(container,predictionFile,
                        "Choose a CSV file with predictions from P2Rank",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result ==  JOptionPane.OK_OPTION){
                    mainPanel.vinaPanel.loadFromP2Rank(predictionFile.getFile(),
                            mainPanel.jMolPanel.getStructure(),
                            mainPanel.jMolPanel.getStructureFilePath());
                }

            }
        });

        JMenuItem loadPrankDirItem = new JMenuItem(new AbstractAction("Load from P2Rank directory") {
            @Override
            public void actionPerformed(ActionEvent e) {
                DirChooser p2rankDir = new DirChooser();
                int result = JOptionPane.showConfirmDialog(container,p2rankDir,
                        "Choose directory with predictions from P2Rank",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result ==  JOptionPane.OK_OPTION){
                    mainPanel.vinaPanel.loadFromP2Rank(p2rankDir.getSelectedFile());
                }
            }
        });
        JMenuItem computeSizeItem = new JMenuItem(new AbstractAction("Compute search size") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        });
        JMenuItem resetItem = new JMenuItem(new AbstractAction("Reset panel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.resetUtilPanel(MainPanel.Services.Vina);
            }
        });

        JMenuItem addPDBfileItem = new JMenuItem(new AbstractAction("Add PDB file (convert to PDBQT)") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(2,1,5,5));

                FileChooser fc = new FileChooser();
                fc.setMultiSelect(false);
                fc.addFileExt("pdb");
                fc.addFileExt("cif");
                fc.addFileExt("mmcif");
                fc.setBorder(BorderFactory.createTitledBorder("File to convert"));

                DirChooser outDir = new DirChooser();
                outDir.setBorder(BorderFactory.createTitledBorder("Output directory"));

                panel.add(fc);
                panel.add(outDir);

                int result = JOptionPane.showConfirmDialog(container,panel,
                        "Select file to be converted into PDBQT",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if(result == JOptionPane.OK_OPTION){
                    OpenBabel openBabel = new OpenBabel();
                    //int exitCode = openBabel.convertToPDBQT(fc.getFile(), outDir.getText());
                    openBabel.convertToPDBQT(fc.getFile(), outDir.getText());
                    int exitCode = 0; // Debug
                    if(exitCode == 0){
                        String newPDBQT = outDir.getText() + "/" + fc.getFile().getName() + ".pdbqt";
                        if(new File(newPDBQT).exists()){
                            System.out.println("NEW FILE EXISTS: " + newPDBQT);
                        }
                        mainPanel.vinaPanel.setReceptorFileField(newPDBQT);
                    }
                }
            }
        });

        vinaMenu.add(openVinaItem);
        vinaMenu.add(loadPrankItem);
        vinaMenu.add(loadPrankDirItem);
        //DELETE vinaMenu.add(computeSizeItem); ?
        vinaMenu.add(resetItem);
        vinaMenu.add(addPDBfileItem);

        return vinaMenu;
    }

    private static JMenu addP2RankMenu(){
        JMenu p2rankMenu = new JMenu("P2Rank");

        JMenuItem openP2RankItem = new JMenuItem(new AbstractAction("Open P2Rank panel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.changeUtilPanel(MainPanel.Services.P2Rank);
            }
        });
        JMenuItem resetItem = new JMenuItem(new AbstractAction("Reset panel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainPanel.resetUtilPanel(MainPanel.Services.P2Rank);
            }
        });


        p2rankMenu.add(openP2RankItem);
        p2rankMenu.add(resetItem);

        return p2rankMenu;
    }

    private static JMenu addJmolMenu(){
        JMenu jmolMenu = new JMenu("Jmol");

        JMenuItem loadP2RankItem = new JMenuItem(new AbstractAction("Load P2Rank predictions") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel();
                panel.setLayout(new FlowLayout());

                FileChooser pdbFile = new FileChooser();
                pdbFile.addFileExt("pdb");
                pdbFile.addFileExt("cif");
                pdbFile.addFileExt("mmcif");
                pdbFile.setMultiSelect(false);
                pdbFile.setBorder(BorderFactory.createTitledBorder("Select structure file"));

                FileChooser predictionFile =  new FileChooser();
                predictionFile.addFileExt("csv");
                predictionFile.setMultiSelect(false);
                predictionFile.setBorder(BorderFactory.createTitledBorder("Select prediction file"));

                panel.add(predictionFile);
                panel.add(pdbFile);

                int result = JOptionPane.showConfirmDialog(container,panel,
                        "Choose a CSV file with predictions from P2Rank",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result ==  JOptionPane.OK_OPTION){
                    mainPanel.jMolPanel.loadPocketPrediction(predictionFile.getFile(), pdbFile.getFile());
                    // DEBUG mainPanel.jMolPanel.loadPocketPrediction("C:\\Users\\marti\\Desktop\\prank_test\\2\\7rdy.pdb_predictions.csv", new File("C:\\Users\\marti\\Desktop\\prank_test\\struc\\7rdy.pdb"));
                }


            }
        });

        JMenuItem loadP2RankDirItem = new JMenuItem(new AbstractAction("Load P2Rank prediction from dir") {
            @Override
            public void actionPerformed(ActionEvent e) {
                DirChooser p2rankDir = new DirChooser();
                int result = JOptionPane.showConfirmDialog(container,p2rankDir,
                        "Choose directory with P2Rank output",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result ==  JOptionPane.OK_OPTION){
                    mainPanel.jMolPanel.loadPredictionFromDir(p2rankDir.getSelectedFile());
                }
            }
        });


        jmolMenu.add(loadP2RankItem);
        jmolMenu.add(loadP2RankDirItem);

        return jmolMenu;
    }

    private static JMenu addObabelMenu(){
       JMenu obabelMenu = new JMenu("Obabel");


       JMenuItem convertItem = new JMenuItem(new AbstractAction("Covert files") {
           @Override
           public void actionPerformed(ActionEvent e) {
               JPanel panel = new JPanel();
               panel.setLayout(new GridLayout(3,1,5,5));
               FileChooser fileToConvert = new FileChooser();
               fileToConvert.setMultiSelect(false);
               fileToConvert.setAcceptAllFiles(true);
               fileToConvert.setBorder(BorderFactory.createTitledBorder("File to convert"));

               JTextField newFileName = new JTextField();
               newFileName.setBorder(BorderFactory.createTitledBorder("New file name with new extension"));
               DirChooser finalDir = new DirChooser();
               finalDir.setBorder(BorderFactory.createTitledBorder("Directory of the new file"));

               panel.add(fileToConvert);
               panel.add(newFileName);
               panel.add(finalDir);


               int result = JOptionPane.showConfirmDialog(container,panel,"Choose file to convert",
                       JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

               if(result == JOptionPane.OK_OPTION){
                   if(newFileName.getText().isEmpty() || fileToConvert.getFile().getAbsolutePath().isEmpty() || finalDir.getText().isEmpty()){
                       JOptionPane.showMessageDialog(new JFrame(),"Options not filled out","Input error",JOptionPane.ERROR_MESSAGE);
                       return;
                   }

                   OpenBabel.convert(fileToConvert.getFile().getAbsolutePath(),finalDir.getText() + newFileName.getText());
               }
           }
       });

       obabelMenu.add(convertItem);

       return obabelMenu;
    }


}
