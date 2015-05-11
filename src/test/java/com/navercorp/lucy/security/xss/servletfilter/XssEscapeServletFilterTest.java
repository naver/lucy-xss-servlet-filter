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
	MockHttpServletResponse response;
	MockFilterChain chain;
	
	@Test
	public void testDoFilter() throws IOException, ServletException {
		request = new MockHttpServletRequest("GET", "/notExistUrl.do");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("globalParameter", "<b>Text</b>");
		response = new MockHttpServletResponse();
		chain = new MockFilterChain();
		
		filter.doFilter(request, response, chain);
		
		assertThat(chain.getRequest().getParameter("title"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(chain.getRequest().getParameter("globalParameter"), is("<b>Text</b>"));

		request = new MockHttpServletRequest("POST", "/url1.do");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("mode", "<script>Text</script>");
		request.addParameter("globalParameter", "<script>Text</script>");
		response = new MockHttpServletResponse();
		chain = new MockFilterChain();
		
		filter.doFilter(request, response, chain);
		
		assertThat(chain.getRequest().getParameter("title"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(chain.getRequest().getParameter("mode"), is("&lt;script&gt;Text&lt;/script&gt;"));
		assertThat(chain.getRequest().getParameter("globalParameter"), is("&lt;script&gt;Text&lt;/script&gt;"));
	}
}
