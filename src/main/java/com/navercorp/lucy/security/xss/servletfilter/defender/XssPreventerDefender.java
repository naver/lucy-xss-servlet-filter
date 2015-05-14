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

package com.navercorp.lucy.security.xss.servletfilter.defender;

import com.nhncorp.lucy.security.xss.XssPreventer;

/**
 * @author todtod80
 */
public class XssPreventerDefender implements Defender {

	/**
	 * @param values String[]
	 * @return void
	 */
	@Override
	public void init(String[] values) {
	}

	/**
	 * @param value String
	 * @return String
	 */
	@Override
	public String doFilter(String value) {
		return XssPreventer.escape(value);
	}
}
