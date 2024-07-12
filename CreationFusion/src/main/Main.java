package main;

import ReadWrite.SpreadsheetReadManager;
import Annalysis.BirthAndDeathTracker;
import GeometricTools.Rectangle;
import ReadWrite.DefaultWriter;
import ReadWrite.FormatedFileWriter;
import ReadWrite.PairReadManager;
import ReadWrite.ReadManager;
import SnapManagement.PairSnDef;
import defectManagement.DefectManager;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import snapDefects.NegSnapDefect;
import snapDefects.SnapDefect;

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

        ArgumentProcessor ap = ArgumentProcessor.getFrom(args);
                
        if (ap instanceof ArgsCreatePictures) {
            PairReadManager prm = PairReadManager.defaultFileFormat(ap.readFrom);
            Stream<PairSnDef> pairs = prm.pairSetContainingLine(((ArgsCreatePictures)ap).targetRow);
//            DrawDefects.drawDefectPairs(100, 800, 800, ap.writeTo, pairs.toList());  TODO: This may need to be uncomented.
            
        } else {
            ArgsReadSnapsWritePairs argsForSnaps = (ArgsReadSnapsWritePairs)ap;
            DefectManager dm = argsForSnaps.readFrom.isDirectory()
                    ? new DefectManager(
                            argsForSnaps.readFrom,
                            argsForSnaps.window,
                            argsForSnaps.thresholds,
                            argsForSnaps.timeToEdge
                    )
                    : new DefectManager(
                            ReadManager.defaultFileFormat(ap.readFrom),
                            argsForSnaps.window,
                            argsForSnaps.thresholds,
                            argsForSnaps.timeToEdge
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

        DefectManager dm = DefaultData.bacteria();//(time, dist);
                
//        new DrawDefects(1600, 1600, 100).draw(
//                new File("/home/edov/projects/CreationFusionCount/CreationFusion/images/output/output"), 
//                new Rectangle(900, 0, 900, 900), 
//                new File("/home/edov/projects/VictorData/HBEC/s2(120-919)/Trans__605.tif"),
//                dm.snaps().filter(snap -> snap.loc.getTime() == 604).toArray(SnapDefect[]::new)
//        );
        
//        new BirthAndDeathTracker(dm).negTailAngleAtDeath(50);
        
        
//        try (FormatedFileWriter ffw = new DefaultWriter("BacteriaPairs.csv")) {
//            dm.writePairesToFile(ffw);
//        }

    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        System.out.println("main.Main.main()");
//        defaults();
//        parseArgs(ArgsCreatePictures.defaultPictureCreationArgs());
//        parseArgs(args);
    }

}
