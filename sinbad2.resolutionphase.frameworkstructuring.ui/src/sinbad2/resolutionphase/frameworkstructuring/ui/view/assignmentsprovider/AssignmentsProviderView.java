package sinbad2.resolutionphase.frameworkstructuring.ui.view.assignmentsprovider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.listener.DomainSetChangeEvent;
import sinbad2.domain.listener.IDomainSetChangeListener;
import sinbad2.domain.listener.IDomainSetListener;
import sinbad2.element.IProblemElementsSetChangeListener;
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
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentsManager;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.operation.ApplyDomainAssignmentsOperation;
import sinbad2.resolutionphase.frameworkstructuring.ui.Images;
import sinbad2.resolutionphase.frameworkstructuring.ui.nls.Messages;

public class AssignmentsProviderView extends ViewPart implements IAlternativesChangeListener, IExpertsChangeListener, ICriteriaChangeListener,
	IDomainSetChangeListener, IProblemElementsSetChangeListener, IDomainSetListener {
	
	public static final String ID = "flintstones.resolutionphase.frameworkstructuring.ui.view.assignmentsprovider"; //$NON-NLS-1$

	private Composite _container;
	private DomainAssignmentsManager _domainAssignmentsManager;
	private DomainsManager _domainsManager;
	private DomainSet _domainSet;
	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementSet;
	private List<Expert> _experts;
	private List<Alternative> _alternatives;
	private List<Criterion> _criteria;
	private String[] _domainValues;
	private String[] _expertValues;
	private String[] _alternativeValues;
	private String[] _criterionValues;
	private Button _applyButton;
	private Combo _domainCombo;
	private Combo _alternativeCombo;
	private Combo _criterionCombo;
	private Combo _expertCombo;
	private Boolean _validElements;
	private Boolean _validDomains;

	public AssignmentsProviderView() {
		_domainAssignmentsManager = DomainAssignmentsManager.getInstance();

		_domainsManager = DomainsManager.getInstance();
		_domainsManager.registerDomainSetChangeListener(this);

		_domainSet = _domainsManager.getActiveDomainSet();
		_domainSet.registerDomainsListener(this);
		extractDomainValues();

		_elementsManager = ProblemElementsManager.getInstance();
		_elementsManager.registerElementsSetChangeListener(this);

		_elementSet = _elementsManager.getActiveElementSet();
		_experts = _elementSet.getExperts();
		_elementSet.registerExpertsChangesListener(this);
		extractExpertValues();

		_alternatives = _elementSet.getAlternatives();
		_elementSet.registerAlternativesChangesListener(this);
		extractAlternativeValues();

		_criteria = _elementSet.getCriteria();
		_elementSet.registerCriteriaChangesListener(this);
		extractCriterionValues();

		_validElements = null;
		_validDomains = null;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_container = parent;
		parent.setLayout(new GridLayout(9, false));

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText(Messages.AssignmentsProviderView_Expert);
		_expertCombo = new Combo(parent, SWT.NONE);
		_expertCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText(Messages.AssignmentsProviderView_Criterion);
		_criterionCombo = new Combo(parent, SWT.NONE);
		_criterionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText(Messages.AssignmentsProviderView_Alternative);
		_alternativeCombo = new Combo(parent, SWT.NONE);
		_alternativeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText(Messages.AssignmentsProviderView_Domain);
		_domainCombo = new Combo(parent, SWT.NONE);
		_domainCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		_applyButton = new Button(parent, SWT.NONE);
		_applyButton.setText(Messages.AssignmentsProviderView_Apply);
		_applyButton.setImage(Images.Apply);
		_applyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Expert expert = null;
				Alternative alternative = null;
				Criterion criterion = null;
				Domain domain = null;

				int pos = _expertCombo.getSelectionIndex();
				String item = null;
				if (pos == 0) {
					expert = null;
				} else {
					item = _expertCombo.getItem(pos);
					expert = Expert.getExpertByCanonicalId(_experts, item);
				}

				pos = _alternativeCombo.getSelectionIndex();
				item = null;
				if (pos == 0) {
					alternative = null;
				} else {
					item = _alternativeCombo.getItem(pos);
					boolean find = false;
					int counter = 0;
					int size = _alternatives.size();
					do {
						alternative = _alternatives.get(counter++);
						if (item.equals(alternative.getId())) {
							find = true;
						}
					} while ((!find) && (counter < size));
				}

				pos = _criterionCombo.getSelectionIndex();
				item = null;
				if (pos == 0) {
					criterion = null;
				} else {
					item = _criterionCombo.getItem(pos);
					criterion = Criterion.getCriterionByCanonicalId(_criteria, item);
				}

				pos = _domainCombo.getSelectionIndex();
				item = _domainCombo.getItem(pos);
				domain = _domainSet.getDomain(item);

				IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
				ApplyDomainAssignmentsOperation operation = new ApplyDomainAssignmentsOperation(_domainAssignmentsManager.getActiveDomainAssignments(),
						expert, alternative, criterion, domain);

				operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
				try {
					operationHistory.execute(operation, null, null);
				} catch (ExecutionException ee) {
					ee.printStackTrace();
				}
			}
		});

		computeState(EComboChange.ALL);
	}
	
	@Override
	public void dispose() {
		_domainsManager.unregisterDomainSetChangeListener(this);
		_domainSet.unregisterDomainsListener(this);
		_elementsManager.unregisterElementsSetChangeListener(this);
		_elementSet.unregisterExpertsChangeListener(this);
		_elementSet.unregisterAlternativesChangeListener(this);
		_elementSet.unregisterCriteriaChangeListener(this);
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void notifyDomainSetListener(DomainSetChangeEvent event) {
		_domainSet = _domainsManager.getActiveDomainSet();
		extractDomainValues();
		computeState(EComboChange.DOMAINS);
	}

	@Override
	public void notifyNewActiveDomainSet(DomainSet domainSet) {
		_domainSet.unregisterDomainsListener(this);
		_domainSet = domainSet;
		_domainSet.registerDomainsListener(this);
		extractDomainValues();
		computeState(EComboChange.DOMAINS);
	}

	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		_experts = _elementSet.getExperts();
		extractExpertValues();
		computeState(EComboChange.ELEMENTS);
	}

	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		_alternatives = _elementSet.getAlternatives();
		extractAlternativeValues();
		computeState(EComboChange.ELEMENTS);
	}

	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		_criteria = _elementSet.getCriteria();
		extractCriterionValues();
		computeState(EComboChange.ELEMENTS);
	}

	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		_elementSet.unregisterExpertsChangeListener(this);
		_elementSet.unregisterAlternativesChangeListener(this);
		_elementSet.unregisterCriteriaChangeListener(this);
		_elementSet = elementSet;

		_elementSet.registerExpertsChangesListener(this);
		_experts = _elementSet.getExperts();
		extractExpertValues();

		_elementSet.registerAlternativesChangesListener(this);
		_alternatives = _elementSet.getAlternatives();
		extractAlternativeValues();

		_elementSet.registerCriteriaChangesListener(this);
		_criteria = _elementSet.getCriteria();
		extractCriterionValues();

		computeState(EComboChange.ELEMENTS);
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		
		if (ISizeProvider.class == adapter) {
			return new ISizeProvider() {
				public int getSizeFlags(boolean width) {
					return SWT.MIN | SWT.MAX | SWT.FILL;
				}

				public int computePreferredSize(boolean width,
						int availableParallel, int availablePerpendicular,
						int preferredResult) {
					return width ? preferredResult : 43;
				}
			};
		}
		return super.getAdapter(adapter);
	}
	
	private void extractDomainValues() {
		_domainValues = _domainSet.getAllDomainsIds();
	}

	private List<String> extractExpertValues(Expert expert) {
		List<String> result = new LinkedList<String>();

		if (expert.hasChildren()) {
			for (Expert e : expert.getChildren()) {
				result.addAll(extractExpertValues(e));
			}
		}

		result.add(expert.getCanonicalId());

		return result;
	}

	private void extractExpertValues() {
		List<String> values = new LinkedList<String>();
		for (Expert expert : _experts) {
			values.addAll(extractExpertValues(expert));
		}
		Collections.sort(values);
		values.add(0, Messages.AssignmentsProviderView_All);

		_expertValues = values.toArray(new String[0]);
	}

	private void extractAlternativeValues() {
		List<String> values = new LinkedList<String>();
		for (Alternative alternative : _alternatives) {
			values.add(alternative.getCanonicalId());
		}
		Collections.sort(values);
		values.add(0, Messages.AssignmentsProviderView_All);

		_alternativeValues = values.toArray(new String[0]);
	}

	private List<String> extractCriterionValues(Criterion criterion) {
		List<String> result = new LinkedList<String>();

		if (criterion.hasSubcriteria()) {
			for (Criterion c : criterion.getSubcriteria()) {
				result.addAll(extractCriterionValues(c));
			}
		}

		result.add(criterion.getCanonicalId());

		return result;
	}

	private void extractCriterionValues() {
		List<String> values = new LinkedList<String>();
		for (Criterion criterion : _criteria) {
			values.addAll(extractCriterionValues(criterion));
		}
		Collections.sort(values);
		values.add(0, Messages.AssignmentsProviderView_All);

		_criterionValues = values.toArray(new String[0]);
	}
	
	private void computeState(EComboChange change) {
		boolean oldEnabled;
		
		if ((_validDomains == null) || (_validElements == null)) {
			oldEnabled = true;
		} else {
			oldEnabled = _validDomains && _validElements;
		}

		boolean enabled = false, testElements = true, testDomains = true;

		if (EComboChange.DOMAINS.equals(change)) {
			testElements = false;
		} else if (EComboChange.ELEMENTS.equals(change)) {
			testDomains = false;
		}

		if (testDomains) {
			_validDomains = _domainValues.length != 0;
		}

		if (testElements) {
			_validElements = ((_expertValues.length != 1)
					&& (_alternativeValues.length != 1) && (_criterionValues.length != 1));
		}

		if ((_validDomains == null) || (_validElements == null)) {
			enabled = false;
		} else {
			enabled = _validDomains && _validElements;
		}

		if (enabled != oldEnabled) {
			for (Control control : _container.getChildren()) {
				control.setEnabled(enabled);
			}
		}

		if (enabled) {
			_expertCombo.setItems(_expertValues);
			_alternativeCombo.setItems(_alternativeValues);
			_criterionCombo.setItems(_criterionValues);
			_domainCombo.setItems(_domainValues);
			_expertCombo.select(0);
			_alternativeCombo.select(0);
			_criterionCombo.select(0);
			_domainCombo.select(0);

		} else {
			_expertCombo.setItems(new String[] {});
			_alternativeCombo.setItems(new String[] {});
			_criterionCombo.setItems(new String[] {});
			_domainCombo.setItems(new String[] {});
		}
	}

}
