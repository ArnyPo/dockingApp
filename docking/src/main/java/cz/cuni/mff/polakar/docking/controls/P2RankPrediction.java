package cz.cuni.mff.polakar.docking.controls;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.biojava.nbio.structure.Atom;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.CifFileReader;

import javax.vecmath.Point3d;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class P2RankPrediction {
    static Map<Integer, Float> score = new HashMap<>();
    static Map<Integer, Float> probability = new HashMap<>();
    static Map<Integer, Point3d> center = new HashMap<>();
    static Map<Integer, Integer[]> pocketAtomIDs = new HashMap<>();
    static String predictionFile;
    static Map<Integer, Point3d> pocketSize = new HashMap<>();

    public P2RankPrediction(String predictionFilePath, Structure structure) {
        predictionFile = predictionFilePath;
        readFile();
        computePocketSize(structure);
    }

    private void readFile() {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(predictionFile)).withSkipLines(1).build()) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 3) {
                    int rank = Integer.parseInt(nextLine[1].replace(" ", ""));
                    float Fscore = Float.parseFloat(nextLine[2].replace(" ", ""));
                    float Fprobability = Float.parseFloat(nextLine[3].replace(" ", ""));
                    float centerX = Float.parseFloat(nextLine[6].replace(" ", ""));
                    float centerY = Float.parseFloat(nextLine[7].replace(" ", ""));
                    float centerZ = Float.parseFloat(nextLine[8].replace(" ", ""));

                    String[] atm = nextLine[10].split(" ");
                    ArrayList<Integer> atoms = new ArrayList<>();
                    for (String s : atm) {
                        if (s.isEmpty()) {
                            continue;
                        }
                        atoms.add(Integer.parseInt(s));
                    }

                    score.put(rank, Fscore);
                    probability.put(rank, Fprobability);
                    center.put(rank, new Point3d(centerX, centerY, centerZ));
                    pocketAtomIDs.put(rank, atoms.toArray(Integer[]::new));
                }
            }
        } catch (IOException | CsvValidationException | NumberFormatException e) {
            System.err.println("A problem occurred while reading the prediction csv file at: " +
                    predictionFile + " - " + e);
        }
    }

    private void computePocketSize(Structure struc){
        // mapa se všemi atomy v pdb/cif souboru
        Map<Integer, Point3d> atomMap = new HashMap<>();
        for(Chain chain: struc.getChains()){
            for(Group group: chain.getAtomGroups()){
                for(Atom atom: group.getAtoms()){
                    Point3d point3d = new Point3d(atom.getX(),atom.getY(),atom.getZ());
                    atomMap.put(atom.getPDBserial(),point3d);
                }
            }
        }

        // mapa, která má v sobě extrémy v jednotlivých třech směrech
        for(Map.Entry<Integer, Integer[]> entry: pocketAtomIDs.entrySet()){
            List<Point3d> points = new ArrayList<>();
            for(Integer atomId:entry.getValue()){
                points.add(atomMap.get(atomId));
            }
            pocketSize.put(entry.getKey(), findExtremes(points.toArray(Point3d[]::new),entry.getKey()));
        }
    }

    private Point3d findExtremes(Point3d[] points, int pocket){
        Double x = Double.MIN_NORMAL;
        Double y = Double.MIN_NORMAL;
        Double z = Double.MIN_NORMAL;
        Point3d center = getCenter(pocket);

        for(Point3d p: points){
            x = Math.max(Math.abs(p.x-center.x),x);
            y = Math.max(Math.abs(p.y-center.y),y);
            z = Math.max(Math.abs(p.z-center.z),z);
        }
        return new Point3d(x,y,z);
    }

    public static String[] getPredictionStructureFiles(File p2rankDir){
        String dir = p2rankDir.getAbsolutePath();
        String predictionFile = null;
        String structureFile = null;

        try (Stream<Path> paths = Files.walk(Path.of(dir))) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                String fileName = path.getFileName().toString();

                // Check for a file that ends with "_prediction.csv"
                if (fileName.endsWith("_predictions.csv")) {
                    predictionFile = path.toString();
                }

                // If both files are found, no need to continue searching
                if (predictionFile != null) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred when trying to find the prediction file " + e);
        }
        try (Stream<Path> paths = Files.walk(Path.of(dir+"/visualizations/data/"))) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                String fileName = path.getFileName().toString();

                // Check for a file that does not end with ".gz"
                if (fileName.endsWith(".pdb") || fileName.endsWith(".cif") || fileName.endsWith(".mmcif")) {
                    structureFile = path.toString();
                }

                // If both files are found, no need to continue searching
                if (structureFile != null) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred when trying to find the structure file " + e);
        }

        if(structureFile == null || predictionFile == null){
            throw new RuntimeException("Structure file or prediction file not found");
        }

        return new String[]{predictionFile,structureFile};
    }

    public float getProbability(int pocket) {
        return probability.get(pocket);
    }

    public float getScore(int pocket) {
        return score.get(pocket);
    }

    public Point3d getCenter(int pocket) {
        return center.get(pocket);
    }

    public Integer[] getAtomIDs(int pocket) {
        return pocketAtomIDs.get(pocket);
    }

    public int getPocketNum() {
        return score.size();
    }

    public Point3d getPocketSize(int pocket) {
        return pocketSize.get(pocket);
    }
}
