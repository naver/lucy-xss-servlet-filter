/*
 * @(#)XssEscapeFilterConfigTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import com.navercorp.lucy.security.xss.servletfilter.defender.XssFilterDefender;
import com.navercorp.lucy.security.xss.servletfilter.defender.XssPreventerDefender;
import com.navercorp.lucy.security.xss.servletfilter.defender.XssSaxFilterDefender;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * XssEscapeFilterConfig 의 설정 파일 로딩 동작에 대한 테스트
 * 
 * @author tod2
 */
public class XssEscapeFilterConfigTest {
	static XssEscapeFilterConfig config;
	
	@BeforeClass
	public static void init() throws Exception {
		config = new XssEscapeFilterConfig();
	}

	@Test(expected = IllegalStateException.class)
	public void testWhenNotExistingConfigFile() {
		new XssEscapeFilterConfig("unkonwn-file.xml");
	}

	@Test(expected = IllegalStateException.class)
	public void testWhenUnknownDefnderClass() {
		new XssEscapeFilterConfig("lucy-xss-servlet-filter-rule-unknown-class.xml");
	}

	@Test
	public void testWhenEmptyDefnderName() {
		new XssEscapeFilterConfig("lucy-xss-servlet-filter-rule-empty-name.xml");
		// leaves warning message
	}

	@Test
	public void testGetDefaultDefender() {
		assertThat(config.getDefaultDefender(), instanceOf(XssPreventerDefender.class));
	}

	@Test
	public void testGetDefenderMap() {
		assertThat(config.getDefenderMap().size(), is(3));
		assertThat(config.getDefenderMap().get("xssPreventerDefender"), instanceOf(XssPreventerDefender.class));
		assertThat(config.getDefenderMap().get("xssFilterDefender"), instanceOf(XssFilterDefender.class));
		assertThat(config.getDefenderMap().get("xssSaxFilterDefender"), instanceOf(XssSaxFilterDefender.class));
	}
	
	@Test
	public void testGetGlobalUrlParamRule() {
		assertThat(config.getUrlParamRule("/notExistUrl.do", "globalParameter"), instanceOf(XssEscapeFilterRule.class));
		assertThat(config.getUrlParamRule("/notExistUrl.do", "globalParameter").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/notExistUrl.do", "globalParameter").getDefender(), is(config.getDefaultDefender()));
	}
	
	@Test
	public void testGetUrlParamRule() {
		assertThat(config.getUrlParamRule("/url1.do", "title"), is(nullValue()));
		assertThat(config.getUrlParamRule("/url1.do", "url1Parameter"), instanceOf(XssEscapeFilterRule.class));
		assertThat(config.getUrlParamRule("/url1.do", "url1Parameter").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/url1.do", "url1Parameter").getDefender(), is(config.getDefaultDefender()));
		
		assertThat(config.getUrlParamRule("/url2.do", "url2Parameter3"), instanceOf(XssEscapeFilterRule.class));
		assertThat(config.getUrlParamRule("/url2.do", "url2Parameter3").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/url2.do", "url2Parameter3").getDefender(), is(config.getDefenderMap().get("xssPreventerDefender")));

		assertThat(config.getUrlParamRule("/url2.do", "url2Parameter2"), instanceOf(XssEscapeFilterRule.class));
		assertThat(config.getUrlParamRule("/url2.do", "url2Parameter2").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/url2.do", "url2Parameter2").getDefender(), is(config.getDefenderMap().get("xssSaxFilterDefender")));
	}
	
	@Test
	public void testUrlDisableAndPrefix() {
		assertThat(config.getUrlParamRule("/disableUrl1.do", null).isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disableUrl2.do", null), is(nullValue()));
		assertThat(config.getUrlParamRule("/disableUrl3.do", "query").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "query").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix1").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix1aaaa").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix2aaaa"), is(nullValue()));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix2").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix3").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix3aaaa").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix4").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix4aaaa").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix5aaaa"),  is(nullValue()));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix5").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disableUrl4.do", "prefix6aaaa").getDefender(), instanceOf(XssSaxFilterDefender.class));
	}
}
