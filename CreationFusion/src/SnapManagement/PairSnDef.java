package SnapManagement;

import GeometricTools.Angle;
import GeometricTools.OpenSpaceTimeBall;
import GeometricTools.Vec;
import defectManagement.DefectManager;
import java.util.Arrays;
import java.util.function.Function;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;

/**
 *
 * @author E. Dov Neimand
 */
public class PairSnDef {
    
    /**
     * The positive defect in the pair.
     */
    public final PosSnapDefect pos;
    /**
     * The negative defect in the pair.
     */
    public final NegSnapDefect neg;

    /**
     * Constructs a pair of snap defects.
     * @param pos The positive defect.
     * @param neg The negative defect.
     */
    public PairSnDef(PosSnapDefect pos, NegSnapDefect neg) {
        this.pos = pos;
        this.neg = neg;
    }

    /**
     * Are both defects functional?
     *
     * @return True if neither defect is null, false otherwise.
     */
    public boolean workingPair() {
        return pos != null && neg != null;
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
     * 
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
     * Does this defect have a velocity?
     * @return True if the defect has a velocity, false otherwise.
     */
    public boolean hasVelocity(){
        return pos.getVelocity() != null && neg.getVelocity() != null && 
                pos.getVelocity().isFinite() && neg.getVelocity().isFinite();
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
     * @param f a function to use on the three angles.
     * @return A new array with 3 angles in it.
     */
    protected Angle[] angles3(Function<Integer, Angle> f) {
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
        return Arrays.stream(tailAnlgesRel()).mapToDouble(angle -> angle.rad()).average().getAsDouble() % (2 * Math.PI / 3); //TODO:Finding the avewrage of angles is not so simple.  Correct
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
    
    /**
     * The positive defect.
     * @param dm The defect manager.
     * @return The positive defect.
     */
    private PosDefect posDef(DefectManager dm){
        return (PosDefect)dm.getDefect(pos);
    }
    
    /**
     * The negative defect.
     * @param dm The defect manager.
     * @return The negative defect.
     */
    private NegDefect negDef(DefectManager dm){
        return (NegDefect)dm.getDefect(neg);
    }
    
    /**
     * These two SnapDefects' defects are pairs.
     * @param proximity How close they need to be together at birth/death to be 
     * considered a pair.
     * @param dm The defect manager.
     * @param birth The event in questions is birth, true, or annihilation false.
     * @return True if the they are born (for birth == true) or die 
     * (for birth == false) together.
     */
    public boolean shareEvent(OpenSpaceTimeBall proximity, DefectManager dm, boolean birth){
        return proximity.near(posDef(dm).get(birth), dm.getDefect(neg).get(birth));
    }
   
    
}
