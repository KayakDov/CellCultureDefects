package ImageWork;

import java.awt.image.WritableRaster;
import java.util.ArrayDeque;

/**
 *
 * @author E. Dov Neimand
 */
public class PossibleNeighborQueue extends ArrayDeque<Pixel>{

    private final WritableRaster raster;

    /**
     * The constructor
     * @param raster a square will be set to black when its vector is added.
     * @param first The first element to be added to the array.
     */
    public PossibleNeighborQueue(WritableRaster raster, Pixel first) {
        super(50_000);
        this.raster = raster;
        add(first);
    }       
    
    @Override
    public boolean add(Pixel e) {
        raster.setPixel(e.x(), e.y(), new int[raster.getNumBands()]);
        return super.add(e);         
    }
    
}
