package ImageWork;

import ImageWork.Cell;
import ImageWork.IntVec2d;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * CellposeSegmentation class for running CellPose segmentation using a Python
 * script and loading the pixel values of the segmented image into a 2D integer
 * array.
 */
public class CellposeSegmentation {

    private Collection<Cell> cells;

    public CellposeSegmentation(String inputImagePath, String outputImagePath) {
        try {
            BufferedImage segmentedImage = ImageIO.read(new File(inputImagePath));
            cells = fromSegmentedImage(segmentedImage.getRaster());
            printCells(outputImagePath, segmentedImage.getWidth(), segmentedImage.getHeight());
        } catch (IOException ex) {
            Logger.getLogger(CellposeSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Constructor for CellposeSegmentation.
     *
     * @param pythonLoc The location of python on the computer.
     * @param pythonScriptPath the path to the Python script
     * @param inputImagePath the path to the input image
     * @param outputImagePath the path to the output image
     */
    public CellposeSegmentation(String pythonLoc, String pythonScriptPath, String inputImagePath, String outputImagePath) {
        try {
            runPythonProcess(pythonLoc, pythonScriptPath, inputImagePath, outputImagePath);

            BufferedImage image = ImageIO.read(new File(outputImagePath));

            if (image == null) {
                System.err.println("Failed to load image from " + outputImagePath);
                return;
            }

            cells = fromSegmentedImage(image.getRaster());

            printCells(outputImagePath.replace(".png", "/"), image.getWidth(), image.getHeight());

        } catch (IOException ex) {
            Logger.getLogger(CellposeSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Prints a picture of each cell to the designated folder.
     *
     * @param folder The folder to contain all the new images.
     * @param width The width of the new images
     * @param height The height of the new images.
     */
    final public void printCells(String folder, int width, int height) {

        File directory = new File(folder);
        if (!directory.exists()) directory.mkdirs();

        cells.forEach(cell -> {
            try {
                ImageIO.write(
                        cell.image(width, height),
                        "png",
                        new File(folder + File.separator + "cell_" + cell.color + ".png")
                );
            } catch (IOException ex) {
                Logger.getLogger(CellposeSegmentation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    
    /**
     * Created individual cells from an image where each cell has it's own
     * unique color.
     *
     * @param raster
     * @return A collection of cells
     */
    private static Set<Cell> fromSegmentedImage(WritableRaster raster) {
                
        return IntVec2d.grid(raster.getWidth(), raster.getHeight())
                .filter(vec -> Cell.color(raster, vec) != 0)
                .map(vec -> new Cell(vec, Cell.color(raster, vec), raster))
                .filter(cell -> !cell.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * Runs the CellPose segmentation using the specified Python script.
     *
     * @param python The path to python
     * @param pythonScriptPath the path to the Python script
     * @param args additional arguments to the Python script (e.g., input and
     * output paths)
     */
    public static void runPythonProcess(String python, String pythonScriptPath, String... args) {
        
        String[] totArgs = new String[2 + args.length];
        totArgs[0] = python;
        totArgs[1] = pythonScriptPath;
        System.arraycopy(args, 0, totArgs, 2, args.length);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(totArgs);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script exited with code " + exitCode);
            }

        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(CellposeSegmentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Main method for testing the CellPoseSegmentation functionality.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        if (args.length != 3) {
//            System.out.println("Usage: java CellposeSegmentation <pythonScriptPath> <inputImagePath> <outputImagePath>");
//            System.exit(1);
//        }
        String pythonLoc = "/home/edov/miniconda3/envs/cellpose/bin/python";
        String pythonScriptPath = "../Vision/cellpose_segment.py";
        String inputImagePath = "../../VictorData/HBEC/s2(120-919)/Trans__042.tif";
        String outputImagePath = "images/output/segmented.png";

        new CellposeSegmentation(outputImagePath, outputImagePath.replace(".png", "/"));
    }
}
