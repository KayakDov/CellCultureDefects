package GeometricTools;

import dataTools.StdDev;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class represents an angle.
 * @author E. Dov Neimand
 */
public class Angle {

    private final double angle;
    /**
     * The length around the circle.  For regular unit circles this is by default set
     * to 2pi.  For nematic angles this should be pi.
     */
    private final double circ = Math.PI * 2;
    
    /**
     * Places an angle between 0 and 2pi.
     * @param posOrNegRadians An angle that may be positive or negative.
     */
    public Angle(double posOrNegRadians) {
        if (posOrNegRadians >= circ) angle = posOrNegRadians % circ;
        else if (posOrNegRadians < 0)angle =  circ + posOrNegRadians % circ;
        else angle = posOrNegRadians;
    }
    
    /**
     * Constructs an angle from the x and y coordinates of a vector.
     * @param x
     * @param y 
     */
    public Angle(double x, double y){
        this(Math.atan2(y, x));
    }
    
    /**
     * Constructs an angle from the x and y coordinates of a vector.
     * @param vec 
     */
    public Angle(Vec vec){
        this(vec.getX(), vec.getY());
    }
    
    /**
     * This angle in radians.
     * @return This angle in radians.
     */
    public double rad(){
        return angle;
    }
    
    /**
     * This angle in degrees from 0 to 360.
     * @return This angle in degrees.
     */
    public double deg(){
        return 360*angle/circ;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "" + rad()/Math.PI + "*pi";
    }
    
    /**
     * The unit vector represented by this angle.
     * @return 
     */
    public Vec vec(){
        return new Vec(Math.cos(angle), Math.sin(angle));
    }
    
    /**
     * The average of the angles.  For nematic angles, be sure to double them 
     * before calculating the average, and then halve the result.
     * @param angles The angles to be averaged.
     * @return The circular average of the angles.
     */
    public static Angle average(Stream<Angle> angles){
        return new Angle(angles.map(ang -> ang.vec()).reduce(Vec.origin, (a, b) -> a.plus(b)));
    }
    
    /**
     * The distance from a to b modular arithmetic. a and b should be between 0
     * and mod.
     * @param a The distance from a to b modular arithmetic.
     * @param b The distance from a to b modular arithmetic.
     * @param mod size of the circle.  This can be set to infinity for regular 
     * arithmetic.
     * @return The difference between the two points.
     */
    public static double modDif(double a, double b, double mod){
        if(!Double.isFinite(mod)) return Math.abs(a - b);
        double direct = Math.abs(a - b),
            outside = (mod - Math.max(a, b)) + Math.min(a, b);
        return Math.min(direct, outside);
    }
    
    /**
     * The arc distance between this angle and another. 
     * This will always be a positive number.
     * @param ang The other angle.
     * @return The arc distance between the two angles.
     */
    public double arcDist(Angle ang){
        return modDif(rad(), ang.rad(), 2* Math.PI);
    }
    
    /**
     * Computes the standard deviation, note, the stream is run 3 times including
     * to count the number of elements, so not super efficient.
     * @param angleStream A stream of angles.
     * @return The standard deviation of the the angles.
     */
    public static double stdDev(Supplier<Stream<Angle>> angleStream){
        Angle avg = average(angleStream.get());
        return angleStream.get()
                .mapToDouble(angle -> StdDev.sq(angle.arcDist(avg))).sum()/
                angleStream.get().count();
    }
    
    /**
     * The difference of this angle and another angle.
     * @param ang The other angle.
     * @return The difference of this angle and another angle.
     */
    public Angle minus(Angle ang){
        return new Angle(rad() - ang.rad());
    }
    
    /**
     * The sum of an angle and a scalar in radians.
     * @param radians A scalar in radians.
     * @return An angle.
     */
    public Angle plus(double radians){
        return new Angle(rad() + radians);
    }
    /**
     *
     */
    public static Angle NaN = new Angle(Double.NaN);
    
    /**
     * A new angle that is this angle multiplied by a scalar;
     * @param t The scalar
     * @return A new angle.
     */
    public Angle mult(double t){
        return new Angle(rad() * t);
    }
    
}
