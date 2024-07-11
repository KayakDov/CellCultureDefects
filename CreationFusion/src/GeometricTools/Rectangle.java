package GeometricTools;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A rectangle in the coordinate plane.
 * @author E. Dov Neimand
 */
public class Rectangle {
//    public final double width, height, 
    double nearEdge;
//    public final Vec minCorner;

    private LineSegment xSeg, ySeg;
    /**
     * The dimensions of the rectangle.
     * @param x The beginning of the x-axis interval.
     * @param y The beginning of they-axis interval. 
     * @param width The length of the x-axis interval.
     * @param height The length of the y-axis interval.
     * @param nearEdge A distance from the edge that's considered nearby.
     */
    public Rectangle(double x, double y, double width, double height, double nearEdge) {
        this(new Vec(x, y), width, height, nearEdge);
    }

    /**
     * Constructs a rectangle xSeg x ySeg.
     * @param nearEdge A distance from the edge that's considered nearby.
     * @param xSeg The x interval.
     * @param ySeg The y interval.
     */
    public Rectangle(double nearEdge, LineSegment xSeg, LineSegment ySeg) {
        this.nearEdge = nearEdge;
        this.xSeg = xSeg;
        this.ySeg = ySeg;
    }
    
    
    
    /**
     * The dimensions of the rectangle.
     * @param x The beginning of the x-axis interval.
     * @param y The beginning of they-axis interval. 
     * @param width The length of the x-axis interval.
     * @param height The length of the y-axis interval.
     */
    public Rectangle(double x, double y, double width, double height) {
        this(x, y, width, height, 0);
    }
    
    /**
     * 
     * @param loc
     * @param width
     * @param height
     * @param nearEdge 
     */
    public Rectangle(Vec loc, double width, double height, double nearEdge) {
        
        xSeg = new LineSegment(loc.getX(), loc.getX() + width);
        ySeg = new LineSegment(loc.getY(), loc.getY() + height);
        this.nearEdge = nearEdge;
    }
    
    /**
     * Constructor.
     * 
     * @param loc The location.
     * @param width width
     * @param height height
     */
    public Rectangle(Vec loc, double width, double height) {
        this(loc, width, height, 0);
    }

    /**
     * An empty rectangle.  
     * @param nearEdge What constitutes being near the edge of the rectangle.
     */
    public Rectangle(double nearEdge) {
        xSeg = new LineSegment();
        ySeg = new LineSegment();
        this.nearEdge = nearEdge;
    }
    
    /**
     * Creates the smallest rectangle containing all the proffered points so 
     * that they are not near the edge..
     * @param inside The points in the rectangle.
     * @param nearEdge How far from the edge must the points be.
     */
    public Rectangle(Stream<Vec> inside, double nearEdge){
        this(nearEdge);
        inside.forEach(vec -> expand(vec));
    }
    
    /**
     * Does this rectangle contain the proffered point?
     * @param loc The point to check for membership in the rectangle.
     * @return True if the point is in the rectangle, false otherwise.
     */
    public boolean contains(Vec loc){
        return contains(loc.getX(), loc.getY());
    }
    
    /**
     * Does this rectangle contain the proffered point.
     * @param x The x value of the point.
     * @param y The y value of the point.
     * @return True if the point is in the rectangle, false otherwise.
     */
    public boolean contains(double x, double y){
        return xSeg.contains(x) && ySeg.contains(y);
    }
    
    /**
     * Is the proffered point inside the rectangle and near the edge?
     * If the point is outside the rectangle then the behaivure is undefined.
     * @param vec A point inside the rectangle that might or might not be near the edge.
     * @return True if vec is near the edge, false otherwise.
     */
    public boolean nearEdge(Vec vec){
        return xSeg.nearEdge(vec.getX(), nearEdge) && ySeg.contains(vec.getY())
                || ySeg.nearEdge(vec.getY(), nearEdge) && xSeg.contains(vec.getX());
    }
    
    /**
     * X the value of the corner closest to the origin.
     * @return X the value of the corner closest to the origin.
     */
    public double getX(){
        return xSeg.getMin();
    }
    
    /**
     * The y value of the corner closest to the origin.
     * @return The y value of the corner closest to the origin.
     */
    public double getY(){
        return ySeg.getMin();
    }
    
    /**
     * The height of the rectangle.
     * @return The height of the rectangle.
     */
    public double height(){
        return ySeg.length();
    }
    
    /**
     * The width of the rectangle.
     * @return The width of the rectangle.
     */
    public double width(){
        return xSeg.length();
    }
    
    /**
     * Increases the size of the rectangle so that it includes vec, and so
     * that vec is not near the edge.
     * @param vec 
     */
    public void expand(Vec vec){
        xSeg.expandCirc(vec.getX(), nearEdge);
        ySeg.expandCirc(vec.getY(), nearEdge);
    }
    
    
    
    /**
     * A function that takes in a vector in this rectangle and maps it to a 
     * corresponding location in the target rectangle.
     * @param source A vector in this rectangle.
     * @param target The rectangle to be mapped to.
     * @return A natural linear mapping from this rectangle to the target 
     * rectangle.
     */
    public Vec scale(Vec source, Rectangle target){
        return new Vec(xSeg.scale(source.getX(), target.xSeg), ySeg.scale(source.getY(), target.ySeg));
    }
}
