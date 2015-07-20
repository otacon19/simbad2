package sinbad2.resolutionphase.sensitivityanalysis.ui.decisionmaking.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableNoScrollModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;


public class DMTableContentProvider extends KTableNoScrollModel {
	
	private String[] _alternatives;
	private String[] _criteria;
	private double[][] _values;
	
	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRendersInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);

	public DMTableContentProvider(KTable table, String[] alternatives, String[] criteria, double[][] values) {
		super(table);
		
		_alternatives = alternatives;
		_criteria = criteria;
		_values = values;
		
		initalize();
		
		_fixedRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendersInTable.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendersInTable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	private void initalize() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Object doGetContentAt(int col, int row) {
		
		if((col == 0) && (row == 0)) {
			return "";
		} else {
			Object erg;
			
			try {
				if(col == 0) {
					erg = "A" + row;
				} else if(row == 0) {
					erg = "C" + col;
				} else {
					erg = _values[row - 1][col - 1];
				}
			} catch(Exception e) {
				erg = null;
			}
			
			return erg;
		}
	}
	
	@Override
	public KTableCellEditor doGetCellEditor(int col, int row) {
		return null;
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {	}

	@Override
	public int doGetColumnCount() {
		return _criteria.length + getFixedColumnCount();
	}

	@Override
	public int doGetRowCount() {
		return _alternatives.length + getFixedRowCount();
	}
	
	@Override
	public int getFixedHeaderRowCount() {
		return 1;
	}
	
	@Override
	public int getFixedHeaderColumnCount() {
		return 1;
	}

	@Override
	public int getFixedSelectableColumnCount() {
		return 0;
	}

	@Override
	public int getFixedSelectableRowCount() {
		return 0;
	}

	@Override
	public int getInitialFirstRowHeight() {
		return 25;
	}
	
	@Override
	public int getRowHeightMinimum() {
		return 60;
	}

	@Override
	public boolean isColumnResizable(int column) {
		return false;
	}

	@Override
	public boolean isRowResizable(int row) {
		return false;
	}
	
	@Override
	public boolean isFixedCell(int col, int row) {
		return true;
	}
	
	@Override
	public boolean isHeaderCell(int col, int row) {
		return isFixedCell(col, row);
	}
	
	@Override
	public KTableCellRenderer doGetCellRenderer(int col, int row) {
		
		if((col < getFixedColumnCount()) || (row < getFixedRowCount())) {
			return _fixedRenderer;
		} else {
			return _fixedRendersInTable;
		}
	}
	
	@Override
	public Point doBelongsToCell(int col, int row) {
		return null;
	}


	@Override
	public int getInitialColumnWidth(int col) {
		return 60;
	}

	@Override
	public int getInitialRowHeight(int row) {
		return 25;
	}
	
	@Override
	public String doGetTooltipAt(int col, int row) {
		
		if((col == 0) && (row == 0)) {
			return "";
		} else if(col < getFixedColumnCount()) {
			return _alternatives[row - 1];
		} else if(row < getFixedRowCount()) {
			return _criteria[col - 1];
		} else {
			return _alternatives[row - 1] + "/" + _criteria[col - 1];
		}
	}

}
