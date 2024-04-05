package Charts;

import GeometricTools.Vec;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ScatterPlot extends ApplicationFrame {

    /**
     * Creates a new scatter plot.
     *
     * @param title the frame title.
     * @param xAxis The name of the x axis.
     * @param yAxis The name of the y axis.
     * @param data The data sets.
     * @param colors The colors for each data set.
     */
    public ScatterPlot(String title, String xAxis, String yAxis, List<XYDataset> data, List<Color> colors) {
        super(title);
        JPanel chartPanel = new ChartPanel(createChart(title, xAxis, yAxis, data, colors));
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    /**
     * Creates a scatter plot chart.
     *
     * @param title the chart title.
     * @param xAxisLabel the x axis label.
     * @param yAxisLabel the y axis label.
     * @param datasets the data sets.
     * @param colors the colors for each data set.
     * @return the scatter plot chart.
     */
    private JFreeChart createChart(String title, String xAxisLabel, String yAxisLabel,
            List<XYDataset> datasets, List<Color> colors) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                title, // chart title
                xAxisLabel, // x axis label
                yAxisLabel, // y axis label
                null, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        XYPlot plot = (XYPlot) chart.getPlot();

        for (int i = 0; i < datasets.size(); i++) {
            plot.setDataset(i, datasets.get(i));
            plot.setRenderer(i, new org.jfree.chart.renderer.xy.XYLineAndShapeRenderer(false, true));
            ((XYLineAndShapeRenderer) plot.getRenderer(i)).setSeriesPaint(0, colors.get(i));
            ((XYLineAndShapeRenderer) plot.getRenderer(i))
                    .setSeriesShape(
                            0, 
                            new java.awt.geom.Ellipse2D.Double(-1.0, -1.0, 1.0, 1.0)
                    ); // Adjust the shape size here
        }

        return chart;
    }

    /**
     * Creates and displays a scatter plot.
     *
     * @param title the frame title.
     * @param xAxis The name of the x axis.
     * @param yAxis The name of the y axis.
     * @param data The data sets.
     * @param colors The colors for each data set.
     */
    public static void createAndShowScatterPlot(String title, String xAxis, String yAxis, List<XYDataset> data, List<Color> colors) {
        ScatterPlot scatterPlot = new ScatterPlot(title, xAxis, yAxis, data, colors);
        scatterPlot.pack();
        RefineryUtilities.centerFrameOnScreen(scatterPlot);
        scatterPlot.setVisible(true);
    }

    /**
     * Creates a scatter plot.
     *
     * @param title The title of the plot.
     * @param xAxis The title of the x axis.
     * @param yAxis The title of the y axis.
     * @param data The data.
     */
    public static void factory(String title, String xAxis, String yAxis, NamedData... data) {
        List<XYDataset> dxyds = new ArrayList<>(data.length);

        for (NamedData nd : data) {
            DefaultXYDataset dSet = new DefaultXYDataset();
            double[] x = nd.data.stream().mapToDouble(vec -> vec.getX()).toArray();
            double[] y = nd.stream().mapToDouble(vec -> vec.getY()).toArray();
            dSet.addSeries(nd.name, new double[][]{x, y});
            dxyds.add(dSet);
        }

        List<Color> colors
                = IntStream.range(0, data.length)
                        .mapToObj(i -> Color.getHSBColor((float) i / data.length, 1, 1))
                        .collect(Collectors.toList());

        ScatterPlot.createAndShowScatterPlot("Scatter Plot", "X", "Y", dxyds, colors);
    }

    public static void main(String[] args) {
        // Example usage:
        List<Vec> data1 = Arrays.asList(new Vec(1, 2), new Vec(3, 4), new Vec(5, 6));
        List<Vec> data2 = Arrays.asList(new Vec(2, 3), new Vec(4, 5), new Vec(6, 7));

        factory("sample", "x", "y", new NamedData(data1, "data1"), new NamedData(data2, "data2"));

    }
}
