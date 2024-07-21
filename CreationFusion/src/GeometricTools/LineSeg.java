package GeometricTools;

import java.awt.Graphics2D;
import static java.lang.Math.round;

/**
 * A line segment.
 * @author E. Dov Neimand
 */
public class LineSeg {
    public final Vec a, b;

    /**
     * A line segment in R2.
     * @param a One end of the segment.
     * @param b The other end of the segment.
     */
    public LineSeg(Vec a, Vec b) {
        this.a = a;
        this.b = b;
    }
    
    /**
     * Draws the line segment.
     * @param g2d The drawer.
     */
    public void draw(Graphics2D g2d){
        g2d.drawLine(
                (int)round(a.getX()), 
                (int)round(a.getY()), 
                (int)round(b.getX()), 
                (int)round(b.getY())
        );
    }
    
}
