/*
 * @(#)RequestParamConfigTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestparam;

import org.junit.*;

import com.naver.service.filter.requestparam.defender.*;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.hamcrest.CoreMatchers.nullValue;

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
}
