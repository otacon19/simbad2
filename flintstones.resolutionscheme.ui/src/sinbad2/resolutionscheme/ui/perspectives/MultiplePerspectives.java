package sinbad2.resolutionscheme.ui.perspectives;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.apache.commons.lang.WordUtils;

import sinbad2.core.workspace.Workspace;
import sinbad2.core.workspace.listener.IWorkspaceListener;
import sinbad2.core.workspace.listener.WorkspaceChangeEvent;
import sinbad2.resolutionphase.ui.ResolutionPhaseUI;
import sinbad2.resolutionphase.ui.ResolutionPhasesUIManager;
import sinbad2.resolutionscheme.ui.nls.Messages;

public class MultiplePerspectives implements IWorkspaceListener {
	
	public IWorkbenchWindow _window;
	public CBanner _switchPerspectives;
	public Composite _mainPanel;
	public ResolutionPhaseUI[] _phasesUI;
	
	private Map<String, Action> _actions;
	private boolean _validAction;
	private Map<String, ActionContributionItem> _buttons;
	private Map<String, IConfigurationElement> _perspectives;
	
	private boolean _pre_workspace_close_action;
	
	public MultiplePerspectives(IWorkbenchWindow window, CBanner switchPerspectives, ResolutionPhaseUI[] phasesUI) {
		_window = window;
		_switchPerspectives = switchPerspectives;
		_phasesUI = phasesUI;
		
		_actions = new HashMap<String, Action>();
		_buttons = new HashMap<String, ActionContributionItem>();
		
		_pre_workspace_close_action = false;
		
		Workspace.getWorkspace().registerWorkspaceListener(this);
	}
	
	public void fillSwitcherPerspectives() {
			
		makeActions();
		
		_mainPanel = new Composite(_switchPerspectives, SWT.NONE);
		
		GridLayout layout = new GridLayout(_phasesUI.length, false);
		layout.marginHeight = 2;
		_mainPanel.setLayout(layout);
		
		ActionContributionItem actionContributionItem;
		for(ResolutionPhaseUI phase: _phasesUI) {
			actionContributionItem = new ActionContributionItem(_actions.get(phase.getId()));
			actionContributionItem.fill(_mainPanel);
			actionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
			_buttons.put(phase.getId(), actionContributionItem);
		}
		
		_actions.get(_phasesUI[0].getId()).setChecked(true);
		
		_switchPerspectives.setRight(_mainPanel);
		_switchPerspectives.setRightMinimumSize(new Point(1, 45));
	}
	
	private void configureActions(Action action, String text, ImageDescriptor imageDescriptor, String toolTipText) {
		action.setText(text);
		action.setToolTipText(toolTipText);
		action.setImageDescriptor(imageDescriptor);
		action.setChecked(false);
		
	}
	
