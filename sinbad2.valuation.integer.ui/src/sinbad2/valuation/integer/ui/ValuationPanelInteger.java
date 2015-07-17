package sinbad2.valuation.integer.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationPanelInteger extends ValuationPanel {

	private Spinner _valueSpinner;
	private int _value;

	protected void createControls() {
		_valuationPart.setLayout(new GridLayout(5, true));
		
		Label label = new Label(_valuationPart, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd.verticalIndent = 15;
		label.setLayoutData(gd);
		label.setText("Integer evaluation");
		
		Label value = new Label(_valuationPart, SWT.NONE);
		value = new Label(_valuationPart, SWT.NONE);
		gd = new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 3, 1);
		gd.verticalIndent = -100;
		value.setLayoutData(gd);
		value.setText("Value");
		value.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		
		new Label(_valuationPart, SWT.NONE);
		_valueSpinner = new Spinner(_valuationPart, SWT.BORDER);
		_valueSpinner.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		_valueSpinner.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		value = new Label(_valuationPart, SWT.NONE);
		value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		value.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		int min = ((NumericIntegerDomain) _domain).getMin();
		int max = ((NumericIntegerDomain) _domain).getMax();
		_value = ((max + min) / 2);

		if (_valuation != null) {
			_value = (int) ((IntegerValuation) _valuation).getValue();
		}

		_valueSpinner.setMinimum((int) min);
		_valueSpinner.setMaximum((int) max);
		_valueSpinner.setSelection((int) _value);

		_valueSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_value = _valueSpinner.getSelection();
				selectionChange();
			}
		});

		initControls();
	}
	
	public Object getSelection() {
		return _value;
	}
	
	public boolean differentValue() {
		
		if(_valuation == null) {
			return true;
		} else {
			return ((int) ((IntegerValuation) _valuation).getValue() != _value);
		}
	}

	@Override
	public Valuation getNewValuation() {
		
		IntegerValuation result = null;
		
		if (_valuation == null) {
			result = (IntegerValuation) _valuationsManager.copyValuation(IntegerValuation.ID);
			result.setDomain(_domain);
		} else {
			result = (IntegerValuation) _valuation.clone();
		}
		
		result.setValue((long) _value);
		
		return result;
	}

	protected void initControls() {
		
		if(_valuation != null) {
			_value = (int) ((IntegerValuation) _valuation).getValue();
		}
		_valueSpinner.setSelection((int) _value);
		selectionChange();
	}

}
