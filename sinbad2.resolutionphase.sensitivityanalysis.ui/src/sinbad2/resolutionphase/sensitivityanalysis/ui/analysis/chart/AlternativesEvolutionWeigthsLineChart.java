package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.resolutionphase.sensitivityanalysis.EModel;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.nls.Messages;

public class AlternativesEvolutionWeigthsLineChart {

	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	private XYSeriesCollection _dataset;
	private ValueMarker _currentMarker;
	private ValueMarker _variableMarker;
	private ValueMarker _horizontalMarker;

	private List<Alternative> _alternatives;
	private Criterion _criterionSelected;

	private Integer _typeTODIMChart;
	
	private EModel _model;

	private SensitivityAnalysis _sensitivityAnalysis;

	public AlternativesEvolutionWeigthsLineChart() {
		_alternatives = ProblemElementsManager.getInstance().getActiveElementSet().getAlternatives();

		_chart = null;
		_chartComposite = null;
	}

	public Criterion getCriterionSelected() {
		return _criterionSelected;
	}

	public void setCriterionSelected(Criterion criterionSelected) {
		_criterionSelected = criterionSelected;
	}

	public double getPositionCurrentValueMarker() {
		if (_currentMarker != null) {
			return _currentMarker.getValue();
		}
		return -1;
	}

	public void setPositionCurrentValueMarker(double valueMarker) {
		_currentMarker.setValue(valueMarker);
	}

	public double getPositionVariableValueMarker() {
		if (_variableMarker != null) {
			return _variableMarker.getValue();
		}
		return -1;
	}

	public void setPositionVariableValueMarker(double valueMarker) {
		_variableMarker.setValue(valueMarker);
	}

	public EModel getModel() {
		return _model;
	}

	public void setModel(EModel model) {
		_model = model;
	}

	public ChartComposite getChartComposite() {
		return _chartComposite;
	}

	public void initialize(Composite container, int width, int height, int style, SensitivityAnalysis sensitivityAnalysis) {
		_sensitivityAnalysis = sensitivityAnalysis;
		_typeTODIMChart = null;

		refreshChart();

		_chartComposite = new ChartComposite(container, style, _chart, true);
		_chartComposite.setSize(width, height);
	}
	
	public void initialize(Composite container, int width, int height, int style, SensitivityAnalysis sensitivityAnalysis, int typeTODIM) {
		_sensitivityAnalysis = sensitivityAnalysis;
		_typeTODIMChart = typeTODIM;

		refreshChart();

		_chartComposite = new ChartComposite(container, style, _chart, true);
		_chartComposite.setSize(width, height);
	}

	public void refreshChart() {
		if (_chart == null) {
			_chart = createChart(createDataset());
		} else {
			_chart.getXYPlot().setDataset(createDataset());
		}
	}

	private JFreeChart createChart(XYDataset dataset) {
		JFreeChart result = ChartFactory.createXYLineChart(null, null, null, dataset, PlotOrientation.VERTICAL, true, true, false);

		result.setBackgroundPaint(Color.white);

		final XYPlot plot = result.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(true);

		setDefaultRanges(plot);

		XYLineAndShapeRenderer render = (XYLineAndShapeRenderer) plot.getRenderer();
		render.setBaseToolTipGenerator(createToolTipGenerator());
		
		createCurrentMarker();
		createVariableMarker();
		
		plot.addDomainMarker(_currentMarker);
		plot.addDomainMarker(_variableMarker);

		return result;
	}

	private void setDefaultRanges(XYPlot plot) {
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		setRangeAxis(rangeAxis, 0, 1);

		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		setRangeAxis(domainAxis, 0, 1);
	}
	
	private void setRangeAxis(ValueAxis rangeAxis, double lower, double upper) {
		rangeAxis.setRange(lower, upper);
	}

	private XYToolTipGenerator createToolTipGenerator() {
		XYToolTipGenerator xyToolTipGenerator = new XYToolTipGenerator() {
			public String generateToolTip(XYDataset dataset, int series, int item) {
				StringBuffer sb = new StringBuffer();
				double x = dataset.getX(series, item).doubleValue();
				double y = dataset.getY(series, item).doubleValue();
				String coordX = Double.toString(Math.round(x * 10000d) / 10000d);
				String coordY = Double.toString(Math.round(y * 10000d) / 10000d);
				sb.append("(" + coordX + ", " + coordY + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return sb.toString();
			}
		};

		return xyToolTipGenerator;
	}

	private void createCurrentMarker() {
		_currentMarker = new ValueMarker(0);
		_currentMarker.setLabelFont(new java.awt.Font("SansSerif", Font.BOLD, 12)); //$NON-NLS-1$
		_currentMarker.setLabel(Messages.AlternativesEvolutionWeigthsLineChart_actual_weighted);
		_currentMarker.setPaint(Color.red);
		_currentMarker.setStroke(new BasicStroke(2));
		_currentMarker.setLabelOffset(new RectangleInsets(10, 10, 10, 50));
		_currentMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
	}
	
	private void createVariableMarker() {
		_variableMarker = new ValueMarker(0);
		_variableMarker.setLabelFont(new java.awt.Font("SansSerif", Font.BOLD, 12)); //$NON-NLS-1$
		_variableMarker.setLabel(Messages.AlternativesEvolutionWeigthsLineChart_variable_weighted);
		_variableMarker.setPaint(Color.blue);
		_variableMarker.setStroke(new BasicStroke(2));
		_variableMarker.setLabelOffset(new RectangleInsets(10, 10, 10, 54));
		_variableMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);	
	}
	
