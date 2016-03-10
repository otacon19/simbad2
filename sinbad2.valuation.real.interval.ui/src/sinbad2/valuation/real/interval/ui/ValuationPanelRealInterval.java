package sinbad2.valuation.real.interval.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationPanelRealInterval extends ValuationPanel {
	
	private Spinner _valueSpinnerMin;
	private Spinner _valueSpinnerMax;
	private Label _intervalLowerLabel;
	private Label _intervalUpperLabel;
	private double _valueMax;
	private double _valueMin;

	protected void createControls() {
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 15;
		_valuationPart.setLayout(layout);
		
		Label label = new Label(_valuationPart, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD));
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd.verticalIndent = 15;
		label.setLayoutData(gd);
		label.setText("Interval evaluation");
		label.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		Composite spinnerComposite = new Composite(_valuationPart, SWT.NONE);
		spinnerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		spinnerComposite.setLayout(new GridLayout(1, false));
		spinnerComposite.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		Composite intervalSpinnerComposite = new Composite(spinnerComposite, SWT.NONE);
		intervalSpinnerComposite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
		intervalSpinnerComposite.setLayout(new GridLayout(2,  false));
		intervalSpinnerComposite.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		 gd = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		_intervalLowerLabel = new Label(intervalSpinnerComposite, SWT.NONE);
		_intervalLowerLabel.setLayoutData(gd);
		_intervalLowerLabel.setText("Lower limit");
		_intervalLowerLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_valueSpinnerMin = new Spinner(intervalSpinnerComposite, SWT.BORDER);
		gd = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		gd.widthHint = 80;
		_valueSpinnerMin.setLayoutData(gd);
		_valueSpinnerMin.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_intervalUpperLabel = new Label(intervalSpinnerComposite, SWT.NONE);
		_intervalUpperLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		_intervalUpperLabel.setText("Upper limit");
		_intervalUpperLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_valueSpinnerMax = new Spinner(intervalSpinnerComposite, SWT.BORDER);
		gd = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		gd.widthHint = 80;
		_valueSpinnerMax.setLayoutData(gd);
		_valueSpinnerMax.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		int value = 0;
		
		if(((NumericRealDomain) _domain).getInRange()) {
			
			_valueMin = ((NumericRealDomain) _domain).getMin();
			_valueMax = ((NumericRealDomain) _domain).getMax();
			
			 value = ((int)_valueMax / 2);
			
			_valueSpinnerMin.setDigits(2);
			_valueSpinnerMin.setMinimum((int) (_valueMin * 100d));
			_valueSpinnerMin.setMaximum((int) (value * 100d));
			_valueSpinnerMax.setDigits(2);
			_valueSpinnerMax.setMinimum((int) (value * 100d));
			_valueSpinnerMax.setMaximum((int) (_valueMax * 100d));
		} else {
			_valueMin = 0;
			_valueMax = 0;
			
			_valueSpinnerMin.setDigits(2);
			_valueSpinnerMin.setMinimum((int) Double.NEGATIVE_INFINITY);
			_valueSpinnerMin.setMaximum((int) Double.POSITIVE_INFINITY);
			_valueSpinnerMax.setDigits(2);
			_valueSpinnerMax.setMinimum((int) Double.NEGATIVE_INFINITY);
			_valueSpinnerMax.setMaximum((int) Double.POSITIVE_INFINITY);
		}
		
		
		_valueSpinnerMin.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_valueMin = _valueSpinnerMin.getSelection() / 100d;
				_valueSpinnerMin.setMaximum((int) (_valueMax * 100d));
				_valueSpinnerMax.setMinimum((int) (_valueMin * 100d));
				selectionChange();
			}
		});
		
		_valueSpinnerMax.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_valueMax = _valueSpinnerMax.getSelection() / 100d;
				_valueSpinnerMax.setMinimum((int) (_valueMin * 100d));
				_valueSpinnerMin.setMaximum((int) (_valueMax * 100d));
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
			if(((double) ((RealIntervalValuation) _valuation).getMin() != _valueMin) || ((double) ((RealIntervalValuation) _valuation).getMax() != _valueMax)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public Valuation getNewValuation() {
		
		RealIntervalValuation result = null;
		
		if (_valuation == null) {
			result = (RealIntervalValuation) _valuationsManager.copyValuation(RealIntervalValuation.ID);
			result.setDomain(_domain);
		} else {
			result = (RealIntervalValuation) _valuation.clone();
		}
		
		result.setMin((double) _valueMin);
		result.setMax((double) _valueMax);
		
		return result;
	}

	protected void initControls() {
		
		if(_valuation != null) {
			_valueMin = ((RealIntervalValuation) _valuation).getMin();
			_valueMax = ((RealIntervalValuation) _valuation).getMax();
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
