package sinbad2.core.ui.io.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import sinbad2.core.ui.nls.Messages;
import sinbad2.core.workspace.Workspace;
import sinbad2.core.workspace.WorkspaceContentPersistenceException;

public class SaveHandler extends AbstractHandler {

	public final String ID = "flintstones.core.ui.io.save"; //$NON-NLS-1$
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		Workspace workspace = Workspace.getWorkspace();
		String fn = workspace.getFileName();
		
		if(fn != null) {
			sinbad2.core.io.handler.SaveHandler handler = null;
			try {
				try {
					handler = new sinbad2.core.io.handler.SaveHandler(fn);
					handler.execute(event);
					if(!fn.equals(workspace.getFileName())) {
						workspace.setFileName(fn);
					}
				} catch(ExecutionException e) {
					String cause = e.getMessage();
					String description = null;
					
					if(IOException.class.getSimpleName().equals(cause)) {
						description = Messages.SaveHandler_File_not_saving_permissions;
					} else if(WorkspaceContentPersistenceException.class.getSimpleName()
							.equals(cause)) {
						description = Messages.SaveHandler_File_not_saving_wrong;
					} else {
						description = Messages.SaveHandler_File_not_saving;
					}
					
					MessageDialog.openError(PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getShell(), 
							Messages.SaveHandler_File_not_saving, description);
				}
			} catch (IllegalArgumentException iae) {
				MessageDialog.openError(shell, 
						Messages.SaveHandler_Invalid_file_name, 
						Messages.SaveHandler_File_name_not_valid);
			}
		
			return null;
	
		} else {
			return (new SaveAsHandler().execute(event));
		}
	}
	
}
