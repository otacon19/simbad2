package sinbad2.domain.ui.view.domainindex.provider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.domain.DomainIndex;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.listener.DomainSetChangeEvent;
import sinbad2.domain.listener.IDomainSetChangeListener;
import sinbad2.domain.listener.IDomainSetListener;

public class DomainIndexContentProvider implements IStructuredContentProvider, IDomainSetListener, IDomainSetChangeListener {
	
	private DomainsManager _domainsManager;
	private DomainIndex _index;
	private DomainSet _domainSet;
	private TableViewer _viewer;

	private DomainIndexContentProvider() {
		_domainsManager = DomainsManager.getInstance();
		_domainSet = _domainsManager.getActiveDomainSet();
		_index = new DomainIndex(_domainSet);

		_domainSet.registerDomainsListener(this);
		_domainsManager.registerDomainSetChangeListener(this);
	}

	public DomainIndexContentProvider(TableViewer viewer) {
		this();
		_viewer = viewer;
	}

	@Override
	public void dispose() {
		_domainSet.unregisterDomainsListener(this);
		_domainsManager.unregisterDomainSetChangeListener(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@Override
	public Object[] getElements(Object inputElement) {
		DomainIndex index = ((DomainIndex) inputElement);
		Map<String, String> entries = index.getIdIndex();
		List<String[]> result = new LinkedList<String[]>();
		List<String> keys = new LinkedList<String>(entries.keySet());
		Collections.sort(keys);
		
		for (String key : keys) {
			result.add(new String[] { key, entries.get(key) });
		}
		return result.toArray(new String[0][0]);
	}

	public Object getInput() {
		return _index;
	}

	@Override
	public void notifyDomainSetListener(DomainSetChangeEvent event) {
		_index = new DomainIndex(_domainSet);
		_viewer.setInput(_index);
		packViewer();
	}

	private void packViewer() {
		
		for (int i = 0; i < _viewer.getTable().getColumns().length - 1 ; ++i) {
			_viewer.getTable().getColumn(i).pack();
		}
	}

	@Override
	public void notifyNewDomainSet(DomainSet domainSet) {
		
		if (_domainSet != domainSet) {
			_domainSet.unregisterDomainsListener(this);
			_domainSet = domainSet;
			_domainSet.registerDomainsListener(this);
			_index = new DomainIndex(_domainSet);
			_viewer.setInput(_index);
		}
	}
	
}
