package SnapManagement;

import GeometricTools.Angle;
import defectManagement.DefectManager;
import java.util.Arrays;
import java.util.List;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;
import snapDefects.SnapDefect;

/**
 *
 * @author E. Dov Neimand
 */
public class PosDefect extends Defect {
    
    /**
     * Constructs a new defect from a snap defect.
     *
     * @param sd The snap defect to construct this defect.
     */
    public PosDefect(PosSnapDefect sd) {
        super(sd);
    }
    
    
    /**
     * Constructs a new defect from a snap defect.
     *
     * @param sd The snap defect to construct this defect.
     */
    public PosDefect(SnapDefect sd) {
        super(sd);
        if(!sd.getCharge()) throw new RuntimeException("You may not create a "
                + "postive defect from a negative snap.");
    }

    @Override
    public boolean getCharge() {
        return DefectManager.POS;
    }

    @Override
    public NegDefect getTwin() {
        return (NegDefect)super.getTwin();
    }

    @Override
    public NegDefect getSpouse() {
        return (NegDefect)super.getSpouse();
    }

    @Override
    public NegDefect getPair(boolean birth) {
        return (NegDefect)super.getPair(birth); 
    }

    /**
     * Sets the pair of this defect.
     * @param pair The pair of this defect.
     * @param birth True if the pair is a twin, false if it's a spouse.
     */
    public void setPair(NegDefect pair, boolean birth) {
        super.setPair(pair, birth);
    }

    /**
     * Adds a snap defect to the life route of this Defect.
     *
     * @param sd The snap defect to be added.
     */
    public void addLifeSnap(PosSnapDefect sd) {
        super.addLifeSnap(sd);
    }

    @Override
    public PosSnapDefect snapFromFrame(int time) {
        return (PosSnapDefect)super.snapFromFrame(time); 
    }

    @Override
    public void prepForTracking() {
        lifeCourse = new PosSnapDefect[age() + 1];
    }

    @Override
    public PosSnapDefect snapFromEvent(int time, boolean birth) {
        return (PosSnapDefect)super.snapFromEvent(time, birth); 
    }

    @Override
    public PairSnDef snapPairFromEvent(int timeFromEvent, boolean birth) {
         
        PosSnapDefect snap = snapFromEvent(timeFromEvent, birth);
        NegSnapDefect snapPair = hasPair(birth)?
                getPair(birth).snapFromEvent(timeFromEvent, birth): null;
        
        return new PairSnDef(snap, snapPair, isFuseUp(birth), timeFromEvent, birth);
    }

    @Override
    public PairSnDef snapPairFromFrame(int time, boolean birth) {
        NegSnapDefect partnerSnap
                = hasPair(birth) && getPair(birth).aliveAt(time)
                ? getPair(birth).snapFromFrame(time)
                : null;

        return new PairSnDef(
                snapFromFrame(time), 
                partnerSnap, 
                isFuseUp(birth), 
                timeFromEvent(time, birth), 
                birth
        );
    }
    
    

    
    /**
     * The average of the anlgePRel
     *
     * @param birth average for birth or death.
     * @param limitTimeFromEvent The proximity to the event to consider.
     * @return The average angle.
     */
    public Angle avgAnglePRel(boolean birth, int limitTimeFromEvent) {
        return hasPair(birth)
                ? Angle.average(AnglesPRel(birth, limitTimeFromEvent)) : Angle.NaN;
    }
    
    private boolean fuseUpTwin, fuseUpSpouse;
    /**
     * Is this defect on average pointing its tail clockwise or counterclockwise
     * to its pair.
     * @param timeLimitFromEvent How long form the creation or fusion event 
     * are we calculating the average over.
     */
    public void setFuseUp(int timeLimitFromEvent){
        fuseUpTwin = avgAnglePRel(DefectManager.BIRTH, timeLimitFromEvent).rad() < Math.PI;
        fuseUpSpouse = avgAnglePRel(DefectManager.DEATH, timeLimitFromEvent).rad() < Math.PI;
    }

    /**
     * Is this defect on average fuse up.
     * @param birth True if it's fuse up with regard to the twin, false for spouse.
     * @return True if it's fuse up, false if it's fuse down.
     */
    public boolean isFuseUp(boolean birth) {
        return birth?fuseUpTwin:fuseUpSpouse;
    }

    @Override
    public List<PosSnapDefect> getLifeCourse() {
        return Arrays.asList((PosSnapDefect[])lifeCourse);
    }

    
}
