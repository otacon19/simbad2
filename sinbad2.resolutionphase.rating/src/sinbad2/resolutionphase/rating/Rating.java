package sinbad2.resolutionphase.rating;

import java.util.Map;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class Rating implements IResolutionPhase {

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IResolutionPhase clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResolutionPhase copyStructure() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer) throws WorkspaceContentPersistenceException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {
		
		if(event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			activate();
		}
		
	}

}
