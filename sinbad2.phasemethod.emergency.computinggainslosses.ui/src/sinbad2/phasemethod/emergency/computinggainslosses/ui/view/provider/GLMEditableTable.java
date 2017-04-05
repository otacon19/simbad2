package sinbad2.phasemethod.emergency.computinggainslosses.ui.view.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.SWTX;

public class GLMEditableTable extends KTable {
	
	private GLMEditableContentProvider _glmTableContentProvider;

	public GLMEditableTable(Composite parent) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT | SWTX.AUTO_SCROLL);
	}

	public void setModel(String[] alternatives, String[] criteria, Object[][] values, String titleTable) {
		setNumRowsVisibleInPreferredSize(alternatives.length + 1);
		setNumColsVisibleInPreferredSize(criteria.length + 1);
		_glmTableContentProvider = new GLMEditableContentProvider(this, alternatives, criteria, values, titleTable);
		
		setModel(_glmTableContentProvider);
	
		getParent().layout();
	}
	
}
