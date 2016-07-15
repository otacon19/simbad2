package sinbad2.element.criterion.handler.move;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.operation.MoveCriterionOperation;
import sinbad2.element.nls.Messages;


public class MoveCriterionHandler  extends AbstractHandler {
	
	public static final String ID = "flintstones.element.criterion.move"; //$NON-NLS-1$
	
	private Criterion _criterion;
	private Criterion _newParent;
	
	private MoveCriterionHandler() {}
	
	public MoveCriterionHandler(Criterion criterion, Criterion newParent) {
		this();
		_criterion = criterion;
		_newParent = newParent;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ProblemElementsManager elementManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = elementManager.getActiveElementSet();
		
		IUndoableOperation operation = new MoveCriterionOperation(Messages.MoveCriterionHandler_Move_criterion, _criterion, _newParent, elementSet);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
		
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistory.execute(operation, null, null);
		
		return null;
	}

}
