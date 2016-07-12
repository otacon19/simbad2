package sinbad2.phasemethod.todim.resolution.ui.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.AggregationOperatorsManager;
import sinbad2.aggregationoperator.EAggregationOperatorType;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.method.ui.MethodsUIManager;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;
import sinbad2.phasemethod.todim.resolution.ui.view.dialog.WeightsDialog;

public class AggregationView extends ViewPart {

	private Composite _parent;
	private Composite _decisionMatrixComposite;
	private Combo _aggregationOperatorsCombo;
	
	private Map<String, String> _operators;
	
	private DecisionMatrixTable _dmTable;
	
	private ResolutionPhase _resolutionPhase;
	
	private ProblemElementsSet _elementsSet;
	
	public AggregationView() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_resolutionPhase = (ResolutionPhase) pmm.getPhaseMethod(ResolutionPhase.ID).getImplementation();
		
		_operators = new HashMap<String, String>();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		_parent.setLayout(new GridLayout(1, true));
		_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite aggregationOperatorsComposite = new Composite(_parent, SWT.NONE);
		aggregationOperatorsComposite.setLayout(new GridLayout(1, false));
		aggregationOperatorsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		_aggregationOperatorsCombo = new Combo(aggregationOperatorsComposite, SWT.BORDER | SWT.READ_ONLY);
		_aggregationOperatorsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setOperator(_aggregationOperatorsCombo.getItem(_aggregationOperatorsCombo.getSelectionIndex()));
			}
		});
		
		fillCombo();
		
		_decisionMatrixComposite = new Composite(_parent, SWT.NONE);
		_decisionMatrixComposite.setLayout(new GridLayout(1, false));
		_decisionMatrixComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_dmTable = new DecisionMatrixTable(_decisionMatrixComposite);
		_dmTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));	
		
		refreshDMTable();
	}

	private void setOperator(String operator) {
		AggregationOperatorsManager aggregationOperatorsManager = AggregationOperatorsManager.getInstance();
		AggregationOperator aggregationOperator = aggregationOperatorsManager.getAggregationOperator(_operators.get(operator));
		
		Map<String, List<Double>> mapWeights = new HashMap<String, List<Double>>();
		if(aggregationOperator instanceof WeightedAggregationOperator) { 
			ProblemElement[] secondary = _elementsSet.getAllCriteria().toArray(new Criterion[0]);
			WeightsDialog dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), _elementsSet.getAllElementExpertChildren(null), secondary, null, 1, "Expert", "All_experts");
			
			int exitValue = dialog.open();
			if(exitValue == WeightsDialog.SAVE) {
				mapWeights = dialog.getWeights();
			} else { 
				mapWeights = null;
			}
		}
		
		_resolutionPhase.calculateDecisionMatrix(aggregationOperator, mapWeights);
		
		refreshDMTable();
	}
	
	private void fillCombo() {
		AggregationOperatorsManager aggregationOperatorsManager = AggregationOperatorsManager.getInstance();
		TreeSet<String> aggregationOperatorsIds = new TreeSet<String>();
		MethodsUIManager methodsUIManager = MethodsUIManager.getInstance();	
		Set<EAggregationOperatorType> operatorsTypes = methodsUIManager.getActivateMethodUI().getMethod().getAggregationTypesSupported();

		String[] operatorsIds;
		for(EAggregationOperatorType operatorType: operatorsTypes) {
			operatorsIds = aggregationOperatorsManager.getAggregationOperatorsIdByType(operatorType);
			for(String operator: operatorsIds) {
				aggregationOperatorsIds.add(operator);
			}
		}
		
		List<String> aggregationOperatorsNames = new LinkedList<String>();
		AggregationOperator operator;
		for(int i = 0; i < aggregationOperatorsIds.size(); i++) {
			operator = aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]);
			String name = ""; //$NON-NLS-1$
			if (operator instanceof WeightedAggregationOperator) {
				name = "(W) " + aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]).getName(); //$NON-NLS-1$
				aggregationOperatorsNames.add(name);
			} else {
				name = aggregationOperatorsManager.getAggregationOperator((String) aggregationOperatorsIds.toArray()[i]).getName();
				aggregationOperatorsNames.add(name);
			}
			
			_operators.put(name, aggregationOperatorsIds.toArray(new String[0])[i]);
		}
		
		_aggregationOperatorsCombo.setItems(aggregationOperatorsNames.toArray(new String[0]));
	}
	
	private void refreshDMTable() {		
		String[] alternatives = new String[_elementsSet.getAlternatives().size()];
		for(int a = 0; a < alternatives.length; ++a) {
			alternatives[a] = _elementsSet.getAlternatives().get(a).getId();
		}
		
		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for(int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		_dmTable.setModel(alternatives, criteria, _resolutionPhase.getDecisionMatrix());
	}

	@Override
	public String getPartName() {
		return "Aggregation";
	}
	
	@Override
	public void setFocus() {
		_aggregationOperatorsCombo.setFocus();
	}

}
