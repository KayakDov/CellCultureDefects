package Animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * The DrawImages class is responsible for creating a combined image from two
 * given images by placing them at specified positions and rotating them to
 * specified angles.
 */
public class DrawImages {

    private BufferedImage[] img;
 
    private final int width, height;

    /**
     * Constructs a DrawDefects instance with the specified image file paths.
     *
     * 
     * @param width The width of the new image.
     * @param height The height of the new image.
     * @param imgPaths The paths to the images.
     */
    public DrawImages(int width, int height, String... imgPaths) {

        this.width = width;
        this.height = height;
        
        img = new BufferedImage[imgPaths.length];
        Arrays.setAll(img, i -> {
            try {
                return ImageIO.read(new File(imgPaths[i]));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

    }

    /**
     * Creates a combined image by placing the two images at the specified
     * positions and rotating them to the specified angles. The combined image
     * is saved to the specified output file path.
     *
     * @param stamps The stamps.  There should be one for each image.
     * @param outputPath The file path where the combined image will be saved.
     */
    public void draw(String outputPath, Stamp... stamps) {
        
        BufferedImage toImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < img.length; i++) stamps[i].draw(img[i], toImg);
        
        try {
            ImageIO.write(toImg, "png", new File(outputPath));
        } catch (IOException ex) {
            Logger.getLogger(DrawImages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    /**
//     * The main method to test the DrawImages class.
//     *
//     * @param args Command-line arguments (not used).
//     */
//    public static void main(String[] args) {
//        DrawImages drawDefects = new DrawImages(
//                "images/source/PosDef.png",
//                "images/source/NegDef.png"
//        );
//
//        drawDefects.draw(
//                "images/output/CombinedDefects.jpg",
//                new Stamp(100, 100, new Angle(3 * Math.PI / 2)),
//                new Stamp(400, 400, new Angle(0))                
//        );
//
//        System.out.println("Combined image created at: images/output/CombinedDefects.jpg");
//    }

}
