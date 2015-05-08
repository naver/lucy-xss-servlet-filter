/*
 * @(#)XssEscapeServletFilter.java $version 2014. 9. 1.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import javax.servlet.*;
import java.io.IOException;

/**
 * RequestParam 을 적용하기 위한 Filter.
 * 
 * @author tod2
 */
public class XssEscapeServletFilter implements Filter {
	/** The filter. */
	private XssEscapeFilter xssEscapeFilter = XssEscapeFilter.getInstance();
	
	/**
	 * @param filterConfig
	 * @throws ServletException
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new XssEscapeServletFilterWrapper(request, xssEscapeFilter), response);
	}

	/**
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}
}
