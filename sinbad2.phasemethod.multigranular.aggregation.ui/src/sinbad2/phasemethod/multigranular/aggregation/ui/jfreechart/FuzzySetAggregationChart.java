package sinbad2.phasemethod.multigranular.aggregation.ui.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class FuzzySetAggregationChart extends DomainChart {

	private XYSeriesCollection _datasetSeries;

	public FuzzySetAggregationChart() {
		_domain = null;
		_chart = null;
		_chartComposite = null;
	}

	public void initialize(Domain domain, Composite composite, int width, int height, int style) {
		setDomain(domain);
		_chartComposite = new ChartComposite(composite, style, _chart, true);
		_chartComposite.setSize(width, height);
	}

	private JFreeChart createChart(XYSeriesCollection xySeriesCollection) {
		JFreeChart result = ChartFactory.createXYLineChart("", "", "", xySeriesCollection, PlotOrientation.VERTICAL,
				true, true, false);

		result.setBackgroundPaint(Color.white);
		XYPlot xyplot = (XYPlot) result.getPlot();
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.lightGray);
		xyplot.setRangeGridlinePaint(Color.lightGray);
		// xyplot.setBackgroundPaint(Color.lightGray);
		// xyplot.setDomainGridlinePaint(Color.white);
		// xyplot.setRangeGridlinePaint(Color.white);
		xyplot.getDomainAxis().setRange(0d, 1d);
		xyplot.getRangeAxis().setRange(0d, 1.1d);
		setBasicRenderer(xyplot);
		return result;
	}

	private XYSeriesCollection createDataset() {

		_datasetSeries = new XYSeriesCollection();

		if (_domain != null) {
			if (((FuzzySet) _domain).getLabelSet().getCardinality() > 0) {
				XYSeries series;
				for (LabelLinguisticDomain label : ((FuzzySet) _domain).getLabelSet().getLabels()) {
					series = new XYSeries(label.getName());
					IMembershipFunction membershipFunction = label.getSemantic();

					if (membershipFunction instanceof TrapezoidalFunction) {
						TrapezoidalFunction tmf = (TrapezoidalFunction) membershipFunction;
						series.add(tmf.getCoverage().getMin(), 0.0);
						series.add(tmf.getCenter().getMin(), 1.0);
						series.add(tmf.getCenter().getMax(), 1.0);
						series.add(tmf.getCoverage().getMax(), 0.0);
					}
					_datasetSeries.addSeries(series);
				}
			}
		}

		return _datasetSeries;
	}
	
	private Color getRGB(int pos) {

		int r, g, b;

		r = (63 * (pos + 1)) % 255;
		g = (107 * (pos + 2)) % 255;
		b = (217 * (pos + 3)) % 255;

		return new Color(r, g, b);
	}

	private void setBasicRenderer(XYPlot xyplot) {
		XYItemRenderer renderer = xyplot.getRenderer(0);

		for (int i = 0; i < xyplot.getSeriesCount(); i++) {
			renderer.setSeriesStroke(i, new BasicStroke(1));
			renderer.setSeriesPaint(i, getRGB(i));
		}
	}

	public void refreshChart() {
		XYPlot xyplot = _chart.getXYPlot();
		xyplot.setDataset(createDataset());
		setBasicRenderer(xyplot);
	}

	public void setDomain(Domain domain) {
		if(_chart == null) {
			_chart = createChart(createDataset());
		} else {
			_chart.getXYPlot().setDataset(createDataset());
		}
		
		setBasicRenderer(_chart.getXYPlot());
	}

	public void setSelection(int pos) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		for (int i = 0; i < xyplot.getSeriesCount(); i++) {
			if (i != pos) {
				renderer.setSeriesStroke(i, new BasicStroke(1));
			} else {
				renderer.setSeriesStroke(i, new BasicStroke(3));
			}
		}
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
				series = _datasetSeries.getSeries(pos[i]);
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
				_datasetSeries.addSeries(alternativeSeries);
			}
		}

		_chart.getXYPlot().setDataset(_datasetSeries);
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		XYTextAnnotation annotation;
		for (int i = 0; i < xyplot.getSeriesCount(); i++) {
			if (i >= cardinality) {
				renderer.setSeriesStroke(i, new BasicStroke(3));
				renderer.setSeriesPaint(i, getRGB(pos[i - cardinality]));
				annotation = new XYTextAnnotation(alternatives[i - cardinality], centers[i - cardinality], 1.05);
				annotation.setFont(new Font("SansSerif", Font.PLAIN, 10)); //$NON-NLS-1$
				xyplot.addAnnotation(annotation);
			} else {
				renderer.setSeriesStroke(i, new BasicStroke(1));
				renderer.setSeriesPaint(i, getRGB(i));
			}
		}
	}

	@Override
	public void setSelection(Object selection) {
		// TODO
		
	}

	@Override
	public void displayRanking(Object ranking) {
		//TODO
	}

}
