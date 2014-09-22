## 개요

기존에는 아래의 사유로 XSS 공격 방어가 누락되거나 비효율적으로 적용되고 있음
- 별도의 preventer 를 구현하여 Controller / BO 코드 내에 적용할 경우, 기능 추가 시 XSS 공격 방어 체크를 누락하여 보안에 허점 발생
- 전체 요청에 대해 용도에 벗어난 XSS Filter 를 적용하여 서비스 성능에 저하가 발생할 수 있으며, White List 방식으로 보안 허점 발생 가능성이 여전히 존재

그래서 서비스 내 URL별 요청 Parameter에 대해 기본으로 모든 태그를 무력화하는 Preventer를 적용하고, 특정 paramater에는 필요에 따라 Preventer를 적용하지 않거나 XSS Filter를 일관된 방식으로 적용할 수 있는 설정 방식을 제공하고자 함


## 적용방법
1. Dependency 설정
``` XML
<dependency>
    <groupId>com.naver.service</groupId>
    <artifactId>request-param-filter</artifactId>
    <version>0.0.3</version>
</dependency>
```

2. Servlet Filter 설정
``` XML
<filter>
    <filter-name>requestParamFilter</filter-name>
    <filter-class>com.naver.service.filter.requestParamFilter.RequestParamFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>requestParamFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

3. Rule 파일 설정
- resource 폴더 내에 "request-param-filter-rule.xml" 파일을 생성
- XML 각 항목에 대한 설명은 "Rule 파일 XML 항목별 설명"을 참고한다.    
``` XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/request-param">
    <defenders>
        <defender>
            <name>preventer</name>
            <class>com.naver.service.filter.requestParamFilter.defender.XssPreventerDefender</class>
        </defender>
    </defenders>
 
    <default>
        <defender>preventer</defender>
    </default>
 
    <global>
        <params>
	    <!-- 모든 URL에 요청되는 'q' parameter 에 대해서는 filtering을 하지 않음. 서버 코드 내에서 별도 escape 처리를 해야 됨 -->
            <param name="q" useDefender="false" />        
        </params>
    </global>
     
    <url-rule-set>
        <url-rule>
            <url>/search.nhn</url>
            <params>
		<!-- /search.nhn URL에 요청되는 'query' parameter 에 대해서는 filtering을 하지 않음. 서버 코드 내에서 별도 escape 처리를 해야 됨 -->
                <param name="query" useDefender="false" />        
            </params>
        </url-rule>
    </url-rule-set>
</config>
```
