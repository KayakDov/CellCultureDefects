package ReadWrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * A class for reading spreadsheets.
 *
 * @author E. Dov Neimand
 */
public class SpreadsheetReadManager {

    private final HashMap<String, Integer> colIndex;
    protected final char delimiter;
    private final File readFrom;

    /**
     * The index of the requested column.
     *
     * @param colName The name of the column for whom the index is desired.
     * @return The index of the requested column.
     */
    protected int indexOf(String colName) {
        return colIndex.get(colName);
    }

    /**
     * Does this spreadsheet have the requested column.
     *
     * @param colName The name of the column being checked.
     * @return True if the spreadsheet has the column, false otherwise.
     */
    protected boolean hasColumn(String colName) {
        return colIndex.containsKey(colName);
    }

    protected void expectedColumns(String... colNames) {
        String error = "";
        for (String name : colNames)
            error += columnCheck(name);

        if (!error.equals("")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(readFrom))) {
                String firstLine = reader.readLine();
                throw new RuntimeException(error
                        + "The file is not formated as expected.\n"
                        + "Please verify that you are reading the correct file.\n"
                        + "The file you are reading is called " + readFrom.toString() + ".\n"
                        + "The first line is: " + firstLine);
            } catch (IOException ex) {
                Logger.getLogger(ReadManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Checks to see if the column is present. If it is not, an error message is
     * returned.
     */
    private String columnCheck(String colName) {
        return hasColumn(colName) ? "" : "The spreadSheed does not have a " + colName + " column.\n";
    }

    /**
     * A buffered reader for the file.
     */
    public class Reader extends BufferedReader {

        /**
         * The constructor.
         *
         * @throws FileNotFoundException
         */
        public Reader() throws FileNotFoundException {
            super(new FileReader(readFrom));
            try {
                readLine();
            } catch (IOException ex) {
                Logger.getLogger(SpreadsheetReadManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        /**
         * Reads the requested column from the next line. If the next line is
         * null, then null is returned.
         *
         * @param columnName The name of the requested column.
         * @return The String in the requested column.
         * @throws java.io.IOException As readLine()
         */
        public String readLine(String columnName) throws IOException {
            String line = super.readLine();

            if (line == null) return null;

            if (!colIndex.containsKey(columnName))
                throw new RuntimeException("There's no column named " + columnName);

            return getCol(line, colIndex.get(columnName));

        }

        /**
         * Reads the requested column from the next line. If the next line is
         * null, then null is returned.
         *
         * @param colInd The index of the requested column.
         * @return The String in the requested column.
         * @throws java.io.IOException As readLine()
         */
        public String readLine(int colInd) throws IOException {
            String line = super.readLine();

            if (line == null) return null;

            if (colInd >= colIndex.size() || colInd < 0)
                throw new RuntimeException("There's no column indexed" + colInd);

            return getCol(line, colInd);

        }

    }

    /**
     * The constructor.
     *
     * @param readFrom
     * @param delimiter The file delimiter.
     */
    public SpreadsheetReadManager(File readFrom, char delimiter) {

        this.delimiter = delimiter;
        this.readFrom = readFrom;

        try (BufferedReader br = new BufferedReader(new FileReader(readFrom))) {

            String[] firstLine = br.readLine().split("" + delimiter);
            colIndex = new HashMap<>(firstLine.length);
            for (int i = 0; i < firstLine.length; i++)
                colIndex.put(firstLine[i], i);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * Creates a reader with a default delimiter of ','.
     *
     * @param readFrom The name of the file.
     */
    public SpreadsheetReadManager(File readFrom) {
        this(readFrom, ',');
    }

    /**
     * Creates a reader with a default delimiter of ','.
     *
     * @param readFrom The name of the file.
     */
    public SpreadsheetReadManager(String readFrom) {
        this(new File(readFrom), ',');
    }

    /**
     * Gets the indexed column from the proffered string.
     *
     * @param row The string with columns.
     * @param col The index of the desired column.
     */
    protected String getCol(String row, int col) {

        int start = 0;

        while (col > 0) {
            start = row.indexOf(delimiter, start) + 1;
            col--;
        }

        int end = row.indexOf(delimiter, start);
        if (end == -1) end = row.length();

        return row.substring(start, end);
    }

    /**
     * Finds the desired column in the given row.
     *
     * @param row The row.
     * @param colName The column desired.
     * @return The cell at the given row and column.
     */
    protected String getCol(String row, String colName) {
        return getCol(row, indexOf(colName));
    }

    /**
     * Provides a stream of all the elements in the given column.
     *
     * @param colInd The desired column.
     * @return A stream of all the elements in the desired column.
     */
    public Stream<String> lines(int colInd) {
        return lines().map(line -> getCol(line, colInd));
    }

    /**
     * Provides a stream of all the elements in the given column.
     *
     * @return A stream of all the elements in the desired column.
     */
    public Stream<String> lines() {
        try {
            return Files.lines(readFrom.toPath()).skip(1);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * All the elements in the requested column.
     *
     * @param colName The name of the desired column.
     * @return A stream of all the column's elements.
     */
    public Stream<String> lines(String colName) {
        return lines(colIndex.get(colName));
    }

    public Reader getReader() {
        try {
            return new Reader();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

}
