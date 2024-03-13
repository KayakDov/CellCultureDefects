package creationfusion;

import snapDefects.PosSnapDefect;
import snapDefects.NegSnapDefect;
import snapDefects.SnapDefect;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Describes a defect tracked over time.
 */
public class Defect implements hasChargeID{

    private Defect twin;
    private Defect spouse;
    private SpaceTemp birth, death;
    final int ID;
    final boolean charge;
    private SnapDefect[] lifeCourse;

    /**
     * Constructs a new Defect instance from a SnapDefect.
     *
     * @param sd The SnapDefect that gives birth to this defect.
     *
     */
    public Defect(SnapDefect sd) {
        death = sd;
        birth = sd;
        this.ID = sd.getID();
        this.charge = sd.getCharge();
    }

    /**
     * Constructs a new Defect instance from a SnapDefect with a twin defect.
     *
     * @param sd The SnapDefect that gives birth to this defect.
     * @param twin The oppositely charged defect created with this one.
     */
    public Defect(SnapDefect sd, Defect twin) {
        this(sd);
        setTwin(twin);
    }

    /**
     * Checks if this defect has a twin defect.
     *
     * @return true if this defect has a twin, false otherwise.
     */
    public boolean hasTwin() {
        return twin != null;
    }

    /**
     * Checks if this defect has a spouse defect.
     *
     * @return true if this defect has a spouse, false otherwise.
     */
    public boolean hasSpouse() {
        return spouse != null;
    }

    /**
     * Sets another defect as having been created together with this defect.
     *
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
     *
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
     *
     * @return The birth moment of this defect.
     */
    public SpaceTemp getBirth() {
        return birth;
    }

    /**
     * The birthday or deathday.
     *
     * @param birth True if the birth is desired, false otherwise.
     * @return If birth is true, then the birth, otherwise the death.
     */
    public SpaceTemp get(boolean birth) {
        return birth ? getBirth() : getDeath();
    }

    /**
     * Does this Defect have the desired partner?
     *
     * @param birth True to check for a twin, false to check for a spouse.
     * @return True if the desired partner exists, false otherwise.
     */
    public boolean hasPartner(boolean birth) {
        return birth ? hasTwin() : hasSpouse();
    }

    /**
     * The partner of this Defect if it exists, otherwise null.
     *
     * @param birth True for a twin, false for a spouse.
     * @return The partner of this Defect.
     */
    public Defect getPartner(boolean birth) {
        return birth ? getTwin() : getSpouse();
    }

    /**
     * Gets the twin of this defect.
     *
     * @return The the twin of this defect.
     */
    public Defect getTwin() {
        return twin;
    }

    /**
     * Gets the spouse of this defect.
     *
     * @return The spouse of this defect.
     */
    public Defect getSpouse() {
        return spouse;
    }

    /**
     * Clears the twin and spouses this defect.
     */
    public void clearPairs() {
        if(hasTwin())twin.twin = null;
        if(hasSpouse()) spouse.spouse = null;
        twin = spouse = null;
    }

    /**
     * Calculates the age of this defect.
     *
     * @return The age of this defect.
     */
    public int age() {
        return getDeath().getTime() - getBirth().getTime();
    }

    /**
     * When this defect was last seen.
     *
     * @return
     */
    public SpaceTemp getDeath() {
        return death;
    }

    
    /**
     * Updates the time and location of this defect.
     *
     * @param sd The current time and location.
     */
    public synchronized void updateBirthDeath(SpaceTemp sd) {
        if (sd.getTime() < getBirth().getTime()) birth = sd;
        else if (sd.getTime() > getDeath().getTime()) death = sd;
    }

    /**
     * Adds a snap defect to the life route of this Defect.
     *
     * @param sd The snap defect to be added.
     */
    public void addLifeSnap(SnapDefect sd) {
        lifeCourse[sd.getTime() - birth.getTime()] = sd;
    }

    /**
     * The location of this defect at the given time. Note, you must prep and
     * load lifeCourse before calling this method.
     *
     * @param time The time (frame number) of the desired location.
     * @return The desired SnapDefect.
     */
    public SnapDefect snapFromFrame(int time) {
        if (time < getBirth().getTime() || time > getDeath().getTime())
            throw new IllegalArgumentException("This defect was not allive at " + time + ".  It was born at " + getBirth().getTime() + " and died at " + getDeath().getTime());
        return lifeCourse[time - getBirth().getTime()];
    }

    /**
     * The deathday is reset to the birthday.
     */
    public void resetBirthDeath() {
        death = birth = null;
    }

    // toString method
    @Override
    public String toString() {
        return "ID: " + getID() + (getCharge() ? "+" : "-") + " born at " + birth + ", time: [" + getBirth().getTime() + ", " + getDeath().getTime() + "]";
    }

    /**
     * The ID of this node.
     *
     * @return The ID of this node.
     */
    public int getID() {
        return ID;
    }

    /**
     * The charge of this node.
     *
     * @return The charge of this node.
     */
    public boolean getCharge() {
        return charge;
    }

