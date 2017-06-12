package sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
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
import sinbad2.resolutionphase.sensitivityanalysis.EProblem;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.nls.Messages;
import sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.SensitivityAnalysisView;

public class SATableContentProvider extends KTableNoScrollModel {
	
	private static final String RELATIVE = Messages.SATableContentProvider_relative;
	private static final String ABSOLUTE = Messages.SATableContentProvider_absolute;
	
	private int[][] _pairs;
	private String[] _alternatives;
	private String[] _criteria;
	private Double[][][] _values;
	private int[] _criticalCell;
	private String _typeDataSelected;
	
	private KTable _table;

	private final FixedCellRenderer _fixedRendererHeader = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS | SWT.BOLD);
	private final FixedCellRenderer _fixedRendererNoCalculated = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT);
	private final FixedCellRenderer _fixedRendererNA = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRendererRed = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	private final FixedCellRenderer _fixedRendererGreen = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	private final FixedCellRenderer _fixedRendererBoldRed = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS | SWT.BOLD);
	private final FixedCellRenderer _fixedRendererBoldGreen = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS | SWT.BOLD);
	
	private SensitivityAnalysisView _sensitivityAnalysisView;
	private SensitivityAnalysis _sensitivityAnalysis;
	
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
		
		_sensitivityAnalysisView = (SensitivityAnalysisView) getView(SensitivityAnalysisView.ID);
		
		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
	
		_alternatives = alternatives;
		_criteria = criteria;
		_values = values;
		
		_criticalCell = new int[2];
		
		if(_sensitivityAnalysis.getProblem() == EProblem.MOST_CRITICAL_CRITERION) { //$NON-NLS-1$
			computePairs();
		} else {
			computeAllPairs();
		}
		
		initialize();

		_fixedRendererHeader.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendererNoCalculated.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		
		_fixedRendererNA.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendererNA.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		_fixedRendererRed.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		Color red = new Color(Display.getCurrent(), 255, 137, 137);
		_fixedRendererRed.setBackground(red);
		_fixedRendererGreen.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		Color green = new Color(Display.getCurrent(), 137, 255, 176);
		_fixedRendererGreen.setBackground(green);
		
		_fixedRendererBoldRed.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendererBoldRed.setBackground(red);
		_fixedRendererBoldGreen.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendererBoldGreen.setBackground(green);
		
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
			String type = ""; //$NON-NLS-1$
			if(_kTableCombo.getControl() != null) {
				CCombo combo = (CCombo) _kTableCombo.getControl();
				type = combo.getText();
			}
			if(!type.isEmpty()) {
				_typeDataSelected = type;	
			}
			
			if(_typeDataSelected.equals(ABSOLUTE)) {
				if(_sensitivityAnalysis.getProblem() == EProblem.MOST_CRITICAL_CRITERION) { //$NON-NLS-1$
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
			} else if(_typeDataSelected.equals(RELATIVE)){
				if(_sensitivityAnalysis.getProblem() == EProblem.MOST_CRITICAL_CRITERION) { //$NON-NLS-1$
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
			
			_sensitivityAnalysisView.notifyChangeSATableListener(_typeDataSelected);
			
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
						erg = "N/F"; //$NON-NLS-1$
					} else if((Double) erg == Double.MAX_VALUE) {
						erg = "-"; //$NON-NLS-1$
					} else {
						if(_typeDataSelected.equals(ABSOLUTE)) {
							double value = Math.round((Double) erg * 1000d) / 1000d;
							erg = value;
						} else {
							double percent = Math.round((Double) erg * 1000d) / 1000d;
							erg = Double.toString(percent) + "%"; //$NON-NLS-1$
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
		Object value;
		double min = Double.MAX_VALUE;
		
		for(int col = 1; col < getColumnCount(); ++col) {
			for(int row = 1; row < getRowCount(); ++row) {
				value = doGetContentAt(col, row);
				if(value instanceof Double) {
					if(Math.abs((Double) value) < min) {
						min = Math.abs((Double) value);
						_criticalCell[0] = col;
						_criticalCell[1] = row;
					}
				}
			}
		}
		
		_table.redraw();
	}

	public KTableCellEditor doGetCellEditor(int col, int row) {
		
		if(col == 0 && row == 0) {
			_kTableCombo = new  MyOwnKTableCellEditorCombo();
			String[] valuesString = new String[2];
			valuesString[0] = Messages.SATableContentProvider_relative;
			valuesString[1] = Messages.SATableContentProvider_absolute;
			_kTableCombo.setItems(valuesString);
			
			return _kTableCombo;
		}
		
		return null;
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {}

	@Override
	public int doGetRowCount() {
		return _pairs.length + 1;
	}

	@Override
	public int getFixedHeaderRowCount() {
		return 0;
	}

	@Override
	public int doGetColumnCount() {
		return _criteria.length + 1;
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
				if(content.equals("N/F")) { //$NON-NLS-1$
					return _fixedRendererNA;
				} else if(content.equals("")) { //$NON-NLS-1$
					return _fixedRendererNoCalculated;
				} else if(!content.contains("%")) { //$NON-NLS-1$
					return _fixedRendererHeader;
				} else {
					String stringWithoutPercent = content.replace("%", ""); //$NON-NLS-1$ //$NON-NLS-2$
					double value = Double.parseDouble(stringWithoutPercent);
					if(value > 0) {
						if(_criticalCell[0] == col && _criticalCell[1] == row) {
							return _fixedRendererBoldRed;
						} else {
							return _fixedRendererRed;
						}
					} else {
						if(_criticalCell[0] == col && _criticalCell[1] == row) {
							return _fixedRendererBoldGreen;
						} else {
							return _fixedRendererGreen;
						}
					}
				}
			} else {
				double value = (Double) doGetContentAt(col, row);
				if(value > 0) {
					if(_criticalCell[0] == col && _criticalCell[1] == row) {
						return _fixedRendererBoldRed;
					} else {
						return _fixedRendererRed;
					}
				} else {
					if(_criticalCell[0] == col && _criticalCell[1] == row) {
						return _fixedRendererBoldGreen;
					} else {
						return _fixedRendererGreen;
					}
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
	
	private IViewPart getView(String id) {
		IViewPart view = null;
		IViewReference viewReferences[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			if (id.equals(viewReferences[i].getId())) {
				view = viewReferences[i].getView(false);
			}
		}
		return view;
	}
}
