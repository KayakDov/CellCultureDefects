package creationfusion;

import StatisticalTools.StandardDeviation;
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
        DefectManager dm = new DefectManager("PlusAndMinusTM.csv");
        
        dm.pairDefects();
        dm.setDistances(20);
        
        //TODO: The set distance function looks like it's getting a lot of bad values.
        dm.all().forEach(defect -> System.out.println(Arrays.toString(Arrays.copyOf(defect.distanceFrom(true), 6))));

    }

}
