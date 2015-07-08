package sinbad2.element.ui.handler.expert.add;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import sinbad2.element.expert.Expert;
import sinbad2.element.ui.Activator;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.preferences.PreferenceConstants;


public class AddExpertInputDialog extends InputDialog {
	
	private boolean _isMember;
	private Button _isMemberButton;
	private Expert _parentOfNewExpert;
	private AddExpertInputValidator _validator;

	public AddExpertInputDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, AddExpertInputValidator validator, Expert parent) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);

		_validator = validator;
		_parentOfNewExpert = parent;
		_isMember = Activator.getDefault().getPreferenceStore().
				getBoolean(PreferenceConstants.P_EXPERT_MEMBER_AS_DEFAULT);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = null;
		container = (Composite) super.createDialogArea(parent);
		
		if(_parentOfNewExpert != null) {
			_isMemberButton = new Button(container, SWT.CHECK);
			_isMemberButton.setText(Messages.AddExpertInputDialog_Is_member_of + " " + _parentOfNewExpert.getCanonicalId()); //$NON-NLS-1$
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
