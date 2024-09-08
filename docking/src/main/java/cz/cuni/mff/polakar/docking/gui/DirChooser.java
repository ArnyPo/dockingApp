package cz.cuni.mff.polakar.docking.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * JPanel komponenta, která vybírá složky
 */
public class DirChooser extends JPanel {
    private final JTextArea directoryPathLabel;
    private final JFileChooser directoryChooser;
    private String selectedString;
    private File selectedFile;

    public DirChooser() {
        this.setLayout(new GridLayout(1,2,5,5));

        directoryPathLabel = new JTextArea(3,10);
        directoryPathLabel.setEditable(true);
        directoryPathLabel.setAutoscrolls(true);
        directoryPathLabel.setLineWrap(true);

        directoryChooser = new JFileChooser();
        directoryChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setAcceptAllFileFilterUsed(false); // Optional: to only allow directory selection

        JButton chooseDirButton = new JButton("Choose Directory");
        chooseDirButton.addActionListener(e -> {
            int returnValue = directoryChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = directoryChooser.getSelectedFile();
                directoryPathLabel.setText("Selected directory: " + selectedDirectory.getAbsolutePath());
                selectedString = selectedDirectory.getAbsolutePath();
                selectedFile = selectedDirectory;
            }
        });
        this.add(chooseDirButton);
        this.add(directoryPathLabel);

        this.setVisible(true);
    }

    /**
     *
     * @return vybraná složka
     */
    public String getText() {
        return selectedString;
    }

    /**
     *
     * @return vybraná složka jako <code>File</code>
     */
    public File getSelectedFile(){return selectedFile;}
}
