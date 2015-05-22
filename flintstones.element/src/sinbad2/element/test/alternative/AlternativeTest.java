package sinbad2.element.test.alternative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import sinbad2.element.alternative.Alternative;

public class AlternativeTest {
	
	private Alternative _tester;
	
	@Before
	public void testAlternativeBefore() {
		_tester = new Alternative("Alternative");
	}
	
	@Test
	public void testClone() {
		Alternative clone = (Alternative) _tester.clone();
		
		assertEquals("the alternative must be cloned", _tester.getId(), clone.getId());
	}

	@Test
	public void testEqualsObject() {
		Alternative other = new Alternative("Other"); 
		
		assertTrue("return true if there are the same object", _tester.equals(_tester));
		assertFalse("return false if there are the same object", _tester.equals(other));
	}

}
