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
import org.eclipse.wb.swt.SWTResourceManager;

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
		label.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false, 5, 1);
		gd.verticalIndent = 15;
		label.setLayoutData(gd);
		label.setText("Linguistic evaluation");
		label.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		Label value = new Label(_valuationPart, SWT.NONE);
		value = new Label(_valuationPart, SWT.NONE);
		gd = new GridData(SWT.CENTER, SWT.BOTTOM, true, false, 3, 1);
		gd.verticalIndent = 15;
		value.setLayoutData(gd);
		value.setText("Value");
		value.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		
		String[] labels = new String[((FuzzySet) _domain).getLabelSet().getCardinality()];
		
		int cont = 0;
		for(LabelLinguisticDomain labelDomain: ((FuzzySet) _domain).getLabelSet().getLabels()) {
			labels[cont] = labelDomain.getName();
			cont++;
		}
		
		new Label(_valuationPart, SWT.NONE);
		_labelCombo = new Combo(_valuationPart, SWT.BORDER);
		_labelCombo.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 3, 1));
		_labelCombo.setItems(labels);
		_labelCombo.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		new Label(_valuationPart, SWT.NONE);
		value = new Label(_valuationPart, SWT.NONE);
		value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		value.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		if (_valuation != null && _valuation instanceof LinguisticValuation) {
			_label = ((LinguisticValuation) _valuation).getLabel();
			_labelCombo.select(((FuzzySet) _domain).getLabelSet().getPos(((LinguisticValuation) _valuation).getLabel()));
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
