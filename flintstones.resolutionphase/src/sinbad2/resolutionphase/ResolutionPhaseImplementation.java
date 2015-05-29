
package sinbad2.resolutionphase;

import sinbad2.core.workspace.IWorkspaceContent;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.resolutionphase.state.IResolutionPhaseStateListener;

public abstract class ResolutionPhaseImplementation implements IResolutionPhaseStateListener, IWorkspaceContent {
	
	protected ResolutionPhase _resolutionPhase;
	
	protected ProblemElementsSet _elementSet;
	protected DomainSet _domainSet;
	
	protected ProblemElementsManager _elementsManager;
	protected DomainsManager _domainsManager;
	
	public ResolutionPhaseImplementation() {
		_elementsManager = ProblemElementsManager.getInstance();
		_domainsManager = DomainsManager.getInstance();
		_elementSet = new ProblemElementsSet();
		_domainSet = new DomainSet();
	}
	
	public ResolutionPhase getResolutionPhase() {
		return _resolutionPhase;
	}
	
	public void setResolutionPhase(ResolutionPhase resolutionPhase) {
		_resolutionPhase = resolutionPhase;
	}
	
	public ProblemElementsSet getProblemElementsSet() {
		return _elementSet;
	}
	
	public void setElementSet(ProblemElementsSet elementSet) {
		_elementSet = elementSet;
	}
	
	public DomainSet getDomainSet() {
		return _domainSet;
	}
	
	public void setDomainSet(DomainSet domainSet) {
		_domainSet = domainSet;
	}
	
	@Override
	public Object getElement(String id) {
		return null;
	}
	
	@Override
	public IWorkspaceContent copyStructure() {
		ResolutionPhaseImplementation result = this.newInstance();
		
		result.setResolutionPhase(_resolutionPhase);
		result.setElementSet(_elementSet);
		result.setDomainSet(_domainSet);

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
		clear();
		ResolutionPhaseImplementation rpi = (ResolutionPhaseImplementation) content;
		_elementSet.setExperts(rpi.getProblemElementsSet().getExperts());
		_elementSet.setAlternatives(rpi.getProblemElementsSet().getAlternatives());
		_elementSet.setCriteria(rpi.getProblemElementsSet().getCriteria());
		
		_domainSet.setDomains(rpi.getDomainSet().getDomains());
	}

	@Override
	public void clear() {
		_elementSet.clear();
		_domainSet.clear();
	}

	@Override
	public void activate() {
		_elementsManager.setActiveElementSet(_elementSet);
		_domainsManager.setActiveDomainSet(_domainSet);
	}
	
	public boolean validate() {
		
		if(_domainSet.getDomains().isEmpty() || _elementSet.getAlternatives().isEmpty() 
				|| _elementSet.getExperts().isEmpty() || _elementSet.getCriteria().isEmpty()) {
			return false;
		}
		
		return true;
		
	}
	
	public abstract ResolutionPhaseImplementation newInstance();

}
