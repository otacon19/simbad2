package dialog;

import java.util.LinkedList;

import jfreechart.LinguisticDomainChart;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.semantic.IMembershipFunction;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.dialog.newDialog.NewDomainDialog;

public class AutoGenerateLinguisticDomainDialog extends NewDomainDialog {
	
	private Composite _container;
	private Label _insertLabelsLabel;
	private Text _labelsText;
	private ControlDecoration _labelsTextControlDecoration;
	private FuzzySet _specificDomain;
	private Label _previewLabel;
	private LinguisticDomainChart _chart;
	
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
		
		_container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		_container.setLayout(layout);
		
		_insertLabelsLabel = new Label(_container, SWT.NULL);
		_insertLabelsLabel.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		_insertLabelsLabel.setText("Insert label names with separator ':'");
		
		
		_labelsText = new Text(_container, SWT.BORDER);
		GridData gd_labelsText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_labelsText.horizontalIndent = 2;
		_labelsText.setLayoutData(gd_labelsText);
		
		_labelsText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String msg = "";
				
				if(!_labelsText.getText().isEmpty()) {
					//TODO hay que crear la semantica
					String[] labelStrings = _labelsText.getText().split(":");
					LinkedList<LabelLinguisticDomain> labels = new LinkedList<LabelLinguisticDomain>();
					LabelLinguisticDomain currentLabel;
					IMembershipFunction semantic;
					
					for(String label: labelStrings) {
						semantic = new TrapezoidalFunction(new double[] {_a, _b, _c, _d});
						currentLabel = new LabelLinguisticDomain(label, semantic);
					}
					
					
					Composite composite = new Composite(_container, SWT.NONE);
					composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
					DomainUIsManager manager = DomainUIsManager.getInstance();
					_chart = (LinguisticDomainChart) manager.newDomainChart(_specificDomain);
					_chart.initialize(_specificDomain, composite, 510, 195, SWT.BORDER);
				} else {
					
				}
				_chart.setDomain(_specificDomain);
				validate(_labelsTextControlDecoration, msg);
			}
		});
		
		_labelsTextControlDecoration = createNotificationDecorator(_labelsText);
		validate(_labelsTextControlDecoration, "Empty domain");
		
		new Label(_container, SWT.NONE);
		
		_previewLabel = new Label(_container, SWT.NONE);
		_previewLabel.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		_previewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));
		_previewLabel.setText("Preview");
		
		
		
		return _container;
	}
}
