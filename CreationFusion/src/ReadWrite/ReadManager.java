package ReadWrite;

import GeometricTools.Rectangle;
import defectManagement.DefectManager;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;
import snapDefects.SnapDefect;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author E. Dov Neimand Specifications for file structure.
 */
public class ReadManager {
//  Dfault values no longer needed.
//    /**
//     * Useless default indices.
//     */
//    public final static int LABEL = 1, ID = 2, QUALITY = 4, POSITION_X = 5,
//            POSITION_Y = 6, POSITION_Z = 7, FRAME = 9, RADIUS = 10,
//            VISIBILITY = 11, MANUAL_SPOT_COLOR = 12, MEAN_INTENSITY_CH1 = 13,
//            MEDIAN_INTENSITY_CH1 = 14, MIN_INTENSITY_CH1 = 15, MAX_INTENSITY_CH1 = 16,
//            TOTAL_INTENSITY_CH1 = 17, STD_INTENSITY_CH1 = 18, CONTRAST_CH1 = 19,
//            SNR_CH1 = 20, x_img1 = 23,
//            y_img1 = 24;
//
//    /**
//     * Useful default indices.
//     */
//    public final static int TRACK_ID = 3, POSITION_T = 8, x_img = 21,
//            y_img = 22, ang1 = 25, ang2 = 26, ang3 = 27, CHARGE = 28;
//
//    private final static char DELINEATOR = ',';

    /**
     * An instance with the default file specs.
     * @param fileName The name of the file with default column headers.
     * @param window The window the snap defects must be in.
     * @return A File format with default column names.
     */
    public static ReadManager defaultFileFormat(String fileName, Rectangle window) {
        return new ReadManager("x_img", "y_img", "TRACK_ID", "POSITION_T", "charge", "ang1", ',', window, fileName);
    }

    public final int x, y, id, time, angle1, charge;
    public final char delimiter;
    private Rectangle window;
    public String fileName;

    /**
     * Sets the indices of the needed values in the file.
     *
     * @param x The index of the x value.
     * @param y The index of the y value.
     * @param id The index of the tracking id.
     * @param time The index of the time.
     * @param charge The index of the charge.
     * @param angle1 The index of the first angle. For negative charges, we
     * assume angle 2 and 3 immediately follow angle 1.
     * @param delimiter The delimiter that separates values.
     * @param window The window within the file to be read.
     */
    public ReadManager(int x, int y, int id, int time, int charge, int angle1, char delimiter, Rectangle window, String fileName) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.time = time;
        this.charge = charge;
        this.angle1 = angle1;
        this.delimiter = delimiter;
        this.window = window;
        this.fileName = fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
     * Sets the indices of the needed values in the file.
     *
     * @param line A string containing the names of the columns seperated by the
 delimiter.
     * @param x The name of the x column.
     * @param y The name of the y column.
     * @param id The name of the ID column.
     * @param time The name of the time column.
     * @param charge The name of the charge column.
     * @param angle1 The name of the first angle column.
     * @param delimiter The delimiter that separates values.
     * @param window The window within the file to be read.
     */
    public ReadManager(String line, String x, String y, String id, String time, String charge, String angle1, char delimiter, Rectangle window, String fileName) {
        String[] split = line.split("" + delimiter);
        this.x = indexOf(split, x);
        this.y = indexOf(split, y);
        this.id = indexOf(split, id);
        this.time = indexOf(split, time);
        this.charge = indexOf(split, charge);
        this.angle1 = indexOf(split, angle1);
        this.delimiter = delimiter;
        this.window = window;
        this.fileName = fileName;
    }

    
    
