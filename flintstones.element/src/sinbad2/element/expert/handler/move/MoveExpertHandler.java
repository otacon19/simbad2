package sinbad2.element.expert.handler.move;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.expert.Expert;
import sinbad2.element.expert.operation.MoveExpertOperation;

public class MoveExpertHandler extends AbstractHandler {

	public static final String ID = "flintstones.element.expert.move";
	
	private Expert _expert;
	private Expert _newParent;
	
	private MoveExpertHandler() {}
	
	public MoveExpertHandler(Expert expert, Expert newParent) {
		this();
		_expert = expert;
		_newParent = newParent;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		IUndoableOperation operation = new MoveExpertOperation("Move expert", _expert, _newParent, elementSet);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistory.execute(operation, null, null);
		
		return null;
	}

}
