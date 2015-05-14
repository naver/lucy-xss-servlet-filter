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

import com.navercorp.lucy.security.xss.servletfilter.defender.Defender;

/**
 * @author todtod80
 * @author leeplay
 */
public class XssEscapeFilterRule {
	private String name;
	private boolean useDefender = true;
	private Defender defender;
	private boolean usePrefix = false;

	/**
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name String
	 * @return void
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return boolean
	 */
	public boolean isUseDefender() {
		return useDefender;
	}

	/**
	 * @param useDefender boolean
	 * @return void
	 */
	public void setUseDefender(boolean useDefender) {
		this.useDefender = useDefender;
	}

	/**
	 * @return Defender
	 */
	public Defender getDefender() {
		return defender;
	}

	/**
	 * @param defender Defender
	 * @return void
	 */
	public void setDefender(Defender defender) {
		this.defender = defender;
	}

	/**
	 * @return boolean
	 */
	public boolean isUsePrefix() {
		return usePrefix;
	}

	/**
	 * @param usePrefix boolean
	 * @return void
	 */
	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}
}
