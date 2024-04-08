package snapDefects;

import GeometricTools.Angle;
import GeometricTools.Vec;
import defectManagement.hasChargeID;

/**
 * Represents a snapshot of a defect at a moment in time.
 */
public abstract class SnapDefect implements hasChargeID {

    public final SpaceTemp loc;

    private int id;

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
        this.loc = loc;
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
        loc = new SpaceTemp(x, y, t);
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public abstract boolean getCharge();

    public boolean equals(SnapDefect sd) {
        return sd.id == id && sd.loc.getTime() == loc.getTime() && sd.getCharge() == getCharge();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + id;
        result = prime * result + loc.getTime();
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
        return getCharge() == other.getCharge() && loc.getTime() == other.loc.getTime();
    }

    /**
     * Sets the displacement from the previous and next locations.
     *
     * One, but not both, of the parameters passed may be null for a slightly
     * less accurate answer.
     *
     * @param prev The previous snap Defect in time with the same the defect.
     * @param next The next snap defect for the defect.
     */
    public void setVelocity(SnapDefect prev, SnapDefect next) {
        boolean badPrev = prev == null || (prev.loc.getTime() != loc.getTime() - 1 && prev.loc.getTime() != loc.getTime()) ;
        boolean badNext = next == null || (next.loc.getTime() != loc.getTime() + 1 && next.loc.getTime() != loc.getTime());

        if (badPrev && badNext);

        else if (badPrev) setVelocity(this, next);

        else if (badNext) setVelocity(prev, this);

        else {
            dxdt = next.loc.minus(prev.loc).mult(1.0 / (next.loc.getTime() - prev.loc.getTime()));
            setAngleVelocity(prev, next);
        }

    }

    /**
     * Sets the velocity of the tail angle rotation.
     *
     * @param prev The previous SnapDefect
     * @param next The next SnapDefect.
     */
    public abstract void setAngleVelocity(SnapDefect prev, SnapDefect next);

    /**
     * Displacement divided by time. Be sure to load this with setDisplacement
     * angle before calling.
     *
     * @return Displacement divided by time.
     */
    public Vec getVelocity() {
        return dxdt;
    }

    @Override
    public String toString() {
        return loc.toString() + ", charge = " + (getCharge() ? "pos" : "neg") + ", id = " + getID() + "\n";
    }
    
    /**
     * The time of this SnapDefect.
     * @return The time of this SnapDefect.
     */
    public int getTime(){
        return loc.getTime();
    }

    /**
     * Resets the ID
     * @param id The new ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    
}
