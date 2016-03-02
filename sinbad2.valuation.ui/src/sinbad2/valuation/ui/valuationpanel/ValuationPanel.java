package sinbad2.valuation.ui.valuationpanel;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import sinbad2.domain.Domain;
import sinbad2.domain.ui.DomainUIsManager;
import sinbad2.domain.ui.jfreechart.DomainChart;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ValuationsManager;
import sinbad2.valuation.ui.Images;
import sinbad2.valuation.ui.valuationpanel.listener.EValuationPanelEvent;
import sinbad2.valuation.ui.valuationpanel.listener.IValuationPanelListener;
import sinbad2.valuation.ui.valuationpanel.listener.ValuationPanelEvent;

public abstract class ValuationPanel {

	protected ValuationsManager _valuationsManager;
	protected Composite _valuationPart;
	
	protected Button _removeButton;
	protected Button _valuateButton;

	protected Valuation _valuation;
	protected Domain _domain;
	
	private Composite _container;
	private Composite _buttonsPart;
	private Composite _domainPart;
	
	private DomainChart _chart;

	private List<IValuationPanelListener> _listeners;
	

	public ValuationPanel() {
		_chart = null;
		_valuationsManager = ValuationsManager.getInstance();
		
		_listeners = new LinkedList<IValuationPanelListener>();
	}

	public void initialize(Composite parent, Domain domain, Valuation valuation) {
		_container = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(19, true);
		layout.horizontalSpacing = 0;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		_container.setLayout(layout);
		_container.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		_domain = domain;
		_valuation = valuation;

		createValuationPart();
		createDomainPart();
		createButtonsPart();

		createControls();
	}

	public void dispose() {
		_listeners.clear();
	}

	public void selectionChange() {
		
		if(_chart != null) {
			_chart.setSelection(getSelection());
		}
		
		changeButtonsState();
	}

	public void removeSelection() {
		notifyValuationPanelChange(new ValuationPanelEvent(EValuationPanelEvent.REMOVE_VALUATION, _valuation, null));
		_valuation = null;
		initControls();
	}

	public void registerValuationPanelListener(IValuationPanelListener listener) {
		_listeners.add(listener);
	}

	public void unregisterValuationPanelListener(IValuationPanelListener listener) {
		_listeners.remove(listener);
	}

	public void notifyValuationPanelChange(ValuationPanelEvent event) {
		for (IValuationPanelListener listener : _listeners) {
			listener.notifyValuationPanelChange(event);
		}
	}
	
	public abstract Valuation getNewValuation();

	public abstract Object getSelection();
	
	public abstract boolean differentValue();
	
	protected abstract void initControls();
	
	protected abstract void createControls();
	
	private void createValuationPart() {
		_valuationPart = new Composite(_container, SWT.NONE);
		_valuationPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 7, 1));
		_valuationPart.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
	}

	private void createDomainPart() {
		_domainPart = new Composite(_container, SWT.NONE);
		_domainPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 9, 1));
		_domainPart.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				refreshChart();
			}
		});
	}

	private void refreshChart() {
		
		if(_chart != null) {
			for (Control control : _domainPart.getChildren()) {
				if (!control.isDisposed()) {
					control.dispose();
				}
			}
		}

		if (_domain != null) {
			Point size = _domainPart.getSize();
			DomainUIsManager manager = DomainUIsManager.getInstance();
			_chart = manager.newDomainChart(_domain);
			_chart.initialize(_domain, _domainPart, size.x, size.y, SWT.NONE);
			_chart.setSelection(getSelection());
			_domainPart.layout();
		}
	}

	private void createButtonsPart() {
		_buttonsPart = new Composite(_container, SWT.NONE);
		_buttonsPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		_buttonsPart.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 10;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		_buttonsPart.setLayout(layout);

		_removeButton = new Button(_buttonsPart, SWT.BORDER);
		_removeButton.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true, 1, 1));
		_removeButton.setText("Remove");
		_removeButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE).createImage());

		_valuateButton = new Button(_buttonsPart, SWT.BORDER);
		_valuateButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1));
		_valuateButton.setImage(Images.VALUATION);
		_valuateButton.setText("Evaluate");
		
		Point sizeRemove = _removeButton.getSize();
		Point sizeValuate = _valuateButton.getSize();

		Point size = new Point((sizeRemove.x < sizeValuate.x) ? sizeValuate.x : sizeRemove.x, sizeRemove.y);
		_removeButton.setSize(size);
		_valuateButton.setSize(size);

		_removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelection();
			}

		});

		_valuateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				EValuationPanelEvent type = (_valuation == null) ? EValuationPanelEvent.NEW_VALUATION : EValuationPanelEvent.MODIFY_VALUATION;

				Valuation newValuation = getNewValuation();
				notifyValuationPanelChange(new ValuationPanelEvent(type, _valuation, newValuation));
				_valuation = newValuation;

				initControls();

			}

		});

	}
	
	private void changeButtonsState() {
		_removeButton.setEnabled(_valuation != null);

		if(_valuation != null) {
			_valuateButton.setEnabled(differentValue());
		} else {
			_valuateButton.setEnabled(true);
		}
	}

}
