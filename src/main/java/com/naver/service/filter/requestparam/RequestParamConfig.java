/*
 * @(#)RequestParamConfig.java $version 2014. 9. 2.
 *
 * Copyright 2007 NHN Corp. All rights Reserved. 
 * NHN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.naver.service.filter.requestparam;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.w3c.dom.*;

import com.naver.service.filter.requestparam.defender.*;

/**
 * RequestParamFilter 에서 사용할 설정 정보를 관리하는 클래스.<br/><br/>
 * 
 * @author tod2
 */
public class RequestParamConfig {
	private static final String DEFAULT_FILTER_RULE_FILENAME = "request-param-filter-rule.xml";

	private static final Log LOG = LogFactory.getLog(RequestParamConfig.class);
	
	private Map<String, Map<String, RequestParamParamRule>> urlRuleSetMap = new HashMap<String, Map<String, RequestParamParamRule>>();
	private Map<String, RequestParamParamRule> globalParamRuleMap = new HashMap<String, RequestParamParamRule>();
	private Map<String, Defender> defenderMap = new HashMap<String, Defender>();
	private Defender defaultDefender = null;

	/**
	 * 설정값 초기화
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public RequestParamConfig() throws Exception {
		this(DEFAULT_FILTER_RULE_FILENAME);
	}
	
	/**
	 * 설정값 초기화
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public RequestParamConfig(String filename) throws Exception {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
			Element rootElement = builder.parse(is).getDocumentElement();

			// defender 설정
			addDefenders(rootElement);
			
			// defaultDefender 설정
			addDefaultInfo(rootElement);

			// globalParam 설정
			addGlobalParams(rootElement);

			// urlRule 설정
			addUrlRuleSet(rootElement);

		} catch (Exception e) {
			throw new Exception(String.format("Cannot parse the RequestParam configuration file [%s].", new Object[] {filename}), e);
		}
	}

	/**
	 * 기본값 정보 설정
	 * 
	 * @param rootElement
	 */
	private void addDefaultInfo(Element rootElement) {
		NodeList nodeList = rootElement.getElementsByTagName("default");
		if (nodeList.getLength() > 0) {
			Element element = (Element)nodeList.item(0);
			addDefaultInfoItems(element);
		}
	}

	/**
	 * 기본값 내 각 항목 설정
	 * 
	 * @param element
	 */
	private void addDefaultInfoItems(Element element) {
		NodeList nodeList = element.getElementsByTagName("defender");
		if (nodeList.getLength() > 0) {
			defaultDefender = defenderMap.get(nodeList.item(0).getTextContent());
			
			if (defaultDefender == null) {
				LOG.error("Error config 'Default defender': Not found '" + nodeList.item(0).getTextContent() + "'");
			}
		}
	}

	/**
	 * Global Param 설정
	 * 
	 * @param rootElement
	 */
	private void addGlobalParams(Element rootElement) {
		NodeList nodeList = rootElement.getElementsByTagName("global");
		if (nodeList.getLength() > 0) {
			Element params = (Element)nodeList.item(0);
			NodeList paramNodeList = params.getElementsByTagName("params");
			
			if (paramNodeList.getLength() > 0) {
				globalParamRuleMap = createRequestParamRuleMap((Element)nodeList.item(0));
			}			
		}
	}
	
	/**
	 * Url Rule Set 설정
	 * 
	 * @param elements
	 */
	private void addUrlRuleSet(Element rootElement) {
		NodeList nodeList = rootElement.getElementsByTagName("url-rule");
		for (int i = 0; nodeList.getLength() > 0 && i < nodeList.getLength(); i++) {
			Element element = (Element)nodeList.item(i);
			addUrlRule(element);
		}
	}

	/**
	 * Url Rule 설정
	 * 
	 * @param elements
	 */
	private void addUrlRule(Element element) {
		Map<String, RequestParamParamRule> paramRuleMap = null;
		String url = null;
		
		NodeList nodeList = element.getElementsByTagName("url");
		if (nodeList.getLength() > 0) {
			url = nodeList.item(0).getTextContent();
		}
		
		if (!url.isEmpty()) {
			nodeList = element.getElementsByTagName("disable");
			if (nodeList.getLength() > 0) {
				paramRuleMap = createRequestParamRuleMap(url, nodeList.item(0).getTextContent());

				if (paramRuleMap != null) {
					urlRuleSetMap.put(url, paramRuleMap);
					return;
				}
			}
		}
		
		nodeList = element.getElementsByTagName("params");
		if (nodeList.getLength() > 0) {
			paramRuleMap = createRequestParamRuleMap((Element)nodeList.item(0));
		}

		urlRuleSetMap.put(url, paramRuleMap);
	}

