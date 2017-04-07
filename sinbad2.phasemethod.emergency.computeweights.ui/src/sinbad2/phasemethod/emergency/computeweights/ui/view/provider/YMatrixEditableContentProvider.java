package sinbad2.phasemethod.emergency.computeweights.ui.view.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableNoScrollModel;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;

public class YMatrixEditableContentProvider extends KTableNoScrollModel {

	private String[] _experts;
	private String[]_criteria;
	private Object[][] _values;
	private String _title;
	
	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRenderersInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);

	public YMatrixEditableContentProvider(KTable table, String[] experts, String[] criteria, Object[][] values, String title) {
		super(table);
		
		_experts = experts;
		_criteria = criteria;
		_values = values;
		_title = title;
		
		initialize();
	}

	
	@Override
	public Object doGetContentAt(int col, int row) {
		Object erg;
		
		try {
			if(col == 0 && row == 0) {
				erg = _title;
			} else if (col == 0) {
				erg = "E" + row; //$NON-NLS-1$
			} else if (row == 0) {
				erg = "C" + col; //$NON-NLS-1$
			} else {
				if(_values[row - 1][col - 1] == null) {
					erg = ""; //$NON-NLS-1$
				} else if(_values[row - 1][col - 1] instanceof Double) {
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

	@Override
	public void doSetContentAt(int col, int row, Object value) {
		if(value != null) {
			_values[row - 1][col - 1] = value;
		}
	}

	@Override
	public int doGetRowCount() {
		return _experts.length + getFixedRowCount();
	}

	@Override
	public int getFixedHeaderRowCount() {
		return 1;
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
		if(col == 0 || row == 0) {
			return true;
		} else {
			return false;
		}
	}

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
			return _experts[row - 1];
		} else if(row == 0) {
			return _criteria[col - 1];
		} else {
			return _experts[row - 1] + "/" + _criteria[col - 1]; //$NON-NLS-1$
		}
	}


	@Override
	public KTableCellEditor doGetCellEditor(int arg0, int arg1) {
		return null;
	}
}
