package cz.cuni.mff.polakar.docking.controls;

import javax.vecmath.Point3d;

/**
 * Implementace AutoDock Vina
 */
public class Vina {
    static CmdExec exec;
    public Vina(){
        exec = new CmdExec("vina");
    }

    /**
     * Spuštěí AutoDock Vina jen s konfikugračním souborem
     * @param configPath konfigurační soubor
     */
    public void runConfig(String configPath){ // TODO implementace
        exec.exec("--config " + configPath, "AUTODOCK VINA");
    }

    public void run(String receptorPath, String ligandPath, String outPath, int cx, int cy, int cz, int sx, int sy, int sz){ // DELETE?
        String cmd = "--receptor " + receptorPath + " --ligand " + ligandPath +
                " --center_x " + cx + " --center_y " + cy +" --center_z " + cz +
                " --size_x " + sx + " --size_y " + sy + " --size_z " + sz +
                " --out " + outPath;
        exec.exec(cmd, "AUTODOCK VINA");
    }

    /**
     * Spuštění AutoDock Vina se všemi potřebými částmi
     * @param receptorPath soubor s receptorem (strukturou)
     * @param ligandPath soubor s ligandem
     * @param outPath output složka
     * @param cx center x
     * @param cy center y
     * @param cz center z
     * @param sx size x
     * @param sy size y
     * @param sz size z
     */
    public void run(String receptorPath, String ligandPath, String outPath,
                    String cx, String cy, String cz, String sx, String sy, String sz){
        String cmd = "--receptor \"" + receptorPath + "\" --ligand \"" + ligandPath +
                "\" --center_x " + cx + " --center_y " + cy +" --center_z " + cz +
                " --size_x " + sx + " --size_y " + sy + " --size_z " + sz +
                " --out \"" + outPath + "\"";
        System.out.println(cmd);
        exec.exec(cmd, "AUTODOCK VINA");
    }
    public void run(String receptorPath, String ligandPath, String outPath,
                    Point3d center, Point3d size){ // TODO implementace

    }

}
