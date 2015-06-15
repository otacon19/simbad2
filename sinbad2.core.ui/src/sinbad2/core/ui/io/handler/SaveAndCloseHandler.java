package sinbad2.core.ui.io.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sinbad2.core.workspace.Workspace;


public class SaveAndCloseHandler extends AbstractHandler {
	
	public static final String ID = "flintstones.core.ui.io.save.and.close";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		SaveHandler saveHandler = new SaveHandler();
		saveHandler.execute(event);
		
		Workspace workspace = Workspace.getWorkspace();
		if(workspace.isClean()) {
			workspace.close();
		}
		
		return null;
	}

}
