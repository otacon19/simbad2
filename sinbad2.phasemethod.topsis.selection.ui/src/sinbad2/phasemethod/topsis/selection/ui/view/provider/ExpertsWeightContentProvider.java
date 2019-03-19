package sinbad2.phasemethod.topsis.selection.ui.view.provider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import de.kupzog.ktable.KTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableNoScrollModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.editors.KTableCellEditorCombo;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.topsis.selection.SelectionPhase;
import sinbad2.phasemethod.topsis.selection.ui.listener.IChangeWeightListener;

public class ExpertsWeightContentProvider extends KTableNoScrollModel  {
	
	private KTable _table;
	
	private ProblemElementsSet _elementsSet;
	
	private SelectionPhase _selectionPhase;
	
	private static List<IChangeWeightListener> _listeners;
	
	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);
	private final FixedCellRenderer _fixedRendererInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	private final FixedCellRenderer _editableRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);
	
	class MyOwnKTableCellEditorCombo extends KTableCellEditorCombo {
		@Override
		public int getActivationSignals() {
			return SINGLECLICK;
		}
	}
	
	private MyOwnKTableCellEditorCombo kTableCombo = new MyOwnKTableCellEditorCombo();

	public ExpertsWeightContentProvider(KTable table, SelectionPhase selectionPhase) {
		super(table);
		
		_table = table;
		_selectionPhase = selectionPhase;
		
		_listeners = new LinkedList<IChangeWeightListener>();
		
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
	public KTableCellEditor doGetCellEditor(int col, int row) {
		if (col != 0 && row != 0) {
			kTableCombo = new MyOwnKTableCellEditorCombo();
			FuzzySet weightsDomain = _selectionPhase.getWeightsDomain();
			
			String[] valuesString = new String[weightsDomain.getLabelSet().getCardinality()];
			for(int i = 0; i < valuesString.length; ++i) {
				valuesString[i] = weightsDomain.getLabelSet().getLabel(i).getName();
			}
	
			kTableCombo.setItems(valuesString);
			
			return kTableCombo;
		}
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
		return _elementsSet.getAllSubcriteria().size() + getFixedColumnCount();
	}

	@Override
	public Object doGetContentAt(int col, int row) {
		if ((col == 0) && (row == 0)) {
			return ""; //$NON-NLS-1$
		}
		
		Object content;

		try {
			if (col == 0) {
				content = expertAbbreviation(row);
			} else if (row == 0) {
				content = criterionAbbreviation(col);
			} else {
				content = _selectionPhase.getExpertWeight(_elementsSet.getOnlyExpertChildren().get(row - 1), col - 1);
			}
			
		} catch (Exception e) {
			content = ""; //$NON-NLS-1$
		}
		
		return content;
	}

	private Object criterionAbbreviation(int pos) {
		List<Criterion> orderedCriteria = new LinkedList<>(_elementsSet.getAllSubcriteria());
		Collections.sort(orderedCriteria);
		return orderedCriteria.get(pos - 1).getId();
	}
	
	private Object expertAbbreviation(int pos) {
		return _elementsSet.getOnlyExpertChildren().get(pos - 1).getId();
	}

	@Override
	public int doGetRowCount() {
		return _elementsSet.getOnlyExpertChildren().size() + getFixedRowCount();
	}

	@Override
	public void doSetContentAt(int col, int row, Object value) {
		LabelLinguisticDomain oldWeight = _selectionPhase.getExpertWeight(_elementsSet.getOnlyExpertChildren().get(row - 1), col - 1);
		LabelLinguisticDomain newWeight = _selectionPhase.getWeightsDomain().getLabelSet().getLabel((String) value);
		
		_selectionPhase.setExpertWeight(_elementsSet.getOnlyExpertChildren().get(row - 1), col - 1, newWeight);
		
		if(oldWeight != newWeight) {
			_selectionPhase.execute();
			notifyChangeWeightListeners();
		}	
		
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

	public static void registerChangeWeightListener(IChangeWeightListener listener) {
		_listeners.add(listener);
	}

	public static void removeChangeWeightListener(IChangeWeightListener listener) {
		_listeners.remove(listener);
	}
	
	public static void notifyChangeWeightListeners() {
		for(IChangeWeightListener listener: _listeners) {
			listener.notifyWeigthChanged();
		}
	}
	
	public void clearListeners() {
		_listeners.clear();
	}
	
	@Override
	public String doGetTooltipAt(int col, int row) {
		if ((col == 0) && (row == 0)) {
			return ""; //$NON-NLS-1$
		} else if (col < getFixedColumnCount()) {
			return _elementsSet.getOnlyExpertChildren().get(row - 1).getId();
		} else if (row < getFixedRowCount()) {
			return _elementsSet.getAllSubcriteria().get(col - 1).getId();
		} else {
			return _elementsSet.getOnlyExpertChildren().get(row - 1).getId() + '/' + _elementsSet.getAllSubcriteria().get(col - 1).getId();
		}
	}
}
