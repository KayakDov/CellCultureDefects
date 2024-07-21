package GeometricTools;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents a location in the coordinate plane.
 */
public class Vec {

    /**
     * Threshold for equality comparison.
     */
    public static final double equalityThreshold = 1e-5;
    
    private final double x, y;

    /**
     * Constructs a new Loc instance with the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new Loc instance by copying another Loc instance.
     *
     * @param orig The original Loc instance to copy.
     */
    public Vec(Vec orig) {
        this.x = orig.x;
        this.y = orig.y;
    }

    /**
     * The norm of this vector.
     *
     * @return The norm of this vector.
     */
    public double norm() {
        return Math.sqrt(dot(this));
    }

    /**
     * The dot product of this vector and another.
     *
     * @param other The other vector.
     * @return The dot product.
     */
    public double dot(Vec other) {
        return x * other.x + y * other.y;
    }

    /**
     * This vector minus another vector.
     *
     * @param v The other vector.
     * @return This vector minus the proffered vector.
     */
    public Vec minus(Vec v) {
        return new Vec(x - v.x, y - v.y);
    }

    /**
     * The angle of the vector pointing from this Vec to the proffered vec.
     * Equvuilently, atan2(vec.minus(this))
     *
     * @param vec The other vector with home the angle is deisred.
     * @return The angle of the vector pointing from this Vec to the proffered
     * vec.
     */
    public double angleTo(Vec vec) {
        return Math.atan2(vec.y - y, vec.x - x);
    }

    /**
     * The mult of this vector and a scalar.
     *
     * @param t A scalar.
     * @return The prduct of this vector and a scalar.
     */
    public Vec mult(double t) {
        return new Vec(x * t, y * t);
    }

    /**
     * The plus of the two vectors.
     *
     * @param vec The other vector.
     * @return A new vector that is the plus of the two vectors.
     */
    public Vec plus(Vec vec) {
        return new Vec(x + vec.x, y + vec.y);
    }

    /**
     * Calculates the distance between this location and another location.
     *
     * @param loc The other location.
     * @return The distance between this location and the specified location.
     */
    public double dist(Vec loc) {
        return dist(loc, Double.NaN, Double.NaN);
    }

    /**
     * Calculates the distance between this location and another location.
     *
     * @param loc The other location.
     * @param yMod The modularity of the y axis.
     * @param xMod The modularity of the x axis.
     * @return The distance between this location and the specified location.
     */
    public double dist(Vec loc, double xMod, double yMod) {
        double dx = Angle.modDif(x, loc.x, xMod);
        double dy = Angle.modDif(y, loc.y, yMod);
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Gets the x-coordinate of this location.
     *
     * @return The x-coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of this location.
     *
     * @return The y-coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns a string representation of this location.
     *
     * @return A string representation of this location in the format "(x, y)".
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Checks if this location is equal to another location within a small
     * threshold.
     *
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
     *
     * @return The angle of this vector.
     */
    public Angle angle() {
        return new Angle(this);
    }

    /**
     * The origin.
     */
    public static Vec origin = new Vec(0, 0);

    /**
     * Double.isFinite on the x and y values.
     *
     * @return
     */
    public boolean isFinite() {
        return Double.isFinite(x) && Double.isFinite(y);
    }

    /**
     * Returns the rotated vector by a given angle.
     *
     * @param rotation The Angle object representing the angle by which to
     * rotate the vector.
     * @return The rotated vector.
     */
    public Vec rotate(Angle rotation) {
        return angle().plus(rotation).vec().mult(norm());
    }

    
    /**
     * The mean value of the desired index of the points.
     * @param collection The collection of points.
     * @param ind The index over which the mean is to be taken.
     * @return The mean value of the desired index.
     */
    private static double meanAtIndex(Collection<? extends Vec> collection, int ind){
        return collection.stream().mapToDouble(p -> ind == 0? p.getX():p.getY()).average().getAsDouble();
    }
    
    /**
     * Computes the mean of a set of points.
     *
     * @param points A set of Vec representing the points.
     * @return The mean of the given points as a new Vec.
     */
    public static Vec mean(Collection<? extends Vec> points) {
        return new Vec(meanAtIndex(points, 0), meanAtIndex(points, 1));
    }
    
    /**
     * projection onto a line given by another vector.
     * @param spanning a vector spanning a line through the origin.
     * @return The projection onto that line.
     */
    public Vec proj(Vec spanning){
        return spanning.mult(dot(spanning)/spanning.dot(spanning));
    }
    

    /**
     * projection onto a line
     * @param line a line to project onto.
     * @return The projection onto that line.
     */
    public Vec proj(Line line){
        Vec translated = new Vec(x, y - line.getYIntercept());
        
        Vec transProj = translated.proj(line.span());
        
        return new Vec(transProj.getX(), transProj.getY() + line.getYIntercept());
    }
    
    /**
     * The distance from this point to a line.
     * @param line The line from which the distance is desired.
     * @return The distance from this point to the line.
     */
    public double dist(Line line){
        return dist(proj(line));
    }
    
    /**
     * The nematic nemDirector defined by this vector, as though it has two heads.
     * @return The nematic nemDirector defined by this vector, as though it has two heads
     */
    public NematicDirector nemDirector(){
        return new NematicDirector(this);
    }

    /**
     * This vector expressed as an array, with the first element the x value and 
     * the second element the y value.
     * @return This vector expressed as an array, with the first element the x value and 
     * the second element the y value.
     */
    public double[] toArray(){
        return new double[]{x, y};
    }
    
    /**
     * The x value rounded to the nearest integer.
     * @return The x value rounded to the nearest integer.
     */
    public int x() {
        return (int)Math.round(getX());
    }

    /**
     * The y value rounded to the nearest integer.
     * @return The y value rounded to the nearest integer.
     */
    public int y() {
        return (int)Math.round(getY());
    }
    
    /**
     * Do these points have the same pixels. (nearest integer approximations.)
     * @param v The point to check against this one.
     * @return True if they have the same pixel, false otherwise.
     */
    public boolean samePixel(Vec v){
        return x() == v.x() && y() == v.y();
    }
}
