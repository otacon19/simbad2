package sinbad2.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.valuationset.ValuationKey;

public class ExcelUtil {

	private XSSFWorkbook _workbook;
	private XSSFSheet _sheet;

	private Map<ValuationKey, Valuation> _unifiedValuations;

	private ProblemElementsSet _elementsSet;

	public ExcelUtil() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();

		_workbook = new XSSFWorkbook();
		_sheet = _workbook.createSheet("Flintstones problem");
	}

	public void createExcelFile(Map<ValuationKey, Valuation> unifiedValuations) {

		_unifiedValuations = unifiedValuations;

		try (FileOutputStream outputStream = new FileOutputStream("C:/Users/Flintstones/Desktop/flintstones_problem.xlsx")) {
			_workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createExcelFileEmergencyProblemStructure(Map<ValuationKey, Valuation> unifiedValuations) {

		_unifiedValuations = unifiedValuations;

		createProblemInformation();

		try (FileOutputStream outputStream = new FileOutputStream("C:/Users/Flintstones/Desktop/flintstones_problem.xlsx")) {
			_workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void createProblemInformation() {
		List<Expert> experts = _elementsSet.getExperts();
		List<Alternative> alternatives = _elementsSet.getAlternatives();
		List<Criterion> criteria = _elementsSet.getAllCriteria();

		CellStyle styleExperts = _workbook.createCellStyle();
		styleExperts.setFillForegroundColor(IndexedColors.RED.getIndex());
		styleExperts.setFillPattern(CellStyle.SOLID_FOREGROUND);

		CellStyle styleAlternatives = _workbook.createCellStyle();
		styleAlternatives.setFillForegroundColor(IndexedColors.CORAL.getIndex());
		styleAlternatives.setFillPattern(CellStyle.SOLID_FOREGROUND);

		CellStyle styleCriteria = _workbook.createCellStyle();
		styleCriteria.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		styleCriteria.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styleCriteria.setAlignment(CellStyle.ALIGN_CENTER);
		styleCriteria.setRightBorderColor(IndexedColors.WHITE.getIndex());

		int rowCountExpertsCriteria = 9, rowCountAlternatives = 10, columnCountCriteria = 3;

		for (Expert e : experts) {
			Row rowExpertCriterion = _sheet.createRow(rowCountExpertsCriteria);
			Cell cell = rowExpertCriterion.createCell(2);
			cell.setCellValue(e.getId());
			cell.setCellStyle(styleExperts);

			rowCountAlternatives = rowCountExpertsCriteria + 1;

			for (Alternative a : alternatives) {

				Row rowAlternative = _sheet.createRow(rowCountAlternatives);
				cell = rowAlternative.createCell(2);
				cell.setCellValue(a.getId());
				cell.setCellStyle(styleAlternatives);

				rowCountAlternatives++;
			}

			for (Criterion c : criteria) {
				CellRangeAddress cellRangeAddress = new CellRangeAddress(rowCountExpertsCriteria,
						rowCountExpertsCriteria, columnCountCriteria, columnCountCriteria + 3);
				_sheet.addMergedRegion(cellRangeAddress);
				cell = rowExpertCriterion.createCell(columnCountCriteria);
				cell.setCellValue(c.getId());
				cell.setCellStyle(styleCriteria);

				columnCountCriteria += 4;
			}

			rowCountExpertsCriteria += alternatives.size() + 2;
			columnCountCriteria = 3;
		}
	}
}
