package cz.cuni.mff.polakar.docking.controls;

import cz.cuni.mff.polakar.docking.gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.*;

/**
 * Spouští GUI
 */
public class Main {
    public static void main(String[] args) {
        new GUI();
    }
}
