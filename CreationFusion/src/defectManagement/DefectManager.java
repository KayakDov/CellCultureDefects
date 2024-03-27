package defectManagement;

import snapDefects.SpaceTemp;
import SnapManagement.Defect;
import SnapManagement.Frame;
import SnapManagement.PairSnDef;
import snapDefects.SnapDefect;
import ReadWrite.ReadManager;
import SnapManagement.PosDefect;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a manager for tracking defects.
 */
public class DefectManager {

    private final List<SnapDefect> snaps;
    private final DefectSet posDefects, negDefects;
    private ReadManager readManager;
    private int timeProx, timeEdge;
    private double distProx, distEdge;
    private int endTime = -1;

    public final static boolean POS = true, NEG = false, BIRTH = true, DEATH = false;

    /**
     * Constructs a DefectManager with the specified file name.
     *
     * @param fileFormat The format of the file.
     * @param distThreshold Two defects must have their creation or annihilation
     * events closer than this threshold to be considered a pair.
     * @param timeThreshold Two defects must have their creation or annihilation
     * times closer than this threshold to be considered a pair.
     * @param timeEdge The time an event must be from the beginning or end of
     * time.
     * @param distEdge The distance an event must be from the edge of the frame.
     */
    public DefectManager(ReadManager fileFormat, int timeThreshold, double distThreshold, int timeEdge, double distEdge) {

        this.readManager = fileFormat;

        setThresholds(timeThreshold, distThreshold, timeEdge, distEdge);

        snaps = new ArrayList<>((int) readManager.lines().parallel().count());
        readManager.snapDefects().filter(snap -> snap.isTracked()).forEachOrdered(snap -> snaps.add(snap));

        var maxID = maxID();

        this.posDefects = new PosDefectSet(maxID.get(POS) + 1);
        this.negDefects = new NegDefectSet(maxID.get(NEG) + 1);

        loadDefects();

        pairDefects();
    }


    /**
     * An internal integrity check method. Checks if each defect is stored
     * correctly according to its index. Note, if a window is used then defects
     * should not be correct.
     *
     * @param charge true to check positive indices, false to check negative.
     * @return True if each index is stored correctly according to its index.
     * False otherwise.
     */
    public boolean indeciesAreCorrect(boolean charge) {

        return IntStream.range(0, defects(charge).size())
                .allMatch(i -> defects(charge).get(i) != null
                && i == defects(charge).get(i).getID());
    }

    /**
     * An internal integrity check method. Checks if each defect is stored
     * correctly according to its index. Note, if a window is used then defects
     * should not be correct.
     *
     * @return True if each index is stored correctly according to its index.
     * False otherwise.
     */
    public boolean indeciesAreCorrect() {
        return indeciesAreCorrect(POS) && indeciesAreCorrect(NEG);
    }

    /**
     * Returns the list of defects of the given charge. Note, some indices may
     * be null.
     *
     * @param charge The charge of the desired defects.
     * @return The list of defects of the desired charge.
     */
    public DefectSet defects(boolean charge) {
        return charge ? posDefects : negDefects;
    }

    /**
     * Returns the stream of defects of the given charge. This is safer than
     * calling the list since none will be null.
     *
     * @param charge The charge of the desired defects.
     * @return A stream of defects of the desired charge.
     */
    public Stream<? extends Defect> defectStream(boolean charge) {
        return charge ? positives() : negatives();
    }

    /**
     * The total number of defects.
     *
     * @return The total number of defects.
     */
    public int size() {
        return posDefects.size() + negDefects.size();
    }

    /**
     * Have any defects been loaded?
     *
     * @return True if a defect has been loaded, false otherwise.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Checks that no indices in the file are skipped, and prints new indices as
     * they appear.
     *
     * @param charge The charge for which order of appearance is presented.
     * @return True if no indices in the file are skipped.
     */
    public List<Integer> orderOfApearence(boolean charge) {

        HashSet<Integer> defects = new HashSet<>(size());
        ArrayList<Integer> orderOfAppearence = new ArrayList<>(defects(charge).size());

        snaps.forEach(sd -> {
            if (sd.isTracked() && !defects.contains(sd.getID())) {
                defects.add(sd.getID());
                orderOfAppearence.add(sd.getID());
            }
        });

        return orderOfAppearence;

    }

