package snapDefects;

import Animation.DefectImage;
import GeometricTools.Angle;
import SnapManagement.Defect;
import SnapManagement.NegDefect;
import java.util.Arrays;

/**
 * A negative SnapDefect.
 * @author E. Dov Neimand
 */
public class NegSnapDefect extends SnapDefect{
    
    private final Angle[] tailAngles;
    private final Angle[] tailAngleVelocities;
    
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
        this.tailAngles = new Angle[3];
        this.tailAngleVelocities = new Angle[3];
        Arrays.setAll(tailAngles, i -> new Angle(angle[i]));
    }

    
    
    @Override
    public boolean getCharge() {
        return false;
    }

    /**
     * The angles of the tails.
     * @return The angles of the tails.
     */
    public Angle[] tailAngle() {
        return tailAngles;
    }

    @Override
    public void setAngleVelocity(SnapDefect prev, SnapDefect next) {
        
        Arrays.setAll(tailAngleVelocities, i -> 
                ((NegSnapDefect)next).tailAngles[i]
                        .minus(((NegSnapDefect)prev).tailAngles[i])
                        .mult(1.0/(next.loc.getTime() - prev.loc.getTime())));
    }
    
    
    @Override
    public NegDefect getDefect() {
        return (NegDefect)defect;
    }

    @Override
    public NegSnapDefect setDefect(Defect defect) {
        super.setDefect(defect);
        return this;
    }
    
    /**
     * The minimum distance between the tail angles.
     * @return The minimum distance between the tail angles.
     */
    public Angle minTailDifference(){
        return new Angle(Math.min(
                tailAngle()[0].arcDist(tailAngle()[1]), 
                Math.min(
                        tailAngle()[0].arcDist(tailAngle()[2]), 
                        tailAngle()[1].arcDist(tailAngle()[2])
                ))
        );
    }

    @Override
    public DefectImage getImage(int diameter) {
        return new DefectImage(this, diameter);
    }

}
