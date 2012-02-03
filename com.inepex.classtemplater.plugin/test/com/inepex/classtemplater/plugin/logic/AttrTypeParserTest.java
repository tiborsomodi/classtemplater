package com.inepex.classtemplater.plugin.logic;

import org.junit.Assert;
import org.junit.Test;

public class AttrTypeParserTest {

	AttrTypeParser parser = new AttrTypeParser("", ";", "<", ">");
	
	@Test
	public void hierarchicalSplitTest(){
		
		String[] res1 = parser.hierarchicalSplit("egy;ketto");
		String[] expexted1 = {"egy", "ketto"};
		
		String[] res2 = parser.hierarchicalSplit("egy;<ketto;harom>;negy");
		String[] expexted2 = {"egy", "<ketto;harom>", "negy"};
		
		String[] res3 = parser.hierarchicalSplit("egy<ketto>;harom<negy>");
		String[] expexted3 = {"egy<ketto>", "harom<negy>"};
	
		Assert.assertArrayEquals(expexted1, res1);
		Assert.assertArrayEquals(expexted2, res2);
		Assert.assertArrayEquals(expexted3, res3);
		
	}
	
	@Test
	public void getBaseTest(){
		String res1 = parser.getBase("egy<ketto>");
		String expected1 = "egy";
		
		String res2 = parser.getBase("<ketto>");
		String expected2 = "";
		
		Assert.assertEquals(expected1, res1);
		Assert.assertEquals(expected2, res2);
	}
	
	@Test
	public void getNextLevelTest(){
		String res1 = parser.nextLevel("egy<ketto>");
		String expected1 = "ketto";
		
		boolean exception = false;
		try {
			String res2 = parser.nextLevel("egy;<ketto>");
		} catch (Exception e) {
			exception = true;
		}
		
		Assert.assertEquals(expected1, res1);
		Assert.assertEquals(true, exception);
	}
	
	@Test
	public void calcFirstLevelItemsTest(){
		
	}
	
	@Test
	public void calcItems(){
		AttrTypeParser parser = new AttrTypeParser("egy<ketto>;harom<negy<ot>>", ";", "<", ">");
		String[] res = parser.getFirstLevelItemsInOrder().toArray(new String[0]);
		String[] expected = {"egy", "ketto", "harom", "negy", "ot"};
		Assert.assertArrayEquals(expected, res);
		
		AttrTypeParser parser2 = new AttrTypeParser("egy<ketto;kettofel>;harom<negy<ot>>", ";", "<", ">");
		String[] res2 = parser2.getFirstLevelItemsInOrder().toArray(new String[0]);
		String[] expected2 = {"egy", "ketto", "kettofel", "harom", "negy", "ot"};
		Assert.assertArrayEquals(expected2, res2);
		
	}
}
