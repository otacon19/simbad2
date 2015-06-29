package sinbad2.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.core.workspace.Workspace;
import sinbad2.domain.listener.DomainSetChangeEvent;
import sinbad2.domain.listener.EDomainSetChange;
import sinbad2.domain.listener.IDomainSetListener;
import sinbad2.resolutionphase.io.XMLRead;

public class DomainSet {
	
	private List<Domain> _domains;
	
	private List<IDomainSetListener> _listeners;
	
	public DomainSet() {
		_domains = new LinkedList<Domain>();
		_listeners = new LinkedList<IDomainSetListener>();
	}
	
	public List<Domain> getDomains() {
		return _domains;
	}
	
	public void setDomains(List<Domain> domains) {
		Validator.notNull(domains);
		
		_domains = domains;
		
		notifyDomainSetChanges(new DomainSetChangeEvent(EDomainSetChange.DOMAINS_CHANGES, null, getDomains(), false));
	}
	
	public void addDomain(Domain domain) {
		Validator.notNull(domain);
		
		_domains.add(domain);
		
		Collections.sort(_domains);
		
		notifyDomainSetChanges(new DomainSetChangeEvent(EDomainSetChange.ADD_DOMAIN, null, domain, false));
		
	}
	
	public void removeDomain(Domain domain) {
		Validator.notNull(domain);
		
		_domains.remove(domain);
		
		notifyDomainSetChanges(new DomainSetChangeEvent(EDomainSetChange.REMOVE_DOMAIN, domain, null, false));
	}
	
	public void modifyDomain(Domain oldDomain, Domain newDomain) {
		
		removeDomain(oldDomain);
		addDomain(newDomain);
		
		notifyDomainSetChanges(new DomainSetChangeEvent(EDomainSetChange.MODIFY_DOMAIN, oldDomain, newDomain, false));
	}
	
	public void removeMultipleDomains(List<Domain> removeDomains) {
		
		for(Domain removeDomain: removeDomains) {
			_domains.remove(removeDomain);
		}
		
		notifyDomainSetChanges(new DomainSetChangeEvent(EDomainSetChange.REMOVE_DOMAINS, removeDomains, null, false));
		
	}
	
	public void addMultipleDomains(List<Domain> addDomains) {
		
		for(Domain addDomain: addDomains) {
			_domains.add(addDomain);
		}
		
		notifyDomainSetChanges(new DomainSetChangeEvent(EDomainSetChange.ADD_DOMAINS, null, addDomains, false));
		
	}
	
	public void modifyIdDomain(String oldId, String newId) {
		Validator.notNull(oldId);
		Validator.notEmpty(oldId);
		Validator.notNull(newId);
		Validator.notEmpty(newId);
		
		boolean find = false;
		int pos = 0;
		int length = _domains.size();
		
		while(!find && (pos < length)) {
			if(_domains.get(pos).getId().equals(oldId)) {
				_domains.get(pos).setId(newId);
				Collections.sort(_domains);
				find = true;
			} else {
				pos++;
			}
		}
		
	}
	
	public Domain getDomain(String id) {
		
		for(Domain domain: _domains) {
			if(domain.getId().equals(id)) {
				return domain;
			}
		}
		return null;
	}
	
	public List<Domain> getDomainsForType(String type) {
		List<Domain> domainsType = new LinkedList<Domain>();
		
		for(Domain domain: _domains) {
			if(domain.getType().equals(type)) {
				domainsType.add(domain);
			}
		}
		
		return domainsType;
	}
	
	public List<String> getAllDomainsIds() {
		List<String> domainsIds = new LinkedList<String>();
		
		for(Domain domain: _domains) {
			domainsIds.add(domain.getId());
		}
		
		return domainsIds;
	}
	
	public List<String> getDomainsIdsForType(String type) {
		List<String> domainsIdsType = new LinkedList<String>();
		
		for(Domain domain: _domains) {
			if(domain.getType().equals(type)) {
				domainsIdsType.add(domain.getId());
			}
		}
		
		return domainsIdsType;
	}
	
	public void clear() {
		
		if(_domains.size() > 0) {
			_domains.clear();
			
			notifyDomainSetChanges(new DomainSetChangeEvent(EDomainSetChange.DOMAINS_CHANGES, null, getDomains(), false));
		}
	}
	
	public void registerDomainsListener(IDomainSetListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterDomainsListener(IDomainSetListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifyDomainSetChanges(DomainSetChangeEvent event) {
		
		for(IDomainSetListener listener: _listeners) {
			listener.notifyDomainSetListener(event);
		}
		
		Workspace.getWorkspace().updateHashCode();
	}
	
	public void save(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("domain-set"); //$NON-NLS-1$
		
		for(Domain domain: _domains) {
			writer.writeStartElement(domain.getType());
			writer.writeAttribute("id", domain.getId()); //$NON-NLS-1$
			domain.save(writer);
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	
	public void read(XMLRead reader) throws XMLStreamException {
		DomainsManager manager = DomainsManager.getInstance();

		XMLEvent event;
		String extensionId = null, endtag = null, id = null;
		Domain domain = null;
		boolean end = false;
		
		reader.goToStartElement("domain-set"); //$NON-NLS-1$
		
		while (reader.hasNext() && !end) {
			event = reader.next();
			if (event.isStartElement()) {
				extensionId = reader.getStartElementLocalPart();
				id = reader.getStartElementAttribute("id"); //$NON-NLS-1$
				try {
					domain = (Domain) manager.copyDomain(extensionId);
					domain.setId(id);
					domain.read(reader);
				} catch (Exception e) {
					throw new XMLStreamException();
				}

			} else if (event.isEndElement()) {
				endtag = reader.getEndElementLocalPart();
				if (endtag.equals(extensionId)) {
					addDomain(domain);
				} else if (endtag.equals("domain-set")) { //$NON-NLS-1$
					end = true;
				}
			}
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		for(Domain domain: _domains) {
			hcb.append(domain);
		}
		return hcb.toHashCode();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		DomainSet result = null;
		
		result = (DomainSet) super.clone();
		
		result._domains = new LinkedList<Domain>();
		for(Domain domain: _domains) {
			result._domains.add((Domain) domain.clone());
		}
		
		result._listeners = new LinkedList<IDomainSetListener>();
		for(IDomainSetListener listener: _listeners) {
			result._listeners.add(listener);
		}
		
		return result;
	}
}
