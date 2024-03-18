package SnapManagement;

import GeometricTools.Angle;
import snapDefects.PosSnapDefect;
import snapDefects.NegSnapDefect;
import snapDefects.SnapDefect;
import GeometricTools.Vec;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author E. Dov Neimand
 */
public class PairSnDef {

    public final PosSnapDefect pos;
    public final NegSnapDefect neg;
    public final boolean birth;

    /**
     * Are both defects functional?
     *
     * @return True if neither defect is null, false otherwise.
     */
    public boolean workingPair() {
        return pos != null && neg != null;
    }

    /**
     * Creates a pair of oppositely charged snap defects.
     *
     * @param a A defect.
     * @param b A defect with opposite charge of a.
     * @param birth True if they are twins, false if they are fusion partners.
     */
    public PairSnDef(SnapDefect a, SnapDefect b, boolean birth) {
        if (a == null & b == null) {
            pos = null;
            neg = null;
        } else {
            this.pos = getPos(a, b);
            this.neg = getNeg(a, b);
        }
        this.birth = birth;
    }

    /**
     * returns a positive snap defect of the two offered. One must be positive
     * and one must be negative.
     *
     * @param a A snap snap defect.
     * @param b An oppositely charges snap defect.
     * @return The positive of the two defects.
     */
    public final static PosSnapDefect getPos(SnapDefect a, SnapDefect b) {
        if (a == null) return getPos(b, a);
        return (PosSnapDefect) (a.getCharge() ? a : b);
    }

    /**
     * Returns a negative snap defect of the two offered. One must be positive
     * and one must be negative.
     *
     * @param a A snap snap defect.
     * @param b An oppositely charges snap defect.
     * @return The negative of the two defects.
     */
    public final static NegSnapDefect getNeg(SnapDefect a, SnapDefect b) {
        if (a == null) return getNeg(b, a);
        return (NegSnapDefect) (a.getCharge() ? b : a);
    }

    /**
     * The distance between the two defects.
     *
     * @return The distance between the two defects. Inifnity if one of the
     * defects is null.
     */
    public double dist() {
        if (!workingPair()) return Double.POSITIVE_INFINITY;
        return pos.dist(neg);
    }

    /**
     *      *
     * The black line relative to the x axis, equivalently, 
     * the angle of the vector from the minus defect to the positive defect.
     * 
     * @return The angle of the vector from the negative defect to the positive
     * defect, with the x axis. Returns double.NaN if one of the defects is
     * null;
     */
    public Angle mpAngle() {
        if (!workingPair()) return Angle.NaN;
        return new Angle(pos.minus(neg));
    }

    /**
     * The angle between the positive tail and the negative defect.
     *
     * @return The angle between the positive tail and the negative defect.
     * Returns double.NaN if one of the defects is null;
     */
    public Angle anglePRel() {
        if (!workingPair()) return Angle.NaN;
        return new Angle(pos.tailAngle() - mpAngle().rad());
    }

    /**
     * The angles between the negative tails and the connecting vector.
     *
     * @return
     */
    public Angle[] ang123Rel() {
        return angles3(i -> new Angle(neg.tailAngle()[i] - mpAngle().rad()));

    }

    /**
     * True if the tail of the positive points counterclockwise to the negative
     * defect, false otherwise.
     *
     * @return True if the tail of the positive points counterclockwise to the
     * negative defect, false otherwise.
     */
    public boolean fuseUp() {
        return anglePRel().rad() < Math.PI; //TODO:  define each set of snap defects by the majarity of all fuse ups over lifespan. //Check if this flips?
    }

    /**
     * The movement of the positive defect relative to the negative defect.
     *
     * @return The movement of the positive defect relative to the negative
     * defect.
     */
    public Vec relVelocity() {
        if(neg.getVelocity() == null || pos.getVelocity() == null) return null;
        return pos.getVelocity().minus(neg.getVelocity());
    }

    /**
     * Creates a new array with 3 angles in it.
     *
     * @return A new array with 3 angles in it.
     */
    private Angle[] angles3(Function<Integer, Angle> f) {
        if (!workingPair()) return new Angle[0];
        Angle[] angles3 = new Angle[3];
        Arrays.setAll(angles3, i -> f.apply(i));
        return angles3;
    }

    /**
     * The angles of the tails relative to one another.
     *
     * @return The angles of the tails relative to one another.
     */
    public Angle[] mp123() {
        return angles3(i -> new Angle(neg.tailAngle()[i] - pos.tailAngle()));
    }

    /**
     * The average of mp123 mod (2/3)pi.
     *
     * @return The average of mp123 mod (2/3)pi.
     */
    public double mpPhase() {
        return Arrays.stream(mp123()).mapToDouble(angle -> angle.rad()).average().getAsDouble() % (2 * Math.PI / 3); //TODO:Finding the avewrage of angles is not so simple.  Correct
    }

    /**
     * This is the (angle of the positive relative to the negative defect)
     * relative to the velocity angle.
     *
     * @return This is the (angle of the positive relative to the negative
     * defect) relative to the velocity angle.
     */
    public Angle anglep1_rel_vel_angle() {
        if(pos.getVelocity() == null) return Angle.NaN;
        return new Angle(anglePRel().rad() - new Angle(pos.getVelocity()).rad());
    }

}
