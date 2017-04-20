package sinbad2.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.label.LabelSetLinguisticDomain;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
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
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerIntervalValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.twoTuple.TwoTuple;
import sinbad2.valuation.unifiedValuation.UnifiedValuation;

public abstract class ExcelWriter {

	protected final static String SEPARATOR = "##SEPARATOR##"; //$NON-NLS-1$

	protected final static int INTEGER = 0;
	protected final static int REAL = 1;
	protected final static int TEXT = 3;
	protected final static int INTEGER_INTERVAL = 4;
	protected final static int REAL_INTERVAL = 5;
	protected final static int UNIFIED = 6;
	protected final static int TWOTUPLE = 7;
	protected final static int DELTA = 8;

	protected WritableCellFormat _timesBoldUnderline;
	protected WritableCellFormat _times;
	protected WritableCellFormat _unlockFormat;
	protected WritableCellFormat _whiteFont;
	protected int _posAlternative;
	protected WritableCellFormat _integerFormat;
	protected WritableCellFormat _realFormat;
	protected int _numSheets;
	protected boolean _existsSubCriteria;
	protected boolean _existsSubSubCriteria;
	protected WritableCellFormat _h1Format;
	protected WritableCellFormat _h2Format;
	protected WritableCellFormat _h3Format;
	
	private ProblemElementsSet _elementsSet;
	private DomainSet _domainsSet;
	private DomainAssignments _domainsAssignments;

	public ExcelWriter() {
		_numSheets = 0;
		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		_domainsSet = DomainsManager.getInstance().getActiveDomainSet();
		_domainsAssignments = DomainAssignmentsManager.getInstance().getActiveDomainAssignments();
	}

	/**
	 * Escribe el archivo Excel.
	 * 
	 * @param fileName
	 *            Nombre del archivo de salida.
	 * @throws IOException
	 *             Si existe algún error accediendo al archivo.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	protected void write(String fileName) throws IOException, WriteException {
		File file = new File(fileName);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("es", "ES")); //$NON-NLS-1$ //$NON-NLS-2$

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		createFormats();
		discoverSubElements();
		createExpertsSheets(workbook);
		createContent(workbook);
		createProblemInformation(workbook);

		workbook.write();
		workbook.close();
	}

	/**
	 * Crea los criterios para un experto.
	 * 
	 * @param sheet
	 *            Hoja en la que escribir los criterios.
	 * @param col
	 *            Columna en la que insertar los criterios.
	 * @param row
	 *            Fila en la que insertar los criterios.
	 * @param criterion
	 *            Criterio a insertar.
	 * @return Fila siguiente a la empleada.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	private int createExpertCriteria(WritableSheet sheet, int col, int row, Criterion criterion) throws WriteException {
		if (criterion != null) {
			addLabel(sheet, col, row++, criterion.getId());
			if(criterion.hasSubcriteria()) {
				for (Criterion son : criterion.getSubcriteria()) {
					row = createExpertCriteria(sheet, col + 1, row, son);
				}
			}
		} else {
			for (Criterion son : _elementsSet.getCriteria()) {
				row = createExpertCriteria(sheet, col + 1, row, son);
			}
		}

		return row;
	}

	/**
	 * Crea los criterios para un experto.
	 * 
	 * @param sheet
	 *            Hoja en la que escribir los criterios.
	 * @param row
	 *            Fila en la que insertar los criterios.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	protected void createExpertCriteria(WritableSheet sheet, int row) throws WriteException {
		createExpertCriteria(sheet, 0, row, null);
	}

	/**
	 * Crea las hojas para las evaluaciones de los expertos.
	 * 
	 * @param workbook
	 *            Libro en el que insertar las hojas.
	 * 
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	private void createExpertsSheets(WritableWorkbook workbook) throws WriteException {
		List<Expert> orphanExperts = _elementsSet.getOnlyExpertChildren();
		for (Expert expert : orphanExperts) {
			createExpertSheet(workbook, expert);
		}
	}

	/**
	 * Crea una hoja para las evaluaciones de un experto.
	 * 
	 * @param workbook
	 *            Libro en el que crear la hoja.
	 * @param expert
	 *            Experto para el que crear la hoja.
	 * @throws WriteException
	 *             Si existe algún error escribiendo la hoja.
	 */
	protected abstract void createExpertSheet(WritableWorkbook workbook, Expert expert) throws WriteException;

