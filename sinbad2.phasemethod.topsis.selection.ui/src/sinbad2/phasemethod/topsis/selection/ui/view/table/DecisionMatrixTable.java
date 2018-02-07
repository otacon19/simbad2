package sinbad2.phasemethod.topsis.selection.ui.view.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTable;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.view.provider.DecisionMatrixContentProvider;

public class DecisionMatrixTable extends KTable {
	
	private DecisionMatrixContentProvider _model;

	public DecisionMatrixTable(Composite parent) {
		super(parent, SWT.NO_BACKGROUND | SWT.FLAT | SWT.V_SCROLL);
		setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_model = null;
	}

	public void setModel(SelectionPhase selectionPhase) {
		_model = new DecisionMatrixContentProvider(this, selectionPhase);
		setModel(_model);
		getParent().getParent().layout();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
