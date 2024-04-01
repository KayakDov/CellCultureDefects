package main;

import GeometricTools.Angle;
import snapDefects.SpaceTemp;
import GeometricTools.Rectangle;
import ReadWrite.DefaultWriter;
import ReadWrite.ReadManager;
import ReadWrite.FormatedFileWriter;
import defectManagement.DefectManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import snapDefects.SnapDefect;

/**
 *
 * @author E. Dov Neimand
 */
public class Main {

    /**
     * If the list of arguments contains the word window, then the four numbers
     * after the word window are taken in as x, y, width, height, of the window.
     *
     * @param args The command line arguments.
     * @param dm The defect manager.
     */
    private static Rectangle getWindow(String[] args) {
        int windowIndex = ReadManager.indexOf(args, "window");
        if (windowIndex == -1)
            throw new RuntimeException("You need the runtime argument window [x] [y] [width] [height]");

        double x = Double.parseDouble(args[windowIndex + 1]);
        double y = Double.parseDouble(args[windowIndex + 2]);
        double width = Double.parseDouble(args[windowIndex + 3]);
        double height = Double.parseDouble(args[windowIndex + 4]);

        return new Rectangle(x, y, width, height);
    }

    /**
     * The distance threshold. It should come after the word threshold.
     *
     * @param args The list of arguments.
     * @return The distance threshold.
     */
    public static double distThreshold(String[] args) {
        int distIndex = ReadManager.indexOf(args, "threshold");
        if (distIndex == -1) return SpaceTemp.defaultDistanceThreshold;
        return Integer.parseInt(args[distIndex + 1]);
    }

    /**
     * The time threshold. It should come after the distance threshold.
     *
     * @param args The list of arguments.
     * @return The distance threshold.
     */
    public static int timeThreshold(String[] args) {
        int distIndex = ReadManager.indexOf(args, "threshold");
        if (distIndex == -1) return SpaceTemp.defaultTimeThreshold;
        return Integer.parseInt(args[distIndex + 2]);
    }

    /**
     * How far must a creation or fusion event be from the edge. This value
     * should be after the time threshold.
     *
     * @param args The list of arguments.
     * @return The distance from the edge .
     */
    public static double distEdge(String[] args) {
        int distIndex = ReadManager.indexOf(args, "threshold");
        if (distIndex == -1) return SpaceTemp.defaultDistanceThreshold;
        return Integer.parseInt(args[distIndex + 3]);
    }

    /**
     * The distance threshold. It should come after the dist from edge.
     *
     * @param args The list of arguments.
     * @return The distance threshold.
     */
    public static int timeEdge(String[] args) {
        int distIndex = ReadManager.indexOf(args, "threshold");
        if (distIndex == -1) return SpaceTemp.defaultTimeThreshold;
        return Integer.parseInt(args[distIndex + 4]);
    }

    /**
     * parses the arguments
     *
     * @param args The user arguments.
     */
    public static void parseArgs(String[] args) throws IOException {

        args = new String[12];
        args[0] = "PlusAndMinusTM6.csv";
        args[1] = "Bacteria_pairs.csv";
        args[2] = "window";
        args[3] = "900";
        args[4] = "0";
        args[5] = "900";
        args[6] = "900";
        args[7] = "threshold";
        args[8] = "35";//small number for bacteria, larger number for cells
        args[9] = "2";
        args[10] = "50";
        args[11] = "5";

        ReadManager ff = ReadManager.defaultFileFormat(args[0]);

        new DefectManager(
                ff,
                getWindow(args),
                timeThreshold(args),
                distThreshold(args),
                timeEdge(args),
                distEdge(args)
        ).writePairesToFile(new DefaultWriter(args[1]));
    }

    /**
     * produces a pair file with some default settings for a bunch of cell
     * experiments.
     */
    public static void cellExperiments() throws IOException {

        String parent = "plusMinusTMs//OneTenElevenTwelve";
        String[] files = Arrays.stream(new File(parent).list()).map(str -> parent + "//" + str).toArray(String[]::new);

        DefaultWriter writer = new DefaultWriter("cellPairs_1_10_11_12.csv");

        for (String fileName : files) {
            DefectManager dm = new DefectManager(
                    ReadManager.defaultFileFormat(fileName), 
                    new Rectangle(0, 0, 2050, 2050), 2, 40, 6, 80
            );
            writer.setIdBegin(fileName.replaceAll("[^0-9]", "") + ".");
            dm.writePairesToFile(writer);
        }

    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        cellExperiments();
//        parseArgs(args);
    }

}
