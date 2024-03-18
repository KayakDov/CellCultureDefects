package snapDefects;

import SnapManagement.Defect;

/**
 * A positive SnapDefect.
 * @author E. Dov Neimand
 */
public class PosSnapDefect extends SnapDefect{
    
    private double angle;
    
    /**
     * A positive SnapDefect.
     * @param x The x location.
     * @param y The y location.
     * @param t The time.
     * @param id The id of the defect.
     * @param angle The angle of the defect relative to the x axis.
     */
    public PosSnapDefect(double x, double y, int t, int id, double angle) {
        super(x, y, t, id);
        this.angle = angle;
    }

    @Override
    public boolean getCharge() {
        return true;
    }

    /**
     * The angle of the snap defect relative to the x axis.  This is the
     * angle of a vector pointing toward the tail of the defect.
     * @return The angle of the snap defect relative to the x axis.
     */
    public double tailAngle() {
        return angle;
    }
    
    
    
    /**
     * Is this defect near another defect during a birth or death event?
     *
     * @param other The other defect.
     * @param dist The distance from one to the other in the plane.
     * @param time The distance from one to the other in time.
     * @return True if the defects are near each other, false otherwise.
     */
    public boolean near(NegSnapDefect other, double dist, int time) {
        return dist(other) < dist && Math.abs(getTime() - other.getTime()) < time;
    }
    
}
