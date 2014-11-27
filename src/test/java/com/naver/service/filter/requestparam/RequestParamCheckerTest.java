/*
 * @(#)RequestParamCheckerTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestparam;

import static org.junit.Assert.*;

import org.junit.*;

import static org.hamcrest.core.Is.*;

/**
 * RequestParamChecker 에 대한 통합 테스트
 * 
 * @author tod2
 */
public class RequestParamCheckerTest {
	RequestParamChecker checker = RequestParamChecker.getInstance();
	
	@Test
	public void testDoFilter() {
		assertThat(checker.doFilter("/notExistUrl.nhn", "title", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/notExistUrl.nhn", "q", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(checker.doFilter("/notExistUrl.nhn", "test", "<script>alert('1');</script>"), is("&lt;script&gt;alert(&#39;1&#39;);&lt;/script&gt;"));
		
		assertThat(checker.doFilter("/tlist/list.nhn", "listId", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(checker.doFilter("/tlist/list.nhn", "mode", "<script>Text</script>"), is("&lt;script&gt;Text&lt;/script&gt;"));
		assertThat(checker.doFilter("/tlist/list.nhn", "q", "<script>Text</script>"), is("&lt;script&gt;Text&lt;/script&gt;"));
		
	}
	
	@Test
	public void testUnicodeDoFilter() {
		assertThat(checker.doFilter("/notExistUrl.nhn", "title", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/notExistUrl.nhn", "title", "안녕"), is("안녕"));
	}
	
	@Test
	public void testDisableUrlDoFilter() {
		assertThat(checker.doFilter("/search.nhn", "title", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));  //기존 거에 영향을 미치는지 확인
		assertThat(checker.doFilter("/search.nhn", "query", "<b>Text</b>"), is("<b>Text</b>"));              //기존 거에 영향을 미치는지 확인
		assertThat(checker.doFilter("/notExistUrl.nhn", "q", "<b>Text</b>"), is("<b>Text</b>"));             //글로벌에 설정되어 있다면 무조건 필터링 확인
		assertThat(checker.doFilter("/disabletest1.nhn", "web", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));   //url disable 설정 이지만 글로벌에 필터링 설정이 되어 있음, 필터링 되어야 함
		assertThat(checker.doFilter("/disabletest1.nhn", "q", "안녕"), is("안녕"));
		assertThat(checker.doFilter("/disabletest1.nhn", "text", "<b>Text</b>"), is("<b>Text</b>"));        //url disable 설정이므로 필터링 되면 안됨 
		assertThat(checker.doFilter("/disabletest1.nhn", "hello", "안녕"), is("안녕"));                        //url disable 설정이므로 필터링 되면 안됨
		assertThat(checker.doFilter("/disabletest2.nhn", "hello", "<안녕>"), is("&lt;안녕&gt;"));
	}
}
