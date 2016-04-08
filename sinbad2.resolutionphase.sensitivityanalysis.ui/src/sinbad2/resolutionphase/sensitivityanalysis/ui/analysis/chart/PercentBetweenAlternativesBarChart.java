package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart;

import java.awt.Color;
import java.awt.GradientPaint;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

public class PercentBetweenAlternativesBarChart {
	
	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	private DefaultCategoryDataset _dataset;
	private int[] _currentAlternativesPair;
	private double[] _percents;
	
	public PercentBetweenAlternativesBarChart() {
		_chart = null;
		_chartComposite = null;
		
		_currentAlternativesPair = null;
		_percents = new double[0];
	}
	
	public double[] getPercents() {
		return _percents;
	}

	public void setPercents(double[] percents) {
		_percents = percents;
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

		_percents = percents;
	}

	public void refreshChart() {
		if (_chart == null) {
			_chart = createChart(createDataset());
		} else {
			_chart.getCategoryPlot().setDataset(createDataset());
		}
	}

	private JFreeChart createChart(DefaultCategoryDataset dataset) {
		JFreeChart result = ChartFactory.createBarChart3D("", "", "Percent", dataset, PlotOrientation.HORIZONTAL, false, true, false);
		
		result.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
	    CategoryPlot plot = result.getCategoryPlot();
	    plot.getRangeAxis().setRange(0d, 100d);
	    plot.getRangeAxis().setUpperBound(100);
	    plot.getRangeAxis().setLowerBound(-100);
	    
		return result;
	}

	private DefaultCategoryDataset createDataset() {
		if(_currentAlternativesPair != null) {
			_chart.setTitle("A" + Integer.toString(_currentAlternativesPair[0] + 1).toUpperCase() + " - " + "A" + Integer.toString(_currentAlternativesPair[1] + 1).toUpperCase());
		}
		
		_dataset = new DefaultCategoryDataset();
		
		for(int i = 0; i < _percents.length; ++i) {
			_dataset.setValue(_percents[i], "criterion", "c" + i);
		}
		
		return _dataset;
	}
}
