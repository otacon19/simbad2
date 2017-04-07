package sinbad2.phasemethod.emergency.computeweights.ui.view.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.SWTX;

public class YMatrixEditableTable extends KTable {

	private YMatrixEditableContentProvider _yMatrixContentProvider;

	public YMatrixEditableTable(Composite parent) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT | SWTX.AUTO_SCROLL);
	}

	public void setModel(String[] experts, String[] criteria, Object[][] values, String title) {
		setNumRowsVisibleInPreferredSize(experts.length + 1);
		setNumColsVisibleInPreferredSize(criteria.length + 1);
		_yMatrixContentProvider = new YMatrixEditableContentProvider(this, experts, criteria, values, title);
		
		setModel(_yMatrixContentProvider);
	
		getParent().layout();
	}
	
}
