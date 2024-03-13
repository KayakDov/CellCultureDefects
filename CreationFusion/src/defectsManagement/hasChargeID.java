package defectsManagement;

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
    public int getID();
    
    
    
    /**
     * Checks if this defect is tracked (i.e., has an ID assigned).
     * @return true if the defect is tracked, false otherwise.
     */
    public default boolean isTracked() {
        return getID() != SnapDefect.NO_ID;
    }

}

