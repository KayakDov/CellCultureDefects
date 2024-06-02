package main;

import GeometricTools.ProximityMetric;
import GeometricTools.Rectangle;
import ReadWrite.ReadManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import snapDefects.SpaceTemp;

/**
 *
 * @author edov
 */
public class ArgumentProcessor {

    public final Rectangle window;
    public final ProximityMetric thresholds;
    public final int timeToEdge;
    public final File readFrom, writeTo;

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

    public ArgumentProcessor(String[] args) {
        this.window = getWindow(args);
        thresholds = new ProximityMetric(distThreshold(args), timeThreshold(args));
        timeToEdge = timeEdge(args);

        readFrom = new File(args[0]);
        writeTo = new File(args[1]);

        if (!readFrom.canRead()) throw new SourceNotFound(readFrom);
    }

    public static class SourceNotFound extends RuntimeException {

        /**
         * The constructor.
         *
         * @param fileName The name of the file that can't be read.
         */
        public SourceNotFound(File fileName) {

            File parent = fileName.getParentFile();

            if (parent == null)
                parent = new File(System.getProperty("user.dir"));

            System.err.println("\nThe file " + fileName + " is not where you are looking for it.\n"
                    + "You seem to be looking in " + parent + "\n"
                    + "which is " + (parent.isDirectory() ? "" : " not ") + "a folder.\n"
                    + (parent.isDirectory() ? "It contains " + Arrays.toString(parent.list()) : "")
                    + " but not the file you are looking for.");

        }
    }

    /**
     * Some arguments for the bacteria file.
     *
     * @return Some arguments for the bacteria file.
     */
    public static String[] defaultBacteriaArgs() {
        return new String[]{
            "PlusAndMinusTM6.csv",
            "output.csv",
            "threshold",
            "20",
            "3",
            "30",
            "2",
            "window",
            "900",
            "0",
            "900",
            "900"
        };
    }
}
