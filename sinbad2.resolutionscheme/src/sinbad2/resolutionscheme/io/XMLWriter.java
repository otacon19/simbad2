package sinbad2.resolutionscheme.io;

import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.ResolutionPhase;
import sinbad2.resolutionscheme.ResolutionScheme;

public class XMLWriter extends sinbad2.resolutionphase.io.XMLWriter {
	
	private ResolutionScheme _scheme;

	public XMLWriter(ResolutionScheme scheme, String fileName) {
		super(fileName);
		_scheme = scheme;
	}
	
	public void save() throws IOException, WorkspaceContentPersistenceException {
		
		try {
			_writer = _factory.createXMLStreamWriter(new FileWriter(_fileName));
			_writer.writeStartDocument();
			_writer.writeStartElement("resolution.scheme"); //$NON-NLS-1$
			_writer.writeAttribute("id", _scheme.getId()); //$NON-NLS-1$
			for(ResolutionPhase phase: _scheme.getPhases()) {
				add(phase);
			}
			_writer.writeEndElement();
			_writer.writeEndDocument();
			
			_writer.flush();
			_writer.close();
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}
	}
	
}
