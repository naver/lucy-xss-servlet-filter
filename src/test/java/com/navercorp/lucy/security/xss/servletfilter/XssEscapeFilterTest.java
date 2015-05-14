/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author todtod80
 * @author leeplay
 */
public class XssEscapeFilterTest {
	XssEscapeFilter filter = XssEscapeFilter.getInstance();
	
	@Test
	public void testDoFilter() {
		assertThat(filter.doFilter("/notExistUrl.do", "title", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(filter.doFilter("/notExistUrl.do", "globalParameter", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(filter.doFilter("/notExistUrl.do", "test", "<script>alert('1');</script>"), is("&lt;script&gt;alert(&#39;1&#39;);&lt;/script&gt;"));
		
		assertThat(filter.doFilter("/url1.do", "url1Parameter", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(filter.doFilter("/url1.do", "globalParameter", "<script>Text</script>"), is("&lt;script&gt;Text&lt;/script&gt;"));
		
	}
	
	@Test
	public void testUnicodeDoFilter() {
		assertThat(filter.doFilter("/notExistUrl.do", "title", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(filter.doFilter("/notExistUrl.do", "title", "안녕"), is("안녕"));
	}
	
	@Test
	public void testDisableUrlDoFilter() {
		assertThat(filter.doFilter("/disableUrl4.do", "title", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));  //기존 거에 영향을 미치는지 확인
		assertThat(filter.doFilter("/disableUrl4.do", "query", "<b>Text</b>"), is("<b>Text</b>"));              //기존 거에 영향을 미치는지 확인
		assertThat(filter.doFilter("/notExistUrl.do", "globalParameter", "<b>Text</b>"), is("<b>Text</b>"));             //글로벌에 설정되어 있다면 글로벌 설정대로 되는지 확인
		
		assertThat(filter.doFilter("/disableUrl1.do", "web", "<b>Text</b>"), is("<b>Text</b>"));   //url disable 설정 있고 글로벌에 필터링 설정이 되어 있지만, url을 disable했으므로 필터링 되면안됨
		assertThat(filter.doFilter("/disableUrl1.do", "q", "안녕"), is("안녕"));
		assertThat(filter.doFilter("/disableUrl1.do", "text", "<b>Text</b>"), is("<b>Text</b>"));        //url disable 설정이 true이므로 필터링 되면 안됨
		assertThat(filter.doFilter("/disableUrl1.do", "hello", "안녕"), is("안녕"));                        //url disable 설정이 true이므로 필터링 되면 안됨
		
		assertThat(filter.doFilter("/disableUrl2.do", "text", "<안녕>"), is("&lt;안녕&gt;"));               //url disable 설정이 false이므로 필터링 되어야 함
		assertThat(filter.doFilter("/disableUrl2.do", "hello", "<안녕>"), is("&lt;안녕&gt;"));              //url disable 설정이 false이므로 필터링 되어야 함 `
		
		assertThat(filter.doFilter("/disableUrl3.do", "query", "<안녕>"), is("<안녕>"));                   //url disable 설정이 true이므로 파라메터 설정이 있더라도 필터링 되면 안됨
		assertThat(filter.doFilter("/disableUrl3.do", "prefix5", "<안녕>"), is("<안녕>"));                   //url disable 설정이 true이므로 파라메터 설정이 있더라도 필터링 되면 안됨
	
		assertThat(filter.doFilter("/disableUrl4.do", "query", "<안녕>"), is("<안녕>"));                //url disable 설정이 false이므로 url 설정은 무시하고 param 설정대로 필터링 되는지 확인
 		assertThat(filter.doFilter("/disableUrl4.do", "prefix1", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(filter.doFilter("/disableUrl4.do", "prefix1aaa", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(filter.doFilter("/disableUrl4.do", "prefix2aaa", "<안녕>"), is("&lt;안녕&gt;"));  //prefix 설정이 안되어 있으므로 param의 필터링 설정을 따르지 않아야한다.
		assertThat(filter.doFilter("/disableUrl4.do", "prefix2", "<안녕>"), is("<안녕>"));
		assertThat(filter.doFilter("/disableUrl4.do", "prefix3", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(filter.doFilter("/disableUrl4.do", "prefix3-123", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(filter.doFilter("/disableUrl4.do", "prefix4", "<안녕>"), is("<안녕>"));
		assertThat(filter.doFilter("/disableUrl4.do", "prefix4adf123", "<안녕>"), is("<안녕>"));
		assertThat(filter.doFilter("/disableUrl4.do", "prefix5", "<안녕>"), is("&lt;안녕&gt;"));
		assertThat(filter.doFilter("/disableUrl4.do", "prefix5-zddfadf123", "<안녕>"), is("&lt;안녕&gt;"));
	}
	
	@Test
	public void testPrefixDoFilter() {
		assertThat(filter.doFilter("/url1.do", "url1PrefixParameter", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(filter.doFilter("/url1.do", "url1PrefixParameterabc", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(filter.doFilter("/url1.do", "url1PrefixParameter123", "<b>Text</b>"), is("<b>Text</b>"));

		assertThat(filter.doFilter("/notExist.do", "globalPrefixParameter1", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(filter.doFilter("/notExist.do", "globalPrefixParameter1aaa", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(filter.doFilter("/notExist.do", "globalPrefixParameter2", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(filter.doFilter("/notExist.do", "globalPrefixParameter2aaa", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(filter.doFilter("/notExist.do", "globalPrefixParameter3", "<b>Text</b>"), is("<b>Text</b>"));
		assertThat(filter.doFilter("/notExist.do", "globalPrefixParameter3a1", "<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
	}
}
