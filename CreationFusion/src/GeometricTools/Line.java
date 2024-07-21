package GeometricTools;

import ImageWork.Pixel;
import java.util.Set;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import snapDefects.SpaceTemp;

/**
 * A line in 2D space.
 *
 * @author E. Dov Neimand
 */
public class Line {

    private double m, b;

    /**
     * The constructor.
     *
     * @param slope m of the line.
     * @param yIntercept The y intercept of the line.
     */
    public Line(double slope, double yIntercept) {
        this.m = slope;
        this.b = yIntercept;
    }


    /**
     * Constructs a Line by fitting a linear regression to a set of points.
     *
     * @param points A set of Vec representing the points to fit the line to.
     */
    public Line(Set<Pixel> points) {
        SimpleRegression regression = new SimpleRegression();
        for (Pixel point : points) 
            regression.addData(point.getX(), point.getY());
        if(!regression.hasIntercept()) throw new RuntimeException("This regression does not have a y intercept.");
        this.m = regression.getSlope();
        this.b = regression.getIntercept();
    }

    /**
     * The nematic nemDirector from the m of the line.
     * @return The nematic nemDirector from the m of the line.
     */
    public NematicDirector nemDir(){
        return new NematicDirector(new Vec(1, m).angle().rad());
    }
    
    public double getSlope() {
        return m;
    }

    public double getYIntercept() {
        return b;
    }
    
    /**
     * The vector that would span this line if the line went through the origin.
     * @return The vector that would span this line if the line went through the origin.
     */
    public Vec span(){
        return new Vec(1, m);
    }

}
