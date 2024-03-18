package snapDefects;

import GeometricTools.Vec;
import defectManagement.hasChargeID;


/**
 * Represents a snapshot of a defect at a moment in time.
 */
public abstract class SnapDefect extends SpaceTemp implements hasChargeID {

    private final int id;

    public final static int NO_ID = Integer.MAX_VALUE;

    private Vec dxdt;

    /**
     * Constructs a new SnapDefect instance with the specified location, ID, and
     * charge.
     *
     * @param loc The location of the defect.
     * @param id The ID of the defect.
     */
    public SnapDefect(SpaceTemp loc, int id) {
        super(loc);
        this.id = id;
    }

    /**
     * Constructs a new SnapDefect instance with the specified location, ID, and
     * charge.
     *
     * @param x The x value.
     * @param y The y value.
     * @param t The time value.
     * @param id The ID of the snap Defect.
     */
    public SnapDefect(double x, double y, int t, int id) {
        super(x, y, t);
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public abstract boolean getCharge();

    public boolean equals(SnapDefect sd) {
        return sd.id == id && sd.getTime() == getTime() && sd.getCharge() == getCharge();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + id;
        result = prime * result + getTime();
        result = prime * result + (getCharge() ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SnapDefect other = (SnapDefect) obj;
        if (this.id != other.id) return false;
        return getCharge() == other.getCharge() && getTime() == other.getTime();
    }

    /**
     * Sets the displacement from the previous and next locations.
     *
     * @param prev
     * @param next
     */
    public void setVelocity(SpaceTemp prev, SpaceTemp next) {
        if(prev == null && next == null) return;
        
        if (prev == null || prev.getTime() != getTime() - 1) 
            dxdt = next.minus(this).mult(1/(next.getTime() - getTime()));

        else if (next == null || next.getTime() != getTime() + 1) 
            dxdt = minus(prev).mult(1/(getTime() - prev.getTime()));
        
        else dxdt = next.minus(prev).mult(1/(next.getTime() - prev.getTime()));
        
    }

    /**
     * Displacement divided by time.  
     * Be sure to load this with setDisplacement angle before calling.
     * @return Displacement divided by time.
     */
    public Vec getVelocity() {
        return dxdt;
    }

    @Override
    public String toString() {
        return super.toString() + ", charge = " + (getCharge()?"pos":"neg") + ", id = " + getID(); 
    }
    
    
    
    
}
