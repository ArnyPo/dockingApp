package cz.cuni.mff.polakar.docking.gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * JPanel pro vybírání jednoho či více souborů
 */
public class FileChooser extends JPanel {
    private JButton fileChooserButton;
    private JTextArea fileInfoDisplay;
    private JFileChooser fileChooser;

    public FileChooser() {
        setLayout(new GridLayout(1,2,5,5));

        fileChooserButton = new JButton("Choose File(s)");
        fileChooserButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                displaySelectedFiles();
            }
        });
        this.add(fileChooserButton);

        fileInfoDisplay = new JTextArea(3,10);
        fileInfoDisplay.setEditable(false);
        fileInfoDisplay.setAutoscrolls(true);
        fileInfoDisplay.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(fileInfoDisplay);
        this.add(scrollPane);

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        this.setVisible(true);
    }

    /**
     * Přidání přípony souborů, které mohou být vybrány.
     * @param ext přípona k přidání
     */
    public void addFileExt(String ext){
        String end = "." + ext;
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f.getName().endsWith(end)){
                    return true;
                }
                if(f.isDirectory()){
                    return true;
                }

                return false;
            }

            @Override
            public String getDescription() {
                return end;
            }
        });
    }


    private void displaySelectedFiles() {
        File[] files = fileChooser.getSelectedFiles();
        if (files.length > 1 || fileChooser.isMultiSelectionEnabled()) {
            java.util.List<String> filePaths = new ArrayList<>();
            for (File file : files) {
                filePaths.add(file.getAbsolutePath());
                fileInfoDisplay.append(file.getAbsolutePath() + "\n");
            }
        } else {
            File file = fileChooser.getSelectedFile();
            fileInfoDisplay.setText(file.getAbsolutePath());
        }
    }

    /**
     *
     * @return text zobrazený v JTextArea
     */
    public String getText() {
        return fileInfoDisplay.getText().strip();
    }

    /**
     *
     * @return všechny vybrané soubory, null pokud žádné nejsou vybrané
     */
    public File[] getFiles(){
        if(fileChooser.isMultiSelectionEnabled()){
            return fileChooser.getSelectedFiles();
        } else{
            if(fileChooser.getSelectedFile() == null){
                return null;
            }
            return fileChooser.getSelectedFile().listFiles();
        }
    }

    /**
     *
     * @return jeden vybraný soubor, null pokud žádný nebyl vybrán
     */
    public File getFile(){
        if(fileChooser.getSelectedFile() == null){
            return null;
        }
        return fileChooser.getSelectedFile();
    }

    /**
     * Vepíše do JTextArea cestu souboru, přepíše cokoliv jiného vybraého
     * @param path cesta k souboru
     */
    public void setText(String path){
        fileInfoDisplay.setText(path);
    }

    /**
     *
     * @param b jestli může vybírat více jak jeden soubor
     */
    public void setMultiSelect(boolean b) {
        fileChooser.setMultiSelectionEnabled(b);
    }

    /**
     *
     * @param b jestli může vybírat všechny typy souborů
     */
    public void setAcceptAllFiles(boolean b){
        fileChooser.setAcceptAllFileFilterUsed(b);
    }

}
