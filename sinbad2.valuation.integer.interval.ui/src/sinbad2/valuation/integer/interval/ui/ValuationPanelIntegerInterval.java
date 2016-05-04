package sinbad2.valuation.integer.interval.ui;

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

import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.interval.IntegerIntervalValuation;
import sinbad2.valuation.integer.interval.ui.nls.Messages;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;

public class ValuationPanelIntegerInterval extends ValuationPanel  {
	
	private Spinner _valueSpinnerMin;
	private Spinner _valueSpinnerMax;
	private Label _intervalLowerLabel;
	private Label _intervalUpperLabel;
	private int _valueMax;
	private int _valueMin;

	protected void createControls() {
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 15;
		_valuationPart.setLayout(layout);
		
		Label label = new Label(_valuationPart, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Cantarell", 11, SWT.BOLD)); //$NON-NLS-1$
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1);
		gd.verticalIndent = 15;
		label.setLayoutData(gd);
		label.setText(Messages.ValuationPanelIntegerInterval_Interval_evaluation);
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
		_intervalLowerLabel.setText(Messages.ValuationPanelIntegerInterval_Lower_limit);
		_intervalLowerLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_valueSpinnerMin = new Spinner(intervalSpinnerComposite, SWT.BORDER);
		gd = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		gd.widthHint = 80;
		_valueSpinnerMin.setLayoutData(gd);
		_valueSpinnerMin.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_intervalUpperLabel = new Label(intervalSpinnerComposite, SWT.NONE);
		_intervalUpperLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		_intervalUpperLabel.setText(Messages.ValuationPanelIntegerInterval_Upper_limit);
		_intervalUpperLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_valueSpinnerMax = new Spinner(intervalSpinnerComposite, SWT.BORDER);
		gd = new GridData(SWT.CENTER, SWT.CENTER, false, true, 1, 1);
		gd.widthHint = 80;
		_valueSpinnerMax.setLayoutData(gd);
		_valueSpinnerMax.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		if(((NumericIntegerDomain) _domain).getInRange()) {
			_valueMin = ((NumericIntegerDomain) _domain).getMin();
			_valueMax = ((NumericIntegerDomain) _domain).getMax();
			
			int value = (_valueMax / 2);
			
			_valueSpinnerMin.setMinimum((int) _valueMin);
			_valueSpinnerMin.setMaximum(value);
			_valueSpinnerMax.setMinimum(value);
			_valueSpinnerMax.setMaximum((int) _valueMax);
		} else {
			_valueMin = 0;
			_valueMax = 0;
			
			_valueSpinnerMin.setMinimum(Integer.MIN_VALUE);
			_valueSpinnerMin.setMaximum(Integer.MAX_VALUE);
			_valueSpinnerMax.setMinimum(Integer.MIN_VALUE);
			_valueSpinnerMax.setMaximum(Integer.MAX_VALUE);
		}
		
		_valueSpinnerMin.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_valueMin = _valueSpinnerMin.getSelection();
				_valueSpinnerMin.setMaximum(_valueMax);
				_valueSpinnerMax.setMinimum(_valueMin);
				selectionChange();
			}
		});
		
		_valueSpinnerMax.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				_valueMax = _valueSpinnerMax.getSelection();
				_valueSpinnerMax.setMinimum(_valueMin);
				_valueSpinnerMin.setMaximum(_valueMax);
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
			if((((IntegerIntervalValuation) _valuation).getMin() !=_valueMin) || ((int) ((IntegerIntervalValuation) _valuation).getMax() !=_valueMax)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public Valuation getNewValuation() {
		
		IntegerIntervalValuation result = null;
		
		if (_valuation == null) {
			result = (IntegerIntervalValuation) _valuationsManager.copyValuation(IntegerIntervalValuation.ID);
			result.setDomain(_domain);
		} else {
			result = (IntegerIntervalValuation) _valuation.clone();
		}
		
		result.setMin((long) _valueMin);
		result.setMax((long) _valueMax);
		
		return result;
	}

	protected void initControls() {
		
		if(_valuation != null) {
			_valueMin = (int) ((IntegerIntervalValuation) _valuation).getMin();
			_valueMax = (int) ((IntegerIntervalValuation) _valuation).getMax();
			_valueSpinnerMin.setSelection(_valueMin);
			_valueSpinnerMax.setSelection(_valueMax);
		} else {
			_valueSpinnerMin.setSelection(_valueMax / 2);
			_valueSpinnerMax.setSelection(_valueMax / 2);
		}
		
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
