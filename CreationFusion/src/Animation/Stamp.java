package Animation;

import GeometricTools.Angle;
import GeometricTools.Vec;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * The position and angle of a picture.
 *
 * @author E. Dov Neimand
 */
public class Stamp {

    /**
     * The x and y coordinates.
     */
    public final Vec loc;

    /**
     * The angle the picture is angle.
     */
    public final Angle angle;

    /**
     * Constructor
     * @param x The x position of the center of the image.
     * @param y The y position of the center of the image.
     * @param angle The angle from the center.
     */
    public Stamp(double x, double y, Angle angle) {
        this(new Vec(x, y), angle);
    }


    /**
     * Constructor
     * @param loc The location of the vector.
     * @param angle The angle from the center.
     */    
    public Stamp(Vec loc, Angle angle){
        this.loc = loc;
        this.angle = angle;
    }
    

    /**
     * Creates the transformation for this objects x, y, and angle values.
     *
     * @param imgWidth The width of the image.
     * @param imgHeight The height of the image.
     * @return Creates the transformation for this objects x, y, and angle
     * values.
     */
private AffineTransform affTr(double imgWidth, double imgHeight) {
    AffineTransform at = new AffineTransform();
    
    // Translate back to align the center of the image with (x, y)
    at.translate(-imgWidth / 2.0, -imgHeight / 2.0);
    
    // Translate to the center of the image
    at.translate(loc.getX(), loc.getY());
    
    // Rotate around the center of the image
    at.rotate(angle.rad(), imgWidth / 2.0, imgHeight / 2.0);
    
    
    
    return at;
}


    /**
     * Draws the image at the given coordinates and with the given angle, then
     * disposes of the graphic.
     *
     * @param source The image being copied from.
     * @param drawTo What you want to draw this image to.
     */
    public void draw(BufferedImage source, BufferedImage drawTo) {
        
        Graphics2D g = drawTo.createGraphics();
        
        g.drawImage(source, affTr(source.getWidth(), source.getHeight()), null);
        
        g.dispose();
    }

}
