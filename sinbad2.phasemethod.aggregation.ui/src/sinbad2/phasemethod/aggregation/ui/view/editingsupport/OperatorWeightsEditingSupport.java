package sinbad2.phasemethod.aggregation.ui.view.editingsupport;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import sinbad2.aggregationoperator.AggregationOperator;
import sinbad2.aggregationoperator.WeightedAggregationOperator;
import sinbad2.element.ProblemElement;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.phasemethod.aggregation.AggregationPhase;
import sinbad2.phasemethod.aggregation.ui.nls.Messages;
import sinbad2.phasemethod.aggregation.ui.view.dialog.QuantifiersDialog;
import sinbad2.phasemethod.aggregation.ui.view.dialog.WeightsDialog;

public class OperatorWeightsEditingSupport extends EditingSupport {

	private TreeViewer _viewer;
	private CheckboxCellEditor _cellEditor;
	private AggregationPhase _aggregationPhase;
	private String _type;

	private static ProblemElement[] getLeafElements(ProblemElement root, String type) {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();

		List<ProblemElement> result = new LinkedList<ProblemElement>();
		if(type.equals("expert")) { //$NON-NLS-1$
			List<Expert> children = elementsSet.getAllExpertsAndChildren((Expert) root);
			for(Expert child : children) {
				if(!child.hasChildren()) {
					result.add(child);
				} else {
					ProblemElement[] subchildren = getLeafElements(child, "expert"); //$NON-NLS-1$
					for (ProblemElement subchild : subchildren) {
						result.add(subchild);
					}
				}
			}

		} else {
			List<Criterion> subcriteria = elementsSet.getAllCriterionSubcriteria((Criterion) root);
			for(Criterion subcriterion : subcriteria) {
				if(!subcriterion.hasSubcriteria()) {
					result.add(subcriterion);
				} else {
					ProblemElement[] sub2criteria = getLeafElements(subcriterion, "criterion"); //$NON-NLS-1$
					for(ProblemElement sub2criterion : sub2criteria) {
						result.add(sub2criterion);
					}
				}
			}

		}

		return result.toArray(new ProblemElement[0]);
	}

	public OperatorWeightsEditingSupport(TreeViewer viewer, String type, AggregationPhase aggregationPhase) {
		super(viewer);

		_aggregationPhase = aggregationPhase;
		_type = type;
		_viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		_cellEditor = new CheckboxCellEditor(_viewer.getTree());
		return _cellEditor;

	}

