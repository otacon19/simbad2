package jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class LinguisticDomainChart extends DomainChart {
	
	private XYSeriesCollection _dataset;
	
	public LinguisticDomainChart() {
		_domain = null;
		_chart = null;
		_chartComposite = null;
	}
	
	@Override
	public void initialize(Domain domain, Composite container, int width, int height, int style) {
		setDomain(domain);
		_chartComposite = new ChartComposite(container, style, _chart, true);
		_chartComposite.setSize(width, height);
	}
	
	@Override
	public void setDomain(Domain domain) {
		_domain = domain;
		refreshChart();
	}

	@Override
	public void refreshChart() {

		if(_chart == null) {
			_chart = createChart(createDataset());
		} else {
			_chart.getXYPlot().setDataset(createDataset());
		}
		
		setBasicRenderer(_chart.getXYPlot());
	}

	@Override
	public void setSelection(Object selection) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		
		for(int i = 0; i < xyplot.getSeriesCount(); ++i) {
			if( i != (Integer) selection) {
				renderer.setSeriesStroke(i, new BasicStroke(1));
			} else {
				renderer.setSeriesStroke(i, new BasicStroke(3));
			}
		}
		
	}
	
	private JFreeChart createChart(XYSeriesCollection dataset) {
		JFreeChart result = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, true, true, false);
		
		result.setBackgroundPaint(Color.WHITE);
		XYPlot xyplot = (XYPlot) result.getPlot();
		xyplot.setBackgroundPaint(Color.WHITE);
		xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		xyplot.getDomainAxis().setRange(0d, 1d);
		xyplot.getRangeAxis().setRange(0d, 1.1d);
		setBasicRenderer(xyplot);
		
		return result;
	}
	
	private XYSeriesCollection createDataset() {
		
		_dataset = new XYSeriesCollection();

		if(_domain != null) {
			if(((FuzzySet) _domain).getLabelSet().getCardinality() > 0 ) {
				XYSeries series;

				for(LabelLinguisticDomain label: ((FuzzySet) _domain).getLabelSet().getLabels()) {
					series = new XYSeries(label.getName());
					IMembershipFunction membershipFunction = label.getSemantic();
					
					if(membershipFunction instanceof TrapezoidalFunction) {
						TrapezoidalFunction trapezoidalFunction = (TrapezoidalFunction) membershipFunction;
						series.add(trapezoidalFunction.getCoverage().getMin(), 0.0);
						series.add(trapezoidalFunction.getCenter().getMin(), 1.0);
						series.add(trapezoidalFunction.getCenter().getMax(), 1.0);
						series.add(trapezoidalFunction.getCoverage().getMax(), 0.0);
					}
					_dataset.addSeries(series);
				}
			}
		}
		
		return _dataset;
		
	}
	
	private Color colorForEachLabel(int pos) {
		int r, g, b;
		
		r = (63 * (pos + 1)) % 255;
		g = (107 * (pos + 2)) % 255;
		b = (217 * (pos + 3)) % 255;
		
		return new Color(r, g, b);
		
	}
	
	private void setBasicRenderer(XYPlot xyplot) {
		XYItemRenderer renderer = xyplot.getRenderer(0);
		
		for(int i = 0; i < xyplot.getSeriesCount(); ++i) {
			renderer.setSeriesStroke(i, new BasicStroke());
			renderer.setSeriesPaint(i, colorForEachLabel(i));
		}
	}

}
