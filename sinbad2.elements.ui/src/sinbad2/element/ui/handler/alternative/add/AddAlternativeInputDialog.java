package sinbad2.element.ui.handler.alternative.add;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class AddAlternativeInputDialog extends InputDialog {

	public AddAlternativeInputDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = null;
		container = (Composite) super.createDialogArea(parent);
		return container;
	}
	
	@Override
	public String getValue() {
		return super.getValue().trim();
	}

}
