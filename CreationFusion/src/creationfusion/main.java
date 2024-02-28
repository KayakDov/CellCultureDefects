package creationfusion;

import StatisticalTools.StandardDeviation;
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
        
        dm.frameStream().parallel().forEach(frame -> System.out.println(frame.time + ": " + frame.charge()));

    }

}
