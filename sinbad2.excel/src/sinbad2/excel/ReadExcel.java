package sinbad2.excel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import sinbad2.core.io.IImportListener;
import sinbad2.core.io.handler.ImportHandler;
import sinbad2.core.workspace.Workspace;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.linguistic.fuzzy.label.LabelSetLinguisticDomain;
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
import sinbad2.valuation.hesitant.EUnaryRelationType;
import sinbad2.valuation.hesitant.HesitantValuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.integer.interval.IntegerIntervalValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.real.interval.RealIntervalValuation;
import sinbad2.valuation.valuationset.ValuationKey;
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
	private DomainSet _domainsSet;
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

		if(file != null) {
			Workspace.getWorkspace().close();
			try {
				read(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

		if(file != null) {
			final File inputWorkbook = new File(file);
	
			new Thread(new Runnable() {
				
				public void run() {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {	
							ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
							progressDialog.open();
							IProgressMonitor monitor = progressDialog.getProgressMonitor();
							
							try {
								monitor.beginTask("Reading problem...", 100);
								Workbook workbook = Workbook.getWorkbook(inputWorkbook);
								monitor.worked(70);
								readProblemInformation(workbook); 
								monitor.worked(50);
								readEvaluations(workbook); 
								monitor.worked(50);
							} catch (BiffException | IOException e) {
								e.printStackTrace();
							}
							progressDialog.close();
						}
					});
				}
			}).start();
		}

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

						if (expertsAlreadyCheck.get(beforeLastExpert) != null) {
							parent = expertsAlreadyCheck.get(beforeLastExpert);
						} else {
							if (_elementsSet.getExpert(beforeLastExpert) != null) {
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

						if (criteriaAlreadyCheck.get(beforeLastCriterion) != null) {
							parent = criteriaAlreadyCheck.get(beforeLastCriterion);
						} else {
							if (_elementsSet.getCriterion(beforeLastCriterion) != null) {
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

							if (domain instanceof FuzzySet || domain instanceof Unbalanced) {
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
				if (expertId.contains(">")) {
					expertId = expertId.substring(expertId.lastIndexOf(">") + 1, expertId.length());
				}
				alternativeId = sheet.getCell(1, row).getContents();
				criterionId = sheet.getCell(2, row).getContents();
				if (criterionId.contains(">")) {
					criterionId = criterionId.substring(criterionId.lastIndexOf(">") + 1, criterionId.length());
				}
				domainId = sheet.getCell(3, row).getContents();
				_domainsAssignments.setDomain(_elementsSet.getExpert(expertId),
						_elementsSet.getAlternative(alternativeId), _elementsSet.getCriterion(criterionId),
						_domainsSet.getDomain(domainId));
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

		String expertId, criterionId, alternativeId, comments, valuationText, valuationLowerText, valuationUpperText;
		double lowerValue, upperValue;
		int[] pos;
		Map<ValuationKey, Valuation> valuations = new HashMap<ValuationKey, Valuation>();

		Expert expert;
		Criterion criterion;
		Alternative alternative;
		Valuation valuation = null;
		Domain domain;

		Sheet sheet;

		for (DomainAssignmentKey dk : _domainsAssignments.getAssignments().keySet()) {
			expertId = dk.getExpert().getCanonicalId();
			alternativeId = dk.getAlternative().getId();
			criterionId = dk.getCriterion().getId();
			pos = ExcelUtil.findPos(workbook, expertId, alternativeId, criterionId, _posAlternative);

			if ((pos[1] != -1) && (pos[2] != -1)) {
				sheet = workbook.getSheet(pos[0]);
				valuationText = sheet.getCell(pos[1], pos[2]).getContents();
				comments = sheet.getCell(pos[1], pos[2]).getCellFeatures().getComment();

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

						if (!comments.contains("Interval")) {
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
									valuation = new IntegerIntervalValuation((NumericIntegerDomain) domain, lowerValue,
											upperValue);
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
						if (!comments.contains("interval")) {
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
									valuation = new RealIntervalValuation((NumericRealDomain) domain, lowerValue,
											upperValue);
									valuation.setId(RealIntervalValuation.ID);
								} else {
									valuation = null;
								}
							}
						}
					} else if (domain instanceof FuzzySet || domain instanceof Unbalanced) {
						if(!comments.contains("Hesitant")) {
							valuation = new LinguisticValuation();
							valuation.setId(LinguisticValuation.ID);
							valuation.setDomain(domain);
							((LinguisticValuation) valuation).setLabel(((FuzzySet) domain).getLabelSet().getLabel(valuationText));
						} else {
							valuation = new HesitantValuation();
							valuation.setId(HesitantValuation.ID);
							valuation.setDomain(domain);
							if(valuationText.contains("Lower than")) {
								valuationText = valuationText.replace("Lower than ", "");
								((HesitantValuation) valuation).setUnaryRelation(EUnaryRelationType.LowerThan, (((FuzzySet) domain).getLabelSet().getLabel(valuationText)));
							} else if(valuationText.contains("Greater than")) {
								valuationText = valuationText.replace("Greater than ", "");
								((HesitantValuation) valuation).setUnaryRelation(EUnaryRelationType.GreaterThan, (((FuzzySet) domain).getLabelSet().getLabel(valuationText)));
							} else if(valuationText.contains("At least")) {
								valuationText = valuationText.replace("At least ", "");
								((HesitantValuation) valuation).setUnaryRelation(EUnaryRelationType.AtLeast, (((FuzzySet) domain).getLabelSet().getLabel(valuationText)));
							} else if(valuationText.contains("At most")) {
								valuationText = valuationText.replace("At most ", "");
								((HesitantValuation) valuation).setUnaryRelation(EUnaryRelationType.AtMost, (((FuzzySet) domain).getLabelSet().getLabel(valuationText)));
							} else if(valuationText.contains("Between")) {
								valuationText = valuationText.replace("Between ", "");
								String lowerTerm = valuationText.substring(0, valuationText.indexOf(" "));
								String upperTerm = valuationText.substring(valuationText.lastIndexOf(" ") + 1, valuationText.length());
								((HesitantValuation) valuation).setBinaryRelation(lowerTerm, upperTerm);
							} else {
								((HesitantValuation) valuation).setLabel(((FuzzySet) domain).getLabelSet().getLabel(valuationText));
							}
						}
					} else {
						valuation = null;
					}
					
					if(valuation != null) {
						valuations.put(new ValuationKey(expert, alternative, criterion), valuation);
						_domainsValuations.addSupportedValuationForSpecificDomain(domain.getId(), valuation.getId());
					}
				}
			}
		}
		_valuationsSet.setValuations(valuations);
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

		if ("sinbad2.domain.numeric.integer.NumericIntegerDomain".equals(domainType) //$NON-NLS-1$
				|| "sinbad2.domain.numeric.real.NumericRealDomain".equals(domainType)) {
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
			String aux[] = string.split(ExcelWriter.SEPARATOR);
			string = aux[0];
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
				for (int l = 0; l < limits.length; ++l) {
					labelValues[l] = Double.parseDouble(limits[l]);
				}

				LabelLinguisticDomain newLabel = new LabelLinguisticDomain(labelTokens[0], new TrapezoidalFunction(labelValues));
				domainLabels.add(newLabel);
				measures.add(Double.parseDouble(fuzzyTokens[1]));
			}

			result = new FuzzySet(domainLabels);
			((FuzzySet) result).setValues(measures);
			
		} else if ("sinbad2.domain.linguistic.unbalanced.Unbalanced".equals(domainType)) { 
			String info;
			String aux[] = string.split(ExcelWriter.SEPARATOR);
			string = aux[0];
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
				for (int l = 0; l < limits.length; ++l) {
					labelValues[l] = Double.parseDouble(limits[l]);
				}

				LabelLinguisticDomain newLabel = new LabelLinguisticDomain(labelTokens[0], new TrapezoidalFunction(labelValues));
				domainLabels.add(newLabel);
				measures.add(Double.parseDouble(fuzzyTokens[1]));
			}
			
			info = aux[1];
			String[] informationPieces = info.split(";");

			//lh
			String[] lhString = informationPieces[0].substring(informationPieces[0].indexOf("=") + 2, informationPieces[0].length()).split(",");
			int[] lh = new int[lhString.length];
			for(int l = 0; l < lh.length; ++l) {
				lh[l] = Integer.parseInt(lhString[l]);
			}
			
			//sl
			String slString = informationPieces[1].substring(informationPieces[1].indexOf("=") + 2, informationPieces[1].length());
			int sl = Integer.parseInt(slString);
			
			//slDensity
			String slDensityString = informationPieces[2].substring(informationPieces[2].indexOf("=") + 2, informationPieces[2].length());
			int slDensity = Integer.parseInt(slDensityString);
	
			//sr
			String srString = informationPieces[3].substring(informationPieces[3].indexOf("=") + 2, informationPieces[3].length());
			int sr = Integer.parseInt(srString);
			
			//srDensity
			String srDensityString = informationPieces[4].substring(informationPieces[4].indexOf("=") + 2, informationPieces[2].length());
			int srDensity = Integer.parseInt(srDensityString);
			
			//labels
			String labelsString = informationPieces[5].replace("labels =", "");
			String[] levels = labelsString.split("&");
			Map<Integer, Map<Integer, Integer>> labelsUnbalanced = new HashMap<Integer, Map<Integer, Integer>>();
			for(int l = 0; l < levels.length; ++l) {
				String stringLabel = levels[l].substring(0, 1);
				int labelUnbalanced = Integer.parseInt(stringLabel);
				String domains = levels[l].substring(levels[l].indexOf("{") + 1, levels[l].indexOf("}"));
				String[] stringDomains = domains.split(",");
		
				Map<Integer, Integer> labelsDomains = new HashMap<Integer, Integer>();
				for(int d = 0; d < stringDomains.length; d++) {
					String[] domainLabel = stringDomains[d].split("=");
					int domain = Integer.parseInt(domainLabel[0]);
					int ld = Integer.parseInt(domainLabel[1]);
					labelsDomains.put(domain, ld);
				}
				labelsUnbalanced.put(labelUnbalanced, labelsDomains);;
			}
		
			result = new Unbalanced();
			((Unbalanced) result).setLh(lh);
			((Unbalanced) result).setSl(sl);
			((Unbalanced) result).setSlDensity(slDensity);
			((Unbalanced) result).setSr(sr);
			((Unbalanced) result).setSrDensity(srDensity);
			((Unbalanced) result).setLabels(labelsUnbalanced);
			((Unbalanced) result).setLabelSet(new LabelSetLinguisticDomain(domainLabels));
			((Unbalanced) result).setValues(measures);
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
