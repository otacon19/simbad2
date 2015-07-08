package sinbad2.valuation.valuationset.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sinbad2.domain.Domain;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;

public class RemoveValuationOperationProvider {
	
	private ValuationSet _valuationSet;
	private Set<Domain> _domains;
	private Set<ValuationKey> _valuations;
	private ERemoveValuation _type;

	private Map<ValuationKey, Valuation> _oldValuations;
	private Map<ValuationKey, Valuation> _newValuations;

	private RemoveValuationOperationProvider() {
		_oldValuations = new HashMap<ValuationKey, Valuation>();
		_newValuations = new HashMap<ValuationKey, Valuation>();
	}

	@SuppressWarnings("unchecked")
	public RemoveValuationOperationProvider(ValuationSet valuationSet, ERemoveValuation type, Object object) {
		this();

		_valuationSet = valuationSet;
		_type = type;

		switch (_type) {
			case DOMAIN:
				_domains = new HashSet<Domain>();
				_domains.add((Domain) object);
				break;
			case VALUATIONS:
				_valuations = new HashSet<ValuationKey>((Collection<? extends ValuationKey>) object);
				break;
				
			default:
				break;
		}
	}

	public Map<ValuationKey, Valuation> check() {
		_oldValuations = _valuationSet.getValuations();
		
		switch (_type) {
			case DOMAIN:
				removeDomains(_domains);
				break;
			case VALUATIONS:
				removeValuations(_valuations);
				break;
	
			default:
				break;
			}

		if(differentValuations()) {
			return _newValuations;
		} else {
			return null;
		}

	}
	
	private void removeValuations(Set<ValuationKey> valuations) {
		
		for(ValuationKey key : _oldValuations.keySet()) {
			if(!valuations.contains(key)) {
				_newValuations.put(key, _oldValuations.get(key));
			}
		}
	}

	private void removeDomains(Set<Domain> domains) {
		Valuation valuation;
		Domain domain;
		
		for(ValuationKey key : _oldValuations.keySet()) {
			valuation = _oldValuations.get(key);
			domain = valuation.getDomain();
			if (!domains.contains(domain)) {
				_newValuations.put(key, _oldValuations.get(key));
			}
		}
	}

	private boolean differentValuations() {

		if(_oldValuations.size() != _newValuations.size()) {
			return true;
		}

		for(ValuationKey key : _oldValuations.keySet()) {
			if (!_newValuations.containsKey(key)) {
				return true;
			}

			Valuation v1 = _oldValuations.get(key);
			Valuation v2 = _newValuations.get(key);

			if (v1 == null) {
				if (v2 != null) {
					return false;
				}
			} else {
				if (!v1.equals(v2)) {
					return false;
				}
			}
		}

		return false;
	}

}
