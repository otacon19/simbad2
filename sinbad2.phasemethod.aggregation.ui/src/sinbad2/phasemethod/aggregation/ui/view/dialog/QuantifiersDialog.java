package sinbad2.phasemethod.aggregation.ui.view.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.aggregationoperator.owa.YagerQuantifiers;
import sinbad2.aggregationoperator.owa.YagerQuantifiers.QuantificationType;
import sinbad2.phasemethod.aggregation.ui.nls.Messages;

public class QuantifiersDialog extends Dialog {

	public static final int SAVE = 100;
	public static final int CANCEL = 101;
	public static final int CANCEL_ALL = 102;

	public static final int SIMPLE = 1;
	public static final int COMPLEX = 2;

	private Double _alpha;
	private Double _beta;
	private int _type;
	private String _title;
	private Combo _quantifiersCombo;

	public QuantifiersDialog(Shell parentShell, Double alpha, Double beta, int type, String elementType, String elementId) {
		super(parentShell);
		
		_alpha = alpha;
		_beta = beta;
		_type = type;
		_title = "Values for" + " " + elementId + " (" + elementType + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
		titleLabel.setText(_title);

		Label lblAlphaValue = new Label(container, SWT.NONE);
		lblAlphaValue.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD)); //$NON-NLS-1$
		lblAlphaValue.setText(Messages.QuantifiersDialog_Quantifier);

		_quantifiersCombo = new Combo(container, SWT.BORDER);
		_quantifiersCombo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));

		QuantificationType[] types = YagerQuantifiers.QuantificationType.values();
		int length = types.length;
		String[] items = new String[length];
		for(int i = 0; i < length; i++) {
			items[i] = types[i].toString();
		}
		_quantifiersCombo.setItems(items);
		_quantifiersCombo.setText(Messages.QuantifiersDialog_Select);

		if((_alpha != null) && (_beta != null)) {
			boolean find = false;
			int pos = 0;
			double[] expected = new double[] { _alpha, _beta };
			double[] result;
			do {
				result = YagerQuantifiers.getQuantificationParams(types[pos]);
				if ((expected[0] == result[0]) && (expected[1] == result[1])) {
					find = true;
				} else {
					pos++;
				}
			} while ((!find) && (pos < length));
			if(find) {
				_quantifiersCombo.select(pos);
			}
		}

		validateFields();
		container.pack();

		hookComboModifyListener();

		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if (_type == COMPLEX) {
			createButton(parent, CANCEL_ALL, Messages.QuantifiersDialog_Cancel_all, false);
		}
		createButton(parent, CANCEL, Messages.QuantifiersDialog_Cancel, false);
		createButton(parent, SAVE, Messages.QuantifiersDialog_Save, true);
		validateFields();
	}
	
	private void hookComboModifyListener() {
		ModifyListener modifyListener = new ModifyListener() {
	
			@Override
			public void modifyText(ModifyEvent e) {
				validateFields();
			}
		};

		_quantifiersCombo.addModifyListener(modifyListener);
	}

	private boolean validateFields() {

		boolean result = false;

		int selectionIndex = _quantifiersCombo.getSelectionIndex();

		if (selectionIndex != -1) {
			result = true;
			double[] values = YagerQuantifiers.getQuantificationParams(QuantificationType.valueOf(_quantifiersCombo.getItem(selectionIndex)));
			_alpha = values[0];
			_beta = values[1];
		}

		Button SaveButton = getButton(SAVE);
		if (SaveButton != null) {
			SaveButton.setEnabled(result);
		}

		return result;
	}

	public Double getAlpha() {
		return _alpha;
	}

	public Double getBeta() {
		return _beta;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		close();
	}
}

