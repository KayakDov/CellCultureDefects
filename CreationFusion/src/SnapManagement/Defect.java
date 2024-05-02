package SnapManagement;

import GeometricTools.Angle;
import dataTools.UnimodalArrayMax;
import defectManagement.DefectManager;
import snapDefects.SpaceTemp;
import defectManagement.hasChargeID;
import snapDefects.SnapDefect;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Describes a defect tracked over time.
 */
public abstract class Defect implements hasChargeID {

    public SpaceTemp birth, death;
    public int ID;
    protected SnapDefect[] lifeCourse;
    private Defect twin, spouse;
    private boolean eligibleForSpouse, eligibleForTwin;

    /**
     * Constructs a new Defect instance from a SnapDefect.
     *
     * @param sd The SnapDefect that gives birth to this defect.
     *
     */
    protected Defect(SnapDefect sd) {
        death = sd.loc;
        birth = sd.loc;
        this.ID = sd.getID();
    }

    /**
     * Checks if this defect has a twin defect.
     *
     * @return true if this defect has a twin, false otherwise.
     */
    public boolean hasTwin() {
        return getTwin() != null;
    }

    /**
     * Checks if this defect has a spouse defect.
     *
     * @return true if this defect has a spouse, false otherwise.
     */
    public boolean hasSpouse() {
        return getSpouse() != null;
    }

    /**
     * Sets another defect as having been created together with this defect.
     *
     * @param pair The defect that was created with this defect.
     * @param birth true to set the twin, false to set the spouse.
     */
    protected void setPair(Defect pair, boolean birth) {
        if (pair != null) pair.setPairHard(this, birth);
        setPairHard(pair, birth);
    }

