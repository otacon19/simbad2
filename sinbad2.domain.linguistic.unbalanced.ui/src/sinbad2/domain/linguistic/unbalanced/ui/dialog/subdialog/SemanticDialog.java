package sinbad2.domain.linguistic.unbalanced.ui.dialog.subdialog;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;

public class SemanticDialog extends NewDomainDialog {
	
	private List<String> _values;
	private List<Text> _textValues;
	private List<ControlDecoration> _controlDecorations;
	private Button _okButton;
	
	public SemanticDialog(String values[]) {
		super();
		
		_values = new LinkedList<String>();
		for(String value: values) {
			_values.add(value);
		}
	}
	
	public List<String> getValues() {
		return _values;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 15;
		gridLayout.numColumns = 1;

		Label titleLabel = new Label(container, SWT.CENTER);
		titleLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true, false, 1, 1));
		titleLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
		titleLabel.setText("Semantic for domain");

		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CENTER);
		scrolledComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		
		gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 15;
		gridLayout.numColumns = 2;
		Composite labelsComposite = new Composite(scrolledComposite, SWT.CENTER);
		labelsComposite.setLayout(gridLayout);

		Label fieldLabel;
		GridData gridData;
		_textValues = new LinkedList<Text>();
		_controlDecorations = new LinkedList<ControlDecoration>();
		Text value;
		ControlDecoration controlDecoration;
		
		for (int i = 0; i < _values.size(); i++) {
			fieldLabel = new Label(labelsComposite, SWT.CENTER);
			fieldLabel.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD));
			fieldLabel.setText("Label" + " " + (i + 1)); //$NON-NLS-1$

			value = new Text(labelsComposite, SWT.BORDER);
			gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
			gridData.widthHint = 105;
			value.setLayoutData(gridData);
			value.setText(_values.get(i));
			_textValues.add(value);

			controlDecoration = createNotificationDecorator(value);
			_controlDecorations.add(controlDecoration);
		}

		scrolledComposite.setContent(labelsComposite);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setMinSize(labelsComposite.computeSize(SWT.DEFAULT,SWT.DEFAULT));
		scrolledComposite.setSize(200, 300);

		hookValuesModifyListener();
		validateFields();
		
		return container;

	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		_okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		_okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		validateFields();
	}

	private void hookValuesModifyListener() {
		ModifyListener modifyListener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				Text semanticText = (Text) e.getSource(), text;
				String semanticValueText = semanticText.getText();
				
				for(int i = 0; i < _textValues.size(); ++i) {
					text = _textValues.get(i);
					if(semanticText == text) {
						_values.set(i, semanticValueText);
					}
				}
				
				validateFields();
			}
		};
		
		for(Text text: _textValues) {
			text.addModifyListener(modifyListener);
		}
		
	}
	
	private boolean validateEmptyText(Text text, ControlDecoration controlDecoration) {
		
		if(text.getText().isEmpty()) {
			return validate(controlDecoration, "Empty Value");
		} else {
			return validate(controlDecoration, "");
		}
	}
	
	private boolean validateInvalidText(Text text, ControlDecoration controlDecoration) {
		String textValue = text.getText();
		
		if(textValue.contains(":")) {
			return validate(controlDecoration, "Illegal value");
		} else {
			return validate(controlDecoration, "");
		}
	}
	
	private boolean validateDuplicateText(Text text, int index, ControlDecoration controlDecoration) {
		String textValue = text.getText();
		
		for(int i = 0; i < _textValues.size(); ++i) {
			if(i != index) {
				if(textValue.equals(_textValues.get(i).getText())) {
					return validate(controlDecoration, "Duplicate value");
				}
			}
		}
		
		return validate(controlDecoration, "");
	}
	
	private void validateFields() {
		Boolean values[] = new Boolean[_values.size()];
		Text text;
		ControlDecoration controlDecoration;
		
		for(int i = 0; i < _textValues.size(); ++i) {
			text = _textValues.get(i);
			controlDecoration = _controlDecorations.get(i);
			values[i] = validateEmptyText(text, controlDecoration);
			if(values[i]) {
				values[i] = validateInvalidText(text, controlDecoration);
				if(values[i]) {
					values[i] = validateDuplicateText(text, i, controlDecoration);
				}
			}
		}
		
		boolean validFields;
		int i = 0;
		
		do {
			validFields = values[i++];
		} while((validFields) && i < values.length);
	
		if(_okButton != null) {
			_okButton.setEnabled(validFields);
		}
	}

}
