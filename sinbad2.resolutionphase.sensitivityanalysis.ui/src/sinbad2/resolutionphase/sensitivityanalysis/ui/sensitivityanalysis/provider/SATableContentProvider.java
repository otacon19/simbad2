package sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableNoScrollModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.editors.KTableCellEditorCombo;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;
import sinbad2.core.workspace.Workspace;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.DMTable;
import sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.DecisionMakingView;
import sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.provider.DMTableContentProvider;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.IChangeSATableValues;

public class SATableContentProvider extends KTableNoScrollModel {
	
	private static final String RELATIVE = "RELATIVE";
	private static final String ABSOLUTE = "ABSOLUTE";
	
	private String _typeDataSelected;
	
	private int[][] _pairs;
	private String[] _alternatives;
	private String[] _criteria;
	private Double[][][] _values;
	
	private KTable _table;

	private final FixedCellRenderer _fixedRendererHeader = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS | SWT.BOLD);
	private final FixedCellRenderer _fixedRendererNA = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRendererRed = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	private final FixedCellRenderer _fixedRendererGreen = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	
	private DecisionMakingView _decisionMakingView;
	private SensitivityAnalysis _sensitivityAnalysis;
	
	private static List<IChangeSATableValues> _listeners = new LinkedList<IChangeSATableValues>();
	
	class MyOwnKTableCellEditorCombo extends KTableCellEditorCombo {
		@Override
		public int getActivationSignals() {
			return SINGLECLICK;
		}
	}

	private MyOwnKTableCellEditorCombo _kTableCombo = new MyOwnKTableCellEditorCombo();
	
	public SATableContentProvider(KTable table, String[] alternatives, String[] criteria, Double[][][] values) {
		super(table);
		
		_table = table;
		
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for(int i = 0; i < viewReferences.length; i++) {
			if(DecisionMakingView.ID.equals(viewReferences[i].getId())) {
				_decisionMakingView = (DecisionMakingView) viewReferences[i].getView(false);
			}
		}
		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);

		_alternatives = alternatives;
		_criteria = criteria;
		_values = values;
		
		if(_decisionMakingView.getTable().getProvider().getTypeProblemSelected().endsWith("MCC")) {
			computePairs();
		} else {
			computeAllPairs();
		}
		
		initialize();

		_fixedRendererHeader.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendererNA.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendererRed.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		Color red = new Color(Display.getCurrent(), 255, 137, 137);
		_fixedRendererRed.setBackground(red);
		_fixedRendererGreen.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		Color green = new Color(Display.getCurrent(), 137, 255, 176);
		_fixedRendererGreen.setBackground(green);
		
		_typeDataSelected = ABSOLUTE;
	}

	private void computePairs() {
		int pairs = (_alternatives.length * (_alternatives.length - 1)) / 2;
		_pairs = new int[pairs][2];
		int pair = 0;
		for(int i = 0; i < _alternatives.length; i++) {
			for(int j = (i + 1); j < _alternatives.length; j++) {
				_pairs[pair][0] = i;
				_pairs[pair][1] = j;
				pair++;
			}
		}
	}

	private void computeAllPairs() {
		int pairs = (_alternatives.length *_alternatives.length) - _alternatives.length;
		_pairs = new int[pairs][2];
		int pair = 0;
		for(int i = 0; i < _alternatives.length; i++) {
			for(int j = 0; j < _alternatives.length; j++) {
				if(i != j) {
					_pairs[pair][0] = i;
					_pairs[pair][1] = j;
					pair++;
				}
			}
		}
	}
	
	@Override
	public Object doGetContentAt(int col, int row) {
		
		if((col == 0) && (row == 0)) {
			String type = "";
			if(_kTableCombo.getControl() != null) {
				CCombo combo = (CCombo) _kTableCombo.getControl();
				type = combo.getText();
			}
			if(!type.isEmpty()) {
				_typeDataSelected = type;	
			}
			
			DMTable dmTable = _decisionMakingView.getTable();
			DMTableContentProvider provider = dmTable.getProvider();
			if(_typeDataSelected.equals(ABSOLUTE)) {
				if(provider.getTypeProblemSelected().equals("MCC")) {
					if(_values != _sensitivityAnalysis.getMinimumAbsoluteChangeInCriteriaWeights()) {
						_values = _sensitivityAnalysis.getMinimumAbsoluteChangeInCriteriaWeights();
						refreshTable();
					}
				} else {
					if(_values != _sensitivityAnalysis.getAbsoluteThresholdValues()) {
						_values = _sensitivityAnalysis.getAbsoluteThresholdValues();
						refreshTable();
					}
				}
			} else {
				if(provider.getTypeProblemSelected().equals("MCC")) {
					if(_values != _sensitivityAnalysis.getMinimumPercentChangeInCriteriaWeights()) {
						_values = _sensitivityAnalysis.getMinimumPercentChangeInCriteriaWeights();
						refreshTable();
					}
				} else {
					if(_values != _sensitivityAnalysis.getRelativeThresholdValues()) {
						_values = _sensitivityAnalysis.getRelativeThresholdValues();
						refreshTable();
					}
				}
			}
			
			notifyChangeSATableListener(_typeDataSelected);
			
			return _typeDataSelected;
		} else {
			Object erg;

			try {
				if(col == 0) {
					erg = "A" + (_pairs[row - 1][0] + 1) + "-" + "A" + (_pairs[row - 1][1] + 1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else if (row == 0) {
					erg = "C" + col; //$NON-NLS-1$
				} else {
					erg = _values[_pairs[row - 1][0]][_pairs[row - 1][1]][col - 1];
					if(erg == null) {
						erg = "N/A"; //$NON-NLS-1$
					} else {
						if(_typeDataSelected.equals(ABSOLUTE)) {
							double value = Math.round((Double) erg * 1000d) / 1000d;
							erg = value;
						} else {
							double percent = Math.round((Double) erg * 1000d) / 1000d;
							erg = Double.toString(percent) + "%";
						}
					}
				}
			} catch(Exception e) {
				erg = null;
			}
			
			return erg;
		}
	}

	public void refreshTable() {
		for(int col = 1; col < getColumnCount(); ++col) {
			for(int row = 1; row < getRowCount(); ++row) {
				doGetContentAt(col, row);
			}
		}
		
		_table.redraw();
	}

	public KTableCellEditor doGetCellEditor(int col, int row) {
		if(col == 0 && row == 0) {
			_kTableCombo = new  MyOwnKTableCellEditorCombo();
			String[] valuesString = new String[2];
			valuesString[0] = RELATIVE;
			valuesString[1] = ABSOLUTE;
			_kTableCombo.setItems(valuesString);
			
			return _kTableCombo;
		}
		
		return null;
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {}

	@Override
	public int doGetRowCount() {
		return _pairs.length + getFixedRowCount() + 1;
	}

	@Override
	public int getFixedHeaderRowCount() {
		return 0;
	}

	@Override
	public int doGetColumnCount() {
		return _criteria.length + getFixedColumnCount() + 1;
	}

	@Override
	public int getFixedHeaderColumnCount() {
		return 0;
	}

	@Override
	public int getFixedSelectableRowCount() {
		return 0;
	}

	@Override
	public int getFixedSelectableColumnCount() {
		return 0;
	}

	@Override
	public boolean isColumnResizable(int col) {
		return false;
	}
	
	@Override
	public int getInitialFirstRowHeight() {
		return 25;
	}

	@Override
	public boolean isRowResizable(int row) {
		return false;
	}

	@Override
	public int getRowHeightMinimum() {
		return 65;
	}

	@Override
	public boolean isFixedCell(int col, int row) {
		return true;
	};

	@Override
	public boolean isHeaderCell(int col, int row) {
		return true;
	}

	@Override
	public KTableCellRenderer doGetCellRenderer(int col, int row) {
		
		if((col < getFixedColumnCount()) || (row < getFixedRowCount())) {
			return _fixedRendererHeader;
		} else {
			if((doGetContentAt(col, row) instanceof String)) {
				String content = (String) doGetContentAt(col, row);
				if(content.equals("N/A")) {
					return _fixedRendererNA;
				}else if(!content.contains("%")) {
					return _fixedRendererHeader;
				} else {
					String stringWithoutPercent = content.replace("%", "");
					double value = Double.parseDouble(stringWithoutPercent);
					if(value > 0) {
						return _fixedRendererRed;
					} else {
						return _fixedRendererGreen;
					}
				}
			} else {
				double value = (Double) doGetContentAt(col, row);
				if(value > 0) {
					return _fixedRendererRed;
				} else {
					return _fixedRendererGreen;
				}
			}
		}
	}

	@Override
	public Point doBelongsToCell(int col, int row) {
		return null;
	}

	@Override
	public int getInitialColumnWidth(int column) {
		return 65;
	}

	@Override
	public int getInitialRowHeight(int arg0) {
		return 25;
	}

	@Override
	public String doGetTooltipAt(int col, int row) {
		
		if((col == 0) && (row == 0)) {
			return ""; //$NON-NLS-1$
		} else if(col == 0) {
			return _alternatives[_pairs[row - 1][0]] + "-" + _alternatives[_pairs[row - 1][1]]; //$NON-NLS-1$
		} else if(row == 0) {
			return _criteria[col - 1];
		} else {
			return _alternatives[_pairs[row - 1][0]] + "-" + _alternatives[_pairs[row - 1][1]] + "/" + _criteria[col - 1]; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	public void registerNotifyChangeSATableListener(IChangeSATableValues listener) {
		_listeners.add(listener);
	}

	public void unregisterNotifyChangeSATableListener(IChangeSATableValues listener) {
		_listeners.remove(listener);
	}

	public void notifyChangeSATableListener(String type) {
		for (IChangeSATableValues listener : _listeners) {
			listener.notifyChangeSATableValues(type);
		}
	}
}
