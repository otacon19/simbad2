package sinbad2.resolutionphase.frameworkstructuring.domainassignments;

import java.util.List;
import java.util.Map;

import sinbad2.domain.Domain;
import sinbad2.domain.DomainSet;
import sinbad2.domain.DomainsManager;
import sinbad2.domain.listener.DomainSetChangeEvent;
import sinbad2.domain.listener.IDomainSetChangeListener;
import sinbad2.domain.listener.IDomainSetListener;
import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.expert.listener.ExpertsChangeEvent;
import sinbad2.element.expert.listener.IExpertsChangeListener;
import sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener.IDomainAssignmentsChangeListener;

public class DomainAssignments implements Cloneable, IExpertsChangeListener, IAlternativesChangeListener, ICriteriaChangeListener, 
	IProblemElementsSetChangeListener, IDomainSetChangeListener, IDomainSetListener {
	
	private ProblemElementsManager _elementsManager;
	private ProblemElementsSet _elementSet;
	private DomainsManager _domainsManager;
	private DomainSet _domainSet;
	private Map<IDomainAssignmentsChangeListener, Domain> _assignments;
	private List<IDomainAssignmentsChangeListener> _listeners;
	

	@Override
	public void notifyDomainSetListener(DomainSetChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyDomainSetChangeListener(DomainSet domainSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyProblemElementsSetChange(ProblemElementsSet elementSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyExpertsChange(ExpertsChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
