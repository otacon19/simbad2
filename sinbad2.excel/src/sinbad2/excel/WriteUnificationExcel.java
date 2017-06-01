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
import jxl.write.biff.RowsExceededException;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.expert.Expert;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;
import sinbad2.valuation.valuationset.ValuationKey;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;


public class WriteUnificationExcel extends ExcelWriter {
	
	private static final String[] FILTER_NAMES = { "Excel files (*.xls)" }; //$NON-NLS-1$
	private static final String[] FILTER_EXTS = { "*.xls" }; //$NON-NLS-1$

	// Valores unificados.
	private Map<ValuationKey, Valuation> _unification;
	private ProblemElementsSet _elementsSet;
	private ValuationSet _valuationsSet;
	
	/**
	 * Constructor por defecto.
	 * <p>
	 * Privado para impedir su instanciación de este modo.
	 */
	private WriteUnificationExcel() {
		super();
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		_valuationsSet = ValuationSetManager.getInstance().getActiveValuationSet();
	}

	/**
	 * Constructor de WriteUnificationExcel indicando nombre de archivo y
	 * evaluaciones unificadas.
	 * 
	 * @param fileName
	 *            Nombre del archivo de salida.
	 * @param unification
	 *            Evaluaciones unificadas.
	 * @throws IOException 
	 * @throws WriteException 
	 */
	public WriteUnificationExcel(Map<ValuationKey, Valuation> unification) {
		this();
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setFilterNames(FILTER_NAMES);
		dlg.setFilterExtensions(FILTER_EXTS);
		String fileName = dlg.open();
		
		_unification = unification;
		
		if(fileName != null) {
			try {
				write(fileName);
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

		String expertId = expert.getId();
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
				addHeaderLabel(sheet, 3, 2, row, expert.getParent().getId());
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
			List<Alternative> alternatives = _elementsSet.getAlternatives();
			for (int i = 1; i < alternatives.size(); i++) {
				addHeaderLabel(sheet, 1, pos++, row, ""); //$NON-NLS-1$
			}
			row++;

			pos = 1;
			addHeaderLabel(sheet, 2, pos, row, "Criteria"); //$NON-NLS-1$
			addHeaderLabel(sheet, 2, pos++, row + 1, ""); //$NON-NLS-1$
			if (_existsSubCriteria) {
				addHeaderLabel(sheet, 1, pos, row, "Sub-criteria"); //$NON-NLS-1$
				addHeaderLabel(sheet, 1, pos++, row + 1, ""); //$NON-NLS-1$
				if (_existsSubSubCriteria) {
					addHeaderLabel(sheet, 1, pos, row, "Sub-sub-criteria"); //$NON-NLS-1$
					addHeaderLabel(sheet, 1, pos++, row + 1, ""); //$NON-NLS-1$
				}
			}

			for (int i = 0; i < alternatives.size(); i++) {
				addHeaderLabel(sheet, 2, pos, row, alternatives.get(i).getId());
				pos ++;
			}
			row ++;

			createExpertCriteria(sheet, row);
			sheet.getSettings().setProtected(true);

			// Column width
			CellView cellView;
			for (int x = 1; x < sheet.getColumns(); x++) {
				cellView = sheet.getColumnView(x);
				if (x >= _posAlternative) {
					cellView.setSize(4000);
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
	protected void createContent(WritableWorkbook workbook) throws WriteException, RowsExceededException {

		// Evaluations
		Map<ValuationKey, Valuation> evaluations = _valuationsSet.getValuations();
		WritableSheet sheet;
		String expertId;
		String criterionId;
		String alternativeId;
		int[] pos;
		
		Valuation unifiedValuation;
		for (ValuationKey vk : evaluations.keySet()) {
			expertId = vk.getExpert().getId();
			alternativeId = vk.getAlternative().getId();
			criterionId = vk.getCriterion().getId();
			pos = ExcelUtil.findPos(workbook, expertId, alternativeId, criterionId, _posAlternative);
			sheet = workbook.getSheet(pos[0]);
			unifiedValuation = _unification.get(vk);
			if(unifiedValuation instanceof TwoTuple) {
				addTwoTupleEvaluation(sheet, pos, unifiedValuation);
			} else if(unifiedValuation instanceof UnifiedValuation) {
				addUnifiedEvaluation(sheet, pos, unifiedValuation);
			}
		}
	}
}

