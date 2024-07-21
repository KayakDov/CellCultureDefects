package nematics;

import GeometricTools.LineSeg;
import GeometricTools.NematicDirector;
import GeometricTools.Vec;

/**
 * The points used to create a continuous nematic field.
 * @author E. Dov Neimand
 */
public interface NematicFieldAnchor {
    
    /**
     * A nemDirector in the nematic field.
     * @return A nemDirector in the nematic field.
     */
    public NematicDirector getDir();
    
    /**
     * The location of the nemDirector in the nematic field.
     * @return The location of the nemDirector in the nematic field.
     */
    public Vec loc();
    
    /**
     * Computed by diving the eigan values of the covariance matrix and subtracting one.
     * @return The higher the number, the less circular it is.
     */
    public double quality();
    
    /**
     * A line segment that runs along this anchor.
     * @param length The length of the segment.
     * @return A line segment that runs along this anchor.
     */
    public default LineSeg dash(double length){
        return new LineSeg(
                loc().plus(getDir().vec().mult(length/2)), 
                loc().plus(getDir().vec().mult(-length/2))
        );
    }
}
