package Charts;

import GeometricTools.Vec;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * A class for generating and displaying heat map charts.
 */
public class HeatMap extends ApplicationFrame {

    /**
 * Creates a new heat map.
 *
 * @param title     the frame title.
 * @param xAxisLabel the label for the x-axis.
 * @param yAxisLabel the label for the y-axis.
 * @param data      the list of data points.
 * @param radius    the size of each grid cell.
 * @param res       The square root of the number of pixels.
 * @param xMod      A value if the x axis is modular, and Double.NaN if it's not.
 * @param yMod      Like xMod, but for the y axis.
 */
public HeatMap(String title, String xAxisLabel, String yAxisLabel, List<Vec> data, double radius, int res, double xMod, double yMod) {
    super(title);

    DefaultXYZDataset dataSet = new DefaultXYZDataset();

    double[][] values = heatField(region(data, xMod, yMod), res, res, data, radius, xMod, yMod);

    dataSet.addSeries("", values);

    NumberAxis xAxis = new NumberAxis(xAxisLabel),
            yAxis = new NumberAxis(yAxisLabel);

    // Adjust the range of the axes
    double xMin = values[0][0];
    double xMax = values[0][values[0].length - 1];
    double yMin = values[1][0];
    double yMax = values[1][values[1].length - 1];
    xAxis.setRange(xMin, xMax);
    yAxis.setRange(yMin, yMax);

    LookupPaintScale paintScale = createPaintScale(values[2]);

    XYBlockRenderer colorAssigner = new XYBlockRenderer();
    colorAssigner.setPaintScale(paintScale);
    XYPlot plot = new XYPlot(dataSet, xAxis, yAxis, colorAssigner);

    JFreeChart chart = new JFreeChart(plot);
    chart.addSubtitle(createPaintScaleLegend(paintScale));

    ChartPanel cp = new ChartPanel(chart);

    XYBlockRenderer blocks = colorAssigner;
    blocks.setBlockWidth(0.5 / res);
    blocks.setBlockHeight(0.5 / res);

    setContentPane(cp);
}


    /**
 * Creates the paint scale for the plot, adjusting the range to go from 0 to maxZ.
 *
 * @param zValues the intensity values.
 * @return the paint scale.
 */
private static LookupPaintScale createPaintScale(double[] zValues) {
    double minZ = Arrays.stream(zValues).min().orElse(0);
    double maxZ = Arrays.stream(zValues).max().orElse(0);

    if (minZ >= maxZ)
        throw new RuntimeException("minZ = " + minZ + " and maxZ = " + maxZ);

    LookupPaintScale paintScale = new LookupPaintScale(0, maxZ, Color.black);
    Color c = Color.white;
    paintScale.add(0.0, c);
    paintScale.add(1, c = Color.green);
    paintScale.add(maxZ * 0.125, c = c.darker());
    paintScale.add(maxZ * 0.25, c.darker());
    paintScale.add(maxZ * 0.375, c = Color.blue);
    paintScale.add(maxZ * 0.5, c = c.darker());
    paintScale.add(maxZ * 0.625, c.darker());
    paintScale.add(maxZ * 0.75, c = Color.red.darker().darker());
    paintScale.add(maxZ * 0.875, c = c.brighter());
    paintScale.add(maxZ, c.brighter());
    return paintScale;
}


    /**
     * Creates the paint scale legend for the plot.
     *
     * @param paintScale the paint scale.
     * @return the paint scale legend.
     */
    private static PaintScaleLegend createPaintScaleLegend(LookupPaintScale paintScale) {
        PaintScaleLegend psl = new PaintScaleLegend(paintScale, new NumberAxis());
        psl.setPosition(RectangleEdge.RIGHT);
        psl.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
        psl.setMargin(50.0, 20.0, 80.0, 0.0);
        return psl;
    }

