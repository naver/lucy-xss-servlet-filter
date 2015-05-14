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
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author todtod80
 * @author leeplay
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
