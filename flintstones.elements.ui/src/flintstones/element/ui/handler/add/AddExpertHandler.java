package flintstones.element.ui.handler.add;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import flintstones.element.ElementSet;
import flintstones.element.ElementsManager;
import flintstones.element.expert.Expert;

public class AddExpertHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.expert.add";
	
	 public AddExpertHandler() {}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ElementsManager elementsManager = ElementsManager.getInstance();
		ElementSet elementSet = elementsManager.getActiveElementSet();
		
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Expert parent = (Expert) selection.getFirstElement();
		
		//TODO deshacer/rehacer
		
		AddExpertInputDialog dialog = new AddExpertInputDialog(Display.getCurrent().getActiveShell(), 
				"Add expert", "Insert expert id", "", new AddExpertInputValidator(parent, elementSet), parent);
		
		return null;
	}

	
}
