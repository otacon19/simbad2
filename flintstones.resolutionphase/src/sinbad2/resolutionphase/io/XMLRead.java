package sinbad2.resolutionphase.io;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.ResolutionPhase;
import sinbad2.resolutionphase.ResolutionPhasesManager;

public class XMLRead {

	public class GetResolutionPhaseResult {

		public String _id;
		public IResolutionPhase _iresolutionPhase;

		public GetResolutionPhaseResult() {
			_id = null;
			_iresolutionPhase = null;
		}
	}

	protected XMLInputFactory _factory;
	protected XMLEventReader _reader;
	protected XMLEvent _event;
	protected String _fileName;

	private XMLRead() {
		_factory = XMLInputFactory.newInstance();
	}

	public XMLRead(String fileName) {
		this();
		_fileName = fileName;
	}

	public XMLEventReader getEventReader() {
		return _reader;
	}

	public String getStartElementLocalPart() {
		StartElement startElement = _event.asStartElement();
		return startElement.getName().getLocalPart();
	}

	public String getEndElementLocalPart() {
		EndElement endElement = _event.asEndElement();
		return endElement.getName().getLocalPart();
	}

	public void goToStartElement(String name) throws XMLStreamException {
		boolean find = false;
		while (hasNext() && !find) {
			if (next().isStartElement()) {
				if (name.equals(getStartElementLocalPart())) {
					find = true;
				}
			}
		}
	}

	public void goToEndElement(String name) throws XMLStreamException {
		boolean find = false;
		while (hasNext() && !find) {
			if (next().isEndElement()) {
				if (name.equals(getEndElementLocalPart())) {
					find = true;
				}
			}
		}
	}

	public String getStartElementAttribute(String name) {
		return _event.asStartElement().getAttributeByName(new QName(name))
				.getValue();
	}

	public GetResolutionPhaseResult getResolutionPhaseResult(
			Map<String, IResolutionPhase> buffer) throws XMLStreamException {
		GetResolutionPhaseResult result = new GetResolutionPhaseResult();

		goToStartElement("resolution.phase"); //$NON-NLS-1$
		result._id = getStartElementAttribute("id"); //$NON-NLS-1$

		ResolutionPhasesManager rpm = ResolutionPhasesManager.getInstance();
		ResolutionPhase resolutionPhase = rpm.getResolutionPhase(result._id);

		result._iresolutionPhase = resolutionPhase.getImplementation();
		result._iresolutionPhase = (IResolutionPhase) result._iresolutionPhase.copyStructure();

		try {
			result._iresolutionPhase.read(this, buffer);
		} catch (WorkspaceContentPersistenceException e) {
			throw new XMLStreamException();
		}

		goToEndElement("resolution.phase"); //$NON-NLS-1$

		buffer.put(result._id, result._iresolutionPhase);

		return result;
	}

	public boolean hasNext() {
		return _reader.hasNext();
	}

	public XMLEvent next() throws XMLStreamException {
		_event = _reader.nextEvent();
		return _event;
	}

	public XMLEvent event() {
		return _event;
	}
}