    /**
     * The number of defects who's ID is not 1 plus the ID of the preceding
     * first appearance.
     *
     * @return The number of defects who's ID is not 1 plus the ID of the
     * preceding first appearance.
     */
    public long outOfOrder() {
        List<Integer> ooaPos = orderOfApearence(true);

        long count = IntStream.range(1, ooaPos.size())
                .filter(i -> !Objects.equals(ooaPos.get(i), ooaPos.get(i - 1)))
                .count();

        List<Integer> ooaNeg = orderOfApearence(false);

        count += IntStream.range(1, ooaNeg.size())
                .filter(i -> !Objects.equals(ooaNeg.get(i), ooaNeg.get(i - 1))).count();

        return count;

    }

    /**
     * Loads defects from the specified file. Sets their birthday and death
     * days. If birth days and death days have already been set, then this
     * method sets life courses.
     *
     */
    public final void loadDefects() {

        snaps.stream().parallel()
                .filter(SnapDefect::isTracked)
                .forEach(sd -> defects(sd.getCharge()).add(sd));
    }

    /**
     * Finds the positive and negative defects with the highest IDss in the
     * file.
     *
     * @return The highest posative and negative IDs in teh file. The first
     * index it the positive and the second index is the negative.
     */
    public final Map<Boolean, Integer> maxID() {

        Map<Boolean, Integer> maxID = new HashMap<>(2);
        maxID.put(true, 0); maxID.put(false, 0);

        readManager.snapDefects().filter(sn -> sn.isTracked()).forEach(sn -> {
            if (sn.getID() > maxID.get(sn.getCharge()))
                maxID.put(sn.getCharge(), sn.getID());
        });

        return maxID;

    }

    /**
     * Checks if a defects creation/annihilation is near an edge.
     * @param def A defect whose birth or death will be checked for proximity to
     * the spacial and temporal edges.
     * @param birth A birth can be near the end of time, but not the beginning,
     * and vice versa for deaths.
     * @return True if the snap Defect is near the edge, false otherwise.
     */
    public boolean nearEdge(Defect def, boolean birth) {
        SpaceTemp sd = def.get(birth);
        return readManager.getWindow().nearEdge(sd, distEdge)
                || sd.getTime() < timeEdge
                || sd.getTime() > getEndTime() - timeEdge;
    }

    /**
     * Pairs defects based on proximity and time thresholds.
     */
    public final void pairDefects() {
        clearPairing();

        Set<Defect> possibleBirths = new HashSet<>(negDefects);
        Set<Defect> possibleDeaths = new HashSet<>(negDefects);

        positives().forEach(lonely -> {
            lonely.setPair(BIRTH, possibleBirths, this);
            lonely.setPair(DEATH, possibleDeaths, this);
        });
    }

    /**
     * The total amount of time in the file.
     *
     * @return The total amount of time in the file.
     */
    public long getEndTime() {
        if (endTime == -1)
            if (posDefects.isEmpty())
                return endTime = snaps.stream().mapToInt(snap-> snap.loc.getTime()).max().getAsInt();
            else
                return endTime = all()
                        .mapToInt(def -> def.getDeath().getTime()).max().getAsInt();

        return endTime;
    }

    /**
     * Calculates the percentage of defects that are tracked.
     *
     * @return The percentage of tracked defects.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public double percentTracked() throws IOException {
        return size() / readManager.lines().count();
    }

    /**
     * Gets the number of positive defects.
     *
     * @return The number of positive defects.
     */
    public long numPositiveDefect() {
        return posDefects.size();
    }

    /**
     * Gets the number of negative defects.
     *
     * @return The number of negative defects.
     */
    public int numNegativeDefects() {
        return negDefects.size();
    }

