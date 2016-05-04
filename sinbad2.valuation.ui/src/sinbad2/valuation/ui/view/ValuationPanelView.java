package sinbad2.valuation.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.part.ViewPart;

import sinbad2.domain.Domain;
import sinbad2.domain.valuations.DomainsValuationsManager;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.ui.ValuationUIsManager;
import sinbad2.valuation.ui.valuationpanel.ValuationPanel;
import sinbad2.valuation.ui.valuationpanel.listener.IValuationPanelListener;

public class ValuationPanelView extends ViewPart {
	
	public static final String ID = "flintstones.valuation.ui.view.valuationpanel"; //$NON-NLS-1$
	
	private ValuationUIsManager _valuationUIsManager;
	private ValuationPanel _valuationPanel;
	
	private Composite _parent;
	
	public ValuationPanelView() {
		_valuationPanel = null;
	}

	@Override
	public void createPartControl(Composite parent) {
		_valuationUIsManager = ValuationUIsManager.getInstance();
		_parent = parent;
		
	}
	
	public void setValues(IValuationPanelListener listener, Domain domain, Valuation valuation) {
		
		if(_valuationPanel != null) {
			if(!_parent.isDisposed()) {
				for(Control control: _parent.getChildren()) {
					if(!control.isDisposed()) {
						control.dispose();
					}
				}
			}
			_valuationPanel.dispose();
		}
		DomainsValuationsManager dvm = DomainsValuationsManager.getInstance();
		_valuationPanel = _valuationUIsManager.newValuationPanel(dvm.getValuationSupportedForSpecificDomain(domain.getId()));
		_valuationPanel.initialize(_parent, domain, valuation);
		_valuationPanel.registerValuationPanelListener(listener);
		_parent.layout();
	}

	@Override
	public void setFocus() {}
	
	@Override
	public void dispose() {
		
		if(_valuationPanel != null) {
			if(!_parent.isDisposed()) {
				for(Control control: _parent.getChildren()) {
					if(!control.isDisposed()) {
						control.dispose();
					}
				}
			}
			_valuationPanel.dispose();
		}
		super.dispose();
	}
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		
		if(ISizeProvider.class == adapter) {
			return new ISizeProvider() {
				
				@Override
				public int getSizeFlags(boolean width) {
					return SWT.MIN | SWT.MAX | SWT.FILL;
				}
				
				@Override
				public int computePreferredSize(boolean width, int availableParallel,int availablePerpendicular, int preferredResult) {
					return width ? 750 : 200;
				}
			};
		}
		return super.getAdapter(adapter);
	}
	
	public void removeSelection() {
		_valuationPanel.removeSelection();
	}

}
