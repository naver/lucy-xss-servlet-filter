/*
 * @(#)XssSaxFilterDefender.java $version 2014. 9. 23.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestparam.defender;

import org.apache.commons.lang3.*;

import com.nhncorp.lucy.security.xss.*;

/**
 * Lucy XSS Sax Filter 를 사용하는 Defender Adapter
 * 
 * @author tod2
 */
public class XssSaxFilterDefender implements Defender {
	private XssSaxFilter filter;

	/**
	 * @param values
	 * @see com.naver.service.filter.requestparam.defender.Defender#init(java.lang.String[])
	 */
	@Override
	public void init(String[] values) {
		if (values == null || values.length == 0) {
			filter = XssSaxFilter.getInstance();
		} else {
			switch (values.length) {
				case 1:
					if (isBoolean(values[0])) {
						filter = XssSaxFilter.getInstance(convertBoolean(values[0]));	
					} else {
						filter = XssSaxFilter.getInstance(values[0]);
					}
					break;
				case 2:
					filter = XssSaxFilter.getInstance(values[0], convertBoolean(values[1]));	
					break;
				default:
					break;
			}
		}
	}

	/**
	 * @param value
	 * @return
	 * @see com.naver.service.filter.requestparam.defender.Defender#doFilter(java.lang.String)
	 */
	@Override
	public String doFilter(String value) {
		return filter.doFilter(value);
	}

	/**
	 * 해당 문자열이 boolean 값을 의미하는지 여부
	 * 
	 * @param value
	 * @return
	 */
	private boolean isBoolean(String value) {
		return StringUtils.equalsIgnoreCase(value, "true") || StringUtils.equalsIgnoreCase(value, "false");
	}
	
	/**
	 * 해당 문자열 값으로 boolean 값을 획득.</br></br>
	 * 
	 * 허용되지 않는 값일 경우는 false 를 return한다.
	 * 
	 * @param value
	 * @return
	 */
	private boolean convertBoolean(String value) {
		return StringUtils.equalsIgnoreCase(value, "true") ? true : false;
	}
}
