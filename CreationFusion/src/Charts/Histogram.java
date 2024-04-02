package Charts;

import java.util.Arrays;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import javax.swing.*;

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
     */
    public Histogram(String title, double[] data, String xAxisTitle, int numBins) {
        super(title);

        // Create a dataset for the histogram
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(title, data, numBins);

        // Create a histogram chart
        JFreeChart chart = ChartFactory.createHistogram(
                title,                          // chart title
                xAxisTitle,                           // x-axis label
                "Frequency",                         // y-axis label
                dataset,                             // data
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // plot orientation
                true,                                // include legend
                false,                              // tooltips (not used here)
                false);                              // urls (not used here)


        // Display the chart in a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
            Histogram histogram = new Histogram("sampleChart", data, xAxisTitle, numBins);
            histogram.setSize(800, 600);
            histogram.setLocationRelativeTo(null);
            histogram.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            histogram.setVisible(true);
        });
    }
}
