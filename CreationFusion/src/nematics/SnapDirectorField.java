package nematics;

import Charts.Histogram;
import GeometricTools.Angle;
import GeometricTools.NematicDirector;
import GeometricTools.Rectangle;
import GeometricTools.Vec;
import ImageWork.CellPSegm;
import ImageWork.Pixel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Function;
import javax.imageio.ImageIO;

/**
 *
 * @author E. Dov Neimand
 */
public class SnapDirectorField implements Function<Vec, NematicDirector> {

    private Set<? extends NematicFieldAnchor> anchors;

    /**
     * The constructor.
     *
     * @param anchors The cells that form the foundation of the field.
     * @param qualityThreshold A positive number. The larger it is, the more
     * circular cells will be removed.
     */
    public SnapDirectorField(Set<? extends NematicFieldAnchor> anchors, double qualityThreshold) {
        this.anchors = anchors;

        anchors.removeIf(cell -> cell.quality() < qualityThreshold);
    }

    /**
     * Draws a nematic streamline.
     *
     * @param g2d Draws the streamline
     */
    private void drawNematicStreamLine(Graphics2D g2d, Vec x, double h, Rectangle window) {

        NematicDirector nem = apply(x);
        Vec a = nem.vec().mult(h).plus(x), b = nem.vec().mult(-h).plus(x);
        drawStreamLine(g2d, x, a, h, window);
        drawStreamLine(g2d, x, b, h, window);
    }

    /**
     * Draws a stream line in one direction.
     *
     * @param g2d The drawing tool.
     * @param p0 A point before the starting point.
     * @param p1 The starting point.
     * @param h The step size.
     * @param window The window dimensions.
     */
    private void drawStreamLine(Graphics2D g2d, Vec p0, Vec p1, double h, Rectangle window) {
        while (window.contains(p1)) {
            Vec p2 = nextStreamLinePoint(p0, p1, h);
            if (!p1.samePixel(p2)) g2d.drawLine(p1.x(), p1.y(), p2.x(), p2.y());
            p0 = p1;
            p1 = p2;
        }

    }

    /**
     * The next point on the stream line
     *
     * @param prev The previous point on the streamline.
     * @param current The current point on the streamline.
     */
    private Vec nextStreamLinePoint(Vec prev, Vec current, double h) {
        Vec dir = apply(current).vec();
        Vec next = dir.mult(h).plus(current);
        if (next.dist(prev) < current.dist(prev)) next = dir.mult(-h).plus(current);
        return next;
    }

    @Override
    public NematicDirector apply(Vec t) {

        return Angle.average(//TODO: Should the impact of a cell depend on its size?
                anchors.stream()
                        .map(anchor -> anchor.getDir().weightedAngle(1 / Math.pow(t.dist(anchor.loc()), 3)))
        ).nematic();
    }

    /**
     * An image of the streamlines.
     *
     * @return An image of the streamlines.
     */
    public BufferedImage streamLineImage(int width, int height, double h) {
        BufferedImage nai = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = nai.createGraphics();
        g2d.setColor(Color.green);

        int numLines = 30;
        for(double i = 0; i < numLines; i++)
            drawNematicStreamLine(
                    g2d, 
                    new Vec(width*(i/numLines), height / 2), 
                    1, 
                    new Rectangle(width, height)
            );

        g2d.dispose();
        return nai;

    }

    /**
     * A histogram of how the cells are elliptical. The higher the value, the
     * less circular.
     */
    public void errorHistogram() {
        Histogram.factory(
                anchors.stream().mapToDouble(anchor -> anchor.quality()).toArray(),
                100,
                "error",
                "error");
    }

    /**
     * An image with each nematic field anchor displayed as a dash.
     *
     * @param width The width of the picture.
     * @param height The height of the picture.
     * @param dashLength The length of the dashes.
     * @return An image with each nematic field anchor displayed as a dash.
     */
    public BufferedImage nematicAnchorsImage(int width, int height, double dashLength) {
        BufferedImage nai = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = nai.createGraphics();
        g2d.setColor(Color.green);
        anchors.stream().forEach(anchor -> anchor.dash(dashLength).draw(g2d));
        g2d.dispose();
        return nai;
    }

    /**
     * Places one image on top of another and saves it.
     *
     * @param underImage The background image.
     * @param overlay The foreground image.
     * @param saveTo Where to save the new picture to.
     * @throws IOException If there's any trouble loading or saving files.
     */
    public static void superimposeImage(BufferedImage underImage, BufferedImage overlay, File saveTo) throws IOException {

        Graphics2D g2d = underImage.createGraphics();
        g2d.drawImage(overlay, 0, 0, null);
        g2d.dispose();
        ImageIO.write(underImage, "png", saveTo);
        System.out.println("New picture written to " + saveTo);
    }

    public static void main(String[] args) throws IOException{
        String inputImagePath = "../../VictorData/HBEC/s2(120-919)/Trans__401.tif";
        String tempImagePath = "images/output/segmented.png";
        String outputImagePath = "images/output/nematicAnchors.png";

//        CellPSegm.fromSegmented(cellPicturePath): CellPSegm.defaultFromRaw(cellPicturePath)
        SnapDirectorField sdf = new SnapDirectorField(CellPSegm.fromSegmented(tempImagePath), 1.3);

        BufferedImage bi = ImageIO.read(new File(inputImagePath));
        
//        sdf.nematicAnchorsImage(bi.getWidth(), bi.getHeight(), 30)
        
        superimposeImage(bi, sdf.streamLineImage(bi.getWidth(), bi.getHeight(), .1), new File(outputImagePath));

    }
}
