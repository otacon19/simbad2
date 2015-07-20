package sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.provider.DMTableContentProvider;
import de.kupzog.ktable.KTable;

public class DMTable extends KTable {

	public DMTable(Composite parent) {
		super(parent, SWT.BACKGROUND | SWT.FLAT);
	}
	
	public void setModel(String[] alternatives, String[] criteria, double[][] values) {
		
		setNumRowsVisibleInPreferredSize(alternatives.length + 1);
		setNumColsVisibleInPreferredSize(criteria.length + 1);
		setModel(new DMTableContentProvider(this, alternatives, criteria, values));
		
		getParent().layout();
	}

}
