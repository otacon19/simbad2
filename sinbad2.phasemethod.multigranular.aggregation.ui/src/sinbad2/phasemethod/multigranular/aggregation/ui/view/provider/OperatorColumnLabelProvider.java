package sinbad2.phasemethod.multigranular.aggregation.ui.view.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.element.ProblemElement;
import sinbad2.phasemethod.multigranular.aggregation.AggregationPhase;

public class OperatorColumnLabelProvider extends ColumnLabelProvider {

	private AggregationPhase _aggregationPhase;
	private String _type;
	
	public OperatorColumnLabelProvider(AggregationPhase aggregationPhase, String type) {
		_aggregationPhase = aggregationPhase;
		_type = type;
	}
	
	@Override
	public String getText(Object element) {
		AggregationOperator operator;
		ProblemElement problemElement = null;
		
		if (element instanceof ProblemElement) {
			problemElement = (ProblemElement) element;
		}
		if (AggregationPhase.EXPERTS.equals(_type)) {
			operator = _aggregationPhase.getExpertOperator(problemElement);
		} else {
			operator = _aggregationPhase.getCriterionOperator(problemElement);			
		}
		if (operator == null) {
			return "Unassigned";
		} else {
			String id = operator.getId();
			id = id.substring(0, 1).toUpperCase() + id.substring(1);
			
			return id;							
		}
	}
}
