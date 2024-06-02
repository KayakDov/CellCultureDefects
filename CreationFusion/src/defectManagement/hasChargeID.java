package defectManagement;

import snapDefects.SnapDefect;

/**
 *
 * @author E. Dov Neimand
 */
public interface hasChargeID {
    /**
     * The charge.
     * @return The charge.
     */
    public boolean getCharge();
    /**
     * The ID.
     * @return The ID. 
     */
    public int getId();
   
    
    /**
     * Checks if this defect is tracked (i.e., has an ID assigned).
     * @return true if the defect is tracked, false otherwise.
     */
    public default boolean isTracked() {
        return getId() != SnapDefect.NO_ID;
    }

}

