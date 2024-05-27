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
import java.util.function.IntToDoubleFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;

/**
 * Represents a manager for tracking defects.
 */
public class DefectManager {

    private final PosDefectSet posDefects;
    private final NegDefectSet negDefects;
    private int numFrames;

    public final static boolean POS = true, NEG = false, BIRTH = true, DEATH = false;

    /**
     * merges another defect manager into this one. The defect manager being
     * merged in is not left in tack.
     *
     * @param other The other defect manager.
     */
    public final void mergeIn(DefectManager other) {

        posDefects.mergeIn(other.posDefects);
        negDefects.mergeIn(other.negDefects);
        numFrames = Math.max(numFrames, other.numFrames);
    }

    /**
     * A constructor that creates an empty defect manager.
     */
    private DefectManager() {
        this.posDefects = new PosDefectSet(0);
        this.negDefects = new NegDefectSet(0);
        numFrames = 0;

    }

    /**
     * For copy construction.
     */
    private DefectManager(DefectManager toCopy) {
        this.posDefects = toCopy.posDefects;
        this.negDefects = toCopy.negDefects;
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

        MaxFileValues mfv = new MaxFileValues(readManager);

        posDefects = new PosDefectSet(mfv.maxPosID + 1);
        negDefects = new NegDefectSet(mfv.maxNegID + 1);
        this.numFrames = mfv.frameCount + 1;

        readManager.snapDefects().parallel()
                .filter(snap -> snap.isTracked())
                .forEach(snap -> defects(snap.getCharge()).add(snap));

        all().parallel().forEach(def -> def.setVelocities());

        Stream.of(BIRTH, DEATH).parallel().forEach(event -> {
            all().forEach(def -> def.setEligable(event, !nearEdge(def, window, timeToEdge, event)));
            pairDefects(window, ball, timeToEdge, event);
        });

        setFuseUp(Integer.MAX_VALUE);

    }

    /**
     * Goes through the file and finds the max positive and negative values as
     * well as the frame count.
     */
    private static class MaxFileValues {

        public int maxPosID = 0, maxNegID = 0, frameCount = 0;

