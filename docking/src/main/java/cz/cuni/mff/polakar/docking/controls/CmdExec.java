package cz.cuni.mff.polakar.docking.controls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída pro spouštění příkazů na shell/cmd
 */
public class CmdExec {
    // TODO vlastní working directory, a IO redirect
    /**
     * Zjištěný operační systém - win/lin
     */
    public String os;
    /** Příkazy závislé na OS + samotný přkaz
     */
    private List<String> commands;
    public CmdExec(){
        commands = new ArrayList<>();
        resolveOS();
    }

    /**
     * Inicializátor s příponou programu, které bude spouštěn
     * @param prefix jméno programu (pokud je v PATH) jinak pustitelný soubor
     */
    public CmdExec(String prefix){
        commands = new ArrayList<>();
        resolveOS();
        commands.add(prefix);
    }

    /**
     * Metoda přidá dle detekovaného operačního systému
     * do <code>commands</code> kusy kódu, které musí být vždy spuštěny
     */
    public void resolveOS(){
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            os = "win";
            commands.add("cmd.exe");
            commands.add("/c");
        }
        else {
            os = "lin";
            commands.add("/bin/bash");
            commands.add("-c");
        }
    }

    /**
     * Spustí příkaz
     * @param cmd Příkaz, který bude vyhodnocen společně s tím, co je v <code>commands</code>
     */
    public void exec(String cmd, String processName){
        JFrame frame = new JFrame("Command output - " + processName);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container container = frame.getContentPane();
        frame.setLayout(new BorderLayout());


        JTextArea outputArea = new JTextArea(10,40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        Worker worker = new Worker(cmd,outputArea);

        JButton cancelButton = new JButton("Cancel computation");
        cancelButton.addActionListener(e -> worker.cancelCommand());
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSED));

        container.add(new JLabel(cmd), BorderLayout.NORTH);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(scrollPane);
        panel.add(cancelButton);
        container.add(panel);

        frame.pack();
        container.validate();
        frame.setVisible(true);


        worker.execute();
    }

    /**
     * Implementace SwingWorker, ve které běží procesy nezávisle na samotné aplikaci
     */
    private class Worker extends SwingWorker<Integer, String>{
        private int exitCode = -1;
        private Process process;
        private final String cmd;
        private final JTextArea outputArea;

        public Worker(String cmd, JTextArea outputArea){
            this.cmd = cmd;
            this.outputArea = outputArea;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            List<String> localCommands = new ArrayList<>(List.copyOf(commands));
            localCommands.addAll(smartSplit(cmd));
            //localCommands.forEach(System.out::println); // DEBUG

            ProcessBuilder builder = new ProcessBuilder(localCommands);
            builder.redirectErrorStream(true);  // Redirect error stream to output stream
            process = builder.start();

            InputStream inputStream = process.getInputStream(); // Get the output stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null && !isCancelled()) {
                    publish(line);
                }
            } catch (IOException ex) {
                if (isCancelled()) {
                    publish("Process was cancelled");
                    return null; // Return early if the process is cancelled
                }
                throw ex;
            }

            exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode); // DEBUG
            return exitCode;
        }

        @Override
        protected void process(List<String> chunks) {
            for(String line:chunks){
                outputArea.append(line + "\n");
            }
        }

        @Override
        protected void done() {
            try {
                exitCode = get();
                outputArea.append("Command executed with exit code: " + exitCode + "\n");
            } catch (Exception e) {
                outputArea.append("Error executing command.\n");
                Thread.currentThread().interrupt();
            }
        }
        private void cancelCommand() {
            cancel(true); // This requests cancellation
            if (process != null) {
                process.destroy(); // Ensures the process is terminated

            }
            publish("CANCELED");
            System.out.println("CANCELED"); // DEBUG
        }
    }


    /**
     * Přidání dalších kusů kódu, které budou před všemi příkazy v této instanci třídy
     * @param prefix prefix
     */
    public void setPrefix(String prefix){
        commands.add(prefix);
    }

    /** Metoda, která rozdělí správně příkaz z jednoho <code>String</code> na správně rozdělené kusy, zejména
     * kvůli uvozovkám.
     *
     * @param input příkaz
     * @return rozdělený příkaz na kusy, které mají být spuštěny pro <code>ProcessBuilder</code>
     * @throws Exception pokud nebyly uzavřeny uvozovky
     */
    private static List<String> smartSplit(String input) throws Exception {
        input = input.replace('\n',' ');
        List<String> parts = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes;  // Toggle the state of inQuotes
                if (!inQuotes && !buffer.isEmpty()) {  // End of a quoted part
                    parts.add(buffer.toString());
                    buffer = new StringBuilder();
                }
                continue;
            }

            if (c == ' ' && !inQuotes) {
                if (!buffer.isEmpty()) {
                    parts.add(buffer.toString());
                    buffer = new StringBuilder();
                }
                continue;
            }

            buffer.append(c);
        }

        if (inQuotes) {
            throw new Exception("Unclosed quotation marks found.");
        }

        if (!buffer.isEmpty()) {
            parts.add(buffer.toString());  // Add the last buffered part
        }

        return parts;
    }
}
