package ImageWork;

import GeometricTools.LineSegment;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of cell outlines extracted from a frame of microscopy
 * images. Each cell is represented as a MatOfPoint containing its outline points.
 * Uses OpenCV for image processing.
 *
 * @author E. Dov Neimand
 */
public class Segmentation extends ArrayList<MatOfPoint> {

    private final int frameNumber;
    private final String experimentName;
    private Mat source;

    /**
     * Constructs a Segmentation object for a specific frame and experiment,
     * processing the provided TIFF file to extract individual cell outlines.
     *
     * @param frameNumber     The frame number of the microscopy image.
     * @param experimentName The name of the experiment.
     * @param filePath       The TIFF file containing the microscopy images.
     * @param preprocessed   Flag indicating if the TIFF image has already been
     *                       preprocessed.
     * @param cellSize       The estimated size of each cell for extraction.
     */
    public Segmentation(int frameNumber, String experimentName, String filePath, boolean preprocessed, LineSegment cellSize) {
        this.frameNumber = frameNumber;
        this.experimentName = experimentName;
        source = Imgcodecs.imread(filePath);

        if (source == null || source.empty())
            throw new IllegalArgumentException("Failed to load TIFF image: " + filePath);

        // Convert to grayscale if necessary
        if (source.channels() > 1)
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);

        // Ensure the image is binary
        if (!isBinary(source))
            source = preprocessImage(source);

        // Find contours
        Imgproc.findContours(source, this, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Filter contours based on cell size
        removeIf(contour -> {
            Rect boundingRect = Imgproc.boundingRect(contour);
            return !cellSize.contains(boundingRect.width) || !cellSize.contains(boundingRect.height);
        });
    }

    /**
     * Checks if the given image matrix is binary.
     *
     * @param image The image matrix to check.
     * @return True if the image is binary, false otherwise.
     */
    private boolean isBinary(Mat image) {
        return image.type() == CvType.CV_8UC1; // Check if image is single-channel (grayscale)
    }

    /**
     * Preprocesses the TIFF image to enhance cell detection.
     *
     * @param tiffImage The TIFF image containing multiple cells.
     * @return The preprocessed image as a Mat object (matrix).
     */
    private Mat preprocessImage(Mat tiffImage) {
        Mat binaryImage = new Mat();

        // Convert to binary using adaptive thresholding
        Imgproc.adaptiveThreshold(tiffImage, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

        // Optionally, apply morphology operations or other filters as needed
        // Example: Uncomment to apply morphology operations
        // Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        // Imgproc.morphologyEx(binaryImage, binaryImage, Imgproc.MORPH_CLOSE, kernel);

        return binaryImage;
    }

    /**
     * Creates an image with each cell outlined in a different color and saves it to a file.
     *
     * @param outputFileName The name of the file to save the image to.
     */
    public void createColoredCellsImage(String outputFileName) {
        // Create a new Mat to draw the colored cells on
        Mat coloredImage = new Mat(source.size(), CvType.CV_8UC3, new Scalar(255, 255, 255)); // White background

        // Draw each contour (cell outline) with a random color
        for (int i = 0; i < this.size(); i++) {
            Scalar color = new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255); // Random color
            Imgproc.drawContours(coloredImage, this, i, color, -1); // Fill contour with color
        }

        // Save the image to file
        Imgcodecs.imwrite(outputFileName, coloredImage);
    }

    /**
     * Returns the file that was segmented.
     *
     * @return The file that was segmented.
     */
    public Mat getSource() {
        return source;
    }

    /**
     * Returns the frame number of the microscopy image.
     *
     * @return The frame number.
     */
    public int getFrameNumber() {
        return frameNumber;
    }

    /**
     * Returns the name of the experiment.
     *
     * @return The experiment name.
     */
    public String getExperimentName() {
        return experimentName;
    }

    /**
     * Main method to demonstrate segmentation and image creation.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Load OpenCV native library
        System.load("/home/edov/projects/CreationFusionCount/Vision/opencv/build/lib/libopencv_java4100.so");

        // Input parameters
        int frameNumber = 1; // Adjust frame number as needed
        String experimentName = "Experiment"; // Adjust experiment name as needed
        String filePath = "/home/edov/projects/VictorData/HBEC/s2(120-919)/Trans__042.tif";
        String outputFileName = "/home/edov/projects/CreationFusionCount/CreationFusion/images/output/segmented.png";

        // Parameters for cell size estimation
        LineSegment cellSize = new LineSegment(10, 100); // Adjust min and max cell size as needed

        // Perform segmentation
        Segmentation segmentation = new Segmentation(frameNumber, experimentName, filePath, false, cellSize);

        // Create colored image with cell outlines
        segmentation.createColoredCellsImage(outputFileName);

        // Optional: Display message on successful completion
        System.out.println("Segmentation complete. Image saved to: " + outputFileName);
    }
}
