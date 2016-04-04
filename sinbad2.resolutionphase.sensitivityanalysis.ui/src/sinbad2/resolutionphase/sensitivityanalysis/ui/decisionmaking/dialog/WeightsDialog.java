package sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.dialog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.element.ProblemElement;

public class WeightsDialog extends Dialog implements PropertyChangeListener {

	private final Color lightGreen = new Color(null, new RGB(230, 255, 230));
	private final Color lightRed = new Color(null, new RGB(255, 230, 230));

	public static final int SAVE = 100;
	public static final int CANCEL = 101;
	public static final int CANCEL_ALL = 102;

	public static final int SIMPLE = 1;
	public static final int COMPLEX = 2;

	private int _type;
	private String _title;
	private Map<String, List<Double>> _weights;
	private List<ProblemElement> _primary;
	private int _cols;
	private Input _input;
	private double _sumGeneral;
	private TableViewer _generalWeightsViewer;

	private static double round(double value) {
		double error = 0.00001;

		double result = value + error;
		result *= 10000;
		result = Math.round(result);
		result /= 10000;

		return result;
	}

	private class WeightEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final int col;
		private final Input input;

		public WeightEditingSupport(TableViewer viewer, Input input, int col) {
			super(viewer);
			
			this.viewer = viewer;
			this.input = input;
			this.col = col;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			Double result = (Double) ((Object[]) element)[col];
			return result.toString();
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

			double newValue;

			try {
				newValue = (Double.parseDouble((String) value));
				if((newValue < 0) || (newValue > 1)) {
					MessageDialog.openError(null, "Invalid range", "Invalid range");
					return;
				}
			} catch (Exception e) {
				MessageDialog.openError(null, "Invalid value", "Invalid value");
				return;
			}

			input.setWeight(col - 1, element, newValue);
			viewer.setInput(input);
		}
	}

	private class Input {

		public final static String WEIGHT = "weight";

		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

		public Input() {

			List<Double> auxWeights;

			if(_weights == null) {
				_weights = new HashMap<String, List<Double>>();

				auxWeights = new LinkedList<Double>();
				for (int i = 0; i < _primary.size(); i++) {
					auxWeights.add(new Double(0));
				}
				_weights.put(null, auxWeights);
				_sumGeneral = 0;

			} else {
				
				auxWeights = _weights.get(null);
				for(Double weight : auxWeights) {
					_sumGeneral += weight;
				}
				_sumGeneral = round(_sumGeneral);
			}
		}

		public String getKey(Object element) {
			Object aux = ((Object[]) element)[0];

			if (aux instanceof ProblemElement) {
				return ((ProblemElement) aux).getCanonicalId();
			} else {
				return null;
			}
		}

		public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
			propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		}

		public void removePropertyChangeListener(PropertyChangeListener listener) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}

		public void setWeight(int col, Object element, double newWeight) {

			double oldWeight;
			List<Double> auxWeights;

			String key = getKey(element);
			auxWeights = _weights.get(key);

			oldWeight = auxWeights.get(col);
			auxWeights.set(col, newWeight);

			if(key == null) {
				_sumGeneral += (newWeight - oldWeight);
				_sumGeneral = round(_sumGeneral);
			}

			propertyChangeSupport.firePropertyChange(WEIGHT, null, null);
		}
	}

	public WeightsDialog(Shell parentShell, List<ProblemElement> primary, int type, String elementType, String elementId) {
		super(parentShell);
		
		_primary = primary;
		_cols = _primary.size();
		_sumGeneral = 0;

		_type = type;
		_title = "Values for" + " " + elementId + " (" + elementType + ")";

	}

	public WeightsDialog(Shell parentShell, List<ProblemElement> primary, Map<String, List<Double>> weights, int type, String elementType, String elementId) {
		this(parentShell, primary, type, elementType, elementId);

		if(weights != null) {
			_weights = new HashMap<String, List<Double>>();
			List<Double> auxWeights1;
			List<Double> auxWeights2;

			for(String key : weights.keySet()) {
				auxWeights1 = weights.get(key);
				auxWeights2 = new LinkedList<Double>();
				for (Double value : auxWeights1) {
					auxWeights2.add(Double.valueOf(value));
				}
				_weights.put(key, auxWeights2);
			}
		}	
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 1;

		Label titleLabel = new Label(container, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		titleLabel.setLayoutData(gridData);
		titleLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
		titleLabel.setText(_title);

		_input = new Input();
		_generalWeightsViewer = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		createGeneralWeightsContentProvider(_generalWeightsViewer);

		createWeightsColumns(_generalWeightsViewer, _input, true);

		Table table = _generalWeightsViewer.getTable();
		gridData = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		gridData.heightHint = 32;
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		_generalWeightsViewer.setInput(_input);

		for(TableColumn column : _generalWeightsViewer.getTable().getColumns()) {
			column.pack();
			if(column.getWidth() < 50) {
				column.setWidth(50);
			}
		}

		int width = 0;
		for(TableColumn column : _generalWeightsViewer.getTable().getColumns()) {
			width += column.getWidth();
		}

		if(width < 325) {
			TableColumn[] columns = _generalWeightsViewer.getTable().getColumns();

			int toAdd = 325 - width;
			toAdd /= (columns.length - 1);

			boolean initial = true;
			for(TableColumn column : _generalWeightsViewer.getTable().getColumns()) {
				if(!initial) {
					column.setWidth(column.getWidth() + toAdd);
				} else {
					initial = false;
				}
			}
		}

		_input.addPropertyChangeListener(Input.WEIGHT, this);
		
		container.pack();
		container.addPaintListener(new PaintListener() {
		
			@Override
			public void paintControl(PaintEvent e) {
				modifySelection();
			}
		});

		return container;
	}

	private void modifySelection() {
		validate();
	}

	private void createGeneralWeightsContentProvider(TableViewer viewer) {

		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			@Override
			public void dispose() {}

			@Override
			public Object[] getElements(Object inputElement) {
				Object[] result = new Object[1];
				Object[] aux;
				double sum;
				double value;
				aux = new Object[_cols + 2];
				aux[0] = "Weight";
				sum = 0;
				for(int j = 0; j < _cols; j++) {
					value = _weights.get(null).get(j);
					aux[j + 1] = value;
					sum += value;
				}
				sum = round(sum);
				aux[_cols + 1] = sum;
				result[0] = aux;

				return result;
			}
		});
	}

	private void createWeightsColumns(TableViewer viewer, Input input, final boolean simple) {

		TableViewerColumn col;

		col = new TableViewerColumn(viewer, SWT.RIGHT);
		col.getColumn().setWidth(70);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(simple) {
					String result = (String) ((Object[]) element)[0];
					return result;
				} else {
					ProblemElement pe = (ProblemElement) ((Object[]) element)[0];
					return pe.getCanonicalId();
				}
			}
		});

		for(int i = 0; i < _cols; i++) {
			col = new TableViewerColumn(viewer, SWT.CENTER);
			col.getColumn().setWidth(70);
			col.getColumn().setText(_primary.get(i).getCanonicalId());
			createColumnLabelProvider(col, i + 1);
			col.setEditingSupport(new WeightEditingSupport(viewer, input, i + 1));
		}

		col = new TableViewerColumn(viewer, SWT.CENTER);
		col.getColumn().setWidth(70);
		col.getColumn().setText("Sum");
		col.setLabelProvider(new ColumnLabelProvider() {
		
			@Override
			public String getText(Object element) {
				Double w = (Double) ((Object[]) element)[_cols + 1];
				return Double.toString(w);
			}

			@Override
			public Color getBackground(Object element) {
				Double w = (Double) ((Object[]) element)[_cols + 1];
				return (w == 1) ? lightGreen : lightRed;
			}
		});
	}

	private void createColumnLabelProvider(TableViewerColumn col, final int pos) {
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				Double w = (Double) ((Object[]) element)[pos];
				return Double.toString(w);
			}
		});
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if (_type == COMPLEX) {
			createButton(parent, CANCEL_ALL, "Cancel all", false);
		}
		createButton(parent, CANCEL, "Cancel", false);
		createButton(parent, SAVE, "Save", true);
	}

	public Map<String, List<Double>> getWeights() {
		Map<String, List<Double>> result;

		result = _weights;
	
		return result;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		_input.removePropertyChangeListener(this);
		close();
	}

	private void validate() {

		boolean valid = true;
		
		valid = (_sumGeneral == 1);

		Button SaveButton = getButton(SAVE);
		if (SaveButton != null) {
			SaveButton.setEnabled(valid);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if(evt == null) {
			return;
		}

		if(Input.WEIGHT.equals(evt.getPropertyName())) {
			validate();
		}
	}
	
}
