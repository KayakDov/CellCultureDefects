package main;

import Animation.DrawDefects;
import ReadWrite.SpreadsheetReadManager;
import Annalysis.BirthAndDeathTracker;
import snapDefects.SpaceTemp;
import GeometricTools.Rectangle;
import GeometricTools.OpenSpaceTimeBall;
import GeometricTools.Vec;
import ReadWrite.DefaultWriter;
import ReadWrite.FormatedFileWriter;
import ReadWrite.PairReadManager;
import ReadWrite.ReadManager;
import SnapManagement.PairSnDef;
import defectManagement.DefectManager;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author E. Dov Neimand
 */
public class Main {

    /**
     * Tests the pair move creation method.
     */
    private static void testImageCreation() {
        PairReadManager prm = PairReadManager.defaultFileFormat("BacteriaPairs.csv");
        List<PairSnDef> pairs = prm.pairSetContainingLine(1);
        DrawDefects.draw("images/source/PosDef.png", "images/source/NegDef.png", "images/output/", pairs);
    }

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

        return new Rectangle(x, y, width, height, distEdge(args));
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

//        args = new String[12];
//        args[0] = "PlusAndMinusTM.csv";
//        args[1] = "Bacteria_pairs.csv";
//        args[2] = "window";
//        args[3] = "900";
//        args[4] = "0";
//        args[5] = "900";
//        args[6] = "900";
//        args[7] = "threshold";
//        args[8] = "8";//small number for bacteria, larger number for cells
//        args[9] = "2";
//        args[10] = "20";
//        args[11] = "5";
        if (!new File(args[1]).isDirectory()) {
            DefectManager dm = new File(args[0]).isDirectory()
                    ? new DefectManager(
                            args[0],
                            getWindow(args),
                            new OpenSpaceTimeBall(timeThreshold(args), distThreshold(args)),
                            timeEdge(args)
                    )
                    : new DefectManager(
                            ReadManager.defaultFileFormat(args[0]),
                            getWindow(args),
                            new OpenSpaceTimeBall(timeThreshold(args), distThreshold(args)),
                            timeEdge(args)
                    );

            try (DefaultWriter writer = new DefaultWriter(args[1])) {
                dm.writePairesToFile(writer);
            }
        } else {
            PairReadManager prm = PairReadManager.defaultFileFormat(args[0]);
            List<PairSnDef> pairs = prm.pairSetContainingLine(Integer.parseInt(args[2]));
            DrawDefects.draw("PosDef.png", "NegDef.png", args[1], pairs);
        }

//        checkFile(args[1]);
    }

    /**
     * Keep this method empty unless simple tests need to be conducted on the
     * file created.
     *
     * @param fileName "plus_id"
     */
    private static void checkOutputFile(String fileName) {
        SpreadsheetReadManager ssr = new SpreadsheetReadManager(fileName);

        Map<Integer, Integer> idCounts = new HashMap<>();

        ssr.lines("plus_id")
                .mapToInt(posIDString -> Integer.valueOf(posIDString))
                .forEach(id -> idCounts.put(id, idCounts.getOrDefault(id, 0) + 1));

        System.out.println(idCounts.values().stream().mapToInt(i -> i).max().getAsInt());

    }

    /**
     * produces a pair file with some default settings for a bunch of cell
     * experiments.
     */
    public static void defaults() throws IOException {

        DefectManager dm = DefaultData.bacteria();//(time, dist);
        BirthAndDeathTracker ct = new BirthAndDeathTracker(dm);
//        ct.angleNearFusion(DefectManager.BIRTH, 20);
        ct.distanceOfFrame(1000, 0);

        try (FormatedFileWriter ffw = new DefaultWriter("BacteriaPairs.csv")) {
            dm.writePairesToFile(ffw);
        }

    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

//        testImageCreation();
//        defaults();
        parseArgs(args);
    }

}
