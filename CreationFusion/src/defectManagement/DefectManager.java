package defectManagement;

import Charts.NamedData;
import GeometricTools.Rectangle;
import GeometricTools.OpenSpaceTimeBall;
import GeometricTools.Vec;
import ReadWrite.FormatedFileWriter;
import snapDefects.SpaceTemp;
import SnapManagement.*;
import snapDefects.SnapDefect;
import ReadWrite.ReadManager;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;

/**
 * Represents a manager for tracking defects.
 */
public class DefectManager {

    private Frame[] frames;
    private final PosDefectSet posDefects;
    private final NegDefectSet negDefects;
    private long numEligibleForSpouse, numEligableForTwin, numEligableForSpouseAndTwin;
    private List<DefectManager> baseExperiments;

    public final static boolean POS = true, NEG = false, BIRTH = true, DEATH = false;

    /**
     * merges another defect manager into this one. The defect manager being
     * merged in is not left in tack.
     *
     * @param other The other defect manager.
     */
    public void mergeIn(DefectManager other) {
        if (baseExperiments == null) {
            baseExperiments = new ArrayList<>();
            baseExperiments.add(new DefectManager(this));
        }
        if (other.baseExperiments != null) {
            baseExperiments.addAll(other.baseExperiments);
            other.baseExperiments = null;
        }

        baseExperiments.add(other);

        posDefects.mergeIn(other.posDefects);
        negDefects.mergeIn(other.negDefects);

        mergeFramesIn(other.frames);

        numEligableForSpouseAndTwin += other.numEligableForSpouseAndTwin;
        numEligableForTwin += other.numEligableForTwin;
        numEligibleForSpouse += other.numEligibleForSpouse;
    }

    /**
     * merges the frames from another defect manager into this one.
     *
     * @param other The defect manager to be merged into this one.
     */
    private void mergeFramesIn(Frame[] other) {
        Frame[] merged = new Frame[Math.max(frames.length, other.length)];
        Arrays.setAll(merged, i -> {
            if (i > frames.length) return other[i];
            if (i > other.length) return frames[i];
            return Frame.merge(other[i], frames[i]);
        });
        frames = merged;
    }

    /**
     * Experiments that were merged together to make this one.
     *
     * @return Underlying experiments.
     */
    public List<DefectManager> getBaseExperiments() {
        return Collections.unmodifiableList(baseExperiments);
    }

    /**
     * A constructor that creates an empty defect manager.
     */
    private DefectManager() {
        this.posDefects = new PosDefectSet(0);
        this.negDefects = new NegDefectSet(0);
        numEligibleForSpouse = numEligableForTwin = numEligableForSpouseAndTwin = 0;
    }

    /**
     * For copy construction.
     */
    private DefectManager(DefectManager toCopy) {
        this.frames = toCopy.frames;
        this.posDefects = toCopy.posDefects;
        this.negDefects = toCopy.negDefects;
        this.numEligibleForSpouse = toCopy.numEligibleForSpouse;
        this.numEligableForTwin = toCopy.numEligableForTwin;
        this.numEligableForSpouseAndTwin = toCopy.numEligableForSpouseAndTwin;

    }

    /**
     * Constructs a DefectManager with the specified file name.
     *
     * @param readManager The format of the file.
     * @param window A window, outside of which no defects are tracked.
     * @param ball defects must have their creation or annihilation events
     * closer than this threshold to be considered a pair.
     * @param timeToEdge The time an event must be from the beginning or end of
     * time.
     */
    public DefectManager(ReadManager readManager, Rectangle window, OpenSpaceTimeBall ball, int timeToEdge) {

        setFrames(readManager);

        posDefects = new PosDefectSet(maxID(POS) + 1);
        negDefects = new NegDefectSet(maxID(NEG) + 1);

        loadDefects();

        Stream.of(BIRTH, DEATH).parallel().forEach(event -> {
            all().forEach(def -> def.setEligable(event, !nearEdge(def, window, timeToEdge, event)));
            pairDefects(window, ball, timeToEdge, event);
        });

        setFuseUp(Integer.MAX_VALUE);

    }

    /**
     * Gets the maximum ID of the given charge.
     *
     * @param charge The charge of the mdesired maximum ID.
     * @return The maximum ID of the given charge.
     */
    private int maxID(boolean charge) {
        return snaps(charge).mapToInt(snap -> snap.getID()).max().getAsInt();
    }

