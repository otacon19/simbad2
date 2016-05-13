package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.resolutionphase.sensitivityanalysis.ui.nls.Messages;

public class SturdinessMeasureStackedChart {
	
	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	private DefaultCategoryDataset _dataset;
	
	private Map<Criterion, Map<Alternative, Double>> _data;

	public SturdinessMeasureStackedChart() {
		_chart = null;
		_chartComposite = null;
	}

	public ChartComposite getChartComposite() {
		return _chartComposite;
	}

	public void initialize(Composite container, int width, int height, int style, Map<Criterion, Map<Alternative, Double>> data) {
		_data = data;
		
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

	private JFreeChart createChart(DefaultCategoryDataset dataset) {
		JFreeChart result = ChartFactory.createStackedBarChart(Messages.SturdinessMeasureStackedChart_Sturdiness_criteria, null, null, dataset, PlotOrientation.HORIZONTAL, true, true, false);

		result.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 1000, 0, Color.blue));
		CategoryPlot plot = result.getCategoryPlot();
		plot.setRangeCrosshairVisible(false);
		plot.getDomainAxis().setVisible(true);
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setVisible(false);

		BarRenderer br = (BarRenderer) plot.getRenderer();
		br.setMaximumBarWidth(.15);
		br.setBaseItemLabelsVisible(true);

		return result;
	}

	private DefaultCategoryDataset createDataset() {
		_dataset = new DefaultCategoryDataset();

		for(Criterion c: _data.keySet()) {
			Map<Alternative, Double> minimunAlternative = _data.get(c);
			for(Alternative a: minimunAlternative.keySet()) {
				_dataset.setValue(minimunAlternative.get(a), a, c);
			}	
		}
		
		return _dataset;
	}
}

