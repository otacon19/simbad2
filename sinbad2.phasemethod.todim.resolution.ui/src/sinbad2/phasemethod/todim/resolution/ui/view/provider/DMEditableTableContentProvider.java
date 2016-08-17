package sinbad2.phasemethod.todim.resolution.ui.view.provider;

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableNoScrollModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.editors.KTableCellEditorText;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;
import sinbad2.phasemethod.todim.resolution.ui.nls.Messages;
import sinbad2.phasemethod.todim.resolution.ui.view.DecisionMatrixEditableTable;

public class DMEditableTableContentProvider extends KTableNoScrollModel {
	
	private String[] _alternatives;
	private String[] _criteria;
	private Object[][] _values;
	private Pattern _p;
	
	private KTable _table;
	
	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRenderersInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	
	public DMEditableTableContentProvider(KTable table, String[] alternatives, String[] criteria, Object[][] values) {
		super(table);

		_table = table;
		
		_alternatives = alternatives;
		_criteria = criteria;
		_values = values;
		
		_p = Pattern.compile(("[(]\\d{1}\\.?\\d*\\,\\d{1}\\.?\\d*\\,\\d{1}\\.?\\d*\\,\\d{1}\\.?\\d*[)]")); //$NON-NLS-1$
		
		initialize();

		_fixedRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	@Override
	public Object doGetContentAt(int col, int row) {
	
		Object erg;
		
		try {
			if(col == 0 && row == 0) {
				erg = Messages.DMTableContentProvider_Consensus_matrix;
			} else if (col == 0) {
				erg = "A" + row; //$NON-NLS-1$
			} else if (row == 0) {
				erg = "C" + col; //$NON-NLS-1$
			} else {
				if(_values[row - 1][col - 1] == null) {
					erg = ""; //$NON-NLS-1$
				} else if(_values[row - 1][col - 1] instanceof Double ){
					erg = Double.toString(((Double) _values[row - 1][col - 1]));
				} else {
					erg = _values[row - 1][col - 1];
				}
			}
		} catch(Exception e) {
			erg = null;
		}
		
		return erg;
	}

	public KTableCellEditor doGetCellEditor(int col, int row) {
		if(col != 0 && row != 0) {
			return new KTableCellEditorText();
		} else {
			return null;
		}
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {
		if(value instanceof Double) {
			_values[row - 1][col - 1] = (Double) value;
		} else {
			boolean format = _p.matcher((String) value).matches(); 
			String noParenthesis = ((String) value).replace("(", ""); //$NON-NLS-1$ //$NON-NLS-2$
			noParenthesis = noParenthesis.replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$
			String[] limits = noParenthesis.split(","); //$NON-NLS-1$
			
			if(format && checkLimits(limits)) {
				_values[row - 1][col - 1] = (String) value;
			}
			
			if(checkConsensusMatrix()) {
				((DecisionMatrixEditableTable) _table).setCompleted(true);
			} else {
				((DecisionMatrixEditableTable) _table).setCompleted(false);
			}
		}
	}

	private boolean checkLimits(String[] limits) {
		
		for(int l1 = 0; l1 < limits.length; ++l1) {
			for(int l2 = l1 + 1; l2 < limits.length; ++l2) {
				if(Double.parseDouble(limits[l1]) > Double.parseDouble(limits[l2])) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean checkConsensusMatrix() {
		
		for(int a = 0; a < _alternatives.length; ++a) {
			for(int c = 0; c < _criteria.length; ++c) {
				if(!_p.matcher(((String) _values[a][c])).matches()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int doGetRowCount() {
		return _alternatives.length + 1;
	}

	@Override
	public int getFixedHeaderRowCount() {
		return 0;
	}

	@Override
	public int doGetColumnCount() {
		return _criteria.length + getFixedColumnCount();
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
			return _alternatives[row - 1];
		} else if(row == 0) {
			return _criteria[col - 1];
		} else {
			return _alternatives[row - 1] + "/" + _criteria[col - 1]; //$NON-NLS-1$
		}
	}
}