    /**
     * All the defects.
     *
     * @return All the defects.
     */
    public Stream<Defect> all() {
        return Stream.concat(positives(), negatives());
    }

    /**
     * All the positive defects.
     *
     * @return All the positive defects.
     */
    public Stream<PosDefect> positives() {
        return posDefects.stream();
    }

    /**
     * Positive defects that have a pair.
     *
     * @param birth true for twin, false for spouse.
     * @return Positive defects that have a pair.
     */
    public Stream<PosDefect> pairedPos(boolean birth) {
        return positives().filter(def -> def.hasPair(birth));
    }

    /**
     * All the pairs for all the defects.
     *
     * @param birth true if creation pairs are desired, false for annihilation
     * pairs.
     * @return All the pairs for all the defects.
     */
    public Stream<PairSnDef> pairs(boolean birth) {
        return positives()
                .filter(posDef -> posDef.hasPair(birth))
                .flatMap(posDef -> posDef.defectPairs(birth));
    }

    /**
     * All the negative defects.
     *
     * @return All the negative defects.
     */
    public Stream<Defect> negatives() {
        return negDefects.stream();
    }

    /**
     * Finds the pare for the proffered defect.
     *
     * @param lonely The positive defect for whom a pair is desired.
     * @param eligibles A set of unmatched negative defects.
     * @param birth True if a twin is sought, false for a spouse.
     * @return The nearest pair if one exists within the given proximity.
     */
    public Defect getPair(Defect lonely, Set<Defect> eligibles, boolean birth) {
        Defect closest = eligibles.stream()
                .parallel()
                .filter(elig -> !nearEdge(elig, birth))
                .filter(eligable -> eligable.get(birth).near(lonely.get(birth), distProx, timeProx))
                .min(Comparator.comparing(eligable -> eligable.get(birth).dist(lonely.get(birth))))
                .orElse(null);

        if (closest != null) eligibles.remove(closest);

        return closest;
    }

    /**
     * Sets proximity thresholds.
     *
     * @param timeProx Another point is near this one if it is closer in time.
     * @param distProx Another point is near this one if it's closer in space.
     * @param timeEdge How far a creation or annihilation event must be from the
     * beginning or end of time.
     * @param distEdge How far an event must be from the edge of the frame.
     */
    public final void setThresholds(int timeProx, double distProx, int timeEdge, double distEdge) {
        this.timeProx = timeProx;
        this.distProx = distProx;
        this.distEdge = distEdge;
        this.timeEdge = timeEdge;
    }

    /**
     * clears all stored defects.
     */
    public void clearDefects() {
        posDefects.clear();
        negDefects.clear();
    }

    /**
     * Removes all spouses and twins.
     */
    private void clearPairing() {
        positives().parallel().forEach(defect -> defect.clearPairs());
    }

    /**
     * An iterator that reads directly from the file, one frame at a time for
     * the given charge.
     *
     * @param charge The charge of the desired frame.
     *
     * @return An iterator that reads positively or negatively charged defects
     * directly from the file, one frame at a time.
     */
    public FrameIterator.ChargedFrameIterator frameIterator(boolean charge) {
        return new FrameIterator.ChargedFrameIterator(readManager, charge);
    }

    /**
     * An iterator that reads directly from the file, one frame at a time.
     *
     *
     * @return An iterator that reads directly from the file, one frame at a
     * time.
     */
    public FrameIterator frameIterator() {
        return new FrameIterator(readManager);
    }

    /**
     * The percent of positive defects whose fusion partner is their creation
     * partner.
     *
     * @return The percent of positive defects whose fusion partner is their
     * creation partner.
     */
    public double spoouseIsTwin() {
        return (double) positives().filter(Defect::spouseIsTwin)
                .count() / positives().filter(pos -> !nearEdge(pos, BIRTH) && !nearEdge(pos, DEATH)).count();
    }

