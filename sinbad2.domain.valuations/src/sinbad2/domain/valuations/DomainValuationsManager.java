package sinbad2.domain.valuations;

import java.util.HashMap;
import java.util.Map;

public class DomainValuationsManager {
	
	private static DomainValuationsManager _instance = null;
	
	private Map<String, String> _valuations;
	
	private DomainValuationsManager() {
		_valuations = new HashMap<String, String>();
	}
	
	public static DomainValuationsManager getInstance() {
		
		if(_instance == null) {
			_instance = new DomainValuationsManager();
		}
		
		return _instance;
	}
	
	public void addValuation(String domainId, String nameValuation) {
		_valuations.put(domainId, nameValuation);
	}
	
	public String getTypeOfValuation(String domainId) {
		return _valuations.get(domainId);
	}

}
