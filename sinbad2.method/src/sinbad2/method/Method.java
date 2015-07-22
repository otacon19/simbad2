package sinbad2.method;

import sinbad2.aggregationoperator.EAggregationOperatorType;

public abstract class Method {
	
	public final static String selectBLTS = "Select BLTS";
	public final static String Unification = "Unification";
	public final static String AggregationProcess = "Aggregation process";
	public final static String Filter = "Filter";
	public final static String SelectUnifiedDomain = "Select unified domain";
	public final static String GenerateUnificationDomain = "Generate unification domain";
	public final static String SelectResultsDomain = "Select results domain";
	public final static String CalculateRepresentation = "Calculate representation";
	
	public final static String AggregationPrefix = "Aggregation";
	public final static String UnificationPrefix = "Unification";
	public final static String RetranslationPrefix = "Retranslation";
	
	protected String _name;
	
	protected String _description;
	
	protected String[] _process;
	
	protected int[] _conditions;
	
	protected String _category;
	
	protected EAggregationOperatorType _operatorType;
	
	public String getName() {
		return _name;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public String[] getProcess() {
		return _process;
	}
	
	public int[] getConditions() {
		return _conditions;
	}
	
	public String getCategory() {
		return _category;
	}
	
	public EAggregationOperatorType getOperatorType() {
		return _operatorType;
	}
	
	public abstract Object phaseMethod(String phase, Object params);
}
