package sinbad2.resolutionphase.sensitivityanalysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class SensitivityAnalysis implements IResolutionPhase {
	
	public static final String ID = "flintstones.resolutionphase.sensitivityanalysis";
	
	private MockModel _model = null;
	public List<ISensitivityAnalysisChangeListener> _listeners;
	
	public SensitivityAnalysis() {
		_listeners = new LinkedList<ISensitivityAnalysisChangeListener>();
	}
	
	@Override
	public IResolutionPhase copyStructure() {
		return new SensitivityAnalysis();
	}
	
	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		@SuppressWarnings("unused")
		SensitivityAnalysis sa = (SensitivityAnalysis) iResolutionPhase;
		
		clear();
	}

	@Override
	public void clear() {}
	
	@Override
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException {
		@SuppressWarnings("unused")
		XMLStreamWriter streamWriter = writer.getStreamWriter();
	}

	@Override
	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer) throws WorkspaceContentPersistenceException {}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		
		return hcb.toHashCode();
	}
	
	@Override
	public IResolutionPhase clone() {
		SensitivityAnalysis result = null;
		
		try {
			result = (SensitivityAnalysis) super.clone();
		} catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public boolean validate() {
		return false;
	}
	
	public void setModel(MockModel model) {
		_model = model;
		notifySensitivityAnalysisChange();
	}
	
	public MockModel getModel() {
		
		if(_model == null) {
			_model = new MockModel();
		}
		
		return _model;
	}
	
	@Override
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {
		
		if(event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			activate();
		}
		
	}

	@Override
	public void activate() {}
	
	public void registerSensitivityAnalysisChangeListener(ISensitivityAnalysisChangeListener listener) {
		_listeners.add(listener);
	}
	
	public void unregisterSensitivityAnalysisChangeListener(ISensitivityAnalysisChangeListener listener) {
		_listeners.remove(listener);
	}
	
	public void notifySensitivityAnalysisChange() {
		
		for(ISensitivityAnalysisChangeListener listener: _listeners) {
			listener.notifySensitivityAnalysisChange();
		}
	}
	
}
