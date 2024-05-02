package Charts;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for generating and displaying bar charts.
 */
public class BarChart extends ApplicationFrame {

    /**
     * Creates a new bar chart.
     *
     * @param title             the chart title.
     * @param categoryAxisLabel the label for the category axis.
     * @param valueAxisLabel    the label for the value axis.
     */
    public BarChart(String title, String categoryAxisLabel, String valueAxisLabel, DefaultCategoryDataset dataset) {
        super(title);

        JFreeChart barChart = createChart(dataset, title, categoryAxisLabel, valueAxisLabel);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    /**
     * Factory method to create and display a bar chart.
     *
     * @param title             the chart title.
     * @param xAxisLabel the label for the category axis.
     * @param yAxisLabel    the label for the value axis.
     * @param categories        the list of categories and their values.
     */
    public static <T extends Comparable<T>> void factory(String title, String xAxisLabel, String yAxisLabel, List<Map.Entry<T, Double>> categories) {
        
        DefaultCategoryDataset dcds = new DefaultCategoryDataset();
        categories.forEach(cat -> dcds.addValue(cat.getValue(),"", cat.getKey()));
        
        
        BarChart barChart = new BarChart(title, xAxisLabel, yAxisLabel, dcds);
        barChart.pack();
        RefineryUtilities.centerFrameOnScreen(barChart);
        barChart.setVisible(true);
    }

    
    /**
     * Factory method to create and display a bar chart.
     *
     * @param title             the chart title.
     * @param categoryAxisLabel the label for the category axis.
     * @param valueAxisLabel    the label for the value axis.
     * @param categories        the list of categories and their values.
     */
    public static <T extends Comparable<T>> void factory(String title, String categoryAxisLabel, String valueAxisLabel, Map<T, Double> categories) {
        factory(title, categoryAxisLabel, valueAxisLabel, mapToSortedList(categories));
    }
    

    /**
     * Creates a bar chart.
     *
     * @param dataset           the dataset.
     * @param title             the chart title.
     * @param categoryAxisLabel the label for the category axis.
     * @param valueAxisLabel    the label for the value axis.
     * @return the bar chart.
     */
    private JFreeChart createChart(DefaultCategoryDataset dataset, String title, String categoryAxisLabel, String valueAxisLabel) {
        JFreeChart chart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.white);

        return chart;
    }
    
    /**
     * Sorts map entries into a string.
     * @param <T>
     * @param map The map to be sorted
     * @return A string of the map's sorted entries.
     */
    public static <T extends Comparable<T>> List<Map.Entry<T, Double>> mapToSortedList(Map<T, Double> map){
        List<Map.Entry<T, Double>> sortedList = new ArrayList<>(map.entrySet());
        Collections.sort(sortedList, Comparator.comparing(entry -> entry.getKey()));
        return sortedList;
    }

    /**
     * Main method to start the bar chart demo.
     *
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        HashMap<Double, Double> categories = new HashMap<>();
        categories.put(1.3, 10.0);
        categories.put(1.8, 20.0);
        categories.put(0.34, 30.0);
        
        factory("Bar Chart Example", "Category", "Value", categories);
    }
}
