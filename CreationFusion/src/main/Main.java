package main;

import Animation.DrawDefects;
import ReadWrite.SpreadsheetReadManager;
import Annalysis.BirthAndDeathTracker;
import ReadWrite.DefaultWriter;
import ReadWrite.FormatedFileWriter;
import ReadWrite.PairReadManager;
import ReadWrite.ReadManager;
import SnapManagement.PairSnDef;
import defectManagement.DefectManager;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author E. Dov Neimand
 */
public class Main {

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
        ArgumentProcessor ap = new ArgumentProcessor(args);

        if (ap.writeTo.isDirectory()) {
            PairReadManager prm = PairReadManager.defaultFileFormat(args[0]);
            List<PairSnDef> pairs = prm.pairSetContainingLine(Integer.parseInt(args[2]));
            DrawDefects.draw("PosDef.png", "NegDef.png", args[1], pairs);
        } else {
            DefectManager dm = ap.readFrom.isDirectory()
                    ? new DefectManager(
                            ap.readFrom,
                            ap.window,
                            ap.thresholds,
                            ap.timeToEdge
                    )
                    : new DefectManager(
                            ReadManager.defaultFileFormat(ap.readFrom),
                            ap.window,
                            ap.thresholds,
                            ap.timeToEdge
                    );

            try (DefaultWriter writer = new DefaultWriter(ap.writeTo)) {
                dm.writePairesToFile(writer);
            }

//            checkOutputFile(ap.writeTo);
        }
    }

    /**
     * Keep this method empty unless simple tests need to be conducted on the
     * file created.
     *
     * @param fileName "plus_id"
     */
    private static void checkOutputFile(File fileName) {
        SpreadsheetReadManager ssr = new SpreadsheetReadManager(fileName);

        ssr.lines().limit(10).forEach(line -> System.out.println(line));
    }

    /**
     * produces a pair file with some default settings for a bunch of cell
     * experiments.
     */
    public static void defaults() throws IOException {

        DefectManager dm = DefaultData.allCells_1_10_11_12_14_15_19();//(time, dist);
        
        new BirthAndDeathTracker(dm).longevity(100, 1, x -> 1800/x);
        
        
//        try (FormatedFileWriter ffw = new DefaultWriter("BacteriaPairs.csv")) {
//            dm.writePairesToFile(ffw);
//        }

    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {


        defaults();
//        parseArgs(ArgumentProcessor.defaultBacteriaArgs());
//        parseArgs(args);
    }

}
