package creationfusion;

/**
 * A negative SnapDefect.
 * @author E. Dov Neimand
 */
public class NegSnapDefect extends SnapDefect{
    
    private double[] tailAngles;
    
    /**
     * A negative SnapDefect.
     * @param x The x location.
     * @param y The y location.
     * @param id The id of the defect.
     * @param angle The angles of thee defect.
     * @param t The time (frame number).
     */
    public NegSnapDefect(double x, double y, int t, int id, double... angle) {
        super(x, y, t, id);
        this.tailAngles = angle;
    }

    
    
    @Override
    public boolean getCharge() {
        return false;
    }

    /**
     * The angles of the tails.
     * @return The angles of the tails.
     */
    public double[] tailAngles() {
        return tailAngles;
    }
    
    
    
}
