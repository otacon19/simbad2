package sinbad2.core.io.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sinbad2.core.nls.Messages;
import sinbad2.core.validator.Validator;
import sinbad2.core.workspace.Workspace;

public class SaveHandler extends AbstractHandler {
public final static String ID = "flintstones.core.io.save"; //$NON-NLS-1$
	
	private String _fileName;
	
	private SaveHandler() {}
	
	public SaveHandler(String fileName) {
		this();
		Validator.notNull(fileName);
		_fileName = fileName;
		
		if(!fileName.toLowerCase().endsWith(".flintstones")) { //$NON-NLS-1$
			throw new IllegalArgumentException(Messages.SaveHandler_Invalid_file_name);
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Workspace workspace = Workspace.getWorkspace();
		
		try {
			workspace.save(_fileName);
		} catch (Exception e) {
			throw new ExecutionException(e.getClass().getSimpleName());
		}
		
		return null;
	}
}
