package defectManagement;

import SnapManagement.Frame;
import snapDefects.SnapDefect;
import ReadWrite.ReadManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Iterates through each of the frames of the formatted file in order.
 *
 * @author E. Dov Neimand
 */
public class FrameIterator implements Iterator<Frame> {

    private final ChargedFrameIterator pos, neg;

    /**
     * Constructs a frame iterator.
     * @param fileFormat The file to be read from.
     */
    public FrameIterator(ReadManager fileFormat) {
        pos = new ChargedFrameIterator(fileFormat, DefectManager.POS);
        neg = new ChargedFrameIterator(fileFormat, DefectManager.NEG);
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
    public static class ChargedFrameIterator implements Iterator<HashMap<Integer, SnapDefect>> {
        
        private ReadManager.Reader reader;
        private int time;
        private boolean hasNext;
        private final boolean charge;

        /**
         * Constructs an iterator that gives frame by frame from a file.
         *
         * @param fileFormat The format of the file to be read from.
         * @param charge Should this iterator read positive of negative charged
         * defects?
         */
        @SuppressWarnings("empty-statement")
        public ChargedFrameIterator(ReadManager fileFormat, boolean charge) {
            
            
            this.charge = charge;
            time = 0;
            reader = fileFormat.getReader(charge);
            
            hasNext = reader.ready();

        }

        @Override
        public boolean hasNext() {

            return hasNext;

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
        public HashMap<Integer, SnapDefect> next() {

            HashMap<Integer, SnapDefect> snaps = new HashMap<>();

            SnapDefect sd;

            while ((sd = reader.readSnapDefect()) != null) {
                
                
                if(!isNow(sd)){
                    reader.backOneLine();
                    break;
                }
                
                snaps.put(sd.getID(), sd);
                
            }
            
            if (sd == null) hasNext = false;

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