    public Stream<? extends SnapDefect> snaps(boolean charge) {
        return charge ? posSnaps() : negSnaps();
    }

    /**
     * Gets all the frames from the file.
     *
     * @return All the frames from the file.
     */
    private void setFrames(ReadManager rm) {

        Frame.FileData frameData = new Frame.FileData(rm);

        frames = new Frame[frameData.numFrames + 1];

        Map<Integer, PosSnapDefect>[] posMaps = new Map[frameData.numFrames + 1];
        Map<Integer, NegSnapDefect>[] negMaps = new Map[frameData.numFrames + 1];

        Arrays.setAll(posMaps, i -> new ConcurrentHashMap<Integer, PosSnapDefect>(frameData.maxFrameSize));
        Arrays.setAll(negMaps, i -> new ConcurrentHashMap<Integer, NegSnapDefect>(frameData.maxFrameSize));

        rm.snapDefects().parallel().filter(snap -> snap.isTracked()).forEach(snap -> {
            if (snap.getCharge())
                posMaps[snap.getTime()].put(snap.getID(), (PosSnapDefect) snap);
            else
                negMaps[snap.getTime()].put(snap.getID(), (NegSnapDefect) snap);
        });

        Arrays.setAll(frames, i -> new Frame(posMaps[i], negMaps[i], i));

    }

    /**
     * All the positive snap defects.
     *
     * @return All the positive snap defects.
     */
    public Stream<PosSnapDefect> posSnaps() {
        return Arrays.stream(frames).flatMap(frame -> frame.positives());
    }

    /**
     * All the negative snap defects.
     *
     * @return All the negative snap defects.
     */
    public Stream<NegSnapDefect> negSnaps() {
        return Arrays.stream(frames).flatMap(frame -> frame.negatives());
    }

