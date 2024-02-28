package creationfusion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

/**
 * Iterates through each of the frames of the formatted file in order.
 *
 * @author E. Dov Neimand
 */
public class FrameIterator implements Iterator<Frame> {

    private final ChargedFrameIterator pos, neg;

    public FrameIterator(String fileName) {
        pos = new ChargedFrameIterator(fileName, true);
        neg = new ChargedFrameIterator(fileName, false);
    }

    @Override
    public boolean hasNext() {
        return pos.hasNext() || neg.hasNext();
    }

    @Override
    public Frame next() {
        int time = pos.getTime();
        assert (pos.getTime() != neg.getTime());

        return new Frame(pos.next(), neg.next(), time);
    }

    /**
     * An iterator that runs over the snap defects of a specific charge.
     */
    public static class ChargedFrameIterator implements Iterator<HashSet<SnapDefect>> {

        private BufferedReader reader;
        private int time;
        private SnapDefect nextSnap;
        private boolean hasNext;
        private final boolean charge;

        /**
         * Constructs an iterator that gives frame by frame from a file.
         *
         * @param fileName The name of the file.
         * @param charge Should this iterator read positive of negative charged
         * defects?
         */
        public ChargedFrameIterator(String fileName, boolean charge) {
            this.charge = charge;
            try {
                time = 0;

                reader = new BufferedReader(new FileReader(fileName));
                reader.readLine();

                if (ready()) nextSnap = SnapDefect.fromLine(readLine());
                while (nextSnap.getCharge() != charge)
                    nextSnap = SnapDefect.fromLine(readLine());

                hasNext = ready();
            } catch (IOException ex) {
                Logger.getLogger(ChargedFrameIterator.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public boolean hasNext() {

            return hasNext;

        }

        private boolean ready() {
            try {
                return reader.ready();
            } catch (IOException ex) {
                Logger.getLogger(ChargedFrameIterator.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * The next line.
         *
         * @return The next line.
         */
        private String readLine() {
            try {
                return reader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ChargedFrameIterator.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Is this snap defect meant to be included in the current Frame.
         *
         * @param sd The SnapDefect in question.
         * @return True if it's meant to be included in the current frame, false
         * otherwise.
         */
        private boolean isNow(SnapDefect sd) {
            return sd.getTime() == time;
        }

        @Override
        public HashSet<SnapDefect> next() {

            HashSet<SnapDefect> snaps = new HashSet<>();

            String line;

            while ((line = readLine()) != null && isNow(nextSnap)) {
                snaps.add(nextSnap);
                nextSnap = SnapDefect.fromLine(line);
            }
            if (isNow(nextSnap)) snaps.add(nextSnap);
            if (line == null || nextSnap.getCharge() != charge) hasNext = false;

            time++;
            return snaps;

        }

        /**
         * The time of the next Frame.
         *
         * @return The time of the next Frame.
         */
        public int getTime() {
            return time;
        }

    }

}
