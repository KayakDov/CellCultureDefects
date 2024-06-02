package ReadWrite;

import SnapManagement.PairSnDef;
import SnapManagement.PairedSnDef;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;

/**
 * Designed to read a file of defect pairs.
 *
 * @author E. Dov Neimand
 */
public class PairReadManager extends SpreadsheetReadManager {

    public final String frame, posID, posX, posY, posAng, negID, negX, negY, negAng, fuseUp, isBirth, posVelAng;

    /**
     * The constructor with the names of the various important columns.
     *
     * @param frame What frame is it?
     * @param posID The ID of the positive defect.
     * @param posX The x position of the positive defect.
     * @param posY The y position of the positive defect.
     * @param posAng The angle of the positive defect.
     * @param negID The ID of the negative defect.
     * @param negX The x position of the negative defect.
     * @param negY The y position of the negative defect.
     * @param negAng The angle of the negative defect (first of 3 columns)
     * @param fuseUp Is the angle fuse up?
     * @param isBirth Is this a creation or a fusion
     * @param fileName The name of the file
     * @param delimiter The delimiter for the file.
     */
    public PairReadManager(String frame, String posID, String posX, String posY, String posAng, String negID, String negX, String negY, String negAng, String fuseUp, String isBirth, String posVelAng, String fileName, char delimiter) {
        super(fileName, delimiter);
        this.frame = frame;
        this.posID = posID;
        this.posX = posX;
        this.posY = posY;
        this.posAng = posAng;
        this.negID = negID;
        this.negX = negX;
        this.negY = negY;
        this.negAng = negAng;
        this.fuseUp = fuseUp;
        this.posVelAng = posVelAng;
        this.isBirth = isBirth;
    }

    /**
     * Creates a PairReadManager using the default file format.
     *
     * @param fileName The name of the file.
     * @return A default paired read manager.
     */
    public static PairReadManager defaultFileFormat(String fileName) {
        return new PairReadManager(
                "FRAME",
                "plus_id",
                "xp",
                "yp",
                "angp1",
                "min_id",
                "xm",
                "ym",
                "angm1",
                "fuse_up",
                "p_vel_angle",
                "creation",
                fileName,
                ',');
    }

    /**
     * All the pairs of the two defects at the given line.
     *
     * @param lineIndex The line of the desired pair set.
     * @return All the pairs that match those at the given line.
     */
    public List<PairSnDef> pairSetContainingLine(int lineIndex) {
        String line = lines().skip(lineIndex - 2).findFirst().get();
        PairSnDef target = fromRow(line);
        return pairs().filter(pair -> target.samePairDifferentTime(pair)).collect(Collectors.toList());
    }


    /**
     * Gets the pair from the given row.
     * @param row The row that defines the pair.
     * @return The pair from the given row.
     */
    public PairSnDef fromRow(String row){
        return new PairBuilder(row).from();
    }
    private class PairBuilder {

        private String[] split;

        /**
         * Builds a pair.
         *
         * @param row The row to build the pair from.
         */
        public PairBuilder(String row) {
            split = row.split(delimiter + "");
        }

        /**
         * Gets the double in the given column.
         *
         * @param col The column of the desired double.
         * @return The double at the desired column.
         */
        public double getDouble(String col) {
            return getDouble(indexOf(col));
        }

        /**
         * Gets the double in the given column.
         *
         * @param col The column of the desired double.
         * @return The double at the desired column.
         */
        public double getDouble(int col) {
            return Double.parseDouble(split[col]);
        }

        /**
         * Gets the int in the given column.
         *
         * @param col The desired column.
         * @return The int in the column.
         */
        public int getInt(String col) {
            return Integer.parseInt(split[indexOf(col)]);
        }

        /**
         * Gets the bool in the given column.
         *
         * @param col The desired column.
         * @return The boolean in the column.
         */
        public boolean getBool(String col) {
            return Boolean.parseBoolean(split[indexOf(col)]);
        }

        /**
         * Paired snap defects from a row.
         *
         * @param row The row.
         * @return The pair of snap defects.
         */
        private PairSnDef from() {

            int frameInd = getInt(frame);

            return new PairSnDef(
                    new PosSnapDefect(
                            getDouble(posX),
                            getDouble(posY),
                            frameInd,
                            getInt(posID),
                            getDouble(posAng)
                    ),
                    new NegSnapDefect(
                            getDouble(negX),
                            getDouble(negY),
                            frameInd,
                            getInt(negID),
                            getDouble(negAng),
                            getDouble(indexOf(negAng) + 1),
                            getDouble(indexOf(negAng) + 2)
                    )
            );
        }
    }

    /**
     * All the pa pairs in the file.
     *
     * @return All the pa pairs in the file.
     */
    public Stream<PairSnDef> pairs() {
        return super.lines().map(line -> new PairBuilder(line).from());
    }

    public class Reader extends SpreadsheetReadManager.Reader {

        public Reader() throws FileNotFoundException {
        }

        /**
         * reads the row into a pair.
         *
         * @return A pair.
         */
        public PairSnDef readPair() {
            try {
                String row = readLine();
                if (row == null) return null;
                return new PairBuilder(row).from();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