    /**
     * The positive defects that are eligible for creation or annihilation
     * pairs.
     *
     * @param birth True for creation, false for annihilation.
     * @return A stream of eligible defects.
     */
    private Stream<PosDefect> eligable(Stream<PosDefect> positives, Rectangle window, int timeToEdge, boolean birth) {
        return positives
                .filter(pos
                        -> window.contains(pos.get(birth))
                && !nearEdge(pos, window, timeToEdge, birth));

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

        negSnaps().forEach(sd -> {
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
    public Stream<? extends SnapDefect> getSnapDefects(boolean charge) {
        return charge ? posSnaps() : negSnaps();
    }

    /**
     * Loads defects from the specified file. Sets their birthday and death
     * days. If birth days and death days have already been set, then this
     * method sets life courses.
     *
     */
    public final void loadDefects() {
        Stream.of(POS, NEG).parallel()
                .forEach(charge
                        -> getSnapDefects(charge).parallel().forEach(sd -> defects(charge).add(sd)
                )
                );
        all().parallel().forEach(def -> def.setVelocities());
    }

    /**
     * Checks if a defects creation/annihilation is near an edge.
     *
     * @param def A defect whose birth or death will be checked for proximity to
     * the spacial and temporal edges.
     * @param window The window containing all defects.
     * @param birth A birth can be near the end of time, but not the beginning,
     * and vice versa for deaths.
     * @param timeToEdge Must be further away from the end and beginning of
     * time.
     * @return True if the snap Defect is near the edge, false otherwise.
     */
    public boolean nearEdge(Defect def, Rectangle window, int timeToEdge, boolean birth) {
        SpaceTemp spaceTemp = def.get(birth);
        return window.nearEdge(spaceTemp)
                || spaceTemp.getTime() < timeToEdge
                || spaceTemp.getTime() > frames.length - timeToEdge;
    }

    /**
     * Pairs defects based on proximity and time thresholds.
     *
     * @param window Pairs must be well inside this window.
     * @param ball A definition of closeness.
     * @param timeToEdge The amount of time that is considered near the edge.
     * @param birth Set true to pair births and false to pair annihilations.
     */
    public final void pairDefects(Rectangle window, OpenSpaceTimeBall ball, int timeToEdge, boolean birth) {
        clearPairing(birth);

        positives().filter(pos -> pos.isEligable(birth)).forEach(lonelyPos
                -> lonelyPos.setPair(
                        getPair(lonelyPos, window, ball, timeToEdge, birth),
                        birth
                )
        );
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
     * @param isBirth true for twin, false for spouse.
     * @return Positive defects that have a pair.
     */
    public Stream<PosDefect> pairedPos(boolean isBirth) {
        return positives().filter(def -> def.hasPair(isBirth));
    }

    /**
     * All the pairs for all the defects.
     *
     * @param isBirth true if creation pairs are desired, false for annihilation
     * pairs.
     * @return All the pairs for all the defects.
     */
    public Stream<PairedSnDef> pairs(boolean isBirth) {
        return positives()
                .filter(posDef -> posDef.hasPair(isBirth))
                .flatMap(posDef -> posDef.defectPairs(isBirth))
                .filter(pair -> pair.workingPair());
    }

    /**
     * All the negative defects.
     *
     * @return All the negative defects.
     */
    public Stream<NegDefect> negatives() {
        return negDefects.stream();
    }

    /**
     * If this is a defects birthday, that defect is returned.  Otherwise, null
     * is returned.
     * @param sd The potential birthday.
     * @return The defect for whom sd is a birthday, or null.
     */
    private NegDefect eventDayOf(NegSnapDefect sd, boolean isBirth){
        NegDefect def = negDefects.get(sd);
        return def.get(isBirth).getTime() == sd.getTime()? def: null;
    }
    
    /**
     * Finds the pare for the proffered defect.
     *
     * @param lonely The positive defect for whom a pair is desired.
     * @param window Only interested in pairs inside window.
     * @param ball A definition of closeness.
     * @param timeToEdge Must be further away from the edge.
     * @param isBirth True if a twin is sought, false for a spouse.
     * @return The nearest pair if one exists within the given proximity.
     */
    public NegDefect getPair(PosDefect lonely, Rectangle window, OpenSpaceTimeBall ball, int timeToEdge, final boolean isBirth) {
        int centTime = lonely.get(isBirth).getTime();
        NegDefect closest = IntStream.range(
                Math.max(centTime - ball.rTime, 0), 
                Math.min(centTime + ball.rTime + 1, frames.length)
        ).boxed().flatMap(i -> frames[i].negatives())
                .map(snap -> eventDayOf(snap, isBirth))
                .filter(def -> def != null && !def.hasPair(isBirth))
                .parallel()
                .filter(eligable -> ball.near(eligable.get(isBirth), lonely.get(isBirth)))
                .min(Comparator.comparing(eligable -> eligable.get(isBirth).dist(lonely.get(isBirth))))
                .orElse(null);

        return closest;
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
     * The percent of positive defects whose fusion partner is their creation
     * partner.
     *
     * @return The percent of positive defects whose fusion partner is their
     * creation partner.
     */
    public double spouseIsTwin() {
        return (double) positives()
                .filter(Defect::spouseIsTwin)
                .count() / numEligableForSpouseAndTwin;
    }

    /**
     * The percent of positive defects that have a spouse and a twin.
     *
     * @return The percent of positive defects that have a spouse and a twin.
     */
    public double hasSpouseAndTwin() {
        return (double) positives()
                .filter(defect -> defect.hasSpouse() && defect.hasTwin())
                .count() / numEligableForSpouseAndTwin;
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
                .count() / numEligibleForSpouse;
    }

    /**
     * The percent of positive defects that have a twin.
     *
     * @return The percent of positive defects that have a spouse or a twin.
     */
    public double hasTwin() {
        return (double) positives()
                .filter(defect -> defect.hasTwin())
                .count() / numEligableForTwin;
    }

    /**
     * The total charge over the entire system from start to end.
     *
     * @return The total charge over the entire system from start to end.
     */
    public List<Vec> cumulativeSystemCharge() {

        int n = (int) frames.length + 1;

        List<Vec> cumeCharge = new ArrayList<>(n);

        cumeCharge.add(new Vec(0, frames[0].charge()));

        for (int i = 1; i < n; i++)
            cumeCharge.add(new Vec(i, cumeCharge.get(i - 1).getY() + frames[i].charge()));

        return cumeCharge;
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
    public final void setFuseUp(int timePeriod) {
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
     *
     * @param x The x value of each data point.
     * @param y The y value of each data point.
     * @param birth True for creation pairs, false for annihilation pairs.
     * @param name The name of the data set.
     * @param numPairs The number of SnapDefectPairs created from each positive
     * defect. This is roughly the number of setFrames desired.
     * @return Data points generated from all the pairs.
     */
    public NamedData getNamedData(Function<PairedSnDef, Double> x, Function<PairedSnDef, Double> y, boolean birth, String name, int numPairs) {
        return new NamedData(
                defectStream(POS)
                        .filter(def -> def.hasPair(birth))
                        .flatMap(def -> def.defectPairs(birth, numPairs, false))
                        .map(pair -> new Vec(x.apply(pair), y.apply(pair)))
                        .collect(Collectors.toList()), name);
    }

    /**
     * Gives the number of defects with the given charge at the given time.
     *
     * @param time The time.
     * @param charge The charge.
     * @return The number.
     */
    public long numOfCharge(int time, boolean charge) {
        return defectStream(charge).filter(def -> def.aliveAt(time)).count();
    }

    /**
     * The positive to negative defect ratio at the given time.
     *
     * @param time The timer for which the ratio is desired.
     * @return The positive to negative defect ratio at the given time.
     */
    public double posToNegRatio(int time) {
        return (double) positives().filter(pos -> pos.aliveAt(time)).count()
                / negatives().filter(neg -> neg.aliveAt(time)).count();
    }

    /**
     * A defect manager for all the files in the proffered folder.
     *
     * @param parentFolder The folder containing the input data files.
     * @param rect The containing window.
     * @param ball A definition of proximity.
     * @param timeToEdge Proximity to the end and beginning of time.
     */
    public DefectManager(String parentFolder, Rectangle rect, OpenSpaceTimeBall ball, int timeToEdge) {
        this();
        String[] files = Arrays.stream(new File(parentFolder).list()).map(str -> parentFolder + "//" + str).toArray(String[]::new);

        for (String fileName : files) {
            DefectManager dm = new DefectManager(
                    ReadManager.defaultFileFormat(fileName),
                    rect,
                    ball,
                    timeToEdge
            );

            if (dm.all().anyMatch(def -> !def.isTrackingLifeCourse()))
                throw new RuntimeException("Someone is not tracking their life course.");

            mergeIn(dm);
        }
    }

    /**
     * A stream of setFrames.
     *
     * @return A stream of setFrames.
     */
    public IntStream timeStream() {
        return IntStream.range(0, frames.length);
    }

    /**
     * A list of values as a function of time.
     *
     * @param f The function of time.
     * @return A list of values as a function of time.
     */
    public List<Vec> ofTime(IntToDoubleFunction f) {
        return timeStream().mapToObj(i -> new Vec(i, f.applyAsDouble(i))).collect(Collectors.toList());
    }

    /**
     * Returns a bunch of data points, with x values the distance between the
     * paired defects and y values f(pair)
     *
     * @param f
     * @param isBirth True for twins, false for spouses.
     * @param maxDist The maximum distance the two defects can have from one
     * another.
     * @return Returns a bunch of data points, with x values the distance
     * between the paired defects and y values f(pair)
     */
    public List<Vec> pairsAtDistance(Function<PairedSnDef, Double> f, boolean isBirth, double maxDist) {
        return pairedPos(isBirth)
                .flatMap(posDef -> posDef.defectPairs(isBirth))
                .filter(pair -> pair.dist() < maxDist)
                .map(pair -> new Vec(pair.dist(), f.apply(pair)))
                .collect(Collectors.toList());
    }

    /**
     * The charge of all the file lines added up.
     *
     * @return The charge of all the file lines added up.
     */
    public long totalCharge() {
        return posSnaps().count() - negSnaps().count();
    }

    private String name = "";

    /**
     * Gets the name of this defect manager.
     *
     * @return The name of this defect manager.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this defect manager.
     *
     * @param name The name of this defect manager.
     */
    public DefectManager setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * A stream of all the pairs of defects that nearly collide. Note, these may
     * or may not be fusion or creation pairs.
     *
     * @param near A definition of proximity
     * @return A stream of all the pairs of defects that nearly collide. Note,
     * these may or may not be fusion or creation pairs.
     */
    public Stream<PairSnDef> nearCollsionsSnPairs(double near) {

        return Arrays.stream(frames).flatMap(frame
                -> frame.positives().flatMap(pos
                        -> frame.negatives().filter(neg -> neg.loc.dist(pos.loc) < near)
                        .map(neg -> new PairSnDef(pos, neg)))
        );
    }

}
