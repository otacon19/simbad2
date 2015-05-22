package sinbad2.element.ui.handler.criterion.add;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import sinbad2.element.criterion.Criterion;

public class AddCriterionInputDialog extends InputDialog {
	
	private boolean _isSubcriterion;
	private boolean _isCost;
	private Button _isSubcriterionButton;
	private Button _isCostButton;
	private Criterion _parent;
	private AddCriterionInputValidator _validator;
	
	public AddCriterionInputDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, AddCriterionInputValidator validator, Criterion parent) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		
		_validator = validator;
		_parent = parent;
		
		//TODO cambiar esto con preferencias
		
		_isCost = true;
		_isSubcriterion = false;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = null;
		container = (Composite) super.createDialogArea(parent);
		
		if(_parent != null) {
			_isSubcriterionButton = new Button(container, SWT.CHECK);
			_isSubcriterionButton.setText("Is subcriterion of " + _parent.getPathId());
			_isSubcriterionButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					_isSubcriterion = _isSubcriterionButton.getSelection();
					_validator.setIsMember(_isSubcriterion);
					AddCriterionInputDialog.this.validateInput();
				}
			});
		}
		
		_isCostButton = new Button(container, SWT.CHECK);
		_isCostButton.setText("Cost criterion");
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
	
	public boolean isSubcriterion() {
		return _isSubcriterion;
	}
}
