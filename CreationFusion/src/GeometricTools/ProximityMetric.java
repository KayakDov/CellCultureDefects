package GeometricTools;

import snapDefects.SpaceTemp;

/**
 * A distance measuring tool in space and time.
 * @author E. Dov Neimand
 */
public class ProximityMetric {
    public final int rTime;
    public final double rSpace;

    /**
     * Constructs a ball in space and time.
     * @param rTime Values closer than rTime are in the ball.
     * @param rSpace Values closer that rSpace are in the ball.
     */
    public ProximityMetric(double rSpace, int rTime) {
        this.rTime = rTime;
        this.rSpace = rSpace;
    }
    
    /**
     * Are two moments in space time near one another.
     * @param a The first time and place.
     * @param b The second time and place.
     * @return True if there time is closer than rTime and their distance is 
     * closer than rSpace.
     */
    public boolean near(SpaceTemp a, SpaceTemp b){
        return a.dist(b) < rSpace && Math.abs(a.getTime() - b.getTime()) < rTime;
    }
}
