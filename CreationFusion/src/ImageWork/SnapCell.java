package ImageWork;

import GeometricTools.NematicDirector;
import GeometricTools.Vec;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import snapDefects.SpaceTemp;

/**
 * Data about a cell at a given moment in time.
 * @author E. Dov Neimand
 */
public class SnapCell {
    public final Point loc;
    public NematicDirector dir;
    public Mat cellImg;

    /**
     * The constructor.
     * 
     * @param cellImg The image of the cell.
     * @param loc The time and place (a point more or less centered in the cell) of the cell.
     */
    public SnapCell(Mat cellImg, Point loc) {
        this.loc = loc;
        this.cellImg = cellImg;
    }

    public Mat getCellImage() {
        return cellImg;
    }
    
    
}
