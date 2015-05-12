package flintstones.resolutionscheme;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import flintstones.resolutionphase.IResolutionPhase;
import flintstones.resolutionphase.ResolutionPhase;
import flintstones.resolutionscheme.state.IResolutionSchemeStateListener;
import flintstones.workspace.IWorkspaceContent;

abstract public class ResolutionSchemeImplementation implements IResolutionSchemeStateListener, IWorkspaceContent {
	
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

	public void setPhasesImplementation(
			Map<String, IResolutionPhase> phasesImplementation) {
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
		for(ResolutionPhase phase: _resolutionScheme.getPhases()) {
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
		
		for(String name: _phasesNames) {
			names.add(name);
			phasesImplementation.put(name, _phasesImplementation.get(name).copyStructure());
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
		for(String name: _phasesNames) {
			_phasesImplementation.get(name).copyData(rsi.getPhasesImplementation().get(name));
		}
	}
	
	@Override
	public void clear() {
		for(String name: _phasesNames) {
			_phasesImplementation.get(name).clear();
		}
	}
	
	@Override
	public void activate() {
		for(String name: _phasesNames) {
			_phasesImplementation.get(name).activate();
		}
	}
	
	//TODO hashCode
	
	abstract public ResolutionSchemeImplementation newInstance();
	


}
