package sinbad2.domain.linguistic.fuzzy.ui.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleInsets;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.ui.jfreechart.DomainChart;

public class PlainFuzzySetChart extends DomainChart {

	private XYSeriesCollection _datasetSeries;

	private boolean _fixColor;

	private int _r;

	private int _g;

	private int _b;

	private boolean _dashed;

	public PlainFuzzySetChart() {
		_domain = null;
		_chart = null;
		_chartComposite = null;
		_fixColor = false;
	}

	@Override
	public void initialize(Domain domain, Composite container, int width, int height, int style) {
		setDomain(domain);
		_chartComposite = new ChartComposite(container, style, _chart, true);
		_chartComposite.setSize(width, height);
	}
	
	public void initialize(Domain domain, Composite container, int width, int height, int style, boolean dashed) {
		setDomain(domain);
		_dashed = dashed;
		_chartComposite = new ChartComposite(container, style, _chart, true);
		_chartComposite.setSize(width, height);
	}

	public void initialize(FuzzySet domain, Composite composite, int width, int height, int style, int r, int g, int b, boolean dashed) {
		_dashed = dashed;
		_fixColor = true;
		_r = r;
		_g = g;
		_b = b;
		_domain = domain;
		_chart = createChart(createDataset());
		_chartComposite = new ChartComposite(composite, style, _chart, true);
		_chartComposite.setSize(width, height);
		_chartComposite.setBounds(-1, -1, width, height);
	}


