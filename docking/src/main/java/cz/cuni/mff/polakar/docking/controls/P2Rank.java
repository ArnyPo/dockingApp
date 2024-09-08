package cz.cuni.mff.polakar.docking.controls;

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

    /**
     * Spuštění plného příkazu predict - se všemi možnými options
     * @param dataset dataset soubor se seznamem struktur
     * @param options options
     */
    public void predict(String dataset, String options){
        exec.exec("predict" + " \"" + dataset + "\" " + options,"PRANK PREDICTION");
    }

    /**
     * Spuštění příkazu predict v nejjednodušší formě - jen jeden soubor se strukturou a místo pro output
     * @param pdbFile PDB soubor
     * @param outFile složka pro output
     */
    public void predictSimple(String pdbFile, String outFile){
        exec.exec("predict" + " -f \"" + pdbFile + "\" -o \"" + outFile+"\"","PRANK PREDICTION");
    }

    /**
     * Spuštění příkazu rescore
     * @param dataset dataset soubor se seznamem struktur
     * @param options options
     */
    public void rescore(String dataset, String options){
        exec.exec("rescore" + " \"" + dataset + "\" " + options,"PRANK RESCORE");
    }


}

