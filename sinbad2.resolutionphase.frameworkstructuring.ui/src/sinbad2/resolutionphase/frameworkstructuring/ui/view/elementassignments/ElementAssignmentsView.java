package sinbad2.resolutionphase.frameworkstructuring.ui.view.elementassignments;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

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
import sinbad2.resolutionphase.frameworkstructuring.ui.nls.Messages;

public class ElementAssignmentsView extends ViewPart implements ISelectionListener, IExpertsChangeListener, ICriteriaChangeListener, 
	IAlternativesChangeListener{
	
	public static final String ID = "flintstones.resolutionphase.frameworkstructuring.ui.view.elementassignments"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.resolutionphase.frameworkstructuring.ui.view.elementassignments.elementassignments_view"; //$NON-NLS-1$

	private ElementView _elementView = null;
	private ElementAssignmentsView _instance = null;
	private String _partName = null;
	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementSet;
	private Composite _container;
	private ProblemElement _selectedElement = null;
	private TabFolder _tabFolder = null;

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
	
	@Override
	public void createPartControl(Composite parent) {
		_instance = this;
		_partName = getPartName();
		_container = parent;
		_container.setLayout(new FillLayout());
		
		hookFocusListener();
		
		getSite().getPage().addSelectionListener(this);
		setModel();
		
		_elementsManager = ProblemElementsManager.getInstance();
		_elementSet = _elementsManager.getActiveElementSet();
		_elementSet.registerExpertsChangesListener(this);
		_elementSet.registerAlternativesChangesListener(this);
		_elementSet.registerCriteriaChangesListener(this);
	}

	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		
		switch (event.getChange()) {
			case MODIFY_ALTERNATIVE:
				if (_selectedElement == event.getNewValue()) {
					setModel();
				}
				break;
			default:
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
				while (criterion.getParent() != null) {
					criterion = criterion.getParent();
					if (_selectedElement == criterion) {
						setModel();
					}
				}
				break;
			case REMOVE_CRITERION:
				criterion = (Criterion) event.getOldValue();
				while (criterion.getParent() != null) {
					criterion = criterion.getParent();
					if (_selectedElement == criterion) {
						setModel();
					}
				}
				break;
			case ADD_CRITERIA:
				criteria = (List<Criterion>) event.getNewValue();
				for (Criterion c : criteria) {
					while (c.getParent() != null) {
						c = c.getParent();
						if (_selectedElement == c) {
							setModel();
						}
					}
				}
				break;
			case REMOVE_CRITERIA:
				criteria = (List<Criterion>) event.getOldValue();
				for (Criterion c : criteria) {
					while (c.getParent() != null) {
						c = c.getParent();
						if (_selectedElement == c) {
							setModel();
						}
					}
				}
				break;
			case MOVE_CRITERION:
				criterion = (Criterion) event.getNewValue();
				while (criterion.getParent() != null) {
					criterion = criterion.getParent();
					if (_selectedElement == criterion) {
						setModel();
					}
				}
				criterion = (Criterion) event.getOldValue();
				if (criterion != null) {
					while (criterion.getParent() != null) {
						criterion = criterion.getParent();
						if (_selectedElement == criterion) {
							setModel();
						}
					}
				}
				break;
			case MODIFY_CRITERION:
				criterion = (Criterion) event.getNewValue();
				while (criterion.getParent() != null) {
					criterion = criterion.getParent();
					if (_selectedElement == criterion) {
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
		
		switch (event.getChange()) {
			case ADD_EXPERT:
				expert = (Expert) event.getNewValue();
				while (expert.getParent() != null) {
					expert = expert.getParent();
					if (_selectedElement == expert) {
						setModel();
					}
				}
				break;
			case REMOVE_EXPERT:
				expert = (Expert) event.getOldValue();
				while (expert.getParent() != null) {
					expert = expert.getParent();
					if (_selectedElement == expert) {
						setModel();
					}
				}
				break;
			case ADD_MULTIPLE_EXPERTS:
				experts = (List<Expert>) event.getNewValue();
				for (Expert e : experts) {
					while (e.getParent() != null) {
						e = e.getParent();
						if (_selectedElement == e) {
							setModel();
						}
					}
				}
				break;
			case REMOVE_MULTIPLE_EXPERTS:
				experts = (List<Expert>) event.getOldValue();
				for (Expert e : experts) {
					while (e.getParent() != null) {
						e = e.getParent();
						if (_selectedElement == e) {
							setModel();
						}
					}
				}
				break;
			case MOVE_EXPERT:
				expert = (Expert) event.getNewValue();
				while (expert.getParent() != null) {
					expert = expert.getParent();
					if (_selectedElement == expert) {
						setModel();
					}
				}
				expert = (Expert) event.getOldValue();
				if (expert != null) {
					while (expert.getParent() != null) {
						expert = expert.getParent();
						if (_selectedElement == expert) {
							setModel();
						}
					}
				}
				break;
			case MODIFY_EXPERT:
				expert = (Expert) event.getNewValue();
				while (expert.getParent() != null) {
					expert = expert.getParent();
					if (_selectedElement == expert) {
						setModel();
					}
				}
	
			default:
		}


		
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
		if(part instanceof ElementView) {
			setElementSelected((StructuredSelection) selection);
			setModel();
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
		
		if(_tabFolder != null) {
			if(!_tabFolder.isDisposed()) {
				for(TabItem item: _tabFolder.getItems()) {
					item.getControl().dispose();
				}
			}
			_tabFolder.dispose();
			_tabFolder = null;
		}
		getSite().getPage().removeSelectionListener(this);
		_elementSet.unregisterExpertsChangeListener(this);
		_elementSet.unregisterCriteriaChangeListener(this);
		_elementSet.unregisterAlternativesChangeListener(this);
		super.dispose();
	}
	
	private void setModel() {
		
		if (_tabFolder != null) {
			for (TabItem tabItem : _tabFolder.getItems()) {
				tabItem.getControl().dispose();
			}
			_tabFolder.dispose();
			_tabFolder = null;
		}
		if (_selectedElement != null) {
			List<ProblemElement> toDisplay = new LinkedList<ProblemElement>();
			if (_selectedElement instanceof Expert) {
				List<Expert> nextLevelExperts = new LinkedList<Expert>();
				nextLevelExperts.add((Expert) _selectedElement);
				do {
					List<Expert> aux = new LinkedList<Expert>(nextLevelExperts);
					nextLevelExperts = new LinkedList<Expert>();
					for (Expert expert : aux) {
						if (expert.hasChildrens()) {
							nextLevelExperts.addAll(expert.getChildrens());
						} else {
							toDisplay.add(expert);
						}
					}
				} while (!nextLevelExperts.isEmpty());
			} else if (_selectedElement instanceof Alternative) {
				toDisplay.add(_selectedElement);

			} else if (_selectedElement instanceof Criterion) {
				List<Criterion> nextLevelCriteria = new LinkedList<Criterion>();
				nextLevelCriteria.add((Criterion) _selectedElement);
				do {
					List<Criterion> aux = new LinkedList<Criterion>(
							nextLevelCriteria);
					nextLevelCriteria = new LinkedList<Criterion>();
					for (Criterion criterion : aux) {
						if (criterion.hasSubcriteria()) {
							nextLevelCriteria
									.addAll(criterion.getSubcriteria());
						} else {
							toDisplay.add(criterion);
						}
					}
				} while (!nextLevelCriteria.isEmpty());

			}
			_tabFolder = new TabFolder(_container, SWT.BORDER);
			for (ProblemElement element : toDisplay) {
				TabItem tabItem = new TabItem(_tabFolder, SWT.NULL);
				tabItem.setText(element.getPathId());
				ElementAssignmentsTable elementAssignmentsTable = new ElementAssignmentsTable(_tabFolder);
				tabItem.setControl(elementAssignmentsTable);
				elementAssignmentsTable.setModel(element);
			}
			if (_selectedElement instanceof Alternative) {
				_instance.setPartName(_partName + " | " + Messages.ElementAssignmentsView_Alternative); //$NON-NLS-1$
			} else if (_selectedElement instanceof Criterion) {
				_instance.setPartName(_partName + " | " + Messages.ElementAssignmentsView_Criterion); //$NON-NLS-1$
			} else {
				_instance.setPartName(_partName + " | " + Messages.ElementAssignmentsView_Expert); //$NON-NLS-1$

			}
		} else {
			_instance.setPartName(_partName);
			forceSelection();
		}
	}

	
	private void forceSelection() {
		
		if(_elementView == null) {
			for(IViewReference reference: getSite().getPage().getViewReferences()) {
				if(reference.getId().equals(ElementView.ID)) {
					_elementView = (ElementView) reference.getView(true);
				}
			}
		} else {
			StructuredSelection selection = (StructuredSelection) _elementView.getSelection();
			if(selection != null) {
				if(!selection.isEmpty()) {
					setElementSelected(selection);
					setModel();
				}
			}
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

}
