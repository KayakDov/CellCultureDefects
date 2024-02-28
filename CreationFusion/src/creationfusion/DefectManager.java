package creationfusion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a manager for tracking defects.
 */
public class DefectManager {

    private List<Defect> posDefects, negDefects;
    private String fileName;
    private int timeThreshold;
    double distThreshold;
    private int endTime = -1;

    /**
     * Constructs a DefectManager with the specified file name.
     *
     * @param fileName The name of the file containing defect data.
     */
    public DefectManager(String fileName) {
        this.negDefects = Collections.synchronizedList(new ArrayList<Defect>());
        this.posDefects = Collections.synchronizedList(new ArrayList<Defect>());
        this.fileName = fileName;
    }

    /**
     * All the lines of the file.
     *
     * @return All the lines of the file.
     */
    private Stream<String> fileStream() {
        try {
            return Files.lines(Paths.get(fileName)).skip(1);
        } catch (IOException ex) {
            Logger.getLogger(DefectManager.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Has each defect track its absences.
     */
    public void setAbsences() {

        fileStream()
                .map(line -> SnapDefect.fromLine(line))
                .filter(SnapDefect::isTracked)
                .forEachOrdered(sd -> {

                    List<Defect> defects = defects(sd.getCharge());

                    if (!addDefect(sd)) {

                        Defect def = defects.get(sd.getID());

                        if (sd.getTime() == def.getBirth().getTime())
                            def.resetDeath();

                        else {
                            int absence = sd.getTime() - def.getDeath().getTime() - 1;
                            if (absence > 0) def.addToTimeAWOL(absence);
                            def.update(sd);
                        }
                    }
                });
    }

    /**
     * Checks if each defect is stored correctly according to its index.
     *
     * @return True if each index is stored correctly according to its index.
     * False otherwise.
     */
    public boolean indeciesAreCorrect() {

        return IntStream.range(0, posDefects.size()).allMatch(i -> posDefects.get(i) != null && i == posDefects.get(i).getID())
                && IntStream.range(0, negDefects.size()).allMatch(i -> negDefects.get(i) != null && i == negDefects.get(i).getID());

    }

    private List<Defect> defects(boolean charge) {
        return charge ? posDefects : negDefects;
    }

    /**
     * Checks that no indices in the file are skipped, and prints new indices as
     * they appear.
     *
     * @return True if no indices in the file are skipped.
     */
    public List<Integer> orderOfApearence(boolean charge) {

        HashSet<Integer> defects = new HashSet<>(posDefects.size() + negDefects.size());
        ArrayList<Integer> orderOfAppearence = new ArrayList<>(defects(charge).size());

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                SnapDefect sd = SnapDefect.fromLine(line);

                if (sd.isTracked() && sd.getCharge() == charge && !defects.contains(sd.getID())) {
                    defects.add(sd.getID());
                    orderOfAppearence.add(sd.getID());
                }
            }

            return orderOfAppearence;

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * The number of defects who's ID is not 1 plus the ID of the preceding
     * first appearance.
     *
     * @return The number of defects who's ID is not 1 plus the ID of the
     * preceding first appearance.
     */
    public long outOfOrder() {
        List<Integer> ooap = orderOfApearence(true);

        long count = IntStream.range(1, ooap.size())
                .filter(i -> ooap.get(i) != ooap.get(i - 1))
                .count();

        List<Integer> ooam = orderOfApearence(false);

        count += IntStream.range(1, ooam.size())
                .filter(i -> ooam.get(i) != ooam.get(i - 1)).count();

        return count;

    }

    /**
     * Adds a defect to the appropriate container.
     *
     * @param sd The defect to be added.
     */
    private boolean addDefect(SnapDefect sd) {
        List<Defect> defects = defects(sd.getCharge());
        while (defects(sd.getCharge()).size() <= sd.getID()) defects.add(null);
        if (defects.get(sd.getID()) == null) {
            defects.set(sd.getID(), new Defect(sd));
            return true;
        }
        return false;
    }

    /**
     * Takes in a bunch of lines and adds the defects to this manager.
     *
     * @param snaps The formated lines of the file.
     */
    private void AddSnapDefects(Stream<SnapDefect> snaps) {
        snaps.filter(SnapDefect::isTracked)
                .filter(sd -> !addDefect(sd))
                .forEach(sd -> defects(sd.getCharge()).get(sd.getID()).update(sd));
    }

    /**
     * Loads defects from the specified file.
     *
     */
    public void loadDefectsForPairing() {

        AddSnapDefects(fileStream().parallel().map(line -> SnapDefect.fromLine(line)));

    }

    /**
     * Pairs the defects with the default thresholds for long eukaryotic cells.
     */
    public void pairDefects() {
        pairDefects(SpaceTemp.defaultDistanceThreshold, SpaceTemp.defaultTimeThreshold);
    }

    /**
     * Pairs defects based on proximity and time thresholds.
     *
     * @param dist The distance threshold for pairing defects.
     * @param time The time threshold for pairing defects.
     */
    public void pairDefects(double dist, int time) {
        clearPairing();

        if (posDefects.isEmpty()) loadDefectsForPairing();

        setThresholds(time, dist);

        Set<Defect> possibleBirths = new HashSet<>(negDefects);
        Set<Defect> possibleDeaths = new HashSet<>(negDefects);

        for (Defect lonely : posDefects) {
            if (lonely.getBirth().getTime() > time)
                lonely.setTwin(getPair(lonely, possibleBirths, true));
            if (lonely.getDeath().getTime() < getEndTime() - time)
                lonely.setSpouse(getPair(lonely, possibleDeaths, false));
        }
    }

    /**
     * The total amount of time in the file.
     *
     * @return The total amount of time in the file.
     */
    public long getEndTime() {
        if (endTime == -1)
            if (posDefects.isEmpty())
                return endTime = fileStream().parallel()
                        .map(line -> SnapDefect.fromLine(line))
                        .mapToInt(sd -> sd.getTime()).max().getAsInt();
            else
                return endTime = all().mapToInt(def -> def.getDeath().getTime()).max().getAsInt();

        return endTime;
    }

    /**
     * Calculates the percentage of defects that are tracked.
     *
     * @return The percentage of tracked defects.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public double percentTracked() throws IOException {
        int untracked = 0, tracked = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                SnapDefect sd = SnapDefect.fromLine(line);
                if (sd.isTracked()) {
                    tracked++;
                } else {
                    untracked++;
                }
            }
        }
        return (double) tracked / (tracked + untracked);
    }

    /**
     * Gets the number of positive defects.
     *
     * @return The number of positive defects.
     */
    public int numPositiveDefect() {
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
        return Stream.concat(posDefects.stream(), negDefects.stream());
    }

    /**
     * All the positive defects.
     *
     * @return All the positive defects.
     */
    public Stream<Defect> positives() {
        return posDefects.stream();
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
    private Defect getPair(Defect lonely, Set<Defect> eligibles, boolean birth) {
        Defect closest = eligibles.stream()
                .parallel()
                .filter(eligable -> eligable.near(lonely, birth, distThreshold, timeThreshold))
                .min(Comparator.comparing(eligable -> eligable.get(birth).dist(lonely.get(birth))))
                .orElse(null);

        if (closest != null) eligibles.remove(closest);

        return closest;
    }

    /**
     * Sets proximity thresholds.
     *
     * @param time Another point is near this one if it is closer in time.
     * @param dist Another point is near this one if it's closer in space.
     */
    private void setThresholds(int time, double dist) {
        this.timeThreshold = time;
        this.distThreshold = dist;
    }

    /**
     * clears all stored defects.
     */
    private void clearDefects() {
        posDefects.clear();
        negDefects.clear();
    }

    /**
     * Removes all spouses and twins.
     */
    private void clearPairing() {
        posDefects.stream().parallel().forEach(defect -> defect.clearPairs());
    }

    /**
     * An iterator that reads directly from the file, one frame at a time
     * for the given charge.
     *
     * @param charge The charge of the desired frame.
     * 
     * @return An iterator that reads positively or negatively charged defects 
     * directly from the file, one frame at a time.
     */
    public FrameIterator.ChargedFrameIterator frameIterator(boolean charge) {
        return new FrameIterator.ChargedFrameIterator(fileName, charge);
    }
    
    /**
     * An iterator that reads directly from the file, one frame at a time.
     *     * 
     * @return An iterator that reads directly from the file, one frame at a
     * time.
     */
    public FrameIterator frameIterator(){
        return new FrameIterator(fileName);
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
                .count() / posDefects.size();
    }

    /**
     * The percent of positive defects that have a spouse and a twin.
     *
     * @return The percent of positive defects that have a spouse and a twin.
     */
    public double hasSpouseAndTwin() {
        return (double) positives()
                .filter(defect -> defect.hasSpouse() && defect.hasTwin())
                .count() / posDefects.size();
    }

    /**
     * The percent of positive defects that have a spouse or a twin.
     *
     * @return The percent of positive defects that have a spouse or a twin.
     */
    public double hasSpouseOrTwin() {
        return (double) positives()
                .filter(defect -> defect.hasSpouse() || defect.hasTwin())
                .count() / posDefects.size();
    }

    /**
     * The percent of positive defects that have a spouse.
     *
     * @return The percent of positive defects that have a spouse or a twin.
     */
    public double hasSpouse() {
        return (double) positives()
                .filter(defect -> defect.hasSpouse())
                .count() / posDefects.size();
    }

    /**
     * The percent of positive defects that have a twin.
     *
     * @return The percent of positive defects that have a spouse or a twin.
     */
    public double hasTwin() {
        return (double) positives()
                .filter(defect -> defect.hasTwin())
                .count() / posDefects.size();
    }

    /**
     * The total charge over the entire system from start to end.
     *
     * @return The total charge over the entire system from start to end.
     */
    public double cumulativeSystemCharge() {
        return all()
                .mapToDouble(defect -> defect.age() * (defect.getCharge() ? .5 : -.5))
                .sum();
    }
    
    /**
     * A stream of all the frames, created from an iterator going over the file.
     * @return A stream of all the frames.
     */
    public Stream<Frame> frameStream(){
        return StreamSupport.stream(
                Spliterators.spliterator(
                        frameIterator(), endTime, Spliterator.IMMUTABLE
                ),
                false
        );
    }

}
