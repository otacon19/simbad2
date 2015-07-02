package sinbad2.resolutionphase.framework;

import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class Framework implements IResolutionPhase {
	
	public static final String ID = "flintstones.resolutionphase.framework"; //$NON-NLS-1$

	private ProblemElementsSet _elementSet;
	private DomainSet _domainSet;

	private DomainsManager _domainsManager;
	private ProblemElementsManager _elementsManager;

	public Framework() {
		_domainsManager = DomainsManager.getInstance();
		_elementsManager = ProblemElementsManager.getInstance();
		_domainSet = new DomainSet();
		_elementSet = new ProblemElementsSet();
	}

	public ProblemElementsSet getElementSet() {
		return _elementSet;
	}

	public DomainSet getDomainSet() {
		return _domainSet;
	}

	@Override
	public IResolutionPhase copyStructure() {
		return new Framework();
	}

	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		Framework framework = (Framework) iResolutionPhase;

		clear();
		_elementSet.setExperts(framework.getElementSet().getExperts());
		_elementSet.setAlternatives(framework.getElementSet().getAlternatives());
		_elementSet.setCriteria(framework.getElementSet().getCriteria());
		_domainSet.setDomains(framework.getDomainSet().getDomains());
	}

	@Override
	public void clear() {
		_elementSet.clear();
		_domainSet.clear();
	}

	@Override
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException {
		XMLStreamWriter streamWriter = writer.getStreamWriter();
		try {
			_elementSet.save(streamWriter);
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}
		try {
			_domainSet.save(streamWriter);
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}
	}

	@Override
	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer)
			throws WorkspaceContentPersistenceException {
		try {
			_elementSet.read(reader);
			_domainSet.read(reader);
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_elementSet);
		hcb.append(_domainSet);
		return hcb.toHashCode();
	}

	@Override
	public IResolutionPhase clone() {
		Framework result = null;

		try {
			result = (Framework) super.clone();
			result._elementSet = (ProblemElementsSet) _elementSet.clone();
			result._domainSet = (DomainSet) _domainSet.clone();
		} catch (CloneNotSupportedException e) {

		}

		return result;
	}

	@Override
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {
		if (event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			activate();
		}
	}

	@Override
	public void activate() {
		_elementsManager.setActiveElementSet(_elementSet);
		_domainsManager.setActiveDomainSet(_domainSet);
	}

	@Override
	public boolean validate() {
		if (_domainSet.getDomains().isEmpty()) {
			return false;
		}

		if (_elementSet.getAlternatives().isEmpty()) {
			return false;
		}

		if (_elementSet.getExperts().isEmpty()) {
			return false;
		}

		if (_elementSet.getCriteria().isEmpty()) {
			return false;
		}

		return true;
	}

}