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
    public static void main(String[] args) throws InterruptedException {
        DefectManager dm = new DefectManager(
                "PlusAndMinusTM.csv", 
                FileFormat.DEFAULT.setWindow(new Rectangle(900, 1800, 0, 900))
        );
        
        dm.loadLifeCourses();
        
        
        dm.all().filter(defect -> defect.hasTwin() && !defect.hasSpouse())
                .forEach(defect -> System.out.println(Arrays.toString(Arrays.copyOf(defect.distances(true), Math.min(defect.distances(true).length, 6)))));

    }

}
