package flintstones.element.ui.handler.add;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import flintstones.element.expert.Expert;

public class AddExpertInputDialog extends InputDialog {
	
	private boolean _isMember;
	private Button _isMemberButton;
	private Expert _parent;
	private AddExpertInputValidator _validator;

	public AddExpertInputDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, AddExpertInputValidator validator, Expert parent) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);

		_validator = validator;
		_parent = parent;
		//TODO preferences
		_isMember = true;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = null;
		container = (Composite) super.createDialogArea(parent);
		
		if(_parent != null) {
			_isMemberButton = new Button(container, SWT.CHECK);
			_isMemberButton.setText("Is member of" + _parent.getFormatId());
			_isMemberButton.setSelection(_isMember);
			_isMemberButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					_isMember = _isMemberButton.getSelection();
					_validator.setIsMember(_isMember);
					AddExpertInputDialog.this.validateInput();
				}
			});
		}
		return container;
		
	}
	
	@Override
	public String getValue() {
		return super.getValue().trim();
	}
	
	public boolean isMember() {
		return _isMember;
	}

}
