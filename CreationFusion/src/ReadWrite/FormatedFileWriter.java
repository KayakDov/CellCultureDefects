package ReadWrite;

import GeometricTools.Vec;
import SnapManagement.PairedSnDef;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author edov
 */
public class FormatedFileWriter extends BufferedWriter {

    public final char delimiter;
    protected Column[] cols;
    

    /**
     * Creates a file writer that writes the given columns.
     *
     * @param delimiter What goes between the columns.
     * @param cols The columns that will be written.
     * @param fileName The name of the file to write to.
     * @throws java.io.IOException
     */
    public FormatedFileWriter(String fileName, char delimiter, Column... cols) throws IOException {
        super(new FileWriter(fileName));
        this.delimiter = delimiter;
        this.cols = cols;
        String firstLine = Arrays.stream(cols)
                .map(col -> col.name + delimiter)
                .reduce("", (a, b) -> a + b);
        write(firstLine);
        newLine();
    }

    /**
     * Writes a line featuring this snap defect.
     *
     * @param sdp The snap defect to get a line.
     */
    public void writeLine(PairedSnDef sdp) {

        try {
            write(Arrays.stream(cols)
                    .map(col -> col.apply(sdp))
                    .collect(Collectors.joining("" + delimiter)));
            newLine();
        } catch (IOException ex) {
            Logger.getLogger(FormatedFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Each column has a name and a function that writes to it.
     */
    public abstract static class Column implements Function<PairedSnDef, String>{

        public final String name;
        
        /**
         * Constructs a column.
         *
         * @param name The name of the column.
         */
        public Column(String name) {
            this.name = name;
        }
    }
    
    

}
