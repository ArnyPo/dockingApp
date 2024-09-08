package cz.cuni.mff.polakar.docking.gui;

import cz.cuni.mff.polakar.docking.controls.P2RankPrediction;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.align.gui.jmol.JmolPanel;
import org.biojava.nbio.structure.io.CifFileReader;
import org.biojava.nbio.structure.io.FileParsingParameters;
import org.biojava.nbio.structure.io.PDBFileParser;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.jmol.adapter.readers.cif.CifReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.stream.Stream;

/**
 * JPanel komponenta pro zobrazení JMol bizualiace a její ovládání
 */
public class JMolPanel extends JPanel {
    private JTextField jmolCommandField;
    private JmolPanel jmolPanel;
    private Structure structure;
    private String structureFilePath;
    public JMolPanel(){
        this.setLayout(new BorderLayout());

        jmolPanel = new JmolPanel();

        try {
            jmolPanel.setStructure(StructureIO.getStructure("4hhb"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new GridLayout(2,1));

        jmolCommandField = new JTextField("Jmol Commands");
        jmolCommandField.addActionListener(new JmolCommandAction());
        commandPanel.add(jmolCommandField);

        JButton jmolExecuteButton = new JButton("Execute");
        jmolExecuteButton.addActionListener(new JmolCommandAction());
        commandPanel.add(jmolExecuteButton);


        this.add(jmolPanel,BorderLayout.CENTER);
        this.add(commandPanel,BorderLayout.SOUTH);
    }

    /**
     * Spustí JMol příkaz
     */
    private class JmolCommandAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            jmolPanel.evalString(jmolCommandField.getText());
            jmolCommandField.setText("");
        }
    }

    /**
     * Nastaví strukturu k zobrazení ze souboru
     * @param file soubor se strukturou (pdb/cif/mmcif)
     * @return struktura
     */
    public static Structure getStructureFromFile(File file) {
        Structure structure = null;
        try {
            // Determine the file extension to decide on the parser
            String fileExtension = getFileExtension(file);

            FileParsingParameters params = new FileParsingParameters();
            params.setDefault();
            params.setParseBioAssembly(true);
            params.setCreateAtomBonds(true);


            if ("cif".equalsIgnoreCase(fileExtension) || "mmcif".equalsIgnoreCase(fileExtension)) {
                CifFileReader cifReader = new CifFileReader();
                cifReader.setFileParsingParameters(params);
                structure = cifReader.getStructure(file);
            } else if ("pdb".equalsIgnoreCase(fileExtension)) {
                PDBFileReader pdbReader = new PDBFileReader();
                pdbReader.setFileParsingParameters(params);
                structure = pdbReader.getStructure(file);
            } else {
                throw new IllegalArgumentException("Unsupported file format: " + fileExtension); // DEBUG
            }

        } catch (Exception e) {
            System.err.println("Failed to load structure from file: " + file.getAbsolutePath() + e);
        }

        return structure;
    }

    /**
     * Zjistí příponu souboru
     * @param file soubor
     * @return přípona, prázdný string, pokud žádná přípona
     */
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";  // default to empty string if no extension found
    }

    /**
     * Z prediction souboru, který vygeneruje P2Rank, metoda extrahuje jednotlivé pockety a obarví
     * atomy, které těmto pocketům náleží. Ke správné funkconalitě je nutné, aby byl vložen dobré pdb/
     * cif/mmcif soubor, jinak ho to nanačte.
     * @param predictionFile vygenerovaný soubor z P2Rank (JménoStruktury_prediction.csv)
     */
    public void loadPocketPrediction(String predictionFile, File strucFile){
        structure = getStructureFromFile(strucFile);
        structureFilePath = strucFile.getAbsolutePath();
        jmolPanel.getViewer().evalString("load " + strucFile.getAbsolutePath());

        P2RankPrediction prediction = new P2RankPrediction(predictionFile, structure);


        jmolPanel.evalString("select *; color translucent 0.5");

        for(int i=1;i<prediction.getPocketNum()+1;i++){
            Integer[] atoms = prediction.getAtomIDs(i);
            StringBuilder jmolCMD = new StringBuilder("select ");
            for(Integer atom:atoms){
                if(atom.equals(atoms[0])){
                    jmolCMD.append("atomno=").append(atom);
                    jmolCMD.append("; label \"POCKET ");
                    jmolCMD.append(i).append("\";");
                    jmolCMD.append("select ");
                }
                jmolCMD.append("atomno=");
                jmolCMD.append(atom);
                if(!atom.equals(atoms[atoms.length-1])){
                    jmolCMD.append(" or ");
                }
            }

            jmolCMD.append(";");
            jmolCMD.append(" color ");
            jmolCMD.append(getRandomColor()).append(";");
            jmolCMD.append("color translucent 1;");
            jmolCMD.append("halo on; ");
            System.out.println(jmolCMD); // DEBUG
            jmolPanel.evalString(jmolCMD.toString());
        }

    }

    /**
     * Z prediction souboru, který vygeneruje P2Rank, metoda extrahuje jednotlivé pockety a obarví
     * atomy, které těmto pocketům náleží. Ke správné funkconalitě je nutné, aby byl vložen dobré pdb/
     * cif/mmcif soubor, jinak ho to nanačte.
     * @param predictionFile vygenerovaný soubor z P2Rank (JménoStruktury_prediction.csv)
     * @param strucFile soubor se strukturou
     */
    public void loadPocketPrediction(File predictionFile, File strucFile){
        loadPocketPrediction(predictionFile.getAbsolutePath(), strucFile);
    }

    /**
     * Ze standardní vygenerované složky P2Rank extrahuje jednotlivé pockety a obarví atmoy,
     * které těmto pockeům náleží.
     * @param p2rankDir složka s výsledky z P2Rank
     */
    public void loadPredictionFromDir(File p2rankDir){
        String[] out = P2RankPrediction.getPredictionStructureFiles(p2rankDir);
        loadPocketPrediction(out[0],new File(out[1]));
    }

    /**
     *
     * @return používaná struktura
     */
    public Structure getStructure() {
        return structure;
    }

    /**
     * vygeneruje náhodou barvu v RGB
     * @return náhodná barva ve formátu [R,G,B]
     */
    private String getRandomColor(){
        Random r = new Random();
        return "[" + r.nextFloat() +
                "," + r.nextFloat() +
                "," + r.nextFloat()+ "]";
    }

    public String getStructureFilePath(){
        return structureFilePath;
    }
}