    /**
     * Sets the indices of the needed values in the file.
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
     * @param window The window within the file to be read.
     */
    public ReadManager(String x, String y, String id, String time, String charge, String angle, char delimiter, Rectangle window, String fileName) {
        

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String[] split = br.readLine().split("" + delimiter);
            this.x = indexOf(split, x);
            this.y = indexOf(split, y);
            this.id = indexOf(split, id);
            this.time = indexOf(split, time);
            this.charge = indexOf(split, charge);
            this.angle1 = indexOf(split, angle);
            this.delimiter = delimiter;
            this.window = window;
            this.fileName = fileName;

        } catch (Exception ex) {
            Logger.getLogger(ReadManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);

        }        
    }

    /**
     * Gets a file reader for a file with this default arangement.
     *
     * @return
     */
    public Reader getReader() {
        try {
            return new Reader(fileName);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadManager.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
    
    
    /**
     * Gets a file reader for a file with this default format.  The readLine
     * command will only look at line with the given charge. It will skip
     * lines in the beginning without that charge and return null for lines
     * after that without the desired charge.
     * 
     *
     * @param charge The desired charge.
     * @return A reader for lines with the given charge.
     */
    @SuppressWarnings("empty-statement")
    public Reader getReader(boolean charge) {
        try {
            
            
            Reader reader = new Reader(fileName){
                @Override
                public String readLine() {
                    String line = super.readLine(); 
                    return line != null && chargeFrom(line) == charge? line: null;
                }
                
            };
            while(reader.readLine() == null);
            reader.backOneLine();
            return reader;
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadManager.class
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
     * it. The line needs to be separated by comas and the indices of its
     * values must match the public final static ints above.
     *
     * @param line The formatted line to be parsed.
     * @return The snap defect generated from the line.
     */
    public SnapDefect snapDefect(String line) {
        if (line == null) return null;
        String[] split = line.split("" + delimiter);

        double lineX = Double.parseDouble(split[x]);
        double lineY = Double.parseDouble(split[y]);
        int lineT = (int) Double.parseDouble(split[time]);
        int lineID = "".equals(split[id])
                ? SnapDefect.NO_ID
                : (int) Double.parseDouble(split[id]);
        boolean lineCharge = Double.parseDouble(split[charge]) > 0;
        double lineAng1 = Double.parseDouble(split[angle1]);
        if (lineCharge)
            return new PosSnapDefect(lineX, lineY, lineT, lineID, lineAng1);

        double lineAng2 = Double.parseDouble(split[angle1 + 1]);
        double lineAng3 = Double.parseDouble(split[angle1 + 2]);
        return new NegSnapDefect(lineX, lineY, lineT, lineID, lineAng1, lineAng2, lineAng3);
    }

    /**
     * Resets the window.
     *
     * @param window
     * @return This file format.
     */
    public ReadManager setWindow(Rectangle window) {
        this.window = window;
        return this;
    }

    /**
     * Is the SnapDefect described on the line in the window?
     * @param line The line whose SnapDefect is to be checked for membership in the window.
     * @return True if the SnapDefect on the line is in the window, false otherwise.
     */
    public boolean inWindow(String line) {
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
        return !line.substring(first, charCount(line, first, 1) - 1)
                .equals("");
    }

    /**
     * The window of interest. Data outside this window should not be
     * considered.
     *
     * @return The window of interest.
     */
    public Rectangle getWindow() {
        return window;
    }
    

    /**
     * The index immediately after the nth comma in the string after start.
     *
     * @param string The string for whom the index is desired.
     * @param start Where to start counting commas.
     * @param n the number of commas.
     * @return The index of the nth comma in the string after start index.
     */
    private int charCount(String string, int start, int n) {
        if (string == null)
            throw new NullPointerException("You passed a null string.");
        int i = start;
        int count = 0;
        for (; i < string.length() && count < n; i++)
            if (string.charAt(i) == delimiter) count++;
        if (count < n) return string.length() + 1;
        return i;
    }

    /**
     * Gets the double at the given index in the line.
     *
     * @param str The line.
     * @param index The index of the formatted line for the desired double.
     * @return The value at the given index in the formatted line.
     */
    private double doubleAt(String str, int index) {
        int startTerm = charCount(str, 0, index);
        String subStr = str.substring(startTerm, charCount(str, startTerm, 1) - 1);
        if(subStr.isEmpty()) return Double.NaN;
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
     * All the lines of the file.
     *
     * @return All the lines of the file.
     */
    public Stream<String> lines() {
        try {
            return Files.lines(Paths.get(fileName)).skip(1).filter(line -> inWindow(line));
        } catch (IOException ex) {
            Logger.getLogger(DefectManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
        
    /**
     * All the snap defects in the file.
     * @return All the snap defects in the file.
     */
    public Stream<SnapDefect> snapDefects(){
        return lines().parallel().map(line -> snapDefect(line));
    }
    
    /**
     * A stream of the elements of a column of the file.
     * @param index The index of the desired column.  The indices of important
     * columns are saved in this object.
     * @return The values of a column.
     */
    public DoubleStream column(int index){
        return lines().mapToDouble(line -> doubleAt(line, index));
    }
    
    /**
     * All the times.
     * @return All the times.
     */
    public IntStream timeColumn(){
        return column(time).mapToInt(d -> (int)d);
    }

    /**
     * A buffered reader for the file.  This reader will skip lines outside the
     * window, and can go back one line, but never further.
     */
    public class Reader extends BufferedReader {

        private boolean backUp = false;

        /**
         *
         * @param fileName The name of the file to be read.
         * @throws FileNotFoundException
         */
        public Reader(String fileName) throws FileNotFoundException {
            super(new FileReader(fileName));
            try {
                super.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ReadManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private String lastLine;

        @Override
        public String readLine() {
            if (backUp) {
                backUp = false;
                return lastLine;
            }
            try {
                String tempLine = super.readLine();
                while (tempLine != null && !inWindow(tempLine))
                    tempLine = super.readLine();
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
            return ReadManager.this.isTracked(readLine());
        }

        /**
         * Backs the reader up one line.
         */
        public void backOneLine() {
            if (backUp) throw new RuntimeException(
                        "This reader is already backed up.  "
                        + "A reader can not backup more than once.");
        }

        /**
         * Jumps the reader forward to the first line of the given charge.
         *
         * @param charge The desired charge.
         * @return This reader.
         */
        @SuppressWarnings("empty-statement")
        public Reader jumpToCharge(boolean charge) {
            String line;
            while ((line = readLine()) != null && chargeFrom(line) != charge);
            backOneLine();
            return this;
        }

        @Override
        public boolean ready() {

            try {
                return !backUp && super.ready();
            } catch (IOException ex) {
                Logger.getLogger(ReadManager.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }

    }

}
