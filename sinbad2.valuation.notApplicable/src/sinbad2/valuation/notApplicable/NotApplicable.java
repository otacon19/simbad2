package sinbad2.valuation.notApplicable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import sinbad2.domain.Domain;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.valuation.Valuation;

public class NotApplicable extends Valuation {
	
	public static final String ID = "flintstones.valuation.notApplicable";

	public NotApplicable() {
		super();
	}
	
	@Override
	public Valuation negateValuation() {
		return null;
	}

	@Override
	public Domain unification(Domain fuzzySet) {
		return null;
	}

	@Override
	public String changeFormatValuationToString() {
		return "N/A";
	}

	@Override
	public void save(XMLStreamWriter writer) throws XMLStreamException {}

	@Override
	public void read(XMLRead reader) throws XMLStreamException {}
	
	@Override
	public int compareTo(Valuation o) {
		if(o == null) {
			return -1;
		}

		if(o instanceof NotApplicable) {
			return 0;
		} else {
			return -1;
		}
	}

}
