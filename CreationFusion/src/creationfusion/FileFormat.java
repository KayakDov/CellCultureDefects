package creationfusion;

import GeometricTools.Rectangle;
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
            POSITION_Y = 6, POSITION_Z = 7, FRAME = 9, RADIUS = 10,
            VISIBILITY = 11, MANUAL_SPOT_COLOR = 12, MEAN_INTENSITY_CH1 = 13,
            MEDIAN_INTENSITY_CH1 = 14, MIN_INTENSITY_CH1 = 15, MAX_INTENSITY_CH1 = 16,
            TOTAL_INTENSITY_CH1 = 17, STD_INTENSITY_CH1 = 18, CONTRAST_CH1 = 19,
            SNR_CH1 = 20, x_img1 = 23,
            y_img1 = 24;

    /**
     * Useful default indices.
     */
    public final static int TRACK_ID = 3, POSITION_T = 8, x_img = 21, 
            y_img = 22, ang1 = 25, ang2 = 26, ang3 = 27, CHARGE = 28;

    private final static char DELINEATOR = ',';

    /**
     * An instance with the default file specs.
     */
    public static FileFormat DEFAULT = new FileFormat(x_img, y_img, TRACK_ID, POSITION_T, CHARGE, ang1, DELINEATOR, Rectangle.COORD_PLANE);

    public final int x, y, id, time, angle1, charge;
    public final char delineator;
    private Rectangle window;

    /**
     * Sets the indices of the needed values in the file.
     *
     * @param x The index of the x value.
     * @param y The index of the y value.
     * @param id The index of the tracking id.
     * @param time The index of the time.
     * @param charge The index of the charge.
     * @param angle1 The index of the first angle.  For negative charges, 
     * we assume angle 2 and 3 immediately follow angle 1.
     * @param delineator The delineator that separates values.
     * @param window The window within the file to be read.
     */
    public FileFormat(int x, int y, int id, int time, int charge, int angle1, char delineator, Rectangle window) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.time = time;
        this.charge = charge;
        this.angle1 = angle1;
        this.delineator = delineator;
        this.window = window;
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
        return doubleAt(line, CHARGE) > 0;
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
        
        double lineX = Double.parseDouble(split[x]);
        double lineY = Double.parseDouble(split[y]);
        int lineT = (int) Double.parseDouble(split[time]);
        int lineID = "".equals(split[id])
                ? SnapDefect.NO_ID
                : (int) Double.parseDouble(split[id]);
        boolean lineCharge = Double.parseDouble(split[charge]) > 0;
        double lineAng1 = Double.parseDouble(split[angle1]);
        if(lineCharge) return new PositiveSnDefect(lineX, lineY, lineT, lineID, lineAng1);
        
        double lineAng2 = Double.parseDouble(split[angle1 + 1]);
        double lineAng3 = Double.parseDouble(split[angle1 + 2]);
        return new NegSnapDefect(lineX, lineY, lineT, lineID, lineAng1, lineAng2, lineAng3);
    }

    /**
     * Resets the window.
     * @param window 
     * @return This file format.
     */
    public FileFormat setWindow(Rectangle window) {
        this.window = window;
        return this;
    }
    
    public boolean inWindow(String line){
        return window.contains(doubleAt(line, this.x), doubleAt(line, this.y));
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
     * The window of interest. Data outside this window should not be considered.
     * @return The window of interest.
     */
    public Rectangle getWindow() {
        return window;
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
     * Gets the double at the given index in the line.
     * @param str The line.
     * @param index The index of the formated line for the desired double.
     * @return The value at the given index in the formated line.
     */
    private double doubleAt(String str, int index){
        int startTerm = charCount(str, 0, index);
        return Double.parseDouble(str.substring(startTerm + 1, charCount(str, startTerm, 1)));
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
                String tempLine = super.readLine();
                while(!inWindow(tempLine)) tempLine = readLine();
                return lastLine = tempLine;
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
