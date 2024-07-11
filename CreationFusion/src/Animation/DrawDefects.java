package Animation;

import GeometricTools.LineSegment;
import GeometricTools.Rectangle;
import GeometricTools.Vec;
import SnapManagement.PairSnDef;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.apache.commons.imaging.Imaging;
import snapDefects.SnapDefect;

/**
 * The DrawDefects class is responsible for creating a combined image from two
 * given images by placing them at specified positions and rotating them to
 * specified angles.
 */
public class DrawDefects {

    private final int diameter;
    private final Rectangle frame;

    /**
     * The constructor.
     *
     * @param width The width of the window
     * @param height The height of the window.
     * @param diameter The diameter of the image
     */
    public DrawDefects(int width, int height, int diameter) {
        frame = new Rectangle(0, 0, width, height);
        this.diameter = diameter;
    }

    /**
     * Draws the images to a file, ensuring they are scaled to fit within the
     * frame.
     *
     * @param outputPath The file path where the combined image will be saved.
     *
     * @param background To be placed behind the
     * @param images The images to be drawn.
     */
    public void draw(File outputPath, File background, DefectImage... images) {
        draw(outputPath, background, new Rectangle(Arrays.stream(images).map(img -> img.embedTo), diameter), images);
    }

    /**
     * Draws the images to a file, ensuring they are scaled to fit within the
     * frame.
     *
     * @param outputPath The file path where the combined image will be saved.
     * @param window The window the defects are coming from. Set this to null
     * and the method will find its own window.
     * @param images The images to be drawn.
     */
    public void draw(File outputPath, Rectangle window, DefectImage... images) {
        draw(outputPath, null, window, images);
    }

    /**
     * Crops a section of the given image file and fits it to frame.
     *
     * @param picture The picture to be cropped and fit.
     * @param window The crop borders.
     * @return A buffered image the size of frame.
     */
    
    private void setBackground(File img, Rectangle window, Graphics2D g2d) {
        if (img == null) {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, (int) frame.width(), (int) frame.height());
            
        } else {
            try {
                BufferedImage grabFrom = Imaging.getBufferedImage(img).getSubimage(
                        (int) window.getX(),
                        (int) window.getY(),
                        (int) window.width(),
                        (int) window.height()
                );
                
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(grabFrom, 0, 0, (int) frame.width(), (int) frame.height(), null);
            } catch (IOException ex) {
                Logger.getLogger(DrawDefects.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    /**
     * Draws the images to a file, ensuring they are scaled to fit within the
     * frame.
     *
     * @param outputPath The file path where the combined image will be saved.
     * @param window The window the defects are coming from. Set this to null
     * and the method will find its own window.
     * @param background To be placed behind the
     * @param images The images to be drawn.
     */
    public void draw(File outputPath, Rectangle window, File background, SnapDefect... images) {
        draw(outputPath, 
                background, window, 
                Arrays.stream(images)
                        .map(snap -> snap.getImage(diameter))
                        .toArray(DefectImage[]::new)
        );
    }

    /**
     * Draws the images to a file, ensuring they are scaled to fit within the
     * frame.
     *
     * @param outputPath The file path where the combined image will be saved.
     * @param window The window the defects are coming from. Set this to null
     * and the method will find its own window.
     * @param background To be placed behind the
     * @param images The images to be drawn.
     */
    public void draw(File outputPath, File background, Rectangle window, DefectImage... images) {
        BufferedImage toImg = new BufferedImage((int) frame.width(), (int) frame.height(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = toImg.createGraphics();

        setBackground(background, window, g2d);

        for (DefectImage image : images) {
            Vec scaled = window.scale(image.embedTo, frame);
            g2d.drawImage(image, (int) scaled.getX(), (int) scaled.getY(), null);
        }

        g2d.dispose();
        try {
            ImageIO.write(toImg, "png", outputPath);
        } catch (IOException ex) {
            Logger.getLogger(DrawDefects.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the bounds for scaling the image positions using a loop. Ensures
     * that the entire negative defect is visible.
     *
     * @param pairs The images to be drawn.
     * @param diameter The diameter of the defects.
     * @return A Rectangle representing the bounds.
     */
    private static Rectangle getBounds(List<PairSnDef> pairs, int diameter) {

        Rectangle bounds = new Rectangle(diameter);

        pairs.stream().flatMap(pair -> pair.defects()).forEach(snap -> bounds.expand(snap.loc));

        return bounds;

    }

    /**
     * Draws the images at their scaled positions on the Graphics2D object.
     *
     * @param g2d The Graphics2D object.
     * @param images The images to be drawn.
     * @param bounds The bounds for scaling the image positions.
     * @param scaleX The scaling factor for the x dimension.
     * @param scaleY The scaling factor for the y dimension.
     */
    private void drawImages(Graphics2D g2d, DefectImage[] images, Rectangle bounds, double scaleX, double scaleY) {
        int r = diameter / 2;

        for (DefectImage image : images) {
            Vec scaled = bounds.scale(image.embedTo, frame);
            g2d.drawImage(image, (int) scaled.getX(), (int) scaled.getY(), null);
        }
    }

    /**
     * Saves the BufferedImage to the specified output file path.
     *
     * @param outputPath The file path where the image will be saved.
     * @param image The BufferedImage to be saved.
     */
    private void saveImage(File outputPath, BufferedImage image) {
        try {
            ImageIO.write(image, "png", outputPath);
        } catch (IOException ex) {
            Logger.getLogger(DrawDefects.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Draws the proffered defects in the folder.
     *
     * @param defectDiameter The diameter of the image.
     * @param frameWidth The width of the pictures.
     * @param frameHeight The height of the pictures.
     * @param parentFolder The folder the new pictures will be put in.
     * @param defectPairs The defect pairs to be drawn.
     */
    public static void drawDefectPairs(int defectDiameter, int frameWidth, int frameHeight, File parentFolder, List<PairSnDef> defectPairs) {
        DrawDefects di = new DrawDefects(frameWidth, frameHeight, defectDiameter);

        Rectangle window = getBounds(defectPairs, defectDiameter);

        defectPairs.forEach(pair -> di.draw(
                new File(parentFolder + File.separator + pair.pos.getTime() + "_" + pair.pos.getId() + "_" + pair.neg.getId() + ".png"),
                window,
                new DefectImage(pair.pos, defectDiameter),
                new DefectImage(pair.neg, defectDiameter)
        )
        );
    }
    
    
    
}
