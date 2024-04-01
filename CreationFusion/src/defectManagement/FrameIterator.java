package defectManagement;

import SnapManagement.Frame;
import snapDefects.SnapDefect;
import ReadWrite.ReadManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;

/**
 * Iterates through each of the frames of the formatted file in order.
 *
 * @author E. Dov Neimand
 */
public class FrameIterator implements Iterator<Frame> {

    private final ChargedFrameIterator<PosSnapDefect> pos;
    private final ChargedFrameIterator<NegSnapDefect> neg;

    /**
     * Constructs a frame iterator.
     * @param posSnaps A chronologically ordered list of positive snap defects.
     * @param negSnaps A chronologically ordered list of negative snap defects.
     */
    public FrameIterator(List<PosSnapDefect> posSnaps, List<NegSnapDefect> negSnaps) {
        pos = new ChargedFrameIterator(posSnaps);
        neg = new ChargedFrameIterator(negSnaps);
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
     * @param <Snap> Positive or negative snap defects.
     */
    public static class ChargedFrameIterator<Snap extends SnapDefect> implements Iterator<HashMap<Integer, Snap>> {
        
        private int index;        
        
        private List<Snap> snapDefects;

        /**
         * Constructs an iterator that gives frame by frame from a file.
         *
         * @param snapDefects A list of chronologically ordered snap defects.
         */
        @SuppressWarnings("empty-statement")
        public ChargedFrameIterator(List<Snap> snapDefects) {
            index = 0;
            this.snapDefects = snapDefects;
        }

        @Override
        public boolean hasNext() {
            return index < snapDefects.size();
        }


        

        @Override
        public HashMap<Integer, Snap> next() {

            HashMap<Integer, Snap> snaps = new HashMap<>();
            
            for(
                    int time = snapDefects.get(index).loc.getTime(); 
                    index < snapDefects.size() && snapDefects.get(index).getTime() == time; 
                    index++
                    )
                snaps.put(snapDefects.get(index).getID(),snapDefects.get(index));
            
            return snaps;

        }

        /**
         * The index of the next Frame.
         *
         * @return The index of the next Frame.
         */
        public int getTime() {
            return index;
        }

    }

}
