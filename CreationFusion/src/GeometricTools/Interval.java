package GeometricTools;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/**
 * A closed line segment.
 *
 * @author E. Dov Neimand
 */
public class Interval {

    private double min, max;

    /**
     * A closed line segment.
     *
     * @param min The minimum value of the segment.
     * @param max The maximum value of the segment.
     */
    public Interval(double min, double max) {
        this.min = min;
        this.max = max;
        if (max < min)
            throw new IllegalArgumentException("max = " + max + " which is less than min = " + min);
    }

    /**
     * Creates an empty lineSegment. This line segment will have an undefined
     * length.
     */
    public Interval() {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
    }

    /**
     * Does this line segment contain the given value.
     *
     * @param x The point to be checked for membership in the line segment.
     * @param epsilon clearance to edge of line segment. Set to 0 for a closed
     * line segment.
     * @return true if x is in the line segment, false otherwise.
     */
    public boolean contains(double x, double epsilon) {
        return min <= x && x <= max;
    }

    /**
     * Does this line segment contain the given value.
     *
     * @param x The point to be checked for membership in the line segment.
     *
     * @return true if x is in the line segment, false otherwise.
     */
    public boolean contains(double x) {
        return contains(x, 0);
    }

    /**
     * Is this point near the edge of the line segment?
     *
     * @param x The value to be tested.
     * @param epsilon The distance from the edge of the semgent.
     * @return True if x is near the edge of the line segment and false if it is
     * not.
     */
    public boolean nearEdge(double x, double epsilon) {
        return contains(x) && !contains(x, epsilon);
    }

    /**
     * If the line segment doesn't include the proffered value, it will be grown
     * to include it.
     *
     * @param x The value to be added to the line segment.
     */
    public void expand(double... x) {
        for (double d : x) {
            if (d < min) min = d;
            if (d > max) max = d;
        }
    }
    
    /**
     * Expands (think hull) to include a 1d circle.
     * @param cent The center of the circle.
     * @param r The radius of the circle.
     */
    public void expandCirc(double cent, double r){
        expand(cent - r, cent + r);
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    /**
     * The length of the line segment.
     *
     * @return The length of the line segment.
     */
    public double length() {
        return max - min;
    }
    
    
    /**
     * A function that takes in a value in this segment and maps it to a 
     * corresponding location in the target segment.
     * @param target The segment to be mapped to.
     * @return A natural linear mapping from this segment to the target 
     * segment.
     */
    public double scale(double sourcePoint, Interval target){
        
        return target.min + (sourcePoint - min)*target.length()/length();
    }
}
