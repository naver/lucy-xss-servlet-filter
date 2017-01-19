[![logo](https://raw.githubusercontent.com/naver/lucy-xss-filter/master/docs/images/logo/LUCYXSS_792x269px_white.jpg)](https://github.com/naver/lucy-xss-filter)

## Overview
이 라이브러리는 기존의 [lucy-xss-filter](https://github.com/naver/lucy-xss-filter)를 사용해도 여전히 아래와 같은 사유로 XSS 공격에 시달리고 있어 이에 대한 해결책으로 등장한 자바 서블릿 필터 기반의 라이브러리 입니다. 

- 필요한 곳에 XSS 방어코드 누락
- 불필요한 곳에 XSS 방어코드가 적용되는 경우
- 여기저기 XSS 방어코드가 혼재되어 유지보수 비용 증가

Lucy-Xss-Servlet-Filter는 웹어플리케이션으로 들어오는 모든 요청 파라메터에 대해 기본적으로 XSS 방어 필터링을 수행하며 아래와 같은 필터링을 제외할 수 있는 효과적인 설정을 제공합니다.

- 설정한 url 필터링 제외
- 설정한 prefix로 시작하는 파라메터 필터링 제외
- 설정한 파라메터 필터링 제외
 
Lucy-Xss-Servlet-Filter를 적용하게 되면 아래와 같은 장단점이 있습니다. 

- XML 설정 만으로 XSS 방어가 가능해짐
- 비지니스 레이어의 코드 수정이 발생하지 않음
- 개발자가 XSS 방어를 신경 쓰지 않아도 됨
- XSS 방어가 누락되지 않음
- 설정 파일 하나로 XSS 방어절차가 파악됨
- 파라메터명에 대해 관리가 필요해짐
- 일괄 적용되어 영향 받기 때문에 정확한 필터링 룰 정의가 중요함

## Release Information

```XML
<dependency>
	<groupId>com.navercorp.lucy</groupId>
	<artifactId>lucy-xss-servlet</artifactId>
	<version>2.0.0</version>
</dependency>
```

## Getting started

- [lucy-xss-filter](https://github.com/naver/lucy-xss-filter) 설정

- web.xml 설정

```XML
<filter>
	<filter-name>xssEscapeServletFilter</filter-name>
	<filter-class>com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>xssEscapeServletFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

**web.xml example**

```XML
...
<!-- xssEscapeServletFilter는 CharacterEncodingFilter 뒤에 위치해야 한다. -->
<filter>
	<filter-name>encodingFilter</filter-name>
	<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
	<init-param>
		<param-name>encoding</param-name>
		<param-value>UTF-8</param-value>
	</init-param>
</filter>
<filter-mapping>
	<filter-name>encodingFilter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>

<filter>
	<filter-name>xssEscapeServletFilter</filter-name>
	<filter-class>com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>xssEscapeServletFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
...
```

- /resource 폴더 내에 "lucy-xss-servlet-filter-rule.xml" 파일을 생성

- lucy-xss-servlet-filter-rule.xml 필터링 룰 작성  

**lucy-xss-servlet-filter-rule.xml example**

```XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/lucy-xss-servlet">
   <defenders>
       <!-- XssPreventer 등록 -->
       <defender>
           <name>xssPreventerDefender</name>
           <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssPreventerDefender</class>
       </defender>

       <!-- XssSaxFilter 등록 -->
       <defender>
           <name>xssSaxFilterDefender</name>
           <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssSaxFilterDefender</class>
           <init-param>
               <param-value>lucy-xss-sax.xml</param-value>   <!-- lucy-xss-filter의 sax용 설정파일 -->
               <param-value>false</param-value>        <!-- 필터링된 코멘트를 남길지 여부, 성능 효율상 false 추천 -->
           </init-param>
       </defender>

       <!-- XssFilter 등록 -->
       <defender>
           <name>xssFilterDefender</name>
           <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssFilterDefender</class>
           <init-param>
               <param-value>lucy-xss.xml</param-value>    <!-- lucy-xss-filter의 dom용 설정파일 -->
               <param-value>false</param-value>         <!-- 필터링된 코멘트를 남길지 여부, 성능 효율상 false 추천 -->
           </init-param>
       </defender>
   </defenders>

    <!-- default defender 선언, 별다른 defender 선언이 없으면 default defender를 사용해 필터링 한다. -->
    <default>
        <defender>xssPreventerDefender</defender>
    </default>

    <!-- global 필터링 룰 선언 -->
    <global>
        <!-- 모든 url에서 들어오는 globalParameter 파라메터는 필터링 되지 않으며 
                또한 globalPrefixParameter로 시작하는 파라메터도 필터링 되지 않는다. -->
        <params>
            <param name="globalParameter" useDefender="false" />
            <param name="globalPrefixParameter" usePrefix="true" useDefender="false" />
        </params>
    </global>

    <!-- url 별 필터링 룰 선언 -->
    <url-rule-set>
       
       <!-- url disable이 true이면 지정한 url 내의 모든 파라메터는 필터링 되지 않는다. -->
       <url-rule>
           <url disable="true">/disableUrl1.do</url>
       </url-rule>
       
        <!-- url1 내의 url1Parameter는 필터링 되지 않으며 또한 url1PrefixParameter로 시작하는 파라메터도 필터링 되지 않는다. -->
        <url-rule>
            <url>/url1.do</url>
            <params>
                <param name="url1Parameter" useDefender="false" />
                <param name="url1PrefixParameter" usePrefix="true" useDefender="false" />
            </params>
        </url-rule>
        
        <!-- url2 내의 url2Parameter1만 필터링 되지 않으며 url2Parameter2는 xssSaxFilterDefender를 사용해 필터링 한다.  -->
        <url-rule>
            <url>/url2.do</url>
            <params>
                <param name="url2Parameter1" useDefender="false" />
                <param name="url2Parameter2">
                    <defender>xssSaxFilterDefender</defender>
                </param>
            </params>
        </url-rule>
    </url-rule-set>
</config>
```

자세한 사용방법은 아래를 참고해 주세요
- [manual.md](https://github.com/naver/lucy-xss-servlet-filter/blob/master/doc/manual.md)
- [lucy-xss-servlet-filter-rule.xml](https://github.com/naver/lucy-xss-servlet-filter/blob/master/src/test/resources/lucy-xss-servlet-filter-rule.xml)

## Contributing to Lucy
Want to hack on Lucy-XSS? Awesome! There are instructions to get you started here.
They are probably not perfect, please let us know if anything feels wrong or incomplete.

## Licensing
Lucy is licensed under the Apache License, Version 2.0. See LICENSE for full license text.
