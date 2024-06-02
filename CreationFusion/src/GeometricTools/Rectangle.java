package GeometricTools;

/**
 * A rectangle in the coordinate plane.
 * @author E. Dov Neimand
 */
public class Rectangle {
    public final double width, height, nearEdge;
    public final Vec minCorner;

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
        this.minCorner = loc;
        this.width = width;
        this.height = height;
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
        return x > this.minCorner.getX() && 
                x < this.minCorner.getX() + width && 
                y > this.minCorner.getY() && 
                y < this.minCorner.getY() + height;
    }
    
    /**
     * Is the proffered point inside the rectangle and near the edge?
     * If the point is outside the rectangle then the behaivure is undefined.
     * @param vec A point inside the rectangle that might or might not be near the edge.
     * @return True if vec is near the edge, false otherwise.
     */
    public boolean nearEdge(Vec vec){
        return vec.getX() < getX() + nearEdge || vec.getX() > getY() + width - nearEdge ||
                vec.getY()<getY() + nearEdge || vec.getY() > getY() + height - nearEdge;
    }
    
    public double getX(){
        return minCorner.getX();
    }
    
    public double getY(){
        return minCorner.getY();
    }
}
