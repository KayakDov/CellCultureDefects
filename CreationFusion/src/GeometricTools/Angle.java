package GeometricTools;

import dataTools.stdDev;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class represents an angle.
 * @author E. Dov Neimand
 */
public class Angle {

    private final double angle;
    
    /**
     * Places an angle between 0 and 2pi.
     * @param posOrNegRadians An angle that may be positive or negative.
     */
    public Angle(double posOrNegRadians) {
        if (posOrNegRadians >= 2 * Math.PI) angle = posOrNegRadians % (2 * Math.PI);
        else if (posOrNegRadians < 0)angle =  2 * Math.PI + posOrNegRadians % (2 * Math.PI);
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
     * This angle in degrees.
     * @return This angle in degrees.
     */
    public double deg(){
        return 360*angle/(2*Math.PI);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "" + rad();
    }
    
    /**
     * The vector on the unit circle represented by this angle.
     * @return 
     */
    public Vec vec(){
        return new Vec(Math.cos(angle), Math.sin(angle));
    }
    
    /**
     * The average of the angles.
     * @param angles
     * @return
     */
    public static Angle average(Stream<Angle> angles){
        return new Angle(angles.map(ang -> ang.vec()).reduce(Vec.origin, (a, b) -> a.plus(b)));
    }
    
    /**
     * The arc distance between this angle and another. 
     * This will always be a positive number.
     * @param ang The other angle.
     * @return The arc distance between the two angles.
     */
    public double arcDist(Angle ang){
        double regAround = Math.abs(rad() - ang.rad());
        double otherAround =
                2*Math.PI - Math.max(rad(), ang.rad()) 
                + Math.min(rad(), ang.rad());
        
        return Math.min(regAround, otherAround);
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
                .mapToDouble(angle -> stdDev.sq(angle.arcDist(avg))).sum()/
                angleStream.get().count();
    }
    
    /**
     *
     */
    public static Angle NaN = new Angle(Double.NaN);
    
}
