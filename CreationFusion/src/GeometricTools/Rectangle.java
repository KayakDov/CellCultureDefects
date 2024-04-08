package GeometricTools;

/**
 * A rectangle in the coordinate plane.
 * @author E. Dov Neimand
 */
public class Rectangle {
    public final double x, y, width, height, nearEdge;

    /**
     * The dimensions of the rectangle.
     * @param x The beginning of the x-axis interval.
     * @param y The beginning of they-axis interval. 
     * @param width The length of the x-axis interval.
     * @param height The length of the y-axis interval.
     * @param nearEdge A distance from the edge that's considered nearby.
     */
    public Rectangle(double x, double y, double width, double height, double nearEdge) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nearEdge = nearEdge;
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
        return x > this.x && 
                x < this.x + width && 
                y > this.y && 
                y < this.y + height;
    }
    
    /**
     * Is the proffered point inside the rectangle and near the edge?
     * If the point is outside the rectangle then the behaivure is undefined.
     * @param vec A point inside the rectangle that might or might not be near the edge.
     * @return True if vec is near the edge, false otherwise.
     */
    public boolean nearEdge(Vec vec){
        return vec.getX() < x + nearEdge || vec.getX() > x + width - nearEdge ||
                vec.getY()< y + nearEdge || vec.getY() > y + height - nearEdge;
    }
}
