package main;

import GeometricTools.Rectangle;
import GeometricTools.ProximityMetric;
import ReadWrite.ReadManager;
import defectManagement.DefectManager;
import java.io.File;

/**
 * Some default data sets.
 *
 * @author E. Dov Neimand
 */
public class DefaultData {

    private static final Rectangle cellWindow = new Rectangle(0, 0, 2050, 2050, 80);
    private static final ProximityMetric cellProximity = new ProximityMetric(40, 2);

    public static DefectManager cells_1_10_11_12() {

        return new DefectManager("plusMinusTMs//1_10_11_12", cellWindow, cellProximity, cellProximity.rTime * 2).setName("cells_1_10_11_12");
    }

    public static DefectManager cells_14_15_19() {
        return new DefectManager("plusMinusTMs//14_15_19", cellWindow, cellProximity, cellProximity.rTime * 2).setName("cells_14_15_19");
    }
    
    public static DefectManager cells_HBEC_s2() {
        return new DefectManager("plusMinusTMs//HBEC", cellWindow, cellProximity, cellProximity.rTime * 2).setName("HBEC");
    }
    
    public static DefectManager allCells_1_10_11_12_14_15_19() {
        return cells_1_10_11_12().mergeIn(cells_14_15_19()).setName("Cells");
    }
    

    /**
     * The bacteria data set
     *
     * @param time How close must pair events be in time?
     * @param dist How close must pairs be at their creation 
     * @param distFromEdge The distance from the edge a creation of fusion event needs to be to be registered.
     * @param timeFromEdge
     * @return The bacteria data set.
     */
    public static DefectManager bacteria(int time, double dist, double distFromEdge, int timeFromEdge) {

        final Rectangle defaultBacteriaDimensions = new Rectangle(900, 0, 900, 900, distFromEdge);
        final ProximityMetric defaultProximity = new ProximityMetric(dist, time);

        return new DefectManager(
                ReadManager.defaultFileFormat("PlusAndMinusTM6.csv"),
                defaultBacteriaDimensions,
                defaultProximity,
                timeFromEdge
        ).setName("Bacteria: time = " + time + " dist = " + dist + " boundary area = " + distFromEdge + " time from Edge " + timeFromEdge);
    }

    /**
     * The bacteria data set with default distances of 18 and time of 2.
     * @return The bacteria data set.
     */
    public static DefectManager bacteria() {
        return bacteria(2, 12, 30, 2);
    }
    
    /**
     * A sample data set.
     * @return A sample data set.
     */
    public static DefectManager sampleDataSet(){
        return new DefectManager(
                ReadManager.defaultFileFormat("SampleDataSet.csv"), 
                new Rectangle(0, 0, 10, 10, 0), 
                new ProximityMetric(2, 1), 
                0                
        ).setName("sample data set");
    }    
}
