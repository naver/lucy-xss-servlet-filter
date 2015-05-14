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

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author todtod80
 */
public class XssPreventerDefenderTest {
	XssPreventerDefender defender = new XssPreventerDefender();

	@Test
	public void testInit() {
		defender.init(null);
		assertThat(defender.doFilter("<Text"), is("&lt;Text"));
		assertThat(defender.doFilter("<b>Text</b>"), is("&lt;b&gt;Text&lt;/b&gt;"));
	}
}
