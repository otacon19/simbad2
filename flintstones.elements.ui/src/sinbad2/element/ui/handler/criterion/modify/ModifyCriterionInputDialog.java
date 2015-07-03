package sinbad2.element.ui.handler.criterion.modify;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import sinbad2.element.ui.nls.Messages;

public class ModifyCriterionInputDialog extends InputDialog {
	
	private boolean _isCost;
	private Button _isCostButton;

	public ModifyCriterionInputDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, Boolean isCost, ModifyCriterionInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		
		_isCost = isCost;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = null;
		
		container =(Composite) super.createDialogArea(parent);
		_isCostButton = new Button(container, SWT.CHECK);
		_isCostButton.setText(Messages.ModifyCriterionInputDialog_Cost_criterion);
		_isCostButton.setSelection(_isCost);
		_isCostButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_isCost = _isCostButton.getSelection();
			}
		});
		
		return container;
	}
	
	@Override
	public String getValue() {
		return super.getValue().trim();
	}
	
	public boolean isCost() {
		return _isCost;
	}

}
