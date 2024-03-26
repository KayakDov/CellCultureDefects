package dataTools;

import java.util.concurrent.RecursiveTask;

/**
 * Find the maximum element in a unimodal array.
 *
 * @author E. Dov Neimand
 */
public class UnimodalArrayMax extends RecursiveTask<Integer> {

    private double[] arr;
    
    
    
    /**
     * The golden raitio.
     */
    public final static double phi = (1 + Math.sqrt(5)) / 2,
            r = phi - 1,
            c = 1 - r;

    /**
     * The constructor.
     * @param array A unimodal array.
     */
    public UnimodalArrayMax(double... array) {
        this.arr = array;
    }

    /**
     * Computes the left probe.
     * @param outLeft The minimal argument for the segment.
     * @param inRight The greater argument probe.
     * @param outRight The maximum argument for the segment.
     * @return The minimal argument probe.
     */
    private static int leftProbe(int outLeft, int inRight, int outRight) {
        return (int)Math.round(r * inRight + c * outLeft);

    }

    /**
     * Computes the right probe.
     * @param outLeft The minimum argument of the segment for which a minimum is sought.
     * @param inLeft The minimum probe for the segment.
     * @param outRight The maximum argument of the segment for which a minimum is sought.
     * @return the maximum probe for the segment.
     */
    public static int rightProbe(int outLeft, int inLeft, int outRight) {
        return (int) Math.round(r * inLeft + c * outRight);
    }

    /**
     * Finds the max element in the segment.
     * @param outLeft The minimum argument of the segment.
     * @param inLeft The minimum probe for the segment.
     * @param inRight The maximum probe for the segement.
     * @param outRight The maximum argument for the segment.
     * @return Th argmax of the segment.
     */
    public static int argMax(int outLeft, int inLeft, int inRight, int outRight, double[] array) {

        if (inRight == inLeft)
            return array[inLeft] > array[inRight] ? inLeft : inRight;
        
        

        return array[inLeft] < array[inRight]
                ? argMax(inLeft, inRight, rightProbe(inLeft, inRight, outRight), outRight, array) //bring left side int
                : argMax(outLeft, leftProbe(outLeft, inLeft, inRight), inLeft, inRight, array); //bring right side in

    }
    
    /**
     * Finds the argMax of a unimodal array.  That is, the index of the maximum 
     * value of the array.
     * @param array The unimodal array.
     * @return The argm max of the array. 
     */
    public static int argMax(double[] array){
                
        if (array.length > 1) {
            if (array[0] > array[1]) return 0;
            if (array[array.length - 1] > array[array.length - 2])
                return array.length - 1;
        } else return 0;

        int n = array.length;

        return argMax(
                0, 
                (int) (n - n / phi), 
                (int) (n / phi), 
                n - 1, 
                array
        );
    }

    @Override
    public Integer compute() {
        return argMax(arr);
    }

    /**
     * Tests the class.
     * @param args Not used.
     */
    public static void main(String[] args) {
        System.out.println(new UnimodalArrayMax(1, 2, 3, 4, 20_000, -10).compute());
    }
}
