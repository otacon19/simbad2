package sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis.provider;

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

public class SATableContentProvider extends KTableNoScrollModel {
	
	private int[][] _pairs;
	private String[] _alternatives;
	private String[] _criteria;
	private Double[][][] _values;

	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRenderersInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);

	public SATableContentProvider(KTable table, String[] alternatives, String[] criteria, Double[][][] values) {
		super(table);

		_alternatives = alternatives;
		_criteria = criteria;
		_values = values;
		computePairs();
		initialize();

		_fixedRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
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

	@Override
	public Object doGetContentAt(int col, int row) {
		
		if((col == 0) && (row == 0)) {
			return ""; //$NON-NLS-1$
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
						erg = ((int) (((Double) erg) * 10000d)) / 10000d;
					}
				}
			} catch(Exception e) {
				erg = null;
			}

			return erg;
		}
	}

	public KTableCellEditor doGetCellEditor(int col, int row) {
		return null;
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {
	}

	@Override
	public int doGetRowCount() {
		return _pairs.length + getFixedRowCount();
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
		return 65;
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
		
		if((col < getFixedColumnCount()) || (row < getFixedRowCount())) {
			return _fixedRenderer;
		} else {
			if(doGetContentAt(col, row).equals("N/A")) {
				return _fixedRenderer;
			} else {
				return _fixedRenderersInTable;
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
		} else if(col < getFixedColumnCount()) {
			return _alternatives[_pairs[row - 1][0]] + "-" + _alternatives[_pairs[row - 1][0]]; //$NON-NLS-1$
		} else if(row < getFixedRowCount()) {
			return _criteria[col - 1];
		} else {
			return _alternatives[_pairs[row - 1][0]] + "-" + _alternatives[_pairs[row - 1][0]] + "/" + _criteria[col - 1]; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}
