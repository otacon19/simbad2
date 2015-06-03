package sinbad2.domain.numeric.integer.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.Domain;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.integer.ui.jfreechart.NumericIntegerDomainChart;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;

public class NewNumericIntegerDomainDialog extends NewDomainDialog {

	private static final int MINIMUN = Integer.MIN_VALUE;
	private static final int MAXIMUN = Integer.MAX_VALUE;
	
	private Composite _container;
	private Label _lowerLimitLabel;
	private Label _upperLimitLabel;
	private Spinner _lowerLimitSpinner;
	private Spinner _upperLimitSpinner;
	private int _lowerLimit;
	private int _upperLimit;
	private NumericIntegerDomain _specificDomain;
	private NumericIntegerDomainChart _chart;
	private Button _inRange;
	private boolean _inRangeValue;
	private ControlDecoration _domainNameTextControlDecoration;
	private Button _okButton;
	
	
	public NewNumericIntegerDomainDialog() {
		super();
	}
	
	@Override
	public void setDomain(Domain domain) {
		super.setDomain(domain);
		_specificDomain = (NumericIntegerDomain) _domain;
		_lowerLimit = _specificDomain.getMin();
		_upperLimit = _specificDomain.getMax();
		_inRangeValue = _specificDomain.getInRange();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		_container = (Composite) super.createDialogArea(parent);
		_container.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				validate();
			}
		});
		
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 10;
		gridLayout.marginBottom = 10;
		_container.setLayout(gridLayout);
		
		Label idLabel = new Label(_container, SWT.NULL);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		idLabel.setLayoutData(gridData);
		idLabel.setText("Domain id");
		idLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
		
		Text text = new Text(_container, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		text.setLayoutData(gridData);
		text.setText(_id);
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				_id = ((Text) e.getSource()).getText().trim();
				_specificDomain.setId(_id);
				validate();
			}
		});
		
		_domainNameTextControlDecoration = createNotificationDecorator(text, _id);
		
		Label titleLabel = new Label(_container, SWT.NULL);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		gridData.verticalIndent = 15;
		titleLabel.setLayoutData(gridData);
		titleLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
		titleLabel.setText("Limits");
		
		_lowerLimitLabel = new Label(_container, SWT.NONE);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		_lowerLimitLabel.setLayoutData(gridData);
		_lowerLimitLabel.setText("Lower");
		
		_lowerLimitSpinner = new Spinner(_container, SWT.BORDER);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 90;
		if(!_inRangeValue) {
			gridData.horizontalIndent = -50;
		}
		
		_lowerLimitSpinner.setLayoutData(gridData);
		_lowerLimitSpinner.setIncrement(1);
		_lowerLimitSpinner.setMinimum(MINIMUN);
		_lowerLimitSpinner.setMaximum(MAXIMUN);
		_lowerLimitSpinner.setDigits(0);
		
		_inRange = new Button(_container, SWT.CHECK);
		_inRange.setText("Without range");
		_inRange.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 2, 2));
		
		_upperLimitLabel = new Label(_container, SWT.NONE);
		gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		_upperLimitLabel.setLayoutData(gridData);
		_upperLimitLabel.setText("Upper");
		
		_upperLimitSpinner.setLayoutData(gridData);
		_upperLimitSpinner.setIncrement(1);
		_upperLimitSpinner.setMinimum(MINIMUN);
		_upperLimitSpinner.setMaximum(MAXIMUN);
		_upperLimitSpinner.setDigits(0);
		
		_lowerLimitSpinner.setSelection(_lowerLimit);
		_upperLimitSpinner.setSelection(_upperLimit);
		
		_lowerLimitSpinner.setMaximum(_upperLimitSpinner.getSelection());
		_upperLimitSpinner.setMinimum(_lowerLimitSpinner.getSelection());
		
		Label previewLabel = new Label(_container, SWT.NONE);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		gridData.verticalIndent = 15;
		previewLabel.setLayoutData(gridData);
		previewLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD));
		previewLabel.setText("Preview");
		
		Composite composite = new Composite(_container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 4, 1));
		
		hookSpinnersListeners();
		
		DomainUIsManager manager = DomainUIsManager.getInstance();
		_chart = (NumericIntegerDomainChart) manager.newDomainChart(_specificDomain);
		_chart.initialize(_specificDomain, composite, 450, 250, SWT.BORDER);
		
		_inRange.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(((Button) e.getSource()).getSelection()) {
					_lowerLimitSpinner.setMaximum(MAXIMUN);
					_lowerLimitSpinner.setSelection(MINIMUN);
					_lowerLimitSpinner.setEnabled(false);
					_lowerLimitSpinner.setVisible(false);
					
					_upperLimitSpinner.setMinimum(MINIMUN);
					_upperLimitSpinner.setSelection(MAXIMUN);
					_upperLimitSpinner.setEnabled(false);
					_upperLimitLabel.setVisible(false);
					
					_specificDomain.setMin(_lowerLimit);
					_specificDomain.setMax(_upperLimit);
					
					_lowerLimitLabel.setText("Lower" + " " + Double.toString(Double.NEGATIVE_INFINITY));
					_upperLimitLabel.setText("Upper" + " " + Double.toString(Double.POSITIVE_INFINITY));
					
					_specificDomain.setInRange(false);
					
				} else {
					_lowerLimitLabel.setText("Lower");
					_upperLimitLabel.setText("Upper");
					
					_lowerLimitSpinner.setEnabled(true);
					_upperLimitSpinner.setEnabled(true);
					_lowerLimitSpinner.setVisible(true);
					_upperLimitSpinner.setVisible(true);
					_specificDomain.setInRange(true);
					
				}
				
				_lowerLimitLabel.pack();
				_upperLimitLabel.pack();
				
				_chart.setDomain(_specificDomain);
				validate();
				
			}
			
		});
		
		return _container;
		
	}

	private void hookSpinnersListeners() {
		ModifyListener limitsListener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				Spinner spinner = (Spinner) e.getSource();
				if(spinner == _lowerLimitSpinner) {
					if(spinner.getSelection() > _upperLimitSpinner.getSelection()) {
						spinner.setSelection(_upperLimitSpinner.getMinimum());
					}
					_lowerLimit = spinner.getSelection();
					_upperLimitSpinner.setMinimum(_lowerLimitSpinner.getSelection());
					_specificDomain.setMin(_lowerLimit);
					_chart.setDomain(_specificDomain);
				} else if(spinner == _upperLimitSpinner) {
					if(spinner.getSelection() < _lowerLimitSpinner.getSelection()) {
						spinner.setSelection(_lowerLimitSpinner.getMaximum());
					}
					_upperLimit = spinner.getSelection();
					_lowerLimitSpinner.setMaximum(_upperLimitSpinner.getSelection());
					_specificDomain.setMax(_upperLimit);
					_chart.setDomain(_specificDomain);
				}
				
				validate();
				
			}
		};
		
		_upperLimitSpinner.addModifyListener(limitsListener);
		_lowerLimitSpinner.addModifyListener(limitsListener);
		
	}

	protected void validate() {
		boolean validId, validDomain;
		
		String message = "";
		
		if(!_id.isEmpty()) {
			if(_ids.contains(_id)) {
				message = "Duplicated id";
			}
		} else {
			message = "Empty value";
		}
		
		validId = validate(_domainNameTextControlDecoration, message);
		validDomain = (!_inRangeValue || (_lowerLimit <= _upperLimit));
		
		_okButton.setEnabled(validId && validDomain);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		_okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(500, 600);
	}
}
