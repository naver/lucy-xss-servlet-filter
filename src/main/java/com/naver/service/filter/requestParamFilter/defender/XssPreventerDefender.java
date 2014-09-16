/*
 * @(#)XssPreventerDefender.java $version 2014. 9. 15.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestParamFilter.defender;

import com.nhncorp.lucy.security.xss.*;

/**
 * Lucy XSS Perventer 를 사용하는 Defender Adapter
 * 
 * @author tod2
 */
public class XssPreventerDefender implements Defender {
	/**
	 * @param values
	 * @see com.naver.service.filter.requestParamFilter.defender.Defender#init(java.lang.String[])
	 */
	@Override
	public void init(String[] values) {
	}
	
	/**
	 * @param value
	 * @return
	 * @see com.naver.service.filter.requestParamFilter.defender.Defender#doFilter(java.lang.String)
	 */
	@Override
	public String doFilter(String value) {
		return XssPreventer.escape(value);
	}
}
