/*
 * @(#)XssEscapeServletFilterWrapperTest.java $version 2014. 9. 24.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * XssEscapeServletFilterWrapper 에 대한 통합 테스트
 * 
 * @author tod2
 */
public class XssEscapeServletFilterWrapperTest {
	XssEscapeFilter checker = XssEscapeFilter.getInstance();;
	MockHttpServletRequest request;
	XssEscapeServletFilterWrapper wrapper;

	@Test
	public void testGetMethodGetParameter() {
		request = new MockHttpServletRequest("GET", "/notExistUrl.nhn");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("q", "<b>Text</b>");
		wrapper = new XssEscapeServletFilterWrapper(request, checker);

		assertThat(wrapper.getParameter("title"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(wrapper.getParameter("q"), is("<b>Text</b>"));

		request = new MockHttpServletRequest("GET", "/tlist/list.nhn");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("mode", "<script>Text</script>");
		request.addParameter("q", "<script>Text</script>");
		wrapper = new XssEscapeServletFilterWrapper(request, checker);

		assertThat(wrapper.getParameter("title"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(wrapper.getParameter("mode"), is("&lt;script&gt;Text&lt;/script&gt;"));
		assertThat(wrapper.getParameter("q"), is("&lt;script&gt;Text&lt;/script&gt;"));
	}

	@Test
	public void testPostMethodGetParameter() {
		request = new MockHttpServletRequest("POST", "/notExistUrl.nhn");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("q", "<b>Text</b>");
		wrapper = new XssEscapeServletFilterWrapper(request, checker);

		assertThat(wrapper.getParameter("title"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(wrapper.getParameter("q"), is("<b>Text</b>"));

		request = new MockHttpServletRequest("POST", "/tlist/list.nhn");
		request.addParameter("title", "<b>Text</b>");
		request.addParameter("mode", "<script>Text</script>");
		request.addParameter("q", "<script>Text</script>");
		wrapper = new XssEscapeServletFilterWrapper(request, checker);

		assertThat(wrapper.getParameter("title"), is("&lt;b&gt;Text&lt;/b&gt;"));
		assertThat(wrapper.getParameter("mode"), is("&lt;script&gt;Text&lt;/script&gt;"));
		assertThat(wrapper.getParameter("q"), is("&lt;script&gt;Text&lt;/script&gt;"));
	}

	@Test
	public void testGetMethodGetParameterValues() {
		request = new MockHttpServletRequest("GET", "/notExistUrl.nhn");
		request.addParameter("title", "<b>Text1</b>");
		request.addParameter("title", "<b>Text2</b>");
		request.addParameter("q", "<b>Text1</b>");
		request.addParameter("q", "<b>Text2</b>");
		wrapper = new XssEscapeServletFilterWrapper(request, checker);

		String[] values = wrapper.getParameterValues("title");
		assertThat(values[0], is("&lt;b&gt;Text1&lt;/b&gt;"));
		assertThat(values[1], is("&lt;b&gt;Text2&lt;/b&gt;"));

		values = wrapper.getParameterValues("q");
		assertThat(values[0], is("<b>Text1</b>"));
		assertThat(values[1], is("<b>Text2</b>"));

		request = new MockHttpServletRequest("GET", "/tlist/list.nhn");
		request.addParameter("title", "<b>Text1</b>");
		request.addParameter("title", "<b>Text2</b>");
		request.addParameter("mode", "<script>Text1</script>");
		request.addParameter("mode", "<script>Text2</script>");
		request.addParameter("q", "<script>Text1</script>");
		request.addParameter("q", "<script>Text2</script>");
		wrapper = new XssEscapeServletFilterWrapper(request, checker);

		values = wrapper.getParameterValues("title");
		assertThat(values[0], is("&lt;b&gt;Text1&lt;/b&gt;"));
		assertThat(values[1], is("&lt;b&gt;Text2&lt;/b&gt;"));

		values = wrapper.getParameterValues("mode");
		assertThat(values[0], is("&lt;script&gt;Text1&lt;/script&gt;"));
		assertThat(values[1], is("&lt;script&gt;Text2&lt;/script&gt;"));

		values = wrapper.getParameterValues("q");
		assertThat(values[0], is("&lt;script&gt;Text1&lt;/script&gt;"));
		assertThat(values[1], is("&lt;script&gt;Text2&lt;/script&gt;"));
	}

	@Test
	public void testGetMethodGetParameterMap() {
		request = new MockHttpServletRequest("GET", "/notExistUrl.nhn");
		request.addParameter("title", "<b>Text1</b>");
		request.addParameter("title", "<b>Text2</b>");
		request.addParameter("q", "<b>Text1</b>");
		wrapper = new XssEscapeServletFilterWrapper(request, checker);

		Map<String, Object> map = wrapper.getParameterMap();
		String[] values = (String[])map.get("title");
		assertThat(values[0], is("&lt;b&gt;Text1&lt;/b&gt;"));
		assertThat(values[1], is("&lt;b&gt;Text2&lt;/b&gt;"));

		values = (String[])map.get("q");
		assertThat(values[0], is("<b>Text1</b>"));

		request = new MockHttpServletRequest("GET", "/tlist/list.nhn");
		request.addParameter("title", "<b>Text1</b>");
		request.addParameter("title", "<b>Text2</b>");
		request.addParameter("mode", "<script>Text1</script>");
		request.addParameter("mode", "<script>Text2</script>");
		request.addParameter("q", "<script>Text1</script>");
		wrapper = new XssEscapeServletFilterWrapper(request, checker);

		map = wrapper.getParameterMap();
		values = (String[])map.get("title");
		assertThat(values[0], is("&lt;b&gt;Text1&lt;/b&gt;"));
		assertThat(values[1], is("&lt;b&gt;Text2&lt;/b&gt;"));

		values = (String[])map.get("mode");
		assertThat(values[0], is("&lt;script&gt;Text1&lt;/script&gt;"));
		assertThat(values[1], is("&lt;script&gt;Text2&lt;/script&gt;"));

		values = (String[])map.get("q");
		assertThat(values[0], is("&lt;script&gt;Text1&lt;/script&gt;"));
	}
}
