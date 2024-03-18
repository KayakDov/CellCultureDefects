package main;

import GeometricTools.Angle;
import snapDefects.SpaceTemp;
import GeometricTools.Rectangle;
import ReadWrite.ReadManager;
import ReadWrite.FormatedFileWriter;
import SnapManagement.Defect;
import dataTools.stdDev;
import defectManagement.DefectManager;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import snapDefects.SnapDefect;

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
            throw new RuntimeException("You need the runtime argument window [x] [y] [wifth] [height]");

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

//        args = new String[11];
//        args[0] = "PlusAndMinusTM.csv";
//        args[1] = "RPE1_pairs.csv";
//        args[2] = "window";
//        args[3] = "900";
//        args[4] = "0";
//        args[5] = "1800";
//        args[6] = "900";
//        args[7] = "threshold";
//        args[8] = "3";
//        args[9] = "35";
//        args[10] = "5";
//        args[11] = "50";
        ReadManager ff = ReadManager.defaultFileFormat(args[0], getWindow(args));

        DefectManager dm = new DefectManager(ff, timeThreshold(args), distThreshold(args), timeEdge(args), distEdge(args));

        dm.loadLifeCourses();

        FormatedFileWriter ffw = FormatedFileWriter.defaultWriter(args[1]);

        dm.pairs(DefectManager.BIRTH).forEach(sd -> ffw.writeLine(sd));

        dm.pairs(DefectManager.DEATH).forEach(sd -> ffw.writeLine(sd));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        ReadManager rm = ReadManager.defaultFileFormat("PlusAndMinusTM.csv", new Rectangle(900, 0, 900, 900));

        DefectManager dm = new DefectManager(rm, 2, 30, 6, 10);

        dm.loadLifeCourses();
                
        System.out.println(Angle.average(dm.pairedPos(false).map(pos -> pos.avgAnglePRel(true, 20))).deg());
        

//        parseArgs(args);
    }

}


/*[
(1153.899953201433, 836.4295373908809), t = 0, charge = pos, id = 9, 
(1155.8352648191753, 795.5147897261858), t = 1, charge = pos, id = 9, 
(1157.7705764369177, 840.3261800256138), t = 2, charge = pos, id = 9, 
(1157.7705764369177, 853.9644292471788), t = 3, charge = pos, id = 9, 
(1163.5765112901452, 832.532894756148), t = 4, charge = pos, id = 9, 
(1161.6411996724025, 822.7912881693159), t = 5, charge = pos, id = 9, 
(1165.5118229078876, 826.6879308040487), t = 6, charge = pos, id = 9, 
(1165.5118229078876, 840.3261800256138), t = 7, charge = pos, id = 9, null, null, 
(1175.1883809965996, 838.3778587082473), t = 10, charge = pos, id = 9, 
(1179.0590042320844, 832.532894756148), t = 11, charge = pos, id = 9, 
(1177.123692614342, 828.6362521214152), t = 12, charge = pos, id = 9, 
(1180.9943158498268, 820.8429668519494), t = 13, charge = pos, id = 9, 
(1186.8002507030542, 818.894645534583), t = 14, charge = pos, id = 9, 
(1165.5118229078876, 776.0315765525214), t = 15, charge = pos, id = 9]
 */
