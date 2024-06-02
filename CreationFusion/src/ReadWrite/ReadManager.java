package ReadWrite;

import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;
import snapDefects.SnapDefect;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author E. Dov Neimand Specifications for file structure.
 */
public class ReadManager extends SpreadsheetReadManager {

    /**
     * An instance with the default file specs.
     *
     * @param fileName The name of the file with default column headers.
     *
     * @return A File format with default column names.
     */
    public static ReadManager defaultFileFormat(String fileName) {
        return new ReadManager("x_img", "y_img", "TRACK_ID", "POSITION_T", "charge", "ang1", ',', fileName);
    }

    /**
     * Finds the index of the key in the array.
     *
     * @param array The array containing the key.
     * @param key The element in the array whose index is desired.
     * @return The index of the desired element. If it's not in the array, -1 is
     * returned.
     */
    public static int indexOf(String[] array, String key) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(key)) return i;
        return -1;
    }

    /**
     * The names of the columns.
     */
    private final String x, y, id, time, charge, angle;

    /**
     * Sets the indices of the needed values in the file.
     *
     * Note, if multiple files are loaded into this reader, it's important that
     * they all have the same endTime. Otherwise, the file with early endTimes
     * will end up with all its defects being marked as fused during its earlier
     * endTime.
     *
     * @param x The name of the x column.
     * @param y The name of the y column.
     * @param id The name of the ID column.
     * @param time The name of the time column.
     * @param angle The name of the first angle column.
     * @param charge The name of the charge column.
     * @param delimiter The delimiter that separates values.
     * @param fileName The name of a file whose first line has the column names
     * on it.
     */
    public ReadManager(String x, String y, String id, String time, String charge, String angle, char delimiter, String fileName) {
        super(fileName, delimiter);
        this.x = x;
        this.y = y;
        this.id = id;
        this.time = time;
        this.charge = charge;
        this.angle = angle;
     
    }

    /**
     * The number of frames in the file.
     *
     * @return The number of frames in the file.
     */
    public int numFrames() {
        
        try(Reader reader = getReader()){
            int numFrames = 0;
            String nextLine;
            while ((nextLine = reader.readLine()) != null)
                numFrames = time(nextLine);
            return numFrames;
        } catch (IOException ex) {
            Logger.getLogger(ReadManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets a file reader for a file with this default arrangement.
     * Don't forget to close the reader when you're done with it.
     * @return A file reader.
     */
    @Override
    public Reader getReader() {
        try {
            return new Reader();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Finds the charge of the line, if the line is properly formatted.
     *
     * @param line The line, representing a SnapDefect, for which the charge is
     * desired.
     * @return The charge of the line.
     */
    public boolean chargeFrom(String line) {
        return doubleAt(line, charge) > 0;
    }

    /**
     * The id of the line.
     *
     * @param line The line for which the ID is desired.
     * @return The ID of the line.
     */
    public int IDFrom(String line) {
        try {
            return (int) doubleAt(line, id);
        } catch (NumberFormatException nfe) {
            return SnapDefect.NO_ID;
        }
    }

    /**
     * Parses a line from a line of a formated file constructs a SnapDefect from
     * it. The line needs to be separated by comas and the indices of its values
     * must match the public final static ints above.
     *
     * @param row The formatted line to be parsed.
     * @return The snap defect generated from the line.
     */
    public SnapDefect snapDefect(String row) {
        try {
            if (row == null) return null;
            String[] split = row.split("" + delimiter);

            double lineX = Double.parseDouble(split[indexOf(x)]);
            double lineY = Double.parseDouble(split[indexOf(y)]);
            int lineT = (int) Double.parseDouble(split[indexOf(time)]);
            int lineID = "".equals(split[indexOf(id)])
                    ? SnapDefect.NO_ID
                    : (int) Double.parseDouble(split[indexOf(id)]);
            boolean lineCharge = Double.parseDouble(split[indexOf(charge)]) > 0;
            double lineAng1 = Double.parseDouble(split[indexOf(angle)]);
            if (lineCharge)
                return new PosSnapDefect(lineX, lineY, lineT, lineID, lineAng1);

            double lineAng2 = Double.parseDouble(split[indexOf(angle) + 1]);
            double lineAng3 = Double.parseDouble(split[indexOf(angle) + 2]);

            return new NegSnapDefect(lineX, lineY, lineT, lineID, lineAng1, lineAng2, lineAng3);

        } catch (NumberFormatException nfe) {
            throw new NumberFormatException(nfe.getMessage()
                    + "\n Original line is: " + row);
        }

    }

    /**
     * Does the line have a tracking ID.
     *
     * @param line The line in question.
     * @return True if it has a tracking ID, false otherwise.
     */
    public boolean isTracked(String line) {
        return !getCol(line, charge).equals("");
    }

    /**
     * Gets the double at the given index in the line.
     *
     * @param str The line.
     * @param colName The index of the formatted line for the desired double.
     * @return The value at the given index in the formatted line.
     */
    private double doubleAt(String str, String colName) {
        
        String subStr = super.getCol(str, colName);
        if (subStr.isEmpty()) return Double.NaN;
        return Double.parseDouble(subStr);
    }

    /**
     * Gets the time of the line.
     *
     * @param string The string that has the desired time.
     * @return The time at the time index in the string.
     */
    public int time(String string) {
        return (int) doubleAt(string, time);

    }


    /**
     * All the snap defects in the file.
     *
     * @return All the snap defects in the file.
     */
    public Stream<SnapDefect> snapDefects() {
        return lines().map(line -> snapDefect(line));
    }

    /**
     * A buffered reader for the file. This reader will skip lines outside the
     * window, and can go back one line, but never further.
     */
    public class Reader extends SpreadsheetReadManager.Reader {

        public Reader() throws FileNotFoundException {
            super();
        }

        /**
         * reads the next snap defect
         *
         * @return The next snap Defect.
         */
        public SnapDefect readSnap() {
            try {
                return snapDefect(readLine());
            } catch (IOException ex) {
                Logger.getLogger(ReadManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }

    }

}
