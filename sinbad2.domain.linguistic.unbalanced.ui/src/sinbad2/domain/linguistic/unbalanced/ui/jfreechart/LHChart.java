package sinbad2.domain.linguistic.unbalanced.ui.jfreechart;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.PlainFuzzySetChart;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.domain.numeric.real.NumericRealDomain;

public class LHChart {
	
	private PlainFuzzySetChart[] _charts;

	private int[] _lh;

	private Composite _composite;

	private Unbalanced _domain;

	private LHChart() {
		_charts = null;
		_domain = null;
	}

	public LHChart(int[] lh, Composite composite, int width, int height) {
		this();

		_lh = lh;
		_charts = new PlainFuzzySetChart[_lh.length];

		_composite = composite;
		for (Control control : _composite.getChildren()) {
			control.dispose();
		}
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginLeft = 0;
		layout.spacing = 0;
		_composite.setLayout(layout);
		Composite auxComposite;
		int sumHeight = 0;
		int auxHeight = (height / _lh.length) + 1;
		for(int i = 0; i < _lh.length; i++) {
			sumHeight += auxHeight;
			while(sumHeight > height) {
				sumHeight--;
				auxHeight--;
			}
			auxComposite = new Composite(composite, SWT.NONE);
			PlainFuzzySetChart chart = new PlainFuzzySetChart();
			chart.initialize(createDomain(_lh[i]), auxComposite, width, auxHeight, SWT.NONE, 0, 0, 125, false);
			_charts[i] = chart;
		}

	}

	public LHChart(int[] lh, Composite composite, int width, int height, Unbalanced domain) {
		this();
		
		_lh = lh;
		int size = _lh.length;
		_charts = new PlainFuzzySetChart[size + 1];
		_domain = domain;
		
		_composite = composite;
		for(Control control : _composite.getChildren()) {
			control.dispose();
		}
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.marginLeft = 0;
		layout.spacing = 0;
		_composite.setLayout(layout);
		Composite auxComposite;

		int sumHeight = 0;
		int auxHeight = (height / (size + 1)) + 1;
		
		for(int i = 0; i < size; i++) {
			sumHeight += auxHeight;
			auxComposite = new Composite(composite, SWT.NONE);
			PlainFuzzySetChart chart = new PlainFuzzySetChart();
			chart.initialize(createDomain(_lh[i]), auxComposite, width, auxHeight, SWT.NONE, 0, 0, 125, true);
			_charts[i] = chart;
		}

		sumHeight += auxHeight;
		while(sumHeight > height) {
			sumHeight--;
			auxHeight--;
		}
		auxComposite = new Composite(composite, SWT.NONE);
		PlainFuzzySetChart chart = new PlainFuzzySetChart();
		chart.initialize(domain, auxComposite, width, auxHeight, SWT.NONE, false);
		_charts[size] = chart;
		
		clearCharts();
	}

	private void clearCharts() {

		for(PlainFuzzySetChart chart : _charts) {
			chart.refreshChart();
		}
		
		boolean find;
		int j;
		NumericRealDomain center;
		NumericRealDomain coverage;
		double left;
		double right;
		int other;
		int otherPos;
		Map<Integer, Map<Integer, Integer>> labels = _domain.getLabels();
		for(Integer pos : labels.keySet()) {
			Map<Integer, Integer> values = labels.get(pos);
			other = -1;
			otherPos = -1;
			for(Integer lhDomain : values.keySet()) {
				find = false;
				j = 0;
				do {
					if(_lh[j] == lhDomain) {
						find = true;
					} else {
						j++;
					}
				} while (!find);
				
				if(values.size() == 1) {
					_charts[j].setPosColor(values.get(lhDomain), PlainFuzzySetChart.getColor(pos));
				} else {
					if (other != -1) {
						center = ((FuzzySet) _domain).getLabelSet().getLabel(pos).getSemantic().getCenter();
						coverage = ((FuzzySet) _domain).getLabelSet().getLabel(pos).getSemantic().getCoverage();
						
						left = center.getMin() - coverage.getMin();
						right = coverage.getMax() - center.getMax();
						
						if(((left > right) && (other < lhDomain)) || ((left < right) && (other > lhDomain))) {
							_charts[otherPos].setPosLeftColor(values.get(other), PlainFuzzySetChart.getColor(pos));
							_charts[j].setPosRightColor(values.get(lhDomain), PlainFuzzySetChart.getColor(pos));	
						} else {
							_charts[j].setPosLeftColor(values.get(lhDomain), PlainFuzzySetChart.getColor(pos));
							_charts[otherPos].setPosRightColor(values.get(other), PlainFuzzySetChart.getColor(pos));
						}
					}
					
				}
				
				if (other == -1) {
					other = lhDomain;
					otherPos = j;
				}
			}
		}
	}
	
	private Unbalanced createDomain(int cardinality) {
		Unbalanced domain = new Unbalanced();
		String[] labels = new String[cardinality];
		for (int i = 0; i < cardinality; i++) {
			labels[i] = Integer.toString(i);
		}

		domain.createTrapezoidalFunction(labels);
		
		return domain;
	}

	public void dispose() {
		if (_charts != null) {
			for (PlainFuzzySetChart chart : _charts) {
				chart.getChartComposite().dispose();
			}
		}
	}
	
	public void select(int pos) {
		clearCharts();
		_charts[_lh.length].select(pos);
		
		Map<Integer, Integer> values = _domain.getLabels().get(pos);
		int other = -1;
		int otherPos = -1;
		boolean find;
		int j;
		NumericRealDomain center;
		NumericRealDomain coverage;
		for(Integer lhDomain : values.keySet()) {
			find = false;
			j = 0;
			do {
				if(_lh[j] == lhDomain) {
					find = true;
				} else {
					j++;
				}
			} while (!find);
			
			if(values.size() == 1) {
				_charts[j].select(values.get(lhDomain));
			} else {
				if(other != -1) {
					center = ((FuzzySet) _domain).getLabelSet().getLabel(pos).getSemantic().getCenter();
					coverage = ((FuzzySet) _domain).getLabelSet().getLabel(pos).getSemantic().getCoverage();
					
					double left = center.getMin() - coverage.getMin();
					double right = coverage.getMax() - center.getMax();
					
					if(((left > right) && (other < lhDomain)) || ((left < right) && (other > lhDomain))) {
						_charts[otherPos].selectLeft(values.get(other));
						_charts[j].selectRight(values.get(lhDomain));	
					} else {
						_charts[j].selectLeft(values.get(lhDomain));
						_charts[otherPos].selectRight(values.get(other));
					}
				}
				
			}
			
			if(other == -1) {
				other = lhDomain;
				otherPos = j;
			}
		}
	}
}
