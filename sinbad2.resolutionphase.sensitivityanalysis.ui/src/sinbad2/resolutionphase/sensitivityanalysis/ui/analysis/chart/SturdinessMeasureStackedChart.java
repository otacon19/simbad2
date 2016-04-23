package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart;

import java.awt.Color;
import java.awt.GradientPaint;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;

public class SturdinessMeasureStackedChart {
	
	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	private CategoryDataset _dataset;
	
	private ProblemElementsSet _elementsSet;

	public SturdinessMeasureStackedChart() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();

		_chart = null;
		_chartComposite = null;
	}

	public ChartComposite getChartComposite() {
		return _chartComposite;
	}

	public void initialize(Composite container, int width, int height, int style) {

		refreshChart();

		_chartComposite = new ChartComposite(container, style, _chart, true);
		_chartComposite.setSize(width, height);
	}

	public void refreshChart() {
		if (_chart == null) {
			_chart = createChart(createDataset());
		} else {
			_chart.getCategoryPlot().setDataset(createDataset());
		}
	}

	private JFreeChart createChart(CategoryDataset dataset) {
		JFreeChart result = ChartFactory.createStackedBarChart(null, null, null, dataset, PlotOrientation.HORIZONTAL, true, true, false);

		result.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
		CategoryPlot plot = result.getCategoryPlot();
		plot.setRangeCrosshairVisible(false);
		plot.getDomainAxis().setVisible(true);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setVisible(false);

		BarRenderer br = (BarRenderer) plot.getRenderer();
		br.setMaximumBarWidth(.15);
		br.setBaseItemLabelsVisible(true);

		return result;
	}

	private CategoryDataset createDataset() {
		_dataset = new DefaultCategoryDataset();

		
		return _dataset;
	}
}

