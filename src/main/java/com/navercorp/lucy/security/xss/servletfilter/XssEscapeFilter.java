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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author todtod80
 * @author leeplay
 * @author benelog
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

	/**
	 * Default Constructor
	 */
	private XssEscapeFilter() {
		config = new XssEscapeFilterConfig();
	}

	/**
	 * @return XssEscapeFilter
	 */
	public static XssEscapeFilter getInstance() {
		return xssEscapeFilter;
	}

	/**
	 * @param url String
	 * @param paramName String
	 * @param value String
	 * @return String
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

	/**
	 * @param url String
	 * @param paramName String
	 * @param value String
	 * @return void
	 */
	private void log(String url, String paramName, String value) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Do not filtered Parameter. Request url: " + url + ", Parameter name: " + paramName + ", Parameter value: " + value);
		}
	}
}
