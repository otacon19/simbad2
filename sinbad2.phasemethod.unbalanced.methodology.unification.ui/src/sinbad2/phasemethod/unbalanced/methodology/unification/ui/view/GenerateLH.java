package sinbad2.phasemethod.unbalanced.methodology.unification.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.linguistic.unbalanced.ui.jfreechart.LHChart;
import sinbad2.phasemethod.unbalanced.methodology.unification.UnificationPhase;
import sinbad2.resolutionphase.rating.ui.listener.IStepStateListener;
import sinbad2.resolutionphase.rating.ui.view.RatingView;

public class GenerateLH extends ViewPart implements IStepStateListener {
	
	public static final String ID = "flintstones.phasemethod.unbalanced.methodology.unification.ui.generatelh";
	
	private Composite _parent;
	private Composite _infoPanel;
	private Composite _chartPanel;

	private Label _lhLabel;
	
	private LHChart _chart;
	
	private ControlListener _controlListener;
	
	private int[] _lh;
	
	private boolean _completed;
	
	private RatingView _ratingView;
	
	private UnificationPhase _unification;
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		
		_chart = null;
		_controlListener = null;
		
		_unification = UnificationPhase.getInstance();
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_parent.setLayoutData(gridData);

		GridLayout layout = new GridLayout();
		layout.marginLeft = 20;
		layout.marginRight = 20;
		layout.marginBottom = 15;
		layout.marginTop = 20;
		_parent.setLayout(layout);

		Display.getCurrent().syncExec(new Runnable() {
			@Override
			public void run() {
				createChart();
			}
		});
		
		activate();
	}
	
	private void createChart() {

		_infoPanel = new Composite(_parent, SWT.NONE);
		_infoPanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		_infoPanel.setLayout(new GridLayout(2, false));

		Label lh = new Label(_infoPanel, SWT.NONE);
		lh.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		lh.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lh.setText("LH");

		_lhLabel = new Label(_infoPanel, SWT.NONE);
		_lhLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.NONE)); //$NON-NLS-1$
		_lhLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		_lhLabel.setText("Empty value");

		_chartPanel = new Composite(_parent, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		_chartPanel.setLayoutData(gridData);
	}

	private void generateLH() {
		_lh = _unification.getDomainLH().getLh();

		if(_lh != null) {
			StringBuilder description = new StringBuilder("["); //$NON-NLS-1$
			for(int i = 0; i < _lh.length; i++) {
				description.append(_lh[i]);
				if(i < (_lh.length - 1)) {
					description.append(", "); //$NON-NLS-1$
				} else {
					description.append("]"); //$NON-NLS-1$
					_lhLabel.setText(description.toString());
				}
			}
			_chart = new LHChart(_lh, _chartPanel, _chartPanel.getSize().x, _chartPanel.getSize().y - 1);
			if(_controlListener == null) {
				_controlListener = new ControlAdapter() {
					@Override
					public void controlResized(ControlEvent e) {
						_chart.dispose();
						generateLH();
					}
				};
				_chartPanel.addControlListener(_controlListener);
			}
			_completed = true;
		} else {
			if (_chart != null) {
				_chart.dispose();
				_chart = null;
				_lhLabel.setText("Empty value");
			}
		}
	}
	
	public void activate() {
		if(_lh == null) {
			generateLH();
			_chartPanel.layout();
		}
	}
	
	@Override
	public void setFocus() {
		_chartPanel.setFocus();
	}
	
	@Override
	public String getPartName() {
		return "Generate LH";
	}

	@Override
	public void notifyStepStateChange() {
		if(_completed) {
			_ratingView.loadNextStep();
			_completed = false;
		}
	}

	@Override
	public void notifyRatingView(RatingView rating) {
		_ratingView = rating;
	}

}
