package Animation;

import GeometricTools.Angle;
import GeometricTools.Rectangle;
import GeometricTools.Vec;
import SnapManagement.PairSnDef;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import javax.imageio.ImageIO;
import javax.swing.Spring;
import snapDefects.NegSnapDefect;
import snapDefects.PosSnapDefect;
import snapDefects.SnapDefect;

/**
 * A Draw images class that is specific for defects.
 *
 * @author E. Dov Neimand
 */
public class DrawDefects extends DrawImages {

    /**
     * How much distance one pixel covers.
     */
    private final double scale;

    /**
     * The x and y coordinates of the top left corner of the window.
     */
    private final Rectangle windowInData;

    /**
     * The constructor.
     *
     * @param posDefImgPath The path to a positive defect image.
     * @param negDefImgPath The path to a negative defect image.
     * @param fromDim This is the smallest rectangle that contains the area of interest in the data.
     * @param toDim These are the dimensions of the picture on the screen.
     *
     */
    public DrawDefects(String posDefImgPath, String negDefImgPath, Rectangle fromDim, Vec toDim) {
        super((int)toDim.getX(), (int)toDim.getY(), posDefImgPath, negDefImgPath);
        this.scale = Math.min(toDim.getY()/fromDim.height, toDim.getX()/fromDim.width);
        this.windowInData = fromDim;
    }

    /**
     * Draws the defects of a pair
     *
     * @param outputPath The file to draw to.
     * @param pair The pair of defects to be drawn.
     */
    public void draw(String outputPath, PairSnDef pair) {

        Stamp neg = new Stamp(
                pair.neg.loc.minus(windowInData.minCorner).mult(scale),
                pair.neg.tailAngle()[0].mult(-1)
        );

        Stamp pos = new Stamp(
                pair.pos.loc.minus(windowInData.minCorner).mult(scale),
                pair.pos.tailAngle().mult(-1)
        );

        draw(outputPath, neg, pos);
    }

    /**
     * Finds the extremes values in the given dimension of the list.
     * @param pairs Thae pairs for which an extreme point is desired.
     * @param val A dimension choosing function, either p-> p.x or p->p.y.
     * @param isMax True if the maximum is desired, false if the minimum is desired.
     * @return The maximum or minimum in the given dimension.
     */
    private static double extreme(List<PairSnDef> pairs, ToDoubleFunction<Vec> val, boolean isMax) {
        DoubleStream doubles = pairs.stream()
                .mapToDouble(pair -> {
                    double a = val.applyAsDouble(pair.pos.loc) + (isMax ? 1 : -1);
                    double b = val.applyAsDouble(pair.neg.loc) + (isMax ? 1 : -1);
                    return isMax ? Math.max(a, b) : Math.min(a, b);
                });
        return (isMax ? doubles.max() : doubles.min()).getAsDouble();
    }

    /**
     * Creates a sequence of images in the selected folder.
     *
     * @param posPath The path to a picture of a positive defect.pairs@param
     * negPath The path to a picture of a negative defect.
     * @param negPath The path to the picture of the negative defect.
     * @param writeToFolder The folder to put the new pictures in.
     * @param pairs The defects to be put in the picture.
     */
    public static void draw(String posPath, String negPath, String writeToFolder, List<PairSnDef> pairs) {

        try {
            BufferedImage posImg = ImageIO.read(new File(posPath));
            BufferedImage negImg = ImageIO.read(new File(negPath));
            
            double defImgHeight = Math.max(posImg.getHeight(), negImg.getHeight())/2,
                    defImgWidth = Math.max(posImg.getWidth(), negImg.getWidth())/2;

            double x = extreme(pairs, p -> p.getX(), false) - defImgWidth,
                    y = extreme(pairs, p -> p.getY(), false) - defImgHeight,
                    width = extreme(pairs, p -> p.getX(), true) + defImgWidth - x,
                    height = extreme(pairs, p -> p.getY(), true) + defImgHeight - y;
            Rectangle minRect = new Rectangle(x, y, width, height);
            
            Vec imgWidthHeight = new Vec(1000, 1000);
            
            DrawDefects dd = new DrawDefects(posPath, negPath, minRect, imgWidthHeight);

            pairs.forEach(pair -> dd.draw(writeToFolder + File.pathSeparator + pair.pos.loc.getTime() + "_" + pair.pos.getId() + "_" +pair.neg.getId(), pair));
            
        } catch (IOException ex) {
            Logger.getLogger(DrawDefects.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public static void main(String[] args) {
        List<PairSnDef> pairs = new ArrayList<>();
        
        pairs.add(new PairSnDef(new PosSnapDefect(100, 100, 0, 0, 0), new NegSnapDefect(200, 100, 0, 1, 0, 2*Math.PI/3, 4*Math.PI/3)));
        draw("images/source/PosDef.png", "images/source/NegDef.png", "images/output/", pairs);
    }

}
