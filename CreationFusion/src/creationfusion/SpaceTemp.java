package creationfusion;

import GeometricTools.Loc;


/**
 * Represents a point in space-time.
 */
public class SpaceTemp extends Loc {

    private final int time;
    
    public final static int defaultTimeThreshold = 4;
    public final static double defaultDistanceThreshold = 40;

    /**
     * Constructs a new SpaceTemp instance with the specified location and time.
     * @param loc The spatial location.
     * @param t The time value.
     */
    public SpaceTemp(Loc loc, int t) {
        super(loc.getX(), loc.getY());
        this.time = t;
    }

    /**
     * Constructs a new SpaceTemp instance by copying another SpaceTemp instance.
     * @param orig The original SpaceTemp instance to copy.
     */
    public SpaceTemp(SpaceTemp orig) {
        super(orig);
        this.time = orig.time;
    }

    /**
     * Constructs a new SpaceTemp instance with the specified location and time.
     * @param time The time value.
     * @param x The x value.
     * @param y The y value.
     */
    public SpaceTemp(double x, double y, int time) {
        super(x, y);
        this.time = time;
    }
    
    /**
     * Gets the time component of this space-time point.
     * @return The time component.
     */
    public int getTime() {
        return time;
    }

    /**
     * Calculates the space-time distance between this point and another space-time point.
     * The distance includes both spatial and temporal components.
     * @param st The other space-time point.
     * @return The space-time distance between this point and the specified point.
     */
    public double sTDist(SpaceTemp st) {
        return dist(st) + Math.abs(st.getTime() - getTime());
    }

    /**
     * Checks if this space-time point is near another space-time point within given thresholds.
     * @param st The other space-time point.
     * @param distThreshold The maximum spatial distance threshold.
     * @param timeThreshold The maximum time difference threshold.
     * @return true if the points are within the specified spatial and temporal thresholds, false otherwise.
     */
    public boolean near(SpaceTemp st, double distThreshold, double timeThreshold) {
        return dist(st) <= distThreshold && Math.abs(time - st.time) <= timeThreshold;
    }
}
