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
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class LinguisticDomainChart extends DomainChart {
	
	private XYSeriesCollection _dataset;
	
	public static final Color[] colors = {Color.RED, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.LIGHT_GRAY, Color.ORANGE, Color.ORANGE,
		Color.MAGENTA, Color.PINK, Color.WHITE, Color.YELLOW};
	
	private ValueMarker[] _alternativesMarkers;
	
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

	@SuppressWarnings("unchecked")
	@Override
	public void setSelection(Object selection) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);

		if(!(selection instanceof LinkedList<?>)) {
			for(int i = 0; i < xyplot.getSeriesCount(); ++i) {
				if( i != (Integer) selection) {
					renderer.setSeriesStroke(i, new BasicStroke(1));
				} else {
					renderer.setSeriesStroke(i, new BasicStroke(3));
				}
			}
		} else {
			if(((LinkedList<Integer>) selection).size() > 1) { 
				int lower = ((LinkedList<Integer>) selection).get(0);
				int upper = ((LinkedList<Integer>) selection).get(1);
				for(int i = 0; i < xyplot.getSeriesCount(); ++i) {
					if ((lower <= i) && (i <= upper)) {
						renderer.setSeriesStroke(i, new BasicStroke(3));
					} else {
						renderer.setSeriesStroke(i, new BasicStroke(1));
					}	
				}
			} else {
				int value = ((LinkedList<Integer>) selection).get(0);
				for(int i = 0; i < xyplot.getSeriesCount(); ++i) {
					if( i != value) {
						renderer.setSeriesStroke(i, new BasicStroke(1));
					} else {
						renderer.setSeriesStroke(i, new BasicStroke(3));
					}
				}
			}
		}
		
	}
	
	private JFreeChart createChart(XYSeriesCollection dataset) {
		JFreeChart result = ChartFactory.createXYLineChart("", "", "", dataset, PlotOrientation.VERTICAL, true, true, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
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

	@Override
	public void displayRanking(Object ranking) {		
		
		Double[] values = null;
		if(ranking != null) {
			String[] alternatives = (String[]) ((Object[]) ranking)[0];
			if(((Object[]) ranking)[1] instanceof int[]) {
				values = new Double[((int[]) ((Object[]) ranking)[1]).length];
				for(int i = 0; i < ((int[]) ((Object[]) ranking)[1]).length; ++i) {
					values[i] = (double) ((int[]) ((Object[]) ranking)[1])[i];
				}
			} else if(((Object[]) ranking)[1] instanceof double[]) {
				values = new Double[((double[]) ((Object[]) ranking)[1]).length];
				for(int i = 0; i < ((double[]) ((Object[]) ranking)[1]).length; ++i) {
					values[i] = (double) ((double[]) ((Object[]) ranking)[1])[i];
				}
			}
			if(((Object[]) ranking).length == 2) {
				displayAlternatives(alternatives, values, colors);
			} else {
				int[] pos = ((int[]) ((Object[]) ranking)[2]);
				double[] alpha = ((double[]) ((Object[]) ranking)[3]);
				displayAlternatives(alternatives, values, pos, alpha, colors);
			}
		}
	}
	
	public void displayAlternatives(String[] alternatives, Double[] pos, Color[] colors) {

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
					items.add(new MyItem(alternatives[i], (Double) pos[i], colors[i]));
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
					_alternativesMarkers[i].setLabelFont(new Font("TimesRoman", Font.BOLD, 20)); //$NON-NLS-1$
					_alternativesMarkers[i].setLabelPaint(item.color);
					_alternativesMarkers[i].setLabelOffset(new RectangleInsets(offset * (i / offset), 25, 0, 0));
					_chart.getXYPlot().addRangeMarker(0, _alternativesMarkers[i], Layer.FOREGROUND);
				}
			}
		}
	}
	
	
	public void displayAlternatives(String[] alternatives, Double[] values, int[] pos, double[] alpha, Color[] colors) {
		displayAlternatives(alternatives, values, colors);
		displayAlternatives(alternatives, pos, alpha);
	}
	
	@SuppressWarnings("unchecked")
	public void displayAlternatives(String[] alternatives, int[] pos, double[] alpha) {
		int size = alternatives.length;
		int cardinality = ((FuzzySet) _domain).getLabelSet().getCardinality();
		XYSeries series, alternativeSeries;
		double[] centers = new double[size];
		double x, y, factor;
		
		List<XYDataItem> dataItems;
		for(int i = 0; i < size; i++) {
			if(alternatives[i] != null) {
				series = _dataset.getSeries(pos[i]);
				alternativeSeries = new XYSeries(alternatives[i]);

				dataItems = (List<XYDataItem>) series.getItems();
				if(alpha[i] >= 0) {
					factor = (dataItems.get(3).getX().doubleValue() - dataItems.get(2).getX().doubleValue()) * alpha[i];
				} else {
					factor = (dataItems.get(1).getX().doubleValue() - dataItems.get(0).getX().doubleValue()) * alpha[i];
				}
				XYDataItem item;
				for(int itemPos = 0; itemPos < dataItems.size(); itemPos++) {
					item = dataItems.get(itemPos);
					x = item.getXValue();
					y = item.getYValue();
					if(itemPos == 0) {
						if(x == dataItems.get(1).getX().doubleValue()) {
							x -= dataItems.get(3).getX().doubleValue() - dataItems.get(2).getX().doubleValue();
						}
					} else if (itemPos == 1) {
						centers[i] = x + factor;
					} else if(itemPos == 3) {
						if(x == dataItems.get(2).getX().doubleValue()) {
							x += dataItems.get(1).getX().doubleValue() - dataItems.get(0).getX().doubleValue();
						}
					}
					alternativeSeries.add(x + factor, y);

				}
				_dataset.addSeries(alternativeSeries);
			}
		}

		_chart.getXYPlot().setDataset(_dataset);
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		XYTextAnnotation annotation;
		for (int i = 0; i < xyplot.getSeriesCount(); i++) {
			if (i >= cardinality) {
				renderer.setSeriesStroke(i, new BasicStroke(3));
				renderer.setSeriesPaint(i, colorForEachLabel(pos[i - cardinality]));
				annotation = new XYTextAnnotation(alternatives[i - cardinality], centers[i - cardinality], 1.05);
				annotation.setFont(new Font("SansSerif", Font.PLAIN, 10)); //$NON-NLS-1$
				xyplot.addAnnotation(annotation);
			} else {
				renderer.setSeriesStroke(i, new BasicStroke(1));
				renderer.setSeriesPaint(i, colorForEachLabel(i));
			}
		}
	}
	
	public void displayAlternatives(String[] alternatives, TrapezoidalFunction[] fuzzyNumbers) {
		int size = fuzzyNumbers.length;
		XYSeries alternativeSeries = null;

		for(int i = 0; i < size; i++) {
			alternativeSeries = new XYSeries(alternatives[i]);
			alternativeSeries.add(fuzzyNumbers[i].getLimits()[0], 0);
			alternativeSeries.add(fuzzyNumbers[i].getLimits()[1], 1);
			alternativeSeries.add(fuzzyNumbers[i].getLimits()[2], 1);
			alternativeSeries.add(fuzzyNumbers[i].getLimits()[3], 0);
			
			_dataset.addSeries(alternativeSeries);
		}
		
		_chart.getXYPlot().setDataset(_dataset);
		
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		for (int i = 0; i < xyplot.getSeriesCount(); i++) {
			if (i >= ((FuzzySet) _domain).getLabelSet().getCardinality()) {
				renderer.setSeriesStroke(i, new BasicStroke(3));
			} else {
				renderer.setSeriesStroke(i, new BasicStroke());
				renderer.setSeriesPaint(i, colorForEachLabel(3));
			}
		}
	}
}
