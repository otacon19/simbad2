package sinbad2.aggregationoperator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class AggregationOperatorsManager {
	
	private final String EXTENSION_POINT = "flintstones.aggregationoperator"; //$NON-NLS-1$
	
	private static AggregationOperatorsManager _instance;
	
	private Map<String, AggregationOperator> _aggregationOperators;
	
	private AggregationOperatorsManager() {
		_aggregationOperators = new HashMap<String, AggregationOperator>();
		loadRegisters();
	}
	
	private void loadRegisters() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(EXTENSION_POINT);

		AggregationOperator aggregationOperator = null;
		IConfigurationElement[] supportedTypesConfiguration;
		Set<EAggregationOperatorType> supportedTypes;
		IConfigurationElement[] implementation = null;
		
		for(IConfigurationElement extension: extensions) {
			implementation = extension.getChildren(EAggregationOperatorElements.unweighted.toString());
			if(implementation.length == 0) {
				implementation = extension.getChildren(EAggregationOperatorElements.weighted.toString());
			}
			
			try {
				aggregationOperator = (AggregationOperator) implementation[0].createExecutableExtension(EAggregationOperatorElements.implementation.toString());
				aggregationOperator.setId(extension.getAttribute(EAggregationOperatorElements.id.toString()));
				aggregationOperator.setName(extension.getAttribute(EAggregationOperatorElements.name.toString()));
				aggregationOperator.setHasParameters(Boolean.parseBoolean(extension.getAttribute(EAggregationOperatorElements.parameters.toString())));
				supportedTypesConfiguration = implementation[0].getChildren(EAggregationOperatorElements.supported_types.toString());
				supportedTypes = new HashSet<EAggregationOperatorType>();
				for(IConfigurationElement type: supportedTypesConfiguration) {
					supportedTypes.add(EAggregationOperatorType.valueOf(type.getAttribute(EAggregationOperatorElements.type.toString())));
				}
				aggregationOperator.setTypes(supportedTypes);
				
				_aggregationOperators.put(aggregationOperator.getId(), aggregationOperator);
				
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public static AggregationOperatorsManager getInstance() {
		if(_instance == null) {
			_instance = new AggregationOperatorsManager();
		}
		
		return _instance;
	}
	
	public AggregationOperator getAggregationOperator(String id) {
		return _aggregationOperators.get(id);
	}
	
	public String[] getAggregationOperatorsIds() {
		return _aggregationOperators.keySet().toArray(new String[0]);
	}
	
	public EAggregationOperatorType[] getAggregationOperatorTypes() {
		Set<EAggregationOperatorType> result = new HashSet<EAggregationOperatorType>();
		
		for(AggregationOperator aggregationOperator: _aggregationOperators.values()) {
			result.addAll(aggregationOperator.getTypes());
		}
		
		return result.toArray(new EAggregationOperatorType[0]);
	}
	
	public String[] getAggregationOperatorsIdByType(EAggregationOperatorType type) {
		Set<String> result = new HashSet<String>();
		
		for(AggregationOperator aggregationOperator: _aggregationOperators.values()) {
			if(aggregationOperator.getTypes().contains(type)) {
				result.add(aggregationOperator.getId());
			}
		}
		
		return result.toArray(new String[0]);
	}

}
