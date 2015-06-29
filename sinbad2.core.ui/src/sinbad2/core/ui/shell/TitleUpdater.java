package sinbad2.core.ui.shell;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import sinbad2.core.ui.nls.Messages;
import sinbad2.core.workspace.Workspace;
import sinbad2.core.workspace.listener.IWorkspaceListener;
import sinbad2.core.workspace.listener.WorkspaceChangeEvent;

public class TitleUpdater implements IWorkspaceListener {
	
	private Workspace _workspace;
	private String _defaultText;
	private String _fileName;
	private Shell _shell;
	private boolean _fileOpen;
	private boolean _modifyState;
	
	public TitleUpdater() {
		_fileOpen = false;
		_modifyState = false;
		_defaultText = null;
		_shell = null;
		_workspace = Workspace.getWorkspace();
		_fileName = _workspace.getFileName();
		_workspace.registerWorkspaceListener(this);
	}

	@Override
	public void notifyWorkspaceChange(WorkspaceChangeEvent event) {
		
		switch (event.getWorkspaceChange()) {
		case NEW_FILE:
			_fileName = (String) event.getNewValue();
			_fileOpen = (_fileName != null);
			_modifyState = false;
			setTitle();
			break;
		case HASH_CODE_SAVED:
			_modifyState = false;
			setTitle();
			break;
		case HASH_CODE_MODIFIED:
			long hasCode = (Long) event.getNewValue();
			boolean oldModifyState = _modifyState;
			_modifyState = (_workspace.getSavedHashCode() != hasCode);
			if(_modifyState != oldModifyState) {
				setTitle();
			}
			break;
		case FILE_REMOVED:
			_shell.getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					_modifyState = true;
					_fileName = null;
					_fileOpen = false;
					setTitle();
					
					MessageDialog.openInformation(_shell, Messages.TitleUpdater_File_removed, Messages.TitleUpdater_File_removed);		
				}
			});
			break;
		default:
			break;
		}
		
	}
	
	private void setTitle() {
		String title = new String();
		
		if(_shell == null) {
			_shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			_defaultText = _shell.getText();
		}
		
		if(_modifyState) {
			title += "*"; //$NON-NLS-1$
		}
		
		if(_fileOpen) {
			title += _fileName + " - "; //$NON-NLS-1$
		}
		
		title += _defaultText;
		
		_shell.setText(title);
	}

}
