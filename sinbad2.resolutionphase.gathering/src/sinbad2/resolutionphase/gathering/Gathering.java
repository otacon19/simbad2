
package sinbad2.resolutionphase.gathering;

import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.workspace.WorkspaceContentPersistenceException;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.framework.Framework;
import sinbad2.resolutionphase.frameworkstructuring.FrameworkStructuring;
import sinbad2.resolutionphase.io.XMLRead;
import sinbad2.resolutionphase.io.XMLWriter;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;
import sinbad2.valuation.valuationset.ValuationSet;
import sinbad2.valuation.valuationset.ValuationSetManager;

public class Gathering implements IResolutionPhase {

	public static final String ID = "flintstones.resolutionphase.gathering"; //$NON-NLS-1$
	
	private ValuationSetManager _valuationSetManager;
	private ValuationSet _valuationSet;
	
	public Gathering() {
		_valuationSetManager = ValuationSetManager.getInstance();
		_valuationSet = new ValuationSet();
	}
	
	public ValuationSet getValuationSet() {
		return _valuationSet;
	}
	
	@Override
	public IResolutionPhase copyStructure() {
		return new Gathering(); 
	}
	
	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		Gathering gathering = (Gathering) iResolutionPhase;
		
		clear();
		_valuationSet.setValuations(gathering.getValuationSet().getValuations());
		
	}
	
	@Override
	public void clear() {
		_valuationSet.clear();
		
	}
	
	@Override
	public void save(XMLWriter writer) throws WorkspaceContentPersistenceException {
		XMLStreamWriter streamWriter = writer.getStreamWriter();
		
		try {
			_valuationSet.save(streamWriter);
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}
		
	}

	@Override
	public void read(XMLRead reader, Map<String, IResolutionPhase> buffer) throws WorkspaceContentPersistenceException {
		
		try {
			_valuationSet.read(reader, (Framework) buffer.get(Framework.ID), (FrameworkStructuring) buffer.get(FrameworkStructuring.ID));
		} catch (XMLStreamException e) {
			throw new WorkspaceContentPersistenceException();
		}
		
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_valuationSet);
		
		return hcb.toHashCode();
	}
	
	@Override
	public IResolutionPhase clone() {
		Gathering result = null;
		
		try {
			result = (Gathering) super.clone();
			result._valuationSet = (ValuationSet) _valuationSet.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public void activate() {
		_valuationSetManager.setActiveValuationSet(_valuationSet);
	}
	
	@Override
	public boolean validate() {
		return !_valuationSet.getValuations().isEmpty();
	}
	
	@Override
	public void notifyResolutionPhaseStateChange(ResolutionPhaseStateChangeEvent event) {

		if(event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			activate();
		}
		
	}

}
