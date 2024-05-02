package snapDefects;

import GeometricTools.Angle;
import SnapManagement.Defect;
import SnapManagement.PosDefect;

/**
 * A positive SnapDefect.
 * @author E. Dov Neimand
 */
public class PosSnapDefect extends SnapDefect{
    
    private Angle tailAngle;
    private Angle dThetadt;
    
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
        this.tailAngle = new Angle(angle);
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
    public Angle tailAngle() {
        return tailAngle;
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
        return loc.dist(other.loc) < dist && Math.abs(loc.getTime() - other.loc.getTime()) < time;
    }
    
    /**
     * Sets the angular velocity.
     * @param prev The previous snap defect
     * @param next the next snap defect
     */
    @Override
    public void setAngleVelocity(SnapDefect prev, SnapDefect next) {       
        
        dThetadt = ((PosSnapDefect)next).tailAngle.minus(((PosSnapDefect)prev).tailAngle)
                .mult(1.0/(next.loc.getTime() - prev.loc.getTime()));
    }

    @Override
    public PosDefect getDefect() {
        return (PosDefect)defect;
    }

    @Override
    public PosSnapDefect setDefect(Defect defect) {
        super.setDefect(defect);
        return this;
    }
        
}
