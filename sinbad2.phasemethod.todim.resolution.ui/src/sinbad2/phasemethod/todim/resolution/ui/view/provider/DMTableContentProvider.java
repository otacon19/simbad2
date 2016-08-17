package sinbad2.phasemethod.todim.resolution.ui.view.provider;

import java.util.Map;

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
import sinbad2.core.utils.Pair;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.todim.resolution.ui.nls.Messages;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;

public class DMTableContentProvider extends KTableNoScrollModel {
	
	private String[] _alternatives;
	private String[] _criteria;
	private Map<Pair<Alternative, Criterion>, Valuation> _values;
	
	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRenderersInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	
	private ProblemElementsSet _elementsSet;
	
	public DMTableContentProvider(KTable table, String[] alternatives, String[] criteria, Map<Pair<Alternative, Criterion>, Valuation> values) {
		super(table);
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();

		_alternatives = alternatives;
		_criteria = criteria;
		_values = values;
		
		initialize();

		_fixedRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object doGetContentAt(int col, int row) {
	
		Object erg;
		
		try {
			if(col == 0 && row == 0) {
				erg = Messages.DMTableContentProvider_Decision_matrix;
			} else if (col == 0) {
				erg = "A" + row; //$NON-NLS-1$
			} else if (row == 0) {
				erg = "C" + col; //$NON-NLS-1$
			} else {
				Alternative a = _elementsSet.getAlternatives().get(row - 1);
				Criterion c = _elementsSet.getAllCriteria().get(col - 1);
				if(_values.get(new Pair(a, c)) == null) {
					erg = ""; //$NON-NLS-1$
				} else {
					erg = ((TwoTuple) _values.get(new Pair(a, c))).changeFormatValuationToString();
				}
			}
		} catch(Exception e) {
			erg = null;
		}
		
		return erg;
	}

	public KTableCellEditor doGetCellEditor(int col, int row) {
		return new KTableCellEditorText();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void doSetContentAt(int col, int row, Object value) {
		Alternative a = _elementsSet.getAlternatives().get(row - 1);
		Criterion c = _elementsSet.getAllCriteria().get(col - 1);
		_values.put(new Pair(a, c), (Valuation) value);;
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
