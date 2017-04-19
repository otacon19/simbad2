package sinbad2.excel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import sinbad2.core.io.IImportListener;
import sinbad2.core.io.handler.ImportHandler;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.unbalanced.Unbalanced;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.domain.valuations.DomainsValuationsManager;
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
import sinbad2.valuation.integer.interval.IntegerIntervalValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

/**
 * Crea un archivo Excel.
 * 
 * @author Estrella Liébana, Francisco Javier
 * @version 1.0
 */
public class ReadExcel implements IImportListener {

	private static final String[] FILTER_NAMES = { "Excel files (*.xls)" }; //$NON-NLS-1$
	private static final String[] FILTER_EXTS = { "*.xls" }; //$NON-NLS-1$

	private int _posAlternative;
	private ProblemElementsSet _elementsSet;
	private DomainSet _domainsSet;;
	private DomainAssignments _domainsAssignments;
	private ValuationSet _valuationsSet;
	private DomainsValuationsManager _domainsValuations;
	
	/**
	 * Constructor de ReadFile indicando el nombre del archivo.
	 * 
	 */
	public ReadExcel() {		
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		_domainsSet = DomainsManager.getInstance().getActiveDomainSet();
		_domainsAssignments = DomainAssignmentsManager.getInstance().getActiveDomainAssignments();
		_valuationsSet = ValuationSetManager.getInstance().getActiveValuationSet();
		_domainsValuations = DomainsValuationsManager.getInstance();
		
		ImportHandler.registerImportListener(this);
	}
	
	private void initialize() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterNames(FILTER_NAMES);
		dlg.setFilterExtensions(FILTER_EXTS);
		String file = dlg.open();

