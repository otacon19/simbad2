package sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.provider.DMTableContentProvider;
import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellSelectionListener;
import de.kupzog.ktable.SWTX;

public class DMTable extends KTable {
	
	private SensitivityAnalysis _sensitivityAnalysis;

	public DMTable(Composite parent, SensitivityAnalysis sensitivityAnalysis) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT | SWTX.AUTO_SCROLL);
		
		_sensitivityAnalysis = sensitivityAnalysis;
	}

	public void setModel(String[] alternatives, String[] criteria, double[][] values) {
		setNumRowsVisibleInPreferredSize(alternatives.length + 1);
		setNumColsVisibleInPreferredSize(criteria.length + 1);
		setModel(new DMTableContentProvider(this, alternatives, criteria, values, _sensitivityAnalysis));

		getParent().layout();
		
		addCellSelectionListener(new KTableCellSelectionListener() {
			public void cellSelected(int col, int row, int statemask) {}

			public void fixedCellSelected(int col, int row, int statemask) {}	
		});
	}
}
