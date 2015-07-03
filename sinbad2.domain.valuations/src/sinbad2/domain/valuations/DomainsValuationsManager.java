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

public class DomainsValuationsManager {
	
	private static DomainsValuationsManager _instance = null;
	
	private Map<String, String> _valuations;
	private Map<String, String> _supportedValuationsForDomains;
	private Map<String, String> _supportedValuationsForSpecificDomains;
	
	private Map<String, Set<String>> _valuationsNewDomainDialogs;
	private Map<String, Set<String>> _valuationsModifyDomainDialogs;
	
	private DomainsValuationsManager() {
		_valuations = new HashMap<String, String>();
		_supportedValuationsForDomains = new HashMap<String, String>();
		_valuationsNewDomainDialogs = new HashMap<String, Set<String>>();
		_valuationsModifyDomainDialogs = new HashMap<String, Set<String>>();
		
		_supportedValuationsForSpecificDomains = new HashMap<String, String>();
	}
	
	public static DomainsValuationsManager getInstance() {
		
		if(_instance == null) {
			_instance = new DomainsValuationsManager();
		}
		
		return _instance;
	}
	
	public void addValuation(String valuationId, String nameValuation) {
		_valuations.put(valuationId, nameValuation);
	}
	
	public void addSupportedValuationForDomain(String valuationId, String domainId) {
		_supportedValuationsForDomains.put(valuationId, domainId);
	}
	
	public void addSupportedValuationForSpecificDomain(String domainID, String valuationId) {
		_supportedValuationsForSpecificDomains.put(domainID, valuationId);
	}
	
	public void removeSupportedValuationForSpecificDomain(String domainID) {
		_supportedValuationsForSpecificDomains.remove(domainID);
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
	
	public String getValuationSupportedForSpecificDomain(String domainID) {
		return _supportedValuationsForSpecificDomains.get(domainID);
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
	
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("domain-valuation"); //$NON-NLS-1$
		
		for(String domainID: _supportedValuationsForSpecificDomains.keySet()) {
			writer.writeStartElement("domain-id");
			writer.writeAttribute("id", domainID); //$NON-NLS-1$
			writer.writeEndElement();
			writer.writeStartElement("valuation-id");
			writer.writeAttribute("id", _supportedValuationsForSpecificDomains.get(domainID)); //$NON-NLS-1$
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
					_supportedValuationsForSpecificDomains.put(idDomain, idValuation);
				} else if (endtag.equals("domain-valuation")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
	}
	
	public void clear() {
		_supportedValuationsForDomains.clear();
		_supportedValuationsForSpecificDomains.clear();
		_valuations.clear();
		_valuationsModifyDomainDialogs.clear();
		_valuationsNewDomainDialogs.clear();
	}
}
