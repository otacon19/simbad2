package sinbad2.afryca;

import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import sinbad2.core.io.IExportAFRYCAListener;
import sinbad2.core.io.handler.ExportAFRYCAHandler;
import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.linguistic.fuzzy.FuzzySet;
import sinbad2.domain.linguistic.fuzzy.function.types.TrapezoidalFunction;
import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.domain.numeric.integer.NumericIntegerDomain;
import sinbad2.domain.numeric.real.NumericRealDomain;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.expert.Expert;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.valuation.Valuation;
import sinbad2.valuation.integer.IntegerValuation;
import sinbad2.valuation.linguistic.LinguisticValuation;
import sinbad2.valuation.real.RealValuation;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class ExportAFRYCAProblem implements IExportAFRYCAListener {
	
	private static final String AFRYCA_INTEGER_DOMAIN = "afryca.domain.integer";
	private static final String AFRYCA_REAL_DOMAIN = "afryca.domain.real";
	private static final String AFRYCA_LINGUISTIC_DOMAIN = "afryca.domain.fuzzyset";
	private static final String AFRYCA_STRUCTURE = "afryca.decisionmatrix";
	
	private static final String[] FILTER_NAMES = { "AFRYCA files (*.afryca)" }; //$NON-NLS-1$
	private static final String[] FILTER_EXTS = { "*.afryca" }; //$NON-NLS-1$
	
	private static final String SEMICOLON = ";";
	
	private ProblemElementsSet _elementsSet;
	private DomainSet _domainsSet;
	private ValuationSet _valuationsSet;

	public ExportAFRYCAProblem() {
		_elementsSet = ProblemElementsManager.getInstance().getActiveElementSet();
		_domainsSet = DomainsManager.getInstance().getActiveDomainSet();
		_valuationsSet = ValuationSetManager.getInstance().getActiveValuationSet();

		ExportAFRYCAHandler.registerExportListener(this);
	}
	
	@Override
	public void notifyExportData() {
		try {
			initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initialize() throws IOException, XMLStreamException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setFilterNames(FILTER_NAMES);
		dlg.setFilterExtensions(FILTER_EXTS);
		String fileName = dlg.open();
		
		if(fileName != null) {
			write(fileName);
		}
	}
	
	private void write(String fileName) throws IOException, XMLStreamException {
		XMLWriter writer = new XMLWriter(fileName);
		XMLStreamWriter streamWriter = writer.getFactory().createXMLStreamWriter(new FileWriter(fileName));
		streamWriter.writeStartElement("gdmp"); //$NON-NLS-1$
		streamWriter.writeStartElement("file"); //$NON-NLS-1$
		streamWriter.writeEndElement(); //$NON-NLS-1$
		writeExperts(streamWriter);
		writeCriteria(streamWriter);
		writeAlternatives(streamWriter);
		writeDomains(streamWriter);
		writeStructure(streamWriter);
		writePreferences(streamWriter);
		writeDomainsStructures(streamWriter);
		streamWriter.writeEndElement();	
		streamWriter.writeEndDocument();
		streamWriter.flush();
		streamWriter.close();
	}

	private void writeExperts(XMLStreamWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("experts"); //$NON-NLS-1$
		for(Expert e: _elementsSet.getExperts()) {
			streamWriter.writeStartElement("expert");
			streamWriter.writeCharacters(e.getId());
			streamWriter.writeEndElement();
		}
		streamWriter.writeEndElement(); //$NON-NLS-1$	
	}
	
	private void writeCriteria(XMLStreamWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("criteria"); //$NON-NLS-1$
		for(Criterion c: _elementsSet.getCriteria()) {
			streamWriter.writeStartElement("criterion");
			streamWriter.writeCharacters(c.getId());
			streamWriter.writeEndElement();
		}
		streamWriter.writeEndElement(); //$NON-NLS-1$	
	}

	private void writeAlternatives(XMLStreamWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("alternatives"); //$NON-NLS-1$
		for(Alternative a: _elementsSet.getAlternatives()) {
			streamWriter.writeStartElement("alternative");
			streamWriter.writeCharacters(a.getId());
			streamWriter.writeEndElement();
		}
		streamWriter.writeEndElement(); //$NON-NLS-1$	
	}
	
	private void writeDomains(XMLStreamWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("domains"); //$NON-NLS-1$
		for(Domain d: _domainsSet.getDomains()) {
			streamWriter.writeStartElement("domain");
			if(d instanceof NumericIntegerDomain) {
				streamWriter.writeCharacters(AFRYCA_INTEGER_DOMAIN);
				streamWriter.writeCharacters(SEMICOLON);
				streamWriter.writeCharacters(d.getId());
				streamWriter.writeCharacters(SEMICOLON);
				streamWriter.writeCharacters(d.toString());
			} else if(d instanceof NumericRealDomain) {
				streamWriter.writeCharacters(AFRYCA_REAL_DOMAIN);
				streamWriter.writeCharacters(SEMICOLON);
				streamWriter.writeCharacters(d.getId());
				streamWriter.writeCharacters(SEMICOLON);
				streamWriter.writeCharacters(d.toString());
			} else if(d instanceof FuzzySet) {
				streamWriter.writeCharacters(AFRYCA_LINGUISTIC_DOMAIN);
				streamWriter.writeCharacters(SEMICOLON);
				streamWriter.writeCharacters(d.getId());
				streamWriter.writeCharacters(SEMICOLON);
				streamWriter.writeCharacters(toStringLinguisticDomain((FuzzySet) d));
			}
			streamWriter.writeEndElement();
		}
		streamWriter.writeEndElement();
	}
	
	private String toStringLinguisticDomain(FuzzySet domain) {
		String result = ""; //$NON-NLS-1$
		int cardinality = domain.getLabelSet().getCardinality();
		
		if(cardinality > 0) {
			for(int i = 0; i < cardinality; ++i) {
				if(i > 0) {
					result += "separator"; //$NON-NLS-1$
				}
				result += "[" + toStringLabel(domain.getLabelSet().getLabels().get(i)) + ";" + domain.getValues().get(i) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		
		return result;
	}
	
	private String toStringLabel(LabelLinguisticDomain label) {
		return label.getName() + ";" + toStringSemantic((TrapezoidalFunction) label.getSemantic());
	}
	
	private String toStringSemantic(TrapezoidalFunction semantic) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
		otherSymbols.setDecimalSeparator('.'); 
		DecimalFormat df = new DecimalFormat("#.##", otherSymbols); //$NON-NLS-1$
		df.setRoundingMode(RoundingMode.CEILING);
		
		return ("Trapezoidal(" + df.format(semantic.getLimits()[0]) + ", " + df.format(semantic.getLimits()[1]) + ", " + df.format(semantic.getLimits()[2]) + ", " + df.format(semantic.getLimits()[3]) + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}
	
	private void writeStructure(XMLStreamWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("structure");
		streamWriter.writeCharacters(AFRYCA_STRUCTURE);
		streamWriter.writeEndElement();
	}
	
	private void writePreferences(XMLStreamWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("preferences");
		double value;
		LabelLinguisticDomain label;
		for(int expert = 0; expert < _elementsSet.getExperts().size(); ++expert) {
			for(int row = 0; row < _elementsSet.getAllCriteria().size(); ++row) {
				streamWriter.writeStartElement("preference");
				for(int col = 0; col < _elementsSet.getAlternatives().size(); ++col) {
					Valuation v = _valuationsSet.getValuation(_elementsSet.getExperts().get(expert), _elementsSet.getAlternatives().get(col), _elementsSet.getCriteria().get(row));
					if(v instanceof IntegerValuation) {
						value = ((IntegerValuation) v).getValue();
						streamWriter.writeCharacters(Integer.toString((int) value));
					} else if(v instanceof RealValuation) {
						value = ((RealValuation) v).getValue();
						streamWriter.writeCharacters(Double.toString(value));
					} else if(v instanceof LinguisticValuation) {
						label = ((LinguisticValuation) v).getLabel();
						streamWriter.writeCharacters(label.getName());
					}
					if(col != _elementsSet.getAlternatives().size() - 1) {
						streamWriter.writeCharacters(",");
					}
				}
				streamWriter.writeEndElement();
			}
		}
		streamWriter.writeEndElement();
	}
	
	private void writeDomainsStructures(XMLStreamWriter streamWriter) throws XMLStreamException {
		streamWriter.writeStartElement("domains_structures");
		for(int expert = 0; expert < _elementsSet.getExperts().size(); ++expert) {
			for(int row = 0; row < _elementsSet.getAllCriteria().size(); ++row) {
				streamWriter.writeStartElement("domain_structure");
				for(int col = 0; col < _elementsSet.getAlternatives().size(); ++col) {
					Valuation v = _valuationsSet.getValuation(_elementsSet.getExperts().get(expert), _elementsSet.getAlternatives().get(col), _elementsSet.getCriteria().get(row));
					streamWriter.writeCharacters(v.getDomain().getId());
					if(col != _elementsSet.getAlternatives().size() - 1) {
						streamWriter.writeCharacters("separator");
					}
				}
				streamWriter.writeEndElement();
			}
		}
		streamWriter.writeEndElement();
	}
}
