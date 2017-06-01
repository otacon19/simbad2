package sinbad2.domain.ui.view.domains.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.DomainSet;
import sinbad2.domain.listener.DomainSetChangeEvent;
import sinbad2.domain.listener.IDomainSetChangeListener;
import sinbad2.domain.listener.IDomainSetListener;
import sinbad2.domain.valuations.DomainsValuationsManager;
import sinbad2.domain.valuations.listener.IDomainValuationSetListener;

public class DomainsContentProvider implements IStructuredContentProvider, IDomainSetListener, IDomainSetChangeListener, IDomainValuationSetListener {
	
	private DomainsManager _domainManager;
	private DomainSet _domainSet;
	private DomainsValuationsManager _domainsValuations;
	private List<Domain> _domains;
	private TableViewer _tableViewer;
	
	private DomainsContentProvider() {
		_domainManager = DomainsManager.getInstance();
		_domainSet = _domainManager.getActiveDomainSet();
		_domainsValuations = DomainsValuationsManager.getInstance();
		
		_domains = _domainSet.getDomains();
		
		_domainSet.registerDomainsListener(this);
		_domainManager.registerDomainSetChangeListener(this);
		_domainsValuations.registerDomainValuationSetChangeListener(this);
	}
	
	public DomainsContentProvider(TableViewer tableViewer) {
		this();
		_tableViewer = tableViewer;
	}
	
	
	@Override
	public void dispose() {
		_domainSet.unregisterDomainsListener(this);
		_domainManager.unregisterDomainSetChangeListener(this);	
	}
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Domain>) inputElement).toArray(new Domain[0]);
	}
	
	public Object getInput() {
		return _domains;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyDomainSetListener(DomainSetChangeEvent event) {
		
		switch(event.getChange()) {
			case DOMAINS_CHANGES:
				_domains = (List<Domain>) event.getNewValue();
				_tableViewer.setInput(_domains);
				if(_domains.size() > 0) {
					_tableViewer.setSelection(new StructuredSelection(_domains.get(0)));
				}
				break;
			case ADD_DOMAIN:
				addDomain((Domain) event.getNewValue());
				_tableViewer.setSelection(new StructuredSelection((Domain) event.getNewValue()));
				break;
			case ADD_DOMAINS:
				Domain firstDomain = null;
				for(Domain domain: (List<Domain>) event.getNewValue()) {
					if(firstDomain == null) {
						firstDomain = domain;
					}
					
					addDomain(domain);
				}
				_tableViewer.setSelection(new StructuredSelection(firstDomain));
				break;
			case REMOVE_DOMAIN:
				_tableViewer.refresh();
				if(_domains.size() == 0) {
					_tableViewer.setSelection(new StructuredSelection());
				}
				break;
			case REMOVE_DOMAINS:
				_tableViewer.refresh();
				_tableViewer.setSelection(new StructuredSelection());
				break;
			case MODIFY_DOMAIN:
				_tableViewer.refresh();
				break;
		}
		
		packViewer();
		
	}
	
	@Override
	public void notifyDomainValuationSetListener() {
		_tableViewer.refresh();
	}
	
	private void addDomain(Domain domain) {
		int pos = 0;
		boolean find = false;
		
		do {
			if(_domains.get(pos) == domain) {
				find = true;
			} else {
				pos++;
			}
		} while(!find);
		_tableViewer.insert(domain, pos);
		
		_tableViewer.refresh();
	}
	
	private void packViewer() {
		
		for(int i = 0; i < _tableViewer.getTable().getColumns().length - 1; ++i ) {
			 _tableViewer.getTable().getColumn(i).pack();
		}
	}
	
	@Override
	public void notifyNewActiveDomainSet(DomainSet domainSet) {
		
		if(_domainSet != domainSet) {
			_domainSet.unregisterDomainsListener(this);
			_domainSet = domainSet;
			_domainSet.registerDomainsListener(this);
			_tableViewer.setInput(_domainSet);
		}
		
	}	
}
