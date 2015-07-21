package sinbad2.aggregationoperator;

import java.util.Set;

public class AggregationOperator {
	
	protected String _id;
	protected String _name;
	
	protected Set<EAggregationOperatorType> _types;
	
	
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
	
	public Set<EAggregationOperatorType> getTypes() {
		return _types;
	}
	
	public void setTypes(Set<EAggregationOperatorType> types) {
		_types = types;
	}
	
}
