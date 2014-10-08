/*
 * @(#)XssFilterDefenderTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestparam.defender;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import org.junit.*;

/**
 * XssFilterDefender 에 대한 테스트
 * 
 * @author tod2
 */
public class XssFilterDefenderTest {
	XssFilterDefender defender = new XssFilterDefender();

	@Test
	public void testWorkingDoFilter() {
		defender.init(null);
		assertThat(defender.doFilter("<script>Text</script>"), is("<!-- Not Allowed Tag Filtered -->&lt;script&gt;Text&lt;/script&gt;"));
		
		defender.init(new String[] {"true"});
		assertThat(defender.doFilter("<script>Text</script>"), is("&lt;script&gt;Text&lt;/script&gt;"));
		
		defender.init(new String[] {"lucy-xss-superset.xml"});
		assertThat(defender.doFilter("<script>Text</script>"), is("<!-- Not Allowed Tag Filtered -->&lt;script&gt;Text&lt;/script&gt;"));
		
		defender.init(new String[] {"lucy-xss-superset.xml", "true"});
		assertThat(defender.doFilter("<script>Text</script>"), is("&lt;script&gt;Text&lt;/script&gt;"));
	}

	@Test(expected=NullPointerException.class)
	public void testNotWorkingDoFilter() {
		defender.init(new String[] {"lucy-xss-superset.xml", "true", "false"});
		assertThat(defender.doFilter("<script>Text</script>"), is("&lt;script&gt;Text&lt;/script&gt;"));
	}
}