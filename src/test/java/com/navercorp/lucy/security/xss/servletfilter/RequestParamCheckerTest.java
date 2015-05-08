/*
 * @(#)RequestParamCheckerTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
		assertThat(checker.doFilter("/notExistUrl.nhn", "q", "<b>Text</b>"), is("<b>Text</b>"));             //글로벌에 설정되어 있다면 글로벌 설정대로 되는지 확인
		
		assertThat(checker.doFilter("/disabletest1.nhn", "web", "<b>Text</b>"), is("<b>Text</b>"));   //url disable 설정 있고 글로벌에 필터링 설정이 되어 있지만, url을 disable했으므로 필터링 되면안됨
		assertThat(checker.doFilter("/disabletest1.nhn", "q", "안녕"), is("안녕"));
		assertThat(checker.doFilter("/disabletest1.nhn", "text", "<b>Text</b>"), is("<b>Text</b>"));        //url disable 설정이 true이므로 필터링 되면 안됨 
		assertThat(checker.doFilter("/disabletest1.nhn", "hello", "안녕"), is("안녕"));                        //url disable 설정이 true이므로 필터링 되면 안됨
		
		assertThat(checker.doFilter("/disabletest2.nhn", "text", "<안녕>"), is("&lt;안녕&gt;"));               //url disable 설정이 false이므로 필터링 되어야 함 
		assertThat(checker.doFilter("/disabletest2.nhn", "hello", "<안녕>"), is("&lt;안녕&gt;"));              //url disable 설정이 false이므로 필터링 되어야 함 `
		
		assertThat(checker.doFilter("/disabletest3.nhn", "query", "<안녕>"), is("<안녕>"));                   //url disable 설정이 true이므로 파라메터 설정이 있더라도 필터링 되면 안됨
		assertThat(checker.doFilter("/disabletest3.nhn", "prefix5", "<안녕>"), is("<안녕>"));                   //url disable 설정이 true이므로 파라메터 설정이 있더라도 필터링 되면 안됨
	
		assertThat(checker.doFilter("/disabletest4.nhn", "query", "<안녕>"), is("<안녕>"));                //url disable 설정이 false이므로 url 설정은 무시하고 param 설정대로 필터링 되는지 확인
 		assertThat(checker.doFilter("/disabletest4.nhn", "prefix1", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix1aaa", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix2aaa", "<안녕>"), is("&lt;안녕&gt;"));  //prefix 설정이 안되어 있으므로 param의 필터링 설정을 따르지 않아야한다.
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix2", "<안녕>"), is("<안녕>"));
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix3", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix3-123", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix4", "<안녕>"), is("<안녕>"));
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix4adf123", "<안녕>"), is("<안녕>"));
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix5", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(checker.doFilter("/disabletest4.nhn", "prefix5-zddfadf123", "<안녕>"), is("&lt;안녕&gt;"));
	}
	
	@Test
	public void testPrefixDoFilter() {
		assertThat(checker.doFilter("/search.nhn", "prefix1", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/search.nhn", "prefix1abc", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/search.nhn", "prefix2ksdc", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/search.nhn", "prefix2", "<script>Text</script>"), is("<script>Text</script>"));
		assertThat(checker.doFilter("/search.nhn", "prefix3", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/search.nhn", "prefix3-dsf", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/search.nhn", "prefix4aaaa", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(checker.doFilter("/search.nhn", "prefix4", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(checker.doFilter("/notExist.nhn", "globalprefix1abc", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/notExist.nhn", "globalprefix2", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(checker.doFilter("/notExist.nhn", "globalprefix2abc", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(checker.doFilter("/notExist.nhn", "globalprefix3", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(checker.doFilter("/notExist.nhn", "globalprefix3a1", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(checker.doFilter("/notExist.nhn", "globalprefix3123", "<b>Text</b>"), is("<b>Text</b>"));
	}
}
