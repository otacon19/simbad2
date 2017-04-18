package sinbad2.excel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import jxl.CellView;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import sinbad2.core.io.IExportListener;
import sinbad2.core.io.handler.ExportHandler;
import sinbad2.domain.Domain;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentKey;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentsManager;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class WriteExcel extends ExcelWriter implements IExportListener {
	
	private static final String[] FILTER_NAMES = { "Excel files (*.xls)" }; //$NON-NLS-1$
	private static final String[] FILTER_EXTS = { "*.xls" }; //$NON-NLS-1$

	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationsSet;
	private DomainAssignments _domainAssignments;
	
	/**
	 * Constructor por defecto.
	 * <p>
	 * @throws IOException 
	 * @throws WriteException 
	 */
	public WriteExcel() {
		super();
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		_valuationsSet = ValuationSetManager.getInstance().getActiveValuationSet();
		_domainAssignments = DomainAssignmentsManager.getInstance().getActiveDomainAssignments();
		
		ExportHandler.registerExportListener(this);
	}

	private void initialize() throws WriteException, IOException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setFilterNames(FILTER_NAMES);
		dlg.setFilterExtensions(FILTER_EXTS);
		String fileName = dlg.open();
		
		if(fileName != null) {
			write(fileName);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mcdacw.rcp.excel.ExcelWriter#createExpertSheet(jxl.write.WritableWorkbook
	 * , mcdacw.dmp.framework.elements.ProblemElement)
	 */
	@Override
	protected void createExpertSheet(WritableWorkbook workbook, Expert expert) throws WriteException {
		String expertId = expert.getCanonicalId();
		int row = 1;

		List<Expert> sons = expert.getChildren();
		if (sons != null) {
			for (Expert son : sons) {
				createExpertSheet(workbook, son);
			}
		} else {
			workbook.createSheet(expertId.replace(">", "->"), _numSheets++); //$NON-NLS-1$ //$NON-NLS-2$
			WritableSheet sheet = workbook.getSheet(expertId.replace(">", "->")); //$NON-NLS-1$ //$NON-NLS-2$
			if (expert.getParent() != null) {
				addHeaderLabel(sheet, 1, 1, row, "EXPERT GROUP "); //$NON-NLS-1$
				addHeaderLabel(sheet, 3, 2, row, expert.getParent().getCanonicalId());
				row++;
			}
			
			addHeaderLabel(sheet, 1, 1, row, "EXPERT"); //$NON-NLS-1$
			addHeaderLabel(sheet, 3, 2, row, expertId);
			row += 2;

			int pos = 1;
			addHeaderLabel(sheet, 1, pos++, row, "CRITERIA   "); //$NON-NLS-1$
			if (_existsSubCriteria) {
				addHeaderLabel(sheet, 1, pos++, row, ""); //$NON-NLS-1$
				if (_existsSubSubCriteria) {
					addHeaderLabel(sheet, 1, pos++, row, ""); //$NON-NLS-1$
				}
			}
			
			addHeaderLabel(sheet, 1, pos++, row, "ALTERNATIVES"); //$NON-NLS-1$
			addHeaderLabel(sheet, 1, pos++, row, ""); //$NON-NLS-1$
			List<Alternative> alternatives = _elementsSet.getAlternatives();
			for (int i = 1; i < alternatives.size(); i++) {
				addHeaderLabel(sheet, 1, pos++, row, ""); //$NON-NLS-1$
				addHeaderLabel(sheet, 1, pos++, row, ""); //$NON-NLS-1$
			}
			row++;

			pos = 1;
			addHeaderLabel(sheet, 2, pos++, row, "Criteria"); //$NON-NLS-1$
			if (_existsSubCriteria) {
				addHeaderLabel(sheet, 1, pos++, row, "Sub-criteria"); //$NON-NLS-1$
				if (_existsSubSubCriteria) {
					addHeaderLabel(sheet, 1, pos++, row, "Sub-sub-criteria"); //$NON-NLS-1$
				}
			}
			for (int i = 0; i < alternatives.size(); i++) {
				addHeaderLabel(sheet, 2, pos++, row, alternatives.get(i).getId());
				addHeaderLabel(sheet, 2, pos++, row, ""); //$NON-NLS-1$
			}
			row++;

			createExpertCriteria(sheet, row);
			sheet.getSettings().setProtected(false);

			// Column width
			CellView cellView;
			for (int x = 1; x < sheet.getColumns(); x++) {
				cellView = sheet.getColumnView(x);
				if (x >= _posAlternative) {
					cellView.setSize(5000); //Cambiado por setDimension()
				} else {
					cellView.setAutosize(true);
				}
				sheet.setColumnView(x, cellView);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mcdacw.rcp.excel.ExcelWriter#createContent(jxl.write.WritableWorkbook)
	 */
	@Override
	protected void createContent(WritableWorkbook workbook) throws WriteException {		
		// Evaluations
		Map<ValuationKey, Valuation> evaluations = _valuationsSet.getValuations();
		Map<DomainAssignmentKey, Domain> domains = _domainAssignments.getAssignments();
		
		WritableSheet sheet;
		Expert expert;
		Criterion criterion;
		Alternative alternative;
		Valuation valuation;
		int[] pos;
		String domainName;
		Domain domain;
		for (DomainAssignmentKey dk : domains.keySet()) {
			expert = dk.getExpert();
			alternative = dk.getAlternative();
			criterion = dk.getCriterion();
			pos = ExcelUtil.findPos(workbook, expert.getCanonicalId(), alternative.getId(), criterion.getCanonicalId(), _posAlternative);
			sheet = workbook.getSheet(pos[0]);
			valuation = evaluations.get(new ValuationKey(expert, alternative, criterion));

			domain = domains.get(dk);
			
			if (domain != null) {
				domainName = domain.getName();
				
				if (domain instanceof NumericIntegerDomain) {
					if(valuation instanceof IntegerValuation) {
						addNumberEvaluation(sheet, pos, (NumericIntegerDomain) domain, domainName, valuation);
					} else {
						addIntervalEvaluation(sheet, pos, (NumericIntegerDomain) domain, domainName, valuation);
					}
				} else if (domain instanceof NumericRealDomain) {
					if(valuation instanceof RealValuation) {
						addNumberEvaluation(sheet, pos, (NumericRealDomain) domain, domainName, valuation);
					} else {
						addIntervalEvaluation(sheet, pos, (NumericRealDomain) domain, domainName, valuation);
					}
				} else if (domain instanceof FuzzySet) {
					addLinguisticEvaluation(sheet, pos, (FuzzySet) domain, domainName, valuation);
				}
			}
		}
	}

	@Override
	public void notifyExportData() {
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
