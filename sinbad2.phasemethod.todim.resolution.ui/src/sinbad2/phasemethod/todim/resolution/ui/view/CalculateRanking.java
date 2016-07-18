package sinbad2.phasemethod.todim.resolution.ui.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.phasemethod.PhasesMethodManager;
import sinbad2.phasemethod.todim.resolution.Pair;
import sinbad2.phasemethod.todim.resolution.ResolutionPhase;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.AnotherAlternativeColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.CriteriaTableContentProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.CriterionColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.CriterionIdColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.CriterionWeightColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.DominanceDegreeAlternativesColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.DominanceDegreeAlternativesContentProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.DominanceDegreeColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.DominanceDegreeTableContentProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.MainAlternativeColumnLabelProvider;
import sinbad2.phasemethod.todim.resolution.ui.view.provider.RelativeWeightCriterionColumnLabelProvider;

public class CalculateRanking extends ViewPart {
	
	public static final String ID = "flintstones.phasemethod.todim.resolution.ui.view.calculateranking";

	private Composite _parent;
	
	private DecisionMatrixTable _dmTable;
	private TableViewer _criteriaTableViewer;
	private TableViewer _dominanceDegreeTableViewer;
	private TableViewer _dominanceDegreeAlternativesTableViewer;
	
	private ResolutionPhase _resolutionPhase;
	
	private ProblemElementsSet _elementsSet;
	
	@SuppressWarnings("rawtypes")
	public static class DataComparator implements Comparator {
		@Override
		public int compare(Object d1, Object d2) {
			String a1 = ((String[]) d1)[0];
			String a2 = ((String[]) d2)[0];
			
			int compare = a1.compareTo(a2);
			if(compare != 0) {
				return compare;
			} else {
				String aS1 = ((String[]) d1)[1];
				String aS2 = ((String[]) d2)[1];
				return aS1.compareTo(aS2);
			}
		}
	 }
	
