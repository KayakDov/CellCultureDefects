package defectManagement;

import SnapManagement.Defect;
import SnapManagement.NegDefect;

/**
 * A set of negative defects.
 * @author E. Dov Neimand
 */
public class NegDefectSet extends DefectSet<NegDefect>{

    private final NegDefect[] defects;

    /**
     * Constructs an empty set of negative defects.
     * @param maxSize One greater then the max id that will be in the set.
     */
    public NegDefectSet(int maxSize) {
        super(maxSize);
        this.defects = new NegDefect[maxSize];
    }
    
    
    
    @Override
    protected void set(int i, NegDefect defect) {
        defects[i] = defect;
    }

    @Override
    protected NegDefect get(int i) {
        return defects[i];
    }

    @Override
    public boolean charge() {
        return DefectManager.NEG;
    }

    @Override
    protected Defect[] array() {
        return defects;
    }
    
}
