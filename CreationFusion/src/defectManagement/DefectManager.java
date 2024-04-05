package defectManagement;

import Charts.NamedData;
import GeometricTools.Rectangle;
import GeometricTools.Vec;
import ReadWrite.FormatedFileWriter;
import snapDefects.SpaceTemp;
import SnapManagement.*;
import snapDefects.SnapDefect;
import ReadWrite.ReadManager;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;

/**
 * Represents a manager for tracking defects.
 */
public class DefectManager {

    private final List<PosSnapDefect> posSnaps;
    private final List<NegSnapDefect> negSnaps;
    private final DefectSet posDefects, negDefects;
    private int timeProx, timeEdge;
    private double distProx, distEdge;
    private final Rectangle window;

    public final static boolean POS = true, NEG = false, BIRTH = true, DEATH = false;

    /**
     * Constructs a DefectManager with the specified file name.
     *
     * @param fileFormat The format of the file.
     * @param window A window, outside of which no defects are tracked.
     * @param distThreshold Two defects must have their creation or annihilation
     * events closer than this threshold to be considered a pair.
     * @param timeThreshold Two defects must have their creation or annihilation
     * times closer than this threshold to be considered a pair.
     * @param timeEdge The time an event must be from the beginning or end of
     * time.
     * @param distEdge The distance an event must be from the edge of the frame.
     */
    public DefectManager(ReadManager fileFormat, Rectangle window, int timeThreshold, double distThreshold, int timeEdge, double distEdge) {

        setThresholds(timeThreshold, distThreshold, timeEdge, distEdge);

        this.window = window;
        int numLines = (int) fileFormat.lines().parallel().count();

        List<PosSnapDefect> posTempSnaps = new ArrayList<>(numLines);
        List<NegSnapDefect> negTempSnaps = new ArrayList<>(numLines);

        Map<Boolean, Integer> maxID = loadSnaps(fileFormat, posTempSnaps, negTempSnaps);

        posSnaps = Collections.unmodifiableList(posTempSnaps);
        negSnaps = Collections.unmodifiableList(negTempSnaps);

        this.posDefects = new PosDefectSet(maxID.get(POS) + 1);
        this.negDefects = new NegDefectSet(maxID.get(NEG) + 1);

        loadDefects();

        Stream.of(BIRTH, DEATH).forEach(event -> pairDefects(event));//TODO This can be parallel.

        loadLifeCourses();
    }

