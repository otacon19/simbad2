package sinbad2.resolutionphase.frameworkstructuring.ui.view.elementassignments;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.expert.listener.ExpertsChangeEvent;
import sinbad2.element.expert.listener.IExpertsChangeListener;
import sinbad2.element.ui.view.elements.ElementView;

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
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	

}
