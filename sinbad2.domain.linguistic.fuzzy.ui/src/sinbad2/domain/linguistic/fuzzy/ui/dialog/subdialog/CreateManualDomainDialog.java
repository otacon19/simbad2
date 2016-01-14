package sinbad2.domain.linguistic.fuzzy.ui.dialog.subdialog;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.CreateManualDomainDialogChart;
import sinbad2.domain.linguistic.fuzzy.ui.nls.Messages;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;

public class CreateManualDomainDialog extends NewDomainDialog {

	private FuzzySet _currentDomain;
	private LabelLinguisticDomain _currentLabel;
	private LabelLinguisticDomain _label;
	private Double _a;
	private Double _b;
	private Double _c;
	private Double _d;
	private String _name;
	private Text _nameText;
	private Text _aValueText;
	private Text _bValueText;
	private Text _cValueText;
	private Text _dValueText;
	private ControlDecoration _nameTextControlDecoration;
	private ControlDecoration _aValueTextControlDecoration;
	private ControlDecoration _bValueTextControlDecoration;
	private ControlDecoration _cValueTextControlDecoration;
	private ControlDecoration _dValueTextControlDecoration;
	private CreateManualDomainDialogChart _chart;
	private boolean _validFields;

	public CreateManualDomainDialog(Shell parentShell, FuzzySet domain, LabelLinguisticDomain label) {
		super();
		
		_currentDomain = domain;
		_currentLabel = label;
		_label = null;
		_name = null;
		_a = null;
		_b = null;
		_c = null;
		_d = null;
		_validFields = false;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 4;
		gridLayout.numColumns = 2;

		Label lblLabelName = new Label(container, SWT.NONE);
		lblLabelName.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD)); //$NON-NLS-1$
		lblLabelName.setText(Messages.CreateManualDomainDialog_Label_name);

		Label lblPreview = new Label(container, SWT.NONE);
		lblPreview.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD)); //$NON-NLS-1$
		lblPreview.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblPreview.setText(Messages.CreateManualDomainDialog_Preview);

		_nameText = new Text(container, SWT.BORDER);
		GridData gd_nameText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_nameText.widthHint = 75;
		_nameText.setLayoutData(gd_nameText);

		_nameTextControlDecoration = createNotificationDecorator(_nameText);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 3));
		_chart = new CreateManualDomainDialogChart();
		_chart.initialize(_currentDomain, composite, 315, 150, SWT.BORDER);

		Label lblSemantic = new Label(container, SWT.NONE);
		GridData gd_lblSemantic = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSemantic.verticalIndent = 10;
		lblSemantic.setLayoutData(gd_lblSemantic);
		lblSemantic.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD)); //$NON-NLS-1$
		lblSemantic.setText(Messages.CreateManualDomainDialog_Semantic);

		Button btnTrapezoidal = new Button(container, SWT.RADIO);
		btnTrapezoidal.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnTrapezoidal.setSelection(true);
		btnTrapezoidal.setText(Messages.CreateManualDomainDialog_Trapezoidal);

		Label lblValues = new Label(container, SWT.NONE);
		GridData gd_lblValues = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblValues.verticalIndent = 10;
		lblValues.setLayoutData(gd_lblValues);
		lblValues.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD)); //$NON-NLS-1$
		lblValues.setText(Messages.CreateManualDomainDialog_Values);
		new Label(container, SWT.NONE);

		composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		GridData gd_composite = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		gd_composite.widthHint = 330;
		composite.setLayoutData(gd_composite);

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setBounds(0, 0, 70, 21);
		lblNewLabel.setText("A"); //$NON-NLS-1$

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
		lblNewLabel_1.setBounds(0, 0, 70, 21);
		lblNewLabel_1.setText("B"); //$NON-NLS-1$

		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setBounds(0, 0, 70, 21);
		lblNewLabel_2.setText("C"); //$NON-NLS-1$

		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setBounds(0, 0, 70, 21);
		lblNewLabel_3.setText("D"); //$NON-NLS-1$

		_aValueText = new Text(composite, SWT.BORDER);
		GridData gd__aValueText = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd__aValueText.widthHint = 65;
		_aValueText.setLayoutData(gd__aValueText);
		_aValueText.setBounds(0, 0, 100, 31);
		_aValueTextControlDecoration = createNotificationDecorator(_aValueText);
		validate(_aValueTextControlDecoration, Messages.CreateManualDomainDialog_Empty_value);

		_bValueText = new Text(composite, SWT.BORDER);
		GridData gd__bValueText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd__bValueText.horizontalIndent = 3;
		gd__bValueText.widthHint = 65;
		_bValueText.setLayoutData(gd__bValueText);
		_bValueText.setBounds(0, 0, 100, 31);
		_bValueTextControlDecoration = createNotificationDecorator(_bValueText);
		validate(_bValueTextControlDecoration, Messages.CreateManualDomainDialog_Empty_value);

		_cValueText = new Text(composite, SWT.BORDER);
		GridData gd__cValueText = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd__cValueText.horizontalIndent = 3;
		gd__cValueText.widthHint = 65;
		_cValueText.setLayoutData(gd__cValueText);
		_cValueText.setBounds(0, 0, 100, 31);
		_cValueTextControlDecoration = createNotificationDecorator(_cValueText);
		validate(_cValueTextControlDecoration, Messages.CreateManualDomainDialog_Empty_value);

		_dValueText = new Text(composite, SWT.BORDER);
		GridData gd__dValueText = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd__dValueText.horizontalIndent = 3;
		gd__dValueText.widthHint = 65;
		_dValueText.setLayoutData(gd__dValueText);
		_dValueText.setBounds(0, 0, 100, 31);
		_dValueTextControlDecoration = createNotificationDecorator(_dValueText);
		validate(_dValueTextControlDecoration, Messages.CreateManualDomainDialog_Empty_value);

		hookNameModifyListener();
		hookValuesModifyListener();

		if (_currentLabel != null) {
			_nameText.setText(_currentLabel.getName());
			_aValueText.setText(((Double) _currentLabel.getSemantic().getCoverage().getMin()).toString());
			_bValueText.setText(((Double) _currentLabel.getSemantic().getCenter().getMin()).toString());
			_cValueText.setText(((Double) _currentLabel.getSemantic().getCenter().getMax()).toString());
			_dValueText.setText(((Double) _currentLabel.getSemantic().getCoverage().getMax()).toString());
		}

		validateFields();

		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		validateFields();
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(441, 409);
	}

	private void hookNameModifyListener() {
		_nameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				_name = _nameText.getText();
				setLabel();
			}
		});
		
		_nameText.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String text = _nameText.getText();
				if(e.character == ' ' && text.isEmpty()) {
					e.doit = false;
					return;
				}
			}
		});
	}

	private void hookValuesModifyListener() {
		ModifyListener modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text auxText = (Text) e.getSource();
				String auxTextValue = auxText.getText();
				Double auxValue;

				try {
					auxValue = Double.parseDouble(auxTextValue);
				} catch (NumberFormatException nfe) {
					auxValue = null;
				}

				if (auxText == _aValueText) {
					_a = auxValue;
				} else if (auxText == _bValueText) {
					_b = auxValue;
				} else if (auxText == _cValueText) {
					_c = auxValue;
				} else if (auxText == _dValueText) {
					_d = auxValue;
				}

				setLabel();

			}
		};

		_aValueText.addModifyListener(modifyListener);
		_bValueText.addModifyListener(modifyListener);
		_cValueText.addModifyListener(modifyListener);
		_dValueText.addModifyListener(modifyListener);
	}

	private boolean validateEmptyText(Text text, ControlDecoration controlDecoration) {
		if (text.getText().isEmpty()) {
			return validate(controlDecoration, Messages.CreateManualDomainDialog_Empty_value);
		} else {
			return validate(controlDecoration, ""); //$NON-NLS-1$
		}
	}

	private boolean validateNotDuplicateName() {

		String name = _nameText.getText();

		if (_currentDomain != null) {
			if (_currentLabel != null) {
				if (!name.equals(_currentLabel.getName())) {
					if (_currentDomain.getLabelSet().containsLabel(name)) {
						return validate(_nameTextControlDecoration, Messages.CreateManualDomainDialog_Duplicated_name);
					} else {
						return validate(_nameTextControlDecoration, ""); //$NON-NLS-1$
					}
				} else {
					return validate(_nameTextControlDecoration, ""); //$NON-NLS-1$
				}
			} else {
				if (_currentDomain.getLabelSet().containsLabel(name)) {
					return validate(_nameTextControlDecoration, Messages.CreateManualDomainDialog_Duplicated_name);
				} else {
					return validate(_nameTextControlDecoration, ""); //$NON-NLS-1$
				}
			}
		} else {
			return validate(_nameTextControlDecoration, ""); //$NON-NLS-1$
		}

	}

	private boolean validateDoubleValue(Text text, ControlDecoration controlDecoration) {

		String value = text.getText();
		try {
			Double.parseDouble(value);
			if(Double.parseDouble(value) >= 0 && Double.parseDouble(value) <= 1) {
				return validate(controlDecoration, ""); //$NON-NLS-1$
			} else {
				return validate(controlDecoration, Messages.CreateManualDomainDialog_Invalid_value); 
			}
		} catch (Exception e) {
			return validate(controlDecoration, Messages.CreateManualDomainDialog_Invalid_value);
		}

	}

	private boolean validateOrdererValues(Text x, Text y, ControlDecoration controlDecoration) {

		double xValue = Double.parseDouble(x.getText());
		double yValue = Double.parseDouble(y.getText());

		if (xValue > yValue) {
			return validate(controlDecoration, Messages.CreateManualDomainDialog_Value_lower_than_the_previous);
		} else {
			return validate(controlDecoration, ""); //$NON-NLS-1$
		}
	}

	private void validateFields() {

		Boolean[] values = new Boolean[4];
		Text[] texts = new Text[] { _aValueText, _bValueText, _cValueText, _dValueText };
		ControlDecoration[] controlDecorations = new ControlDecoration[] {
				_aValueTextControlDecoration, _bValueTextControlDecoration,
				_cValueTextControlDecoration, _dValueTextControlDecoration };

		boolean validName;
		validName = validateEmptyText(_nameText, _nameTextControlDecoration);
		if (validName) {
			validName = validateNotDuplicateName();
		}

		for (int i = 0; i < texts.length; i++) {
			values[i] = validateEmptyText(texts[i], controlDecorations[i]);
			if (values[i]) {
				values[i] = validateDoubleValue(texts[i], controlDecorations[i]);
			}
		}

		Boolean auxValues[] = new Boolean[] { null, null, null };
		for (int i = 0; i < texts.length - 1; i++) {
			if (values[i] && values[i + 1]) {
				auxValues[i] = validateOrdererValues(texts[i], texts[i + 1], controlDecorations[i + 1]);
			}
		}

		for (int i = 0; i < auxValues.length; i++) {
			if (auxValues[i] != null) {
				values[i + 1] = auxValues[i];
			}
		}

		_validFields = validName && values[0] && values[1] && values[2] && values[3];
		Button OKButton = getButton(IDialogConstants.OK_ID);
		if (OKButton != null) {
			OKButton.setEnabled(_validFields);
		}
	}

	private void setLabel() {

		try {
			IMembershipFunction semantic = new TrapezoidalFunction(new double[] {_a, _b, _c, _d});
			_label = new LabelLinguisticDomain(_name, semantic);
		} catch (Exception e) {
			_label = null;
		}

		validateFields();

		_chart.setLabel(_label);
	}

	public LabelLinguisticDomain getLabel() {
		return _label;
	}


}
