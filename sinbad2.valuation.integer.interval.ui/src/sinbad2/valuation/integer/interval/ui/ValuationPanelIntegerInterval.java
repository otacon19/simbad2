package sinbad2.valuation.integer.interval.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.interval.IntegerInterval;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationPanelIntegerInterval extends ValuationPanel  {
	
	private Spinner _valueSpinnerMin;
	private Spinner _valueSpinnerMax;
	private Label _intervalLowerLabel;
	private Label _intervalUpperLabel;
	private Button _applicableCheckBox;
	private int _valueMax;
	private int _valueMin;

	protected void createControls() {
		_valuationPart.setLayout(new GridLayout(5, true));

		_intervalLowerLabel = new Label(_valuationPart, SWT.NONE);
		GridData gd = new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 3, 1);
		_intervalLowerLabel.setLayoutData(gd);
		_intervalLowerLabel.setText("Lower limit");
		_intervalLowerLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		new Label(_valuationPart, SWT.NONE);
		_valueSpinnerMin = new Spinner(_valuationPart, SWT.BORDER);
		_valueSpinnerMin.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		_valueSpinnerMin.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_intervalUpperLabel = new Label(_valuationPart, SWT.NONE);
		_intervalUpperLabel.setLayoutData(gd);
		_intervalUpperLabel.setText("Upper limit");
		_intervalUpperLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		
		_valueSpinnerMax = new Spinner(_valuationPart, SWT.BORDER);
		_valueSpinnerMax.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		_valueSpinnerMax.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		
		_valueMin = ((NumericIntegerDomain) _domain).getMin();
		_valueMax = ((NumericIntegerDomain) _domain).getMax();
		int value = ((_valueMax + _valueMin) / 2);
		
		_valueSpinnerMin.setMinimum((int) _valueMin);
		_valueSpinnerMin.setMaximum(value);
		_valueSpinnerMin.setSelection((int) value);
		_valueSpinnerMax.setMinimum(value);
		_valueSpinnerMax.setMaximum((int) _valueMax);
		_valueSpinnerMax.setSelection((int) value);

		_valueSpinnerMin.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_valueMin = _valueSpinnerMin.getSelection();
				selectionChange();
			}
		});
		
		_valueSpinnerMax.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_valueMax = _valueSpinnerMax.getSelection();
				selectionChange();
			}
		});
		
		_applicableCheckBox = new Button(_valuationPart, SWT.CHECK);
		gd = new GridData(SWT.CENTER, SWT.CENTER, false, true, 2, 1);
		_applicableCheckBox.setLayoutData(gd);
		_applicableCheckBox.setText("Not applicable");
		
		_applicableCheckBox.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = _applicableCheckBox.getSelection();
				_intervalLowerLabel.setEnabled(!selection);
				_intervalUpperLabel.setEnabled(!selection);
				_valueSpinnerMin.setEnabled(!selection);
				_valueSpinnerMax.setEnabled(!selection);	
			}
		});
		

		initControls();
	}
	
	public Object getValueMax() {
		return _valueMax;
	}
	
	public Object getValueMin() {
		return _valueMin;
	}
	
	public boolean differentValue() {
		
		if(_valuation == null) {
			return true;
		} else {
			if((((IntegerInterval) _valuation).getMin() !=_valueMin) || ((int) ((IntegerInterval) _valuation).getMax() !=_valueMax)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public Valuation getNewValuation() {
		
		IntegerInterval result = null;
		
		if (_valuation == null) {
			result = (IntegerInterval) _valuationsManager.copyValuation(IntegerInterval.ID);
			result.setDomain(_domain);
		} else {
			result = (IntegerInterval) _valuation.clone();
		}
		
		result.setMin((long) _valueMin);
		result.setMax((long) _valueMax);
		
		return result;
	}

	protected void initControls() {
		
		if(_valuation != null) {
			_valueMin = (int) ((IntegerInterval) _valuation).getMin();
			_valueMax = (int) ((IntegerInterval) _valuation).getMax();
		}
		_valueSpinnerMin.setSelection((int) _valueMin);
		_valueSpinnerMax.setSelection((int) _valueMax);
		selectionChange();
	}
	
	@Override
	public Object getSelection() {
		List<Integer> minMax = new LinkedList<Integer>();
		minMax.add(_valueMin);
		minMax.add(_valueMax);
		
		return minMax;
	}

}
