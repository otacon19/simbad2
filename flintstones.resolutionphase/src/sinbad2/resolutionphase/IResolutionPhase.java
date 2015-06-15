package sinbad2.resolutionphase;


import java.util.Map;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.state.IResolutionPhaseStateListener;

public interface IResolutionPhase extends IResolutionPhaseStateListener {
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException;

	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer)
			throws WorkspaceContentPersistenceException;

	public void clear();

	public IResolutionPhase clone();

	public IResolutionPhase copyStructure();

	public void copyData(IResolutionPhase iResolutionPhase);

	public void activate();

	public boolean validate();
}

