package sinbad2.domain.numeric.real.ui.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

import sinbad2.core.validator.Validator;
import sinbad2.domain.Domain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.domain.numeric.real.ui.nls.Messages;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class NumericRealDomainChart extends DomainChart {
	
	private NumericRealDomain _domain;
	
	public static final Color[] colors = {Color.RED, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.LIGHT_GRAY, Color.ORANGE, Color.ORANGE,
		Color.MAGENTA, Color.PINK, Color.WHITE, Color.YELLOW};
	
	private ValueMarker _numMarker; 
	private IntervalMarker _intervalMarker; 
	
	private ValueMarker[] _alternativesMarkers;

	public NumericRealDomainChart() {
		super();
		_domain = null;
		_chart = null;
		_chartComposite = null;
		_numMarker = null;
		_intervalMarker = null;
	}
	
	@Override
	public void refreshChart() {
		
		if(_chart == null) {
			_chart = createChart(createIntervalDataset());
		} else {
			_chart.getXYPlot().setDataset(createIntervalDataset());
		}
		
		double upperLimit = _domain.getMax(), lowerLimit = _domain.getMin();
		boolean inRange = _domain.getInRange();
		
		double rangeSize = upperLimit - lowerLimit;
		double margin = (rangeSize > 0) ? rangeSize / 4d : 1;
		
		_chart.getXYPlot().getRangeAxis().setRange(lowerLimit - margin, upperLimit + margin);
		_chart.getXYPlot().getRangeAxis().setTickLabelsVisible(inRange);
		
		if(inRange) {
			_chart.getXYPlot().getRangeAxis().setLabel(Messages.NumericRealDomainChart_Domain);
		} else {
			_chart.getXYPlot().getRangeAxis().setLabel(Double.toString(Double.NEGATIVE_INFINITY) + 
					"                                         " +  //$NON-NLS-1$
					Double.toString(Double.POSITIVE_INFINITY));
		}
		
	}
	
	@Override
	public void setDomain(Domain domain) {
		Validator.notNull(domain);
		Validator.notIllegalElementType(domain, 
				new String[] { NumericRealDomain.class.toString() });
		
		_domain = (NumericRealDomain) domain;
		refreshChart();
	}

	@Override
	public void setSelection(Object selection) {

		if(_numMarker != null) {
			_chart.getXYPlot().removeRangeMarker(_numMarker);
		}
		
		if(_intervalMarker != null) {
			_chart.getXYPlot().removeRangeMarker(_intervalMarker);
		}
		
		if(selection instanceof LinkedList<?>) {
			_intervalMarker = new IntervalMarker((Double)((LinkedList<?>) selection).getFirst(), (Double)((LinkedList<?>) selection).getLast());
			_intervalMarker.setAlpha(0.5f);
			_intervalMarker.setPaint(Color.RED);
			_chart.getXYPlot().addRangeMarker(_intervalMarker);
		} else {	
			_numMarker = new ValueMarker((Double) selection);
			_numMarker.setPaint(Color.RED);
			_numMarker.setStroke(new BasicStroke(4));
			_chart.getXYPlot().addRangeMarker(_numMarker);
		}
		
	}

	@Override
	public void initialize(Domain domain, Composite container, int width, int height, int style) {
		
		setDomain(domain);
		_chartComposite = new ChartComposite(container, style, _chart, true);
		_chartComposite.setSize(width, height);
		
	}
	
	private JFreeChart createChart(IntervalXYDataset intervalXYDataset) {
		JFreeChart result = ChartFactory.createXYBarChart("", "X", false, "", intervalXYDataset,  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				PlotOrientation.HORIZONTAL, false, false, false);
		
		result.setBackgroundPaint(Color.WHITE);
		XYPlot xyplot = (XYPlot) result.getPlot();
		
		XYBarRenderer xyBarRenderer = (XYBarRenderer) xyplot.getRenderer();
		xyBarRenderer.setSeriesPaint(0, Color.BLUE);
		xyBarRenderer.setUseYInterval(true);
		xyBarRenderer.setBarPainter(new StandardXYBarPainter());
		
		xyplot.setBackgroundPaint(Color.WHITE);
		xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		xyplot.getDomainAxis().setVisible(false);
		
		return result;
	}
	
	private IntervalXYDataset createIntervalDataset() {
		DefaultIntervalXYDataset result = new DefaultIntervalXYDataset();
		
		double upperLimit = _domain.getMax(), lowerLimit = _domain.getMin();
		
		double[] x = new double[] {1};
		double[] xStart = new double[] {0.1};
		double[] xEnd = new double[] {0.9};
		double[] y = new double[] {upperLimit - lowerLimit};
		double[] yStart = new double[] {lowerLimit};
		double[] yEnd = new double[] {upperLimit};
		double[][] data = new double[][] {x, xStart, xEnd, y, yStart, yEnd};
		result.addSeries(Messages.NumericRealDomainChart_Range, data);
		
		return result;
	}

	@Override
	public void displayRanking(Object ranking) {
		
		if(ranking != null) {
			String[] alternatives = (String[]) ((Object[]) ranking)[0];
			double[] pos = (double[]) ((Object[]) ranking)[1];
			displayAlternatives(alternatives, pos, colors);
		}
	}
	
	public void displayAlternatives(String[] alternatives, double[] pos, Color[] colors) {
		
		if(_numMarker != null) {
			_chart.getXYPlot().removeRangeMarker(_numMarker);
		}
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
			if(size > 0) {
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
