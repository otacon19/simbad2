package sinbad2.resolutionphase.frameworkstructuring;

import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.framework.Framework;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignmentsManager;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class FrameworkStructuring implements IResolutionPhase {

	public static final String ID = "flintstones.resolutionphase.frameworkstructuring"; //$NON-NLS-1$

	private DomainAssignments _domainAssignments;

	private DomainAssignmentsManager _domainAssignmentsManager;

	public FrameworkStructuring() {
		_domainAssignmentsManager = DomainAssignmentsManager.getInstance();
		_domainAssignments = new DomainAssignments();
	}

	public DomainAssignments getDomainAssignments() {
		return _domainAssignments;
	}

	@Override
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException {
		XMLStreamWriter streamWriter = writer.getStreamWriter();
		
		try {
			_domainAssignments.save(streamWriter);
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}

	}

	@Override
	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer) throws WorkspaceContentPersistenceException {
		
		try {
			_domainAssignments.read(reader, (Framework) buffer.get(Framework.ID));
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}
	}

	@Override
	public void clear() {
		_domainAssignments.clear();
	}

	@Override
	public IResolutionPhase copyStructure() {
		FrameworkStructuring result = new FrameworkStructuring();
		result._domainAssignments = new DomainAssignments();

		return result;
	}

	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		FrameworkStructuring frameworkStructuring = (FrameworkStructuring) iResolutionPhase;

		clear();
		_domainAssignments.setAssignments(frameworkStructuring.getDomainAssignments().getAssignments());

	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_domainAssignments);
		return hcb.hashCode();
	}

	@Override
	public IResolutionPhase clone() {
		FrameworkStructuring result = null;

		try {
			result = (FrameworkStructuring) super.clone();
			result._domainAssignments = (DomainAssignments) _domainAssignments
					.clone();
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
		_domainAssignmentsManager.setActiveDomainAssignments(_domainAssignments);
	}

	@Override
	public boolean validate() {
		
		if (_domainAssignments.getAssignments().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

}
