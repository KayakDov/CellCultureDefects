package defectManagement;

import SnapManagement.PosDefect;

/**
 *
 * @author E. Dov Neimand
 */
public class PosDefectSet extends DefectSet<PosDefect>{
    
    public PosDefectSet(int maxSize) {
        super(maxSize);
        defects = new PosDefect[maxSize];
    }
    
    public PosDefectSet(PosDefectSet pds){
        super(pds);
    }

    @Override
    public boolean charge() {
        return true;
    }
    
}
