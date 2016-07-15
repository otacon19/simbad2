package sinbad2.resolutionscheme.io;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionscheme.ResolutionScheme;
import sinbad2.resolutionscheme.ResolutionSchemeImplementation;
import sinbad2.resolutionscheme.ResolutionSchemesManager;

public class XMLRead extends sinbad2.resolutionphase.io.XMLRead {
	
	public XMLRead(String fileName) {
		super(fileName);
	}
	
	public ResolutionSchemeImplementation read() throws IOException,
		WorkspaceContentPersistenceException {
		
		ResolutionSchemeImplementation result = null;
		
		try {
			_reader = _factory.createXMLEventReader(new FileReader(_fileName));
			
			boolean end = false;
			while(hasNext() && !end) {
				next();
				if(event().isStartDocument()) {
					result = getResolutionScheme();
				} else if(event().isEndDocument()) {
					end = true;
				}
			}
			
			_reader.close();
			
		} catch(XMLStreamException e) {
			try {
				_reader.close();
			} catch(XMLStreamException se) {
				se.printStackTrace();
			}
			throw new WorkspaceContentPersistenceException();
		}
		
		return result;
	}
	
	private ResolutionSchemeImplementation getResolutionScheme() 
			throws XMLStreamException {
		
		ResolutionSchemeImplementation result = null;
		
		goToStartElement("resolution.scheme"); //$NON-NLS-1$
		String resolutionSchemeId = getStartElementAttribute("id"); //$NON-NLS-1$
		
		ResolutionSchemesManager rsm = ResolutionSchemesManager.getInstance();
		ResolutionScheme resolutionScheme = rsm.getResolutionScheme(resolutionSchemeId);
		
		result = resolutionScheme.getImplementation();
		result = (ResolutionSchemeImplementation) result.copyStructure();
		
		GetResolutionPhaseResult aux;
		int numberOfPhases = result.getPhasesNames().size();
		Map<String, IResolutionPhase> buffer = new HashMap<String, IResolutionPhase>();
		for(int i = 0; i < numberOfPhases; ++i) {
			aux = getResolutionPhaseResult(buffer);
			result.getPhasesImplementation().put(aux._id, aux._iresolutionPhase);
		}
		
		goToEndElement("resolution-scheme"); //$NON-NLS-1$
		
		return result;
		
	}
}
