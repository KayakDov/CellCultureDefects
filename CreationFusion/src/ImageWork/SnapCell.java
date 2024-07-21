package ImageWork;

import GeometricTools.NematicDirector;
import nematics.NematicFieldAnchor;
import GeometricTools.Vec;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.HashSet;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * A cell representing a set of coordinates in an image.
 */
public class SnapCell extends HashSet<Pixel> implements NematicFieldAnchor {

//    public static final int[] zeroPixel;
    public final int color;
    public final Vec mean;
    private final EigenDecomposition covarMatrEigan;

    /**
     * Constructor for Cell.
     *
     * @param toAdd A point in the cell
     * @param color The unique identifier of the cell.
     * @param raster To read and write to a buffered image.
     */
    public SnapCell(Pixel toAdd, int color, WritableRaster raster) {
        this.color = color;

        PossibleNeighborQueue potentials = new PossibleNeighborQueue(raster, toAdd);

        while (!potentials.isEmpty()) {
            toAdd = potentials.removeFirst();

            add(toAdd);

            toAdd.adjacent()
                    .filter(neighbor -> neighbor.inside(raster.getWidth(), raster.getHeight()))
                    .filter(neighbor -> color(raster, neighbor) == color)
                    .forEach(neighbor -> potentials.add(neighbor));
        }

        this.mean = Vec.mean(this);

        RealMatrix centeredPoints = new Array2DRowRealMatrix(stream() //TODO: this could be made faster with less new memory alocated!
                .map(vec -> vec.minus(mean).toArray())
                .toArray(double[][]::new)
        );

        covarMatrEigan = new EigenDecomposition(
                centeredPoints
                        .transpose()
                        .multiply(centeredPoints)
                        .scalarMultiply(1.0 / size())
        );

    }

    /**
     * Get the RGB value of a pixel from the raster.
     *
     * @param raster The raster to read from.
     * @param vec The x coordinate of the pixel.
     * @return The RGB value of the pixel.
     */
    public static int color(Raster raster, Pixel vec) {
        return raster.getPixel(vec.x(), vec.y(), (int[]) null)[0];
    }

    /**
     * Generate a binary image representation of the cell.
     *
     * @param width The width of the image.
     * @param height The height of the image.
     * @return BufferedImage representing the cell as a binary image.
     */
    public BufferedImage image(int width, int height) {

        BufferedImage cellImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D g2d = cellImage.createGraphics();

        // Set background color to white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Set pixel color to black
        g2d.setColor(Color.BLACK);

        for (Pixel vec : this) {
            if (vec.inside(width, height)) {
                g2d.fillRect(vec.x(), vec.y(), 1, 1);
            } else {
                System.err.println("Coordinate out of bounds: " + vec);
            }
        }

        g2d.dispose();

        return cellImage;
    }

    public NematicDirector getDir() {
        
        return new Vec(
                covarMatrEigan.getEigenvector(0).getEntry(0), 
                covarMatrEigan.getEigenvector(0).getEntry(1)
        ).nemDirector();
    }

    @Override
    public Vec loc() {
        return mean;
    }

    @Override
    public double quality() {
        if(covarMatrEigan.getRealEigenvalue(0) < covarMatrEigan.getRealEigenvalue(1))
            throw new RuntimeException("first eigan value is not the biggest.");
            
        return covarMatrEigan.getRealEigenvalue(0)/covarMatrEigan.getRealEigenvalue(1);
    }

}
