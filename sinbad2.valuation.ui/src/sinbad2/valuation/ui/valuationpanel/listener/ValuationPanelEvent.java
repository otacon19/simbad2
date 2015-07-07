package sinbad2.valuation.ui.valuationpanel.listener;

import sinbad2.valuation.Valuation;

public class ValuationPanelEvent {
	
	private EValuationPanelEvent _event;
	private Valuation _oldValuation;
	private Valuation _newValuation;
	
	private ValuationPanelEvent() {}
	
	public ValuationPanelEvent(EValuationPanelEvent event, Valuation oldValuation, Valuation newValuation) {
		this();
		
		_event = event;
		_oldValuation = oldValuation;
		_newValuation = newValuation;
	}
	
	public EValuationPanelEvent getEvent() {
		return _event;
	}
	
	public Valuation getOldValuation() {
		return _oldValuation;
	}
	
	public Valuation getNewValuation() {
		return _newValuation;
	}

}