    /**
     * The region that the data is in.
     *
     * @param data The data in the region.
     * @param xMod If the x axis is modular, if it's not pass Double.NaN.
     * @param yMod If the y axis is modular, if it's not pass Double.NaN.
     * @return The region the data is in.
     */
    private static GeometricTools.Rectangle region(List<Vec> data, double xMod, double yMod) {
        double xMin, xMax, yMin, yMax;
        
        if (Double.isFinite(xMod)) {
            xMin = 0;
            xMax = xMod;
        } else {
            xMin = get(data, true).min().getAsDouble();
            xMax = get(data, true).max().getAsDouble();
        }

        if (Double.isFinite(yMod)) {
            yMin = 0;
            yMax = yMod;
        } else {
            yMin = get(data, false).min().getAsDouble();
            yMax = get(data, false).max().getAsDouble();
        }

        return new GeometricTools.Rectangle(xMin, yMin, xMax - xMin, yMax - yMin, 0);
    }

    /**
     * The x values.
     *
     * @param data The source.
     * @return The x values.
     */
    private static DoubleStream get(List<Vec> data, boolean isX) {
        return data.stream().mapToDouble(datum -> isX ? datum.getX() : datum.getY());
    }

    /**
     * Constructs a heat field. The number of points is xRes * yRes.
     *
     * @param region The region.
     * @param xRes   The x resolution. Big number is higher resolution.
     * @param yRes   The y resolution. Big number is higher resolution.
     * @param data   The data used to generate the field.
     * @param r      The heat value of a point is the number of data within this
     *               radius from the point.
     * @param xMod   The modularity of the x axis.
     * @param yMod   The modularity of the y axis.
     * @return The heat field.
     */
    private static double[][] heatField(GeometricTools.Rectangle region, int xRes, int yRes, List<Vec> data, double r, double xMod, double yMod) {

        double dx = region.width / xRes;
        double dy = region.height / yRes;

        double x[] = new double[xRes * yRes];
        double y[] = new double[xRes * yRes];
        double z[] = new double[xRes * yRes];

        IntStream.range(0, xRes * yRes).parallel().forEach(i -> {

            x[i] = region.getX() + (i % xRes) * dx;
            y[i] = region.getY() + (i / yRes) * dy;
            z[i] = data.stream()
                    .filter(vec -> new Vec(x[i], y[i]).dist(vec, xMod, yMod) < r)
                    .count();
        });

        return new double[][]{x, y, z};
    }

    /**
     * Factory method to create a HeatMap instance.
     *
     * @param title      the frame title.
     * @param xAxisLabel the label for the x-axis.
     * @param yAxisLabel the label for the y-axis.
     * @param data       the list of data points.
     * @param res        The number of pixels
     * @param r          the size of each grid cell.
     */
    public static void factory(String title, String xAxisLabel, String yAxisLabel, List<Vec> data, int res, int r) {
        factory(title, xAxisLabel, yAxisLabel, data, res, r, Double.NaN, Double.NaN);
    }

    /**
     * Factory method to create a HeatMap instance.
     *
     * @param title      the frame title.
     * @param xAxisLabel the label for the x-axis.
     * @param yAxisLabel the label for the y-axis.
     * @param data       the list of data points.
     * @param res        The resolution. Higher number is more detail.
     * @param r          the size of each grid cell.
     * @param xMod       The modularity of the x axis.
     * @param yMod       The modularity of the y axis.
     */
    public static void factory(String title, String xAxisLabel, String yAxisLabel, List<Vec> data, int res, double r, double xMod, double yMod) {
        HeatMap hm = new HeatMap(title, xAxisLabel, yAxisLabel, data, r, res, xMod, yMod);
        hm.pack();
        hm.setVisible(true);
    }

    /**
     * Main method to start the heatmap demo.
     *
     * @param args the command-line arguments.
     */
    public static void main(String args[]) {
        // Example usage:
        List<Vec> data = Arrays.asList(
                new Vec(1, 2), new Vec(3, 4), new Vec(5, 6),
                new Vec(2, 3), new Vec(4, 5), new Vec(6, 7)
        );
        int gridSize = 3; // Adjust grid size as needed
        String xAxisLabel = "X Axis";
        String yAxisLabel = "Y Axis";
        String title = "Heatmap Example";

        HeatMap.factory(title, xAxisLabel, yAxisLabel, data, 30, 10);
    }
}
