package cz.cuni.mff.polakar.docking.gui;

import cz.cuni.mff.polakar.docking.controls.P2Rank;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * JPanel pro P2Rank
 */
public class P2RankPanel extends JPanel {

    public P2RankPanel(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        String[] commands = {"predict", "eval"};
        JComboBox<String> commandDropdown = new JComboBox<>(commands);
        commandDropdown.setBorder(BorderFactory.createTitledBorder("P2Rank function"));
        this.add(commandDropdown);


        FileChooser strucFileChooser = new FileChooser();
        strucFileChooser.addFileExt("pdb");
        strucFileChooser.addFileExt("mmcif");
        strucFileChooser.setBorder(BorderFactory.createTitledBorder("Select file or files"));
        strucFileChooser.setMultiSelect(true);
        this.add(strucFileChooser);

        DirChooser outDir = new DirChooser();
        outDir.setBorder(BorderFactory.createTitledBorder("Output directory"));
        this.add(outDir);

        ExtraOptions options = new ExtraOptions();

        this.add(options);


        P2Rank p2Rank = new P2Rank();
        JButton executeButton = new JButton(new AbstractAction("Execute") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(strucFileChooser.getText().isEmpty() || outDir.getText().isEmpty()){
                    JOptionPane.showMessageDialog(new JFrame(),"Files or output directory not chosen",
                            "File or output dir error",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(commandDropdown.getSelectedItem() == "predict"){
                    if(options.addButton.getModel().isPressed()){
                        File[] strucFiles = strucFileChooser.getFiles();
                        if(strucFiles.length == 1){
                            p2Rank.predict("-f " + strucFiles[0].getAbsolutePath(),
                                    "-o \"" + outDir.getText() +"\"" + options.getOptions());
                        }else{
                            p2Rank.predict(makeDatasetFile(outDir.getText(),strucFiles).getAbsolutePath(),
                                    "-o \"" + outDir.getText() +"\"" + options.getOptions());
                        }

                    } else{
                        p2Rank.predictSimple(strucFileChooser.getText(),outDir.getText());
                    }
                } else if (commandDropdown.getSelectedItem() == "eval") {// TODO

                    System.out.println("EVAL");
                }
            }
        });
        this.add(executeButton);
    }

    /**
     * Vytvoří dataset soubor (dataset.ds) pro spuštění P2Rank s více soubory
     * @param outDir output složka
     * @param strucFiles soubory se strukturami
     * @return dataset soubor
     */
    private File makeDatasetFile(String outDir, File[] strucFiles){
        File dsFile = new File(outDir + "/dataset.ds");
        try {
            boolean created = dsFile.createNewFile();
            if(!created){
                dsFile.delete();
                dsFile.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter fw = new FileWriter(dsFile)){
            for(File f:strucFiles){
                fw.append(f.getAbsolutePath());
                fw.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return dsFile;
    }

    /**
     * JPanel s možnostvi vložit další možnosti pro spuštění P2Rank
     */
    private static class ExtraOptions extends JPanel {

        private JButton addButton;
        private FileChooser configField;
        private FileChooser modelFile;
        private JTextField threadsField;
        private JComboBox<String> visualizationComboBox;
        private JTextField otherOptionsField;


        public ExtraOptions() {

            //setLayout(new BorderLayout());

            // Main panel
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


            // Button to add input fields
            addButton = new JButton("Extras");
            addButton.addActionListener(e -> {
                addInputFields();
                addButton.setEnabled(false); // Disable the button after inputs are added
                addButton.setVisible(false);
            });

            this.add(addButton);
            this.setVisible(true);
        }

        private void addInputFields() {
            configField = new FileChooser();
            configField.setAcceptAllFiles(true);
            configField.setMultiSelect(false);
            configField.setBorder(BorderFactory.createTitledBorder("Config"));
            this.add(configField);

            modelFile = new FileChooser();
            modelFile.setAcceptAllFiles(true);
            modelFile.setMultiSelect(false);
            modelFile.setBorder(BorderFactory.createTitledBorder("Model"));
            this.add(modelFile);


            threadsField = new JTextField(20);
            threadsField.setBorder(BorderFactory.createTitledBorder("Threads"));
            threadsField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c)) {
                        e.consume(); // Ignore this input
                    }
                }
            });
            this.add(threadsField);

            String[] visualizations = {"True", "False"};
            visualizationComboBox = new JComboBox<>(visualizations);
            visualizationComboBox.setBorder(BorderFactory.createTitledBorder("Visualization"));
            this.add(visualizationComboBox);

            otherOptionsField = new JTextField(20);
            otherOptionsField.setBorder(BorderFactory.createTitledBorder("Additional Options"));
            this.add(otherOptionsField);

            this.revalidate();
            this.repaint();
        }

        /**
         * Vrátí možnosti jako <code>String</code> oddělené mezerami
         * @return možnosti
         */
        public String getOptions(){
            StringBuilder sb = new StringBuilder();

            if(!configField.getText().isEmpty()){
                sb.append("-c \"");
                sb.append(configField.getText());
                sb.append("\" ");
            }
            if(modelFile.getFiles() != null){
                sb.append(" -m \"");
                sb.append(modelFile.getFiles()[0].getAbsolutePath());
                sb.append("\" ");
            }
            if(!threadsField.getText().isEmpty()){
                sb.append(" -threads ");
                sb.append(threadsField.getText());
                sb.append(" ");
            }
            if(visualizationComboBox.getSelectedItem() == "True"){
                sb.append(" -visualizations 1 ");
            } else{
                sb.append(" -visualizations 0 ");
            }
            sb.append(otherOptionsField.getText());

            return sb.toString();
        }


    }
}
