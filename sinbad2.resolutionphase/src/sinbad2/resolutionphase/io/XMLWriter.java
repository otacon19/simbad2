package sinbad2.resolutionphase.io;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.ResolutionPhase;

public class XMLWriter {

	protected XMLOutputFactory _factory;
	protected XMLStreamWriter _writer;
	protected String _fileName;

	private XMLWriter() {
		_factory = XMLOutputFactory.newInstance();
	}

	public XMLWriter(String fileName) {
		this();
		_fileName = fileName;
	}

	public void add(ResolutionPhase phase)
			throws WorkspaceContentPersistenceException {
		try {
			_writer.writeStartElement("resolution.phase"); //$NON-NLS-1$
			_writer.writeAttribute("id", phase.getId()); //$NON-NLS-1$
			phase.getImplementation().save(this);
			_writer.writeEndElement();
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}
	}

	public XMLStreamWriter getStreamWriter() {
		return _writer;
	}
}
