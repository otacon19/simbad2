package sinbad2.domain.linguistic.fuzzy.ui.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class CreateManualDomainDialogChart extends DomainChart {
	
	private LabelLinguisticDomain _label;
	
	public static final Color[] colors = {Color.RED, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.LIGHT_GRAY, Color.ORANGE, Color.ORANGE,
		Color.MAGENTA, Color.PINK, Color.WHITE, Color.YELLOW};
	
	private ValueMarker[] _alternativesMarkers;
	
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
		JFreeChart result = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, false, true, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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
	
	//TODO
	@Override
	public void displayRanking(Object ranking) {
		
		if(ranking != null) {
			String[] alternatives = (String[]) ((Object[]) ranking)[0];
			int[] pos = (int[]) ((Object[]) ranking)[1];
			displayAlternatives(alternatives, pos, colors);
		}
		
	}
	
	//TODO
	public void displayAlternatives(String[] alternatives, int[] pos, Color[] colors) {
			
		if(_alternativesMarkers != null) {
			for (ValueMarker marker : _alternativesMarkers) {
				_chart.getXYPlot().removeRangeMarker(marker);
			}
		}

		_alternativesMarkers = null;

		class MyItem implements Comparable<MyItem> {
			public String alternative;
			public Double pos;
			public Color color;

			public MyItem(String alternative, double pos, Color color) {
				this.alternative = alternative;
				this.pos = pos;
				this.color = color;
			}

			@Override
			public int compareTo(MyItem other) {
				return Double.compare(this.pos, other.pos);
			}
		}

		List<MyItem> items = null;
		if((alternatives != null) && (pos != null) && (colors != null)) {
			items = new LinkedList<MyItem>();
			for(int i = 0; i < pos.length; i++) {
				if (alternatives[i] != null) {
					items.add(new MyItem(alternatives[i], pos[i], colors[i]));
				}
			}

			Collections.sort(items);
		}

		if(items != null) {
			int size = items.size();
			if (size > 0) {
				int height = (int) (_chartComposite.getSize().y * 0.75f);
				int offset = height / size;
				_alternativesMarkers = new ValueMarker[size];
				MyItem item;
				for (int i = 0; i < size; i++) {
					item = items.get(i);
					_alternativesMarkers[i] = new ValueMarker(item.pos);
					_alternativesMarkers[i].setPaint(item.color);
					_alternativesMarkers[i].setStroke(new BasicStroke(3));
					_alternativesMarkers[i].setLabel(item.alternative);
					_alternativesMarkers[i].setLabelFont(new Font("TimesRoman", Font.BOLD, 20));
					_alternativesMarkers[i].setLabelPaint(item.color);
					_alternativesMarkers[i].setLabelOffset(new RectangleInsets(offset + (offset * i), 15, 0, 0));
					_chart.getXYPlot().addRangeMarker(0, _alternativesMarkers[i], Layer.FOREGROUND);
				}
			}
		}
	}
}
