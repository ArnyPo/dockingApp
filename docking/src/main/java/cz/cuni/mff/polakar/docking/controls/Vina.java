package cz.cuni.mff.polakar.docking.controls;

import cz.cuni.mff.polakar.docking.utils.CmdExec;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.biojava.nbio.structure.*;
import org.biojava.nbio.structure.align.util.AtomCache;
import org.biojava.nbio.structure.io.CifFileReader;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.jmol.adapter.readers.cif.MMCifReader;

import javax.vecmath.Point3d;

/**
 * Implementation of AutoDock Vina
 */
public class Vina {

    static CmdExec exec;

    public Vina(){
        exec = new CmdExec("vina");

    }

    public void runConfig(String configPath){
        exec.exec("--config " + configPath, "AUTODOCK VINA");
    }

    public void runReq(String receptorPath, String ligandPath, String outPath,
                           int cx, int cy, int cz, int sx, int sy, int sz){
        String cmd = "--receptor " + receptorPath + " --ligand " + ligandPath +
                " --center_x " + cx + " --center_y " + cy +" --center_z " + cz +
                " --size_x " + sx + " --size_y " + sy + " --size_z " + sz +
                " --out " + outPath;
        exec.exec(cmd, "AUTODOCK VINA");
    }
    public void runReq(String receptorPath, String ligandPath, String outPath,
                       String cx, String cy, String cz, String sx, String sy, String sz){
        String cmd = "--receptor \"" + receptorPath + "\" --ligand \"" + ligandPath +
                "\" --center_x " + cx + " --center_y " + cy +" --center_z " + cz +
                " --size_x " + sx + " --size_y " + sy + " --size_z " + sz +
                " --out \"" + outPath + "\"";
        System.out.println(cmd);
        exec.exec(cmd, "AUTODOCK VINA");
    }
    public void runReq(String receptorPath, String ligandPath, String outPath,
                       Point3d center, Point3d size){

    }

}
