package SnapManagement;

import defectManagement.DefectManager;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;
import snapDefects.SnapDefect;

/**
 *
 * @author E. Dov Neimand
 */
public class NegDefect extends Defect {

    /**
     * Constructs from a snap defect.
     *
     * @param sd The snap defect that is used to construct this defect.
     */
    public NegDefect(NegSnapDefect sd) {
        super(sd);
    }
        
    @Override
    public boolean getCharge() {
        return DefectManager.NEG;
    }

    @Override
    public PosDefect getTwin() {
        return (PosDefect) super.getTwin();
    }

    @Override
    public PosDefect getSpouse() {
        return (PosDefect) super.getSpouse();
    }

    @Override
    public PosDefect getPair(boolean birth) {
        return (PosDefect) super.getPair(birth);
    }

    /**
     * Sets the pair of this defect.
     *
     * @param pair The pair of this defect.
     * @param birth True if the pair is a twin, false if it's a spouse.
     */
    public void setPair(PosDefect pair, boolean birth) {
        super.setPair(pair, birth);
    }

    /**
     * Adds a snap defect to the life route of this Defect.
     *
     * @param sd The snap defect to be added.
     */
    public void addLifeSnap(NegSnapDefect sd) {
        super.addSnap(sd);
    }

        
    
    /**
     * Constructs a new defect from a snap defect.
     *
     * @param sd The snap defect to construct this defect.
     */
    public NegDefect(SnapDefect sd) {
        super(sd);
        if(sd.getCharge()) throw new RuntimeException("You may not create a "
                + "negative defect from a positive snap.");
    }
    
    @Override
    public NegSnapDefect snapFromFrame(int time) {
        return (NegSnapDefect) super.snapFromFrame(time);
    }

    @Override
    public NegSnapDefect snapFromEvent(int time, boolean birth) {
        return (NegSnapDefect) super.snapFromEvent(time, birth);
    }

    @Override
    public PairedSnDef snapPairFromEvent(int timeFromEvent, boolean birth) {

        return new PairedSnDef(
                getPair(birth).snapFromEvent(timeFromEvent, birth),
                snapFromEvent(timeFromEvent, birth),
                getPair(birth).isFuseUp(birth),
                timeFromEvent,
                birth);

    }
    
    
    @Override
    public PairedSnDef snapPairFromFrame(int time, boolean birth) {
        PosSnapDefect partnerSnap
                = hasPair(birth) && getPair(birth).aliveAt(time)
                ? getPair(birth).snapFromFrame(time)
                : null;

        return new PairedSnDef(
                partnerSnap, 
                snapFromFrame(time), 
                getPair(birth).isFuseUp(birth), 
                timeFromEvent(time, birth),
                birth
        );
    }
    
    
    
}
