package Animation;

import GeometricTools.Vec;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;
import snapDefects.SnapDefect;

/**
 * A diagram of a defect.
 *
 * @author E. Dov Neimand
 */
public class DefectImage extends BufferedImage {

    public final Vec embedTo;
    public final Color color;

    /**
     * Creates a BufferedImage with a transparent background and a disk in the
     * middle.
     *
     * @param width The width of the image.
     * @param height The height of the image.
     * @return A BufferedImage with a transparent background and a disk in the
     * middle.
     */
    private DefectImage(SnapDefect sd, int diameter, Color color) {
        super(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        embedTo = sd.loc;
        this.color = color;

        Graphics2D g2d = createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, diameter, diameter);
        g2d.setComposite(AlphaComposite.SrcOver);

        int innerRadius = diameter / 15;

        g2d.setColor(color);
        g2d.fillOval(diameter / 2 - innerRadius, diameter / 2 - innerRadius, 2 * innerRadius, 2 * innerRadius);

        g2d.dispose();
    }

    /**
     * Adds a tail to the given BufferedImage at a specified angle.
     *
     * @param image The BufferedImage to draw on.
     * @param angle The angle of the line segment.
     */
    private void addTail(double angle) {
        Graphics2D g2d = createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centX = getWidth() / 2, centY = getHeight() / 2;

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(color);
        double lineLength = Math.min(getWidth(), getHeight()) / 2;
        int endX = (int) (centX + lineLength * Math.cos(angle));
        int endY = (int) (centY + lineLength * Math.sin(angle));
        g2d.drawLine(getWidth() / 2, centY, endX, endY);

        g2d.dispose();
    }

    /**
     * Constructs the image for a positive snap defect. The circle will be in
     * the center of the image.
     *
     * @param psd The defect.
     * @param diameter The diameter of the defect.
     */
    public DefectImage(PosSnapDefect psd, int diameter) {
        this(psd, diameter, Color.RED);
        addTail(psd.tailAngle().rad());
    }

    /**
     * Constructs the image for a negative snap defect. The circle will be in
     * the center of the image.
     *
     * @param nsd The defect.
     * @param diameter The diamter of the defect.
     */
    public DefectImage(NegSnapDefect nsd, int diameter) {
        this(nsd, diameter, Color.BLUE);
        Arrays.stream(nsd.tailAngle()).map(tail -> tail.rad())
                .forEach(tailAngle -> addTail(tailAngle));
    }

}
