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
public class PairedSnDef extends PairSnDef {
    
    /**
     * The amount of time from the event, birth or death 
     */
    public final int timeFromEvent;
    
    /**
     * true if this is a pair of twins, false if this is a spouse pair.
     */
    public final boolean birth;


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
     * Set to -1 if unknown.
     * @param birth True if they are twins, false if they are fusion partners.
     */
    public PairedSnDef(PosSnapDefect pos, NegSnapDefect neg, boolean fuseUp, int timeFromEvent, boolean birth) {
        super(pos, neg);
        this.birth = birth;
        this.fuseUp = fuseUp;
        this.timeFromEvent = timeFromEvent;
    }


}