	public CalculateRanking() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		PhasesMethodManager pmm = PhasesMethodManager.getInstance();
		_resolutionPhase = (ResolutionPhase) pmm.getPhaseMethod(ResolutionPhase.ID).getImplementation();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_parent = parent;
		_parent.setLayout(new GridLayout(1, true));
		_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite decisionMatrixComposite = new Composite(_parent, SWT.NONE);
		decisionMatrixComposite.setLayout(new GridLayout(1, true));
		decisionMatrixComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		_dmTable = new DecisionMatrixTable(decisionMatrixComposite);
		_dmTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));	
		
		Composite tablesComposite = new Composite(_parent, SWT.NONE);
		tablesComposite.setLayout(new GridLayout(2, false));
		tablesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		_criteriaTableViewer = new TableViewer(tablesComposite);
		_criteriaTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_criteriaTableViewer.setContentProvider(new CriteriaTableContentProvider());
		_criteriaTableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn criterionReference = new TableViewerColumn(_criteriaTableViewer, SWT.NONE);
		criterionReference.getColumn().setText("Reference criterion");
		criterionReference.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
		});
		criterionReference.getColumn().pack();
		
		TableViewerColumn criterionId = new TableViewerColumn(_criteriaTableViewer, SWT.NONE);
		criterionId.getColumn().setText("Id");
		criterionId.setLabelProvider(new CriterionIdColumnLabelProvider());
		criterionId.getColumn().pack();
		
		TableViewerColumn criterionWeight = new TableViewerColumn(_criteriaTableViewer, SWT.NONE);
		criterionWeight.getColumn().setText("Weight");
		criterionWeight.setLabelProvider(new CriterionWeightColumnLabelProvider());
		criterionWeight.getColumn().pack();
		
		TableViewerColumn relativeWeight = new TableViewerColumn(_criteriaTableViewer, SWT.NONE);
		relativeWeight.getColumn().setText("Relative weight");
		relativeWeight.setLabelProvider(new RelativeWeightCriterionColumnLabelProvider());
		relativeWeight.getColumn().pack();
	
		_dominanceDegreeTableViewer = new TableViewer(tablesComposite);
		_dominanceDegreeTableViewer.setContentProvider(new DominanceDegreeTableContentProvider());
		_dominanceDegreeTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_dominanceDegreeTableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn mainAlternative = new TableViewerColumn(_dominanceDegreeTableViewer, SWT.NONE);
		mainAlternative.getColumn().setText("Alternative");
		mainAlternative.setLabelProvider(new MainAlternativeColumnLabelProvider());
		mainAlternative.getColumn().pack();
		
		TableViewerColumn anotherAlternative = new TableViewerColumn(_dominanceDegreeTableViewer, SWT.NONE);
		anotherAlternative.getColumn().setText("Alternative");
		anotherAlternative.setLabelProvider(new AnotherAlternativeColumnLabelProvider());
		anotherAlternative.getColumn().pack();
		
		TableViewerColumn criterion= new TableViewerColumn(_dominanceDegreeTableViewer, SWT.NONE);
		criterion.getColumn().setText("Criterion");
		criterion.setLabelProvider(new CriterionColumnLabelProvider());
		criterion.getColumn().pack();
		
		TableViewerColumn dominanceDegree = new TableViewerColumn(_dominanceDegreeTableViewer, SWT.NONE);
		dominanceDegree.getColumn().setText("Dominance degree");
		dominanceDegree.setLabelProvider(new DominanceDegreeColumnLabelProvider());
		dominanceDegree.getColumn().pack();
		
		Composite dominanceDegreeAlternativesComposite = new Composite(_parent, SWT.NONE);
		dominanceDegreeAlternativesComposite.setLayout(new GridLayout(1, true));
		dominanceDegreeAlternativesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_dominanceDegreeAlternativesTableViewer = new TableViewer(dominanceDegreeAlternativesComposite);
		_dominanceDegreeAlternativesTableViewer.setContentProvider(new DominanceDegreeAlternativesContentProvider());
		_dominanceDegreeAlternativesTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		_dominanceDegreeAlternativesTableViewer.getTable().setHeaderVisible(true);
		
		TableViewerColumn mainAlternative2 = new TableViewerColumn(_dominanceDegreeAlternativesTableViewer, SWT.NONE);
		mainAlternative2.getColumn().setText("Alternative");
		mainAlternative2.setLabelProvider(new MainAlternativeColumnLabelProvider());
		mainAlternative2.getColumn().pack();
		
		TableViewerColumn anotherAlternative2 = new TableViewerColumn(_dominanceDegreeAlternativesTableViewer, SWT.NONE);
		anotherAlternative2.getColumn().setText("Alternative");
		anotherAlternative2.setLabelProvider(new AnotherAlternativeColumnLabelProvider());
		anotherAlternative2.getColumn().pack();
		
		TableViewerColumn dominanceDegreeAlternative = new TableViewerColumn(_dominanceDegreeAlternativesTableViewer, SWT.NONE);
		dominanceDegreeAlternative.getColumn().setText("Dominance degree");
		dominanceDegreeAlternative.setLabelProvider(new DominanceDegreeAlternativesColumnLabelProvider());
		dominanceDegreeAlternative.getColumn().pack();
		
		
		refreshConsensusMatrixTable();
		
		setInputCriteriaTable();
	}

	private void refreshConsensusMatrixTable() {		
		String[] alternatives = new String[_elementsSet.getAlternatives().size()];
		for(int a = 0; a < alternatives.length; ++a) {
			alternatives[a] = _elementsSet.getAlternatives().get(a).getId();
		}
		
		String[] criteria = new String[_elementsSet.getCriteria().size()];
		for(int c = 0; c < criteria.length; ++c) {
			criteria[c] = _elementsSet.getCriteria().get(c).getId();
		}

		_dmTable.setModel(alternatives, criteria, _resolutionPhase.getConsensusMatrix());
	}
	
	private void setInputCriteriaTable() {
		List<String[]> result = new LinkedList<String[]>();
		
		List<Double> globalWeights = _resolutionPhase.getGlobalWeights();
		Map<String, Double> relativeWeights = _resolutionPhase.calculateRelativeWeights();
		
		int indexCriterion = 0;
		for(Criterion c: _elementsSet.getAllCriteria()) {
			String[] row = new String[4];
			row[0] = c.getCanonicalId();
			row[1] = Double.toString(globalWeights.get(indexCriterion));
			if(relativeWeights.isEmpty()) {
				row[2] = "0";
			} else {
				row[2] = Double.toString(relativeWeights.get(c.getCanonicalId()));
			}
			
			indexCriterion++;
			result.add(row);
		}
		_criteriaTableViewer.setInput(result);
		
		TableItem[] items = _criteriaTableViewer.getTable().getItems();
		for(int i = 0; i < items.length; ++i) {
			TableEditor editor = new TableEditor(_criteriaTableViewer.getTable());
			Button button = new Button(_criteriaTableViewer.getTable(), SWT.RADIO);
		    button.pack();
		    button.setData("numCriterion", i);
		    editor.minimumWidth = button.getSize().x;
		    editor.horizontalAlignment = SWT.CENTER;
		    editor.setEditor(button, items[i], 0);
		    button.addSelectionListener(new SelectionAdapter() {
		    	@Override
		    	public void widgetSelected(SelectionEvent e) {
		    		_resolutionPhase.setReferenceCriterion((int) ((Button) e.widget).getData("numCriterion"));
		    		
		    		setInputCriteriaTable();
		    		setInputDominanceDegreeTable();
		    		setInputDominaceAlternativeDegreeTable();
		    	}
			});
		}
		
		pack();
	}
	
	@SuppressWarnings("unchecked")
	private void setInputDominanceDegreeTable() {
		List<String[]> input = new LinkedList<String[]>();
		
		Map<Criterion, Map<Pair<Alternative, Alternative>, Double>> dominanceDegreeByCriterion = _resolutionPhase.calculateDominanceDegreeByCriterion();
		for(Criterion c: _elementsSet.getAllCriteria()) {
			Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance = dominanceDegreeByCriterion.get(c);
			for(Pair<Alternative, Alternative> pair: pairAlternativesDominance.keySet()) {
				String[] data = new String[4];
				data[0] = pair.getLeft().getId();
				data[1] = pair.getRight().getId();
				data[2] = c.getCanonicalId();
				data[3] = Double.toString(Math.round(pairAlternativesDominance.get(pair) * 1000) / 1000d);
				input.add(data);
			}
		}
		
		Collections.sort(input, new DataComparator());
		
		_dominanceDegreeTableViewer.setInput(input);
	}
	
	@SuppressWarnings("unchecked")
	private void setInputDominaceAlternativeDegreeTable() {
		List<String[]> input = new LinkedList<String[]>();
		
		Map<Pair<Alternative, Alternative>, Double> pairAlternativesDominance = _resolutionPhase.calculateDominaceDegreeAlternatives();
		for(Pair<Alternative, Alternative> pair: pairAlternativesDominance.keySet()) {
			String[] data = new String[3];
			data[0] = pair.getLeft().getCanonicalId();
			data[1] = pair.getRight().getCanonicalId();
			data[2] = Double.toString(Math.round(pairAlternativesDominance.get(pair) * 1000) / 1000d);
			
			input.add(data);
		}
		
		Collections.sort(input, new DataComparator());
		
		_dominanceDegreeAlternativesTableViewer.setInput(input);
	}
	
	private void pack() {
		for(TableColumn tc: _criteriaTableViewer.getTable().getColumns()) {
			tc.pack();
		}
	}
	
	@Override
	public void setFocus() {
		_criteriaTableViewer.getTable().setFocus();
	}
	
	@Override
	public String getPartName() {
		return "Calculate ranking";
	}

}
