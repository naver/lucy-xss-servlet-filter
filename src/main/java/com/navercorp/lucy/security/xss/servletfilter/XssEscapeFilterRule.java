/*
 * @(#)RequestParamParamRule.java $version 2014. 9. 15.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.navercorp.lucy.security.xss.servletfilter;

import com.navercorp.lucy.security.xss.servletfilter.defender.*;

/**
 * 설정 파일 내 Param Rule 정보를 기록할 모델 클래스 
 * 
 * @author tod2
 */
public class RequestParamParamRule {
	private String name;
	private boolean useDefender = true;
	private Defender defender;
	private boolean usePrefix = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUseDefender() {
		return useDefender;
	}

	public void setUseDefender(boolean useDefender) {
		this.useDefender = useDefender;
	}

	public Defender getDefender() {
		return defender;
	}

	public void setDefender(Defender defender) {
		this.defender = defender;
	}
	
	public boolean isUsePrefix() {
		return usePrefix;
	}

	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}
}
