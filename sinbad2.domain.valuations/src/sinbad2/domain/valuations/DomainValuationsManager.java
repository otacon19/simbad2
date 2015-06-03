package sinbad2.domain.valuations;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DomainValuationsManager {
	
	private static DomainValuationsManager _instance = null;
	
	private Map<String, String> _valuations;
	private Map<String, String> _valuationsSupported;
	private Map<String, Set<String>> _valuationsNewDomainDialogs;
	private Map<String, Set<String>> _valuationsModifyDomainDialogs;
	
	private DomainValuationsManager() {
		_valuations = new HashMap<String, String>();
		_valuationsSupported = new HashMap<String, String>();
		_valuationsNewDomainDialogs = new HashMap<String, Set<String>>();
		_valuationsModifyDomainDialogs = new HashMap<String, Set<String>>();
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
	
	public void addSupportedValuation(String domainId, String valuationName) {
		_valuationsSupported.put(domainId, valuationName);
	}
	
	public void addValuationNewDomainDialog(String valuationID, String newDomainDialogID) {
		Set<String> valuationNewDomainDialogs = _valuationsNewDomainDialogs.get(valuationID);
		if(valuationNewDomainDialogs == null) {
			valuationNewDomainDialogs = new HashSet<String>();
			_valuationsNewDomainDialogs.put(valuationID, valuationNewDomainDialogs);
		}
		
		valuationNewDomainDialogs.add(newDomainDialogID);
		
	}
	
	public void addValuationModifyDomainDialog(String valuationID, String modifyDomainDialogID) {
		Set<String> valuationModifyDomainDialogs = _valuationsModifyDomainDialogs.get(valuationID);
		
		if(valuationModifyDomainDialogs == null) {
			valuationModifyDomainDialogs = new HashSet<String>();
			_valuationsModifyDomainDialogs.put(valuationID, valuationModifyDomainDialogs);
		}
		
		valuationModifyDomainDialogs.add(modifyDomainDialogID);
		
	}
	
	public boolean hasNewDomainDialogs(String valuationID) {
		return _valuationsNewDomainDialogs.containsKey(valuationID);
	}
	
	public List<String> getValuationNewDomainDialogs(String valuationID) {
		List<String> result = new LinkedList<String>();
		Set<String> valuationNewDomainDialogs = _valuationsNewDomainDialogs.get(valuationID);
		
		if(valuationNewDomainDialogs != null) {
			result.addAll(valuationNewDomainDialogs);
			Collections.sort(result);
		}
		
		return result;
	}
	
	public List<String> getValuationModifyDomainDialogs(String valuationID) {
		List<String> result = new LinkedList<String>();
		Set<String> valuationModifyDomainDialogs = _valuationsModifyDomainDialogs.get(valuationID);
		
		if(valuationModifyDomainDialogs != null) {
			result.addAll(valuationModifyDomainDialogs);
			Collections.sort(result);
		}
		
		return result;
	}
	
	public Map<String, Set<String>> getValuationsNewDomainDialogs() {
		return _valuationsNewDomainDialogs;
	}
	
	public Map<String, Set<String>> getValuationsModifyDomainDialogs() {
		return _valuationsModifyDomainDialogs;
	}
	
	public String getTypeOfValuation(String domainId) {
		return _valuations.get(domainId);
	}
	
	public Map<String, String> getValuationsSupported() {
		return _valuationsSupported;
	}
	
}
