package sinbad2.domain.valuations;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import sinbad2.resolutionphase.io.XMLRead;

public class DomainValuationsManager {
	
	private static DomainValuationsManager _instance = null;
	
	private Map<String, String> _valuations;
	private Map<String, String> _supportedValuationsForDomains;
	private Map<String, Set<String>> _valuationsNewDomainDialogs;
	private Map<String, Set<String>> _valuationsModifyDomainDialogs;
	
	//Necesitamos este mapa porque cuando se crea un dominio, se crea con el id que le hayas puesto en la caja de texto y ese
	//dominio ya no lo podemos encontrar en _valuationsSupportedForDomain. Hay que registrar el dominio con ese id y la valoracion
	private Map<String, String> _supportedValuationsForNewDomains;
	
	private DomainValuationsManager() {
		_valuations = new HashMap<String, String>();
		_supportedValuationsForDomains = new HashMap<String, String>();
		_valuationsNewDomainDialogs = new HashMap<String, Set<String>>();
		_valuationsModifyDomainDialogs = new HashMap<String, Set<String>>();
		
		_supportedValuationsForNewDomains = new HashMap<String, String>();
	}
	
	public static DomainValuationsManager getInstance() {
		
		if(_instance == null) {
			_instance = new DomainValuationsManager();
		}
		
		return _instance;
	}
	
	public void addValuation(String valuationId, String nameValuation) {
		_valuations.put(valuationId, nameValuation);
	}
	
	public void addSupportedValuationForDomain(String valuationId, String domainId) {
		_supportedValuationsForDomains.put(valuationId, domainId);
	}
	
	public void addSupportedValuationForNewDomain(String domainID, String valuationId) {
		_supportedValuationsForNewDomains.put(domainID, valuationId);
	}
	
	public void removeSupportedValuationForNewDomain(String domainID) {
		_supportedValuationsForNewDomains.remove(domainID);
	}
	
	public boolean hasValuationsNewDomainDialogs(String valuationID) {
		return _valuationsNewDomainDialogs.containsKey(valuationID);
	}
	
	public void addValuationNewDomainDialog(String valuationID, String newDomainDialogID) {
		Set<String> valuationNewDomainDialogs = _valuationsNewDomainDialogs.get(valuationID);
		if(valuationNewDomainDialogs == null) {
			valuationNewDomainDialogs = new HashSet<String>();
			_valuationsNewDomainDialogs.put(valuationID, valuationNewDomainDialogs);
		}
		
		valuationNewDomainDialogs.add(newDomainDialogID);
		
	}
	
	public void addValuationModifyDomainDialog(String valuationID, String modifyDomainDialogID) {
		Set<String> valuationModifyDomainDialogs = _valuationsModifyDomainDialogs.get(valuationID);
		
		if(valuationModifyDomainDialogs == null) {
			valuationModifyDomainDialogs = new HashSet<String>();
			_valuationsModifyDomainDialogs.put(valuationID, valuationModifyDomainDialogs);
		}
		
		valuationModifyDomainDialogs.add(modifyDomainDialogID);
		
	}
	
	public String getNameValuation(String idValuation) {
		return _valuations.get(idValuation);
	}
	
	public String getValuationSupportedForNewDomain(String domainID) {
		return _supportedValuationsForNewDomains.get(domainID);
	}

	public List<String> getValuationNewDomainDialogs(String valuationID) {
		List<String> result = new LinkedList<String>();
		Set<String> valuationNewDomainDialogs = _valuationsNewDomainDialogs.get(valuationID);
		
		if(valuationNewDomainDialogs != null) {
			result.addAll(valuationNewDomainDialogs);
			Collections.sort(result);
		}
		
		return result;
	}
		
	public List<String> getValuationModifyDomainDialogs(String valuationID) {
		List<String> result = new LinkedList<String>();
		Set<String> valuationModifyDomainDialogs = _valuationsModifyDomainDialogs.get(valuationID);
		
		if(valuationModifyDomainDialogs != null) {
			result.addAll(valuationModifyDomainDialogs);
			Collections.sort(result);
		}
		
		return result;
	}
	
	public List<String> getValuationsSupportedForDomain(String domainID) {
		List<String> result = new LinkedList<String>();
		
		for(String valuationId: _supportedValuationsForDomains.keySet()) {
			if(_supportedValuationsForDomains.get(valuationId).equals(domainID)) {
				result.add(valuationId);
			}
		}
		
		return result;
		
	}
	
	public Map<String, Set<String>> getValuationsNewDomainDialogs() {
		return _valuationsNewDomainDialogs;
	}
	
	public Map<String, Set<String>> getValuationsModifyDomainDialogs() {
		return _valuationsModifyDomainDialogs;
	}

	
	public Map<String, String> getSupportedValuations() {
		return _supportedValuationsForDomains;
	}
	
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("domain-valuation"); //$NON-NLS-1$
		
		for(String domainID: _supportedValuationsForNewDomains.keySet()) {
			writer.writeStartElement("domain-id");
			writer.writeAttribute("id", domainID); //$NON-NLS-1$
			writer.writeEndElement();
			writer.writeStartElement("valuation-id");
			writer.writeAttribute("id", _supportedValuationsForNewDomains.get(domainID)); //$NON-NLS-1$
			writer.writeEndElement();
			
		}
		writer.writeEndElement();
	}
	
	public void read(XMLRead reader) throws XMLStreamException {
		XMLEvent event;
		String endtag = null, idDomain = null, idValuation = null;
		boolean end = false;
		
		reader.goToStartElement("domain-valuation"); //$NON-NLS-1$
		
		while (reader.hasNext() && !end) {
			event = reader.next();
			if (event.isStartElement()) {
				if ("domain-id".equals(reader.getStartElementLocalPart())) {
					idDomain = reader.getStartElementAttribute("id"); //$NON-NLS-1$
				} else if ("valuation-id".equals(reader.getStartElementLocalPart())) {
					idValuation = reader.getStartElementAttribute("id");
				}
			} else if (event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if (endtag.equals("valuation-id")) {
					_supportedValuationsForNewDomains.put(idDomain, idValuation);
				} else if (endtag.equals("domain-valuation")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
	}
	
}
