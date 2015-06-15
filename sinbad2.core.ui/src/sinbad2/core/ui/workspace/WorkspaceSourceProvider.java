package sinbad2.core.ui.workspace;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

import sinbad2.core.ui.io.handler.SaveHandler;
import sinbad2.core.ui.shell.TitleUpdater;
import sinbad2.core.workspace.Workspace;
import sinbad2.core.workspace.listener.IWorkspaceListener;
import sinbad2.core.workspace.listener.WorkspaceChangeEvent;

public class WorkspaceSourceProvider extends AbstractSourceProvider implements
	IWorkspaceListener {
	
	public final static String SAVE_STATE = "flintstones.ui.io.save.state";
	public final static String ENABLED = "ENABLED";
	public final static String DISENABLED = "DISENABLED";
	
	private TitleUpdater _shellTitleMessageManager;
	private Workspace _workspace;
	private boolean _changesToSave;
	private long _savedHashCode;
	
	public WorkspaceSourceProvider() {
		_changesToSave = false;
		_workspace = Workspace.getWorkspace();
		_savedHashCode = _workspace.getSavedHashCode();
		_shellTitleMessageManager = new TitleUpdater();
		
		_workspace.registerWorkspaceListener(this);
		
		hookWorkbenchListener();
	}

	@Override
	public void dispose() {
		_workspace.unregisterWorkspaceListener(this);
		_workspace.unregisterWorkspaceListener(_shellTitleMessageManager);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getCurrentState() {
		Map<String, String> map = new HashMap<String, String>();
		
		String value = _changesToSave ? ENABLED : DISENABLED;
		map.put(SAVE_STATE, value);
		
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { SAVE_STATE };
	}

	@Override
	public void notifyWorkspaceChange(WorkspaceChangeEvent event) {
		boolean oldSaveState = _changesToSave;
		
		switch(event.getWorkspaceChange()) {
			case HASH_CODE_MODIFIED:
				_changesToSave = _savedHashCode != (Long) event.getNewValue();
				break;
			case HASH_CODE_SAVED:
				_savedHashCode = (Long) event.getNewValue();
				_changesToSave = false;
				break;
			case NEW_CONTENT:
				_savedHashCode = _workspace.getSavedHashCode();
				_changesToSave = false;
				break;
			case FILE_REMOVED:
				_savedHashCode = _workspace.getSavedHashCode();
				_changesToSave = true;
				break;
			case PRE_WORKSPACE_CLOSE:
				
				if(_changesToSave) {
					MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							SWT.ICON_WARNING | SWT.CANCEL | SWT.NO | SWT.YES);
					messageBox.setMessage("Save current changes?");
					int rc = messageBox.open();
					boolean doClose;
					
					if(SWT.YES == rc) {
						try {
							new SaveHandler().execute(new ExecutionEvent());
							doClose = !_changesToSave;
						} catch(Exception e) {
							doClose = false;
						}
					} else if(SWT.NO == rc) {
						doClose = true;
					} else {
						doClose = false;
					}
					if(!doClose) {
						_workspace.setDoClose(doClose);
					}
				}
				break;
			case POST_WORKSPACE_CLOSE:
				_savedHashCode = _workspace.getSavedHashCode();
				_changesToSave = false;
				break;
			default:
				break;
		}
		
		if(_changesToSave != oldSaveState) {
			if(_changesToSave) {
				fireSourceChanged(ISources.WORKBENCH, SAVE_STATE, ENABLED);
			} else {
				fireSourceChanged(ISources.WORKBENCH, SAVE_STATE, DISENABLED);
			}
		}
		
	}
	
	private void hookWorkbenchListener() {
		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
			
			@Override
			public boolean preShutdown(IWorkbench workbench, boolean forced) {
				_workspace.close();
				return _workspace.isClean();
			}
			
			@Override
			public void postShutdown(IWorkbench workbench) {}
		});
	}
}
