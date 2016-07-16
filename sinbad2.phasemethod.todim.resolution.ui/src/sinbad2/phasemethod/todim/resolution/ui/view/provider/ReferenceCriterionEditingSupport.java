package sinbad2.phasemethod.todim.resolution.ui.view.provider;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

public class ReferenceCriterionEditingSupport extends EditingSupport {

	private TableViewer _viewer;
	
	public ReferenceCriterionEditingSupport(TableViewer viewer) {
		super(viewer);


		_viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor(_viewer.getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		String result = (String) ((Object[]) element)[2];
		return new Boolean(result);
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(value == null) {
			return;
		} else if(value instanceof String) {
			if (((String) value).isEmpty()) {
				return;
			}
		} else {
			return;
		}
	}
}
