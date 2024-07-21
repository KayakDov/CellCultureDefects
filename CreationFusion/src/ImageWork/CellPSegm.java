package ImageWork;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * runs cell segmentation on a picture of cells, and processes the segmented picture into SnapCells.
 * @author edov
 */
public class CellPSegm {


    public static int min_num_cell_pixels = 5; //Cells with a size smaller than or equal to 5 will not be recognized.

    /**
     * Uploads the cells from a segmented picture.
     * @param inputImagePath  A segmented picture of cells.
     * @return 
     */
    public static Set<SnapCell> fromSegmented(String inputImagePath) {
        try {
            
            return fromSegmentedImage(ImageIO.read(new File(inputImagePath)).getRaster());

        } catch (IOException ex) {
            Logger.getLogger(CellPSegm.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Constructor for fromSegmented.
     *
     * @param pythonLoc The location of python on the computer.
     * @param pythonScriptPath the path to the Python script
     * @param inputImagePath the path to the input image
     * @param tempSegmentedImageStorage the path to the output image
     */
    public static Set<SnapCell> fromRaw(String pythonLoc, String pythonScriptPath, String inputImagePath, String tempSegmentedImageStorage) {
        
            runPythonProcess(pythonLoc, pythonScriptPath, inputImagePath, tempSegmentedImageStorage);

            return fromSegmented(tempSegmentedImageStorage);

    }

    /**
     * Created individual cells from an image where each cell has it's own
     * unique color.
     *
     * @param raster
     * @return A collection of cells
     */
    private static Set<SnapCell> fromSegmentedImage(WritableRaster raster) {
                
        return Pixel.grid(raster.getWidth(), raster.getHeight())
                .filter(vec -> SnapCell.color(raster, vec) != 0)
                .map(vec -> new SnapCell(vec, SnapCell.color(raster, vec), raster))
                .filter(cell -> cell.size() > min_num_cell_pixels)
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
            Logger.getLogger(CellPSegm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static final String pythonLoc = "/home/edov/miniconda3/envs/cellpose/bin/python",
            pythonScriptPath = "../Vision/cellpose_segment.py",
            inputImagePath = "../../VictorData/HBEC/s2(120-919)/Trans__042.tif",
            tempSegmentedStorage = "images/output/segmented.png";    
    
    /**
     * Creates a cellposeSegmentation with some default values.
     * @param cellPicturePath The path to a pictures of cells.
     * @return a cellposeSegmentation with some default values.
     */
    public static Set<SnapCell> defaultFromRaw(String cellPicturePath){
        return fromRaw(pythonLoc, pythonScriptPath, cellPicturePath, tempSegmentedStorage);
    }
    
    
}






//
//    /**
//     * Prints a picture of each cell to the designated folder.
//     *
//     * @param folder The folder to contain all the new images.
//     * @param width The width of the new images
//     * @param height The height of the new images.
//     */
//    final public void printCells(String folder, int width, int height) {
//
//        File directory = new File(folder);
//        if (!directory.exists()) directory.mkdirs();
//
//        cells.forEach(cell -> {
//            try {
//                ImageIO.write(
//                        cell.image(width, height),
//                        "png",
//                        new File(folder + File.separator + "cell_" + cell.color + ".png")
//                );
//            } catch (IOException ex) {
//                Logger.getLogger(CellPSegm.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        });
//    }

    