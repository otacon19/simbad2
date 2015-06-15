package sinbad2.core.io.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sinbad2.core.workspace.Workspace;

public class NewHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.core.io.new";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Workspace workspace = Workspace.getWorkspace();
		workspace.close();
		return null;
	}

}
