package sinbad2.phasemethod.todim.resolution.ui.view.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.phasemethod.todim.resolution.ui.nls.Messages;

public class WeightsExpertsDialog extends Dialog implements PropertyChangeListener {
	
	public static final int SAVE = 100;

	private final Color lightGreen = new Color(null, new RGB(230, 255, 230));
	private final Color lightRed = new Color(null, new RGB(255, 230, 230));
	
	private TableViewer _expertsWeightsViewer;
	private WeightsExpertsEditingSupport _editingSupport;

	private ProblemElementsSet _elementsSet;
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public WeightsExpertsDialog(Shell parent) {
		super(parent);
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
	}
	
	public WeightsExpertsEditingSupport getEditingSupport() {
		return _editingSupport;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, true));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_expertsWeightsViewer = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		_expertsWeightsViewer.getTable().setHeaderVisible(true);
		_expertsWeightsViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		createCompleteWeightsContentProvider(_expertsWeightsViewer);
		
		TableViewerColumn expertsColumn = new TableViewerColumn(_expertsWeightsViewer, SWT.NONE);
		expertsColumn.getColumn().setText(Messages.WeightsExpertsDialog_Expert);
		expertsColumn.getColumn().pack();
		expertsColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) ((Object[]) element)[0];
			}
		});
		
		_editingSupport = new WeightsExpertsEditingSupport(_expertsWeightsViewer, _elementsSet.getExperts(), propertyChangeSupport);
		
		TableViewerColumn weightsColumn = new TableViewerColumn(_expertsWeightsViewer, SWT.NONE);
		weightsColumn.getColumn().setText(Messages.WeightsExpertsDialog_Weight);
		weightsColumn.getColumn().pack();
		weightsColumn.setEditingSupport(_editingSupport);
		weightsColumn.setLabelProvider(new ColumnLabelProvider() {
			
			@Override
			public String getText(Object element) {
				Double w = (Double) ((Object[]) element)[1];
				return Double.toString(w);
			}

			@Override
			public Color getBackground(Object element) {
				if(validate()) {
					return lightGreen;
				}
				
				return lightRed;
			}
		});
		
		addPropertyChangeListener("weight", this); //$NON-NLS-1$
		
		setInput();
		
		return container;
	}

	private void createCompleteWeightsContentProvider(TableViewer viewer) {

		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			@Override
			public void dispose() {}
			
			@Override
			public Object[] getElements(Object inputElement) {
				List<Object[]> result = new LinkedList<Object[]>();
				Object[] aux;
				double value;
				double weight;
				for(int i = 0; i < _elementsSet.getExperts().size(); i++) {
					aux = new Object[2];
					aux[0] = _elementsSet.getExperts().get(i).getCanonicalId();
					weight = _editingSupport.getWeights().get(_elementsSet.getExperts().get(i).getCanonicalId());
					value = weight;
					aux[1] = Double.valueOf(value);
			
					result.add(aux);
				}

				return ((List<Object[]>) result).toArray(new Object[0][0]);
			}
		});
	}
	
	private void setInput() {
		List<Object[]> input = new LinkedList<Object[]>();
		
		Map<String, Double> weights = _editingSupport.getWeights();
		for(String expert: weights.keySet()) {
			Object[] data = new Object[2];
			data[0] = expert;
			data[1] = weights.get(expert);
			
			input.add(data);
 		}
		
		_expertsWeightsViewer.setInput(input);
		_expertsWeightsViewer.refresh();
	}
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if(evt == null) {
			return;
		}

		if("weight".equals(evt.getPropertyName())) { //$NON-NLS-1$
			Button SaveButton = getButton(SAVE);
			if (SaveButton != null) {
				SaveButton.setEnabled(validate());
			}
		}
	}
	
	private boolean validate() {
		return _editingSupport.validate();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, CANCEL, Messages.WeightsExpertsDialog_Cancel, false);
		createButton(parent, SAVE, Messages.WeightsExpertsDialog_Save, true);
		getButton(SAVE).setEnabled(false);;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		removePropertyChangeListener(this);
		close();
	}
}
