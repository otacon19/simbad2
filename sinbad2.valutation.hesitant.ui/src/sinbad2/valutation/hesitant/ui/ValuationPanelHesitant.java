package sinbad2.valutation.hesitant.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationPanelHesitant extends ValuationPanel {
	
	private Combo _labelCombo;
	private Button _primaryButton;
	private Button _compositeButton;
	private Button _unaryRelationshipButton;
	private Button _binaryRelationshipButton;
	private LabelLinguisticDomain _label;

	protected void createControls() {
		GridLayout layout = new GridLayout(2, true);
		_valuationPart.setLayout(layout);
		
		GridData gd = new GridData(SWT.CENTER, SWT.BOTTOM, false, true, 1, 1);
		
		_primaryButton = new Button(_valuationPart, SWT.RADIO);
		_primaryButton.setLayoutData(gd);
		gd.horizontalIndent = 13;
		gd.verticalIndent = 10;
		_primaryButton.setText("Primary");
		
		 gd = new GridData(SWT.CENTER, SWT.BOTTOM, false, true, 1, 1);
		_unaryRelationshipButton = new Button(_valuationPart, SWT.RADIO);
		_unaryRelationshipButton.setLayoutData(gd);
		gd.horizontalIndent = 30;
		_unaryRelationshipButton.setText("Unary");
		
		 gd = new GridData(SWT.CENTER, SWT.TOP, false, true, 1, 1);
		_compositeButton = new Button(_valuationPart, SWT.RADIO);
		_compositeButton.setLayoutData(gd);
		gd.horizontalIndent = 30;
		gd.verticalIndent = 10;
		_compositeButton.setText("Composite");
		
		gd = new GridData(SWT.CENTER, SWT.TOP, false, true, 1, 1);
		_binaryRelationshipButton = new Button(_valuationPart, SWT.RADIO);
		_binaryRelationshipButton.setLayoutData(gd);
		gd.horizontalIndent = 30;
		gd.verticalIndent = 10;
		_binaryRelationshipButton.setText("Binary");
		
		gd = new GridData(SWT.CENTER, SWT.TOP, false, false, 2, 1);
		Label label = new Label(_valuationPart, SWT.NONE);
		label.setLayoutData(gd);
		gd.horizontalIndent = 30;
		label.setText("Value");
		label.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		String[] labels = new String[((FuzzySet) _domain).getLabelSet().getCardinality()];
		
		int cont = 0;
		for(LabelLinguisticDomain labelDomain: ((FuzzySet) _domain).getLabelSet().getLabels()) {
			labels[cont] = labelDomain.getName();
			cont++;
		}
		
		gd = new GridData(SWT.CENTER, SWT.TOP, false, true, 2, 1);
		_labelCombo = new Combo(_valuationPart, SWT.BORDER);
		_labelCombo.setLayoutData(gd);
		gd.horizontalIndent = 30;
		_labelCombo.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_labelCombo.setItems(labels);

		if (_valuation != null) {
			_label = ((HesitantValuation) _valuation).getLabel();
			_labelCombo.select(((FuzzySet) _domain).getLabelSet().getPos(((HesitantValuation) _valuation).getLabel()));
		} else {
			_label  = ((FuzzySet) _domain).getLabelSet().getLabel(0);
			_labelCombo.select(0);
		}
		
		_labelCombo.setText(_label.getName());
		
		_labelCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_label  = ((FuzzySet) _domain).getLabelSet().getLabel(_labelCombo.getText());
				selectionChange();
			}
		});

		initControls();
	}
	
	public Object getSelection() {
		return _labelCombo.getSelectionIndex();
	}
	
	public boolean differentValue() {
		
		if(_valuation == null) {
			return true;
		} else {
			return ((HesitantValuation) _valuation).getLabel().getName().equals(_label);
		}
	}

	@Override
	public Valuation getNewValuation() {
		
		HesitantValuation result = null;
		
		if (_valuation == null) {
			result = (HesitantValuation) _valuationsManager.copyValuation(HesitantValuation.ID);
			result.setDomain(_domain);
		} else {
			result = (HesitantValuation) _valuation.clone();
		}
		
		result.setLabel(_label);
		
		return result;
	}

	protected void initControls() {
		
		if(_valuation != null) {
			_label = ((HesitantValuation) _valuation).getLabel();
		} else {
			_label  = ((FuzzySet) _domain).getLabelSet().getLabel(0);
		}
		
		_labelCombo.setText(_label.getName());
		selectionChange();
	}
	
}
