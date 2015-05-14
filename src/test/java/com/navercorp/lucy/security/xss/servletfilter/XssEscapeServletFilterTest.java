/*
 * @(#)XssEscapeServletFilterTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * XssEscapeServletFilter 에 대한 통합 테스트
 * 
 * @author tod2
 */
public class XssEscapeServletFilterTest {
	XssEscapeServletFilter filter = new XssEscapeServletFilter();
	MockHttpServletRequest request;
	MockHttpServletResponse response = new MockHttpServletResponse();
	MockFilterChain chain = new MockFilterChain();
	
	@Test
	public void testDoFilterWithUndefinedUrl() throws IOException, ServletException {
		request = new MockHttpServletRequest("GET", "/notExistUrl.do");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("globalParameter", "<b>Text</b>");
		
		filter.doFilter(request, response, chain);
		
		assertFiltered("title", "&lt;b&gt;Text&lt;/b&gt;");
		assertFiltered("globalParameter", "<b>Text</b>");
	}

	@Test
	public void testDoFilterWithDefinedUrl() throws IOException, ServletException {
		request = new MockHttpServletRequest("POST", "/url1.do");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("mode", "<script>Text</script>");
		request.addParameter("globalParameter", "<script>Text</script>");
		request.addParameter("url1Parameter", "<hello>");

		filter.doFilter(request, response, chain);

		assertFiltered("title", "&lt;b&gt;Text&lt;/b&gt;");
		assertFiltered("mode", "&lt;script&gt;Text&lt;/script&gt;");
		assertFiltered("globalParameter", "&lt;script&gt;Text&lt;/script&gt;");
		assertFiltered("url1Parameter", "<hello>");
	}
	
	private void assertFiltered(String paramName, String filteredValue) {
		ServletRequest filteredRequest = chain.getRequest();
		assertThat(filteredRequest.getParameter(paramName), is(filteredValue));
	}
}
