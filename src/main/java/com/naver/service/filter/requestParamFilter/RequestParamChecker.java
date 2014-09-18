/*
 * @(#)RequestParamChecker.java $version 2014. 9. 15.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestParamFilter;

import org.apache.commons.lang.*;
import org.apache.commons.logging.*;

/**
 * 요청 URL 및 parameter 에 대해 RequestParam 체크를 진행하는 Checker.
 * 
 * @author tod2
 */
public final class RequestParamChecker {
	private static final Log LOG = LogFactory.getLog(RequestParamChecker.class);

	private static RequestParamChecker requestParamChecker;
	private static RequestParamConfig config;

	static {
		try {
			requestParamChecker = new RequestParamChecker();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private RequestParamChecker() throws Exception {
		config = new RequestParamConfig();
	}

	/**
	 * @return
	 */
	public static RequestParamChecker getInstance() {
		return requestParamChecker;
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

		RequestParamParamRule urlRule = config.getUrlParamRule(url, paramName);
		if (urlRule == null) {
			// Default defender 적용
			return config.getDefaultDefender().doFilter(value);
		} else {
			if (!urlRule.isUseDefender()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Do not filtered Parameter. Request url: " + url + ", Parameter name: " + paramName + ", Parameter value: " + value);
				}
				return value;
			} else {
				return urlRule.getDefender().doFilter(value);
			}
		}
	}
}
