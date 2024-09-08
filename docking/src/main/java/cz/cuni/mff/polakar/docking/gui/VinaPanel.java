package cz.cuni.mff.polakar.docking.gui;

import cz.cuni.mff.polakar.docking.controls.OpenBabel;
import cz.cuni.mff.polakar.docking.controls.P2RankPrediction;
import cz.cuni.mff.polakar.docking.controls.Vina;
import org.biojava.nbio.structure.Structure;

import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Panel obsluhuje základní kontrolu programmu AutoDock Vina
 */
public class VinaPanel extends JPanel {
    private FileChooser receptorFileField;
    private FileChooser ligandFileField;
    private JTextField cxField, cyField, czField;
    private JTextField sxField, syField, szField;
    private JTextField outFileField;
    private JButton computeButton;
    private OpenBabel oBabel;

    public VinaPanel() {
        this.setLayout(new GridLayout(10, 2, 5, 5));

        // Receptor file
        this.add(new JLabel("Receptor file (PDBQT):"));
        receptorFileField = new FileChooser();
        receptorFileField.addFileExt("pdb");
        receptorFileField.addFileExt("cif");
        receptorFileField.addFileExt("mmcif");
        receptorFileField.setMultiSelect(false);
        this.add(receptorFileField);

        // Ligand file
        this.add(new JLabel("Ligand file (PDBQT):"));
        ligandFileField = new FileChooser();
        ligandFileField.addFileExt("pdbqt");
        ligandFileField.setMultiSelect(true); // TODO handling of multiple files
        this.add(ligandFileField);

        // Center coordinates cx, cy, cz
        this.add(new JLabel("Center x:"));
        cxField = new JTextField();
        this.add(cxField);

        this.add(new JLabel("Center y:"));
        cyField = new JTextField();
        this.add(cyField);

        this.add(new JLabel("Center z:"));
        czField = new JTextField();
        this.add(czField);

        // Size sx, sy, sz
        this.add(new JLabel("Size x:"));
        sxField = new JTextField();
        this.add(sxField);

        this.add(new JLabel("Size y:"));
        syField = new JTextField();
        this.add(syField);

        this.add(new JLabel("Size z:"));
        szField = new JTextField();
        this.add(szField);

        // Output file
        this.add(new JLabel("Output file:"));
        outFileField = new JTextField();
        this.add(outFileField);

        // Compute button
        computeButton = new JButton("Compute");
        computeButton.addActionListener(e -> compute());
        this.add(computeButton);
        this.add(new JLabel("")); // Placeholder

        oBabel = new OpenBabel();
    }

