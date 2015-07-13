package sinbad2.valuation.real.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationPanelReal extends ValuationPanel  {
	private Spinner _valueSpinner;
	private double _value;

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
		_valueSpinner = new Spinner(_valuationPart, SWT.BORDER);
		_valueSpinner.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		_valueSpinner.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		label = new Label(_valuationPart, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		label.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		double min = (double) ((NumericRealDomain) _domain).getMin();
		double max = (double) ((NumericRealDomain) _domain).getMax();
		_value = ((max + min) / 2);

		if (_valuation != null) {
			_value = (double) (((RealValuation) _valuation).getValue());
		}
		
		_valueSpinner.setDigits(2);
		_valueSpinner.setMinimum((int) (min * 100d));
		_valueSpinner.setMaximum((int) (max * 100d));
		_valueSpinner.setSelection((int) (_value * 100d));

		_valueSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_value = _valueSpinner.getSelection() / 100d;
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
			return ((int) ((RealValuation) _valuation).getValue() != _value);
		}
	}

	@Override
	public Valuation getNewValuation() {
		
		RealValuation result = null;
		
		if (_valuation == null) {
			result = (RealValuation) _valuationsManager.copyValuation(RealValuation.ID);
			result.setDomain(_domain);
		} else {
			result = (RealValuation) _valuation.clone();
		}
		
		result.setValue((double) _value);
		
		return result;
	}

	protected void initControls() {
		
		if(_valuation != null) {
			_value = (Double) ((RealValuation) _valuation).getValue();
		}
		_valueSpinner.setSelection((int) (_value * 100d));
		selectionChange();
	}

}
