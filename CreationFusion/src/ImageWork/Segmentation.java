package ImageWork;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a set of cell directors extracted from a frame of microscopy
 * images. Each cell is represented as a SnapCell containing its image and
 * location. Uses OpenCV for image processing.
 *
 * @author E. Dov Neimand
 */
public class Segmentation extends HashSet<SnapCell> {

    private final int frameNumber;
    private final String experimentName;
    private final boolean preprocessed; // Flag to indicate if image has been preprocessed
    private final int estimatedCellSize; // Estimated size of each cell

    /**
     * Constructs a CellsFromFrame object for a specific frame and experiment,
     * processing the provided TIFF file to extract individual cells.
     *
     * @param frameNumber The frame number of the microscopy image.
     * @param experimentName The name of the experiment.
     * @param filePath The TIFF file containing the microscopy images.
     * @param preprocessed Flag indicating if the TIFF image has already been
     * preprocessed.
     * @param estimatedCellSize The estimated size of each cell for extraction.
     */
    public Segmentation(int frameNumber, String experimentName, String filePath, boolean preprocessed, int estimatedCellSize) {
        this.frameNumber = frameNumber;
        this.experimentName = experimentName;
        this.preprocessed = preprocessed;
        this.estimatedCellSize = estimatedCellSize;


        Mat processedImage = Imgcodecs.imread(filePath);
        if (processedImage == null || processedImage.empty()) {
            throw new IllegalArgumentException("Failed to load TIFF image: " + filePath);
        }

        if(!preprocessed) processedImage = preprocessImage(processedImage);
        
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(processedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);

            // Ensure the bounding box is large enough to be considered a cell
            if (boundingRect.width >= estimatedCellSize && boundingRect.height >= estimatedCellSize) {
                // Extract cell region from the original image
                Mat cellImage = new Mat(processedImage, boundingRect);
                Point cellCenter = new Point(boundingRect.x + boundingRect.width / 2.0,
                        boundingRect.y + boundingRect.height / 2.0);
                add(new SnapCell(cellImage, cellCenter));
            }
        }
        
    }


    /**
     * Preprocesses the TIFF image to enhance cell detection.
     *
     * @param tiffImage The TIFF image containing multiple cells.
     * @return The preprocessed image as a Mat object (matrix).
     */
    private Mat preprocessImage(Mat tiffImage) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(tiffImage, grayImage, Imgproc.COLOR_BGR2GRAY); // Convert to grayscale

        // Apply adaptive thresholding to segment cells from background
        Mat binaryImage = new Mat();
        Imgproc.adaptiveThreshold(grayImage, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

        // Optionally, apply morphology operations or other filters as needed
        // Example: Uncomment to apply morphology operations
        // Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        // Imgproc.morphologyEx(binaryImage, binaryImage, Imgproc.MORPH_CLOSE, kernel);
        return binaryImage;
    }

}
