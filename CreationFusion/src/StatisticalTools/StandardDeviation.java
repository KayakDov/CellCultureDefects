package StatisticalTools;

import java.util.concurrent.RecursiveTask;
import java.util.stream.DoubleStream;

/**
 * Is meant to be used to calculate the standard deviation of a dataset.
 * @author E. Dov Neimand
 */
public abstract class StandardDeviation extends RecursiveTask<Double>{
    
    private boolean averageIsKnown = false;
    private double average;

    public StandardDeviation() {
    }

    /**
     * The average of the data set.
     * @param average 
     */
    public StandardDeviation(double average) {
        setAverage(average);
    }

    
    
    /**
     * Sets the average of the data set.
     * @param average The average of the data set.
     */
    final public double setAverage(double average) {
        this.average = average;
        averageIsKnown = true;
        return average;
    }

    /**
     * The average of the data set.  If one is not saved, then it is computed.
     * @return The average of the data set.
     */
    public double getAverage() {
        if(averageIsKnown) return average;
        
        else return setAverage(data().average().getAsDouble());
    }
    
    
    /**
     * Generates the data over which the average standard deviation is desired.
     * @return 
     */
    abstract public DoubleStream data();
    
    @Override
    protected Double compute() {
        double avg = getAverage();
        return Math.sqrt(data().map(d -> (d - avg)*(d-avg)).average().getAsDouble());
    }
    
}
