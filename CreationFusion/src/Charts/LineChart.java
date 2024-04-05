package Charts;

import GeometricTools.Vec;
import dataTools.StdDev;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.DoubleStream;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;



public class LineChart extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title the frame title.
     * @param xAxis The name of the x axis.
     * @param yAxis The name of the y axis.
     * @param dataSet The data set.
     */
    public LineChart(String title, String xAxis, String yAxis, XYDataset dataSet) {
        super("Plot");
        JPanel chartPanel = new ChartPanel(createChart(title, xAxis, yAxis, dataSet));
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }


    /**
     * Creates a chart.
     *
     * @param dataset the data for the chart.
     * @return a chart.
     */
    private static JFreeChart createChart(String title, String xAxName, String yAxName, XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,      // chart title
                xAxName,                      // x axis label
                yAxName,                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        DeviationRenderer renderer = new DeviationRenderer(true, false);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesFillPaint(0, new Color(255, 200, 200)); // Series 1 - blue shade
        renderer.setSeriesFillPaint(1, new Color(200, 200, 255)); // Series 2 - red shade
        plot.setRenderer(renderer);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setAutoRangeIncludesZero(false);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }

    
    /**
     * An unlabeled data set.
     * @param data 
     */
    public static void factory(XYDataset data){
        factory("", "x", "y", data);
    }

    /**
     * Creates and displays a line chart.
     * @param title The title of the chart.
     * @param xAxis The name of the x axis.
     * @param yAxis The name of the y axis.
     * @param data The data.
     */
    public static void factory(String title, String xAxis, String yAxis, XYDataset data){
        LineChart demo = new LineChart(title, xAxis, yAxis, data);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
    
    
    /**
     * Creates a sample data set.
     *
     * @return a sample data set.
     */
    private static XYDataset createDataset() {
        YIntervalSeries series1 = new YIntervalSeries("Series 1");
        YIntervalSeries series2 = new YIntervalSeries("Series 2");

        double y1 = 100.0;
        double y2 = 100.0;
        for (int i = 0; i <= 100; i++) {
            y1 = y1 + Math.random() - 0.48;
            double dev1 = (0.05 * i);
            series1.add(i, y1, y1 - dev1, y1 + dev1);

            y2 = y2 + Math.random() - 0.50;
            double dev2 = (0.07 * i);
            series2.add(i, y2, y2 - dev2, y2 + dev2);
        }

        YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);

        return dataset;
    }
    
    /**
     * Creates and displays a line chart.
     * @param title The title of the chart.
     * @param xAxis The name of the x axis.
     * @param yAxis The name of the y axis.
     * @param data The data.
     */
    public static void factory(String title, String xAxis, String yAxis, NamedData... data){
        YIntervalSeries[] series = new YIntervalSeries[data.length];
        Arrays.setAll(series, i -> new YIntervalSeries(data[i].name));
        
        for(int i = 0; i < data.length; i++){
            HashMap<Double, List<Double>> map = new HashMap<>(data[i].size());
            data[i].forEach(datum -> {
                if(!map.containsKey(datum.getX()))
                    map.put(datum.getX(), new LinkedList<>());
                map.get(datum.getX()).add(datum.getY());
            });
            final int j = i;
            map.forEach((x, yList) -> {
                StdDev stdDev = new StdDev() {
                    @Override
                    public DoubleStream data() {
                        return yList.stream().mapToDouble(d ->d);
                    }
                };
                double avgY = stdDev.getAverage(), dev = stdDev.compute();
                series[j].add(x, avgY, avgY - dev, avgY + dev);
            });
            
        }
        
        
        YIntervalSeriesCollection dataSet = new YIntervalSeriesCollection();
        Arrays.stream(series).forEach(s -> dataSet.addSeries(s));
        
        factory(title, xAxis, yAxis, dataSet);
    }
    
    
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        factory("title", "x", "y", createDataset());
    }
}
