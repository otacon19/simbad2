package sinbad2.element.test.criterion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sinbad2.element.criterion.Criterion;

public class CriterionTest {
	
	private Criterion _tester;
	private Criterion _criterion;

	@Before
	public void testCriterionBefore() {
		_tester = new Criterion("Subcriterion"); //$NON-NLS-1$
		_criterion = new Criterion("Criterion"); //$NON-NLS-1$
	}
	
	@Test
	public void testAddSubcriterion() {
		_criterion.addSubcriterion(_tester);
		
		assertThat(_criterion.getSubcriteria().contains(_tester), is(true));
		assertEquals("Parents must be introduced",_tester.getParent(), _criterion); //$NON-NLS-1$
	}
	
	@Test
	public void testGetFormatId() {
		_criterion.addSubcriterion(_tester);
		
		assertEquals("must return parent_id:criterion_id", _tester.getParent().getId() + ">" + _tester.getId(), _tester.getPathId()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testClone() {
		Criterion clone = (Criterion) _tester.clone();
		
		assertThat(_tester.getParent() == clone.getParent(), is(true));
		assertThat(_tester.getCost() == clone.getCost(), is(true));
		assertThat(_tester.getSubcriteria() == clone.getSubcriteria(), is(true));
	}

	@Test
	public void testRemoveSubcriterion() {
		_criterion.addSubcriterion(_tester);
		_criterion.removeSubcriterion(_tester);
		
		assertThat(_tester.getParent() != _criterion, is(true));
		assertThat(!_criterion.getSubcriteria().contains(_tester), is(true));
	}

	@Test
	public void testHasSubcriterial() {
		Criterion children = new Criterion("Son"); //$NON-NLS-1$

		assertEquals("return false if there aren't children", false, _tester.hasSubcriteria()); //$NON-NLS-1$
		
		_tester.addSubcriterion(children);

		assertEquals("return true if there are children", true, _tester.hasSubcriteria()); //$NON-NLS-1$
	}

	@Test
	public void testEqualsObject() {
		assertTrue("return true if there are the same object", _tester.equals(_tester)); //$NON-NLS-1$
		assertFalse("return false if there are the same object", _tester.equals(_criterion)); //$NON-NLS-1$
	}

	@Test
	public void testGetCriterionByFormatId() {
		List<Criterion> criteria = new LinkedList<Criterion>();
		
		_criterion.addSubcriterion(_tester);
		criteria.add(_tester);
		criteria.add(_criterion);
		
		assertEquals("must be the correct criterion", _tester, Criterion.getCriterionByFormatId( //$NON-NLS-1$
				criteria, _criterion.getId() + ">" + _tester.getId())); //$NON-NLS-1$
	}

}
