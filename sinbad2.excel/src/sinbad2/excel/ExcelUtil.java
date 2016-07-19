package sinbad2.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.valuationset.ValuationKey;

public class ExcelUtil {

	private XSSFWorkbook _workbook;
	private XSSFSheet _sheet;

	private List<Expert> _experts;
	private List<Criterion> _criteria;
	private List<Alternative> _alternatives;
	private Map<ValuationKey, Valuation> _unifiedValuations;

	private ProblemElementsSet _elementsSet;

	public ExcelUtil() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();

		_experts = _elementsSet.getExperts();
		_alternatives = _elementsSet.getAlternatives();
		_criteria = _elementsSet.getAllCriteria();
		
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

		try (FileOutputStream outputStream = new FileOutputStream("C:/Users/Álvaro/Desktop/flintstones_problem.xlsx")) {
			_workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void createProblemInformation() {
		
		CellStyle styleExperts = _workbook.createCellStyle();
		styleExperts.setFillForegroundColor(IndexedColors.RED.getIndex());
		styleExperts.setFillPattern(CellStyle.SOLID_FOREGROUND);

		CellStyle styleAlternatives = _workbook.createCellStyle();
		styleAlternatives.setFillForegroundColor(IndexedColors.CORAL.getIndex());
		styleAlternatives.setFillPattern(CellStyle.SOLID_FOREGROUND);

		CellStyle styleCriteria = _workbook.createCellStyle();
		styleCriteria.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		styleCriteria.setFillPattern(CellStyle.SOLID_FOREGROUND);
		styleCriteria.setAlignment(CellStyle.ALIGN_CENTER);
		styleCriteria.setBorderLeft(BorderStyle.THIN);
		styleCriteria.setBorderRight(BorderStyle.THIN);
		
		int rowCountExpertsCriteria = 9, rowCountAlternatives = 10, columnCountCriteria = 3, columnCountValuation = 3;

		for (Expert e : _experts) {
			
			Row rowExpertCriterion = _sheet.createRow(rowCountExpertsCriteria);
			Cell cell = rowExpertCriterion.createCell(2);
			cell.setCellValue(e.getId());
			cell.setCellStyle(styleExperts);

			rowCountAlternatives = rowCountExpertsCriteria + 1;

			for (Alternative a : _alternatives) {

				Row rowAlternative = _sheet.createRow(rowCountAlternatives);
				cell = rowAlternative.createCell(2);
				cell.setCellValue(a.getId());
				cell.setCellStyle(styleAlternatives);

				rowCountAlternatives++;
				
				for (Criterion c : _criteria) {
					for(ValuationKey vk: _unifiedValuations.keySet()) {
						if(vk.getExpert().equals(e) && vk.getAlternative().equals(a) && vk.getCriterion().equals(c)) {
							
							TwoTuple v = (TwoTuple) _unifiedValuations.get(vk);
							LabelLinguisticDomain label = v.getLabel();
							TrapezoidalFunction semantic = (TrapezoidalFunction) label.getSemantic();
							double limits[] = semantic.getLimits();
							
							cell = rowAlternative.createCell(columnCountValuation);
							cell.setCellValue(Double.toString(limits[0]));
							
							columnCountValuation++;
							
							cell = rowAlternative.createCell(columnCountValuation);
							cell.setCellValue(Double.toString(limits[1]));
					
							columnCountValuation++;
							
							cell = rowAlternative.createCell(columnCountValuation);
							cell.setCellValue(Double.toString(limits[2]));
							
							columnCountValuation++;
							
							cell = rowAlternative.createCell(columnCountValuation);
							cell.setCellValue(Double.toString(limits[3]));
							
							columnCountValuation++;
						}
					}
				}
				
				columnCountValuation = 3;
			}

			for (Criterion c : _criteria) {
				cell = rowExpertCriterion.createCell(columnCountCriteria);
				cell.setCellValue(c.getId());
				cell.setCellStyle(styleCriteria);

				CellRangeAddress cellRangeAddress = new CellRangeAddress(rowCountExpertsCriteria, rowCountExpertsCriteria, columnCountCriteria, columnCountCriteria + 3);
				_sheet.addMergedRegion(cellRangeAddress);
				
				columnCountCriteria += 4;
			}

			rowCountExpertsCriteria += _alternatives.size() + 2;
			columnCountCriteria = 3;
		}
	}
}
