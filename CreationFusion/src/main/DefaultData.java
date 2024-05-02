package main;

import GeometricTools.Rectangle;
import GeometricTools.SpaceTimeBall;
import ReadWrite.ReadManager;
import defectManagement.DefectManager;
import java.io.File;
import java.util.Arrays;

/**
 * Some default data sets.
 * @author E. Dov Neimand
 */
public class DefaultData {
    
    private static final Rectangle cellWindow = new Rectangle(0, 0, 2050, 2050, 80);
    private static final SpaceTimeBall cellProximity = new SpaceTimeBall(2, 40);
    
    
    public static DefectManager Cells_1_10_11_12(){
        
        return new DefectManager("plusMinusTMs//1_10_11_12", cellWindow, cellProximity, cellProximity.rTime*2).setName("cells_1_10_11_12");
    }
    public static DefectManager Cells_14_15_19(){
        return new DefectManager("plusMinusTMs//14_15_19", cellWindow, cellProximity, cellProximity.rTime*2).setName("cells_14_15_19");
    }
    
    
    public static DefectManager bacteria(){
        
        final Rectangle defaultBacteriaDimensions = new Rectangle(900, 0, 900, 900, 20);
        final SpaceTimeBall defaultProximity = new SpaceTimeBall(2, 15);
        
        return new DefectManager(
                ReadManager.defaultFileFormat("PlusAndMinusTM6.csv"),
                defaultBacteriaDimensions,
                defaultProximity, 
                defaultProximity.rTime*2
        ).setName("bacteria");
    }
}
