package creationfusion;

import GeometricTools.Vec;
import java.util.Arrays;
import java.util.function.IntToDoubleFunction;

/**
 *
 * @author E. Dov Neimand
 */
public class SnapDefectPair {

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
    public SnapDefectPair(SnapDefect a, SnapDefect b, boolean birth) {
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
     * The angle of the vector from the positive defect to the negative defect,
     * with the x axis.
     *
     * @return The angle of the vector from the positive defect to the negative
     * defect, with the x axis. Returns double.NaN if one of the defects is
     * null;
     */
    public double mpAngle() {
        if (!workingPair()) return Double.NaN;
        return pos.angleTo(neg);
    }

    /**
     * The angle between the positive tail and the negative defect.
     *
     * @return The angle between the positive tail and the negative defect.
     * Returns double.NaN if one of the defects is null;
     */
    public double anglePRel() {
        if (!workingPair()) return Double.NaN;
        return pos.tailAngle() - mpAngle();
    }

    /**
     * The angles between the negative tails and the connecting vector.
     *
     * @return
     */
    public double[] ang123Rel() {
        return angles3(i -> neg.tailAngles()[i] - mpAngle());

    }

    /**
     * True if the tail of the positive points counterclockwise to the negative
     * defect, false otherwise.
     *
     * @return True if the tail of the positive points counterclockwise to the
     * negative defect, false otherwise.
     */
    public boolean fuseUp() {
        return anglePRel() > 0;
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
    private double[] angles3(IntToDoubleFunction f) {
        if (!workingPair()) return new double[0];
        double[] angles3 = new double[3];
        Arrays.setAll(angles3, i -> f.applyAsDouble(i));
        return angles3;
    }

    /**
     * The angles of the tails relative to one another.
     *
     * @return The angles of the tails relative to one another.
     */
    public double[] mp123() {
        return angles3(i -> pos.tailAngle() - neg.tailAngles()[i]);
    }

    /**
     * The average of mp123 mod (2/3)pi.
     *
     * @return The average of mp123 mod (2/3)pi.
     */
    public double mpPhase() {
        return Arrays.stream(mp123()).average().getAsDouble() % (2 * Math.PI / 3);
    }

    /**
     * This is the (angle of the positive relative to the negative defect)
     * relative to the velocity angle.
     *
     * @return This is the (angle of the positive relative to the negative
     * defect) relative to the velocity angle.
     */
    public double anglep1_rel_vel_angle() {
        if(pos.getVelocity() == null) return Double.NaN;
        return anglePRel() - pos.getVelocity().angle();
    }

}
