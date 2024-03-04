package GeometricTools;

/**
 * A rectangle in the coordinate plane.
 * @author E. Dov Neimand
 */
public class Rectangle {
    private double x, y, width, height;

    /**
     * The dimensions of the rectangle.
     * @param x The beginning of the x-axis interval.
     * @param y The beginning of they-axis interval. 
     * @param width The length of the x-axis interval.
     * @param height The length of the y-axis interval.
     */
    public Rectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    
    /**
     * The dimensions of the rectangle.
     * @param loc The corner nearest the origin.
     * @param width The length of the x-axis interval.
     * @param height The length of the y-axis interval.
     */
    public Rectangle(Loc loc, double width, double height) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.width = width;
        this.height = height;
    }
    
    /**
     * Does this rectangle contain the proffered point?
     * @param loc The point to check for membership in the rectangle.
     * @return True if the point is in the rectangle, false otherwise.
     */
    public boolean contains(Loc loc){
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
     * A rectangle representing the entire coordinate plane.
     */
    public final static Rectangle COORD_PLANE = new Rectangle(
            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY){
              
        @Override
        public boolean contains(Loc loc) {
            return true;
        }
        
    };
}
