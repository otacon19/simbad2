package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;

public class AlternativesEvolutionWeigthsLineChart {

	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	private XYSeriesCollection _dataset;
	private ValueMarker _currentMarker;
	private ValueMarker _variableMarker;

	private Criterion _criterionSelected;
	
	private SensitivityAnalysis _sensitivityAnalysis;
	
	private ProblemElementsSet _elementsSet;

	public AlternativesEvolutionWeigthsLineChart() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
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
		if(_currentMarker != null) {
			return _currentMarker.getValue();
		}
		return -1;
	}
	
	public void setPositionCurrentValueMarker(double valueMarker) {
		_currentMarker.setValue(valueMarker);
	}
	
	public double getPositionVariableValueMarker() {
		if(_variableMarker != null) {
			return _variableMarker.getValue();
		}
		return -1;
	}
	
	public void setPositionVariableValueMarker(double valueMarker) {
		_variableMarker.setValue(valueMarker);
	}
	
	public ChartComposite getChartComposite() {
		return _chartComposite;
	}

	public void initialize(Composite container, int width, int height, int style, SensitivityAnalysis sensitivityAnalysis) {
		_sensitivityAnalysis = sensitivityAnalysis;
		
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
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 1);
        rangeAxis.setTickMarksVisible(true);
        
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0, 1);
        domainAxis.setTickMarksVisible(true);
        
        XYToolTipGenerator xyToolTipGenerator = new XYToolTipGenerator() {
            public String generateToolTip(XYDataset dataset, int series, int item) {
               StringBuffer sb = new StringBuffer();
               double x = dataset.getX(series, item).doubleValue();
               double y = dataset.getY(series, item).doubleValue();
               String coordX = Double.toString(Math.round(x * 10000d) / 10000d);
               String coordY = Double.toString(Math.round(y * 10000d) / 10000d);
               sb.append("(" + coordX + ", " + coordY + ")");
               return sb.toString();
            }
         };

         XYLineAndShapeRenderer render = (XYLineAndShapeRenderer) plot.getRenderer();
         render.setBaseToolTipGenerator(xyToolTipGenerator);
        
        _currentMarker = new ValueMarker(0);
		_currentMarker.setLabelFont(new java.awt.Font("SansSerif", Font.BOLD, 12));
		_currentMarker.setLabel("peso real");
		_currentMarker.setPaint(Color.red);
		_currentMarker.setStroke(new BasicStroke(2));
        plot.addDomainMarker(_currentMarker);
        
        _variableMarker = new ValueMarker(0);
		_variableMarker.setLabelFont(new java.awt.Font("SansSerif", Font.BOLD, 12));
		_variableMarker.setLabel("peso variable");
		_variableMarker.setPaint(Color.blue);
		_variableMarker.setStroke(new BasicStroke(2));
        plot.addDomainMarker(_variableMarker);
        
		return result;
	}

	private XYDataset createDataset() {
		_dataset = new XYSeriesCollection();
                
		if(_criterionSelected != null) {
			_chart.setTitle(_criterionSelected.getId().toUpperCase());
			
			List<Alternative> alternatives = _elementsSet.getAlternatives();
			for(int i = 0; i < alternatives.size(); ++i) {
				XYSeries alternativeSerie = new XYSeries(alternatives.get(i).getId());
				alternativeSerie.add(0, _sensitivityAnalysis.computeAlternativeFinalPreferenceInferWeights(i, _sensitivityAnalysis.calculateInferWeights(_criterionSelected, 0)));
				alternativeSerie.add(_sensitivityAnalysis.getWeights()[_elementsSet.getAllCriteria().indexOf(_criterionSelected)], _sensitivityAnalysis.getAlternativesFinalPreferences()[i]);
				alternativeSerie.add(1, _sensitivityAnalysis.computeAlternativeFinalPreferenceInferWeights(i, _sensitivityAnalysis.calculateInferWeights(_criterionSelected, 1)));
				_dataset.addSeries(alternativeSerie);
			}
		}
		return _dataset;
	}
}
