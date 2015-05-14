/*
 * @(#)XssEscapeFilter.java $version 2014. 9. 15.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;

/**
 * 요청 URL 및 parameter 에 대해 RequestParam 체크를 진행하는 Checker.
 * 
 * @author tod2
 */
public final class XssEscapeFilter {
	private static final Log LOG = LogFactory.getLog(XssEscapeFilter.class);

	private static XssEscapeFilter xssEscapeFilter;
	private static XssEscapeFilterConfig config;

	static {
		try {
			xssEscapeFilter = new XssEscapeFilter();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private XssEscapeFilter() {
		config = new XssEscapeFilterConfig();
	}

	/**
	 * @return
	 */
	public static XssEscapeFilter getInstance() {
		return xssEscapeFilter;
	}

	/**
	 * 해당 URL에 대한 파라메터가 등록되어 있으면 filtering, 아닐 경우 공백을 반환
	 * 
	 * @param url
	 * @param paramName 
	 * @param value 
	 * @return
	 */
	public String doFilter(String url, String paramName, String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}

		XssEscapeFilterRule urlRule = config.getUrlParamRule(url, paramName);
		if (urlRule == null) {
			// Default defender 적용
			return config.getDefaultDefender().doFilter(value);
		} 

		if (!urlRule.isUseDefender()) {
			log(url, paramName, value);
			return value;
		}

		return urlRule.getDefender().doFilter(value);
	}

	private void log(String url, String paramName, String value) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Do not filtered Parameter. Request url: " + url + ", Parameter name: " + paramName + ", Parameter value: " + value);
		}
	}
}
