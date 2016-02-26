package sinbad2.method;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import sinbad2.aggregationoperator.EAggregationOperatorType;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class MethodsManager {
	
	private final String EXTENSION_POINT = "flintstones.method"; //$NON-NLS-1$

	private static MethodsManager _instance = null;

	private Method _activeMethod;

	private Map<String, MethodRegistryExtension> _registers;
	private Map<String, Method> _methods;
	private Map<MethodImplementation, String> _implementationMethods;
	
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationsSet;
	private DomainSet _domainsSet;

	private MethodsManager() {
		_activeMethod = null;
		_registers = new HashMap<String, MethodRegistryExtension>();
		_methods = new HashMap<String, Method>();
		_implementationMethods = new HashMap<MethodImplementation, String>();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		ValuationSetManager valuationSetManager = ValuationSetManager.getInstance();
		_valuationsSet = valuationSetManager.getActiveValuationSet();
		DomainsManager domainsManager = DomainsManager.getInstance();
		_domainsSet = domainsManager.getActiveDomainSet();
		
		loadRegistersExtension();
	}

	public static MethodsManager getInstance() {
		if (_instance == null) {
			_instance = new MethodsManager();
		}
		return _instance;
	}

	private void loadRegistersExtension() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);

		MethodRegistryExtension registry;
		for (IConfigurationElement extension : extensions) {
			registry = new MethodRegistryExtension(extension);
			_registers.put(registry.getElement(EMethodElements.id), registry);
		}
		
		IConfigurationElement[] supportedTypesConfiguration = extensions[0].getChildren(EMethodElements.aggregation_supported.toString());
		Set<EAggregationOperatorType> supportedTypes = new HashSet<EAggregationOperatorType>();
		for(IConfigurationElement type: supportedTypesConfiguration) {
			supportedTypes.add(EAggregationOperatorType.valueOf(type.getAttribute(EMethodElements.type.toString())));
		}
	}

	public String[] getIdsRegisters() {
		return _registers.keySet().toArray(new String[0]);
	}

	public MethodRegistryExtension getRegistry(String id) {
		return _registers.get(id);
	}

	public void setImplementationMethod(MethodImplementation implementation, String methodId) {
		_implementationMethods.put(implementation, methodId);
	}

	public Method getImplementationMethod(MethodImplementation implementation) {
		Method result = null;

		String id = _implementationMethods.get(implementation);

		if (id != null) {
			result = _methods.get(id);
		}

		return result;
	}

	public Method getMethod(String id) {

		if (_methods.containsKey(id)) {
			return _methods.get(id);
		} else {
			try {
				return initializeMethod(id);
			} catch (Exception e) {
				return null;
			}
		}
	}

	public Method getActiveMethod() {
		return _activeMethod;
	}

	public void deactiveCurrentActive() {
		if (_activeMethod != null) {
			_activeMethod.deactivate();
			_activeMethod = null;
		}
	}

	public void activate(String id) {
		boolean needActivate = true;
		if (_activeMethod != null) {
			if (!_activeMethod.getId().equals(id)) {
				deactiveCurrentActive();
			} else {
				needActivate = false;
			}
		}

		if (needActivate) {
			_activeMethod = getMethod(id);
			if (_activeMethod != null) {
				_activeMethod.activate();
			}
		}
	}
	
	private Method initializeMethod(String id) {
		MethodRegistryExtension methodRegistry = getRegistry(id);
		Method method = new Method();
		method.setId(id);
		method.setName(methodRegistry.getElement(EMethodElements.name));
		method.setCategory(methodRegistry.getElement(EMethodElements.category));
		method.setDescription(methodRegistry.getElement(EMethodElements.description));
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);
		
		IConfigurationElement[] supportedTypesConfiguration = extensions[0].getChildren(EMethodElements.aggregation_supported.toString());
		Set<EAggregationOperatorType> types = new HashSet<EAggregationOperatorType>();
		for(int i = 0; i < supportedTypesConfiguration.length; ++i) {
			EAggregationOperatorType supportedType = EAggregationOperatorType.valueOf(supportedTypesConfiguration[i].getAttribute(EMethodElements.type.toString()));
			types.add(supportedType);
		}
			
		method.setAggregationTypeSupported(types);
		
		method.setRegistry(methodRegistry);

		_methods.put(id, method);

		return method;

	}
	
	public String getRecommendedMethod() {
		Map<Integer, Boolean> bcl = getBestConditionsLinguistic();
		int[] cardinalities = getCardinalitiesFuzzySet();
		
		if(cardinalities.length > 0 && bcl.size() > 0) {
			if((bcl.get(cardinalities[0])) && _domainsSet.getDomains().size() == 1) {
				return "2-Tuple linguistic computational model";
			} else if(getBestConditionsNumeric()) {
				return "Fusion approach for managing heterogeneous information";
			} else if(getBestConditionsUnbalanced() && _domainsSet.getDomains().size() == 1) {
				return "Methodology to deal with unbalanced linguistic term sets";
			} else {
				for(int i = 0; i < cardinalities.length; i++) {
					if(!getBestConditionsLinguistic().get(cardinalities[i])) {
						return "Fusion approach for managing multi-granular linguistic information";
					} else if((i + 1) < cardinalities.length) {
						if((((cardinalities[i] -1) * 2) +1) != cardinalities[i + 1]) {
							return "Extended Linguistic Hierarchies";
						}
					}
				}
				return "Linguistic Hierarchies";
			}
		}
		return "";
	}
	
	public int[] getCardinalitiesFuzzySet() {
		Set<Integer> cardinalities = new HashSet<Integer>();
		Domain generateDomain;
		
		for(Expert expert : _elementsSet.getAllExperts()) {
			if(!expert.hasChildrens()) {
				for(Criterion criterion : _elementsSet.getAllCriteria()) {
					if(!criterion.hasSubcriteria()) {
						for(Alternative alternative : _elementsSet.getAlternatives()) {
							Valuation v = _valuationsSet.getValuation(expert, alternative, criterion);
							if(v != null) {
								generateDomain = v.getDomain();
								if(generateDomain != null) {
									if(generateDomain instanceof FuzzySet) {
										cardinalities.add(((FuzzySet) generateDomain).getLabelSet().getCardinality());
									}
								}
							}
						}
					}
				}
			}
		}
		
		List<Integer> auxCardinalities = new LinkedList<Integer>(cardinalities);
		Collections.sort(auxCardinalities);
		Integer[] auxResult = auxCardinalities.toArray(new Integer[0]);
		
		int length = auxResult.length;
		int[] result = new int[length];
		for(int i = 0; i < length; i++) {
			result[i] = auxResult[i].intValue();
		}
		return result;
	}
	
	public Map<Integer, Boolean> getBestConditionsLinguistic() {
		Map<Integer, Boolean> result = new HashMap<Integer, Boolean>();
		FuzzySet fs = null;
		Domain generateDomain;
		
		for(Expert expert : _elementsSet.getAllExperts()) {
			if(!expert.hasChildrens()) {
				for(Criterion criterion : _elementsSet.getAllCriteria()) {
					if(!criterion.hasSubcriteria()) {
						for(Alternative alternative : _elementsSet.getAlternatives()) {
							Valuation v = _valuationsSet.getValuation(expert, alternative, criterion);
							if(v != null) {
								generateDomain = v.getDomain();
								if(generateDomain != null) {
									if(generateDomain instanceof FuzzySet) {
										fs = (FuzzySet) generateDomain;
										result.put(fs.getLabelSet().getCardinality(), fs.isBLTS());
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	public boolean getBestConditionsUnbalanced() {
		Domain generateDomain;
		
		for(Expert expert : _elementsSet.getAllExperts()) {
			if(!expert.hasChildrens()) {
				for(Criterion criterion : _elementsSet.getAllCriteria()) {
					if(!criterion.hasSubcriteria()) {
						for(Alternative alternative : _elementsSet.getAlternatives()) {
							Valuation v = _valuationsSet.getValuation(expert, alternative, criterion);
							if(v != null) {
								generateDomain = v.getDomain();
								if(generateDomain != null) {
									if(generateDomain instanceof Unbalanced) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public boolean getBestConditionsNumeric() {
		Domain generateDomain;
		
		for(Expert expert : _elementsSet.getAllExperts()) {
			if(!expert.hasChildrens()) {
				for(Criterion criterion : _elementsSet.getAllCriteria()) {
					if(!criterion.hasSubcriteria()) {
						for(Alternative alternative : _elementsSet.getAlternatives()) {
							Valuation v = _valuationsSet.getValuation(expert, alternative, criterion);
							if(v != null) {
								generateDomain = v.getDomain();
								if(generateDomain != null) {
									if(generateDomain instanceof NumericRealDomain || generateDomain instanceof NumericIntegerDomain) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
