package sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.provider.DMTableContentProvider;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.SWTX;

public class DMTable extends KTable {
	
	private SensitivityAnalysis _sensitivityAnalysis;
	private DMTableContentProvider _provider;

	public DMTable(Composite parent, SensitivityAnalysis sensitivityAnalysis) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT | SWTX.AUTO_SCROLL);
		
		_sensitivityAnalysis = sensitivityAnalysis;
	}

	public void setModel(String[] alternatives, String[] criteria, double[][] values) {
		setNumRowsVisibleInPreferredSize(alternatives.length + 1);
		setNumColsVisibleInPreferredSize(criteria.length + 1);
		
		if(_provider != null) {
			if(_provider.getValues() != values) {
				_provider = new DMTableContentProvider(this, alternatives, criteria, values, _sensitivityAnalysis);
				setModel(_provider);
			}
		} else {
			_provider = new DMTableContentProvider(this, alternatives, criteria, values, _sensitivityAnalysis);
			setModel(_provider);
		}
		
		getParent().layout();
	}
	
	public DMTableContentProvider getProvider() {
		return _provider;
	}
}
