package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.TextAnchor;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;

public class MinimunValueBetweenAlternativesBarChart {
	
	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	private DefaultCategoryDataset _dataset;
	private int[] _currentAlternativesPair;
	private double[] _values;
	
	private ProblemElementsSet _elementsSet;
	
	public MinimunValueBetweenAlternativesBarChart() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_chart = null;
		_chartComposite = null;
		
		_currentAlternativesPair = null;
		_values = new double[0];
	}
	
	public double[] getValues() {
		return _values;
	}

	public void setValues(double[] values) {
		_values = values;
	}
	
	public int[] getCurrentAlternativesPair() {
		return _currentAlternativesPair;
	}
	
	public void setCurrentAlternativesPair(int[] currentAlternativesPair) {
		_currentAlternativesPair = currentAlternativesPair;
	}
	
	public ChartComposite getChartComposite() {
		return _chartComposite;
	}

	public void initialize(Composite container, int width, int height, int style, double[] percents) {
		
		refreshChart();
		
		_chartComposite = new ChartComposite(container, style, _chart, true);
		_chartComposite.setSize(width, height);

		_values = percents;
	}

	public void refreshChart() {
		if (_chart == null) {
			_chart = createChart(createDataset());
		} else {
			_chart.getCategoryPlot().setDataset(createDataset());
		}
	}

	@SuppressWarnings("serial")
	private JFreeChart createChart(DefaultCategoryDataset dataset) {
		JFreeChart result = ChartFactory.createBarChart(null, null, "Percent", dataset, PlotOrientation.HORIZONTAL, false, true, false);
		
		result.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
	    CategoryPlot plot = result.getCategoryPlot();
	    plot.getRangeAxis().setRange(0d, 100d);
	    plot.getRangeAxis().setUpperBound(100);
	    plot.getRangeAxis().setLowerBound(-100);
	    plot.setRangeCrosshairVisible(true);
	    plot.getDomainAxis().setVisible(false);
	    plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setUpperMargin(0.15);
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setVisible(false);
             
        BarRenderer br = (BarRenderer) plot.getRenderer();
	    br.setMaximumBarWidth(.35);
        br.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator() {
        	@Override
        	public String generateLabel(CategoryDataset dataset, int row, int column) {
        		return dataset.getRowKey(row).toString();
        	}
        });
        
        br.setBaseItemLabelsVisible(true);
        br.setSeriesItemLabelFont(0, new java.awt.Font("SansSerif", Font.PLAIN, 12));
        br.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.BOTTOM_LEFT, TextAnchor.BOTTOM_LEFT, 0));
	  
		return result;
	}

	private DefaultCategoryDataset createDataset() {
		String a1 = "", a2 = "";
		if(_currentAlternativesPair != null) {
			a1 = _elementsSet.getAlternatives().get(_currentAlternativesPair[0]).getId();
			a2 = _elementsSet.getAlternatives().get(_currentAlternativesPair[1]).getId();
			_chart.setTitle("Minimun percent " + a1 + " - " + a2);
		}
		
		_dataset = new DefaultCategoryDataset();
		
		List<Criterion> criteria = _elementsSet.getAllCriteria();
		for(int i = 0; i < _values.length; ++i) {
			if(_values[i] != 0) {
				_dataset.setValue(_values[i], criteria.get(i), a1 + " - " + a2);
			}
		}
		
		return _dataset;
	}
}