	/**
	 * Crea el contenido del problema.
	 * 
	 * @param workbook
	 *            Libro en el que crear el contenido.
	 * @throws WriteException
	 *             Si existe algún problema asociado a la escritura del archivo.
	 * @throws RowsExceededException
	 *             Si alguna fila sobrepasa los límites.
	 */
	protected abstract void createContent(WritableWorkbook workbook) throws WriteException, RowsExceededException ;

	/**
	 * Crea la información del problema.
	 * 
	 * @param workbook
	 *            Libro en que crear la información del problema.
	 * @throws WriteException
	 *             Si existe algún problema escribiendo la información del
	 *             problema.
	 */
	private void createProblemInformation(WritableWorkbook workbook) throws WriteException {

		workbook.createSheet("Problem", _numSheets); //$NON-NLS-1$
		WritableSheet sheet = workbook.getSheet("Problem"); //$NON-NLS-1$

		List<Expert> experts = _elementsSet.getAllExperts(); //TODO cambiado a getAll
		int row = 0;
		for (int i = 0; i < experts.size(); i++) {
			addWhiteLabel(sheet, i, row, experts.get(i).getCanonicalId());
		}
		
		row++;
		List<Alternative> alternatives = _elementsSet.getAlternatives();
		for (int i = 0; i < alternatives.size(); i++) {
			addWhiteLabel(sheet, i, row, alternatives.get(i).getId());
		}
		
		row++;
		List<Criterion> criteria = _elementsSet.getAllCriteria();
		Boolean cost;
		for (int i = 0; i < criteria.size(); i++) {
			addWhiteLabel(sheet, i, row, criteria.get(i).getCanonicalId());
			cost = criteria.get(i).isCost();
			if (cost != null) {
				addWhiteLabel(sheet, i, row + 1, cost.toString());
			}
		}
		
		row += 2;
		List<Domain> domains = _domainsSet.getDomains();
		Domain domain;
		for (int i = 0; i < domains.size(); i++) {
			addWhiteLabel(sheet, 0, row, domains.get(i).getId());
			domain = domains.get(i);
			addWhiteLabel(sheet, 1, row, domain.getClass().toString().split(" ")[1]); //$NON-NLS-1$
			String value = domain.toString();
			
			if (domain instanceof NumericIntegerDomain) {
				if (!((NumericIntegerDomain) domain).getInRange()) {
					value += ":U"; // $NON-NLS-1$ //$NON-NLS-1$
				}	
			} else if (domain instanceof Unbalanced) {
				value = ((Unbalanced) domain).toStringExport();
				value += SEPARATOR + ((Unbalanced) domain).getInfo();
			} else if (domain instanceof FuzzySet) {
				value = ((FuzzySet) domain).toStringExport();
			}
			
			addWhiteLabel(sheet, 2, row, value);
			row++;
		}
		
		Map<DomainAssignmentKey, Domain> assignments = _domainsAssignments.getAssignments();
		for (DomainAssignmentKey dk: assignments.keySet()) {
			addWhiteLabel(sheet, 0, row, dk.getExpert().getCanonicalId());
			addWhiteLabel(sheet, 1, row, dk.getAlternative().getId());
			addWhiteLabel(sheet, 2, row, dk.getCriterion().getCanonicalId());
			addWhiteLabel(sheet, 3, row, assignments.get(dk).getId());
			addWhiteLabel(sheet, 4, row, "false");
			row++;
		}

		// Fila en blanco para evitar excepciones en la lectura
		for (int i = 0; i < 5; i++) {
			addWhiteLabel(sheet, i, row, ""); //$NON-NLS-1$
		}

		sheet.getSettings().setProtected(true);
	}

	/**
	 * Crea los formatos a emplear para las celdas.
	 * 
	 * @throws WriteException
	 *             Si existe algún problema creado los formatos.
	 */
	private void createFormats() throws WriteException {

		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		_times = new WritableCellFormat(times10pt);
		_times.setWrap(true);

		WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.SINGLE);
		_timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		_timesBoldUnderline.setWrap(true);

