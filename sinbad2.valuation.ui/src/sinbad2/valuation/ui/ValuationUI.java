package sinbad2.valuation.ui;

public class ValuationUI {
	
	private String _valuation;
	private ValuationUIRegistryExtension _registry;
	
	public ValuationUI() {
		_valuation = null;
		_registry = null;
	}
	
	public void setValuation(String valuation) {
		_valuation = valuation;
	}
	
	public String getValuation() {
		return _valuation;
	}
	
	public void setRegistry(ValuationUIRegistryExtension registry) {
		_registry = registry;
	}
	
	public ValuationUIRegistryExtension getRegistry() {
		return _registry;
	}
}
