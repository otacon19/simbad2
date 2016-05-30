package sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableNoScrollModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.editors.KTableCellEditorCombo;
import de.kupzog.ktable.editors.KTableCellEditorText;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;
import sinbad2.resolutionphase.sensitivityanalysis.EModel;
import sinbad2.resolutionphase.sensitivityanalysis.EProblem;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.IDisplayRankingChangeListener;
import sinbad2.resolutionphase.sensitivityanalysis.ui.ranking.RankingViewManager;

public class DMTableContentProvider extends KTableNoScrollModel implements IDisplayRankingChangeListener {
	
	private String[] _alternatives;
	private String[] _criteria;
	private double[][] _values;
	
	private KTable _table;
	
	private RankingViewManager _rankingViewManager;
	private SensitivityAnalysis _sensitivityAnalysis;

	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRenderersInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	
	class MyOwnKTableCellEditorCombo extends KTableCellEditorCombo {
		@Override
		public int getActivationSignals() {
			return SINGLECLICK;
		}
	}

	private MyOwnKTableCellEditorCombo _kTableCombo = new MyOwnKTableCellEditorCombo();

	
	public DMTableContentProvider(KTable table, String[] alternatives, String[] criteria, double[][] values, SensitivityAnalysis sensitivityAnalysis) {
		super(table);

		_table = table;
		
		_rankingViewManager = RankingViewManager.getInstance();
		_rankingViewManager.registerDisplayRankingChangeListener(this);
		
		_sensitivityAnalysis = sensitivityAnalysis;
		
		_alternatives = alternatives;
		_criteria = criteria;
		_values = values;
		
		initialize();

		_fixedRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	@Override
	public Object doGetContentAt(int col, int row) {
		if((col == 0) && (row == 0)) {
			
			if(_kTableCombo.getControl() != null) {
				CCombo combo = (CCombo) _kTableCombo.getControl();
				combo.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						
						_sensitivityAnalysis.setProblem(EProblem.values()[((CCombo) e.widget).getSelectionIndex()]);
						
						EModel model = _sensitivityAnalysis.getModel();
						if(_sensitivityAnalysis.getProblem() == EProblem.MOST_CRITICAL_CRITERION) {
							switch(model) {
							case WEIGHTED_SUM:
								_sensitivityAnalysis.computeWeightedSumModelCriticalCriterion();
								break;
							case WEIGHTED_PRODUCT:
								_sensitivityAnalysis.computeWeightedProductModelCriticalCriterion();
								break;
							case ANALYTIC_HIERARCHY_PROCESS:
								_sensitivityAnalysis.computeAnalyticHierarchyProcessModelCriticalCriterion();
								break;
							}
						} else {
							switch(model) {
							case WEIGHTED_SUM:
								_sensitivityAnalysis.computeWeightedSumModelCriticalMeasure();
								break;
							case WEIGHTED_PRODUCT:
								_sensitivityAnalysis.computeWeightedProductModelCriticalMeasure();
								break;
							case ANALYTIC_HIERARCHY_PROCESS:
								_sensitivityAnalysis.computeAnalyticHierarchyProcessModelCriticalMeasure();
								break;
							}
						}
					}
				});
			}
			
			return _sensitivityAnalysis.getProblem().getValue();
			
		} else {
			Object erg;
			try {
				if (col == 0) {
					erg = "C" + row; //$NON-NLS-1$
				} else if (row == 0) {
					erg = "A" + col; //$NON-NLS-1$
				} else {
					erg = _values[row - 1][col - 1];
				}
			} catch(Exception e) {
				erg = null;
			}
			return erg;
		}
	}
	
	private void refreshTable() {
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
			valuesString[0] = EProblem.MOST_CRITICAL_CRITERION.getValue();
			valuesString[1] = EProblem.MOST_CRITICAL_MEASURE.getValue();
			_kTableCombo.setItems(valuesString);
			
			return _kTableCombo;
		} else if(col != 0 && row != 0){
			return new KTableCellEditorText();
		}
		
		return null;
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {
		if(col != 0 && row != 0) {
			double v = Double.parseDouble((String) value);
			_values[row - 1][col - 1] = v;
			
			EModel model = _sensitivityAnalysis.getModel();
			if(_sensitivityAnalysis.getProblem() == EProblem.MOST_CRITICAL_CRITERION) {
				switch(model) {
				case WEIGHTED_SUM:
					_sensitivityAnalysis.computeWeightedSumModelCriticalCriterion();
					break;
				case WEIGHTED_PRODUCT:
					_sensitivityAnalysis.computeWeightedProductModelCriticalCriterion();
					break;
				case ANALYTIC_HIERARCHY_PROCESS:
					_sensitivityAnalysis.computeAnalyticHierarchyProcessModelCriticalCriterion();
					break;
				}
			} else {
				switch(model) {
				case WEIGHTED_SUM:
					_sensitivityAnalysis.computeWeightedSumModelCriticalMeasure();
					break;
				case WEIGHTED_PRODUCT:
					_sensitivityAnalysis.computeWeightedProductModelCriticalMeasure();
					break;
				case ANALYTIC_HIERARCHY_PROCESS:
					_sensitivityAnalysis.computeAnalyticHierarchyProcessModelCriticalMeasure();
					break;
				}
			}
		}
	}

	@Override
	public int doGetRowCount() {
		return _criteria.length + getFixedRowCount() + 1;
	}

	@Override
	public int getFixedHeaderRowCount() {
		return 0;
	}

	@Override
	public int doGetColumnCount() {
		return _alternatives.length + getFixedColumnCount();
	}

	@Override
	public int getFixedHeaderColumnCount() {
		return 1;
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
		return 60;
	}

	@Override
	public boolean isFixedCell(int col, int row) {
		return true;
	};

	@Override
	public boolean isHeaderCell(int col, int row) {
		return isFixedCell(col, row);
	}

	@Override
	public KTableCellRenderer doGetCellRenderer(int col, int row) {
		
		if((col < getFixedColumnCount()) || (row < getFixedRowCount() + 1)) {
			return _fixedRenderer;
		} else {
			return _fixedRenderersInTable;
		}
	}

	@Override
	public Point doBelongsToCell(int col, int row) {
		return null;
	}

	@Override
	public int getInitialColumnWidth(int column) {
		return 60;
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
			return _criteria[row - 1];
		} else if(row == 0) {
			return _alternatives[col - 1];
		} else {
			return _criteria[row - 1] + "/" + _alternatives[col - 1]; //$NON-NLS-1$
		}
	}
	
	public double[][] getValues() {
		return _values;
	}
	
	@Override
	public void displayRankingChange(Object ranking) {
		refreshTable();
	}
}
