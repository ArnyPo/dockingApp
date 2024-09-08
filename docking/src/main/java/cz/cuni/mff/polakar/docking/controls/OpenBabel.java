package cz.cuni.mff.polakar.docking.controls;

import cz.cuni.mff.polakar.docking.utils.CmdExec;

import java.io.File;
import java.util.Optional;

/**
 * OBabel implementace <br>
 *
 * OpenBabel je aplikace, která umí konvertovat různé výzkumnické datové formáty mezi sebou.
 * Pro více informací o jednotlivých formátech atp. navštivte <code>obabel -H</code>.
 */
public class OpenBabel {
    private static CmdExec exec;
    public OpenBabel(){
        exec = new CmdExec("obabel");
    }

    public void convertPDBtoPDBQT(String pdbFile, String outDir){ // DELETE
        exec.exec("-i pdb \"" + pdbFile + "\" " +
                "-o pdbqt -O \"" + outDir + "/" + pdbFile + ".pdb\"",
                "OBABEL CONVERTOR PDB TO PDBQT");
    }

    /**
     * Konverze souboru do PDBQT
     * @param filePath soubor, který bude konvertován
     * @param outDir složka, ve které bude nový soubor uložen
     */
    public static void convertToPDBQT(File filePath, String outDir){
        String ext = Optional.of(filePath.getAbsolutePath())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filePath.getAbsolutePath().lastIndexOf(".") + 1)).get();

        exec.exec("-i " + ext + " \"" + filePath + "\" " +
                "-o pdbqt -O \"" + outDir + "\\" +
                filePath.getName().substring(0,filePath.getName().lastIndexOf('.')) + ".pdbqt\"",
                "OBABEL CONVERTOR TO PDBQT");
    }

    public static String getPDBQTFileName(File file){
        return file.getName().substring(0,file.getName().lastIndexOf('.')) + ".pdbqt";
    }
    public static String getPDBQTFileName(String file){
        return getPDBQTFileName(new File(file));
    }

    /**
     * Konverze jakéhokoliv soboru
     * @param file soubor, který bude konvertován
     * @param outFile jméno nového soboru včetně nové přípony
     */
    public static void convert(String file, String outFile){
        exec.exec(file + " -O " + outFile , "OBABEL CONVERTOR");
    }
}
