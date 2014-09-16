/*
 * @(#)RequestParamFilteredRequest.java $version 2014. 9. 1.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestParamFilter;

import java.util.*;
import java.util.Map.Entry;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * RequestParam 이 적용되어 parameter 값을 변경하기 위한 Wrapper.<br/><br/>
 * 
 * RequestParam 필터의 Rule에 의해 값이 변조되어야 할 경우 request 객체의 값을 변조하는 역할을 한다.  
 * 
 * @author tod2
 */
public class RequestParamWrapper extends HttpServletRequestWrapper {
	private RequestParamChecker requestParamChecker;
	private String path = null;

	/**
	 * @param request
	 * @param filter
	 */
	public RequestParamWrapper(ServletRequest request, RequestParamChecker requestParamChecker) {
		super((HttpServletRequest)request);
		this.requestParamChecker = requestParamChecker;
		this.path = ((HttpServletRequest)request).getRequestURI();
	}

	/**
	 * Gets the parameter.
	 *
	 * @param paramName the param name
	 * @return the parameter
	 */
	@Override
	public String getParameter(String paramName) {
		String value = super.getParameter(paramName);
		return doFilter(paramName, value);
	}

	/**
	 * Gets the parameter values.
	 *
	 * @param paramName the param name
	 * @return the parameter values
	 */

	@Override
	public String[] getParameterValues(String paramName) {
		String values[] = super.getParameterValues(paramName);
		if (values == null) {
			return values;
		}
		for (int index = 0; index < values.length; index++) {
			values[index] = doFilter(paramName, values[index]);
		}
		return values;
	}

	/**
	 * Gets the parameter map.
	 *
	 * @return the parameter map
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> paramMap = super.getParameterMap();
		Map<String, Object> newFilteredParamMap = new HashMap<String, Object>();

		Set<Entry<String, Object>> entries = paramMap.entrySet();
		for (Entry<String, Object> entry : entries) {
			String paramName = entry.getKey();
			Object[] valueObj = (Object[])entry.getValue();
			String value = doFilter(paramName, String.valueOf(valueObj[0]));
			newFilteredParamMap.put(entry.getKey(), new String[] {value});
		}

		return newFilteredParamMap;
	}

	/**
	 * 파라메터 값에 대해 Filtering 수행
	 *
	 * @param paramName 
	 * @return value
	 */
	private String doFilter(String paramName, String value) {
		return requestParamChecker.doFilter(path, paramName, value);
	}

}
