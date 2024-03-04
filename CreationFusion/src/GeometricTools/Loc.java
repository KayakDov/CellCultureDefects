package GeometricTools;


/**
 * Represents a location in the coordinate plane.
 */
public class Loc {

    /** Threshold for equality comparison. */
    public static final double equalityThreshold = 1e-10;

    private final double x, y;


    /**
     * Constructs a new Loc instance with the specified coordinates.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public Loc(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new Loc instance by copying another Loc instance.
     * @param orig The original Loc instance to copy.
     */
    public Loc(Loc orig) {
        this.x = orig.x;
        this.y = orig.y;
    }

    /**
     * Calculates the distance between this location and another location.
     * @param loc The other location.
     * @return The distance between this location and the specified location.
     */
    public double dist(Loc loc) {
        double dx = loc.x - x;
        double dy = loc.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Gets the x-coordinate of this location.
     * @return The x-coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of this location.
     * @return The y-coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns a string representation of this location.
     * @return A string representation of this location in the format "(x, y)".
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Checks if this location is equal to another location within a small threshold.
     * @param obj The other object to compare.
     * @return true if the two locations are considered equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Loc)) {
            return false;
        }
        Loc other = (Loc) obj;
        return this.dist(other) <= equalityThreshold;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }
}