    /**
     * Z souborů vygenerovaných P2Rank vybere data o pozici jednolivých
     * pocketů a vyplní velikost boxu, ve kterém bude AutoDock Vina
     * dockovat jednotlivé ligandy.
     */
    public void loadFromP2Rank(File predictionFile, Structure structure, String structureFilePath) {
        P2RankPrediction prediction = new P2RankPrediction(predictionFile.getAbsolutePath(), structure);

        int pocketNum = (int) JOptionPane.showInputDialog(this,"Pocket number","Choose pocket number",
                JOptionPane.QUESTION_MESSAGE,null,getPocketArray(prediction.getPocketNum()),1);

        Point3d center = prediction.getCenter(pocketNum);
        cxField.setText(String.valueOf(center.x));
        cyField.setText(String.valueOf(center.y));
        czField.setText(String.valueOf(center.z));

        Point3d size = prediction.getPocketSize(pocketNum);
        sxField.setText(String.valueOf(size.x));
        syField.setText(String.valueOf(size.y));
        szField.setText(String.valueOf(size.z));

        /* TODO - p2rank dir není - takže buď odstranit nebo nějak jinka vymyslet
        int exit = oBabel.convertToPDBQT(new File(structureFilePath), p2rankDir.getAbsolutePath());
        if(exit != 0){
            throw new RuntimeException("PDBQT conversion failed"); // DEBUG
        }
        receptorFileField.setText(OpenBabel.getPDBQTFileName(structureFilePath));

         */

    }
    public void loadFromP2Rank(File p2rankDir){
        String[] out = P2RankPrediction.getPredictionStructureFiles(p2rankDir);
        String predictionFile = out[0];
        String structureFile = out[1];

        P2RankPrediction prediction = new P2RankPrediction(predictionFile,
                JMolPanel.getStructureFromFile(new File(structureFile)));

        int pocketNum = (int) JOptionPane.showInputDialog(this,"Pocket number","Choose pocket number",
                JOptionPane.QUESTION_MESSAGE,null,getPocketArray(prediction.getPocketNum()),1);


        Point3d center = prediction.getCenter(pocketNum);
        cxField.setText(String.valueOf(center.x));
        cyField.setText(String.valueOf(center.y));
        czField.setText(String.valueOf(center.z));

        Point3d size = prediction.getPocketSize(pocketNum);
        sxField.setText(String.valueOf(size.x));
        syField.setText(String.valueOf(size.y));
        szField.setText(String.valueOf(size.z));

        oBabel.convertToPDBQT(new File(structureFile), p2rankDir.getAbsolutePath());
        /*
        int exit = oBabel.convertToPDBQT(new File(structureFile), p2rankDir.getAbsolutePath());
        if(exit != 0){
            throw new RuntimeException("PDBQT conversion failed"); // DEBUG
        }

         */
        receptorFileField.setText(OpenBabel.getPDBQTFileName(structureFile));

        outFileField.setText("C:\\Users\\marti\\Desktop\\prank_test\\2\\out.txt"); // DEBUG
        ligandFileField.setText("C:\\Users\\marti\\Desktop\\SKOLA\\cuni\\SKOLA 2023-2024\\MeetEU\\meeteu\\ligands\\t100\\1199.pdbqt"); // DEBUG

    }

    private void compute() {

        String receptorFile = receptorFileField.getText();
        String ligandFile = ligandFileField.getText();
        String cx = cxField.getText();
        String cy = cyField.getText();
        String cz = czField.getText();
        String sx = sxField.getText();
        String sy = syField.getText();
        String sz = szField.getText();
        String outFile = outFileField.getText();

        if(!receptorFile.endsWith(".pdbqt") || ligandFile.endsWith(".pdbqt")){
            JOptionPane.showMessageDialog(this,"Receptor or ligand file is not a pdbqt file!","File input error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(cx.isEmpty() || cy.isEmpty() || cz.isEmpty()){
            JOptionPane.showMessageDialog(this,"Center coordinates not present!","Center coordinates input error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(sx.isEmpty() || sy.isEmpty() || sz.isEmpty()){
            JOptionPane.showMessageDialog(this,"Size values not present!","Size input error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(outFile.isEmpty() || receptorFile.isEmpty() || ligandFile.isEmpty()){
            JOptionPane.showMessageDialog(this,"Files not selected!","File intput error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        Vina vina = new Vina();
        vina.runReq(receptorFile, ligandFile,outFile,cx,cy,cz,sx,sy,sz);

        // DELETE Placeholder for actual computation
        /*
        JOptionPane.showMessageDialog(this, "Computation started with the following parameters:\n" +
                "Receptor file: " + receptorFile + "\n" +
                "Ligand file: " + ligandFile + "\n" +
                "Center: (" + cx + ", " + cy + ", " + cz + ")\n" +
                "Size: (" + sx + ", " + sy + ", " + sz + ")\n" +
                "Output file: " + outFile);

         */
    }

    public void setReceptorFileField(String filePath){
        receptorFileField.setText(filePath);
    }

    private Object[] getPocketArray(int pockets){
        Object[] pocketA = new Object[pockets];
        for(int i=1;i<=pockets;i++){
            pocketA[i-1] = i;
        }
        return pocketA;
    }

}
