package Charts;

import GeometricTools.Vec;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A simple container for a list and a name of the contents of that list.
 *
 * @author E. Dov Neimand
 */
public class NamedData {

    public final List<Vec> data;
    public final String name;

    /**
     * Constructor.
     *
     * @param data The data.
     * @param name The name of the data set.
     */
    public NamedData(List<Vec> data, String name) {
        this.data = data;
        this.name = name;
    }

    /**
     * performs f on each member of the list.
     *
     * @param f The operation to be performed on each member of the list.
     */
    public void forEach(Consumer<Vec> f) {
        data.forEach(f);
    }

    /**
     * The number of elements in the list.
     *
     * @return The number of elements in the list.
     */
    public int size() {
        return data.size();
    }
    
    /**
     * A stream of data.
     * @return A stream of data.
     */
    public Stream<Vec> stream(){
        return data.stream();
    }

}
