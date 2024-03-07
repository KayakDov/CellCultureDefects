package creationfusion;

import java.util.Arrays;

/**
 * Represents a snapshot of a defect at a moment in time.
 */
public abstract class SnapDefect extends SpaceTemp implements hasChargeID{

    private final int id;
    
    public final static int NO_ID = Integer.MAX_VALUE;

    /**
     * Constructs a new SnapDefect instance with the specified location, ID, and charge.
     * @param loc The location of the defect.
     * @param id The ID of the defect.
     */
    public SnapDefect(SpaceTemp loc, int id) {
        super(loc);
        this.id = id;
    }
    
    /**
     * Constructs a new SnapDefect instance with the specified location, ID, and charge.
     * @param x The x value.
     * @param y The y value.
     * @param t The time value.
     * @param id The ID of the snap Defect.
     */
    public SnapDefect(double x, double y, int t, int id){
        super(x, y, t);
        this.id = id;
    }


    /**
     * Gets the ID of this defect.
     * @return The ID of the defect.
     */
    public int getID() {
        return id;
    }

    /**
     * Gets the charge of this defect.
     * @return true if the defect has a positive charge, false otherwise.
     */
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

    
    
    
}
