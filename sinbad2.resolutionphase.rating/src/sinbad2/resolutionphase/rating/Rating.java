package sinbad2.resolutionphase.rating;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.method.Method;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class Rating implements IResolutionPhase {

	public static final String ID = "flintstones.resolutionphase.rating";
	
	List<Method> _methods;
	
	public Rating() {
		_methods = new LinkedList<Method>();
	}

	@Override
	public IResolutionPhase copyStructure() {
		return new Rating();
	}

	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		clear();
	}

	@Override
	public void clear() {
		_methods.clear();
	}

	@Override
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException {
	}

	@Override
	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer)
			throws WorkspaceContentPersistenceException {
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_methods);
		return hcb.toHashCode();
	}

	@Override
	public IResolutionPhase clone() {
		Rating result = null;

		try {
			result = (Rating) super.clone();
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
	}

	@Override
	public boolean validate() {
		if(_methods.isEmpty()) {
			return false;
		}
		return true;
	}


}
