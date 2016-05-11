package sinbad2.phasemethod.retranslation.ui.view;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.analysis.AnalysisPhase;
import sinbad2.phasemethod.retranslation.RetranslationPhase;
import sinbad2.phasemethod.retranslation.ui.nls.Messages;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class Retranslation extends ViewPart implements IStepStateListener {
	
public static final String ID = "flintstones.phasemethod.multigranular.lh.retranslation.ui.view.retranslation"; //$NON-NLS-1$
	
	private Composite _parent;
	private Composite _validDomainsPanel;
	private Composite _selectedDomainPanel;
	
	private TableViewer _validDomainsViewer;
	
	private LinguisticDomainChart _chart;
	
	private ControlAdapter _controlListener;
	
	private Domain _selectedLHDomain;
	
	private boolean _completed;
	private boolean _loaded;
	
	private RetranslationPhase _retranslationPhase;
	
	private RatingView _ratingView;
	
	@Override
	public void createPartControl(Composite parent) {
		_completed = false;
		_loaded = false;
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_retranslationPhase = (RetranslationPhase) pmm.getPhaseMethod(RetranslationPhase.ID).getImplementation();
		
		_chart = null;
		_controlListener = null;
		
		_parent = parent;
		
		_selectedLHDomain = null;
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(14, true);
		layout.horizontalSpacing = 15;
		_parent.setLayout(layout);
		
		createValidDomainsPanel();
		createSelectedDomainPanel();
	}

	private void createValidDomainsPanel() {
		_validDomainsPanel = new Composite(_parent, SWT.NONE);
		_validDomainsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 20;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		_validDomainsPanel.setLayout(layout);

		_validDomainsViewer = new TableViewer(_validDomainsPanel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_validDomainsViewer.getTable().setLayoutData(gridData);
		_validDomainsViewer.getTable().setHeaderVisible(true);
		_validDomainsViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			@Override
			public void dispose() {}
			
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<Domain>) inputElement).toArray();
			}
		});

		_validDomainsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = _validDomainsViewer.getSelection();
				_selectedLHDomain = (FuzzySet) ((Object[]) ((IStructuredSelection) selection).getFirstElement())[2];
				_completed = true;
				notifyStepStateChange();
				refreshChart();
			}
		});
		
		TableViewerColumn col = new TableViewerColumn(_validDomainsViewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.Retranslation_Name);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((String) ((Object[]) element)[0]);
			}
		});

		final TableViewerColumn descriptionColumn = new TableViewerColumn(_validDomainsViewer, SWT.NONE);
		descriptionColumn.getColumn().setWidth(100);
		descriptionColumn.getColumn().setText(Messages.Retranslation_Description);
		descriptionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Domain) ((Object[]) element)[2]).formatDescriptionDomain();
			}
		});
		
		loadDomains();
		
		descriptionColumn.getColumn().pack();
	}
	
	private void loadDomains() {
		List<Object[]> domains = _retranslationPhase.getLHDomains();
		_validDomainsViewer.setInput(domains);
	}

	private void createSelectedDomainPanel() {
		_selectedDomainPanel = new Composite(_parent, SWT.NONE);
		_selectedDomainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));
		
		refreshChart();	
	}
	
	
	private void refreshChart() {
		removeChart();
		_chart = new LinguisticDomainChart();
		Point size = _selectedDomainPanel.getSize();
		_chart.initialize(_selectedLHDomain, _selectedDomainPanel, size.x, size.y, SWT.BORDER);
		
		if (_controlListener == null) {
			_controlListener = new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					refreshChart();
				}
			};
			_selectedDomainPanel.addControlListener(_controlListener);
		}
	}
	
	private void removeChart() {
		if (_chart != null) {
			_chart.getChartComposite().dispose();
		}
	}

	@Override
	public String getPartName() {
		return Messages.Retranslation_Retranslation;
	}
	
	@Override
	public void setFocus() {
		_validDomainsViewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		super.dispose();
			
		_chart = null;
		_completed = false;
		_controlListener = null;
		_loaded = false;
		_selectedLHDomain = null;
		_retranslationPhase.isActivated(false);
	}

	@Override
	public void notifyStepStateChange() {
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		AnalysisPhase analysis = (AnalysisPhase) pmm.getPhaseMethod(AnalysisPhase.ID).getImplementation();
		analysis.clear();
		if(_selectedLHDomain != null) {
			analysis.setDomain((Domain) _selectedLHDomain.clone());
		}
		
		if(_completed && !_loaded) {
			_ratingView.loadNextStep();
			_completed = false;
			_loaded = true;
		}	
	}

	@Override
	public void setRatingView(RatingView rating) {
		_ratingView = rating;
	}
	
}
