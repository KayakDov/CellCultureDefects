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

/**
 * A class for generating and displaying heat map charts.
 */
public class HeatMap extends ApplicationFrame {

    /**
     * Creates a new heat map.
     *
     * @param title      the frame title.
     * @param xAxisLabel the label for the x-axis.
     * @param yAxisLabel the label for the y-axis.
     * @param data       the list of data points.
     * @param radius   the size of each grid cell.
     * @param res The square root of the number of pixels.
     * @param xMod A value if the x axis is modular, and Double.NaN if it's not.
     * @param yMod Lke xMod, but for the y axis.
     */
    public HeatMap(String title, String xAxisLabel, String yAxisLabel, List<Vec> data, int radius, int res, double xMod, double yMod) {
        super(title);
        final JFreeChart chart = createChart(createDataset(data, res, res, radius, xMod, yMod), xAxisLabel, yAxisLabel);
        setContentPane(new ChartPanel(chart));
    }

    /**
     * Creates the heatmap chart.
     *
     * @param dataset    the dataset for the heatmap.
     * @param xAxisLabel the label for the x-axis.
     * @param yAxisLabel the label for the y-axis.
     * @return the heatmap chart.
     */
    private static JFreeChart createChart(XYZDataset dataset, String xAxisLabel, String yAxisLabel) {
        double[] zValues = extractZValues(dataset); // Get the intensity values from the dataset
        NumberAxis xAxis = new NumberAxis(xAxisLabel),
                   yAxis = new NumberAxis(yAxisLabel);

        // Create a paint-scale and a legend showing it
        LookupPaintScale paintScale = createPaintScale(zValues);
        PaintScaleLegend psl = createPaintScaleLegend(paintScale);

        // Create a renderer and a plot
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, new XYBlockRenderer());
        ((XYBlockRenderer) plot.getRenderer()).setPaintScale(paintScale);

        // Create the chart
        JFreeChart chart = new JFreeChart(null, null, plot, false);
        chart.addSubtitle(psl);
        return chart;
    }

    /**
     * Extracts the intensity values from the dataset.
     *
     * @param dataset the dataset for the heatmap.
     * @return the intensity values.
     */
    private static double[] extractZValues(XYZDataset dataset) {
        int seriesCount = dataset.getSeriesCount();
        int itemCount = dataset.getItemCount(0);
        double[] zValues = new double[itemCount];

        for (int i = 0; i < itemCount; i++) {
            zValues[i] = dataset.getZValue(0, i);
        }

        return zValues;
    }

    /**
     * Creates the paint scale for the plot.
     *
     * @param zValues the intensity values.
     * @return the paint scale.
     */
    private static LookupPaintScale createPaintScale(double[] zValues) {
        double minZ = Arrays.stream(zValues).min().orElse(0);
        double maxZ = Arrays.stream(zValues).max().orElse(0);
        
        if(minZ >= maxZ) throw new RuntimeException("minZ = " + minZ + " and maxZ = " + maxZ);

        LookupPaintScale paintScale = new LookupPaintScale(minZ, maxZ, Color.black);
        Color c = Color.green;
        paintScale.add(0.0, c);
        paintScale.add(33.0, c = c.darker());
        paintScale.add(66.0, c.darker());
        paintScale.add(100.0, c = Color.blue);
        paintScale.add(133.0, c = c.darker());
        paintScale.add(166.0, c.darker());
        paintScale.add(200.0, c = Color.red.darker().darker());
        paintScale.add(233.0, c = c.brighter());
        paintScale.add(266.0, c.brighter());
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
     * The x values.
     * @param data The source.
     * @return The x values.
     */
    private static DoubleStream get(List<Vec> data, boolean isX){
        return data.stream().mapToDouble(datum -> isX?datum.getX():datum.getY());
    }
    
    
    
    /**
     * Constructs a heat field. The number of points is xRes * yRes.
     * @param reigon
     * @param xRes The x resolution.  Big number is higher resolution.
     * @param yRes The y resolution.  Big number is higher resolution.
     * @param data The data used to generate the field.
     * @param r The heat value of a point is the number of data within this radius from the point.
     * @param xMod The modularity of the x axis.
     * @param yMod The modularity of the y axis.
     * @return The heat field.
     */
    private static double[][] heatField(GeometricTools.Rectangle reigon, int xRes, int yRes, List<Vec> data, double r, double xMod, double yMod) {

        double[][] x = new double[3][xRes*yRes];
                
        double dx = reigon.width/xRes, dy = reigon.height/yRes;

        Arrays.setAll(x[0], i -> (reigon.x + (i%yRes) * dx));
        Arrays.setAll(x[1], i -> reigon.y + (i/xRes) * dy);
        Arrays.setAll(x[2], i -> data.stream().filter(vec -> new Vec(x[0][i], x[1][i]).dist(vec, xMod, yMod) < r).count());
        
        
        return x;
    }
    
    /**
     * The region that the data is in.
     * @param data The data in the region.
     * @param xMod If the x axis is modular, if it's not pass Double.NaN.
     * @param yMod If the y axis is modular, if it's not pass Double.NaN.
     * @return The region the data is in.
     */
    private static GeometricTools.Rectangle region(List<Vec> data, double xMod, double yMod){
        double xMin, xMax, yMin, yMax;
        
        if(Double.isFinite(xMod)){
            xMin = 0; 
            xMax = xMod;
        }else{
            xMin = get(data, true).min().getAsDouble();
            xMax = get(data, true).max().getAsDouble();
        }
        
        if(Double.isFinite(yMod)){
            yMin = 0; 
            yMax = yMod;
        }else{
            yMin = get(data, false).min().getAsDouble();
            yMax = get(data, false).max().getAsDouble();
        }       
        
        return new GeometricTools.Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
    }
    
    /**
     * Creates the dataset for the heatmap.
     *
     * @param data     the list of data points.
     * @param xRes The number of cells on the x dimension.
     * @param yRes The number of cells on the y dimension.
     * @param xMod The modularity of the x axis.
     * @param yMod The modularity of the y axis.
     * @param radius The heat of a point is the sum of data points within the radius.
     * @return the dataset for the heatmap.
     */
    public static XYZDataset createDataset(List<Vec> data, int xRes, int yRes, double xMod, double yMod, double radius) {

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        dataset.addSeries("", heatField(region(data, xMod, yMod), xRes, yRes, data, radius, xMod, yMod));
        return dataset;
    }

    
    /**
     * Factory method to create a HeatMap instance.
     *
     * @param title      the frame title.
     * @param xAxisLabel the label for the x-axis.
     * @param yAxisLabel the label for the y-axis.
     * @param data       the list of data points.
     * @param res The number of pixels
     * @param r   the size of each grid cell.
     */
    public static void factory(String title, String xAxisLabel, String yAxisLabel, List<Vec> data, int res, int r){
        factory(title, xAxisLabel, yAxisLabel, data, res, r, Double.NaN, Double.NaN);
    }
    /**
     * Factory method to create a HeatMap instance.
     *
     * @param title      the frame title.
     * @param xAxisLabel the label for the x-axis.
     * @param yAxisLabel the label for the y-axis.
     * @param data       the list of data points.
     * @param res The resolution. Higher number is more detail.
     * @param r   the size of each grid cell.
     * @param xMod The modularity of the x axis.
     * @param yMod The modularity of the y axis.
     */
    public static void factory(String title, String xAxisLabel, String yAxisLabel, List<Vec> data, int res, int r, double xMod, double yMod) {
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