    /**
     * The percent of positive defects that have a spouse and a twin.
     *
     * @return The percent of positive defects that have a spouse and a twin.
     */
    public double hasSpouseAndTwin() {
        return (double) positives()
                .filter(defect -> defect.hasSpouse() && defect.hasTwin())
                .count() / positives().filter(pos -> !nearEdge(pos, BIRTH) && !nearEdge(pos, DEATH)).count();
    }

    /**
     * The percent of positive defects that have a spouse or a twin.
     *
     * @return The percent of positive defects that have a spouse or a twin.
     */
    public double hasSpouseOrTwin() {
        return hasSpouse() + hasTwin() - hasSpouseAndTwin();
    }

    /**
     * The percent of positive defects that have a spouse.
     *
     * @return The percent of positive defects that have a spouse or a twin.
     */
    public double hasSpouse() {
        return (double) positives()
                .filter(defect -> defect.hasSpouse())
                .count() / positives().filter(pos -> !nearEdge(pos, DEATH)).count();
    }

    /**
     * The percent of positive defects that have a twin.
     *
     * @return The percent of positive defects that have a spouse or a twin.
     */
    public double hasTwin() {
        return (double) positives()
                .filter(defect -> defect.hasTwin())
                .count() / positives().filter(pos -> !nearEdge(pos, BIRTH)).count();
    }

    /**
     * The total charge over the entire system from start to end.
     *
     * @return The total charge over the entire system from start to end.
     */
    public double cumulativeSystemCharge() {
        if (posDefects.isEmpty()) loadDefects();
        return all()
                .mapToDouble(defect -> defect.age() * (defect.getCharge() ? .5 : -.5))
                .sum();
    }

    /**
     * A stream of all the frames, created from an iterator going over the file.
     *
     * @return A stream of all the frames.
     */
    public Stream<Frame> frameStream() {
        return StreamSupport.stream(
                Spliterators.spliterator(
                        frameIterator(), endTime, Spliterator.IMMUTABLE
                ),
                false
        );
    }

    /**
     * The defect for which the proffered SnapDefect is a moment of.
     *
     * @param sd For which the entire defect is desired.
     * @return The defect for which the proffered SnapDefect is a moment of.
     */
    public Defect getDefect(SnapDefect sd) {
        return defects(sd.getCharge()).get(sd);
    }

    /**
     * Loads the life courses of each defect.
     */
    public void loadLifeCourses() {

        all().parallel().forEach(def -> def.prepForTracking());

        loadDefects();

        all().parallel().forEach(def -> def.setVelocities());

        setFuseUp(Integer.MAX_VALUE);

    }

    /**
     * The mean square displacement of all the defects at the given time from
     * their births.
     *
     * @param timeFromBirth The time from birth the displacement is taken over.
     * @return The mean square displacement at the given time.
     */
    public double meanSquareDisplacement(int timeFromBirth) {
        return all().parallel()
                .filter(def -> timeFromBirth <= def.age())
                .mapToDouble(def -> def.displacement(timeFromBirth))
                .map(d -> d * d).sum() / size();
    }

    /**
     * The average standard deviation of the anglePRel over all the positive
     * defects.
     *
     * @param birth True for twin pairs, false for spouse pairs.
     * @param minAge Only consider defects that live longer than this age.
     * @param limitFromEvent Only consider angles closer in time to the desired
     * event.
     * @return The average standard deviation of the anglePRel over all the
     * positive defects.
     */
    public double avgStdDevAngPRel(boolean birth, int minAge, int limitFromEvent) {
        return positives().filter(pos -> pos.hasPair(birth))
                .filter(pos -> pos.age() > minAge)
                .mapToDouble(pos -> pos.stdDevAnglePRel(birth, limitFromEvent))
                .average()
                .getAsDouble();
    }

    /**
     * Will set all positive defects to be fuse up or down.
     *
     * @param timePeriod Over how long from the event is the average taken.
     */
    public void setFuseUp(int timePeriod) {
        positives().parallel().forEach(def -> def.setFuseUp(timePeriod));
    }
}