    /**
     * Sets the twin without reciprocity.
     *
     * @param pair The new twin.
     * @param birth True to set the twin, false to set the spouse.
     */
    protected void setPairHard(Defect pair, boolean birth) {
        if (birth) twin = pair;
        else spouse = pair;
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
    public boolean hasPair(boolean birth) {
        return birth ? hasTwin() : hasSpouse();
    }

    /**
     * The partner of this Defect if it exists, otherwise null.
     *
     * @param birth True for a twin, false for a spouse.
     * @return The partner of this Defect.
     */
    public Defect getPair(boolean birth) {
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
        if (hasTwin()) twin.twin = null;
        if (hasSpouse()) spouse.spouse = null;
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
        lifeCourse[sd.loc.getTime() - birth.getTime()] = sd;
    }

    /**
     * The location of this defect at the given time. Note, you must prep and
     * load lifeCourse before calling this method.
     *
     * @param time The time (frame number) of the desired location.
     * @return The desired SnapDefect.
     */
    protected SnapDefect snapFromFrame(int time) {
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
        return "ID: " + getID() + " lived from " + getBirth() + " to " + getDeath() + "\n";
    }

    /**
     * The ID of this node.
     *
     * @return The ID of this node.
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * The charge of this node.
     *
     * @return The charge of this node.
     */
    @Override
    public abstract boolean getCharge();

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
    public abstract void prepForTracking();

    /**
     * Did this defect exists during the proffered time?
     *
     * @param time The time in question.
     * @return True if the defect existed at the given time, false otherwise.
     */
    public boolean aliveAt(int time) {
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
     * @return The snap defect at the given time from birth/death. If the time
     * is greater than the age, then null is returned.
     */
    protected SnapDefect snapFromEvent(int time, boolean birth) {
        if (time > age()) return null;
        return lifeCourse[birth ? time : lifeCourse.length - 1 - time];

    }

    /**
     * A pair of snap defects.
     *
     * @param birth True if twins are desired, false for fusion partners.
     * @param timeFromEvent The amount of time form birth or death of this
     * defect.
     * @return
     */
    public abstract PairedSnDef snapPairFromEvent(int timeFromEvent, boolean birth);

    /**
     * Gets the partners snap defect at the desired time.
     *
     * @param time The time from the event that the SnapDefect is desired.
     * @param birth True for birth/twin and false for death/spouse.
     * @return The snap defect of the partner at the requested time. If no
     * defect is available, then a null value is returned.
     */
    public abstract PairedSnDef snapPairFromFrame(int time, boolean birth);

    /**
     * A stream of this defects snapsDefects together with their birth or death
     * pairs.
     *
     * @param birth True for twin, false for spouses.
     * @return A stream of this defects snapsDefects together with their birth
     * or death pairs.
     */
    public Stream<PairedSnDef> defectPairs(boolean birth) {
        return defectPairs(birth, Integer.MAX_VALUE, false);
    }

    /**
     * A class to provide ranges for iteration over the lifeCourse.
     */
    private class LifeCourseRange {

        public int start, end;

        /**
         * Constructs a range to iterate over for lifeCourse.
         * @param birth Is this a set of birth pairs or annihilation pairs.
         * @param maxNumPairs The maximum number of pairs.
         * @param peakDistStop True if the range should end with the furthest distance apart.
         */
        public LifeCourseRange(boolean birth, int maxNumPairs, boolean peakDistStop) {
            if (birth) {
                start = 0;
                end = Math.min(maxNumPairs, lifeCourse.length);
            } else {
                start = Math.max(lifeCourse.length - maxNumPairs, 0);
                 end = lifeCourse.length;
            }
            if(peakDistStop && spouseIsTwin()){
                int mid = maxDistIndex(birth);
                if(birth) end = Math.min(end, mid);
                else start = Math.max(start, mid);
            }
        }

    }

    /**
     * A stream of this defects snapsDefects together with their birth or death
     * pairs.
     *
     * @param birth True for twin, false for spouses.
     * @param maxNumPairs There will be at most this many pairs from the event
     * in the stream.
     * @param peakDistStop should the lifeCourse stop at the peak distance the 
     * pairs reach from one another.
     * @return A stream of this defects snapsDefects together with their birth
     * or death pairs.
     */
    public Stream<PairedSnDef> defectPairs(boolean birth, int maxNumPairs, boolean peakDistStop) {
        
        LifeCourseRange lcr = new LifeCourseRange(birth, maxNumPairs, peakDistStop);
        
        return hasPair(birth)
                ? IntStream.range(lcr.start, lcr.end)
                        .mapToObj(i -> snapPairFromFrame(getBirth().getTime() + i, birth))
                        .filter(sdp -> sdp.workingPair())
                : Stream.of();

    }

    /**
     * Is this defects entire life course meant to be tracked.
     *
     * @return True if it is, false otherwise.
     */
    public boolean followingLifeCourse() {
        return lifeCourse != null;
    }

    /**
     * Sets the velocity of all the snap defects.
     */
    public void setVelocities() {
        if (lifeCourse.length <= 1) return;
        IntStream.range(1, lifeCourse.length - 1)
                .filter(i -> lifeCourse[i] != null)
                .forEach(i -> lifeCourse[i].setVelocity(lifeCourse[i - 1], lifeCourse[i + 1]));
        lifeCourse[0].setVelocity(null, lifeCourse[1]);
        lifeCourse[lifeCourse.length - 1].setVelocity(lifeCourse[lifeCourse.length - 2], null);
    }

    /**
     * The distance from the location this defect was created.
     *
     * @param timeFromBirth The time afterbirth the distance is to be taken.
     * @return The distance from the place of birth.
     */
    public double displacement(int timeFromBirth) {
        return birth.dist(snapFromEvent(timeFromBirth, DefectManager.BIRTH).loc);
    }

    /**
     * The life course of this defect.
     *
     * @return The life course of this defect.
     */
    public List<? extends SnapDefect> getLifeCourse() {
        return Arrays.asList(lifeCourse);
    }

    /**
     * Gives a stream of the AnglesPRel for pairs of this defect.
     *
     * @param birth True for twin pairs, false for spouse pairs.
     * @param limitTimeFromEvent The time limit from the event.
     * @return A stream of angles between this defects tail and its pair.
     */
    public Stream<Angle> AnglesPRel(boolean birth, int limitTimeFromEvent) {
        return defectPairs(birth, limitTimeFromEvent, true).map(pair -> pair.anglePRel()); 
    }

    /**
     * The standard deviation of this defects pairs' anglePRel
     *
     * @param birth Birth pairs or death pairs.
     * @param limitTimeFromEvent How long should the standard deviation be taken
     * over? If the event is a birth, elements past the limit will be ignored.
     * If the event is a death, elements before the limit will be ignored.
     * @return The standard deviation of this defects pairs' anglePRel
     */
    public double stdDevAnglePRel(boolean birth, int limitTimeFromEvent) {

        return hasPair(birth) ? Angle.stdDev(()
                -> AnglesPRel(birth, limitTimeFromEvent)
        ) : Double.NaN;
    }
    
    /**
     * The snap defect pair at the given time from birth.
     * @param time The amount of time from birth.
     * @return The snap defect pair at the given time from birth.
     */
    private PairedSnDef pairFromBirth(int time){
        return snapPairFromEvent(time, DefectManager.BIRTH);
    }
    
    /**
     * Finds the first working pair after the given index and returns its index.
     * @param i The first working pair at or after this index will be found.
     * @return The first working pair at index i, or after will be found.
     */
    private int workingPairAfter(int i){
        
        for(; i < lifeCourse.length; i++)
            if(pairFromBirth(i).workingPair()) return i;
        
        return lifeCourse.length;
    }
    
    /**
     * Finds the index for which this defect has the maximum distance from its pair.
     * @param birth True for twin, false for spouse.
     * @return The index in lifeCourse for which this defect has the maximum distance
     * form its pair.
     */
    public int maxDistIndex(boolean birth){
        int index = new UnimodalArrayMax(defectPairs(birth).mapToDouble(pair -> pair.dist()).toArray()).compute();
        
        while(index < lifeCourse.length - 1 && 
              pairFromBirth(index).dist() < pairFromBirth(workingPairAfter(index + 1)).dist()
                ) index = workingPairAfter(index + 1);
        return index;
    }
    
    /**
     * The time from the given event.
     * @param frameNumber The number of the frame.
     * @param birth True if the event is birth, false otherwise.
     * @return The number of frames from the frame number to the event.
     */
    protected int timeFromEvent(int frameNumber, boolean birth){
        return birth?frameNumber - getBirth().getTime(): getDeath().getTime() - frameNumber;
    }

    /**
     * If the ID needs to be changed.
     * @param Id 
     */
    public void setID(int Id) {
        this.ID = Id;
        Arrays.stream(lifeCourse).filter(snap -> snap != null).forEach(snap -> snap.setId(Id));
    }
    
    /**
     * Is this defect tracking its entire life course.
     * @return True if this defect is tracking its entire life course, false otherwise.
     */
    public boolean isTrackingLifeCourse(){
        return lifeCourse != null;
    }

    /**
     * Is this defect eligible to be paired.
     * @param birth True for twin, false for spouse.
     * @return Is this defect eligible fora pairing.
     */
    public boolean isEligable(boolean birth) {
        return birth? eligibleForTwin:eligibleForSpouse;
    }

    /**
     * Is this defect eligible for a pair.
     * @param birth True for twin,false for spouse.
     * @param eligibility True if yes, false if no.
     */
    public void setEligable(boolean birth, boolean eligibility) {
        if(birth) eligibleForTwin = eligibility;
        else eligibleForSpouse = eligibility;
    }
}
