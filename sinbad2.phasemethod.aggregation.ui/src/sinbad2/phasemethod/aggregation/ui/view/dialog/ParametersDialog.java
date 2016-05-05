package sinbad2.phasemethod.aggregation.ui.view.dialog;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.phasemethod.aggregation.ui.nls.Messages;

public class ParametersDialog extends Dialog {
	
	public static final int SAVE = 100;
	public static final int CANCEL = 101;
	
	private Text _parametersText;
	private String _parameters;
	
	public ParametersDialog(Shell parentShell) {
		super(parentShell);
		
		_parameters = ""; //$NON-NLS-1$
	}
	
	public ParametersDialog(Shell parentShell, List<Double> parameters) {
		super(parentShell);
		
		convertParametersToString(parameters);
	}
	
	private void convertParametersToString(List<Double> parameters) {
		for(double p: parameters) {
			_parameters += Double.toString(p) + ';';
		}
		_parameters = _parameters.substring(0, _parameters.length() - 1);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 15;
		gridLayout.numColumns = 2;
		
		Label titleLabel = new Label(container, SWT.NONE);
		titleLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		titleLabel.setFont(SWTResourceManager.getFont("Cantarell", 12, SWT.BOLD)); //$NON-NLS-1$
		titleLabel.setText(Messages.ParametersDialog_Set_parameters);

		_parametersText = new Text(container, SWT.BORDER);
		_parametersText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		_parametersText.setText(_parameters);
		
		container.pack();

		hookComboModifyListener();
		hookComboVerifyListener();
		
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, CANCEL, Messages.ParametersDialog_Cancel, false);		
		createButton(parent, SAVE, Messages.ParametersDialog_Save, true);
		
		validateFields(false);
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		close();
	}
	
	private void hookComboModifyListener() {
		ModifyListener modifyListener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				_parameters = ((Text) e.widget).getText();
			}
		};
			
		_parametersText.addModifyListener(modifyListener);
	}
	
	private void hookComboVerifyListener() {
		VerifyListener verifyListener = new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent e) {
				char currentCharacter = e.character;
				if(e.keyCode != 8) {
			        if(currentCharacter != '.' && currentCharacter != ';') {
			        	try {
			        		Integer.parseInt(String.valueOf(currentCharacter)); 
			        	} catch(NumberFormatException ex) {
			        		e.doit = false;
			        	}
			        	validateFields(true); 
			        } else {
			        	validateFields(false);
			        }
				} else {
					if(((Text) e.widget).getText().length() == 1) {
						validateFields(false);
					}
				}
			}
		};
		
		_parametersText.addVerifyListener(verifyListener);
	}

	private void validateFields(boolean state) {
		Button SaveButton = getButton(SAVE);
		if (SaveButton != null) {
			SaveButton.setEnabled(state);
		}
	}
	
	public List<Double> getParameters() {
		List<Double> result = new LinkedList<Double>();

		if(!_parameters.isEmpty()) {
			double parameter;
			String parameterS = ""; //$NON-NLS-1$
			for(int i = 0; i < _parameters.length(); ++i) {
				if(_parameters.charAt(i) != ';') {
					parameterS += _parameters.charAt(i);
				} else {
					parameter = Double.parseDouble(parameterS);
					result.add(parameter);
					parameterS = ""; //$NON-NLS-1$
				}
				
				if(i == _parameters.length() - 1) {
					parameter = Double.parseDouble(parameterS);
					result.add(parameter);
				}
			}
		}
		return result;
	}
}
