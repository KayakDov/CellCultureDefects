package main;

import Annalysis.BirthAndDeathTracker;
import Charts.HeatMap;
import Charts.NamedData;
import Charts.ScatterPlot;
import snapDefects.SpaceTemp;
import GeometricTools.Rectangle;
import GeometricTools.OpenSpaceTimeBall;
import ReadWrite.DefaultWriter;
import ReadWrite.FormatedFileWriter;
import ReadWrite.ReadManager;
import defectManagement.DefectManager;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
     * Working on the bacteria file.
     */
    public static void bacteriaWork() {
        DefectManager dm = DefaultData.bacteria();

        System.out.println(dm.pairs(DefectManager.DEATH).count());

        NamedData mpPhaseOfMpAngle = dm.getNamedData(
                p -> p.mpPhase(),
                p -> p.mpAngle().deg(),
                DefectManager.DEATH,
                "",
                15
        );

        HeatMap.factory(
                "Bacteria",
                "mpAngle",
                "mpPhase",
                mpPhaseOfMpAngle.data,
                900,
                .5,
                2 * Math.PI / 3,
                2 * Math.PI
        );

        ScatterPlot.factory("Bacteria", "mpPhase", "mpAngle", mpPhaseOfMpAngle);

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
        args[8] = "8";//small number for bacteria, larger number for cells
        args[9] = "2";
        args[10] = "20";
        args[11] = "5";

        File from = new File(args[0]);

        String[] files;

        if (from.isDirectory()) {
            String parent = from.toString();
            files = Arrays.stream(new File(parent).list()).map(str -> parent + "//" + str).toArray(String[]::new);
        } else files = new String[]{args[0]};

        DefaultWriter writer = new DefaultWriter(args[1]);

        for (String fileName : files) {
            DefectManager dm = new DefectManager(
                    ReadManager.defaultFileFormat(fileName),
                    getWindow(args),
                    new OpenSpaceTimeBall(timeThreshold(args), distThreshold(args)),
                    timeEdge(args)
            );
            writer.setIdPrefix(fileName.replaceAll("[^0-9]", "") + ".");//Sets the ID prefix to be the numbers in the file name.
            dm.writePairesToFile(writer);
        }

    }

    /**
     * produces a pair file with some default settings for a bunch of cell
     * experiments.
     */
    public static void cellExperiments() throws IOException {

//        for (int time = 1; time < 3; time++)
//            for (double dist = 6; dist <= 18; dist += 2) {
//                DefectManager dm = DefaultData.bacteria();//(time, dist);
//                BirthAndDeathTracker ct = new BirthAndDeathTracker(dm);
//                ct.angleNearFusion(DefectManager.BIRTH, 20);
//                ct.distanceOfFrame(60, 5);
        DefectManager dm = DefaultData.sampleDataSet();//.sampleDataSet();//(time, dist);
        
        try (FormatedFileWriter ffw = new DefaultWriter("SamplePairs.csv")) {
            dm.writePairesToFile(ffw);
        }
//            }

    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

//        bacteriaWork();
        cellExperiments();
//        parseArgs(args);
    }

}
