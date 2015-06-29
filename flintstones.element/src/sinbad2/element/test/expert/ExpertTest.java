package sinbad2.element.test.expert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sinbad2.element.expert.Expert;

public class ExpertTest {
	
	private Expert _tester;
	private Expert _parent;

	@Before 
	public void testExpertBefore() {
		List<Expert> childrens = new LinkedList<Expert>();
		
		_tester = new Expert("Son"); //$NON-NLS-1$
		_parent = new Expert("Parent"); //$NON-NLS-1$
		
		_tester.setChildrens(childrens);
	}
	
	@Test 
	public void testAddChildren() {
		_parent.addChildren(_tester);
		
		assertThat(_parent.getChildrens().contains(_tester), is(true));
		assertEquals("Parents must be introduced",_tester.getParent(), _parent); //$NON-NLS-1$
	}
	
	@Test
	public void testGetFormatId() {
		_parent.addChildren(_tester);
			
		assertEquals("must return parent_id:expert_id", _tester.getParent().getId() + ">" + _tester.getId(), _tester.getPathId()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testClone() {
		Expert clone = (Expert) _tester.clone();
		
		assertThat(_tester.getParent() == clone.getParent(), is(true));
		assertThat(_tester.getChildrens() == clone.getChildrens(), is(true));
		
	}

	@Test
	public void testHasChildrens() {
		Expert children = new Expert("Son"); //$NON-NLS-1$

		assertEquals("return false if there aren't children", false, _tester.hasChildrens()); //$NON-NLS-1$
		
		_tester.addChildren(children);

		assertEquals("return true if there are children", true, _tester.hasChildrens()); //$NON-NLS-1$
	}

	@Test
	public void testEqualsObject() {
		
		assertTrue("return true if there are the same object", _tester.equals(_tester)); //$NON-NLS-1$
		assertFalse("return false if there are the same object", _tester.equals(_parent)); //$NON-NLS-1$
	}

	@Test
	public void testGetExpertByFormatId() {
		List<Expert> experts = new LinkedList<Expert>();
		
		_parent.addChildren(_tester);
		experts.add(_tester);
		experts.add(_parent);
		
		assertEquals("must be the correct expert", _tester, Expert.getExpertByFormatId( //$NON-NLS-1$
				experts, _parent.getId() + ">" + _tester.getId())); //$NON-NLS-1$
		
	}

}
