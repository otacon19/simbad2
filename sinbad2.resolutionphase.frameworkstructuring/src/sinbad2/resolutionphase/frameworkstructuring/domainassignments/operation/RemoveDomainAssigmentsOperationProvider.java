package sinbad2.resolutionphase.frameworkstructuring.domainassignments.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sinbad2.domain.Domain;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentKey;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;

public class RemoveDomainAssigmentsOperationProvider {
	
	private DomainAssignments _domainAssignments;
	private Set<Expert> _experts;
	private Set<Alternative> _alternatives;
	private Set<Criterion> _criteria;
	private Set<Domain> _domains;
	private ERemoveDomainAssignments _type;

	private Map<DomainAssignmentKey, Domain> _oldDomainAssignments;
	private Map<DomainAssignmentKey, Domain> _newDomainAssignments;

	private RemoveDomainAssigmentsOperationProvider() {
		_oldDomainAssignments = new HashMap<DomainAssignmentKey, Domain>();
		_newDomainAssignments = new HashMap<DomainAssignmentKey, Domain>();
	}

	@SuppressWarnings("unchecked")
	public RemoveDomainAssigmentsOperationProvider(DomainAssignments domainAssignments, ERemoveDomainAssignments type, Object object) {
		this();

		_domainAssignments = domainAssignments;
		_type = type;

		switch (_type) {
		case EXPERT:
			_experts = new HashSet<Expert>();
			_experts.add((Expert) object);
			break;
		case EXPERTS:
			_experts = new HashSet<Expert>((Collection<? extends Expert>) object);
			break;
		case ALTERNATIVE:
			_alternatives = new HashSet<Alternative>();
			_alternatives.add((Alternative) object);
			break;
		case ALTERNATIVES:
			_alternatives = new HashSet<Alternative>((Collection<? extends Alternative>) object);
			break;
		case CRITERION:
			_criteria = new HashSet<Criterion>();
			_criteria.add((Criterion) object);
			break;
		case CRITERIA:
			_criteria = new HashSet<Criterion>((Collection<? extends Criterion>) object);
			break;
		case DOMAIN:
			_domains = new HashSet<Domain>();
			_domains.add((Domain) object);
			break;
		case DOMAINS:
			_domains = new HashSet<Domain>((Collection<? extends Domain>) object);
			break;

		default:
			break;
		}
	}

	public Map<DomainAssignmentKey, Domain> test() {

		_oldDomainAssignments = _domainAssignments.getAssignments();
		switch (_type) {
		case DOMAIN:
			removeDomains(_domains);
			break;

		case DOMAINS:
			removeDomains(_domains);
			break;

		case EXPERT:
			removeExperts(_experts);
			break;

		case EXPERTS:
			removeExperts(_experts);
			break;

		case ALTERNATIVE:
			removeAlternatives(_alternatives);
			break;

		case ALTERNATIVES:
			removeAlternatives(_alternatives);
			break;

		case CRITERION:
			removeCriteria(_criteria);
			break;

		case CRITERIA:
			removeCriteria(_criteria);
			break;

		default:
			break;
		}

		if (differentDomains()) {
			return _newDomainAssignments;

		} else {
			return null;
		}

	}

	private void removeDomains(Set<Domain> domains) {
		Domain domain;
		for (DomainAssignmentKey key : _oldDomainAssignments.keySet()) {
			domain = _oldDomainAssignments.get(key);
			if (!domains.contains(domain)) {
				_newDomainAssignments.put(key, _oldDomainAssignments.get(key));
			}
		}
	}

	private void removeExperts(Set<Expert> experts) {
		for (DomainAssignmentKey key : _oldDomainAssignments.keySet()) {
			if (!expertsContains(key.getExpert())) {
				_newDomainAssignments.put(key, _oldDomainAssignments.get(key));
			}
		}
	}

	private void removeAlternatives(Set<Alternative> alternatives) {
		for (DomainAssignmentKey key : _oldDomainAssignments.keySet()) {
			if (!alternatives.contains(key.getAlternative())) {
				_newDomainAssignments.put(key, _oldDomainAssignments.get(key));
			}
		}
	}

	private void removeCriteria(Set<Criterion> criteria) {
		for (DomainAssignmentKey key : _oldDomainAssignments.keySet()) {
			if (!criteriaContains(key.getCriterion())) {
				_newDomainAssignments.put(key, _oldDomainAssignments.get(key));
			}
		}
	}

	private boolean expertsContains(Expert expert) {
		if (_experts.contains(expert)) {
			return true;
		} else if (expert.getParent() != null) {
			return expertsContains(expert.getParent());
		} else {
			return false;
		}
	}

	private boolean criteriaContains(Criterion criterion) {
		if (_criteria.contains(criterion)) {
			return true;
		} else if (criterion.getParent() != null) {
			return criteriaContains(criterion.getParent());
		} else {
			return false;
		}
	}

	private boolean differentDomains() {

		if (_oldDomainAssignments.size() != _newDomainAssignments.size()) {
			return true;
		}

		for (DomainAssignmentKey key : _oldDomainAssignments.keySet()) {
			if (!_newDomainAssignments.containsKey(key)) {
				return true;
			}

			Domain d1 = _oldDomainAssignments.get(key);
			Domain d2 = _newDomainAssignments.get(key);

			if (d1 == null) {
				if (d2 != null) {
					return false;
				}
			} else {
				if (!d1.equals(d2)) {
					return false;
				}
			}
		}

		return false;
	}

}
