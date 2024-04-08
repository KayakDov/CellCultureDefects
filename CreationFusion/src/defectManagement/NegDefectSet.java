package defectManagement;

import SnapManagement.NegDefect;

/**
 *
 * @author E. Dov Neimand
 */
public class NegDefectSet extends DefectSet<NegDefect>{
    
    public NegDefectSet(int maxSize) {
        super(maxSize);
        defects = new NegDefect[maxSize];
    }

    /**
     * A copy constructor
     * @param nds 
     */
    public NegDefectSet(NegDefectSet nds) {
        super(nds);
    }

    
    
    @Override
    public boolean charge() {
        return false;
    }
    
}
