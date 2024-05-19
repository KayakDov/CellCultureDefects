package main;

import GeometricTools.Rectangle;
import GeometricTools.OpenSpaceTimeBall;
import ReadWrite.ReadManager;
import defectManagement.DefectManager;

/**
 * Some default data sets.
 *
 * @author E. Dov Neimand
 */
public class DefaultData {

    private static final Rectangle cellWindow = new Rectangle(0, 0, 2050, 2050, 80);
    private static final OpenSpaceTimeBall cellProximity = new OpenSpaceTimeBall(2, 40);

    public static DefectManager Cells_1_10_11_12() {

        return new DefectManager("plusMinusTMs//1_10_11_12", cellWindow, cellProximity, cellProximity.rTime * 2).setName("cells_1_10_11_12");
    }

    public static DefectManager Cells_14_15_19() {
        return new DefectManager("plusMinusTMs//14_15_19", cellWindow, cellProximity, cellProximity.rTime * 2).setName("cells_14_15_19");
    }

    /**
     * The bacteria data set
     *
     * @param time How close must pair events be in time?
     * @param dist How close must pairs be at their creation 
     * @return The bacteria data set.
     */
    public static DefectManager bacteria(int time, double dist) {

        final Rectangle defaultBacteriaDimensions = new Rectangle(900, 0, 900, 900, 20);
        final OpenSpaceTimeBall defaultProximity = new OpenSpaceTimeBall(time, dist);

        return new DefectManager(
                ReadManager.defaultFileFormat("PlusAndMinusTM6.csv"),
                defaultBacteriaDimensions,
                defaultProximity,
                defaultProximity.rTime * 2
        ).setName("Bacteria: time = " + time + " dist = " + dist);
    }

    public static DefectManager bacteria() {
        return bacteria(2, 18);
    }
}
