package creationfusion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author E. Dov Neimand Specifications for file structure.
 */
public class FileFormat {

    /**
     * Useless default indices.
     */
    public final static int LABEL = 1, ID = 2, QUALITY = 4, POSITION_X = 5,
            POSITION_Y = 6, POSITION_Z = 7, RADIUS = 10,
            VISIBILITY = 11, MANUAL_SPOT_COLOR = 12, MEAN_INTENSITY_CH1 = 13,
            MEDIAN_INTENSITY_CH1 = 14, MIN_INTENSITY_CH1 = 15, MAX_INTENSITY_CH1 = 16,
            TOTAL_INTENSITY_CH1 = 17, STD_INTENSITY_CH1 = 18, CONTRAST_CH1 = 19,
            SNR_CH1 = 20, x_img = 21, y_img = 22, FRAME = 9;

    /**
     * Useful default indices.
     */
    public final static int TRACK_ID = 3, POSITION_T = 8, x_img1 = 23,
            y_img1 = 24, ang1 = 25, ang2 = 26, ang3 = 27, CHARGE = 28;

    private final static char DELINEATOR = ',';

    /**
     * An instance with the default file specs.
     */
    public static FileFormat DEFAULT = new FileFormat(x_img1, y_img1, TRACK_ID, POSITION_T, CHARGE, DELINEATOR);

    public final int x, y, id, time, charge;
    public final char delineator;

    /**
     * Sets the indices of the needed values in the file.
     *
     * @param x The index of the x value.
     * @param y The index of the y value.
     * @param id The index of the tracking id.
     * @param time The index of the time.
     * @param charge The index of the charge.
     * @param delineator The delineator that seperates values.
     */
    public FileFormat(int x, int y, int id, int time, int charge, char delineator) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.time = time;
        this.charge = charge;
        this.delineator = delineator;
    }

    /**
     * Gets a file reader for a file with this default arangement.
     *
     * @param fileName The name of the file.
     * @return
     */
    public Reader getReader(String fileName) {
        try {
            return new Reader(fileName);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileFormat.class
                    .getName()).log(Level.SEVERE, null, ex);
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
        return line.substring(line.lastIndexOf(delineator) + 1).equals("0.5");
    }

    /**
     * Parses a line from a line of a formated file constructs a SnapDefect from
     * it. The line needs to be delineated by comas and the indices of its
     * values must match the public final static ints above.
     *
     * @param line The formatted line to be parsed.
     * @return The snap defect generated from the line.
     */
    public SnapDefect snapDefect(String line) {
        if(line == null) return null;
        String[] split = line.split("" + delineator);

        return new SnapDefect(
                Double.parseDouble(split[x]),
                Double.parseDouble(split[y]),
                (int) Double.parseDouble(split[time]),
                "".equals(split[id])
                ? SnapDefect.NO_ID
                : (int) Double.parseDouble(split[id]),
                Double.parseDouble(split[this.charge]) > 0);
    }

    /**
     * Does the line have a tracking ID.
     *
     * @param line The line in question.
     * @return True if it has a tracking ID, false otherwise.
     */
    public boolean isTracked(String line) {
        int first = charCount(line, 0, id);
        return !line.substring(first + 1, charCount(line, first + 1, 1))
                .equals("");
    }

    /**
     * The index of the nth comma in the string after start.
     *
     * @param string The string for whom the index is desired.
     * @param start Where to start counting commas.
     * @param n the number of commas.
     * @return The index of the nth comma in the string after start index.
     */
    private int charCount(String string, int start, int n) {
        int i = start;
        for (int count = 0; i < string.length() && count < n; i++)
            if (string.charAt(i) == delineator) count++;
        return i;
    }

    /**
     * Gets the time of the line.
     *
     * @param string The string that has the desired time.
     * @return The time at the time index in the string.
     */
    public int time(String string) {
        int startTime = charCount(string, 0, time);
        return (int) Double.parseDouble(string.substring(startTime + 1, charCount(string, startTime + 1, 1)));
    }

    
    
    public class Reader extends BufferedReader {

        private boolean backUp = false;
        /**
         *
         * @param fileName The name of the file to be read.
         * @throws FileNotFoundException
         */
        public Reader(String fileName) throws FileNotFoundException {
            super(new FileReader(fileName));
            readLine();
        }


        private String lastLine;

        @Override
        public String readLine() {
            if(backUp){
                backUp = false;
                return lastLine;
            }
            try {
                return lastLine = super.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * The snap defect from the line.
         *
         * @return The snap defect from the line.
         */
        public SnapDefect readSnapDefect() {
            String line = readLine();
            if (line == null) return null;
            return snapDefect(readLine());
        }

        /**
         * The charge from the line.
         *
         * @return The charge from the line.
         */
        public boolean readCharge() {
            return chargeFrom(readLine());
        }

        /**
         * Is the next line tracked.
         *
         * @return True if the next line is tracked, false otherwise.
         */
        public boolean isTracked() {
            return FileFormat.this.isTracked(readLine());
        }
        
        /**
         * Backs the reader up one line.
         */
        public void backOneLine(){
            if(backUp) throw new RuntimeException(
                    "This reader is already backed up.  "
                            + "A reader can not backup more than once.");
        }

        @Override
        public boolean ready(){
            
            try { 
                return !backUp && super.ready();
            } catch (IOException ex) {
                Logger.getLogger(FileFormat.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }
        
        

    }

}
