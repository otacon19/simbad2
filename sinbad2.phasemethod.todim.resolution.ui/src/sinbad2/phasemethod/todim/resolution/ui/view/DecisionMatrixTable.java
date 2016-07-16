package sinbad2.phasemethod.todim.resolution.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.SWTX;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.DMTableContentProvider;

public class DecisionMatrixTable extends KTable {
	
	public DecisionMatrixTable(Composite parent) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT | SWTX.AUTO_SCROLL);
	}

	public void setModel(String[] alternatives, String[] criteria, Double[][] values) {
		setNumRowsVisibleInPreferredSize(alternatives.length + 1);
		setNumColsVisibleInPreferredSize(criteria.length + 1);
		setModel(new DMTableContentProvider(this, alternatives, criteria, values));
	
		getParent().layout();
	}
}