        /**
         * Goes through the file and finds the max positive and negative values
         * as well as the frame count.
         */
        public MaxFileValues(ReadManager rm) {

            try (ReadManager.Reader reader = rm.getReader()){
                SnapDefect sd;
                while ((sd = reader.readSnap()) != null) {
                    if (sd.isTracked()) {
                        if (sd.getCharge())
                            maxPosID = Math.max(sd.getID(), maxPosID);
                        else maxNegID = Math.max(sd.getID(), maxNegID);
                    }
                    frameCount = Math.max(frameCount, sd.getTime());
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * Positive or negative snap defects.
     *
     * @param charge The charge of the desired snap defects.
     * @return All of the snap defects of the given charge.
     */
    public Stream<? extends SnapDefect> snaps(boolean charge) {
        return charge ? posSnaps() : negSnaps();
    }

    /**
     * The snap defects.
     *
     * @return
     */
    public Stream<? extends SnapDefect> snaps() {
        return Stream.concat(posSnaps(), negSnaps());
    }

    /**
     * Gets all the frames from the file.
     *
     * @return All the frames from the file.
     */
    private Frame[] getFrames() {

        Frame[] frames = new Frame[numFrames + 1];

        Map<Integer, PosSnapDefect>[] posMaps = new Map[numFrames + 1];
        Map<Integer, NegSnapDefect>[] negMaps = new Map[numFrames + 1];

        Arrays.setAll(posMaps, i -> new ConcurrentHashMap<Integer, PosSnapDefect>());
        Arrays.setAll(negMaps, i -> new ConcurrentHashMap<Integer, NegSnapDefect>());

        snaps().parallel().filter(snap -> snap.isTracked()).forEach(snap -> {
            if (snap.getCharge())
                posMaps[snap.getTime()].put(snap.getID(), (PosSnapDefect) snap);
            else
                negMaps[snap.getTime()].put(snap.getID(), (NegSnapDefect) snap);
        });

        Arrays.setAll(frames, i -> new Frame(posMaps[i], negMaps[i], i));

        return frames;

    }

    /**
     * All the positive snap defects.
     *
     * @return All the positive snap defects.
     */
    public Stream<PosSnapDefect> posSnaps() {
        return positives().flatMap(def -> def.snapDefects());
    }

    /**
     * All the negative snap defects.
     *
     * @return All the negative snap defects.
     */
    public Stream<NegSnapDefect> negSnaps() {
        return negatives().flatMap(def -> def.snapDefects());
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
                || spaceTemp.getTime() > numFrames - timeToEdge;
    }

    /**
     * A list of birthdays/death days of negative defects.
     *
     * @return An array of birthdays/death days of negative defects.
     */
    private Set<NegDefect>[] eventDays(boolean isBirth) {
        Set<NegDefect>[] eventDays = new Set[(int) numFrames];

        Arrays.setAll(eventDays, i -> new HashSet<NegDefect>());

        negatives()
                .filter(negDef -> negDef.isEligable(isBirth))
                .forEach(negDef -> eventDays[negDef.get(isBirth).getTime()].add(negDef));

        return eventDays;
    }

    /**
     * Pairs defects based on proximity and time thresholds.
     *
     * @param window Pairs must be well inside this window.
     * @param ball A definition of closeness.
     * @param timeToEdge The amount of time that is considered near the edge.
     * @param isBirth Set true to pair births and false to pair annihilations.
     */
    public final void pairDefects(Rectangle window, OpenSpaceTimeBall ball, int timeToEdge, boolean isBirth) {
        clearPairing(isBirth);

        Set<NegDefect>[] eventDays = eventDays(isBirth);

        positives().filter(pos -> pos.isEligable(isBirth)).forEach(lonelyPos
                -> lonelyPos.setPair(
                        getPair(lonelyPos, eventDays, window, ball, timeToEdge, isBirth),
                        isBirth
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
        try (ReadManager.Reader reader = rm.getReader()) {
            while (reader.readLine() != null) count++;
        }
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
     * If this is a defects birthday, that defect is returned. Otherwise, null
     * is returned.
     *
     * @param sd The potential birthday.
     * @return The defect for whom sd is a birthday, or null.
     */
    private NegDefect eventDayOf(NegSnapDefect sd, boolean isBirth) {
        NegDefect def = negDefects.get(sd);
        return def.get(isBirth).getTime() == sd.getTime() ? def : null;
    }

    /**
     * Finds the pare for the proffered defect.
     *
     * @param lonely The positive defect for whom a pair is desired.
     * @param singles eligible singles
     * @param window Only interested in pairs inside window.
     * @param ball A definition of closeness.
     * @param timeToEdge Must be further away from the edge.
     * @param isBirth True if a twin is sought, false for a spouse.
     * @return The nearest pair if one exists within the given proximity.
     */
    public NegDefect getPair(PosDefect lonely, Set<NegDefect>[] singles, Rectangle window, OpenSpaceTimeBall ball, int timeToEdge, final boolean isBirth) {

        int centTime = lonely.get(isBirth).getTime();
        NegDefect closest = IntStream.range(
                Math.max(centTime - ball.rTime, 0),
                Math.min(centTime + ball.rTime + 1, (int) numFrames)
        ).boxed().flatMap(i -> singles[i].stream())
                .parallel()
                .filter(single -> ball.near(single.get(isBirth), lonely.get(isBirth)))
                .min(Comparator.comparing(single -> single.get(isBirth).dist(lonely.get(isBirth))))
                .orElse(null);

        if (closest != null)
            singles[closest.get(isBirth).getTime()].remove(closest);

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
                .count()
                / positives()
                        .filter(pos -> pos.isEligable(BIRTH) && pos.isEligable(DEATH))
                        .count();
    }

    /**
     * The percent of positive defects that have a spouse and a twin.
     *
     * @return The percent of positive defects that have a spouse and a twin.
     */
    public double hasSpouseAndTwin() {
        return (double) positives()
                .filter(defect -> defect.hasSpouse() && defect.hasTwin())
                .count()
                / positives()
                        .filter(pos -> pos.isEligable(BIRTH) && pos.isEligable(DEATH))
                        .count();
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
                .count() / positives().filter(pos -> pos.isEligable(DEATH)).count();
    }

    /**
     * The percent of positive defects that have a twin.
     *
     * @return The percent of positive defects that have a spouse or a twin.
     */
    public double hasTwin() {
        return (double) positives()
                .filter(defect -> defect.hasTwin())
                .count() / positives().filter(pos -> pos.isEligable(BIRTH)).count();
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

            mergeIn(dm);
        }
    }

    /**
     * A stream of setFrames.
     *
     * @return A stream of setFrames.
     */
    public IntStream timeStream() {
        return IntStream.range(0, (int) numFrames);
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

        Frame[] frames = getFrames();
        return Arrays.stream(frames).flatMap(frame
                -> frame.positives().flatMap(pos
                        -> frame.negatives().filter(neg -> neg.loc.dist(pos.loc) < near)
                        .map(neg -> new PairSnDef(pos, neg)))
        );
    }

}
