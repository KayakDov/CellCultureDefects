package defectManagement;

import SnapManagement.Defect;
import SnapManagement.PosDefect;

/**
 * A set of positive defects.
 * @author E. Dov Neimand
 */
public class PosDefectSet extends DefectSet<PosDefect>{

    private final PosDefect[] defects;

    /**
     * Constructs a new defect set.
     * @param maxSize One greater than the maximum id that will be held in the set.
     */
    public PosDefectSet(int maxSize) {
        super(maxSize);
        this.defects = new PosDefect[maxSize];
    }
    
    
    
    @Override
    protected void set(int i, PosDefect defect) {
        defects[i] = defect;
    }

    @Override
    protected PosDefect get(int i) {
        return defects[i];
    }

    @Override
    public boolean charge() {
        return DefectManager.POS;
    }

    @Override
    protected Defect[] array() {
        return defects;
    }
    
}
