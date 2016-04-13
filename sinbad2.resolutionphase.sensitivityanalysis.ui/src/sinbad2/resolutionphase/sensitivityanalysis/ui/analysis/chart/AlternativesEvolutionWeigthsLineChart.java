package sinbad2.resolutionphase.sensitivityanalysis.ui.analysis.chart;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
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

public class AlternativesEvolutionWeigthsLineChart {

	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	private XYSeriesCollection _dataset;
	private ValueMarker _marker;

	private double[][] _decisionMatrix;
	private Criterion _criterionSelected;
	private double _markerPosition;
	
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
	
	public double[][] getDecisionMatrix() {
		return _decisionMatrix;
	}
	
	public void setDecisionMatrix(double[][] decisionMatrix) {
		_decisionMatrix = decisionMatrix;
	}
	
	public double getValueMarker() {
		return _markerPosition;
	}
	
	public void setValueMarker(double valueMarker) {
		_markerPosition = valueMarker;
	}
	
	public ChartComposite getChartComposite() {
		return _chartComposite;
	}

	public void initialize(Composite container, int width, int height, int style) {
		
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

        XYPlot plot = result.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickMarksVisible(true);
        
		return result;
	}

	private XYDataset createDataset() {
		_dataset = new XYSeriesCollection();
		
		if(_chart != null) {
			_chart.getXYPlot().removeDomainMarker(_marker);
			_marker = new ValueMarker(_markerPosition);
			_marker.setLabelFont(new java.awt.Font("SansSerif", Font.BOLD, 12));
			_marker.setLabel("peso real");
			_marker.setPaint(Color.red);
	        _chart.getXYPlot().addDomainMarker(_marker);
		}
                
		if(_criterionSelected != null) {
			_chart.setTitle(_criterionSelected.getId().toUpperCase());
			
			List<Alternative> alternatives = _elementsSet.getAlternatives();
			int criterion = _elementsSet.getAllCriteria().indexOf(_criterionSelected);
			for(int i = 0; i < alternatives.size(); ++i) {
				XYSeries alternativeSeries = new XYSeries(alternatives.get(i).getId());
				for(double w = 0; w <= 1; w += 0.1 ) {
					alternativeSeries.add(w, _decisionMatrix[criterion][i] * w);
				}
				_dataset.addSeries(alternativeSeries);
			}
		}
		return _dataset;
	}
}