	private JFreeChart createChart(XYSeriesCollection xySeriesCollection) {
		JFreeChart result = ChartFactory.createXYLineChart("", "", "", xySeriesCollection, PlotOrientation.VERTICAL, false, false, false);

		result.setBackgroundPaint(Color.white);
		XYPlot xyplot = (XYPlot) result.getPlot();
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.lightGray);
		xyplot.setRangeGridlinePaint(Color.lightGray);
		xyplot.getDomainAxis().setRange(0d, 1d);
		xyplot.getRangeAxis().setRange(0d, 1.01d);
		xyplot.getDomainAxis().setVisible(false);
		xyplot.getRangeAxis().setVisible(false);
		xyplot.setAxisOffset(RectangleInsets.ZERO_INSETS);
		xyplot.setInsets(RectangleInsets.ZERO_INSETS);
		result.setPadding(RectangleInsets.ZERO_INSETS);
		setBasicRenderer(xyplot);
		return result;
	}

	private XYSeriesCollection createDataset() {

		_datasetSeries = new XYSeriesCollection();

		if(_domain != null) {
			if(((FuzzySet) _domain).getLabelSet().getCardinality() > 0) {

				XYSeries series;
				for(LabelLinguisticDomain label : ((FuzzySet) _domain).getLabelSet().getLabels()) {
					IMembershipFunction membershipFunction = label.getSemantic();
					
					if(membershipFunction instanceof TrapezoidalFunction) {
						TrapezoidalFunction tmf = (TrapezoidalFunction) membershipFunction;
						series = new XYSeries(label.getName() + "_left"); //$NON-NLS-1$
						series.add(tmf.getCoverage().getMin(), 0.0);
						series.add(tmf.getCenter().getMin(), 1.0);
						_datasetSeries.addSeries(series);

						series = new XYSeries(label.getName() + "_right"); //$NON-NLS-1$
						series.add(tmf.getCenter().getMax(), 1.0);
						series.add(tmf.getCoverage().getMax(), 0.0);
						_datasetSeries.addSeries(series);
					}
				}
			}
		}

		return _datasetSeries;
	}

	private Color getRGB(int pos) {

		if(!_fixColor) {
			_r = (63 * ((pos / 2) + 1)) % 255;
			_g = (107 * ((pos / 2) + 2)) % 255;
			_b = (217 * ((pos / 2) + 3)) % 255;
		}

		return new Color(_r, _g, _b);
	}

	public static Color getColor(int pos) {

		int r, g, b;
		r = (63 * (pos + 1)) % 255;
		g = (107 * (pos + 2)) % 255;
		b = (217 * (pos + 3)) % 255;

		return new Color(r, g, b);
	}

	private void setBasicRenderer(XYPlot xyplot) {
		XYItemRenderer renderer = xyplot.getRenderer(0);

		BasicStroke stroke;
		for(int i = 0; i < xyplot.getSeriesCount(); i++) {
			if(_dashed) {
				stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 20.0f }, 0.0f);
			} else {
				stroke = new BasicStroke(1);
			}
			renderer.setSeriesStroke(i, stroke);
			renderer.setSeriesPaint(i, getRGB(i));
		}
	}
	
	public void setDomain(Domain domain) {
		_domain = domain;
		refreshChart();
	}

	public void refreshChart() {
		if(_chart == null) {
			_chart = createChart(createDataset());
		} else {
			_chart.getXYPlot().setDataset(createDataset());
		}
		setBasicRenderer(_chart.getXYPlot());
	}

	public void setSelection(int pos) {
		
	}

	public void setPosLeftColor(int pos, Color color) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		renderer.setSeriesPaint(pos * 2, color);
		renderer.setSeriesStroke(pos * 2, new BasicStroke(1));
	}

	public void setPosRightColor(int pos, Color color) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		renderer.setSeriesPaint((pos * 2) + 1, color);
		renderer.setSeriesStroke((pos * 2) + 1, new BasicStroke(1));
	}

	public void setPosColor(int pos, Color color) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		renderer.setSeriesPaint(pos * 2, color);
		renderer.setSeriesPaint((pos * 2) + 1, color);
		renderer.setSeriesStroke(pos * 2, new BasicStroke(1));
		renderer.setSeriesStroke((pos * 2) + 1, new BasicStroke(1));
	}

	public void selectLeft(int pos) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		renderer.setSeriesStroke(pos * 2, new BasicStroke(3));
	}

	public void selectRight(int pos) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		renderer.setSeriesStroke((pos * 2) + 1, new BasicStroke(3));
	}

	public void select(int pos) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		renderer.setSeriesStroke(pos * 2, new BasicStroke(3));
		renderer.setSeriesStroke((pos * 2) + 1, new BasicStroke(3));
	}

	public void setSelection(int lower, int upper) {
		XYPlot xyplot = (XYPlot) _chart.getPlot();
		XYItemRenderer renderer = xyplot.getRenderer(0);
		for (int i = 0; i < xyplot.getSeriesCount(); i++) {
			if (((lower * 2) <= i) && (i <= (upper * 2))) {
				renderer.setSeriesStroke(i, new BasicStroke(3));
			} else {
				renderer.setSeriesStroke(i, new BasicStroke(1));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelection(Object selection) {
		if(selection instanceof Integer) {
			XYPlot xyplot = (XYPlot) _chart.getPlot();
			XYItemRenderer renderer = xyplot.getRenderer(0);
			for(int i = 0; i < xyplot.getSeriesCount(); i++) {
				if((i == ((Integer) selection * 2)) || (i == (((Integer) selection * 2) + 1))) {
					renderer.setSeriesStroke(i, new BasicStroke(3));
				} else {
					renderer.setSeriesStroke(i, new BasicStroke(1));
				}
			}
		} else {
			List<Integer> lowerUpper = (List<Integer>) selection;
			XYPlot xyplot = (XYPlot) _chart.getPlot();
			XYItemRenderer renderer = xyplot.getRenderer(0);
			for (int i = 0; i < xyplot.getSeriesCount(); i++) {
				if (((lowerUpper.get(0) * 2) <= i) && (i <= (lowerUpper.get(1) * 2))) {
					renderer.setSeriesStroke(i, new BasicStroke(3));
				} else {
					renderer.setSeriesStroke(i, new BasicStroke(1));
				}
			}
		}
			
	}

	@Override
	public void displayRanking(Object ranking) {}
}
