package sinbad2.domain.linguistic.fuzzy.ui.dialog;



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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.ui.jfreechart.LinguisticDomainChart;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;

public class AutoGenerateLinguisticDomainDialog extends NewDomainDialog {
	
	private Composite _container;
	private Label _insertLabelsLabel;
	private Text _labelsText;
	private ControlDecoration _labelsTextControlDecoration;
	private ControlDecoration _domainNameTextControlDecoration;
	private FuzzySet _specificDomain;
	private Label _previewLabel;
	private LinguisticDomainChart _chart;
	private Button _okButton;
	
	public AutoGenerateLinguisticDomainDialog() {
		super();
	}
	
	@Override
	public void setDomain(Domain domain) {
		super.setDomain(domain);
		_specificDomain = (FuzzySet) _domain;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		_container = new Composite(parent, SWT.CENTER);
		GridLayout layout = new GridLayout();
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.marginLeft = 10;
		_container.setLayout(layout);
		
		Label idLabel = new Label(_container, SWT.NULL);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		idLabel.setLayoutData(gridData);
		idLabel.setText("Domain id");
		idLabel.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		
		Text textID = new Text(_container, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		textID.setLayoutData(gridData);
		textID.setText(_id);
		textID.setFocus();
		textID.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				_id = ((Text) e.getSource()).getText().trim();
				_specificDomain.setId(_id);
				validate();
			}
		});
		
		_insertLabelsLabel = new Label(_container, SWT.NULL);
		gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gridData.verticalIndent = 15;
		_insertLabelsLabel.setLayoutData(gridData);
		_insertLabelsLabel.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.NONE));
		_insertLabelsLabel.setText("Insert label names with separator ':'");
		
		_labelsText = new Text(_container, SWT.BORDER);
		GridData gd_labelsText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_labelsText.horizontalIndent = 2;
		_labelsText.setLayoutData(gd_labelsText);
		
		_labelsText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {

				if(!_labelsText.getText().isEmpty()) {
					String[] labels = _labelsText.getText().split(":");
					_specificDomain = ((FuzzySet) _specificDomain).createTrapezoidalFunction(labels);
				}
				
				validate();
				_chart.setDomain(_specificDomain);
			}
			
		});
		
		_labelsTextControlDecoration = createNotificationDecorator(_labelsText);
		_domainNameTextControlDecoration = createNotificationDecorator(textID);
		
		_previewLabel = new Label(_container, SWT.NONE);
		_previewLabel.setFont(SWTResourceManager.getFont("Cantarell", 9, SWT.BOLD));
		_previewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
		_previewLabel.setText("Preview");
		
		Composite composite = new Composite(_container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		DomainUIsManager manager = DomainUIsManager.getInstance();
		_chart = (LinguisticDomainChart) manager.newDomainChart(_domain);
		_chart.initialize(_specificDomain, composite, 510, 195, SWT.BORDER);
		
		return _container;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(545, 420);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Auto-generated domain");
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		_okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		_okButton.setEnabled(false);
		_labelsTextControlDecoration.show();
		_domainNameTextControlDecoration.show();
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	private void validate() {		
		boolean validLabel, validId;
		String msgLabel = "", msgId = "";
		
		if(!_labelsText.getText().isEmpty()) {
			msgLabel = "";
		} else {
			msgLabel = "Empty domain";
		}
		
		if(!_id.isEmpty()) {
			if(_ids.contains(_id)) {
				msgId = "Duplicated id";
			}
		} else {
				msgId = "Empty value";
		}
		
		validLabel = validate(_labelsTextControlDecoration, msgLabel);
		validId = validate(_domainNameTextControlDecoration, msgId);
							
		_okButton.setEnabled(validLabel && validId);
				
	}
}
