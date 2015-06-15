package sinbad2.core.io.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import sinbad2.core.validator.Validator;
import sinbad2.core.workspace.Workspace;

public class SaveAndCloseHandler extends AbstractHandler {
	
public final static String ID = "flintstones.core.io.save.and.close";
	
	private String _fileName;
	
	private SaveAndCloseHandler() {}
	
	public SaveAndCloseHandler(String fileName) {
		this();
		Validator.notNull(fileName);
		_fileName = fileName;
		
		if(!fileName.toLowerCase().endsWith(".flintstones")) {
			throw new IllegalArgumentException("Invalid file name");
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		new SaveHandler(_fileName).execute(event);
		Workspace workspace = Workspace.getWorkspace();
		
		if(workspace.isClean()) {
			workspace.close();
		}
		
		return null;
	}

}
