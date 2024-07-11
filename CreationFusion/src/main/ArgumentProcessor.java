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

    public final File readFrom, writeTo;

    public ArgumentProcessor(String[] args) {

        readFrom = new File(args[0]);
        writeTo = new File(args[1]);

        if (!readFrom.canRead()) throw new SourceNotFound(readFrom);
    }

    /**
     * Returns the appropriate argument container for the arguments passed.
     * @param args
     * @return 
     */
    public static ArgumentProcessor getFrom(String[] args){
        if(new File(args[1]).isDirectory()) return new ArgsCreatePictures(args);
        else return new ArgsReadSnapsWritePairs(args);
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
}
