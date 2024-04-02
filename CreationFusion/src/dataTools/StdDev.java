package dataTools;

import java.util.concurrent.RecursiveTask;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

/**
 * Is meant to be used to calculate the standard deviation of a dataset.
 * @author E. Dov Neimand
 */
public abstract class StdDev extends RecursiveTask<Double>{
    
    private boolean averageIsKnown = false;
    private double average;

    public StdDev() {
    }

    /**
     * The average of the data set.
     * @param average 
     */
    public StdDev(double average) {
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
     * Squares a number.
     * @param d
     * @return 
     */
    public static double sq(double d){
        return d*d;
    }
    
    /**
     * Generates the data over which the average standard deviation is desired.
     * @return 
     */
    abstract public DoubleStream data();
    
    @Override
    public Double compute() {
        double avg = getAverage();
        return Math.sqrt(data().map(d -> sq(d - avg)).average().getAsDouble());
    }
    
    /**
     * A Standard Deviation
     * @param stream Supplies a stream of doubles for whom the standard deviation is desired.
     * @return 
     */
    public static StdDev get(Supplier<DoubleStream> stream){
        return new StdDev() {
            @Override
            public DoubleStream data() {
                return stream.get();
            }
        };
    }
    
}
