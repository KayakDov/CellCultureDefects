package SnapManagement;

import snapDefects.SnapDefect;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;

/**
 * All the defects at a specific moment in time.
 * @author e. Dov Neimand
 */
public class Frame {
    private final Map<Integer, PosSnapDefect> posDefects;
    private final Map<Integer, NegSnapDefect> negDefects;
    public final int time;

    /**
     * The constructor.
     * @param posDefects All the positive defects in the frame.
     * @param negDefects All the negative defects in the frame.
     * @param time The time all these defects were in this frame.
     */
    public Frame(Map<Integer, PosSnapDefect> posDefects, Map<Integer, NegSnapDefect> negDefects, int time) {
        this.posDefects = posDefects;
        this.negDefects = negDefects;
        this.time = time;
    }
    
    /**
     * The charge of the frame at this time.
     * @return The charge of all the defects in the frame at this time.
     */
    public int charge(){
        return (posDefects.size() - negDefects.size());
    }
    
    /**
     * A stream of all the defects.
     * @return A stream of all the defects.
     */
    public Stream<SnapDefect> allDefects(){
        return Stream.concat(posDefects.values().stream(), negDefects.values().stream());
    }
    
    /**
     * A stream of the positive defects.
     * @return A stream of the positive defects.
     */
    public Stream<PosSnapDefect> positives(){
        return posDefects.values().stream();
    }
    /**
     * A stream of the negative defects.
     * @return A stream of the positive defects.
     */
    public Stream<NegSnapDefect> negatives(){
        return negDefects.values().stream();
    }
    
    /**
     * Are all the snap defects in this frame taken at the same time.
     * @return True if all the snap defects in this frame are taken at the same
     * time, false otherwise.
     */
    public boolean confirmIntegrity(){
        return allDefects().allMatch(def -> def.loc.getTime() == time);
    }

    /**
     * The time for every SnapDefect in this frame.
     * @return The time for every SnapDefect in this frame.
     */
    public int getTime() {
        return time;
    }
    
    /**
     * retrieves the positive or negative maps as requested.
     * @param charge The type of map desired.
     * @return The positive or negative maps storing the SnapDefects in this frame.
     */
    private Map<Integer, ? extends SnapDefect> map(boolean charge){
        return charge? posDefects : negDefects;
    }
    
    /**
     * Retieves a SnapDefect from the frame.
     * @param id The id of the desired defect.
     * @param charge The charge of the desired defect.
     * @return The desired defect if it's present, false otherwise.
     */
    public SnapDefect get(int id, boolean charge){
        return map(charge).get(id);
    }
    
    /**
     * Is a snap defect with the requested id in this frame?
     * @param id The id of the queried SnapDefect.
     * @param charge The charge of the queried SnapDefect.
     * @return true if it's in this frame and false otherwise.
     */
    public boolean contains(int id, boolean charge){
        return map(charge).containsKey(id);
    }
    
    /**
     * The number od snap defects in this frame.
     * @return The number od snap defects in this frame.
     */
    public int size(){
        return posDefects.size() + negDefects.size();
    }
    
}
