package sinbad2.resolutionphase.frameworkstructuring.ui.view.elementassignments;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import sinbad2.element.ProblemElement;
import sinbad2.resolutionphase.frameworkstructuring.ui.view.elementassignments.provider.ElementAssignmentsTableContentProvider;
import de.kupzog.ktable.KTable;


public class ElementAssignmentsTable extends KTable {
	
	private ElementAssignmentsTableContentProvider _model;

	public ElementAssignmentsTable(Composite parent) {
		super(parent, SWT.NO_BACKGROUND | SWT.V_SCROLL | SWT.FLAT);
		setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_model = null;
	}
	
	public void setModel(ProblemElement element) {
		_model = new ElementAssignmentsTableContentProvider(this, element);
		setModel(_model);
		getParent().getParent().layout();
	}
	
	@Override
	public void dispose() {
		_model.dispose();
		super.dispose();
	}

}
