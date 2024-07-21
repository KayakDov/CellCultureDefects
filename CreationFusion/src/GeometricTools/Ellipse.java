package GeometricTools;

/**
 * Represents an ellipse in the coordinate plane.
 */
public class Ellipse {
    
    private final Vec center;
    private final double a;
    private final double b;
    private final Angle rotationAngle; // Angle in radians
    
    /**
     * Constructs a new Ellipse instance with the specified parameters.
     * 
     * @param center The center of the ellipse.
     * @param semiMajorAxis The length of the semi-major axis.
     * @param semiMinorAxis The length of the semi-minor axis.
     * @param rotationAngle The rotation angle of the ellipse.
     */
    public Ellipse(Vec center, double semiMajorAxis, double semiMinorAxis, Angle rotationAngle) {
        this.center = center;
        this.a = semiMajorAxis;
        this.b = semiMinorAxis;
        this.rotationAngle = rotationAngle;
    }
    
    /**
     * Gets the center of the ellipse.
     * 
     * @return The center of the ellipse.
     */
    public Vec getCenter() {
        return center;
    }
    
    /**
     * Gets the length of the semi-major axis of the ellipse.
     * 
     * @return The length of the semi-major axis.
     */
    public double getSemiMajorAxis() {
        return a;
    }
    
    /**
     * Gets the length of the semi-minor axis of the ellipse.
     * 
     * @return The length of the semi-minor axis.
     */
    public double getSemiMinorAxis() {
        return b;
    }
    
    /**
     * Gets the rotation angle of the ellipse.
     * 
     * @return The rotation angle of the ellipse.
     */
    public Angle getRotationAngle() {
        return rotationAngle;
    }
    
    /**
     * Computes the area of the ellipse.
     * 
     * @return The area of the ellipse.
     */
    public double computeArea() {
        return Math.PI * a * b;
    }
    
    /**
     * Computes the perimeter (circumference) of the ellipse
     * using Ramanujan's approximation formula.
     * 
     * @return The perimeter (circumference) of the ellipse.
     */
    public double perimeterLength() {
        double h = Math.pow(a - b, 2) / Math.pow(a + b, 2);
        return Math.PI * (a + b) * (1 + (3 * h) / (10 + Math.sqrt(4 - 3 * h)));
    }
    
    /**
     * Checks if a point is inside the ellipse.
     * 
     * @param point The point to check.
     * @return true if the point is inside the ellipse, false otherwise.
     */
    public boolean contains(Vec point) {
        Vec diff = point.minus(center).rotate(rotationAngle.mult(-1));
        return Math.pow(diff.getX() / a, 2) + Math.pow(diff.getY() / b, 2) <= 1;
    }
    
    /**
     * Checks if two ellipses are equal.
     * 
     * @param obj The object to compare.
     * @return true if the ellipses are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Ellipse)) {
            return false;
        }
        Ellipse other = (Ellipse) obj;
        return center.equals(other.center)
            && a == other.a
            && b == other.b
            && rotationAngle.equals(other.rotationAngle);
    }
    
    /**
     * Returns a string representation of the ellipse.
     * 
     * @return A string representation of the ellipse.
     */
    @Override
    public String toString() {
        return "Ellipse[center=" + center + ", semiMajorAxis=" + a +
                ", semiMinorAxis=" + b + ", rotationAngle=" + rotationAngle + "]";
    }
    
    /**
     * Computes a point on the ellipse edge corresponding to the given angle.
     *
     * @param angle The angle around the ellipse where the point is desired.
     * @return The point on the ellipse edge corresponding to the given angle.
     */
    public Vec pointOnEdge(Angle angle) {

        Vec preRotation = new Vec(
                a * angle.cos(), 
                b * angle.sin()
        );
        
        preRotation = preRotation.rotate(rotationAngle);

        return preRotation.plus(center);
    }
    
    
    public static void main(String[] args) {
        
        Ellipse elli = new Ellipse(new Vec(0, 0), 2, 1, new Angle(0));
        for(double rad = 0; rad < Math.PI * 2; rad += .1){
            Vec edgePoint = elli.pointOnEdge(new Angle(rad));
            System.out.println(
                    Math.pow(edgePoint.getX()/elli.a,2) + Math.pow(edgePoint.getY()/elli.b, 2)
            );
        }

        

//        System.out.println(elli.pointOnEdge(new Angle(Math.PI)));
    }
}
