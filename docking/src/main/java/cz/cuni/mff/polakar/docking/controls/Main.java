package cz.cuni.mff.polakar.docking.controls;

import cz.cuni.mff.polakar.docking.gui.GUI;
import cz.cuni.mff.polakar.docking.utils.CmdExec;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        new GUI();

        //new Frame1();
    }
}

class Executor extends SwingWorker<Integer, String> implements Callable<Void> {
    Frame2 frame;
    int exitCode;
    String cmd;
    Process process;
    public Executor(String cmd){
        frame = new Frame2();
        this.cmd = cmd;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> {
            ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));
            builder.redirectErrorStream(true);
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


            process.waitFor();
            return process.exitValue();
        };

        Future<Integer> future = executor.submit(task);
        try {
            // Optionally, you can provide a timeout
            return future.get();  // This will block until the task is completed
        } finally {
            executor.shutdownNow();  // Always remember to shutdown the executor
        }
    }

    @Override
    protected void process(List<String> chunks) {
        for(String line:chunks){
            frame.append(line);
        }
    }

    @Override
    protected void done() {
        try {
            exitCode = get();
            frame.append("Command executed with exit code: " + exitCode + "\n");
        } catch (Exception e) {
            frame.append("Error executing command.\n");
            exitCode = -1;
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Integer resultNow() {
        return super.resultNow();
    }

    @Override
    public State state() {
        return super.state();
    }


    @Override
    public Void call() throws Exception {
        return null;
    }

    public void cancelCommand() {
        cancel(true); // This requests cancellation
        if (process != null) {
            process.destroy(); // Ensures the process is terminated
        }
    }

}

class Frame1 extends JFrame{
    JFrame frame;
    Executor executor;
    Process process;
    int exitCode;
    public Frame1(){
        frame = new JFrame();
        frame.setSize(200,200);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        Container container = frame.getContentPane();
        Executor executor = new Executor("cmd.exe /c prank");

        JButton executeButton = new JButton(new AbstractAction("Execute") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int out = executeCommand("cmd.exe /c prank");
                    setExitCode(out);
                } catch (ExecutionException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                //executor.execute();
                //System.out.println(executor.exitCode);
            }
        });
        JButton cancelButton = new JButton(new AbstractAction("------ Cancel computation ----") {
            @Override
            public void actionPerformed(ActionEvent e) {
                process.destroy();
                //executor.cancelCommand();
            }
        });
        container.add(executeButton);
        container.add(cancelButton);
        frame.pack();
        frame.setVisible(true);
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
        System.out.println("NEW EXIT CODE: " + exitCode);
    }

    public int executeCommand(String command) throws ExecutionException, InterruptedException {

        Frame2 frame2 = new Frame2();


        ExecutorService executor = Executors.newSingleThreadExecutor(Thread::new);


        Callable<Integer> task = () -> {
            ProcessBuilder builder = new ProcessBuilder(command.split(" "));
            builder.redirectErrorStream(true);
            process = builder.start();

            InputStream inputStream = process.getInputStream(); // Get the output stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null && process.isAlive()) {
                    frame2.append(line);
                }
            } catch (IOException ex) {
                if (!process.isAlive()) {
                    frame2.append("Process was cancelled");
                    return null; // Return early if the process is cancelled
                }
                throw ex;
            }

            process.waitFor();
            return process.exitValue();
        };

        Future<Integer> future = executor.submit(task);
        try {
            // Optionally, you can provide a timeout
            return future.get();  // This will block until the task is completed
        } finally {
            executor.shutdownNow();  // Always remember to shutdown the executor
        }
    }

}
class Frame2 extends JFrame{
    public JTextArea output;

    public Frame2(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        Container container = frame.getContentPane();

        output = new JTextArea(10,50);
        JScrollPane scrollPane = new JScrollPane(output);

        container.add(scrollPane);
        frame.pack();
        frame.setVisible(true);

    }
    public void append(String s){
        output.append(s + "\n");
    }

}
