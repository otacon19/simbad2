package sinbad2.valuation.real.interval.ui;

import java.util.LinkedList;
import java.util.List;

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
import sinbad2.valuation.real.interval.RealInterval;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationPanelRealInterval extends ValuationPanel {
	
	private Spinner _valueSpinnerMin;
	private Spinner _valueSpinnerMax;
	private Label _intervalLowerLabel;
	private Label _intervalUpperLabel;
	private double _valueMax;
	private double _valueMin;

	protected void createControls() {
		_valuationPart.setLayout(new GridLayout(5, true));

		_intervalLowerLabel = new Label(_valuationPart, SWT.NONE);
		GridData gd = new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 5, 1);
		_intervalLowerLabel.setLayoutData(gd);
		_intervalLowerLabel.setText("Lower limit");
		_intervalLowerLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_valueSpinnerMin = new Spinner(_valuationPart, SWT.BORDER);
		_valueSpinnerMin.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 5, 1));
		_valueSpinnerMin.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_intervalUpperLabel = new Label(_valuationPart, SWT.NONE);
		_intervalUpperLabel.setLayoutData(gd);
		_intervalUpperLabel.setText("Upper limit");
		_intervalUpperLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_valueSpinnerMax = new Spinner(_valuationPart, SWT.BORDER);
		_valueSpinnerMax.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 5, 1));
		_valueSpinnerMax.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		new Label(_valuationPart, SWT.NONE);
		
		_valueMin = ((NumericRealDomain) _domain).getMin();
		_valueMax = ((NumericRealDomain) _domain).getMax();
		int value = ((int)_valueMax / 2);
		
		_valueSpinnerMin.setDigits(2);
		_valueSpinnerMin.setMinimum((int) (_valueMin * 100d));
		_valueSpinnerMin.setMaximum((int) (value * 100d));
		_valueSpinnerMax.setDigits(2);
		_valueSpinnerMax.setMinimum((int) (value * 100d));
		_valueSpinnerMax.setMaximum((int) (_valueMax * 100d));

		_valueSpinnerMin.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_valueMin = _valueSpinnerMin.getSelection() / 100d;
				selectionChange();
			}
		});
		
		_valueSpinnerMax.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_valueMax = _valueSpinnerMax.getSelection() / 100d;
				selectionChange();
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
			if(((double) ((RealInterval) _valuation).getMin() != _valueMin) || ((double) ((RealInterval) _valuation).getMax() != _valueMax)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public Valuation getNewValuation() {
		
		RealInterval result = null;
		
		if (_valuation == null) {
			result = (RealInterval) _valuationsManager.copyValuation(RealInterval.ID);
			result.setDomain(_domain);
		} else {
			result = (RealInterval) _valuation.clone();
		}
		
		result.setMin((double) _valueMin);
		result.setMax((double) _valueMax);
		
		return result;
	}

	protected void initControls() {
		
		if(_valuation != null) {
			_valueMin = ((RealInterval) _valuation).getMin();
			_valueMax = ((RealInterval) _valuation).getMax();
			_valueSpinnerMin.setSelection((int) (_valueMin * 100d));
			_valueSpinnerMax.setSelection((int) (_valueMax * 100d));
		} else {
			_valueSpinnerMin.setSelection((int) ((_valueMax / 2) * 100d));
			_valueSpinnerMax.setSelection((int) ((_valueMax / 2) * 100d));
		}
		
		selectionChange();
	}
	
	@Override
	public Object getSelection() {
		List<Double> minMax = new LinkedList<Double>();
		minMax.add(_valueMin);
		minMax.add(_valueMax);
		
		return minMax;
	}


}
