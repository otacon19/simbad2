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
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class CreateManualDomainDialogChart extends DomainChart {
	
	private LabelLinguisticDomain _label;
	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	
	public CreateManualDomainDialogChart() {
		_label = null;
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
	public void setSelection(Object selection) {}
	
	public void setLabel(LabelLinguisticDomain label) {
		_label = label;
		refreshChart();
	}
	
	private JFreeChart createChart(XYSeriesCollection dataset) {
		JFreeChart result = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, false, true, false);

		result.setBackgroundPaint(Color.WHITE);
		XYPlot xyplot = (XYPlot) result.getPlot();
		xyplot.setBackgroundPaint(Color.WHITE);
		xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		xyplot.getDomainAxis().setRange(0d, 1d);
		xyplot.getRangeAxis().setRange(0d, 1.1d);
		
		return result;
	}
	
	private XYSeriesCollection createDataset() {
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		if(_label != null) {
			XYSeries series = new XYSeries(_label.getName());
			IMembershipFunction membershipFunction = _label.getSemantic();		
			if(membershipFunction instanceof TrapezoidalFunction) {
				TrapezoidalFunction trapezoidalFunction = (TrapezoidalFunction) membershipFunction;
				series.add(trapezoidalFunction.getCoverage().getMin(), 0.0);
				series.add(trapezoidalFunction.getCenter().getMin(), 1.0);
				series.add(trapezoidalFunction.getCenter().getMax(), 1.0);
				series.add(trapezoidalFunction.getCoverage().getMax(), 0.0);
			}
				dataset.addSeries(series);
		}
		
		return dataset;
		
	}
	
	private void setBasicRenderer(XYPlot xyplot) {
		xyplot = (XYPlot) _chart.getXYPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		
		for(int i = 0; i < xyplot.getSeriesCount(); ++i) {
			renderer.setSeriesStroke(i, new BasicStroke(1));
		}
	}

}
