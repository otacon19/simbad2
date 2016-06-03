package sinbad2.phasemethod.aggregation.ui.view.dialog;

import java.util.Arrays;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.element.ProblemElement;

public class ChoquetIntegralWeightsDialog extends Dialog {

	public static final int SAVE = 100;
	public static final int CANCEL = 101;
	public static final int CANCEL_ALL = 102;

	public static final int SIMPLE = 1;
	public static final int COMPLEX = 2;

	private int _type;
	private String _title;
	private List<Double> _weights;
	private Text _value;

	public ChoquetIntegralWeightsDialog(Shell parentShell, List<ProblemElement> elements, int type, String elementType,
			String elementId) {
		super(parentShell);
		_type = type;
		_title = "Values for" + " " + elementId + " (" + elementType + ")";
	}

	public ChoquetIntegralWeightsDialog(Shell parentShell, List<ProblemElement> elements, List<Double> weights,
			int type, String elementType, String elementId) {
		this(parentShell, elements, type, elementType, elementId);
		_weights = weights;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 15;
		gridLayout.numColumns = 2;

		Label titleLabel = new Label(container, SWT.NONE);
		titleLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		titleLabel.setFont(SWTResourceManager.getFont("Cantarell", 12, SWT.BOLD));
		titleLabel.setText(_title);

		GridData gridData;
		_value = new Text(container, SWT.BORDER | SWT.MULTI);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gridData.widthHint = 75;
		gridData.heightHint = 400;
		_value.setLayoutData(gridData);

		int counter = -1;
		int elements = -1;
		StringBuilder text = null;
		if (_weights != null) {
			for (Double weight : _weights) {
				if (counter == -1) {
					if (text == null) {
						text = new StringBuilder(""); //$NON-NLS-1$
					} else {
						text.append("\n"); //$NON-NLS-1$
					}
					elements = weight.intValue();
					counter = 0;
				} else {
					if (counter != elements) {
						text.append(weight.intValue() + ";"); //$NON-NLS-1$
						counter++;
					} else {
						text.append(weight);
						counter = -1;
					}
				}

			}

			_value.setText(text.toString());
		}

		hookValuesModifyListener();
		hookValuesVerifyListener();
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if (_type == COMPLEX) {
			createButton(parent, CANCEL_ALL, "Cancell all", false);
		}
		createButton(parent, CANCEL, "Cancel", false);
		createButton(parent, SAVE, "Save", true);
	}

	private void hookValuesModifyListener() {
		ModifyListener modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text auxText = (Text) e.getSource();
				String auxTextValue = auxText.getText();

				String[] entries = auxTextValue.split("\n"); //$NON-NLS-1$
				String[] values;
				_weights = new LinkedList<Double>();
				for (String entry : entries) {
					values = entry.split(";"); //$NON-NLS-1$
					_weights.add((double) (values.length - 1));
					for (String value : values) {
						_weights.add(Double.parseDouble(value));
					}
				}
			}
		};

		_value.addModifyListener(modifyListener);
	}

	private void hookValuesVerifyListener() {
		VerifyListener verifyListener = new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {
				List<String> numbers = new LinkedList<String>((Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))); 
				
				char c = e.character;
				String character = String.valueOf(c);
				if(!numbers.contains(character)) {
					if(c != ';') {
						e.doit = false;
					}
				}
			}
		};

		_value.addVerifyListener(verifyListener);
	}

	public List<Double> getWeights() {
		return _weights;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		close();
	}
}
