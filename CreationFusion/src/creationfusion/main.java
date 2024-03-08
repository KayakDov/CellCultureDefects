package creationfusion;

import GeometricTools.Rectangle;
import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 *
 * @author E. Dov Neimand
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
        DefectManager dm = new DefectManager(
                "PlusAndMinusTM.csv", 
                FileFormat.DEFAULT.setWindow(new Rectangle(900, 0, 1800, 900))
        );
        
        dm.loadPairs();
        dm.loadLifeCourses();
        
//        System.out.println("DM Status:" + dm.positives().count());
  

        
        
        dm.all().filter(defect -> defect.hasTwin() && !defect.spouseIsTwin())
                .forEach(defect -> System.out.println(
                        Arrays.toString(
                                Arrays.copyOf(
                                        defect.defectPairs(DefectManager.BIRTH), 
                                        Math.min(defect.defectPairs(DefectManager.BIRTH).length, 6))
                        )
                    )
                );

    }

}
