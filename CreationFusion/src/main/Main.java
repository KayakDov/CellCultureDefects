package main;

import GeometricTools.Angle;
import snapDefects.SpaceTemp;
import GeometricTools.Rectangle;
import ReadWrite.ReadManager;
import ReadWrite.FormatedFileWriter;
import defectManagement.DefectManager;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author E. Dov Neimand
 */
public class Main {

    public static void createPosFile() {
        BufferedWriter bw = null;
        try {
            ReadManager rm = ReadManager.defaultFileFormat("PlusAndMinusTM.csv", new Rectangle(900, 0, 900, 900));
            bw = new BufferedWriter(new FileWriter("PlusTM.csv"));
            ReadManager.Reader reader = rm.getReader();
            String line;
            while ((line = reader.readLine()) != null)
                if (rm.chargeFrom(line)) bw.write(line + "\n");
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
    public static void parseArgs(String[] args) {

        args = new String[12];
        args[0] = "PlusAndMinusTM.csv";
        args[1] = "RPE1_pairs.csv";
        args[2] = "window";
        args[3] = "900";
        args[4] = "0";
        args[5] = "900";
        args[6] = "900";
        args[7] = "threshold";
        args[8] = "35";
        args[9] = "3";
        args[10] = "50";
        args[11] = "5";
        
        ReadManager ff = ReadManager.defaultFileFormat(args[0], getWindow(args));

        DefectManager dm = new DefectManager(ff, timeThreshold(args), distThreshold(args), timeEdge(args), distEdge(args));
        
        dm.loadLifeCourses();

        FormatedFileWriter ffw = FormatedFileWriter.defaultWriter(args[1]);

        dm.pairs(DefectManager.BIRTH).forEach(sd -> ffw.writeLine(sd));

        dm.pairs(DefectManager.DEATH).forEach(sd -> ffw.writeLine(sd));
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

//        ReadManager rm = ReadManager.defaultFileFormat("PlusAndMinusTM.csv", new Rectangle(900, 0, 900, 900));
//
//        DefectManager dm = new DefectManager(rm, 2, 10, 6, 45);
//        
//        
//
//        dm.loadLifeCourses();
//        
//        
//        System.out.println(dm.pairedPos(DefectManager.DEATH).mapToDouble(pos -> pos.fuseUpConsistent(DefectManager.DEATH, 10)).average());//TODO: fix fuse uyp to average
//        
//        boolean event = DefectManager.DEATH;
//                
//        System.out.println(Angle.average(dm.pairedPos(event).map(pos -> pos.avgAnglePRel(event, 20))));
//        System.out.println(Angle.stdDev(() -> dm.pairedPos(event).map(pos -> pos.avgAnglePRel(event, 20)))/Math.PI + " pi");
        

        parseArgs(args);
    }

}

