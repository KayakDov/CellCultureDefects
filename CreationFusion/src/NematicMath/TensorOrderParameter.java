package NematicMath;

import GeometricTools.Vec;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Computes the tensor order parameter Q for a system of nematic directors.
 */
public class TensorOrderParameter {
//
//    private NematicDirector[] directors;  // Array of nematic directors
//
//    /**
//     * Constructor to initialize with an array of NematicDirector objects.
//     * @param directors Array of NematicDirector objects.
//     */
//    public TensorOrderParameter(NematicDirector[] directors) {
//        this.directors = directors;
//    }
//
//    /**
//     * Computes the tensor order parameter Q at a given position r.
//     * @param r Position vector where Q is computed.
//     * @return Tensor order parameter Q as a 2x2 matrix.
//     */
//    public RealMatrix nematicVector(double[] r) {
//        final int d = 2; // Dimensionality (2 for 2D)
//        RealMatrix Q = MatrixUtils.createRealMatrix(d, d); // Initialize Q matrix
//
//        // Compute total weight for normalization
//        double totalWeight = 0.0;
//
//        // Iterate over each NematicDirector
//        for (NematicDirector director : directors) {
//            Vec r_i = director.r; // Position vector of the director
//
//            // Compute weight based on proximity using Dirac delta function approximation
//            double weight = deltaFunction(r, r_i);
//
//            // Direction vector nu from the angle theta
//            Vec nu = director.theta.vec();
//
//            RealMatrix tensor = MatrixUtils.createRealMatrix(new double[][] {
//                { nu.getX() * nu.getX() - 1.0 / d, nu.getX() * nu.getY() },
//                { nu.getX() * nu.getY(), nu.getY() * nu.getY() - 1.0 / d }
//            });
//
//            // Multiply tensor by weight and add the contribution to Q
//            Q = Q.add(tensor.scalarMultiply(weight));
//
//            // Accumulate total weight
//            totalWeight += weight;
//        }
//
//        // Normalize Q by the total weight
//        if (totalWeight > 0.0) {
//            Q = Q.scalarMultiply(1.0 / totalWeight);
//        }
//
//        return Q;
//    }
//
//    /**
//     * Dirac delta function approximation for weighted contribution.
//     * @param r Position vector.
//     * @param r_i Position vector to compare against.
//     * @return Weighted contribution based on proximity.
//     */
//    private double deltaFunction(Vec r, Vec r_i) {
//        // Adjust the tolerance based on your application's spatial resolution
//        double tolerance = 1e-3;
//
//        // Use a Gaussian or similar function for weighted proximity
//        double weight = Math.exp(-r.dist(r_i) / tolerance);
//
//        return weight;
//    }
}