	/**
	 * Url Rule 모델 객체 생성
	 * 
	 * @param element
	 * @return
	 */
	private Map<String, RequestParamParamRule> createRequestParamRuleMap(Element element) {
		Map<String, RequestParamParamRule> urlRuleMap = new HashMap<String, RequestParamParamRule>();

		NodeList nodeList = element.getElementsByTagName("param");
		for (int i = 0; nodeList.getLength() > 0 && i < nodeList.getLength(); i++) {
			Element eachElement = (Element)nodeList.item(i);
			String name = eachElement.getAttribute("name");
			boolean useDefender = StringUtils.equalsIgnoreCase(eachElement.getAttribute("useDefender"), "false") ? false : true;
			Defender defender = null;

			NodeList defenderNodeList = eachElement.getElementsByTagName("defender");
			if (defenderNodeList.getLength() > 0) {
				defender = defenderMap.get(defenderNodeList.item(0).getTextContent());
				
				if (defender == null) {
					LOG.error("Error config 'param defender': Not found '" + nodeList.item(0).getTextContent() + "'");
				}
			} else {
				defender = defaultDefender;
			}

			RequestParamParamRule urlRule = new RequestParamParamRule();
			urlRule.setName(name);
			urlRule.setUseDefender(useDefender);
			urlRule.setDefender(defender);

			urlRuleMap.put(name, urlRule);
		}

		return urlRuleMap;
	}

	/**
	 * Url Rule 모델 객체 생성
	 * 
	 * @param string, boolean
	 * @return
	 */
	private Map<String, RequestParamParamRule> createRequestParamRuleMap(String url, String disable) {
		if (disable.isEmpty() && !(disable.equals("true"))) {
			return null;
		}
		
		Map<String, RequestParamParamRule> urlRuleMap = new HashMap<String, RequestParamParamRule>();
		RequestParamParamRule urlRule = new RequestParamParamRule();
		urlRule.setName(url);
		urlRule.setUseDefender(false);
		urlRule.setDefender(defaultDefender);
		urlRuleMap.put(url, urlRule);
		
		return urlRuleMap;
	}
	
	/**
	 * Defenders 설정
	 * 
	 * @param rootElement
	 */
	private void addDefenders(Element rootElement) {
		NodeList nodeList = rootElement.getElementsByTagName("defenders");

		if (nodeList.getLength() > 0) {
			Element element = (Element)nodeList.item(0);
			addDefender(element);
		}
	}

	/**
	 * Defender 설정
	 * 
	 * @param elements
	 */
	private void addDefender(Element element) {
		NodeList nodeList = element.getElementsByTagName("defender");
		for (int i = 0; nodeList.getLength() > 0 && i < nodeList.getLength(); i++) {
			String name = null;
			String clazz = null;
			String[] args = null;
			
			Element eachElement = (Element)nodeList.item(i);
			NodeList nameNodeList = eachElement.getElementsByTagName("name");
			if (nameNodeList.getLength() > 0) {
				name = nameNodeList.item(0).getTextContent();
			}

			NodeList classNodeList = eachElement.getElementsByTagName("class");
			if (classNodeList.getLength() > 0) {
				clazz = classNodeList.item(0).getTextContent();
			}
			
			NodeList initParamNodeList = eachElement.getElementsByTagName("init-param");
			if (initParamNodeList.getLength() > 0) {
				Element paramValueElement = (Element)initParamNodeList.item(0);
				NodeList paramValueNodeList = paramValueElement.getElementsByTagName("param-value");
			
				args = new String[paramValueNodeList.getLength()];
				for (int j = 0; paramValueNodeList.getLength() > 0 && j < paramValueNodeList.getLength(); j++) {
					args[j] = paramValueNodeList.item(j).getTextContent();
				}
			}

			Defender defender;
			try {
				defender = (Defender)Class.forName(clazz.trim()).newInstance();
				defender.init(args);
				defenderMap.put(name, defender);
			} catch (InstantiationException e) {
				LOG.error("Error config 'Defender': " + clazz);
				LOG.error(e.getMessage());
			} catch (IllegalAccessException e) {
				LOG.error("Error config 'Defender': " + clazz);
				LOG.error(e.getMessage());
			} catch (ClassNotFoundException e) {
				LOG.error("Error config 'Defender': " + clazz);
				LOG.error(e.getMessage());
			}
		}
	}

	/**
	 * 해당 URL 에 정의된 Param 정의 정보를 획득.<br/><br/>
	 * 
	 * 해당 URL 에 정의된 Param 정보가 없을 경우 Global Param 정보를 찾아 반환하며, 둘 다 없을 경우는 null 을 반환한다.
	 * 
	 * @param url
	 * @param paramName
	 * @return
	 */
	public RequestParamParamRule getUrlParamRule(String url, String paramName) {
		Map<String, RequestParamParamRule> urlParamRuleMap = urlRuleSetMap.get(url);
		if (urlParamRuleMap == null) {
			return globalParamRuleMap.get(paramName);
		} else {
			RequestParamParamRule paramRule = urlParamRuleMap.get(paramName);
			
			if (paramRule == null) {
				paramRule = globalParamRuleMap.get(paramName);
			}
			
			if (paramRule == null) {
				if (urlParamRuleMap.containsKey(url)) {
					if (!(urlParamRuleMap.get(url).isUseDefender())) {
						RequestParamParamRule paramUrlRule = new RequestParamParamRule();
						paramUrlRule.setUseDefender(false);
					
						return paramUrlRule;
					}
				}
			}
			return paramRule;
		}
	}

	/**
	 * 해당 URL 에 정의된 Param 정의 정보를 획득.<br/><br/>
	 * 
	 * @return
	 */
	public Map<String, Defender> getDefenderMap() {
		return defenderMap;
	}

	/**
	 * Default Defender 획득
	 * 
	 * @return
	 */
	public Defender getDefaultDefender() {
		return defaultDefender;
	}
}