    /**
     * Loads the snap defects into the proffered lists.
     *
     * @param fileFormat The file to be read from.
     * @param posTempSnaps The list to have the positive snap defects read into.
     * @param negTempSnaps The list to have the negative snap defects read into.
     * @return The maximum ID in the positive and negative snap defects.
     */
    private Map<Boolean, Integer> loadSnaps(ReadManager fileFormat, List<PosSnapDefect> posTempSnaps, List<NegSnapDefect> negTempSnaps) {
        Map<Boolean, Integer> maxID = new HashMap<>(2);
        maxID.put(POS, 0);
        maxID.put(NEG, 0);

        fileFormat.snapDefects()
                .filter(snap -> (window != null && window.contains(snap.loc)) || window == null)
                .filter(snap -> snap.isTracked())
                .forEachOrdered(snap -> {
                    if (snap.getCharge()) {
                        posTempSnaps.add((PosSnapDefect) snap);
                        maxID.put(POS, Math.max(maxID.get(POS), snap.getID()));
                    } else {
                        negTempSnaps.add((NegSnapDefect) snap);
                        maxID.put(NEG, Math.max(maxID.get(NEG), snap.getID()));
                    }
                });
        return maxID;
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

        negSnaps.forEach(sd -> {
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
     * Gets a list of snap defects.
     *
     * @param charge The charge of the desired list.
     * @return The list of snap defects with the desired charge.
     */
    public List<? extends SnapDefect> getSnapDefects(boolean charge) {
        return charge ? posSnaps : negSnaps;
    }

    /**
     * Loads defects from the specified file. Sets their birthday and death
     * days. If birth days and death days have already been set, then this
     * method sets life courses.
     *
     */
    public final void loadDefects() {
        Stream.of(BIRTH, DEATH).parallel().forEach(charge
                -> getSnapDefects(charge).stream().parallel()
                        .forEach(sd -> defects(sd.getCharge()).add(sd))
        );
    }

    /**
     * Checks if a defects creation/annihilation is near an edge.
     *
     * @param def A defect whose birth or death will be checked for proximity to
     * the spacial and temporal edges.
     * @param birth A birth can be near the end of time, but not the beginning,
     * and vice versa for deaths.
     * @return True if the snap Defect is near the edge, false otherwise.
     */
    public boolean nearEdge(Defect def, boolean birth) {
        SpaceTemp sd = def.get(birth);
        return window.nearEdge(sd, distEdge)
                || sd.getTime() < timeEdge
                || sd.getTime() > getEndTime() - timeEdge;
    }

    /**
     * Pairs defects based on proximity and time thresholds.
     * @param birth Set true to pair births and false to pair annihilations.
     */
    public final void pairDefects(final boolean birth) {
        clearPairing(birth);

        Set<NegDefect> possiblePairs = new HashSet<>(negDefects);

        positives().forEach(lonely -> 
            lonely.setPair(
                    getPair(lonely, possiblePairs, birth), 
                    birth
            )
        );
    }

    private int endTime = -1;

    /**
     * The total amount of time in the file.
     *
     * @return The total amount of time in the file.
     */
    public long getEndTime() {
        if (endTime == -1)
            if (posDefects.isEmpty())
                return endTime = negSnaps.stream().mapToInt(snap -> snap.loc.getTime()).max().getAsInt();
            else
                return endTime = all()
                        .mapToInt(def -> def.getDeath().getTime()).max().getAsInt();

        return endTime;
    }

    /**
     * Calculates the percentage of defects that are tracked.
     *
     * @param rm The file being read.
     * @return The percentage of tracked defects.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public double percentTracked(ReadManager rm) throws IOException {
        double count = 0;
        ReadManager.Reader reader = rm.getReader();
        while (reader.readLine() != null) count++;
        reader.close();
        return size() / count;
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
    public NegDefect getPair(PosDefect lonely, Set<NegDefect> eligibles, final boolean birth) {
        NegDefect closest = eligibles.stream()
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
     *
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
    private void clearPairing(boolean birth) {
        pairedPos(birth).parallel().forEach(defect -> defect.setPair(null, birth));
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
        return new FrameIterator.ChargedFrameIterator(getSnapDefects(charge));
    }

    /**
     * An iterator that reads directly from the file, one frame at a time.
     *
     *
     * @return An iterator that reads directly from the file, one frame at a
     * time.
     */
    public FrameIterator frameIterator() {
        return new FrameIterator(posSnaps, negSnaps);
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
    public final void loadLifeCourses() {

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

    /**
     * Writes all the pairs of this defect manager to the given file.
     *
     *
     * @param ffw A formatted file writer to write.
     */
    public void writePairesToFile(FormatedFileWriter ffw) {
        pairs(DefectManager.BIRTH).forEach(sd -> ffw.writeLine(sd));
        pairs(DefectManager.DEATH).forEach(sd -> ffw.writeLine(sd));
    }
    
    /**
     * Uses all the pairs to provide a set of data points.
     * @param x The x value of each data point.
     * @param y The y value of each data point.
     * @param birth True for creation pairs, false for annihilation pairs.
     * @param name The name of the data set.
     * @param numPairs The number of SnapDefectPairs created from each positive defect.
     * This is roughly the number of frames desired.
     * @return Data points generated from all the pairs.
     */
    public NamedData getNamedData(Function<PairSnDef, Double> x, Function<PairSnDef, Double> y, boolean birth, String name, int numPairs){
        return new NamedData( 
                defectStream(POS)
                        .filter(def -> def.hasPair(birth))
                        .flatMap(def -> def.defectPairs(birth, numPairs, false))
                        .map(pair -> new Vec(x.apply(pair), y.apply(pair)))
                        .collect(Collectors.toList()), name);
    }
}
