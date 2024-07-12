package GeometricTools;

/**
 *
 * The nematic director, and angle between 0 and 180. Can be thought of as a
 * directionless vector.
 *
 * @author E. Dov Neimand
 */
public class NematicDirector extends Angle {
    /**
     * The nematic director, and angle between 0 and 180. Can be thought of as a
     * directionless vector.
     *
     * @param posOrNegRadians The angle.
     */
    public NematicDirector(double posOrNegRadians) {
        super(posOrNegRadians, Math.PI);
    }

}
