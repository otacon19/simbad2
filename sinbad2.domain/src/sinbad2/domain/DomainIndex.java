package sinbad2.domain;

import java.util.HashMap;
import java.util.Map;

public class DomainIndex {
	
	private final String PREFIX = "D";
	private Map<String, String> _idIndex;
	private Map<String, Domain> _domainIndex;
	
	private DomainIndex() {
		_idIndex = new HashMap<String, String>();
		_domainIndex = new HashMap<String, Domain>();
	}
	
	public DomainIndex(DomainSet domainSet) {
		this();
		
		int cont = 1;
		String index;
		String[] ids = domainSet.getAllDomainsIds();
		
		for(String id: ids) {
			index = PREFIX + Integer.toString(cont);
			cont++;
			
			_idIndex.put(index, id);
			_domainIndex.put(index, domainSet.getDomain(id));
		}
	}
	
	public Map<String, String> getIdIndex() {
		return _idIndex;
	}
	
	public Map<String, Domain> getDomainIndex() {
		return _domainIndex;
	}
	
	public Domain getDomain(String index) {
		return _domainIndex.get(index);
	}
	
	public String getIndex(Domain domain) {
		
		for(String index: _domainIndex.keySet()) {
			if(_domainIndex.get(index) == domain) {
				return index;
			}
		}
		return null;
	}

}
