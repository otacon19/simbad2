package sinbad2.domain.ui.jfreechart;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

import sinbad2.domain.Domain;

public abstract class DomainChart {
	
	protected Domain _domain;
	protected JFreeChart _chart;
	protected ChartComposite _chartComposite;
	
	public ChartComposite getChartComposite() {
		return _chartComposite;
	}
	
	public void setCharComposite(ChartComposite chartComposite) {
		_chartComposite = chartComposite;
	}
	
	public Domain getDomain() {
		return _domain;
	}
	
	public void setDomain(Domain domain) {
		_domain = domain;
		refreshChart();
	}
	
	public abstract void refreshChart();
	
	public abstract void setSelection(Object selection);
	
	public abstract void initialize(Domain domain, Composite container, int width, int height, int style);
	
	public abstract void displayRanking(Object ranking);
}
