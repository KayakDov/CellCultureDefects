package GeometricTools;

import dataTools.StdDev;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static java.lang.Math.*;

/**
 * This class represents an angle.
 *
 * @author E. Dov Neimand
 */
public class Angle {


    private final double rad;
    /**
     * The length around the circle. For regular unit circles this is by default
     * set to 2pi. For nematic angles this should be pi.
     */
    private final double circ;

    /**
     * Places an angle between 0 and 2pi.
     *
     * @param posOrNegRadians An angle that may be positive or negative.
     * @param circ The circumference of the circle. This should be 2pi for
     * regular angles and pi for nematic directors.
     */
    protected Angle(double posOrNegRadians, double circ) {
        this.circ = circ;
        if (posOrNegRadians >= circ) rad = posOrNegRadians % circ;
        else if (posOrNegRadians < 0) rad = circ + posOrNegRadians % circ;
        else rad = posOrNegRadians;
    }
    
    /**
     * Places an angle between 0 and 2pi.
     *
     * @param posOrNegRadians An angle that may be positive or negative.
     *
     */
    public Angle(double posOrNegRadians) {
        this(posOrNegRadians, 2 * PI);
    }

    /**
     * Constructs an angle from the x and y coordinates of a vector.
     *
     * @param v The vector being passed.
     * @param circ The circumference of the circle.
     */
    protected Angle(Vec v, double circ) {
        this(atan2(v.getY(), v.getX()), circ);
    }

    /**
     * Constructs an angle from the x and y coordinates of a vector.
     *
     * @param x The vector being passed.
     *
     */
    public Angle(Vec x) {
        this(x, 2 * PI);
    }

    /**
     * This angle in radians.
     *
     * @return This angle in radians.
     */
    public double rad() {
        return rad;
    }

    /**
     * This angle in degrees from 0 to 360.
     *
     * @return This angle in degrees.
     */
    public double deg() {
        return 360 * rad / circ;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "" + rad() / PI + "*pi";
    }

    /**
     * The cos of this angle.
     *
     * @return The cos of this angle.
     */
    public double cos() {
        return Math.cos(rad);
    }

    /**
     * The sin of this anlge.
     *
     * @return The sin of this anlge.
     */
    public double sin() {
        return Math.sin(rad);
    }

    /**
     * The unit vector represented by this angle.
     *
     * @return
     */
    public Vec vec() {
        return new Vec(cos(), sin());
    }

    /**
     * The average of the angles. For nematic angles, be sure to double them
     * before calculating the average, and then halve the result.
     *
     * @param angles The angles to be averaged.
     * @return The circular average of the angles.
     */
    public static Angle average(Stream<Angle> angles) {
        
        return new Angle(angles.map(ang -> ang.vec().mult(ang.getWeight())).reduce(Vec.origin, (a, b) -> a.plus(b)));
    }

    /**
     * The distance from a to b modular arithmetic. a and b should be between 0
     * and mod.
     *
     * @param a The distance from a to b modular arithmetic.
     * @param b The distance from a to b modular arithmetic.
     * @param mod size of the circle. This can be set to infinity for regular
     * arithmetic.
     * @return The difference between the two points.
     */
    public static double modDif(double a, double b, double mod) {
        if (!Double.isFinite(mod)) return abs(a - b);
        double direct = abs(a - b),
                outside = (mod - max(a, b)) + min(a, b);
        return min(direct, outside);
    }

    /**
     * The arc distance between this angle and another. This will always be a
     * positive number.
     *
     * @param ang The other angle.
     * @return The arc distance between the two angles.
     */
    public double arcDist(Angle ang) {
        return modDif(rad(), ang.rad(), circ);
    }

    /**
     * Computes the standard deviation, note, the stream is run 3 times
     * including to count the number of elements, so not super efficient.
     *
     * @param angleStream A stream of angles.
     * @return The standard deviation of the the angles.
     */
    public static double stdDev(Supplier<Stream<Angle>> angleStream) {
        Angle avg = average(angleStream.get());
        return angleStream.get()
                .mapToDouble(angle -> StdDev.sq(angle.arcDist(avg))).sum()
                / angleStream.get().count();
    }

    /**
     * The difference of this angle and another angle.
     *
     * @param ang The other angle.
     * @return The difference of this angle and another angle.
     */
    public Angle minus(Angle ang) {
        return new Angle(rad() - ang.rad());
    }

    /**
     * The sum of an angle and a scalar in radians.
     *
     * @param radians A scalar in radians.
     * @return An angle.
     */
    public Angle plus(double radians) {
        return new Angle(rad() + radians);
    }

    /**
     * Add an angle to this one.
     *
     * @param angle The angle to be added.
     * @return a new angle.
     */
    public Angle plus(Angle angle) {
        return plus(angle.rad());
    }
    /**
     *
     */
    public static Angle NaN = new Angle(Double.NaN);

    /**
     * A new angle that is this angle multiplied by a scalar;
     *
     * @param t The scalar
     * @return A new angle.
     */
    public Angle mult(double t) {
        return new Angle(rad() * t);
    }

    /**
     * Used for computing averages.
     * @return The weight used for computing averages.
     */
    public double getWeight() {
        return 1;
    }
    
    /**
     * An angle identical to this one, but with a weight for the purpose of computing averages.
     * @param weight The weight of the new angle.
     * @return An angle with a weight.
     */
    public Angle weightedAngle(double weight){
        return new Angle(rad, circ){
            @Override
            public double getWeight() {
                return weight;
            }
        };
    }

    /**
     * This angle as a nematic.
     * @return This angle as a nematic.
     */
    public NematicDirector nematic(){
        return new NematicDirector(rad);
    }
}