	private XYDataset createDataset() {
		_dataset = new XYSeriesCollection();

		if (_criterionSelected != null) {
			switch (_model) {
				case WEIGHTED_SUM:
				case ANALYTIC_HIERARCHY_PROCESS:
					
					setChartTitle(Messages.AlternativesEvolutionWeigthsLineChart_Preferences_evolution + _criterionSelected.getId().toUpperCase());
					removeMarker(_horizontalMarker);
					
					List<XYSeries> alternativesSeries;
					if (_typeTODIMChart == null) {	
						alternativesSeries = computeEvolutionWeights();	
					} else if(_typeTODIMChart == 0) {
						alternativesSeries = computeEvolutionWeights();	
					} else {
						setRangeAxis(_chart.getXYPlot().getRangeAxis(), -1, 2);
						setRangeAxis(_chart.getXYPlot().getDomainAxis(), 1, 5);
						setChartTitle("Attenuation factor evolution");
						
						alternativesSeries = computeAttenuationFactorEvolution();
					}
	
					for (XYSeries se : alternativesSeries) {
						_dataset.addSeries(se);
					}
	
					break;
				case WEIGHTED_PRODUCT:
					
					setChartTitle(Messages.AlternativesEvolutionWeigthsLineChart_Ratios_evolution + _criterionSelected.getId().toUpperCase());
	
					if (_horizontalMarker == null) {
						createHorizontalMarker();
					}
	
					int numSeries = 0;
					for (int i = 0; i < _alternatives.size() - 1; ++i) {
						for (int j = (i + 1); j < _alternatives.size(); ++j) {
							XYSeries alternativeSerie = new XYSeries(_alternatives.get(i).getId() + " - " + _alternatives.get(j).getId()); //$NON-NLS-1$
							for (double k = 0; k <= 1.01; k += 0.01) {
								alternativeSerie.add(Math.round(k * 100d) / 100d, _sensitivityAnalysis.computeAlternativeRatioFinalPreferenceInferWeights(i, j,
												_sensitivityAnalysis.calculateInferWeights(_criterionSelected, k)));
							}
							
							_dataset.addSeries(alternativeSerie);
							_chart.getXYPlot().getRenderer().setSeriesStroke(numSeries, new BasicStroke(2.0f));
							numSeries++;
						}
					}

					setRangeAxis(_chart.getXYPlot().getRangeAxis(), -10, _dataset.getRangeUpperBound(false) + 2);
					break;
			}
		}
		return _dataset;
	}

	private void setChartTitle(String title) {
		_chart.setTitle(title);
	}

	private void removeMarker(ValueMarker marker) {
		if (marker != null) {
			_chart.getXYPlot().removeRangeMarker(marker);
		}
	}

	private void createHorizontalMarker() {
		Stroke dashed = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f);
		_horizontalMarker = new ValueMarker(1);
		_horizontalMarker.setLabelFont(new java.awt.Font("SansSerif", Font.BOLD, 12)); //$NON-NLS-1$
		_horizontalMarker.setPaint(Color.red);
		_horizontalMarker.setStroke(dashed);
		_horizontalMarker.setLabelOffset(new RectangleInsets(10, 10, 30, 10));
		_horizontalMarker.setLabelAnchor(RectangleAnchor.CENTER);
		_chart.getXYPlot().addRangeMarker(_horizontalMarker);
	}
	
	private List<XYSeries> computeEvolutionWeights() {

		List<XYSeries> alternativesSeries = new LinkedList<XYSeries>();
		for (int i = 0; i < _alternatives.size(); ++i) {
			alternativesSeries.add(new XYSeries(_alternatives.get(i).getId()));
		}
		
		Double[] alternativePreferences;
		for (double j = 0.01; j <= 1.0; j += 0.01) {
			alternativePreferences = _sensitivityAnalysis.computeAlternativesFinalPreferenceInferWeights(_sensitivityAnalysis.calculateInferWeights(_criterionSelected, j));
			for (int s = 0; s < alternativesSeries.size(); ++s) {
				XYSeries serie = alternativesSeries.get(s);
				serie.add(Math.round(j * 100d) / 100d, alternativePreferences[s]);
			}
		}
		
		return alternativesSeries;
	}
	
	private List<XYSeries> computeAttenuationFactorEvolution() {
		
		List<XYSeries> alternativesSeries = new LinkedList<XYSeries>();
		for (int i = 0; i < _alternatives.size(); ++i) {
			alternativesSeries.add(new XYSeries(_alternatives.get(i).getId()));
		}
		
		Double[] alternativePreferences;
		for (int j = 1; j <= 5; j++) {
			alternativePreferences = _sensitivityAnalysis.computeAlternativesPreferenceInferAttenuationFactor(j);
			for (int s = 0; s < alternativesSeries.size(); ++s) {
				XYSeries serie = alternativesSeries.get(s);
				serie.add(j, alternativePreferences[s]);
			}
		}
		
		return alternativesSeries;
	}
}
