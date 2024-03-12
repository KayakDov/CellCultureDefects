package creationfusion;

import GeometricTools.Rectangle;
import ReadWrite.ReadManager;
import ReadWrite.FormatedFileWriter;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author E. Dov Neimand
 */
public class main {

    /**
     * If the list of arguments contains the word window, then the four 
     * numbers after the word window are taken in as x, y, width, height, of the
     * window.
     * @param args The command line arguments.
     * @param dm The defect manager.
     */
    private static Rectangle getWindow(String[] args){
        int windowIndex = ReadManager.indexOf(args, "window");
        if(windowIndex == -1) return Rectangle.COORD_PLANE;
        
        double x = Double.parseDouble(args[windowIndex + 1]);
        double y = Double.parseDouble(args[windowIndex + 2]);
        double width = Double.parseDouble(args[windowIndex + 3]);
        double height = Double.parseDouble(args[windowIndex + 4]);
        
        return new Rectangle(x, y, width, height);
    }
    
    /**
     * The distance threshold.  It should come after the word threshold.
     * @param args The list of arguments.
     * @return The distance threshold.
     */
    public static double distThreshold(String[] args){
        int distIndex = ReadManager.indexOf(args, "threshold");
        if(distIndex == -1) return SpaceTemp.defaultDistanceThreshold;
        return Integer.parseInt(args[distIndex + 1]);
    }
    /**
     * The distance threshold.  It should come after the word threshold.
     * @param args The list of arguments.
     * @return The distance threshold.
     */
    public static int timeThreshold(String[] args){
        int distIndex = ReadManager.indexOf(args, "threshold");
        if(distIndex == -1) return SpaceTemp.defaultTimeThreshold;
        return Integer.parseInt(args[distIndex + 2]);
    }
    
    /**
     * parses the arguments
     * @param args The user arguments.
     */
    public static void parseArgs(String[] args){
        
        ReadManager ff = ReadManager.defaultFileFormat(args[0])
                .setWindow(getWindow(args));
        
        DefectManager dm = new DefectManager(ff, timeThreshold(args), distThreshold(args));
        
        dm.loadLifeCourses();
        
        FormatedFileWriter ffw = FormatedFileWriter.defaultWriter(args[1]);
        
        dm.pairs(DefectManager.BIRTH).forEach(sd -> ffw.writeLine(sd));
        
        dm.pairs(DefectManager.DEATH).forEach(sd -> ffw.writeLine(sd));
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
//        args = new String[7];
//        args[0] = "PlusAndMinusTM.csv";
//        args[1] = "RPE1_pairs.csv";
//        args[2] = "window";
//        args[3] = "900";
//        args[4] = "0";
//        args[5] = "1800";
//        args[6] = "900";
        
        parseArgs(args);


//        System.out.println((-3 % (2*Math.PI)) - Math.PI);
        
        
    }

}
