package creationfusion;

import java.util.Arrays;

/**
 *
 * @author E. Dov Neimand
 */
public class SnapDefectPair {
    public final PosSnapDefect pos;
    public final NegSnapDefect neg;
    public final boolean birth;

    /**
     * Creates a pair of oppositely charged snap defects.
     * @param a A defect.
     * @param b A defect with opposite charge of a.
     * @param birth True if they are twins, false if they are fusion partners.
     */
    public SnapDefectPair(SnapDefect a, SnapDefect b, boolean birth) {
        this.pos = getPos(a, b);
        this.neg = getNeg(a, b);
        this.birth = birth;
    }
    
    /**
     * returns a positive snap defect of the two offered.  One must be positive 
     * and one must be negative.
     * @param a A snap snap defect.
     * @param b An oppositely charges snap defect.
     * @return The positive of the two defects.
     */
    public final static PosSnapDefect getPos(SnapDefect a, SnapDefect b){
        return (PosSnapDefect)(a.getCharge()?a:b);
    }
    
    /**
     * Returns a negative snap defect of the two offered.  One must be positive 
     * and one must be negative.
     * @param a A snap snap defect.
     * @param b An oppositely charges snap defect.
     * @return The negative of the two defects.
     */
    public final static NegSnapDefect getNeg(SnapDefect a, SnapDefect b){
        return (NegSnapDefect)(a.getCharge()?b:a);
    }
    
    /**
     * The distance between the two defects.
     * @return 
     */
    public double dist(){
        if(pos == null || neg == null) return Double.POSITIVE_INFINITY;
        return pos.dist(neg);
    }
    
    /**
     * The angle of the vector from the positive defect to the negative defect, 
     * with the x axis.
     * @return The angle of the vector from the positive defect to the negative 
     * defect, with the x axis.
     */
    private double connectingAngle(){
        if(pos == null || neg == null) return Double.NaN;
        return pos.angleTo(neg);
    }
    
    /**
     * The angle between the positive tail and the negative defect.
     * @return The angle between the positive tail and the negative defect.
     */
    public double anglePRel(){
        if(pos == null || neg == null) return Double.NaN;
        return pos.tailAngle() - connectingAngle();
    }
    
    /**
     * The angles between the negative tails and the connecting vector.
     * @return 
     */
    public double[] ang123Rel(){
        if(pos == null || neg == null) return new double[0];
        double[] ang123Rel = new double[3];
        Arrays.setAll(ang123Rel, i -> neg.tailAngles()[i] - connectingAngle());
        return ang123Rel;
        
    }
    
    /**
     * True if the tail of the positive  points counterclockwise to the negative
     * defect, false otherwise.
     * @return True if the tail of the positive  points counterclockwise to the negative
     * defect, false otherwise.
     */
    public boolean fuseUp(){
        return anglePRel() > 0;
    }
    
}