    /**
     * Is this defect near another defect during a birth or death event?
     *
     * @param other The other defect.
     * @param birth Birth or death?
     * @param dist The distance from one to the other in the plane.
     * @param time The distance from one to the other in time.
     * @return True if the defects are near each other, false otherwise.
     */
    public boolean near(Defect other, boolean birth, double dist, int time) {
        if (birth) return getBirth().near(other.getBirth(), dist, time);
        else return getDeath().near(other.getDeath(), dist, time);
    }

    /**
     * True if the spouse is the twin, false otherwise.
     *
     * @return True if the spouse is the twin, false otherwise.
     */
    public boolean spouseIsTwin() {
        return getSpouse() == getTwin();
    }

    /**
     * Prepares this defect to track the defectPairs from its spouse and twin.
     */
    public void prepForTracking() {
        lifeCourse = charge ? new PosSnapDefect[age() + 1] : new NegSnapDefect[age() + 1];
    }

    /**
     * Did this defect exists during the proffered time?
     *
     * @param time The time in question.
     * @return True if the defect existed at the given time, false otherwise.
     */
    public boolean duringLifeTime(int time) {
        if (time < getBirth().getTime() || time > getDeath().getTime())
            return false;
        if (lifeCourse != null) return snapFromFrame(time) != null;
        return true;
    }

    /**
     * The SnapDefect at the given time from birth/death.
     *
     * @param time The time from birth/death.
     * @param birth True for time from birth, false for time from death.
     * @return The snap defect at the given time from birth/death.  If the time
     * is greater than the age, then null is returned.
     */
    public SnapDefect snapFromEvent(int time, boolean birth) {
        if(time > age()) return null;
        return lifeCourse[birth ? time : lifeCourse.length - 1 - time];
        
    }

    /**
     * A pair of snap defects.
     * @param birth True if twins are desired, false for fusion partners.
     * @param timeFromEvent The amount of time form birth or death of this defect.
     * @return 
     */
    public SnapDefectPair snapPairFromEvent(int timeFromEvent, boolean birth){
        return new SnapDefectPair(
                snapFromEvent(timeFromEvent, birth), 
                getPartner(birth).snapFromEvent(timeFromEvent, birth), 
                birth);
    }
    
    /**
     * Gets the partners snap defect at the desired time.
     *
     * @param time The time from the event that the SnapDefect is desired.
     * @param birth True for birth/twin and false for death/spouse.
     * @return The snap defect of the partner at the requested time. If no
     * defect is available, then a null value is returned.
     */
    public SnapDefectPair snapPairFromFrame(int time, boolean birth) {
        SnapDefect partnerSnap = 
                hasPartner(birth) && getPartner(birth).duringLifeTime(time)? 
                getPartner(birth).snapFromFrame(time): 
                null;
        
        return new SnapDefectPair(snapFromFrame(time), partnerSnap, birth);
    }

    /**
     * A stream of this defects snapsDefects together with their birth or 
     * death pairs.
     * @param birth True for twin, false for spouses.
     * @return A stream of this defects snapsDefects together with their birth 
     * or death pairs.
     */
    public Stream<SnapDefectPair> defectPairs(boolean birth) {

        return hasPartner(birth)?
                IntStream.range(0, lifeCourse.length)
                        .mapToObj(i -> snapPairFromFrame(getBirth().getTime() + i, birth))
                        .filter(sdp -> sdp.workingPair())
                :Stream.of();

    }

    /**
     * Is this defects entire life course meant to be tracked.
     * @return 
     */
    public boolean followingLifeCourse(){
        return lifeCourse != null;
    }
    
    /**
     * Sets the displacement angles of all the snap defects.
     */
    public void setDisplacementAngles(){
        if(lifeCourse.length <= 1) return;
        IntStream.range(1, lifeCourse.length - 1)
                .filter(i -> lifeCourse[i] != null)
                .forEach(i -> lifeCourse[i].setVelocity(lifeCourse[i - 1], lifeCourse[i + 1]));
       lifeCourse[0].setVelocity(null, lifeCourse[1]);
       lifeCourse[lifeCourse.length - 1].setVelocity(lifeCourse[lifeCourse.length - 2], null);
    }
    
    /**
     * The distance from the location this defect was created.
     * @param timeFromBirth The time afterbirth the distance is to be taken.
     * @return The distance from the place of birth.
     */
    public double displacement(int timeFromBirth){
        return birth.dist(snapFromEvent(timeFromBirth, DefectManager.BIRTH));
    }
    
    
    
//    
//    /**
//     * An array of distances.  If this defect is married to its spouse, then
//     * the array terminates around the maximum distance.  In either case,
//     * the array begins with the minimum distance.
//     * 
//     * @param birth True for distance from twin starting at birth, false for distance
//     * from spouse starting at death.
//     * @return an array of distances.
//     */
//    public double[] distances(boolean birth){
//        double[] distances = defectPairs(birth)
//                .mapToDouble(SnapDefectPair::dist)
//                .toArray();
//        if(!birth) Collections.reverse(Arrays.asList(distances));
//        if(spouseIsTwin()){
//            
//        }
//    }
}
