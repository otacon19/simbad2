package sinbad2.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;

public class ExcelUtil {

	private XSSFWorkbook _workbook;
	private XSSFSheet _sheet;
	
	private ProblemElementsSet _elementsSet;
	
	public ExcelUtil() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_workbook = new XSSFWorkbook();
		_sheet = _workbook.createSheet("Flintstones problem");
	}
	
	public void createExcelFile() {
		
		try (FileOutputStream outputStream = new FileOutputStream("C:/Users/Flintstones/Desktop/flintstones_problem.xlsx")) {
			_workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createExcelFileEmergencyProblemStructure() {
		
		createDecsionMatrix();
		
		try (FileOutputStream outputStream = new FileOutputStream("C:/Users/Flintstones/Desktop/flintstones_problem.xlsx")) {
			_workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void createDecsionMatrix() {
		List<Alternative> alternatives = _elementsSet.getAlternatives();
		List<Criterion> criteria = _elementsSet.getAllCriteria();
		
		int rowCountAlternatives = 2, rowCountCriteria = 1, columnCountAlternatives = 1, columCountCriteria = 2;
		for(Alternative a: alternatives) {
			Row row = _sheet.createRow(rowCountAlternatives);
			Cell cell = row.createCell(columnCountAlternatives);
			cell.setCellValue(a.getId());
			rowCountAlternatives++;
		}
		
		for(Criterion c: criteria) {
			Row row = _sheet.createRow(rowCountCriteria);
			Cell cell = row.createCell(columCountCriteria);
			cell.setCellValue(c.getId());
			rowCountCriteria++;
		}
		
	}
}
