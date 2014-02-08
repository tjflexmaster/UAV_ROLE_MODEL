package simulator.metrics;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.text.NumberFormat;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MetricDisplayPanel extends JPanel implements ChangeListener
{
  /** A scrollbar to update the dataset value. */
  JScrollBar scroller;
  
  JFreeChart _chart;
  
  CategoryDataset _dataset;
  
  public MetricDisplayPanel(String chartName, CategoryDataset dataset)
  {
    _dataset = dataset; //new SlidingCategoryDataset(dataset, 0, 20);
    
    setLayout(new BorderLayout());
    
    //Create chart
    _chart = ChartFactory.createLineChart(
        chartName,      // chart title
        null,                      // x axis label
        "Workload",                      // y axis label
        _dataset,                  // data
        PlotOrientation.VERTICAL,
        true,                     // include legend
        true,                     // tooltips
        false                     // urls
    );
    
    CategoryPlot plot = (CategoryPlot) _chart.getPlot();
    plot.setBackgroundPaint(Color.black);

    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setMaximumCategoryLabelWidthRatio(0.2f);
    domainAxis.setLowerMargin(0.02);
    domainAxis.setUpperMargin(0.02);
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

    // set the range axis to display integers only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis.setAutoRange(true);
    rangeAxis.setAutoRangeMinimumSize(10.0);
    rangeAxis.setRangeType(RangeType.POSITIVE);
    
    _chart.setBackgroundPaint(Color.white);
    
    final LineAndShapeRenderer renderer = 
        (LineAndShapeRenderer) ((CategoryPlot)_chart.getPlot()).getRenderer();
//  renderer.setDrawShapes(true);

//    renderer.setSeriesStroke(
//        0, new BasicStroke(
//            2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
//            1.0f, new float[] {10.0f, 6.0f}, 0.0f
//        )
//    );
    renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(
        "Series {0}, Category {1}, Value {2}", NumberFormat.getInstance()));
    
    
    ChartPanel cp = new ChartPanel(_chart);
    cp.setPreferredSize(new Dimension(1880, 350));
    add(cp);
    
    //Setup scrollbar
    this.scroller = new JScrollBar(SwingConstants.HORIZONTAL);
    this.scroller.setMinimum(0);
    this.scroller.setMaximum(0);
    this.scroller.getModel().addChangeListener(this);
    JPanel scrollPanel = new JPanel(new BorderLayout());
    scrollPanel.add(scroller);
    scrollPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    scrollPanel.setBackground(Color.white);
    add(scrollPanel, BorderLayout.SOUTH);
    
  }
  
  public void setMinimumRangeSize(double val)
  {
    CategoryPlot plot = (CategoryPlot) _chart.getPlot();
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setAutoRangeMinimumSize(val);
  }
  
  public void setCategoryTooltip(String category, String msg)
  {
    CategoryPlot plot = (CategoryPlot) _chart.getPlot();
    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.addCategoryLabelToolTip(category, msg);
  }
  
  @Override
  public void stateChanged(ChangeEvent arg0)
  {
//    _dataset.setFirstCategoryIndex(this.scroller.getValue());
  }

  public void updateScrollBarSize(int increment)
  {
//    int cols = _dataset.getUnderlyingDataset().getColumnCount();
//    int cols = _dataset.getColumnCount();
//    int newMax = Math.max(cols-10, 0);
//    this.scroller.setMaximum(newMax);
//    scroller.setValue(newMax);
  }
}
