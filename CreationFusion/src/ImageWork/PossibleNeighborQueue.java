package ImageWork;

import java.awt.image.WritableRaster;
import java.util.ArrayDeque;

/**
 *
 * @author E. Dov Neimand
 */
public class PossibleNeighborQueue extends ArrayDeque<IntVec2d>{

    private final WritableRaster raster;

    /**
     * The constructor
     * @param raster a square will be set to black when its vector is added.
     */
    public PossibleNeighborQueue(WritableRaster raster, IntVec2d first) {
        super(50_000);
        this.raster = raster;
        add(first);
    }       
    
    @Override
    public boolean add(IntVec2d e) {
        raster.setPixel(e.x, e.y, Cell.zeroPixel);
        return super.add(e);         
    }
    
}