	private void configureActions() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] perspectives = reg.getConfigurationElementsFor("org.eclipse.ui.perspectives"); //$NON-NLS-1$
		
		_perspectives = new HashMap<String, IConfigurationElement>();
		String id;
		boolean find;
		int counter;
		
		for(IConfigurationElement perspective: perspectives) {
			id = perspective.getAttribute("id"); //$NON-NLS-1$
			counter = 0;
			find = false;
			do {
				if(id.equals(_phasesUI[counter].getUIId())) {
					_perspectives.put(_phasesUI[counter].getId(), perspective);
					find = true;
				} else {
					counter++;
				}
			} while ((counter < _phasesUI.length) && (!find));
		}
		
		getDataPhasesUI();
	
	}
	
	private void getDataPhasesUI() {
		IConfigurationElement perspective;
		Action action;
		String name, iconPath;
		ImageDescriptor imageDescriptor;
		
		for(ResolutionPhaseUI phase: _phasesUI) {
			perspective = _perspectives.get(phase.getId());
			action = _actions.get(phase.getId());			
			name =  WordUtils.capitalize(phase.getName());
			iconPath = perspective.getAttribute("icon"); //$NON-NLS-1$
			imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(perspective.getDeclaringExtension().getContributor().getName(), iconPath);
			
			configureActions(action, name, imageDescriptor, ""); //$NON-NLS-1$
		}	
	}
	
	private void makeActions() {
		Action action;
		_validAction = true;
		for(final ResolutionPhaseUI phase: _phasesUI) {
			action = new Action() {
				private final ResolutionPhasesUIManager resolutionPhasesUIManager = ResolutionPhasesUIManager.getInstance();
				private final String phaseUIId = phase.getId();
				private final String perspectiveId = phase.getRegistry().getUIID();
				private final IWorkbench workbench = PlatformUI.getWorkbench();
				private final IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
				
				@Override
				public void run() {
					String activeResolutionPhaseUI = resolutionPhasesUIManager.getActiveResolutionPhasesUI().getId();
					int rc = SWT.NO;
					if(!activeResolutionPhaseUI.equals(phaseUIId)) {
						if(activeResolutionPhaseUI.equals("flintstones.resolutionphase.rating.ui") && !_pre_workspace_close_action) { //$NON-NLS-1$
							MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
						    messageBox.setMessage(Messages.MultiplePerspectives_You_will_lose_all_information);
						    rc = messageBox.open();
						    if(rc == SWT.YES) {
								resolutionPhasesUIManager.activate(phaseUIId);
								try {
									workbench.showPerspective(perspectiveId, workbenchWindow);
								} catch (WorkbenchException e) {
									e.printStackTrace();
								}
								_actions.get(activeResolutionPhaseUI).setChecked(false);
								_actions.get(phaseUIId).setChecked(true);
							} else {
								_actions.get(phaseUIId).setChecked(false);
								_validAction = false;
							}
						} else {
							_pre_workspace_close_action = false;
							resolutionPhasesUIManager.activate(phaseUIId);
							try {
								workbench.showPerspective(perspectiveId, workbenchWindow);
							} catch (WorkbenchException e) {
								e.printStackTrace();
							}
							_actions.get(activeResolutionPhaseUI).setChecked(false);
							_actions.get(phaseUIId).setChecked(true);
						}
					}
				}
			};
			_actions.put(phase.getId(), action);
		}
		configureActions();
		setState();
	}
	
	private void setState() {
		boolean firstNotValid = true, enabled = true, previousEnabled = true;
		String lastEnabled = null;
		
		for(ResolutionPhaseUI resolutionPhaseUI: _phasesUI) {
			enabled = (previousEnabled) ? resolutionPhaseUI.getResolutionPhase().getImplementation().validate() : false;
			if(!enabled) {
				if(firstNotValid && !resolutionPhaseUI.getResolutionPhase().getId().equals("flintstones.resolutionphase.sensitivityanalysis")) { //$NON-NLS-1$
					enabled = true;
					lastEnabled = resolutionPhaseUI.getId();
					firstNotValid = false;
				}
			}
			_actions.get(resolutionPhaseUI.getId()).setEnabled(enabled);
			previousEnabled = enabled;
		}
		
		String activeResolutionPhaseUI = ResolutionPhasesUIManager.getInstance().getActiveResolutionPhasesUI().getId();

		if(!_actions.get(activeResolutionPhaseUI).isEnabled() && _validAction) {
			_actions.get(lastEnabled).run();
		}
	}

	@Override
	public void notifyWorkspaceChange(WorkspaceChangeEvent event) {
		switch(event.getWorkspaceChange()) {
			case HASH_CODE_MODIFIED:
				setState();
				break;
			case HASH_CODE_SAVED:
				setState();
				break;
			case NEW_CONTENT:
				setState();
				break;
			case PRE_WORKSPACE_CLOSE:
				_pre_workspace_close_action = true;
				break;
			case AGGREGATION_PHASE_CHANGED:
				setState();
				break;
			default:
		}
		
	}

}
