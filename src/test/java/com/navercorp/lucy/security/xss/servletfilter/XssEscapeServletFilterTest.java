/*
 * @(#)RequestParamFilterTest.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import java.io.*;

import javax.servlet.*;

import org.junit.*;
import org.springframework.mock.web.*;

/**
 * RequestParamFilter 에 대한 통합 테스트
 * 
 * @author tod2
 */
public class RequestParamFilterTest {
	RequestParamFilter filter = new RequestParamFilter();
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	MockFilterChain chain;
	
	@Test
	public void testDoFilter() throws IOException, ServletException {
		request = new MockHttpServletRequest("GET", "/notExistUrl.nhn");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("q", "<b>Text</b>");
		response = new MockHttpServletResponse();
		chain = new MockFilterChain();
		
		filter.doFilter(request, response, chain);
		
		assertThat(chain.getRequest().getParameter("title"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(chain.getRequest().getParameter("q"), is("<b>Text</b>"));

		request = new MockHttpServletRequest("POST", "/tlist/list.nhn");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("mode", "<script>Text</script>");
		request.addParameter("q", "<script>Text</script>");
		response = new MockHttpServletResponse();
		chain = new MockFilterChain();
		
		filter.doFilter(request, response, chain);
		
		assertThat(chain.getRequest().getParameter("title"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(chain.getRequest().getParameter("mode"), is("&lt;script&gt;Text&lt;/script&gt;"));
		assertThat(chain.getRequest().getParameter("q"), is("&lt;script&gt;Text&lt;/script&gt;"));
	}
}
