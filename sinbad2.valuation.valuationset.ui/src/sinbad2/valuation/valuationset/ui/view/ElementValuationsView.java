package sinbad2.valuation.valuationset.ui.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.Domain;
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
import sinbad2.element.ui.view.elements.ElementView;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentsManager;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.DomainAssignmentsChangeEvent;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.IDomainAssignmentsChangeListener;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ui.valuationpanel.listener.IValuationPanelListener;
import sinbad2.valuation.ui.valuationpanel.listener.ValuationPanelEvent;
import sinbad2.valuation.ui.view.ValuationPanelView;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;
import sinbad2.valuation.valuationset.listener.IValuationSetChangeListener;
import sinbad2.valuation.valuationset.listener.ValuationSetChangeEvent;
import sinbad2.valuation.valuationset.operation.ERemoveValuation;
import sinbad2.valuation.valuationset.operation.ModifyValuationOperation;
import sinbad2.valuation.valuationset.operation.NewValuationOperation;
import sinbad2.valuation.valuationset.operation.RemoveValuationOperation;
import sinbad2.valuation.valuationset.operation.RemoveValuationOperationProvider;
import sinbad2.valuation.valuationset.ui.nls.Messages;
import sinbad2.valuation.valuationset.ui.view.listener.IRemoveValuationListener;

