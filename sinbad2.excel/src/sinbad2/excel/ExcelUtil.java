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

	private CellStyle _styleExperts;
	private CellStyle _styleAlternatives;
	private CellStyle _styleCriteria;
	private CellStyle _styleTitles;
	
	private List<Expert> _experts;
	private List<Criterion> _criteria;
	private List<Alternative> _alternatives;
	private Map<ValuationKey, Valuation> _unifiedValuations;
	private List<Double> _expertsWeights;

	private ProblemElementsSet _elementsSet;

	public ExcelUtil() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();

		_experts = _elementsSet.getExperts();
		_alternatives = _elementsSet.getAlternatives();
		_criteria = _elementsSet.getAllCriteria();
		
		_workbook = new XSSFWorkbook();
		_sheet = _workbook.createSheet("Flintstones problem");
		
		_styleExperts = _workbook.createCellStyle();
		_styleExperts.setFillForegroundColor(IndexedColors.RED.getIndex());
		_styleExperts.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		_styleAlternatives = _workbook.createCellStyle();
		_styleAlternatives.setFillForegroundColor(IndexedColors.CORAL.getIndex());
		_styleAlternatives.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		_styleCriteria = _workbook.createCellStyle();
		_styleCriteria.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		_styleCriteria.setFillPattern(CellStyle.SOLID_FOREGROUND);
		_styleCriteria.setAlignment(CellStyle.ALIGN_CENTER);
		_styleCriteria.setBorderLeft(BorderStyle.THIN);
		_styleCriteria.setBorderRight(BorderStyle.THIN);
		
		_styleTitles = _workbook.createCellStyle();
		_styleTitles.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		_styleTitles.setFillPattern(CellStyle.SOLID_FOREGROUND);
		_styleTitles.setAlignment(CellStyle.ALIGN_CENTER);
	}

	public void createExcelFile(Map<ValuationKey, Valuation> unifiedValuations) {

		_unifiedValuations = unifiedValuations;

		try (FileOutputStream outputStream = new FileOutputStream("C:/Users/Flintstones/Desktop/flintstones_problem.xlsx")) {
			_workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createExcelFileEmergencyProblemStructure(Map<ValuationKey, Valuation> unifiedValuations, List<Double> expertsWeights) {

		_unifiedValuations = unifiedValuations;
		_expertsWeights = expertsWeights;
		
		createProblemInformation();

		try (FileOutputStream outputStream = new FileOutputStream("C:/Users/Flintstones/Desktop/flintstones_problem.xlsx")) {
			_workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void createProblemInformation() {
		
		createExpertsTablesInformation();
		createExpertsWeightsInformation();
	}

	private void createExpertsTablesInformation() {
		
		int rowCountExpertsCriteria = 9, rowCountAlternatives = 10, columnCountCriteria = 3, columnCountValuation = 3;

		for (Expert e : _experts) {
			
			Row rowExpertCriterion = _sheet.createRow(rowCountExpertsCriteria);
			Cell cell = rowExpertCriterion.createCell(2);
			cell.setCellValue(e.getId());
			cell.setCellStyle(_styleExperts);

			rowCountAlternatives = rowCountExpertsCriteria + 1;

			for (Alternative a : _alternatives) {

				Row rowAlternative = _sheet.createRow(rowCountAlternatives);
				cell = rowAlternative.createCell(2);
				cell.setCellValue(a.getId());
				cell.setCellStyle(_styleAlternatives);

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
				cell.setCellStyle(_styleCriteria);

				CellRangeAddress cellRangeAddress = new CellRangeAddress(rowCountExpertsCriteria, rowCountExpertsCriteria, columnCountCriteria, columnCountCriteria + 3);
				_sheet.addMergedRegion(cellRangeAddress);
				
				columnCountCriteria += 4;
			}

			rowCountExpertsCriteria += _alternatives.size() + 2;
			columnCountCriteria = 3;
		}
	}
	
	private void createExpertsWeightsInformation() {
		
		Row rowAggregationWeights = _sheet.createRow(1);
		Cell cell = rowAggregationWeights.createCell(2);
		cell.setCellStyle(_styleTitles);
		cell.setCellValue("Aggregation weights");
		CellRangeAddress cellRangeAddress = new CellRangeAddress(1, 1, 2, 1 + _experts.size());
		_sheet.addMergedRegion(cellRangeAddress);
		
		int columnCount = 2;
		Row rowExperts = _sheet.createRow(2);
		Row rowWeights = _sheet.createRow(3);
		for(int i = 0; i < _experts.size(); ++i) {
			cell = rowExperts.createCell(columnCount);
			cell.setCellStyle(_styleExperts);
			cell.setCellValue(_experts.get(i).getCanonicalId());
			
			cell = rowWeights.createCell(columnCount);
			cell.setCellValue(_expertsWeights.get(i));
			
			columnCount++;
		}
	}
}
