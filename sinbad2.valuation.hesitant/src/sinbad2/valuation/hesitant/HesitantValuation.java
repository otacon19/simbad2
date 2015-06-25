package sinbad2.valuation.hesitant;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import sinbad2.domain.linguistic.fuzzy.label.LabelLinguisticDomain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;

public class HesitantValuation extends Valuation {
	private LabelLinguisticDomain _term;
	private LabelLinguisticDomain _upperTerm;
	private LabelLinguisticDomain _lowerTerm;
	private LabelLinguisticDomain _label;
	
	public static final String ID = "flintstones.valuation.hesitant";

	@Override
	public int compareTo(Valuation arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Valuation negateValutation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String changeFormatValuationToString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		
		writer.writeStartElement("hesitant");
		
		writer.writeStartElement("label");
		_label.save(writer);
		writer.writeEndElement();
		writer.writeStartElement("term");
		_term.save(writer);
		writer.writeEndElement();
		writer.writeStartElement("upperTerm");
		_upperTerm.save(writer);
		writer.writeEndElement();
		writer.writeStartElement("lowerTerm");
		_lowerTerm.save(writer);
		writer.writeEndElement();
		
		writer.writeEndElement();
		
	}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {
		XMLEvent event;
		String endtag = null;
		boolean end = false;
		
		reader.goToStartElement("hesitant"); //$NON-NLS-1$
		
		while (reader.hasNext() && !end) {
			event = reader.next();
			if (event.isStartElement()) {
				if ("label".equals(reader.getStartElementLocalPart())) { //$NON-NLS-1$
					_label = new LabelLinguisticDomain();
					_label.read(reader);
				} else if("term".equals(reader.getStartElementLocalPart())){
					_term = new LabelLinguisticDomain();
					_term.read(reader);
				} else if("upperTerm".equals(reader.getStartElementLocalPart())){
					_upperTerm = new LabelLinguisticDomain();
					_upperTerm.read(reader);
				} else if("lowerTerm".equals(reader.getStartElementLocalPart())) {
					_lowerTerm = new LabelLinguisticDomain();
					_lowerTerm.read(reader);
				}
			} else if (event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if (endtag.equals("hesitant")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
	}

}
