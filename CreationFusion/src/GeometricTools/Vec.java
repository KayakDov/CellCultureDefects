package GeometricTools;


/**
 * Represents a location in the coordinate plane.
 */
public class Vec {

    /** Threshold for equality comparison. */
    public static final double equalityThreshold = 1e-10;

    private final double x, y;


    /**
     * Constructs a new Loc instance with the specified coordinates.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new Loc instance by copying another Loc instance.
     * @param orig The original Loc instance to copy.
     */
    public Vec(Vec orig) {
        this.x = orig.x;
        this.y = orig.y;
    }

    /**
     * The norm of this vector.
     * @return The norm of this vector.
     */
    public double norm(){
        return Math.sqrt(x*x + y*y);
    }
    
    /**
     * This vector minus another vector.
     * @param v The other vector.
     * @return This vector minus the proffered vector.
     */
    public Vec minus(Vec v){
        return new Vec(x-v.x, y - v.y);
    }
    
    /**
     * The angle of the vector pointing from this Vec to the proffered vec.
     * Equvuilently, atan2(vec.minus(this))
     * @param vec The other vector with home the angle is deisred.
     * @return The angle of the vector pointing from this Vec to the proffered vec.
     */
    public double angleTo(Vec vec){
        return Math.atan2(vec.y - y, vec.x - x);
    }
    
    /**
     * The mult of this vector and a scalar.
     * @param t A scalar.
     * @return The prduct of this vector and a scalar.
     */
    public Vec mult(double t){
        return new Vec(x*t, y*t);
    }
    
    /**
     * The plus of the two vectors.
     * @param vec The other vector.
     * @return A new vector that is the plus of the two vectors.
     */
    public Vec plus(Vec vec){
        return new Vec(x + vec.x, y + vec.y);
    }
        
    /**
     * Calculates the distance between this location and another location.
     * @param loc The other location.
     * @return The distance between this location and the specified location.
     */
    public double dist(Vec loc){
        return dist(loc, Double.NaN, Double.NaN);
    }
    
    /**
     * Calculates the distance between this location and another location.
     * @param loc The other location.
     * @param yMod The modularity of the y axis.
     * @param xMod The modularity of the x axis.
     * @return The distance between this location and the specified location.
     */
    public double dist(Vec loc, double yMod, double xMod) {
        double dx = Angle.modDif(x, loc.x, xMod);
        double dy = Angle.modDif(y, loc.y, yMod);
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
        if (!(obj instanceof Vec)) {
            return false;
        }
        Vec other = (Vec) obj;
        return this.dist(other) <= equalityThreshold;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }
    
    /**
     * The angle of this vector.
     * @return The angle of this vector.
     */
    public Angle angle(){
        return new Angle(this);
    }
    
    /**
     * The origin.
     */
    public static Vec origin = new Vec(0, 0);
}