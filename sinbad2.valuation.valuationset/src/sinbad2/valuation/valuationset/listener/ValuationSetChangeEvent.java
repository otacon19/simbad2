package sinbad2.valuation.valuationset.listener;


public class ValuationSetChangeEvent {
	
	private EValuationSetChange _change;
	private Object _oldValue;
	private Object _newValue;
	private boolean _inUndoRedo;
	
	private ValuationSetChangeEvent() {
		_change = null;
		_oldValue = null;
		_newValue = null;
		_inUndoRedo = false;
	}
	
	public ValuationSetChangeEvent(EValuationSetChange change, Object oldValue, Object newValue, boolean inUndoRedo) {
		this();
		
		_change = change;
		_oldValue = oldValue;
		_newValue = newValue;
		_inUndoRedo = inUndoRedo;
	}
	
	public EValuationSetChange getChange() {
		return _change;
	}
	
	public Object getOldValue() {
		return _oldValue;
	}
	
	public Object getNewValue() {
		return _newValue;
	}
	
	public boolean getInUndoRedo() {
		return _inUndoRedo;
	}
}
