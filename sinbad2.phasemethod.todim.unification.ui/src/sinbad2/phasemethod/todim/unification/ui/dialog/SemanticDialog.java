package sinbad2.phasemethod.todim.unification.ui.dialog;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.phasemethod.todim.unification.ui.nls.Messages;

public class SemanticDialog extends Dialog {

	public static final int SAVE = 100;
	public static final int CANCEL = 101;

	private String _title;
	private boolean _validFields;
	private int _num;
	private List<Text> _textValues;
	private List<ControlDecoration> _controlDecorations;
	private List<String> _values;

	/**
	 * Crea el diálogo.
	 * 
	 * @param parentShell
	 *            Contenedor del diálogo.
	 * @param values
	 *            Nombres de las etiquetas
	 */
	public SemanticDialog(Shell parentShell, String[] values) {
		super(parentShell);
		
		_values = new LinkedList<String>();
		for (String value : values) {
			_values.add(value);
		}
		_num = _values.size();
		_validFields = false;
		_title = Messages.SemanticDialog_Semantic_for_domain;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 15;
		gridLayout.numColumns = 1;

		Label titleLabel = new Label(container, SWT.NONE);
		titleLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		titleLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		titleLabel.setText(_title);

		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 15;
		gridLayout.numColumns = 2;
		Composite labelsComposite = new Composite(scrolledComposite, SWT.NONE);
		labelsComposite.setLayout(gridLayout);

		Label fieldLabel;
		GridData gridData;
		_textValues = new LinkedList<Text>();
		_controlDecorations = new LinkedList<ControlDecoration>();
		Text value;
		ControlDecoration controlDecoration;
		for (int i = 0; i < _num; i++) {
			fieldLabel = new Label(labelsComposite, SWT.NONE);
			fieldLabel.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD)); //$NON-NLS-1$
			fieldLabel.setText("Label" + " " + (i + 1)); //$NON-NLS-1$ //$NON-NLS-2$

			value = new Text(labelsComposite, SWT.BORDER);
			gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gridData.widthHint = 75;
			value.setLayoutData(gridData);
			value.setText(_values.get(i));
			_textValues.add(value);

			controlDecoration = createExceptionDecorator(value);
			_controlDecorations.add(controlDecoration);
		}

		scrolledComposite.setContent(labelsComposite);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setMinSize(labelsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setSize(200, 300);

		hookValuesModifyListener();
		validateFields();
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, CANCEL, Messages.SemanticDialog_Cancel, false);
		createButton(parent, SAVE, Messages.SemanticDialog_Save, true);
	}

	/**
	 * Crea un decorador de excepción en un campo.
	 * 
	 * @param control
	 *            Control a decorar.
	 * @return Decorador creado.
	 */
	private ControlDecoration createExceptionDecorator(Control control) {
		ControlDecoration controlDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		validate(controlDecoration, ""); //$NON-NLS-1$
		return controlDecoration;
	}

	/**
	 * Valida un campo.
	 * 
	 * @param controlDecoration
	 *            Decorador del campo.
	 * @param text
	 *            Texto del decorador.
	 * @return True si el campo es válido, False en caso contrario.
	 */
	private boolean validate(ControlDecoration controlDecoration, String text) {
		controlDecoration.setDescriptionText(text);
		if (text.isEmpty()) {
			controlDecoration.hide();
			return true;
		} else {
			controlDecoration.show();
			return false;
		}
	}

	/**
	 * Engancha los listeners de modificación de valores.
	 */
	private void hookValuesModifyListener() {
		ModifyListener modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text auxText = (Text) e.getSource();
				String auxValue = auxText.getText();
				Text text;

				for (int i = 0; i < _textValues.size(); i++) {
					text = _textValues.get(i);
					if (auxText == text) {
						_values.set(i, auxValue);
					}
				}

				validateFields();
			}
		};

		for (Text text : _textValues) {
			text.addModifyListener(modifyListener);
		}
		
		
	}

	/**
	 * Comprueba que un texto no esté en blanco.
	 * 
	 * @param text
	 *            Texto a comprobar.
	 * @param controlDecoration
	 *            Decorador del campo.
	 * @return True si el campo no está en blanco, false en caso contrario.
	 */
	private boolean validateEmptyText(Text text, ControlDecoration controlDecoration) {
		if (text.getText().isEmpty()) {
			return validate(controlDecoration, Messages.SemanticDialog_Empty_value);
		} else {
			return validate(controlDecoration, ""); //$NON-NLS-1$
		}
	}

	/**
	 * Comprueba que un texto no contenga valores inválidos.
	 * 
	 * @param text
	 *            Texto a comprobar.
	 * @param controlDecoration
	 *            Decorador del campo.
	 * @return True si el campo no contiene valores inválidos, false en caso
	 *         contrario.
	 */
	private boolean validateInvalidText(Text text, ControlDecoration controlDecoration) {
		String textValue = text.getText();

		if (textValue.contains(":")) { //$NON-NLS-1$
			return validate(controlDecoration, Messages.SemanticDialog_Illegal_value);
		} else {
			return validate(controlDecoration, ""); //$NON-NLS-1$
		}
	}

	/**
	 * Comprueba que un texto no esté duplicado.
	 * 
	 * @param text
	 *            Texto a comprobar.
	 * @param controlDecoration
	 *            Decorador del campo.
	 * @return True si el campo no está duplicado, false en caso contrario.
	 */
	private boolean validateDuplicateText(Text text, int index, ControlDecoration controlDecoration) {

		String textValue = text.getText();
		for (int i = 0; i < _textValues.size(); i++) {
			if (i != index) {
				if (textValue.equals(_textValues.get(i).getText())) {
					return validate(controlDecoration, Messages.SemanticDialog_Duplicated_value);
				}
			}
		}

		return validate(controlDecoration, ""); //$NON-NLS-1$
	}

	/**
	 * Realiza todas las validaciones.
	 */
	private void validateFields() {

		Boolean[] values = new Boolean[_num];

		// Validar campos no nulos
		Text text;
		ControlDecoration controlDecoration;
		for (int index = 0; index < _textValues.size(); index++) {
			text = _textValues.get(index);
			controlDecoration = _controlDecorations.get(index);
			values[index] = validateEmptyText(text, controlDecoration);
			if (values[index]) {
				values[index] = validateInvalidText(text, controlDecoration);
				if (values[index]) {
					values[index] = validateDuplicateText(text, index, controlDecoration);
				}
			}
		}

		_validFields = true;
		int i = 0;
		do {
			_validFields = values[i++];
		} while ((_validFields) && (i < values.length));
		Button SaveButton = getButton(SAVE);
		if (SaveButton != null) {
			SaveButton.setEnabled(_validFields);
		}
	}

	/**
	 * Devuelve los valores.
	 * 
	 * @return Valores de las etiquetas.
	 */
	public List<String> getValues() {
		return _values;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		close();
	}
}