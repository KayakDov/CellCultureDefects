package creationfusion;

import java.util.Set;
import java.util.stream.Stream;

/**
 * All the defects at a specific moment in time.
 * @author e. Dov Neimand
 */
public class Frame {
    private final Set<SnapDefect> posDefects, negDefects;
    public final int time;

    /**
     * The constructor.
     * @param posDefects All the positive defects in the frame.
     * @param negDefects All the negative defects in the frame.
     * @param time The time all these defects were in this frame.
     */
    public Frame(Set<SnapDefect> posDefects, Set<SnapDefect> negDefects, int time) {
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
    public Stream<SnapDefect> all(){
        return Stream.concat(posDefects.stream(), negDefects.stream());
    }
    
    /**
     * Are all the snap defects in this frame taken at the same time.
     * @return True if all the snap defects in this frame are taken at the same
     * time, false otherwise.
     */
    public boolean confirmIntegrity(){
        return all().allMatch(def -> def.getTime() == time);
    }

    public int getTime() {
        return time;
    }
    
    
}
