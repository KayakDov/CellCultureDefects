//package ImageWork;
//
//
//import java.awt.Color;
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferByte;
//import org.opencv.core.Mat;
//import java.io.File;
//import java.io.IOException;
//import javax.imageio.ImageIO;
//
//public class DrawSegmentation {
//
//    /**
//     * Creates a PNG file representing SnapCells with each cell drawn in a different color.
//     *
//     * @param snapCells Set of SnapCellOutline objects to draw.
//     * @param outputFile File object representing the location to save the PNG file.
//     * @throws IOException If an error occurs while writing the PNG file.
//     */
//    public static void createColoredCellsImage(Segmentation snapCells, File outputFile) throws IOException {
//        // Determine image dimensions based on the largest cell dimensions
//        int maxWidth = 0;
//        int maxHeight = 0;
//        for (SnapCellOutline cell : snapCells) {
//            Mat cellImage = cell.getOutline();
//            maxWidth = Math.max(maxWidth, cellImage.cols());
//            maxHeight = Math.max(maxHeight, cellImage.rows());
//        }
//
//        // Create a BufferedImage with RGB color space
//        BufferedImage image = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);
//
//        // Assign a unique color to each SnapCellOutline and draw it on the BufferedImage
//        int colorIndex = 0;
//        for (SnapCellOutline cell : snapCells) {
//            Color color = getRandomColor(colorIndex++); // Generate a unique color for each cell
//            Mat cellImage = cell.getOutline();
//            drawCell(image, cellImage, color);
//        }
//
//        // Save the BufferedImage as a PNG file
//        ImageIO.write(image, "png", outputFile);
//    }
//
//    /**
//     * Draws a cell image onto a BufferedImage with the specified color.
//     *
//     * @param image BufferedImage to draw on.
//     * @param cellImage Mat object representing the cell image.
//     * @param color Color to use for drawing the cell.
//     */
//    private static void drawCell(BufferedImage image, Mat cellImage, Color color) {
//        // Convert OpenCV Mat to BufferedImage
//        BufferedImage cellBufferedImage = matToBufferedImage(cellImage);
//
//        // Draw the cell image onto the main image with the specified color
//        for (int y = 0; y < cellBufferedImage.getHeight(); y++) {
//            for (int x = 0; x < cellBufferedImage.getWidth(); x++) {
//                int rgb = cellBufferedImage.getRGB(x, y);
//                if ((rgb & 0x00FFFFFF) != 0) { // Non-transparent pixel
//                    image.setRGB(x, y, color.getRGB());
//                }
//            }
//        }
//    }
//
//    /**
//     * Converts an OpenCV Mat object to a BufferedImage.
//     *
//     * @param mat OpenCV Mat object to convert.
//     * @return Converted BufferedImage.
//     */
//    private static BufferedImage matToBufferedImage(Mat mat) {
//        int type = BufferedImage.TYPE_BYTE_GRAY;
//        if (mat.channels() > 1) {
//            type = BufferedImage.TYPE_3BYTE_BGR;
//        }
//        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
//        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
//        return image;
//    }
//
//    /**
//     * Generates a random color based on an index.
//     *
//     * @param index Index to generate the color.
//     * @return Generated Color object.
//     */
//    private static Color getRandomColor(int index) {
//        float hue = (float) index * 0.618033988749895f; // Golden ratio hue
//        hue = hue - (int) hue; // Normalize hue
//        return Color.getHSBColor(hue, 0.8f, 0.9f); // Slightly saturated and bright colors
//    }
//
//    static{
//        /**
//         * prevents linking error.
//         */
//        System.load("/home/edov/projects/CreationFusionCount/Vision/opencv/build/lib/libopencv_java4100.so");
//    }
//    
//    public static void main(String[] args) {
//
//
//        System.out.println("ImageWork.DrawSegmentation.main()");
//        
//        String inputFile = "/home/edov/projects/VictorData/HBEC/s2(120-919)/Trans__042.tif";
//        
//        System.out.println("reading " + inputFile);
//        
//        String outputFile = "/home/edov/projects/CreationFusionCount/CreationFusion/images/output/segmented.png";
//        int frameNumber = 1; // Adjust as needed
//        String experimentName = "Experiment"; // Adjust as needed
//
//        // Perform segmentation using Segmentation class
//        try {
//            Segmentation snapCells = new Segmentation(frameNumber, experimentName, inputFile, true, 30);
//
//            // Create colored cells image and save as PNG
//            DrawSegmentation.createColoredCellsImage(snapCells, new File(outputFile));
//            System.out.println("Segmented cells image saved successfully: " + outputFile);
//        } catch (IOException e) {
//            System.err.println("Error processing image or saving segmented cells: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
