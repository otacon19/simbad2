package sinbad2.method;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.workspace.IWorkspaceContent;
import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.method.state.IMethodStateListener;
import sinbad2.phasemethod.IPhaseMethod;
import sinbad2.phasemethod.PhaseMethod;

public abstract class MethodImplementation implements IMethodStateListener, IWorkspaceContent {
	
	protected List<String> _phasesNames;
	protected Map<String, PhaseMethod> _phases;
	protected Map<String, IPhaseMethod> _phasesImplementation;
	protected Method _method;

	public List<String> getPhasesNames() {
		return _phasesNames;
	}

	public void setPhasesNames(List<String> phasesNames) {
		_phasesNames = phasesNames;
	}

	public Map<String, PhaseMethod> getPhases() {
		return _phases;
	}

	public void setPhases(Map<String, PhaseMethod> phases) {
		_phases = phases;
	}

	public Map<String, IPhaseMethod> getPhasesImplementation() {
		return _phasesImplementation;
	}

	public void setPhasesImplementation(Map<String, IPhaseMethod> phasesImplementation) {
		_phasesImplementation = phasesImplementation;
	}

	public Method getMethod() {
		return _method;
	}

	public void setMethod(Method method) {

		_method = method;

		_phasesNames = new LinkedList<String>();
		_phasesImplementation = new HashMap<String, IPhaseMethod>();
		_phases = new HashMap<String, PhaseMethod>();

		System.out.println(method.getName());
		
		String name;
		for (PhaseMethod phase : _method.getPhases()) {
			name = phase.getId();
			_phasesNames.add(name);
			_phases.put(name, phase);
			
			_phasesImplementation.put(name, phase.getImplementation());
			System.out.println(phase.getId());
			System.out.println(phase.getImplementation());
		}
	}

	@Override
	public Object getElement(String id) {
		return _phasesImplementation.get(id);
	}

	@Override
	public IWorkspaceContent copyStructure() {
		MethodImplementation result = this.newInstance();
		List<String> names = new LinkedList<String>();
		Map<String, IPhaseMethod> phasesImplementation = new HashMap<String, IPhaseMethod>();

		for (String name : _phasesNames) {
			names.add(name);
			phasesImplementation.put(name, (IPhaseMethod)_phasesImplementation.get(name).copyStructure());
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
		MethodImplementation rsi = (MethodImplementation) content;
		for (String name : _phasesNames) {
			_phasesImplementation.get(name).copyData(rsi.getPhasesImplementation().get(name));
		}
	}
	
	@Override
	public IWorkspaceContent read(String fileName) throws IOException, WorkspaceContentPersistenceException {
		return null;
	}
	
	@Override
	public void save(String fileName) throws IOException, WorkspaceContentPersistenceException {}
	
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
	
	public abstract MethodImplementation newInstance();
	
	public abstract String isAvailable();

}