		try {
			read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lee el contenido del archivo indicado.
	 * 
	 * @param file
	 *            Nombre del archivo.
	 * @throws IOException
	 *             Si existe algún problema con la ruta indicada.
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws BiffException 
	 */
	public void read(String file) throws IOException, InvocationTargetException, InterruptedException, BiffException {
		final File inputWorkbook = new File(file);
		Workbook workbook = Workbook.getWorkbook(inputWorkbook);
		readProblemInformation(workbook);
		readEvaluations(workbook);
		/*final ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {
					monitor.beginTask("Opening file", IProgressMonitor.UNKNOWN);
					Workbook workbook = Workbook.getWorkbook(inputWorkbook);
					monitor.beginTask("Reading problem information", IProgressMonitor.UNKNOWN);
					readProblemInformation(workbook);
					monitor.beginTask("Reading evaluations", IProgressMonitor.UNKNOWN);
					readEvaluations(workbook);
					monitor.done();

				} catch (BiffException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		progressDialog.run(true, false, runnable);*/

	}

	/**
	 * Lee la información del problema.
	 * 
	 * @param workbook
	 *            Libro en el que buscar.
	 * @throws IOException
	 *             Si existe algún problema de lectura del archivo.
	 */
	private void readProblemInformation(Workbook workbook) throws IOException {
		int row;
		int col;
		Cell cell;
		String value;

		// Obtenemos la hoja del problema
		Sheet sheet = workbook.getSheet("Problem"); //$NON-NLS-1$

		// Leer expertos
		row = col = 0;
		Expert expert;
		Criterion criterion;
		Alternative alternative;
		String[] tokens;
		Map<String, Expert> expertsAlreadyCheck = new HashMap<String, Expert>();
		Map<String, Criterion> criteriaAlreadyCheck = new HashMap<String, Criterion>();

		do {
			try {
				cell = sheet.getCell(col++, row);
				value = cell.getContents();
				if (!value.isEmpty()) {
					String beforeLastExpert;
					
					tokens = value.split(">"); //$NON-NLS-1$
					if (tokens.length > 1) {
						Expert parent;
						
						String childId = value.substring(value.lastIndexOf(">") + 1, value.length());
						expert = new Expert(childId);
	
						beforeLastExpert = tokens[tokens.length - 2];
									
						if(expertsAlreadyCheck.get(beforeLastExpert) != null) {
							parent = expertsAlreadyCheck.get(beforeLastExpert);
						} else {
							if(_elementsSet.getExpert(beforeLastExpert) != null) {
								parent = _elementsSet.getExpert(beforeLastExpert);
							} else {
								parent = new Expert(beforeLastExpert);
							}
							expertsAlreadyCheck.put(beforeLastExpert, parent);
						}
						
						parent.addChildren(expert);
						_elementsSet.addExpert(expert, true, false);
					} else {
						expert = new Expert(value);
						_elementsSet.addExpert(expert, false, false);
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				value = ""; //$NON-NLS-1$
			}
		} while (!value.isEmpty() && (col < sheet.getColumns()));

		// Leer alternativas
		row = 1;
		col = 0;
		do {
			try {
				cell = sheet.getCell(col++, row);
				value = cell.getContents();
				if (!value.isEmpty()) {
					alternative = new Alternative(value);
					_elementsSet.addAlternative(alternative, false);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				value = ""; //$NON-NLS-1$
			}
		} while (!value.isEmpty() && (col < sheet.getColumns()));

		// Leer criterios
		row = 2;
		col = 0;
		do {
			try {
				cell = sheet.getCell(col, row);
				value = cell.getContents();
				
				if (!value.isEmpty()) {
					String beforeLastCriterion;
					
					tokens = value.split(">"); //$NON-NLS-1$
					if (tokens.length > 1) {
						Criterion parent;
						
						String childId = value.substring(value.lastIndexOf(">") + 1, value.length());
						criterion = new Criterion(childId);
	
						beforeLastCriterion = tokens[tokens.length - 2];
									
						if(criteriaAlreadyCheck.get(beforeLastCriterion) != null) {
							parent = criteriaAlreadyCheck.get(beforeLastCriterion);
						} else {
							if(_elementsSet.getCriterion(beforeLastCriterion) != null) {
								parent = _elementsSet.getCriterion(beforeLastCriterion);
							} else {
								parent = new Criterion(beforeLastCriterion);
							}
							criteriaAlreadyCheck.put(beforeLastCriterion, parent);
						}
						
						parent.addSubcriterion(criterion);
						_elementsSet.addCriterion(criterion, true, false);
					} else {
						criterion = new Criterion(value);
						_elementsSet.addCriterion(criterion, false, false);
					}
					
					cell = sheet.getCell(col, row + 1);
					value = cell.getContents();
					boolean cost;
					if (!value.isEmpty()) {
						cost = new Boolean(value);
						criterion.setCost(cost);
					}
				}
				col++;
			} catch (ArrayIndexOutOfBoundsException e) {
				value = ""; //$NON-NLS-1$
			}
		} while (!value.isEmpty() && (col < sheet.getColumns()));

		// Leer dominios
		row = 4;
		String domainName;
		String domainType;
		String domainToString;
		boolean endDomains = false;
		do {
			try {
				if (sheet.getCell(3, row).getContents().isEmpty()) {
					cell = sheet.getCell(0, row);
					domainName = cell.getContents();
					if (!domainName.isEmpty()) {
						cell = sheet.getCell(1, row);
						domainType = cell.getContents();
						cell = sheet.getCell(2, row);
						domainToString = cell.getContents();
						if (!domainToString.isEmpty()) {
							Domain domain = generateDomain(domainType, domainToString);
							
							String domainTypeId = domainType.replace("sinbad2", "flintstones");
							domainTypeId = domainTypeId.substring(0, domainTypeId.lastIndexOf("."));
			
							if(domain instanceof FuzzySet) {
								domainTypeId = domainTypeId.substring(0, domainTypeId.lastIndexOf("."));
							}
							
							domain.setId(domainName);
							domain.setType(domainTypeId);
							_domainsSet.addDomain(domain, false);
				
							row++;
						}
					} else {
						endDomains = true;
					}
				} else {
					endDomains = true;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				endDomains = true;
			}
		} while (!endDomains);

		// Leer asignaciones
		String domainId, expertId, alternativeId, criterionId;
		try {
			while ((row < sheet.getRows()) && (!sheet.getCell(4, row).getContents().isEmpty())) {
				expertId = sheet.getCell(0, row).getContents();
				if(expertId.contains(">")) {
					expertId = expertId.substring(expertId.lastIndexOf(">") + 1, expertId.length());
				}
				alternativeId = sheet.getCell(1, row).getContents();
				criterionId = sheet.getCell(2, row).getContents();
				if(criterionId.contains(">")) {
					criterionId = criterionId.substring(criterionId.lastIndexOf(">") + 1, criterionId.length());
				}
				domainId = sheet.getCell(3, row).getContents();
				_domainsAssignments.setDomain(_elementsSet.getExpert(expertId), _elementsSet.getAlternative(alternativeId), _elementsSet.getCriterion(criterionId), _domainsSet.getDomain(domainId));
				row++;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lee las evaluaciones del problema.
	 * 
	 * @param workbook
	 *            Libro del que leer las evaluaciones.
	 * @throws IOException
	 *             Si ocurre algún problema de lectura.
	 */
	private void readEvaluations(Workbook workbook) throws IOException {
		discoverSubElements();

		String expertId;
		String criterionId;
		String alternativeId;
		Valuation valuation;
		String comments;
		String valuationText;
		String valuationLowerText;
		String valuationUpperText;
		double lowerValue;
		double upperValue;
		int[] pos;
		Domain domain;
		Expert expert;
		Criterion criterion;
		Alternative alternative;
		Sheet sheet;
		for (DomainAssignmentKey dk : _domainsAssignments.getAssignments().keySet()) {
			expertId = dk.getExpert().getCanonicalId();
			alternativeId = dk.getAlternative().getId();
			criterionId = dk.getCriterion().getCanonicalId();
			pos = ExcelUtil.findPos(workbook, expertId, alternativeId, criterionId, _posAlternative);

			if ((pos[1] != -1) && (pos[2] != -1)) {
				sheet = workbook.getSheet(pos[0]);
				valuationText = sheet.getCell(pos[1], pos[2]).getContents();
				comments =  sheet.getCell(pos[1], pos[2]).getCellFeatures().getComment();
				
				expert = _elementsSet.getExpert(dk.getExpert().getId());
				alternative = _elementsSet.getAlternative(alternativeId);
				criterion = _elementsSet.getCriterion(dk.getCriterion().getId());
				domain = _domainsAssignments.getDomain(expert, alternative, criterion);
	
				if (domain != null) {
					if (valuationText.isEmpty()) {
						valuation = null;
					} else if (valuationText.equals(":NA:")) { //$NON-NLS-1$
						valuation = null;
					} else if (domain instanceof NumericIntegerDomain) {
						double measure = Double.MIN_VALUE;
						try {
							measure = Double.parseDouble(valuationText);
						} catch (NumberFormatException ex) {
							if (valuationText.contains(",")) { //$NON-NLS-1$
								measure = Double.parseDouble(valuationText.replace(",", ".")); //$NON-NLS-1$ //$NON-NLS-2$
							} else if (valuationText.contains(".")) { //$NON-NLS-1$
								measure = Double.parseDouble(valuationText.replace(".", ",")); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
						
						if(!comments.contains("Interval")) {
							valuation = new IntegerValuation((NumericIntegerDomain) domain, measure);
							valuation.setId(IntegerValuation.ID);
						} else {
							valuationLowerText = valuationText;
							valuationUpperText = sheet.getCell(pos[1] + 1, pos[2]).getContents();
							if ((valuationLowerText.isEmpty()) || (valuationUpperText.isEmpty())) {
								valuation = null;
							} else {
								lowerValue = Double.parseDouble(valuationLowerText.replace(",", ".")); //$NON-NLS-1$ //$NON-NLS-2$
								upperValue = Double.parseDouble(valuationUpperText.replace(",", ".")); //$NON-NLS-1$ //$NON-NLS-2$
								if (lowerValue <= upperValue) {
									valuation = new IntegerIntervalValuation((NumericIntegerDomain) domain, lowerValue, upperValue);
									valuation.setId(IntegerIntervalValuation.ID);
								} else {
									valuation = null;
								}
							}		
						}
					} else if (domain instanceof NumericRealDomain) {
						double measure = Double.MIN_VALUE;
						try {
							measure = Double.parseDouble(valuationText);
						} catch (NumberFormatException ex) {
							if (valuationText.contains(",")) { //$NON-NLS-1$
								measure = Double.parseDouble(valuationText.replace(",", ".")); //$NON-NLS-1$ //$NON-NLS-2$
							} else if (valuationText.contains(".")) { //$NON-NLS-1$
								measure = Double.parseDouble(valuationText.replace(".", ",")); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
						if(!comments.contains("interval")) {
							valuation = new RealValuation((NumericRealDomain) domain, measure);
							valuation.setId(RealValuation.ID);
						} else {
							valuationLowerText = valuationText;
							valuationUpperText = sheet.getCell(pos[1] + 1, pos[2]).getContents();
							if ((valuationLowerText.isEmpty()) || (valuationUpperText.isEmpty())) {
								valuation = null;
							} else {
								lowerValue = Double.parseDouble(valuationLowerText.replace(",", ".")); //$NON-NLS-1$ //$NON-NLS-2$
								upperValue = Double.parseDouble(valuationUpperText.replace(",", ".")); //$NON-NLS-1$ //$NON-NLS-2$
								if (lowerValue <= upperValue) {
									valuation = new RealIntervalValuation((NumericRealDomain) domain, lowerValue, upperValue);
									valuation.setId(RealIntervalValuation.ID);
								} else {
									valuation = null;
								}
							}
						}
					} else if (domain instanceof FuzzySet) {
						valuation = new LinguisticValuation();
						valuation.setId(LinguisticValuation.ID);
						valuation.setDomain(domain);
						((LinguisticValuation) valuation).setLabel(((FuzzySet) domain).getLabelSet().getLabel(valuationText));
					} else {
						valuation = null;
					}
					
					_valuationsSet.setValuation(expert, alternative, criterion, valuation);
					_domainsValuations.addSupportedValuationForSpecificDomain(domain.getId(), valuation.getId());
				}
			}
		}
	}

	/**
	 * Genera un dominio válido a partir de información excel.
	 * 
	 * @param domainType
	 *            Clase del dominio.
	 * @param string
	 *            Valor del dominio en modo texto.
	 * @return Dominio generado.
	 */
	private Domain generateDomain(String domainType, String string) {
		Domain result = null;
		if ("sinbad2.domain.numeric.integer.NumericIntegerDomain".equals(domainType) || "sinbad2.domain.numeric.real.NumericRealDomain".equals(domainType)) { //$NON-NLS-1$
			String[] tokens = string.split(":"); //$NON-NLS-1$
			string = tokens[0].substring(1, tokens[0].length() - 1);
			String[] limits = string.split(", "); //$NON-NLS-1$
			double lowerLimit = Double.parseDouble(limits[0]);
			double upperLimit = Double.parseDouble(limits[1]);

			if (limits[0].contains(".")) { //$NON-NLS-1$
				result = new NumericRealDomain();
				((NumericRealDomain) result).setMinMax(lowerLimit, upperLimit);
				if (tokens.length > 1) {
					((NumericRealDomain) result).setInRange(false);
				}
			} else {
				result = new NumericIntegerDomain();
				((NumericIntegerDomain) result).setMinMax((int) lowerLimit, (int) upperLimit);
				if (tokens.length > 1) {
					((NumericRealDomain) result).setInRange(false);
				}
			}
			
		} else if ("sinbad2.domain.linguistic.fuzzy.FuzzySet".equals(domainType)) {
			if (string.contains(ExcelWriter.SEPARATOR)) {
				String aux[] = string.split(ExcelWriter.SEPARATOR);
				string = aux[0];
				//info = aux[1];
			}
			string = string.substring(1, string.length() - 2);
			String[] labels = string.split("\\], "); //$NON-NLS-1$
			String label;
			String fuzzyNumberAndValue;
			String fuzzyNumber;
			String[] labelTokens;
			String[] fuzzyTokens;
			String[] limits;
			List<LabelLinguisticDomain> domainLabels = new LinkedList<LabelLinguisticDomain>();
			List<Double> measures = new ArrayList<Double>();
			double[] labelValues;
			for (int i = 0; i < labels.length; i++) {
				label = labels[i];
				label = label.substring(1);
				labelTokens = label.split("::"); //$NON-NLS-1$
				fuzzyNumberAndValue = labelTokens[1];
				fuzzyTokens = fuzzyNumberAndValue.split(";");
				fuzzyNumber = fuzzyTokens[0];
				fuzzyNumber = fuzzyNumber.replace("Trapezoidal", "").replace("(", "").replace(")", "");
				limits = fuzzyNumber.split(",");
				
				labelValues = new double[limits.length];
				for(int l = 0; l < limits.length; ++l) {
					labelValues[l] = Double.parseDouble(limits[l]);
				}
				
				LabelLinguisticDomain newLabel = new LabelLinguisticDomain(labelTokens[0], new TrapezoidalFunction(labelValues));
				domainLabels.add(newLabel);
				measures.add(Double.parseDouble(fuzzyTokens[1]));
			}

			result = new FuzzySet(domainLabels);
			((FuzzySet) result).setValues(measures);

		} else if (Unbalanced.class.toString().equals(domainType)) {
			//TODO
		}

		return result;
	}

	/**
	 * Descubre si el problema cuenta con subelementos.
	 */
	private void discoverSubElements() {
		_posAlternative = 2;
		if (ExcelUtil.existsSubElements(_elementsSet.getCriteria())) {
			_posAlternative++;
		}
		if (ExcelUtil.existsSubSubElements(_elementsSet.getCriteria())) {
			_posAlternative++;
		}
	}

	@Override
	public void notifyImportData() {
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
