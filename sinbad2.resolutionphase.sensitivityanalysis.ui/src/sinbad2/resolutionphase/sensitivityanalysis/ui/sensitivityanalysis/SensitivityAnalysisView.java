package sinbad2.resolutionphase.sensitivityanalysis.ui.sensitivityanalysis;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.core.workspace.Workspace;
import sinbad2.resolutionphase.sensitivityanalysis.ISensitivityAnalysisChangeListener;
import sinbad2.resolutionphase.sensitivityanalysis.SensitivityAnalysis;

public class SensitivityAnalysisView extends ViewPart implements ISensitivityAnalysisChangeListener {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.sensitivityanalysis"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.resolutionphase.sensitivityanalysis.ui.views.sensitivityanalysis.sensitivityanalysis_view"; //$NON-NLS-1$

	private SATable _saTable = null;
	private Composite _container;
	
	private SensitivityAnalysis _sensitivityAnalysis = null;

	private List<IChangeSATableValues> _listeners = new LinkedList<IChangeSATableValues>();
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	@Override
	public void createPartControl(Composite parent) {
		_container = parent;
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_container.setLayoutData(gridData);
		GridLayout layout = new GridLayout(1, true);
		_container.setLayout(layout);
		
		_sensitivityAnalysis = (SensitivityAnalysis) Workspace.getWorkspace().getElement(SensitivityAnalysis.ID);
		_sensitivityAnalysis.registerSensitivityAnalysisChangeListener(this);
		
		initSATable();
		hookFocusListener();

		getSite().setSelectionProvider(_saTable);
	}
	
	public SATable getSATable() {
		return _saTable;
	}
	
	private void initSATable() {
		disposeSATable();
		
		_saTable = new SATable(_container);
		_saTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_saTable.setModel(_sensitivityAnalysis.getAlternativesIds(), _sensitivityAnalysis.getCriteriaIds(), _sensitivityAnalysis.getMinimumAbsoluteChangeInCriteriaWeights(), _sensitivityAnalysis.getDecisionMatrix(), _sensitivityAnalysis.getWeights());
	}
	
	private void disposeSATable() {
		if (_saTable != null) {
			if (!_saTable.isDisposed()) {
				_saTable.dispose();
			}
		}
	}

	@Override
	public void dispose() {
		_sensitivityAnalysis.unregisterSensitivityAnalysisChangeListener(this);
		disposeSATable();
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		_container.setFocus();
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

	public void registerNotifyChangeSATableListener(IChangeSATableValues listener) {
		_listeners.add(listener);
	}

	public void unregisterNotifyChangeSATableListener(IChangeSATableValues listener) {
		_listeners.remove(listener);
	}

	public void notifyChangeSATableListener(String type) {
		for (IChangeSATableValues listener : _listeners) {
			listener.notifyChangeSATableValues(type);
		}
	}
	
	@Override
	public void notifySensitivityAnalysisChange() {
		initSATable();
		_saTable.getProvider().refreshTable();
	}
}
