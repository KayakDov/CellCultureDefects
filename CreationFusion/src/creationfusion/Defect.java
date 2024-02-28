package creationfusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes a defect tracked over time.
 */
public class Defect {

    private Defect twin;
    private Defect spouse;
    private SpaceTemp birth, death;
    final int ID;
    final boolean charge;
    ArrayList<Integer> abscenses;
    private double[] distFromSpouse;
    private double[] distFromTwin;

    /**
     * Constructs a new Defect instance from a SnapDefect.
     * @param sd The SnapDefect that gives birth to this defect.
     
     */
    public Defect(SnapDefect sd) {
        this.abscenses = new ArrayList<>();
        death = sd;
        birth = sd;
        this.ID = sd.getID();
        this.charge = sd.getCharge();
        final int defaultDistanceTimeMonitoring = 20;
    }

    /**
     * Constructs a new Defect instance from a SnapDefect with a twin defect.
     * @param sd The SnapDefect that gives birth to this defect.
     * @param twin The oppositely charged defect created with this one.
     */
    public Defect(SnapDefect sd, Defect twin) {
        this(sd);
        setTwin(twin);        
    }

    /**
     * Checks if this defect has a twin defect.
     * @return true if this defect has a twin, false otherwise.
     */
    public boolean hasTwin() {
        return twin != null;
    }

    /**
     * Checks if this defect has a spouse defect.
     * @return true if this defect has a spouse, false otherwise.
     */
    public boolean hasSpouse() {
        return spouse != null;
    }

    /**
     * Sets another defect as having been created together with this defect.
     * @param twin The defect that was created with this defect.
     */
    public void setTwin(Defect twin) {
        if (twin != null) {
            this.twin = twin;
            twin.twin = this;
        }
    }

    /**
     * Sets another defect as having fused with this defect.
     * @param spouse The defect that has fused with this defect.
     */
    public void setSpouse(Defect spouse) {
        if (spouse != null) {
            this.spouse = spouse;
            spouse.spouse = this;
        }
    }

    /**
     * Gets the birth moment of this defect.
     * @return The birth moment of this defect.
     */
    public SpaceTemp getBirth() {
        return birth;
    }
    
    /**
     * The birthday or deathday.
     * @param birth True if the birth is desired, false otherwise.
     * @return If birth is true, then the birth, otherwise the death.
     */
    public SpaceTemp get(boolean birth){
        return birth? getBirth():getDeath();
    }

    /**
     * Gets the twin of this defect.
     * @return The the twin of this defect.
     */
    public Defect getTwin() {
        return twin;
    }

    /**
     * Gets the spouse of this defect.
     * @return The spouse of this defect.
     */
    public Defect getSpouse() {
        return spouse;
    }

    /**
     * Clears the twin and spouses this defect.
     */
    public void clearPairs() {
        twin.twin = spouse.spouse = null;
        twin = spouse = null;
    }

    /**
     * Calculates the age of this defect.
     * @return The age of this defect.
     */
    public int age() {
        return getDeath().getTime() - getBirth().getTime();
    }

    /**
     * When this defect was last seen.
     * @return 
     */
    public SpaceTemp getDeath() {
        return death;
    }
    
    

    /**
     * Updates the time and location of this defect.
     * @param st The current time and location.
     */
    public void update(SpaceTemp st) {
        if(st.getTime() < getBirth().getTime()) birth = st;
        else if(st.getTime() > getDeath().getTime()) death = st;
    }
    
    /**
     * The deathday is reset to the birthday.
     */
    public void resetDeath(){
        death = birth;
    }

    // toString method
    @Override
    public String toString() {
        return "ID: " + getID() + (getCharge() ? "+" : "-") + " born at " + birth + ", time: [" + getBirth().getTime() + ", " + getDeath().getTime() + "]";
    }

    /**
     * The ID of this node.
     * @return The ID of this node.
     */
    public int getID() {
        return ID;
    }
    
    /**
     * The charge of this node.
     * @return The charge of this node.
     */
    public boolean getCharge(){
        return charge;
    }
    
    public boolean near(Defect other, boolean birth, double dist, int time){
        if(birth) return getBirth().near(other.getBirth(), dist, time);
        else return getDeath().near(other.getDeath(), dist, time);
    }
    
    /**
     * Adds to the amount of time this Defect was not accounted for between
     * its birth and death.
     * @param time The amount of time abs
     */
    public void addToTimeAWOL(int time){
        abscenses.add(time);
    }

    /**
     * The list of absence lengths.
     * @return The list of absence lengths.
     */
    public List<Integer> getAbscenses() {
        return Collections.unmodifiableList(abscenses);
    }
    
    
    /**
     * True if the spouse is the twin, false otherwise.
     * @return True if the spouse is the twin, false otherwise.
     */
    public boolean spouseIsTwin(){
        return getSpouse() == getTwin();
    }
    
    /**
     * Prepares this defect to track the distances from its spouse and twin.
     * @param maxDistanceMonitoring The maximum amount of time the distance
     * will be monitored for.
     */
    public void prepForDistances(int maxDistanceMonitoring){
        distFromSpouse = new double[Math.min(maxDistanceMonitoring, age())];
        distFromTwin = new double[Math.min(maxDistanceMonitoring, age())];
    }
    
    /**
     * Sets the distance from the twin.
     * @param dist The distance from the twin.
     * @param frameNumber This is the time elapsed since the first frame.
     * @return True The operation was a success.
     */
    public boolean setDistFromTwin(int dist, int frameNumber){
        if(frameNumber > getDeath().getTime()) return false;
        distFromTwin[frameNumber - getBirth().getTime()]= dist;
        return true;
    }
    
    
    /**
     * Sets the distance from the spouse.
     * @param dist The distance from the spouse.
     * @param frameNumber This is the time elapsed since the first frame.
     * @return The operation was a success.
     */
    public boolean setDistFromSpouse(int dist, int frameNumber){
        if(dist < getBirth().getTime()) return false;
        distFromSpouse[getDeath().getTime() - frameNumber]= dist;
        return true;
    }
    
    /**
     * The distance from the partner.
     * @param timeFromEvent
     * @param birth True for distance from twin, false for distance from spouse.
     * @return The distance from the partner.
     */
    public double getDist(int timeFromEvent, boolean birth){
        return (birth?distFromTwin:distFromSpouse)[timeFromEvent];
    }
}
