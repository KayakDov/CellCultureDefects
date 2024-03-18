package ReadWrite;

import GeometricTools.Vec;
import SnapManagement.PairSnDef;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author edov
 */
public class FormatedFileWriter extends BufferedWriter {

    public final char delimiter;
    public Column[] cols;

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
    public void writeLine(PairSnDef sdp) {

        try {
            write(Arrays.stream(cols)
                    .map(col -> col.f.apply(sdp))
                    .collect(Collectors.joining("" + delimiter)));
            newLine();
        } catch (IOException ex) {
            Logger.getLogger(FormatedFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Each column has a name and a function that writes to it.
     */
    public static class Column {

        public final String name;
        private final Function<PairSnDef, String> f;

        /**
         * Constructs a column.
         *
         * @param name The name of the column.
         * @param f The tool for computing elements of the column
         */
        public Column(String name, Function<PairSnDef, String> f) {
            this.name = name;
            this.f = f;
        }

    }

    /**
     * A default file writer.
     * @param fileName The name of the file to write to.
     * @return A default file writer.
     */
    public static FormatedFileWriter defaultWriter(String fileName) {
        try {
            return new FormatedFileWriter(
                    fileName,
                    ',',
                    new Column("FRAME", sdp -> sdp.pos.getTime() + ""),
                    new Column("plus_id", sdp -> sdp.pos.getID() + ""),
                    new Column("xp", sdp -> sdp.pos.getX() + ""),
                    new Column("yp", sdp -> sdp.pos.getY() + ""),
                    new Column("angp1", sdp -> sdp.pos.tailAngle() + ""),
                    new Column("min_id", sdp -> sdp.neg.getID() + ""),
                    new Column("xm", sdp -> sdp.neg.getX() + ""),
                    new Column("ym", sdp -> sdp.neg.getY() + ""),
                    new Column("angm1", sdp -> sdp.neg.tailAngle()[0] + ""),
                    new Column("angm2", sdp -> sdp.neg.tailAngle()[1] + ""),
                    new Column("angm3", sdp -> sdp.neg.tailAngle()[2] + ""),
                    new Column("distance", sdp -> sdp.dist() + ""),
                    new Column("mp_angle", sdp -> sdp.mpAngle() + ""),
                    new Column("angp1_rel", sdp -> sdp.anglePRel() + ""),
                    new Column("angm1_rel", sdp -> sdp.ang123Rel()[0] + ""),
                    new Column("angm2_rel", sdp -> sdp.ang123Rel()[1] + ""),
                    new Column("angm3_rel", sdp -> sdp.ang123Rel()[2] + ""),
                    new Column("fuse_up", sdp -> sdp.fuseUp()?"TRUE":"FALSE"),
                    new Column("p_vel_angle", sdp -> {
                        Vec vel = sdp.pos.getVelocity();
                        return (vel!=null? vel.angle():"") + "";
                    }),
                    new Column("p_vel_angle_rel", sdp -> {
                        Vec vel = sdp.relVelocity();
                        return vel != null?vel.angle() + "":"";}),
                    new Column("anglep1_rel_vel_angle", sdp -> sdp.anglep1_rel_vel_angle() + ""), 
                    new Column("fusion", sdp -> !sdp.birth?"TRUE":"FALSE"),
                    new Column("creation", sdp -> sdp.birth?"TRUE":"FALSE"),
                    new Column("mp_angl1", sdp -> sdp.mp123()[0] + ""),
                    new Column("mp_angl2", sdp -> sdp.mp123()[1] + ""),
                    new Column("mp_angl3", sdp -> sdp.mp123()[2] + ""),
                    new Column("mp_phase", sdp -> sdp.mpPhase() + "")
            );
        } catch (IOException ex) {
            Logger.getLogger(FormatedFileWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
