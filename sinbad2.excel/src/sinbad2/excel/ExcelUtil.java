package sinbad2.excel;

import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import sinbad2.element.criterion.Criterion;

public class ExcelUtil {

	/**
	 * Constructor por defecto.
	 * <p>
	 * Privado para impedir su instanciación de este modo.
	 */
	private ExcelUtil() {
	};

	/**
	 * Comprueba si existen subelementos.
	 * 
	 * @param manager
	 *            Mánager de elements.
	 * @return True si existen subelementos, False en caso contrario.
	 */
	public static boolean existsSubElements(List<Criterion> criteria) {
		for (Criterion c : criteria) {
			if (c.hasSubcriteria()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Comprueba si existen subsubelementos.
	 * 
	 * @param manager
	 *            Mánager de elementos.
	 * @return True si existen subsubelementos, False en caso contrario.
	 */
	public static boolean existsSubSubElements(List<Criterion> criteria) {
		for (Criterion c1 : criteria) {
			if(c1.hasSubcriteria()) {
				for (Criterion subcriterion : c1.getSubcriteria()) {
					if (subcriterion.hasSubcriteria()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Encuentra la posición para una evaluación.
	 * 
	 * @param workbook
	 *            Libro en el que buscar.
	 * @param expert
	 *            Experto.
	 * @param alternative
	 *            Alternativa.
	 * @param criterion
	 *            Criterio.
	 * @param posAlternative
	 *            Posición desde la que comienzan las alternativas.
	 * 
	 * @return hoja, fila y columna de la evaluación.
	 *         <p>
	 *         Si algún valor no existe valdrá -1.
	 */
	public static int[] findPos(WritableWorkbook workbook, String expert, String alternative, String criterion, int posAlternative) {
		int[] result = new int[] { -1, -1, -1 };
		result[0] = findSheet(workbook, expert);
		if (result[0] != -1) {
			Sheet sheet = workbook.getSheet(result[0]);
			result[1] = findCol(sheet, alternative, posAlternative);
			result[2] = findRow(sheet, criterion, posAlternative);
		}
		return result;
	}

	/**
	 * Encuentra la posición para una evaluación.
	 * 
	 * @param workbook
	 *            Libro en el que buscar.
	 * @param expert
	 *            Experto.
	 * @param alternative
	 *            Alternativa.
	 * @param criterion
	 *            Criterio.
	 * @param posAlternative
	 *            Posición desde la que comienzan las alternativas.
	 * 
	 * @return hoja, fila y columna de la evaluación.
	 *         <p>
	 *         Si algún valor no existe valdrá -1.
	 */
	public static int[] findPos(Workbook workbook, String expert,
			String alternative, String criterion, int posAlternative) {
		int[] result = new int[] { -1, -1, -1 };

		result[0] = findSheet(workbook, expert);
		if (result[0] != -1) {
			Sheet sheet = workbook.getSheet(result[0]);
			result[1] = findCol(sheet, alternative, posAlternative);
			result[2] = findRow(sheet, criterion, posAlternative);
		}
		return result;
	}

	/**
	 * Encuentra la hoja en la que se encuentra un experto.
	 * 
	 * @param workbook
	 *            Libro en el que buscar.
	 * @param expert
	 *            Experto.
	 * @return Número de hoja en la que se encuentra el experto.
	 *         <p>
	 *         -1 si el experto no se encuentra.
	 */
	public static int findSheet(WritableWorkbook workbook, String expert) {
		String[] sheetNames = workbook.getSheetNames();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			if (expert.replace(">", "->").equals(sheetNames[i])) { //$NON-NLS-1$ //$NON-NLS-2$
				return i;
			}
		}
		return -1;
	}

	/**
	 * Encuentra la hoja en la que se encuentra un experto.
	 * 
	 * @param workbook
	 *            Libro en el que buscar.
	 * @param expert
	 *            Experto.
	 * @return Número de hoja en la que se encuentra el experto.
	 *         <p>
	 *         -1 si el experto no se encuentra.
	 */
	public static int findSheet(Workbook workbook, String expert) {
		String[] sheetNames = workbook.getSheetNames();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			if (expert.replace(">", "->").equals(sheetNames[i])) { //$NON-NLS-1$ //$NON-NLS-2$
				return i;
			}
		}
		return -1;
	}

	/**
	 * Encuentra la fila en la que se encuentra un criterio.
	 * 
	 * @param sheet
	 *            Hoja en la que buscar.
	 * @param criterion
	 *            Criterio.
	 * @param posAlternative
	 *            Posición en la que comienzan las alternativas.
	 * @return Número de linea en la que se encuentra el criterio.
	 *         <p>
	 *         -1 si el criterio no se encuentra.
	 */
	public static int findRow(Sheet sheet, String criterion, int posAlternative) {

		int i = 0;
		boolean find = false;
		int numRows = sheet.getRows();
		while ((i < numRows) && (!find)) {
			if (sheet.getCell(1, i).getContents().equals("CRITERIA   ")) { //$NON-NLS-1$
				find = true;
			} else {
				i++;
			}
		}

		for (int j = i + 1; j < sheet.getRows(); j++) {
			for (int k = 1; k < posAlternative; k++) {
				if (sheet.getCell(k, j).getContents().equals(criterion)) {
					return j;
				}
			}
		}

		return -1;
	}

	/**
	 * Encuentra la columna de una alternativa.
	 * 
	 * @param sheet
	 *            Hoja en la que buscar.
	 * @param alternative
	 *            Nombre de la alternativa.
	 * @param posAlternative
	 *            Posición a partir de la que se encuentran las alternativas.
	 * @return Número de columna de la alternativa.
	 *         <p>
	 *         -1 si la alternativa no existe.
	 */
	public static int findCol(Sheet sheet, String alternative,
			int posAlternative) {

		int i = 0;
		boolean find = false;
		int numRows = sheet.getRows();
		while ((i < numRows) && (!find)) {
			if (sheet.getCell(posAlternative, i).getContents()
					.equals("ALTERNATIVES")) { //$NON-NLS-1$
				find = true;
			} else {
				i++;
			}
		}

		if (find) {
			for (int x = posAlternative; x < sheet.getColumns(); x++) {
				if (sheet.getCell(x, i + 1).getContents().equals(alternative)) {
					return x;
				}
			}
		}

		return -1;
	}
	
	
}