		WritableFont h1Font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		_h1Format = new WritableCellFormat(h1Font);
		_h1Format.setBackground(Colour.GRAY_50);

		WritableFont h2Font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		_h2Format = new WritableCellFormat(h2Font);
		_h2Format.setBackground(Colour.GRAY_50);

		WritableFont h3Font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
		_h3Format = new WritableCellFormat(h3Font);

		WritableFont evaluationFormat = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
		_unlockFormat = new WritableCellFormat(evaluationFormat);
		_unlockFormat.setBackground(Colour.GRAY_25);
		_unlockFormat.setLocked(false);

		_integerFormat = new WritableCellFormat(evaluationFormat, NumberFormats.INTEGER);
		_integerFormat.setBackground(Colour.GRAY_25);
		_integerFormat.setLocked(false);

		_realFormat = new WritableCellFormat(evaluationFormat, NumberFormats.FLOAT);
		_realFormat.setBackground(Colour.GRAY_25);
		_realFormat.setLocked(false);

		WritableFont whiteFormat = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
		_whiteFont = new WritableCellFormat(whiteFormat);
	}

	/**
	 * Descubre si existen subelementos en el problema.
	 */
	private void discoverSubElements() {
		_existsSubCriteria = ExcelUtil.existsSubElements(_elementsSet.getCriteria());
		_existsSubSubCriteria = ExcelUtil.existsSubSubElements(_elementsSet.getCriteria());
		_posAlternative = 2;
		if (_existsSubCriteria) {
			_posAlternative++;
		}
		if (_existsSubSubCriteria) {
			_posAlternative++;
		}
	}

	/**
	 * Añade una evaluación unificada.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar la evaluación.
	 * @param pos
	 *            Posición de inserción
	 * @param valuation
	 *            Evaluación a insertar.
	 * @throws RowsExceededException
	 *             Si la fila de insercción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	protected void addUnifiedEvaluation(WritableSheet sheet, int[] pos, Valuation valuation) throws WriteException, RowsExceededException {
		addEvaluationLabel(sheet, pos[1], pos[2], valuation, null, UNIFIED);
	}

	/**
	 * Añade una evaluación dos tuplas.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar la evaluación.
	 * @param pos
	 *            Posición de inserción
	 * @param valuation
	 *            Evaluación a insertar.
	 * @throws RowsExceededException
	 *             Si la fila de insercción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	protected void addTwoTupleEvaluation(WritableSheet sheet, int[] pos, Valuation valuation) throws WriteException, RowsExceededException {
		addEvaluationLabel(sheet, pos[1], pos[2], valuation, null, TWOTUPLE);
	}

	/**
	 * Añade el valor delta de una evaluación dos tuplas
	 * 
	 * @param sheet
	 *            Hoja en la que insertar la evaluación.
	 * @param pos
	 *            Posición de inserción
	 * @param valuation
	 *            Evaluación a insertar.
	 * @throws RowsExceededException
	 *             Si la fila de insercción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	protected void addDeltaEvaluation(WritableSheet sheet, int[] pos, Valuation valuation) throws WriteException, RowsExceededException {
		addEvaluationLabel(sheet, pos[1], pos[2], valuation, null, DELTA);
	}

	/**
	 * Añade una evaluación numérica.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar la celda.
	 * @param pos
	 *            Posición en la que insertar la celda.
	 * @param domain
	 *            Dominio de la evaluación.
	 * @param domainName
	 *            Nombre del dominio de la evaluación.
	 * @param valuation
	 *            Valor de la evaluación.
	 * @throws RowsExceededException
	 *             Si la linea de inserción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura en el archivo.
	 */
	protected void addNumberEvaluation(WritableSheet sheet, int[] pos, Domain domain, String domainName, Valuation valuation) throws WriteException, RowsExceededException {

		WritableCellFeatures cellFeatures = null;

		if(domain instanceof NumericIntegerDomain) {
			double min = ((NumericIntegerDomain) domain).getMin();
			double max = ((NumericIntegerDomain) domain).getMax();
			cellFeatures = new WritableCellFeatures();
			cellFeatures.setNumberValidation(min, max, WritableCellFeatures.BETWEEN);
			cellFeatures.setComment("Domain " //$NON-NLS-1$
						+ domainName + "\nDomain type is 'Numeric integer'\nOnly values in range [ " //$NON-NLS-1$
						+ (long) min + ", " + (long) max //$NON-NLS-1$
						+ "]\nEmpty for not applicable.", 4, 4); //$NON-NLS-1$	
		} else if(domain instanceof NumericRealDomain) {
			double min = ((NumericRealDomain) domain).getMin();
			double max = ((NumericRealDomain) domain).getMax();
			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment("Domain " //$NON-NLS-1$
						+ domainName + "\nDomain type is 'Numeric real'\nOnly values in range [ " //$NON-NLS-1$
						+ min + ", " + max //$NON-NLS-1$
						+ "]\nEmpty for not applicable.", 4, 4); //$NON-NLS-1$
		}

		Double measure = null;
		String text = null;
		if (valuation != null) {
			if (valuation instanceof IntegerValuation) {
				measure = ((IntegerValuation) valuation).getValue();
			} else if (valuation instanceof RealValuation) {
				measure = ((RealValuation) valuation).getValue();
			} else {
				text = ":NA:"; //$NON-NLS-1$
			}
		}
		if (text == null) {
			if(domain instanceof NumericIntegerDomain) {
				addEvaluationLabel(sheet, pos[1], pos[2], measure, cellFeatures, 0);
			} else if(domain instanceof NumericRealDomain) {
				addEvaluationLabel(sheet, pos[1], pos[2], measure, cellFeatures, 1);
			}
		} else {
			addEvaluationLabel(sheet, pos[1], pos[2], text, cellFeatures, TEXT);
		}
	}

	/**
	 * Añade una evaluación lingüística.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar la evaluación.
	 * @param pos
	 *            Posición en la que insertar la evaluación.
	 * @param domain
	 *            Dominio de la evaluación.
	 * @param domainName
	 *            Nombre del dominio de la evaluación.
	 * @param valuation
	 *            Valor de la evaluación.
	 * @throws RowsExceededException
	 *             Si la linea de inserción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura en el archivo.
	 */
	protected void addLinguisticEvaluation(WritableSheet sheet, int[] pos, FuzzySet domain, String domainName, Valuation valuation) throws WriteException, RowsExceededException {

		WritableCellFeatures cellFeatures;

		LabelSetLinguisticDomain labels = domain.getLabelSet();
		List<String> values = new ArrayList<String>();
		StringBuilder validValues = new StringBuilder("("); //$NON-NLS-1$
		String labelName;
		for (LabelLinguisticDomain l : labels.getLabels()) {
			labelName = l.getName();
			values.add(labelName);
			validValues.append(labelName);
			validValues.append(","); //$NON-NLS-1$
		}
		
		if (validValues.length() > 1) {
			validValues.replace(validValues.length() - 1, validValues.length(), ")"); //$NON-NLS-1$
		} else {
			validValues.append(")"); //$NON-NLS-1$
		}
		cellFeatures = new WritableCellFeatures();
		String text = null;
		if (valuation != null) {
			if (valuation instanceof LinguisticValuation) {
				cellFeatures.setComment("Domain " + domainName //$NON-NLS-1$
						+ "\nDomain type is 'Fuzzy'\nOnly values in set " + validValues //$NON-NLS-1$
						+ "\nEmpty for not applicable.", 12, 4); //$NON-NLS-1$
				cellFeatures.setDataValidationList(values);
				text = ((LinguisticValuation) valuation).getLabel().getName();
			} else if(valuation instanceof HesitantValuation) {
				cellFeatures.setComment("Domain " + domainName //$NON-NLS-1$
						+ "\nDomain type is 'Hesitant Fuzzy'\nOnly values in set " + validValues //$NON-NLS-1$
						+ "\nEmpty for not applicable.", 12, 4); //$NON-NLS-1$
				text = ((HesitantValuation) valuation).changeFormatValuationToString();
			} else {
				text = ":NA:"; //$NON-NLS-1$
			}
		}

		addEvaluationLabel(sheet, pos[1], pos[2], text, cellFeatures, TEXT);
	}

	/**
	 * Añade una evaluación intervalar.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar la evaluación.
	 * @param pos
	 *            Posición en la que insertar la evaluación.
	 * @param domain
	 *            Dominio de la evaluación.
	 * @param domainName
	 *            Nombre del dominio de la evaluación.
	 * @param valuation
	 *            Valor de la evaluación.
	 * @throws RowsExceededException
	 *             Si la linea de inserción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura en el archivo.
	 */
	protected void addIntervalEvaluation(WritableSheet sheet, int[] pos, Domain domain, String domainName, Valuation valuation) throws WriteException, RowsExceededException {

		WritableCellFeatures[] cellFeatures = new WritableCellFeatures[2];
		int type;
		Double[] measure = null;

		if(domain instanceof NumericIntegerDomain) {
			double min = ((NumericIntegerDomain) domain).getMin();
			double max = ((NumericIntegerDomain) domain).getMax();
			
			cellFeatures[0] = new WritableCellFeatures();
			cellFeatures[1] = new WritableCellFeatures();
			cellFeatures[0].setNumberValidation(min, max, WritableCellFeatures.BETWEEN);
			cellFeatures[1].setNumberValidation(min, max, WritableCellFeatures.BETWEEN);
			cellFeatures[0].setComment("Domain " //$NON-NLS-1$
						+ domainName + "\nDomain type is 'Interval numeric integer'\nOnly values in range [ " //$NON-NLS-1$
						+ (long) min + ", " + (long) max //$NON-NLS-1$
						+ "]\nEmpty for not applicable.", 4, 4); //$NON-NLS-1$
			cellFeatures[1].setComment("Domain " //$NON-NLS-1$
						+ domainName + "\nDomain type is 'Interval numeric integer'\nOnly values in range [ " //$NON-NLS-1$
						+ (long) min + ", " + (long) max //$NON-NLS-1$
						+ "]\nEmpty for not applicable.", 4, 4); //$NON-NLS-1$
		} else if(domain instanceof NumericRealDomain) {
			double min = ((NumericRealDomain) domain).getMin();
			double max = ((NumericRealDomain) domain).getMax();
			
			cellFeatures[0] = new WritableCellFeatures();
			cellFeatures[1] = new WritableCellFeatures();
			cellFeatures[0].setComment("Domain " //$NON-NLS-1$
						+ domainName + "\nDomain type is 'Interval numeric real'\nOnly values in range [ " //$NON-NLS-1$
						+ min + ", " + max //$NON-NLS-1$
						+ "]\nEmpty for not applicable.", 4, 4); //$NON-NLS-1$
			cellFeatures[1].setComment("Domain " //$NON-NLS-1$
						+ domainName + "\nDomain type is 'Interval numeric real'\nOnly values in range [ " //$NON-NLS-1$
						+ min + ", " + max //$NON-NLS-1$
						+ "]\nEmpty for not applicable.", 4, 4); //$NON-NLS-1$
		}

		if (valuation instanceof IntegerIntervalValuation) {
			type = INTEGER_INTERVAL;
		} else {
			type = REAL_INTERVAL;
		}
		String text = null;
		if (valuation != null) {
			if (valuation instanceof IntegerIntervalValuation) {
				measure = new Double[] { ((IntegerIntervalValuation) valuation).getMin(), ((IntegerIntervalValuation) valuation).getMax() };
			} else if (valuation instanceof RealIntervalValuation) {
				measure = new Double[] { ((RealIntervalValuation) valuation).getMin(), ((RealIntervalValuation) valuation).getMax() };
			} else {
				text = ":NA:"; //$NON-NLS-1$
			}
		}
		if (text == null) {
			addEvaluationLabel(sheet, pos[1], pos[2], measure, cellFeatures, type);
		} else {
			addEvaluationLabel(sheet, pos[1], pos[2], text, cellFeatures, TEXT);
		}
	}

	/**
	 * Añade un encabezado.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar el encabezado.
	 * @param level
	 *            Nivel de encabezado.
	 * @param column
	 *            Columna en la que insertar el encabezado.
	 * @param row
	 *            Fila en la que insertar el encabezado.
	 * @param s
	 *            Texto a mostrar en el encabezado.
	 * @throws RowsExceededException
	 *             Si la fila de insercción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	protected void addHeaderLabel(WritableSheet sheet, int level, int column, int row, String s)
			throws WriteException, RowsExceededException {

		Label label;

		if (level == 1) {
			label = new Label(column, row, s, _h1Format);
		} else if (level == 2) {
			label = new Label(column, row, s, _h2Format);
		} else if (level == 3) {
			label = new Label(column, row, s, _h3Format);
		} else {
			label = addCaption(sheet, column, row, s);
		}
		sheet.addCell(label);
	}

	/**
	 * Añade una celda de título.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar el título.
	 * @param column
	 *            Columna en la que insertar el título.
	 * @param row
	 *            Fila en la que insertar el título.
	 * @param s
	 *            Texto a insertar.
	 * @return Etiqueta creada.
	 * @throws RowsExceededException
	 *             Si la fila de insercción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	private Label addCaption(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, _timesBoldUnderline);
		sheet.addCell(label);
		return label;
	}

	/**
	 * Añade una celda con texto en blanco.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar el texto en blanco.
	 * @param column
	 *            Columna en la que insertar el texto en blanco.
	 * @param row
	 *            Fila en la que insertar el texto en blanco.
	 * @param s
	 *            Texto a insertar.
	 * @return Etiqueta creada.
	 * @throws RowsExceededException
	 *             Si la fila de insercción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	private Label addWhiteLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, _whiteFont);
		sheet.addCell(label);
		return label;
	}

	/**
	 * Añade una etiqueta de evaluación.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar la evaluación.
	 * @param column
	 *            Columna en la que insertar la etiqueta.
	 * @param row
	 *            Fila en la que insertar la etiqueta.
	 * @param content
	 *            Contenido a insertar.
	 * @param cellFeatures
	 *            Características de la etiqueta.
	 * @param type
	 *            Tipo de evaluación.
	 * @return Etiqueta creada.
	 * @throws RowsExceededException
	 *             Si la fila de insercción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	private Object addEvaluationLabel(WritableSheet sheet, int column, int row, Object content, Object cellFeatures,
			int type) throws WriteException, RowsExceededException {
	
		Object result = null;
		switch (type) {
		case INTEGER:
			if (content != null) {
				result = new jxl.write.Number(column, row, (Double) content, _integerFormat);
			} else {
				result = new Label(column, row, "", _integerFormat); //$NON-NLS-1$
			}
			break;

		case REAL:
			if (content != null) {
				result = new jxl.write.Number(column, row, (Double) content, _realFormat);
			} else {
				result = new Label(column, row, "", _realFormat); //$NON-NLS-1$
			}
			break;

		case TEXT:
			result = new Label(column, row, (String) content, _unlockFormat);
			break;
		case INTEGER_INTERVAL:
			result = new Object[2];
			if (content != null) {
				((Object[]) result)[0] = new jxl.write.Number(column, row, ((Double[]) content)[0], _integerFormat);
				((Object[]) result)[1] = new jxl.write.Number(column + 1, row, ((Double[]) content)[1], _integerFormat);
			} else {
				((Object[]) result)[0] = new Label(column, row, "", //$NON-NLS-1$
						_integerFormat);
				((Object[]) result)[1] = new Label(column + 1, row, "", //$NON-NLS-1$
						_integerFormat);
			}
			break;
		case REAL_INTERVAL:
			result = new Object[2];
			if (content != null) {
				((Object[]) result)[0] = new jxl.write.Number(column, row, ((Double[]) content)[0], _realFormat);
				((Object[]) result)[1] = new jxl.write.Number(column + 1, row, ((Double[]) content)[1], _realFormat);
			} else {
				((Object[]) result)[0] = new Label(column, row, "", _realFormat); //$NON-NLS-1$
				((Object[]) result)[1] = new Label(column + 1, row, "", //$NON-NLS-1$
						_realFormat);
			}
			break;
		case UNIFIED:
			if (content != null) {
				FuzzySet domain = ((FuzzySet) ((Valuation) content).getDomain());
				int domainSize = domain.getLabelSet().getCardinality();
				result = new Object[domainSize];
				String text;
				if (content instanceof UnifiedValuation) {
					double measure;
					for (int i = 0; i < domainSize; i++) {
						measure = domain.getValue(i);
						if (measure == 0) {
							text = ".0"; //$NON-NLS-1$
						} else if (measure == 1) {
							text = "1"; //$NON-NLS-1$
						} else {
							text = Double.toString(measure);
							text = text.substring(text.indexOf(".")); //$NON-NLS-1$
							if (text.length() > 4) {
								text = text.substring(0, 4);
							}

						}
						((Object[]) result)[i] = new Label(column + i, row, text, _unlockFormat);
					}
				} else {
					for (int i = 0; i < domainSize; i++) {
						if (i == 0) {
							text = "NA";
						} else {
							text = ""; //$NON-NLS-1$
						}
						((Object[]) result)[i] = new Label(column + i, row, text, _unlockFormat);
					}
				}
			}
			break;
		case TWOTUPLE:
			if (content != null) {
				String label;
				String alphaValue;
				if (content instanceof TwoTuple) {
					TwoTuple valuation = (TwoTuple) content;
					FuzzySet domain = (FuzzySet) valuation.getDomain();
					int labelPos = domain.getLabelSet().getPos(valuation.getLabel());
					double alpha = valuation.getAlpha();
					label = Integer.toString(labelPos);
					alphaValue = Double.toString(alpha);
				} else {
					label = "NA";
					alphaValue = ""; //$NON-NLS-1$
				}
				result = new Object[2];
				((Object[]) result)[0] = new Label(column, row, label, _unlockFormat);
				((Object[]) result)[1] = new Label(column + 1, row, alphaValue, _unlockFormat);
			}
			break;

		case DELTA:
			if (content != null) {
				String cellContent;
				if (content instanceof TwoTuple) {
					TwoTuple valuation = (TwoTuple) content;
					double delta = valuation.calculateInverseDelta();
					cellContent = Double.toString(delta);
				} else {
					cellContent = "NA";
				}
				result = new Object[1];
				((Object[]) result)[0] = new Label(column, row, cellContent, _unlockFormat);
			}
		}

		if (result != null) {
			if (result instanceof Label) {
				sheet.addCell((Label) result);
				if (cellFeatures != null) {
					((Label) result).setCellFeatures((WritableCellFeatures) cellFeatures);
				}
			} else if (result instanceof jxl.write.Number) {
				sheet.addCell((jxl.write.Number) result);
				if (cellFeatures != null) {
					((jxl.write.Number) result).setCellFeatures((WritableCellFeatures) cellFeatures);
				}
			} else if (result instanceof Object[]) {
				if (((Object[]) result)[0] instanceof Label) {
					int pos = 0;
					for (Object label : (Object[]) result) {
						sheet.addCell((Label) label);
						if (cellFeatures != null) {
							((Label) label).setCellFeatures(((WritableCellFeatures[]) cellFeatures)[pos]);
						}
						pos++;
					}
				} else if (((Object[]) result)[0] instanceof jxl.write.Number) {
					sheet.addCell((jxl.write.Number) ((Object[]) result)[0]);
					sheet.addCell((jxl.write.Number) ((Object[]) result)[1]);
					if (cellFeatures != null) {
						((jxl.write.Number) ((Object[]) result)[0])
								.setCellFeatures(((WritableCellFeatures[]) cellFeatures)[0]);
						((jxl.write.Number) ((Object[]) result)[1])
								.setCellFeatures(((WritableCellFeatures[]) cellFeatures)[1]);
					}
				}
			}
		}

		return result;

	}

	/**
	 * Añade una celda con texto.
	 * 
	 * @param sheet
	 *            Hoja en la que insertar el texto.
	 * @param column
	 *            Columna en la que insertar el texto.
	 * @param row
	 *            Fila en la que insertar el texto.
	 * @param s
	 *            Texto a insertar.
	 * @return Etiqueta creada.
	 * @throws RowsExceededException
	 *             Si la fila de insercción excede el número de filas.
	 * @throws WriteException
	 *             Si existe algún error de escritura.
	 */
	private Label addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, _times);
		sheet.addCell(label);
		return label;
	}

}
