package creationfusion;

import snapDefects.SnapDefect;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * All the defects at a specific moment in time.
 * @author e. Dov Neimand
 */
public class Frame {
    private final Map<Integer, SnapDefect> posDefects, negDefects;
    public final int time;

    /**
     * The constructor.
     * @param posDefects All the positive defects in the frame.
     * @param negDefects All the negative defects in the frame.
     * @param time The time all these defects were in this frame.
     */
    public Frame(Map<Integer, SnapDefect> posDefects, Map<Integer, SnapDefect> negDefects, int time) {
        this.posDefects = posDefects;
        this.negDefects = negDefects;
        this.time = time;
    }
    
    /**
     * The charge of the frame at this time.
     * @return The charge of all the defects in the frame at this time.
     */
    public double charge(){
        return (posDefects.size() - negDefects.size())*0.5;
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
    public Stream<SnapDefect> positives(){
        return posDefects.values().stream();
    }
    
    /**
     * Are all the snap defects in this frame taken at the same time.
     * @return True if all the snap defects in this frame are taken at the same
     * time, false otherwise.
     */
    public boolean confirmIntegrity(){
        return allDefects().allMatch(def -> def.getTime() == time);
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
    private Map<Integer, SnapDefect> map(boolean charge){
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
    
    
}
