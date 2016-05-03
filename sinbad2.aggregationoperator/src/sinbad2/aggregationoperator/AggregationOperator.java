package sinbad2.aggregationoperator;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AggregationOperator {
	
	protected String _id;
	protected String _name;
	protected boolean _hasParameters;
	
	protected List<Double> _parameters;
	
	protected Set<EAggregationOperatorType> _types;
	
	public AggregationOperator() {
		_parameters = new LinkedList<Double>();
	}
	
	public String getId() {
		return _id;
	}
	
	public void setId(String id) {
		_id = id;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public boolean hasParameters() {
		return _hasParameters;
	}
	
	public void setHasParameters(boolean hasParameters) {
		_hasParameters = hasParameters;
	}
	
	public List<Double> getParameters() {
		return _parameters;
	}
	
	public void setParameters(List<Double> parameters) {
		_parameters = parameters;
	}
	
	public Set<EAggregationOperatorType> getTypes() {
		return _types;
	}
	
	public void setTypes(Set<EAggregationOperatorType> types) {
		_types = types;
	}
	
}
