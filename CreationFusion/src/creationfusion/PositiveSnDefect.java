package creationfusion;

/**
 * A positive SnapDefect.
 * @author E. Dov Neimand
 */
public class PositiveSnDefect extends SnapDefect{
    
    private double angle;
    
    /**
     * A positive SnapDefect.
     * @param x The x location.
     * @param y The y location.
     * @param t The time.
     * @param id The id of the defect.
     * @param angle The angle of the defect relative to the x axis.
     */
    public PositiveSnDefect(double x, double y, int t, int id, double angle) {
        super(x, y, t, id);
        this.angle = angle;
    }

    @Override
    public boolean getCharge() {
        return true;
    }
    
    
    
}