	@Override
	protected boolean canEdit(Object element) {

		ProblemElement problemElement = null;
		if (element instanceof ProblemElement) {
			problemElement = (ProblemElement) element;
		}

		AggregationOperator operator;
		if (AggregationPhase.EXPERTS.equals(_type)) {
			operator = _aggregationPhase.getExpertOperator(problemElement);
		} else {
			operator = _aggregationPhase.getCriterionOperator(problemElement);
		}
		if (operator != null) {
			if (operator instanceof WeightedAggregationOperator) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected Object getValue(Object element) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object element, Object value) {

		if ((element == null) || (value == null)) {
			return;
		}

		ProblemElement problemElement = null;
		if (element instanceof ProblemElement) {
			problemElement = (ProblemElement) element;
		}

		AggregationOperator operator;
		if (AggregationPhase.EXPERTS.equals(_type)) {
			operator = _aggregationPhase.getExpertOperator(problemElement);
		} else {
			operator = _aggregationPhase.getCriterionOperator(problemElement);
		}

		List<Double> weights = null;
		Map<String, List<Double>> mapWeights = null;
		Object aux = null;
		if (AggregationPhase.EXPERTS.equals(_type)) {
			aux = _aggregationPhase.getExpertOperatorWeights(problemElement);
		} else {
			aux = _aggregationPhase.getCriterionOperatorWeights(problemElement);
		}
		if (aux instanceof List<?>) {
			weights = (List<Double>) aux;
		} else if (aux instanceof Map<?, ?>) {
			mapWeights = (Map<String, List<Double>>) aux;
		}

		String elementType;
		String elementId;

		if (AggregationPhase.EXPERTS.equals(_type)) {
			elementType = "Expert"; //$NON-NLS-1$
		} else {
			elementType = "Criterion"; //$NON-NLS-1$
		}

		if (problemElement == null) {
			if (AggregationPhase.EXPERTS.equals(_type)) {
				elementId = Messages.OperatorWeightsEditingSupport_All_experts;
			} else {
				elementId = Messages.OperatorWeightsEditingSupport_All_criteria;
			}
		} else {
			elementId = problemElement.getId();
		}

		if (operator.getName().equals("OWA")) { //$NON-NLS-1$
			QuantifiersDialog dialog = new QuantifiersDialog(Display.getCurrent().getActiveShell(), weights.get(0), weights.get(1), QuantifiersDialog.SIMPLE, elementType, elementId);
			if(dialog.open() == QuantifiersDialog.SAVE) {
				weights = new LinkedList<Double>();
				weights.add(dialog.getAlpha());
				weights.add(dialog.getBeta());
				if (AggregationPhase.EXPERTS.equals(_type)) {
					_aggregationPhase.setExpertOperator(problemElement, operator, weights);
				} else {
					_aggregationPhase.setCriterionOperator(problemElement, operator, weights);

				}
			}
		} else if(operator.getName().equals("Weighted mean")) { //$NON-NLS-1$

			ProblemElement nullElement = null;
			ProblemElement[] secondary;

			ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
			ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
			
			WeightsDialog dialog; 
			if(elementType.equals("Expert")) {
				secondary = getLeafElements(nullElement, "criterion");
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementExpertsAndChildren((Expert) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			} else {
				secondary = getLeafElements(nullElement, "expert");
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria((Criterion) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			}
			
			if(dialog.open() == QuantifiersDialog.SAVE) {
				mapWeights = dialog.getWeights();
				if (AggregationPhase.EXPERTS.equals(_type)) {
					_aggregationPhase.setExpertOperator(problemElement, operator, mapWeights);
				} else {
					_aggregationPhase.setCriterionOperator(problemElement, operator, mapWeights);
				}
			}
		} else if(operator.getName().equals("Weighted geometric")) { //$NON-NLS-1$

			ProblemElement nullElement = null;
			ProblemElement[] secondary;

			ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
			ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
			
			WeightsDialog dialog; 
			if(elementType.equals("Expert")) {
				secondary = getLeafElements(nullElement, "criterion");
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementExpertsAndChildren((Expert) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			} else {
				secondary = getLeafElements(nullElement, "expert");
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria((Criterion) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			}
			
			if(dialog.open() == QuantifiersDialog.SAVE) {
				mapWeights = dialog.getWeights();
				if (AggregationPhase.EXPERTS.equals(_type)) {
					_aggregationPhase.setExpertOperator(problemElement, operator, mapWeights);
				} else {
					_aggregationPhase.setCriterionOperator(problemElement, operator, mapWeights);
				}
			}
		} else if(operator.getName().equals("Weighted mean modified")) { //$NON-NLS-1$

			ProblemElement nullElement = null;
			ProblemElement[] secondary;

			ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
			ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
			
			WeightsDialog dialog; 
			if(elementType.equals("Expert")) {
				secondary = getLeafElements(nullElement, "criterion");
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementExpertsAndChildren((Expert) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			} else {
				secondary = getLeafElements(nullElement, "expert");
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria((Criterion) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			}
			
			if(dialog.open() == QuantifiersDialog.SAVE) {
				mapWeights = dialog.getWeights();
				if (AggregationPhase.EXPERTS.equals(_type)) {
					_aggregationPhase.setExpertOperator(problemElement, operator, mapWeights);
				} else {
					_aggregationPhase.setCriterionOperator(problemElement, operator, mapWeights);
				}
			}
		} else if(operator.getName().equals("Weighted harmonic")) { //$NON-NLS-1$

			ProblemElement nullElement = null;
			ProblemElement[] secondary;

			ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
			ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
			
			WeightsDialog dialog; 
			if(elementType.equals("Expert")) {
				secondary = getLeafElements(nullElement, "criterion");
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementExpertsAndChildren((Expert) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			} else {
				secondary = getLeafElements(nullElement, "expert");
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria((Criterion) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			}
			
			if(dialog.open() == QuantifiersDialog.SAVE) {
				mapWeights = dialog.getWeights();
				if (AggregationPhase.EXPERTS.equals(_type)) {
					_aggregationPhase.setExpertOperator(problemElement, operator, mapWeights);
				} else {
					_aggregationPhase.setCriterionOperator(problemElement, operator, mapWeights);
				}
			}
		} else {
			ProblemElement nullElement = null;
			ProblemElement[] secondary;

			ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
			ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
			
			WeightsDialog dialog; 
			if(elementType.equals("Expert")) { //$NON-NLS-1$
				secondary = getLeafElements(nullElement, "criterion"); //$NON-NLS-1$
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementExpertsAndChildren((Expert) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			} else {
				secondary = getLeafElements(nullElement, "expert"); //$NON-NLS-1$
				dialog = new WeightsDialog(Display.getCurrent().getActiveShell(), elementsSet.getAllElementCriterionSubcriteria((Criterion) problemElement), secondary, mapWeights, QuantifiersDialog.SIMPLE, elementType, elementId);
			}
			
			if(dialog.open() == QuantifiersDialog.SAVE) {
				mapWeights = dialog.getWeights();
				if (AggregationPhase.EXPERTS.equals(_type)) {
					_aggregationPhase.setExpertOperator(problemElement, operator, mapWeights);
				} else {
					_aggregationPhase.setCriterionOperator(problemElement, operator, mapWeights);
				}
			}
		}
	}
}


