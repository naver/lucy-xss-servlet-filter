/*
 * @(#)Defender.java $version 2014. 9. 15.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestParamFilter.defender;

/**
 * Request Param Filter 에서 Filtering 동작을 정의하는 Interface 
 * 
 * @author tod2
 */
public interface Defender {
	public abstract void init(String[] values);
	public abstract String doFilter(String value);
}
