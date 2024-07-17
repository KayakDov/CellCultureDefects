package ImageWork;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.HashSet;
import java.util.LinkedList;


/**
 * A cell representing a set of coordinates in an image.
 */
public class Cell extends HashSet<IntVec2d> {

    public static final int[] zeroPixel = {0};
    public final int color;

    /**
     * Constructor for Cell.
     *
     * @param start A point in the cell
     * @param color The unique identifier of the cell.
     * @param raster To read and write to a buffered image.
     */
    public Cell(IntVec2d start, int color, WritableRaster raster) {
        this.color = color;

        PossibleNeighborQueue potentials = new PossibleNeighborQueue(raster, start);

        while (!potentials.isEmpty()) {
            IntVec2d maybe = potentials.removeFirst();

            add(maybe);

            maybe.adjacent()
                    .filter(neighbor -> neighbor.inside(raster.getWidth(), raster.getHeight()))
                    .filter(neighbor -> color(raster, neighbor) == color)
                    .forEach(neighbor -> potentials.add(neighbor));

        }
    }
    
    

    /**
     * Get the RGB value of a pixel from the raster.
     *
     * @param raster The raster to read from.
     * @param vec The x coordinate of the pixel.
     * @return The RGB value of the pixel.
     */
    public static int color(Raster raster, IntVec2d vec) {
        return raster.getPixel(vec.x, vec.y, (int[]) null)[0];
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

        for (IntVec2d vec : this) {
            if (vec.inside(width, height)) {
                g2d.fillRect(vec.x, vec.y, 1, 1);
            } else {
                System.err.println("Coordinate out of bounds: " + vec);
            }
        }

        g2d.dispose();

        return cellImage;
    }
}
