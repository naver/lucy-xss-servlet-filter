/*
 * @(#)RequestParamConfigTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import com.navercorp.lucy.security.xss.servletfilter.defender.XssFilterDefender;
import com.navercorp.lucy.security.xss.servletfilter.defender.XssPreventerDefender;
import com.navercorp.lucy.security.xss.servletfilter.defender.XssSaxFilterDefender;

/**
 * RequestParamConfig 의 설정 파일 로딩 동작에 대한 테스트
 * 
 * @author tod2
 */
public class RequestParamConfigTest {
	static RequestParamConfig config;
	
	@BeforeClass
	public static void init() throws Exception {
		config = new RequestParamConfig();
	}
	
	@Test
	public void testGetDefaultDefender() {
		assertThat(config.getDefaultDefender(), instanceOf(XssPreventerDefender.class));
	}

	@Test
	public void testGetDefenderMap() {
		assertThat(config.getDefenderMap().size(), is(3));
		assertThat(config.getDefenderMap().get("preventer"), instanceOf(XssPreventerDefender.class));
		assertThat(config.getDefenderMap().get("xss"), instanceOf(XssFilterDefender.class));
		assertThat(config.getDefenderMap().get("xss_sax"), instanceOf(XssSaxFilterDefender.class));
	}
	
	@Test
	public void testGetGlobalUrlParamRule() {
		assertThat(config.getUrlParamRule("/notExistUrl.nhn", "q"), instanceOf(RequestParamParamRule.class));
		assertThat(config.getUrlParamRule("/notExistUrl.nhn", "q").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/notExistUrl.nhn", "q").getDefender(), is(config.getDefaultDefender()));
	}
	
	@Test
	public void testGetUrlParamRule() {
		assertThat(config.getUrlParamRule("/search.nhn", "title"), is(nullValue()));
		assertThat(config.getUrlParamRule("/search.nhn", "query"), instanceOf(RequestParamParamRule.class));
		assertThat(config.getUrlParamRule("/search.nhn", "query").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/search.nhn", "query").getDefender(), is(config.getDefaultDefender()));
		
		assertThat(config.getUrlParamRule("/tlist/list.nhn", "mode"), instanceOf(RequestParamParamRule.class));
		assertThat(config.getUrlParamRule("/tlist/list.nhn", "mode").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/tlist/list.nhn", "mode").getDefender(), is(config.getDefenderMap().get("xss")));

		assertThat(config.getUrlParamRule("/tlist/list.nhn", "q"), instanceOf(RequestParamParamRule.class));
		assertThat(config.getUrlParamRule("/tlist/list.nhn", "q").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/tlist/list.nhn", "q").getDefender(), is(config.getDefenderMap().get("xss_sax")));
	}
	
	@Test
	public void testUrlDisable() {
		assertThat(config.getUrlParamRule("/disabletest1.nhn", null).isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disabletest2.nhn", null), is(nullValue()));
		assertThat(config.getUrlParamRule("/disabletest3.nhn", "query").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "query").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix1").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix1aaaa").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix2aaaa"), is(nullValue()));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix2").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix3").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix3aaaa").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix4").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix4aaaa").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix5aaaa"),  is(nullValue()));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix5").isUseDefender(), is(true));
		
	}
	
	@Test
	public void testUrlPrefix() {
		assertThat(config.getUrlParamRule("/search.nhn", "query").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/search.nhn", "prefix1").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/search.nhn", "prefix1aaaa").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/search.nhn", "prefix2aaaa"), is(nullValue()));
		assertThat(config.getUrlParamRule("/search.nhn", "prefix2").isUseDefender(), is(false));
		assertThat(config.getUrlParamRule("/search.nhn", "prefix3").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/disabletest4.nhn", "prefix3aaaa").isUseDefender(), is(true));
		assertThat(config.getUrlParamRule("/search.nhn", "prefix4aaaa").getDefender(), instanceOf(XssSaxFilterDefender.class));
	}
}
