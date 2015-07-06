package sinbad2.resolutionphase.frameworkstructuring.ui.view.elementassignments.provider;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainIndex;
import sinbad2.domain.DomainsManager;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.listener.ExpertsChangeEvent;
import sinbad2.element.expert.listener.IExpertsChangeListener;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentsManager;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.DomainAssignmentsChangeEvent;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.IDomainAssignmentsChangeListener;
import sinbad2.resolutionphase.frameworkstructuring.ui.Activator;
import sinbad2.resolutionphase.frameworkstructuring.ui.preferences.PreferenceConstants;
import sinbad2.resolutionphase.frameworkstructuring.ui.view.elementassignments.ElementAssignmentsTable;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellRenderer;
import de.kupzog.ktable.KTableNoScrollModel;
import de.kupzog.ktable.SWTX;
import de.kupzog.ktable.renderers.FixedCellRenderer;
import de.kupzog.ktable.renderers.TextCellRenderer;

public class ElementAssignmentsTableContentProvider extends KTableNoScrollModel implements IDomainAssignmentsChangeListener, 
	IExpertsChangeListener, IAlternativesChangeListener, ICriteriaChangeListener {


	private ElementAssignmentsTable _table;
	private DomainIndex _domainIndex;
	private DomainAssignmentsManager _domainAssignmentsManager;
	private DomainAssignments _domainAssignments;
	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementSet;
	private List<Expert> _experts;
	private List<Alternative> _alternatives;
	private List<Criterion> _criteria;
	private List<Expert> _finalExperts;
	private List<Alternative> _finalAlternatives;
	private List<Criterion> _finalCriteria;
	private Map<ProblemElement, String> _index;
	private ProblemElement[][] _colHeaderMatrix;
	private ProblemElement[][] _rowHeaderMatrix;
	private EElement _row;
	private EElement _col;
	private int _expertsDepth = -1;
	private int _alternativesDepth = -1;
	private int _criteriaDepth = -1;
	private int _fixedRows = -1;
	private int _fixedCols = -1;
	private IPropertyChangeListener _preferencesListener;

	private ProblemElement _element;

	private final FixedCellRenderer _fixedRenderer = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | SWT.BOLD);

	private final FixedCellRenderer _fixedRenderersInTable = new FixedCellRenderer(FixedCellRenderer.STYLE_FLAT | TextCellRenderer.INDICATION_FOCUS);

	/**
	 * Initialize the base implementation.
	 */
	public ElementAssignmentsTableContentProvider(
			ElementAssignmentsTable table, ProblemElement element) {
		super(table);

		_table = table;
		_elementsManager = ProblemElementsManager.getInstance();
		_elementSet = _elementsManager.getActiveElementSet();
		_domainAssignmentsManager = DomainAssignmentsManager.getInstance();
		_domainAssignments = _domainAssignmentsManager
				.getActiveDomainAssignments();

		_element = element;
		_experts = _elementSet.getExperts();
		_alternatives = _elementSet.getAlternatives();
		_criteria = _elementSet.getCriteria();

		computeOrder();
		hookPreferenceListener();
		initialize();

		_fixedRenderer.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setAlignment(SWTX.ALIGN_HORIZONTAL_CENTER | SWTX.ALIGN_VERTICAL_CENTER);
		_fixedRenderersInTable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		_domainAssignments.registerDomainAssignmentsChangeListener(this);
	}

	private void computeOrder() {
		if (_element instanceof Alternative) {
			boolean expertsInRows = Activator.getDefault().getPreferenceStore().getBoolean(
							PreferenceConstants.P_ALTERNATIVES_TABLE_EXPERTS_IN_ROWS);
			if (expertsInRows) {
				_row = EElement.EXPERT;
				_col = EElement.CRITERION;
			} else {
				_row = EElement.CRITERION;
				_col = EElement.EXPERT;
			}
			_elementSet.registerExpertsChangesListener(this);
			_elementSet.registerCriteriaChangesListener(this);
		} else if (_element instanceof Criterion) {
			boolean alternativesInRows = Activator.getDefault().getPreferenceStore().getBoolean(
							PreferenceConstants.P_CRITERIA_TABLE_ALTERNATIVES_IN_ROWS);
			if (alternativesInRows) {
				_row = EElement.ALTERNATIVE;
				_col = EElement.EXPERT;
			} else {
				_row = EElement.EXPERT;
				_col = EElement.ALTERNATIVE;
			}
			_elementSet.registerExpertsChangesListener(this);
			_elementSet.registerAlternativesChangesListener(this);
		} else {
			boolean alternativesInRows = Activator.getDefault().getPreferenceStore().getBoolean(
							PreferenceConstants.P_EXPERTS_TABLE_ALTERNATIVES_IN_ROWS);
			if (alternativesInRows) {
				_row = EElement.ALTERNATIVE;
				_col = EElement.CRITERION;
			} else {
				_row = EElement.CRITERION;
				_col = EElement.ALTERNATIVE;
			}
			_elementSet.registerCriteriaChangesListener(this);
			_elementSet.registerAlternativesChangesListener(this);
		}
	}

	@Override
	public void initialize() {
		computeDomainIndex();
		computeDepths();
		computeFixedValues();
		extractElements();
		super.initialize();
	}

	public void dispose() {
		_domainAssignments.unregisterDomainAssignmentsChangeListener(this);
		_elementSet.unregisterExpertsChangeListener(this);
		_elementSet.unregisterAlternativesChangeListener(this);
		_elementSet.unregisterCriteriaChangeListener(this);
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(_preferencesListener);
	}

	private void hookPreferenceListener() {

		final String constant = (_element instanceof Alternative) ? PreferenceConstants.P_ALTERNATIVES_TABLE_EXPERTS_IN_ROWS
				: (_element instanceof Criterion) ? PreferenceConstants.P_CRITERIA_TABLE_ALTERNATIVES_IN_ROWS
						: PreferenceConstants.P_EXPERTS_TABLE_ALTERNATIVES_IN_ROWS;

		_preferencesListener = new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(constant)) {
					EElement aux = _row;
					_row = _col;
					_col = aux;
					initialize();
					_table.redraw();
				}

			}
		};
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(_preferencesListener);
	}

	private void computeDomainIndex() {
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainIndex = new DomainIndex(domainsManager.getActiveDomainSet());
	}

	private void computeDepths() {
		_expertsDepth = 1;
		List<Expert> nextLevelExperts = new LinkedList<Expert>(_experts);
		
		do {
			List<Expert> aux = new LinkedList<Expert>(nextLevelExperts);
			nextLevelExperts = new LinkedList<Expert>();
			for (Expert expert : aux) {
				if (expert.hasChildrens()) {
					nextLevelExperts.addAll(expert.getChildrens());
				}
			}
			if (!nextLevelExperts.isEmpty()) {
				_expertsDepth++;
			}
		} while (!nextLevelExperts.isEmpty());

		_alternativesDepth = 1;

		_criteriaDepth = 1;
		List<Criterion> nextLevelCriteria = new LinkedList<Criterion>(_criteria);
		do {
			List<Criterion> aux = new LinkedList<Criterion>(nextLevelCriteria);
			nextLevelCriteria = new LinkedList<Criterion>();
			for (Criterion criterion : aux) {
				if (criterion.hasSubcriteria()) {
					nextLevelCriteria.addAll(criterion.getSubcriteria());
				}
			}
			if (!nextLevelCriteria.isEmpty()) {
				_criteriaDepth++;
			}
		} while (!nextLevelCriteria.isEmpty());
	}


	private void computeFixedValues() {

		if (_row.equals(EElement.EXPERT)) {
			_fixedCols = _expertsDepth;
		} else if (_row.equals(EElement.ALTERNATIVE)) {
			_fixedCols = _alternativesDepth;
		} else {
			_fixedCols = _criteriaDepth;
		}

		if (_col.equals(EElement.EXPERT)) {
			_fixedRows = _expertsDepth;
		} else if (_col.equals(EElement.ALTERNATIVE)) {
			_fixedRows = _alternativesDepth;
		} else {
			_fixedRows = _criteriaDepth;
		}

	}

	private List<Expert> extractExperts(Expert expert, int c) {

		List<Expert> result = new LinkedList<Expert>();

		String index = _index.get(expert.getParent());
		if (index == null) {
			index = "E" + c; //$NON-NLS-1$
		} else {
			index += "." + c; //$NON-NLS-1$
		}
		_index.put(expert, index);

		int counter = 1;
		if (expert.hasChildrens()) {
			for (Expert e : expert.getChildrens()) {
				result.addAll(extractExperts(e, counter++));
			}
		} else {
			result.add(expert);
		}

		return result;
	}

	private List<Criterion> extractCriteria(Criterion criterion, int c) {

		List<Criterion> result = new LinkedList<Criterion>();

		String index = _index.get(criterion.getParent());
		if (index == null) {
			index = "C" + c; //$NON-NLS-1$
		} else {
			index += "." + c; //$NON-NLS-1$
		}
		_index.put(criterion, index);

		int counter = 1;
		if (criterion.hasSubcriteria()) {
			for (Criterion cr : criterion.getSubcriteria()) {
				result.addAll(extractCriteria(cr, counter++));
			}
		} else {
			result.add(criterion);
		}
		return result;
	}

	private void extractElements() {
		_index = new HashMap<ProblemElement, String>();

		_finalExperts = new LinkedList<Expert>();
		int counter = 1;
		for (Expert expert : _experts) {
			_finalExperts.addAll(extractExperts(expert, counter++));
		}

		_finalAlternatives = new LinkedList<Alternative>();
		counter = 1;
		for (Alternative alternative : _alternatives) {
			_index.put(alternative, "A" + counter++); //$NON-NLS-1$
			_finalAlternatives.add(alternative);
		}

		counter = 1;
		_finalCriteria = new LinkedList<Criterion>();
		for (Criterion criterion : _criteria) {
			_finalCriteria.addAll(extractCriteria(criterion, counter++));
		}

		Expert expert;
		Expert eParent;
		Alternative alternative;
		Criterion criterion;
		Criterion cParent;
		int col;
		int row;
		int elementDepth;
		
		if (_row.equals(EElement.EXPERT)) {
			_rowHeaderMatrix = new ProblemElement[_fixedCols][_fixedRows + _finalExperts.size()];
			for (int i = 0; i < _finalExperts.size(); i++) {
				expert = _finalExperts.get(i);
				row = _fixedRows + i;
				elementDepth = expert.getPathId().split(">").length - 1; //$NON-NLS-1$
				col = elementDepth;
				eParent = expert;
				while (eParent.getParent() != null) {
					eParent = eParent.getParent();
					_rowHeaderMatrix[--col][row] = eParent;
				}
				col = elementDepth;
				while (col < _fixedCols) {
					_rowHeaderMatrix[col++][row] = expert;
				}
			}
		} else if (_row.equals(EElement.ALTERNATIVE)) {
			_rowHeaderMatrix = new ProblemElement[_fixedCols][_fixedRows + _finalAlternatives.size()];
			for (int i = 0; i < _finalAlternatives.size(); i++) {
				alternative = _finalAlternatives.get(i);
				col = _fixedCols - 1;
				row = _fixedRows + i;
				_rowHeaderMatrix[col][row] = alternative;
			}
		} else {
			_rowHeaderMatrix = new ProblemElement[_fixedCols][_fixedRows + _finalCriteria.size()];
			for (int i = 0; i < _finalCriteria.size(); i++) {
				criterion = _finalCriteria.get(i);
				row = _fixedRows + i;
				elementDepth = criterion.getPathId().split(">").length - 1; //$NON-NLS-1$
				col = elementDepth;
				cParent = criterion;
				while (cParent.getParent() != null) {
					cParent = cParent.getParent();
					_rowHeaderMatrix[--col][row] = cParent;
				}
				col = elementDepth;
				while (col < _fixedCols) {
					_rowHeaderMatrix[col++][row] = criterion;
				}
			}
		}

		if (_col.equals(EElement.EXPERT)) {
			_colHeaderMatrix = new ProblemElement[_fixedCols + _finalExperts.size()][_fixedRows];
			for (int i = 0; i < _finalExperts.size(); i++) {
				expert = _finalExperts.get(i);
				col = _fixedCols + i;
				elementDepth = expert.getPathId().split(">").length - 1; //$NON-NLS-1$
				row = elementDepth;
				eParent = expert;
				while (eParent.getParent() != null) {
					eParent = eParent.getParent();
					_colHeaderMatrix[col][--row] = eParent;
				}
				row = elementDepth;
				while (row < _fixedRows) {
					_colHeaderMatrix[col][row++] = expert;
				}
			}
		} else if (_col.equals(EElement.ALTERNATIVE)) {
			_colHeaderMatrix = new ProblemElement[_fixedCols
					+ _finalAlternatives.size()][_fixedRows];
			for (int i = 0; i < _finalAlternatives.size(); i++) {
				alternative = _finalAlternatives.get(i);
				col = _fixedCols + i;
				row = _fixedRows - 1;
				_colHeaderMatrix[col][row] = alternative;
			}

		} else {
			_colHeaderMatrix = new ProblemElement[_fixedCols
					+ _finalCriteria.size()][_fixedRows];
			for (int i = 0; i < _finalCriteria.size(); i++) {
				criterion = _finalCriteria.get(i);
				col = _fixedCols + i;
				elementDepth = criterion.getPathId().split(">").length - 1; //$NON-NLS-1$
				row = elementDepth;
				cParent = criterion;
				while (cParent.getParent() != null) {
					cParent = cParent.getParent();
					_colHeaderMatrix[col][--row] = cParent;
				}
				row = elementDepth;
				while (row < _fixedRows) {
					_colHeaderMatrix[col][row++] = criterion;
				}
			}
		}

		for (int i = 0; i < _fixedCols; i++) {
			for (int j = 0; j < _fixedRows; j++) {
				_colHeaderMatrix[i][j] = null;
				_rowHeaderMatrix[i][j] = null;
			}
		}
	}

	@Override
	public Object doGetContentAt(int col, int row) {
		
		if ((col < _fixedCols) && (row < _fixedRows)) {
			return ""; //$NON-NLS-1$

		} else {
			Object erg = null;
			Expert expert = null;
			Alternative alternative = null;
			Criterion criterion = null;

			try {
				if (col < _fixedCols) {
					erg = (getColumnWidth(col) > 80) ? _rowHeaderMatrix[col][row].getId() : _index.get(_rowHeaderMatrix[col][row]);

				} else if (row < _fixedRows) {
					erg = (getColumnWidth(col) > 80) ? _colHeaderMatrix[col][row].getId() : _index.get(_colHeaderMatrix[col][row]);

				} else {
					col = col - _fixedCols;
					row = row - _fixedRows;
					switch (_col) {
					case EXPERT:
						expert = _finalExperts.get(col);
						break;

					case ALTERNATIVE:
						alternative = _finalAlternatives.get(col);
						break;

					case CRITERION:
						criterion = _finalCriteria.get(col);
						break;
					}

					switch (_row) {
					case EXPERT:
						expert = _finalExperts.get(row);
						break;

					case ALTERNATIVE:
						alternative = _finalAlternatives.get(row);
						break;

					case CRITERION:
						criterion = _finalCriteria.get(row);
						break;
					}

					if (expert == null) {
						expert = (Expert) _element;
					} else if (alternative == null) {
						alternative = (Alternative) _element;
					} else {
						criterion = (Criterion) _element;
					}

					erg = _domainAssignments.getDomain(expert, alternative, criterion);
					
					if (erg != null) {
						erg = (getColumnWidth(col) > 80) ? ((Domain) erg).getId() : _domainIndex.getIndex((Domain) erg);
					} else {
						erg = ""; //$NON-NLS-1$
					}
				}
			} catch (Exception e) {
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
		switch (_row) {
		case EXPERT:
			return _finalExperts.size() + getFixedRowCount();

		case ALTERNATIVE:
			return _finalAlternatives.size() + getFixedRowCount();

		case CRITERION:
			return _finalCriteria.size() + getFixedRowCount();

		default:
			return 0;
		}
	}

	@Override
	public int doGetColumnCount() {
		switch (_col) {
		case EXPERT:
			return _finalExperts.size() + getFixedColumnCount();

		case ALTERNATIVE:
			return _finalAlternatives.size() + getFixedColumnCount();

		case CRITERION:
			return _finalCriteria.size() + getFixedColumnCount();

		default:
			return 0;
		}
	}

	@Override
	public int getFixedHeaderRowCount() {
		return _fixedRows;
	}

	@Override
	public int getFixedHeaderColumnCount() {
		return _fixedCols;
	}

	public int getFixedRowCount() {
		return _fixedRows;
	}

	public int getFixedColumnCount() {
		return _fixedCols;
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
		if ((col < getFixedColumnCount()) || (row < getFixedRowCount())) {
			return _fixedRenderer;
		} else {
			return _fixedRenderersInTable;
		}
	}

	@Override
	public Point doBelongsToCell(int col, int row) {
		
		if ((col < _fixedCols) || (row < _fixedRows)) {
			if (col < _fixedCols) {
				if (col > 0) {
					if (_rowHeaderMatrix[col][row] == _rowHeaderMatrix[col - 1][row]) {
						return new Point(col - 1, row);
					}
				}
				if (row > 0) {
					if (_rowHeaderMatrix[col][row] == _rowHeaderMatrix[col][row - 1]) {
						return new Point(col, row - 1);
					}
				}
			}
			if (row < _fixedRows) {
				if (col > 0) {
					if (_colHeaderMatrix[col][row] == _colHeaderMatrix[col - 1][row]) {
						return new Point(col - 1, row);
					}
				}
				if (row > 0) {
					if (_colHeaderMatrix[col][row] == _colHeaderMatrix[col][row - 1]) {
						return new Point(col, row - 1);
					}
				}
			}
		}
		return new Point(col, row);
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
		
		if ((col < _fixedCols) && (row < _fixedRows)) {
			return ""; //$NON-NLS-1$
		} else if (col < _fixedCols) {
			return _rowHeaderMatrix[col][row].getId();
		} else if (row < _fixedRows) {
			return _colHeaderMatrix[col][row].getId();
		} else {
			String value = null;
			col = col - _fixedCols;
			row = row - _fixedRows;
			Expert expert = null;
			Alternative alternative = null;
			Criterion criterion = null;
			Domain domain;
			switch (_row) {
			case EXPERT:
				expert = _finalExperts.get(row);
				value = expert.getId();
				break;

			case ALTERNATIVE:
				alternative = _finalAlternatives.get(row);
				value = alternative.getId();
				break;

			case CRITERION:
				criterion = _finalCriteria.get(row);
				value = criterion.getId();
				break;
			}

			value += "/"; //$NON-NLS-1$

			switch (_col) {
			case EXPERT:
				expert = _finalExperts.get(col);
				value += expert.getId();
				break;

			case ALTERNATIVE:
				alternative = _finalAlternatives.get(col);
				value += alternative.getId();
				break;

			case CRITERION:
				criterion = _finalCriteria.get(col);
				value += criterion.getId();
				break;
			}

			if (_element instanceof Expert) {
				expert = (Expert) _element;
			} else if (_element instanceof Alternative) {
				alternative = (Alternative) _element;
			} else {
				criterion = (Criterion) _element;
			}

			domain = _domainAssignments.getDomain(expert, alternative,
					criterion);
			if (domain == null) {
				return value;
			} else {
				return value += " - " + domain.getId(); //$NON-NLS-1$
			}
		}

	}

	@Override
	public void notifyNewActiveDomainAssignments(DomainAssignments domainAssignments) {
		
		if (_domainAssignments != domainAssignments) {
			_domainAssignments.unregisterDomainAssignmentsChangeListener(this);
			_domainAssignments = domainAssignments;
			_domainAssignments.registerDomainAssignmentsChangeListener(this);

			initialize();
			_table.redraw();
		}

	}

	@Override
	public void notifyDomainAssignmentsChange(DomainAssignmentsChangeEvent event) {
		initialize();
		_table.redraw();
	}

	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		_criteria = _elementSet.getCriteria();
		initialize();
		_table.redraw();
	}

	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		_alternatives = _elementSet.getAlternatives();
		initialize();
		_table.redraw();
	}

	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		_experts = _elementSet.getExperts();
		initialize();
		_table.redraw();
	}

}
