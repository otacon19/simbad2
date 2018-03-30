package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableNoScrollModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.valuation.twoTuple.TwoTuple;

public class DecisionMatrixContentProvider extends KTableNoScrollModel  {
	
	private KTable _table;
	
	private ProblemElementsSet _elementsSet;
	
	private SelectionPhase _selectionPhase;
	
	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRendererInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	private final FixedCellRenderer _editableRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	

	public DecisionMatrixContentProvider(KTable table, SelectionPhase selectionPhase) {
		super(table);
		
		_table = table;
		_selectionPhase = selectionPhase;
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		
		initialize();
		setDesign();
	}

	private void setDesign() {
		_fixedRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRendererInTable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

		_editableRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_editableRenderer.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	@Override
	public int getFixedHeaderColumnCount() {
		return 1;
	}

	@Override
	public int getFixedHeaderRowCount() {
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
	public int getRowHeightMinimum() {
		return 0;
	}

	@Override
	public boolean isColumnResizable(int arg0) {
		return false;
	}

	@Override
	public boolean isRowResizable(int arg0) {
		return false;
	}
	
	@Override
	public KTableCellEditor doGetCellEditor(int arg0, int arg1) {
		return null;
	}

	@Override
	public KTableCellRenderer doGetCellRenderer(int col, int row) {
		if((col < getFixedColumnCount()) || (row < getFixedRowCount())) {
			return _fixedRenderer;
		} else {
			return _editableRenderer;
		}
	}

	@Override
	public int doGetColumnCount() {
		return _elementsSet.getAlternatives().size() + getFixedColumnCount();
	}

	@Override
	public Object doGetContentAt(int col, int row) {
		if ((col == 0) && (row == 0)) {
			return "Weighted Decision Matrix"; //$NON-NLS-1$
		}
		
		Object content;

		try {
			if (col == 0) {
				content = criterionAbbreviation(row);
			} else if (row == 0) {
				content = alternativeAbbreviation(col);
			} else {
				content = ((TwoTuple) _selectionPhase.getDecisionMatrix()[row - 1][ col - 1]).prettyFormat();
			}
		} catch (Exception e) {
			content = ""; //$NON-NLS-1$
		}
		
		return content;
	}

	private Object criterionAbbreviation(int pos) {
		return _elementsSet.getAllSubcriteria().get(pos - 1).getId();
	}
	
	private Object alternativeAbbreviation(int pos) {
		return _elementsSet.getAlternatives().get(pos - 1).getId();
	}

	@Override
	public int doGetRowCount() {
		return _elementsSet.getAllSubcriteria().size() + getFixedRowCount();
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {
		_table.redraw();
	}

	@Override
	public int getInitialColumnWidth(int arg0) {
		return 30;
	}

	@Override
	public int getInitialRowHeight(int arg0) {
		return 30;
	}
	
	@Override
	public boolean isFixedCell(int col, int row) {
		if (col == 0) {
			return true;
		} else if (row == 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isHeaderCell(int col, int row) {
		return isFixedCell(col, row);
	}

	@Override
	public String doGetTooltipAt(int col, int row) {
		if ((col == 0) && (row == 0)) {
			return ""; //$NON-NLS-1$
		} else if (col < getFixedColumnCount()) {
			return _elementsSet.getAllSubcriteria().get(row - 1).getId();
		} else if (row < getFixedRowCount()) {
			return _elementsSet.getAlternatives().get(col - 1).getId();
		} else {
			return _elementsSet.getAllSubcriteria().get(row - 1).getId() + '/' + _elementsSet.getAlternatives().get(col - 1).getId();
		}
	}
}
