package cz.cuni.mff.polakar.docking.controls;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import cz.cuni.mff.polakar.docking.utils.CmdExec;

import javax.vecmath.Point3d;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * P2Rank implementace
 */
public class P2Rank {
    public CmdExec exec;
    public P2Rank(){
        exec = new CmdExec();
        if(Objects.equals(exec.os, "win")){exec.setPrefix(".\\prank.bat");}
        else{exec.setPrefix("./prank.sh");}
    }

    public void predict(String dataset, String options){
        exec.exec("predict" + " \"" + dataset + "\" " + options,"PRANK PREDICTION");
    }

    public void predictSimple(String pdbFile, String outFile){
        exec.exec("predict" + " -f \"" + pdbFile + "\" -o \"" + outFile+"\"","PRANK PREDICTION");
    }
    public void rescore(String dataset, String options){
        exec.exec("rescore" + " \"" + dataset + "\" " + options,"PRANK RESCORE");
    }


}

