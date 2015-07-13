package sinbad2.valuation.linguistic.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationPanelLinguistic extends ValuationPanel {
	
	private Combo _labelCombo;
	private LabelLinguisticDomain _label;

	protected void createControls() {
		_valuationPart.setLayout(new GridLayout(5, true));

		Label label = new Label(_valuationPart, SWT.NONE);
		label = new Label(_valuationPart, SWT.NONE);
		GridData gd = new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 3, 1);
		gd.verticalIndent = -100;
		label.setLayoutData(gd);
		label.setText("Value");
		label.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		
		new Label(_valuationPart, SWT.NONE);
		_labelCombo = new Combo(_valuationPart, SWT.BORDER);
		_labelCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		_labelCombo.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		label = new Label(_valuationPart, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		label.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		if (_valuation != null) {
			_label = ((LinguisticValuation) _valuation).getLabel();
		} else {
			_label  = ((FuzzySet) _domain).getLabelSet().getLabel(0);
		}
		
		String[] labels = new String[((FuzzySet) _domain).getLabelSet().getCardinality()];
		
		int cont = 0;
		for(LabelLinguisticDomain labelDomain: ((FuzzySet) _domain).getLabelSet().getLabels()) {
			labels[cont] = labelDomain.getName();
			cont++;
		}
		
		_labelCombo.setItems(labels);
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
			return ((LinguisticValuation) _valuation).getLabel().getName().equals(_label);
		}
	}

	@Override
	public Valuation getNewValuation() {
		
		LinguisticValuation result = null;
		
		if (_valuation == null) {
			result = (LinguisticValuation) _valuationsManager.copyValuation(LinguisticValuation.ID);
			result.setDomain(_domain);
		} else {
			result = (LinguisticValuation) _valuation.clone();
		}
		
		result.setLabel(_label);
		
		return result;
	}

	protected void initControls() {
		
		if(_valuation != null) {
			_label = ((LinguisticValuation) _valuation).getLabel();
		} else {
			_label  = ((FuzzySet) _domain).getLabelSet().getLabel(0);
		}
		
		_labelCombo.setText(_label.getName());
		selectionChange();
	}
	

	
}
