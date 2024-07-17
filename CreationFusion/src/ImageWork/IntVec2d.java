package ImageWork;

import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author E. Dov Neimand
 */
public class IntVec2d {

    public final int x, y;

    /**
     * Constructor
     *
     * @param x The x coordinate.
     * @param y The y coordinate
     */
    public IntVec2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Is the vector inside a rectangle centered at the origin with the given
     * height and width.
     *
     * @param height The height of the rectangle.
     * @param width The width of the rectangle.
     * @return
     */
    public boolean inside(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * The adjacent intVect2ds.
     *
     * @return The adjacent intVect2ds.
     */
    public Stream<IntVec2d> adjacent() {
        return Stream.of(
                new IntVec2d(x - 1, y),
                new IntVec2d(x + 1, y),
                new IntVec2d(x, y - 1),
                new IntVec2d(x, y + 1),
                new IntVec2d(x + 1, y + 1),
                new IntVec2d(x - 1, y + 1),
                new IntVec2d(x + 1, y - 1),
                new IntVec2d(x - 1, y - 1)
                );
    }

    /**
     * Are the two points equal.
     *
     * @param vec the other point.
     * @return true if they are equal, false otherwise.
     */
    public boolean equals(IntVec2d vec) {
        return vec.x == x && vec.y == y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final IntVec2d other = (IntVec2d) obj;
        if (this.x != other.x) return false;
        return this.y == other.y;
    }

    /**
     * A grid of points, starting at the origin.
     * @param width The maximum x value, exclusive.
     * @param height The maximum y value, exclusive.
     * @return All the points between (0,0) and (width, height)
     */
    public static Stream<IntVec2d> grid(int width, int height){
        return IntStream.range(0, width).boxed().flatMap(x -> IntStream.range(0, height).mapToObj(y -> new IntVec2d(x, y)));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    
}
