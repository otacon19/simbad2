package sinbad2.resolutionscheme;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.workspace.IWorkspaceContent;
import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.ResolutionPhase;
import sinbad2.resolutionscheme.io.XMLWriter;
import sinbad2.resolutionscheme.state.IResolutionSchemeStateListener;

public abstract class ResolutionSchemeImplementation implements IResolutionSchemeStateListener, IWorkspaceContent {

	protected List<String> _phasesNames;
	protected Map<String, ResolutionPhase> _phases;
	protected Map<String, IResolutionPhase> _phasesImplementation;
	protected ResolutionScheme _resolutionScheme;

	public List<String> getPhasesNames() {
		return _phasesNames;
	}

	public void setPhasesNames(List<String> phasesNames) {
		_phasesNames = phasesNames;
	}

	public Map<String, ResolutionPhase> getPhases() {
		return _phases;
	}

	public void setPhases(Map<String, ResolutionPhase> phases) {
		_phases = phases;
	}

	public Map<String, IResolutionPhase> getPhasesImplementation() {
		return _phasesImplementation;
	}

	public void setPhasesImplementation(Map<String, IResolutionPhase> phasesImplementation) {
		_phasesImplementation = phasesImplementation;
	}

	public ResolutionScheme getResolutionScheme() {
		return _resolutionScheme;
	}

	public void setResolutionScheme(ResolutionScheme resolutionScheme) {

		_resolutionScheme = resolutionScheme;

		_phasesNames = new LinkedList<String>();
		_phasesImplementation = new HashMap<String, IResolutionPhase>();
		_phases = new HashMap<String, ResolutionPhase>();

		String name;
		for (ResolutionPhase phase : _resolutionScheme.getPhases()) {
			name = phase.getId();
			_phasesNames.add(name);
			_phases.put(name, phase);
			_phasesImplementation.put(name, phase.getImplementation());
		}
	}

	@Override
	public Object getElement(String id) {
		return _phasesImplementation.get(id);
	}

	@Override
	public IWorkspaceContent copyStructure() {
		ResolutionSchemeImplementation result = this.newInstance();
		List<String> names = new LinkedList<String>();
		Map<String, IResolutionPhase> phasesImplementation = new HashMap<String, IResolutionPhase>();

		for (String name : _phasesNames) {
			names.add(name);
			phasesImplementation.put(name, (IResolutionPhase)_phasesImplementation.get(name).copyStructure());
		}

		result.setPhasesNames(_phasesNames);
		result.setPhasesImplementation(phasesImplementation);

		return result;
	}

	@Override
	public IWorkspaceContent copyData() {
		IWorkspaceContent result = copyStructure();
		result.copyData(this);

		return result;
	}

	@Override
	public void copyData(IWorkspaceContent content) {
		ResolutionSchemeImplementation rsi = (ResolutionSchemeImplementation) content;
		for (String name : _phasesNames) {
			_phasesImplementation.get(name).copyData(rsi.getPhasesImplementation().get(name));
		}
	}
	
	@Override
	public IWorkspaceContent read(String fileName) throws IOException,
			WorkspaceContentPersistenceException {
		ResolutionSchemeImplementation result = null;
		sinbad2.resolutionscheme.io.XMLRead xmlRead = 
				new sinbad2.resolutionscheme.io.XMLRead(fileName);
		result = xmlRead.read();
		return result;
	}
	
	@Override
	public void save(String fileName) throws IOException,
			WorkspaceContentPersistenceException {
		new XMLWriter(_resolutionScheme, fileName).save();
	}
	
	@Override
	public void clear() {
		for (String name : _phasesNames) {
			_phasesImplementation.get(name).clear();
		}
	}

	@Override
	public void activate() {
		
		for (String name : _phasesNames) {
			_phasesImplementation.get(name).activate();
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		for(String id: _phasesNames) {
			hcb.append(_phasesImplementation.get(id));
		}
		return hcb.toHashCode();
	}
	
	public abstract ResolutionSchemeImplementation newInstance();

}
