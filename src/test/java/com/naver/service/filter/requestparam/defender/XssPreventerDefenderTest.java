/*
 * @(#)XssPreventerDefenderTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestparam.defender;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import org.junit.*;

/**
 * XssPreventerDefender 에 대한 테스트
 * 
 * @author tod2
 */
public class XssPreventerDefenderTest {
	XssPreventerDefender defender = new XssPreventerDefender();

	@Test
	public void testInit() {
		defender.init(null);
		assertThat(defender.doFilter("<Text"), is("&lt;Text"));
		assertThat(defender.doFilter("<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
	}
}