public class ElementValuationsView extends ViewPart implements ISelectionListener, IExpertsChangeListener, ICriteriaChangeListener, 
	IAlternativesChangeListener,  ISelectionChangedListener, IDomainAssignmentsChangeListener, IValuationSetChangeListener, 
	IValuationPanelListener, IRemoveValuationListener {
	
	public static final String ID = "flintstones.valuation.valuationset.ui.view.elementvaluations"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.valuation.valuationset.ui.view.elementvaluations.elementvaluations_view"; //$NON-NLS-1$

	private ElementView _elementView = null;
	private DomainAssignmentsManager _domainAssignmentsMananager;
	private ValuationSetManager _valuationSetManager;
	private ValuationSet _valuationSet;
	private ElementValuationsView _instance = null;
	private String _partName = null;
	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementSet;
	private Composite _container;
	private ProblemElement _selectedElement = null;
	private TabFolder _tabFolder = null;
	private List<ElementValuationsTable> _tables = null;
	private ValuationPanelView _valuationPanelView = null;

	private ValuationKey _selectedKey;

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
	
	@Override
	public void createPartControl(Composite parent) {
		_instance = this;
		_partName = getPartName();
		_container = parent;
		_container.setLayout(new FillLayout());
		_tables = new LinkedList<ElementValuationsTable>();
		
		hookFocusListener();
		
		getSite().getPage().addSelectionListener(this);
		setModel();

		_elementsManager = ProblemElementsManager.getInstance();
		_elementSet = _elementsManager.getActiveElementSet();
		_elementSet.registerCriteriaChangesListener(this);
		_elementSet.registerExpertsChangesListener(this);
		_elementSet.registerAlternativesChangesListener(this);
		_domainAssignmentsMananager = DomainAssignmentsManager.getInstance();
		_domainAssignmentsMananager.registerDomainAssignmentsChangeListener(this);
		_valuationSetManager = ValuationSetManager.getInstance();
		_valuationSetManager.registerValuationSetChangeListener(this);
		_valuationSet = _valuationSetManager.getActiveValuationSet();
		_valuationSet.registerValuationSetChangeListener(this);
	}
	
	private void setModel() {
		setSelection(null);

		for(ElementValuationsTable table : _tables) {
			if(!table.isDisposed()) {
				table.dispose();
			}
			table.removeSelectionChangedListener(this);
			table.unregisterRemoveValuationListener(this);
		}
		_tables.clear();
		if(_tabFolder != null) {
			if(!_tabFolder.isDisposed()) {
				for(TabItem tabItem : _tabFolder.getItems()) {
					if(!tabItem.isDisposed()) {
						tabItem.dispose();
					}
				}
				_tabFolder.dispose();
			}
			_tabFolder = null;
		}
		if(_selectedElement != null) {
			List<ProblemElement> toDisplay = new LinkedList<ProblemElement>();
			if(_selectedElement instanceof Expert) {
				List<Expert> nextLevelExperts = new LinkedList<Expert>();
				nextLevelExperts.add((Expert) _selectedElement);
				do {
					List<Expert> aux = new LinkedList<Expert>(nextLevelExperts);
					nextLevelExperts = new LinkedList<Expert>();
					for(Expert expert : aux) {
						if(expert.hasChildren()) {
							nextLevelExperts.addAll(expert.getChildren());
						} else {
							toDisplay.add(expert);
						}
					}
				} while (!nextLevelExperts.isEmpty());
			} else if(_selectedElement instanceof Alternative) {
				toDisplay.add(_selectedElement);

			} else if(_selectedElement instanceof Criterion) {
				List<Criterion> nextLevelCriteria = new LinkedList<Criterion>();
				nextLevelCriteria.add((Criterion) _selectedElement);
				do {
					List<Criterion> aux = new LinkedList<Criterion>(nextLevelCriteria);
					nextLevelCriteria = new LinkedList<Criterion>();
					for(Criterion criterion : aux) {
						if (criterion.hasSubcriteria()) {
							nextLevelCriteria.addAll(criterion.getSubcriteria());
						} else {
							toDisplay.add(criterion);
						}
					}
				} while (!nextLevelCriteria.isEmpty());

			}
			_tabFolder = new TabFolder(_container, SWT.BORDER);
			for(ProblemElement element : toDisplay) {
				TabItem tabItem = new TabItem(_tabFolder, SWT.NULL);
				tabItem.setText(element.getCanonicalId());
				ElementValuationsTable elementValuationsTable = new ElementValuationsTable(_tabFolder);
				tabItem.setControl(elementValuationsTable);
				elementValuationsTable.setModel(element);
				elementValuationsTable.addSelectionChangedListener(this);
				elementValuationsTable.registerRemoveValuationListener(this);
				_tables.add(elementValuationsTable);
			}
			if(_selectedElement instanceof Alternative) {
				_instance.setPartName(_partName + " | " + Messages.ElementValuationsView_Alternative); //$NON-NLS-1$
			} else if (_selectedElement instanceof Criterion) {
				_instance.setPartName(_partName + " | " + Messages.ElementValuationsView_Criterion); //$NON-NLS-1$
			} else {
				_instance.setPartName(_partName + " | " + Messages.ElementValuationsView_Expert); //$NON-NLS-1$
			}

		} else {
			_instance.setPartName(_partName);
			forceSelection();
		}
	}
	
	private void forceSelection() {
		
		if(_elementView == null) {
			for(IViewReference reference : getSite().getPage().getViewReferences()) {
				if (reference.getId().equals(ElementView.ID)) {
					_elementView = (ElementView) reference.getView(true);
				}
			}
		}
		if(_elementView != null) {
			StructuredSelection selection = (StructuredSelection) _elementView.getSelection();
			if (selection != null) {
				if (!selection.isEmpty()) {
					setElementSelected(selection);
					setModel();
				}
			}
		}
	}
	
	@Override
	public void setFocus() {
		_container.setFocus();
		
		if(_selectedElement == null) {
			forceSelection();
		}
	}
	

	@Override
	public void dispose() {
		
		for(ElementValuationsTable table : _tables) {
			if(!table.isDisposed()) {
				table.removeSelectionChangedListener(this);
				table.unregisterRemoveValuationListener(this);
				table.dispose();
			}
		}
		_tables.clear();
		if(_tabFolder != null) {
			if(!_tabFolder.isDisposed()) {
				for(TabItem tabItem : _tabFolder.getItems()) {
					if(!tabItem.isDisposed()) {
						tabItem.dispose();
					}
				}
				_tabFolder.dispose();
			}
			_tabFolder = null;
		}
		getSite().getPage().removeSelectionListener(this);
		_elementSet.unregisterExpertsChangeListener(this);
		_elementSet.unregisterCriteriaChangeListener(this);
		_elementSet.unregisterAlternativesChangeListener(this);
		super.dispose();
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
		if(part instanceof ElementView) {
			setElementSelected((StructuredSelection) selection);
			setModel();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		Criterion criterion;
		List<Criterion> criteria;
		
		switch (event.getChange()) {
			case ADD_CRITERION:
				criterion = (Criterion) event.getNewValue();
				while(criterion.getParent() != null) {
					criterion = criterion.getParent();
					if(_selectedElement == criterion) {
						setModel();
					}
				}
				break;
			case REMOVE_CRITERION:
				criterion = (Criterion) event.getOldValue();
				while(criterion.getParent() != null) {
					criterion = criterion.getParent();
					if(_selectedElement == criterion) {
						setModel();
					}
				}
				break;
			case ADD_CRITERIA:
				criteria = (List<Criterion>) event.getNewValue();
				for(Criterion c : criteria) {
					while(c.getParent() != null) {
						c = c.getParent();
						if (_selectedElement == c) {
							setModel();
						}
					}
				}
				break;
			case REMOVE_CRITERIA:
				criteria = (List<Criterion>) event.getOldValue();
				for(Criterion c : criteria) {
					while(c.getParent() != null) {
						c = c.getParent();
						if(_selectedElement == c) {
							setModel();
						}
					}
				}
				break;
			case MOVE_CRITERION:
				criterion = (Criterion) event.getNewValue();
				while(criterion.getParent() != null) {
					criterion = criterion.getParent();
					if(_selectedElement == criterion) {
						setModel();
					}
				}
				criterion = (Criterion) event.getOldValue();
				if(criterion != null) {
					while(criterion.getParent() != null) {
						criterion = criterion.getParent();
						if(_selectedElement == criterion) {
							setModel();
						}
					}
				}
				break;
	
			case MODIFY_CRITERION:
				criterion = (Criterion) event.getNewValue();
				while(criterion.getParent() != null) {
					criterion = criterion.getParent();
					if(_selectedElement == criterion) {
						setModel();
					}
				}
				break;
	
			default:
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		Expert expert;
		List<Expert> experts;
		
		switch(event.getChange()) {
			case ADD_EXPERT:
				expert = (Expert) event.getNewValue();
				while(expert.getParent() != null) {
					expert = expert.getParent();
					if(_selectedElement == expert) {
						setModel();
					}
				}
				break;
			case REMOVE_EXPERT:
				expert = (Expert) event.getOldValue();
				while(expert.getParent() != null) {
					expert = expert.getParent();
					if(_selectedElement == expert) {
						setModel();
					}
				}
				break;
			case ADD_MULTIPLE_EXPERTS:
				experts = (List<Expert>) event.getNewValue();
				for(Expert e : experts) {
					while(e.getParent() != null) {
						e = e.getParent();
						if(_selectedElement == e) {
							setModel();
						}
					}
				}
				break;
			case REMOVE_MULTIPLE_EXPERTS:
				experts = (List<Expert>) event.getOldValue();
				for(Expert e : experts) {
					while(e.getParent() != null) {
						e = e.getParent();
						if(_selectedElement == e) {
							setModel();
						}
					}
				}
				break;
			case MOVE_EXPERT:
				expert = (Expert) event.getNewValue();
				while(expert.getParent() != null) {
					expert = expert.getParent();
					if(_selectedElement == expert) {
						setModel();
					}
				}
				expert = (Expert) event.getOldValue();
				if(expert != null) {
					while(expert.getParent() != null) {
						expert = expert.getParent();
						if(_selectedElement == expert) {
							setModel();
						}
					}
				}
				break;
	
			case MODIFY_EXPERT:
				expert = (Expert) event.getNewValue();
				while(expert.getParent() != null) {
					expert = expert.getParent();
					if(_selectedElement == expert) {
						setModel();
					}
				}
	
			default:
		}

	}
	
	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		
		switch(event.getChange()) {
			case MODIFY_ALTERNATIVE:
				if(_selectedElement == event.getNewValue()) {
					setModel();
				}
				break;
			
			default:

		}
	}
	
	private void hookFocusListener() {
		_container.addFocusListener(new FocusListener() {

			private IContextActivation activation = null;

			@Override
			public void focusLost(FocusEvent e) {
				_contextService.deactivateContext(activation);
			}

			@Override
			public void focusGained(FocusEvent e) {
				activation = _contextService.activateContext(CONTEXT_ID);
			}
		});
	}

	private void setElementSelected(StructuredSelection selection) {
		ProblemElement element = null;
		
		if(!selection.isEmpty()) {
			element = (ProblemElement) selection.getFirstElement();
		}
		_selectedElement = element;
	}

	private void setSelection(ValuationKey key) {

		IWorkbenchPage page = getSite().getPage();

		if((_selectedKey == null) && (key == null)) {
			page.hideView(_valuationPanelView);
			return;
		}

		if(key != null) {
			DomainAssignments domainAssignments = _domainAssignmentsMananager.getActiveDomainAssignments();
			Domain domain = domainAssignments.getDomain(key.getExpert(), key.getAlternative(), key.getCriterion());

			if(_selectedKey == null) {
				if(domain != null) {
					try {
						_valuationPanelView = (ValuationPanelView) page.showView(ValuationPanelView.ID);
						_valuationPanelView.setValues(this, domain, _valuationSet.getValuations().get(key));
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			} else {
				if(domain == null) {
					try {
						page.hideView(_valuationPanelView);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						_valuationPanelView = (ValuationPanelView) page.showView(ValuationPanelView.ID);
						_valuationPanelView.setValues(this, domain,	_valuationSet.getValuations().get(key));

					} catch (PartInitException e) {
						e.printStackTrace();
					} catch (RuntimeException re) {
						re.printStackTrace();
					}
				}

			}
		}

		_selectedKey = key;

	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		
		if(selection.isEmpty()) {
			setSelection(null);
		} else {
			setSelection((ValuationKey) selection.getFirstElement());
		}
	}
	
	@Override
	public void notifyValuationSetChange(ValuationSetChangeEvent event) {
		
		if(event.getInUndoRedo()) {
			setSelection(_selectedKey);
		}
	}
	
	@Override
	public void notifyNewActiveValuationSet(ValuationSet valuationSet) {}
	
	@Override
	public void notifyNewActiveDomainAssignments(DomainAssignments domainAssignments) {}

	@Override
	public void notifyDomainAssignmentsChange(DomainAssignmentsChangeEvent event) {
		setSelection(_selectedKey);
	}
	
	@Override
	public void notifyValuationPanelChange(ValuationPanelEvent event) {
		Valuation oldValuation = event.getOldValuation();
		Valuation newValuation = event.getNewValuation();

		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();

		switch(event.getEvent()) {
			case REMOVE_VALUATION:
				List<ValuationKey> keys = new LinkedList<ValuationKey>();
				keys.add(_selectedKey);
				Map<ValuationKey, Valuation> valuations = new RemoveValuationOperationProvider(_valuationSet, ERemoveValuation.VALUATIONS, keys).check();
	
				RemoveValuationOperation rvo = new RemoveValuationOperation(_valuationSet, valuations);
				rvo.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
				try {
					operationHistory.execute(rvo, null, null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				break;
			case NEW_VALUATION:
				NewValuationOperation nvo = new NewValuationOperation(_valuationSet, _selectedKey.getExpert(), _selectedKey.getAlternative(), _selectedKey.getCriterion(),
						newValuation);
	
				nvo.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
				try {
					operationHistory.execute(nvo, null, null);
				} catch (ExecutionException e) {
				}
	
				break;
			case MODIFY_VALUATION:
				ModifyValuationOperation mvo = new ModifyValuationOperation(_selectedKey, newValuation, oldValuation, _valuationSet);
				mvo.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
				try {
					operationHistory.execute(mvo, null, null);
				} catch (ExecutionException e) {
				}
				
				break;
		}

	}
	
	@Override
	public void notifyRemoveValuation(ValuationKey key) {
		_valuationPanelView.removeSelection();
	}

}
