package sinbad2.phasemethod.multigranular.elh.unification.ui.view;


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.multigranular.elh.unification.UnificationPhase;
import sinbad2.phasemethod.multigranular.elh.unification.ui.nls.Messages;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class GenerateUnificationDomain extends ViewPart implements IStepStateListener {
	
	public static final String ID = "flintstones.phasemethod.multigranular.elh.unification.ui.view.generateunificationdomain"; //$NON-NLS-1$
	
	private Composite _parent;
	private Composite _domainInfoPanel;
	private Composite _domainChartPanel;
	
	private Label _levelLabel;
	private Label _nameLabel;
	private Label _descriptionLabel;
	
	private boolean _completed;
	private boolean _loaded;
	
	private LinguisticDomainChart _domainChart;
	
	private ControlAdapter _controlListener;
	
	private UnificationPhase _unificationPhase;
	
	private RatingView _ratingView;
	
	private static FuzzySet _unifiedDomain;
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
		_completed = false;
		_loaded = false;
		
		_controlListener = null;
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_unificationPhase = (UnificationPhase) pmm.getPhaseMethod(UnificationPhase.ID).getImplementation();
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		layout.marginLeft = 20;
		layout.marginRight = 20;
		layout.marginBottom = 15;
		layout.marginTop = 20;
		_parent.setLayout(layout);

		createDomainInfoPanel();
		createDomainChartPanel();
	}
	
	private void createDomainInfoPanel() {
		_domainInfoPanel = new Composite(_parent, SWT.NONE);
		_domainInfoPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		_domainInfoPanel.setLayout(new GridLayout(2, false));

		Label level = new Label(_domainInfoPanel, SWT.NONE);
		level.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		level.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		level.setText(Messages.GenerateUnificationDomain_Level);

		_levelLabel = new Label(_domainInfoPanel, SWT.NONE);
		_levelLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE)); //$NON-NLS-1$
		_levelLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label name = new Label(_domainInfoPanel, SWT.NONE);
		name.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		name.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		name.setText(Messages.GenerateUnificationDomain_Name);

		_nameLabel = new Label(_domainInfoPanel, SWT.NONE);
		_nameLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE)); //$NON-NLS-1$
		_nameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		Label description = new Label(_domainInfoPanel, SWT.NONE);
		description.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		description.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		description.setText(Messages.GenerateUnificationDomain_Description);

		_descriptionLabel = new Label(_domainInfoPanel, SWT.NONE);
		_descriptionLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE)); //$NON-NLS-1$
		_descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
	}

	private void createDomainChartPanel() {
		_domainChartPanel = new Composite(_parent, SWT.NONE);
		_domainChartPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
	
	private void removeChart() {
		if (_domainChart != null) {
			_domainChart.getChartComposite().dispose();
		}
	}

	private void refreshDomainChart() {
		removeChart();

		Point size = _domainChartPanel.getSize();
		_domainChart = new LinguisticDomainChart();
		_domainChart.initialize(_unifiedDomain, _domainChartPanel, size.x, size.y, SWT.BORDER);
		
		if(_controlListener == null) {
			_controlListener = new ControlAdapter() {
	
				@Override
				public void controlResized(ControlEvent e) {
					refreshDomainChart();
				}
			};
			_domainChartPanel.addControlListener(_controlListener);
		}
	}

	public static FuzzySet getUnifiedDomain() {
		return _unifiedDomain;
	}

	private void generateLH() {
		List<Object[]> elhDomains = _unificationPhase.generateLH();
		Object[] result = null;
		
		if (elhDomains != null) {
			result = elhDomains.get(elhDomains.size() - 1);
		}

		if(result != null) {
			_levelLabel.setText((String) result[0]);
			_nameLabel.setText((String) result[1]);
			_unifiedDomain = ((FuzzySet) result[2]);
			_descriptionLabel.setText(_unifiedDomain.formatDescriptionDomain());
			_completed = true;
		} else {
			_levelLabel.setText(""); //$NON-NLS-1$
			_nameLabel.setText(""); //$NON-NLS-1$
			_descriptionLabel.setText(""); //$NON-NLS-1$
		}
	}

	public void activate() {
		generateLH();
		refreshDomainChart();
		_domainInfoPanel.layout();
	}
	
	@Override
	public String getPartName() {
		return Messages.GenerateUnificationDomain_Generate_unification_domain;
	}
	
	@Override
	public void setFocus() {}
	
	@Override
	public void dispose() {
		super.dispose();
		
		_completed = false;
		_controlListener = null;
		_domainChart = null;
		_unifiedDomain = null;
	} 
	
	@Override
	public void notifyStepStateChange() {
		activate();
		
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
