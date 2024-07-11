package Charts;

import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import javax.swing.*;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * A class for creating a histogram chart using JFreeChart library.
 */
public class Histogram extends JFrame {

    /**
     * Constructs a histogram chart.
     * @param title The title of the chart.
     * @param data The array of values for which the histogram is to be generated.
     * @param xAxisTitle The title for the x-axis.
     * @param numBins The number of bins (segments) in the histogram.
     * @param overlay A function to be drawn on top of the histogram.
     */
    public Histogram(String title, double[] data, String xAxisTitle, int numBins, DoubleUnaryOperator overlay) {
        super(title);

        // Create a dataset for the histogram
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(title, data, numBins);

        // Create a histogram chart
        JFreeChart chart = ChartFactory.createHistogram(
                title,                          // chart title
                xAxisTitle,                           // x-axis label
                "Count",                         // y-axis label
                dataset,                             // data
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // plot orientation
                true,                                // include legend
                false,                              // tooltips (not used here)
                false);                              // urls (not used here)
        
        if(overlay != null) 
            overlayFunction(
                chart, 
                Arrays.stream(data).min().getAsDouble(), 
                Arrays.stream(data).max().getAsDouble(), 
                overlay, 
                numBins*2
            );

        // Display the chart in a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
    
    /**
     * Overlays a function on the given histogram chart.
     * @param chart The histogram chart to overlay the function on.
     * @param data The array of values for which the histogram was generated.
     * @param overlay The function to be drawn on top of the histogram.
     * @param steps The number of steps of the overlay function.
     */
    private void overlayFunction(JFreeChart chart, double min, double max, DoubleUnaryOperator overlay, int increments) {
        XYSeries series = new XYSeries("Overlay");

        double step = (double)(max - min)/ increments;        
        
        for (double x = min; x <= max; x += step) series.add(x, overlay.applyAsDouble(x));
        

        XYSeriesCollection functionDataset = new XYSeriesCollection();
        functionDataset.addSeries(series);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDataset(1, functionDataset);

        // Create a renderer for the overlay dataset
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(1, renderer);

        // Ensure the overlay is rendered after the histogram
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    }
    
    /**
     * Puts a histogram on the screen.
     * @param data The values in the histogram.
     * @param numBins The number of bins.
     * @param title The title.
     * @param xAxis The x axis label.
     * @param overlay A function that overlays the chart.
     */
    public static void factory(double[] data, int numBins, String title, String xAxis, DoubleUnaryOperator overlay){
        SwingUtilities.invokeLater(() -> {
            Histogram histogram = new Histogram(title, data, xAxis, numBins, overlay);
            histogram.setSize(600, 400);
            histogram.setLocationRelativeTo(null);
            histogram.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            histogram.setVisible(true);
        });
    }
    
    
    /**
     * Puts a histogram on the screen.
     * @param data The values in the histogram.
     * @param numBins The number of bins.
     * @param title The title.
     * @param xAxis The x axis label.
     */
    public static void factory(double[] data, int numBins, String title, String xAxis){
        factory(data, numBins, title, xAxis, null);
    }
    
    
    /**
     * Main method for testing the Histogram class.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Example usage
        double[] data = new double[1000];
        
        Random rand = new Random();
        
        Arrays.setAll(data, i -> rand.nextGaussian());
        
        String xAxisTitle = "Value";
        
        int numBins = 50;
        
        SwingUtilities.invokeLater(() -> {
            Histogram histogram = new Histogram("sampleChart", data, xAxisTitle, numBins, null);
            histogram.setSize(800, 600);
            histogram.setLocationRelativeTo(null);
            histogram.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            histogram.setVisible(true);
        });
    }
}
