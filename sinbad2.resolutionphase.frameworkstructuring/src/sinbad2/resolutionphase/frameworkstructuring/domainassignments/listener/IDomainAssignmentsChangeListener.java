package sinbad2.resolutionphase.frameworkstructuring.domainassignments.listener;

import sinbad2.resolutionphase.frameworkstructuring.domainassignments.DomainAssignments;

public interface IDomainAssignmentsChangeListener {
	
	public void notifyNewActiveDomainAssignments(DomainAssignments domainAssignments);
	
	public void notifyDomainAssignmentsChange(DomainAssignmentsChangeEvent event);
	
}
