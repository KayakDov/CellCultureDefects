package SnapManagement;

import GeometricTools.Angle;
import snapDefects.PosSnapDefect;
import snapDefects.NegSnapDefect;
import GeometricTools.Vec;
import java.util.Arrays;
import java.util.function.Function;

/**
 *
 * @author E. Dov Neimand
 */
public class PairSnDef {

    
    /**
     * The amount of time from the event, birth or death 
     */
    public final int timeFromEvent;
    /**
     * The positive defect in the pair.
     */
    public final PosSnapDefect pos;
    /**
     * The negative defect in the pair.
     */
    public final NegSnapDefect neg;

    /**
     * true if this is a pair of twins, false if this is a spouse pair.
     */
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
     * True if the tail of the positive points counterclockwise to the negative
     * defect most of the time, false otherwise.
     */
    public final boolean fuseUp;

    /**
     * Creates a pair of oppositely charged snap defects.
     *
     * @param pos A defect.
     * @param neg A defect with opposite charge of a.
     * @param fuseUp True if the positive snap defect points clockwise around
     * the negative snap defect on average.
     * @param timeFromEvent The amount of time from the birth or death event.
     * @param birth True if they are twins, false if they are fusion partners.
     */
    public PairSnDef(PosSnapDefect pos, NegSnapDefect neg, boolean fuseUp, int timeFromEvent, boolean birth) {
        this.pos = pos;
        this.neg = neg;
        this.birth = birth;
        this.fuseUp = fuseUp;
        this.timeFromEvent = timeFromEvent;
    }

    /**
     * The distance between the two defects.
     *
     * @return The distance between the two defects. Infinity if one of the
     * defects is null.
     */
    public double dist() {
        if (!workingPair()) return Double.POSITIVE_INFINITY;
        return pos.loc.dist(neg.loc);
    }

    /**
     *      *
     * The black line relative to the x axis, equivalently, the angle of the
     * vector from the minus defect to the positive defect.
     *
     * @return The angle of the vector from the negative defect to the positive
     * defect, with the x axis. Returns double.NaN if one of the defects is
     * null;
     */
    public Angle mpAngle() {
        if (!workingPair()) return Angle.NaN;
        return new Angle(neg.loc.minus(pos.loc));
    }

    /**
     * The angle between the positive tail and the negative defect.
     *
     * @return The angle between the positive tail and the negative defect.
     * Returns double.NaN if one of the defects is null;
     */
    public Angle anglePRel() {
        if (!workingPair()) return Angle.NaN;
        return pos.tailAngle().minus(mpAngle());
    }

    /**
     * The angles between the negative tails and the connecting vector.
     *
     * @return
     */
    public Angle[] ang123Rel() {
        return angles3(i -> neg.tailAngle()[i].minus(mpAngle()));

    }

    /**
     * The movement of the positive defect relative to the negative defect.
     *
     * @return The movement of the positive defect relative to the negative
     * defect.
     */
    public Vec relVelocity() {
        if (neg.getVelocity() == null || pos.getVelocity() == null) return null;
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
    public Angle[] tailAnlgesRel() {
        return angles3(i -> neg.tailAngle()[i].minus(pos.tailAngle()));
    }

    /**
     * The average of tailAnlgesRel mod (2/3)pi.
     *
     * @return The average of tailAnlgesRel mod (2/3)pi.
     */
    public double mpPhase() {
        return Arrays.stream(tailAnlgesRel())
                .mapToDouble(angle -> angle.rad())
                .average().getAsDouble() % (2 * Math.PI / 3); //TODO:Finding the avewrage of angles is not so simple.  Correct
    }

    /**
     * This is the (angle of the positive relative to the negative defect)
     * relative to the velocity angle.
     *
     * @return This is the (angle of the positive relative to the negative
     * defect) relative to the velocity angle.
     */
    public Angle anglep1_rel_vel_angle() {
        if (pos.getVelocity() == null) return Angle.NaN;
        return anglePRel().minus(pos.getVelocity().angle());
    }

}
