<h6>_용어가 낯설다면 문서 하단부 용어설명 참고해주세요_</h6>

## 개요
기존에는 아래의 사유로 XSS 공격 방어가 누락되거나 비효율적으로 적용되고 있다. 
- 별도의 필터링 라이브러리를 구현하여 Controller / BO 코드 내에 적용할 경우, 기능 추가 시 XSS 공격 방어 체크를 누락하여 보안에 허점 발생
- 서비스에서 필터링 대상이 아닌데 XSS Filter 를 적용하여 서비스 성능에 저하가 발생할 수 있음
- 개발자 임의대로 XSS 방어 로직을 적용할 경우 보안상 문제의 가능성이 있을 수 있음

그래서 서비스 내 URL별 요청 parameter에 대해 기본으로 모든 태그를 무력화하는 Preventer를 적용하고, 특정 paramater에는 필요에 따라 Preventer를 적용하지 않거나 XSS Filter를 일관된 방식으로 적용할 수 있는 설정 방식을 제공하고자 함

## lucy-xss-filter vs lucy-xss-servlet-filter
| 항목 |xss-filter                      |xss-servlet-filter                |
|----|--------------------------------|----------------------------------|
|설명   |xss 공격을 방어하는 기본 라이브러리                  |xss-filter를 기반으로  xss 공격을 방어하는 서블릿 필터 라이브러리|
|차이점|xss 방어 코드를 개발자가 직접 서비스 코드에 추가|web.xml에 서블릿 필터 선언으로 xss 방어 코드가 전체 일괄 적용됨|
|장점   |개발 자유도 증가                                                 |xss 방어 코드를 신경쓰지 않아도 설정된 필터링 조건에 전체 적용됨|
|단점   |xss 방어 로직이 필요할 때마다 직접 작성해야 함   |전체 적용되기 때문에 의도치 않은 결과가 발생할 수 있으므로 테스트 필수|
 
 ___권장사항___
 문자열 전체를 escape하는 경우가 주로 많다면 xss-servlet-filter를 사용해 전체에 일괄 적용하는 방법을 추천하며 
 서비스의 성격상 필터링의 조건이 동적으로 변하거나 전체 문자열에 대해 필터링하지 않고 태그별로 선별해서 필터링 해야한다면 개발자가 판단해 xss-filter 라이브러리를 직접 사용하는 방법을 추천 

[lucy-xss-filter 문서](http://devcafe.nhncorp.com/index.php?mid=issuetracker&act=dispIssuetrackerDownload&vid=Lucy&package_srl=282220)


## 주의사항
- ** <font color='red'> 사용자 입력데이터를 화면에 다시 노출시킬 목적이 아닌 Business Logic에만 쓰이는 데이터일 경우에는 filtering을 하지 말아야 한다. 불필요한 eacape/unescape이 발생해 원본데이터가 훼손될 수 있다. </font> **

- ** <font color='red'> 원본 데이터의 훼손 가능성 및 DB 검색 키워드용으로 저장 시 문제가 있어 파라메터 필터링을 DB에 저장되기 전 시점이 아닌 사용자 화면에 보여지는 시점에 진행하고자 한다면  useDefender 설정을 false로 한다. 하지만 이럴 경우 코드 곳곳에 xss 공격 방어 로직이 삽입되어 개발자의 사용 상 주의가 필요하다. </font> **

- ** <font color='red'> 파라메터 필터링과 컨텐츠 필터링 둘 다  전부 적용을 고려한다면  아래 링크에서 웹플랫폼 검토의견을 참고한다. </font> **
 	(http://yobi.navercorp.com/lucy-projects/lucy-xss-servlet-filter/post/3) 	

- ** <font color='red'> web.xml 내에 filter-mapping 선언 시 전체가 아닌 특정 url 만 등록하거나, XssEscapeServletFilter를 Copy 및 재구현하여 특정 url 만 타도록 하는 등 예외를 두지 않도록 한다. </font> **

- ** <font color='red'> global params에 서비스 전체에서 사용되는 공통 파라메터 값이 아닌, 서비스되는 URL의 모든 파라메터 값을 useDefender = "false" 로 넣지 않도록 한다. </font> **

- ** <font color='red'> HttpServletRequest의 getQueryString()을 사용해 값을 그대로 UI에 노출하는 경우 lucy-xss-servlet-filter에서 필터링을 하게되면 원본 데이터가 훼손되어 오류를 발생시킬 수 있어  (ex: 서버에서 URL Redirect 시) 필터링 처리를 하지 않으며 개발자가 XssPreventer를 사용해 직접 필터링 처리를 하도록 한다. </font> **


## 적용방법 
1. Dependency 설정
``` XML
<dependency>
	<groupId>com.nhncorp.lucy</groupId>
	<artifactId>lucy-xss-servlet</artifactId>
	<version>1.0.2</version>
</dependency>
```

2. Servlet Filter 설정
``` XML
<filter>
	<filter-name>requestParamFilter</filter-name>
	<filter-class>com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>requestParamFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
__주의 : requestParamFilter는 아래 예제처럼 Lucy 1.6을 사용한다면 ServiceFilter 뒤에, Lucy 1.7을 사용한다면 CharacterEncodingFilter 뒤에 위치해야 한다.__

```XML
<!-- lucy 1.7 sample-->
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
	<filter-name>requestParamFilter</filter-name>
	<filter-class>com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>requestParamFilter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
```

```XML
<!-- lucy 1.6 sample-->
<filter>
	<filter-name>service-filter</filter-name>
	<filter-class>com.nhncorp.lucy.web.filter.ServiceFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>service-filter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>

<filter>
	<filter-name>requestParamFilter</filter-name>
	<filter-class>com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>requestParamFilter</filter-name>
	<url-pattern>/*</url-pattern>
</filter-mapping>
```

3. Rule 파일 설정 예제 (XML 각 항목에 대한 설명은 "Rule 파일 XML 항목별 설명"을 참고한다.)
- resource 폴더 내에 "lucy-xss-servlet-filter-rule.xml" 파일을 생성
- 특정 Parameter에만 파라메터 필터링을 적용 

```XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/request-param">
    <defenders>
        <defender>
        	<!--  Lucy XSS XssPreventer defender 등록 -->
            <name>preventer</name>
            <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssPreventerDefender</class>
        </defender>
    </defenders>
 
    <default>
        <defender>preventer</defender>
    </default>
 
 	<!-- xml에 설정한 <global>, <url-rule-set> 태그안에 설정된 정보가 필터링 선정 기준이 된다. -->
 	<!-- 서블릿 필터가 인자로 받은 parameter와 url이 xml에 설정한 필터링 선정 기준에 포함되지 않으면 아무런 작업도 수행하지 않는다. -->
    <global>
        <params>
	        <!-- 모든 URL에 요청되는 'q' parameter 에 대해서는 filtering을 하지 않음. 서버 코드 내에서 직접 escape 처리를 해야 됨 -->
            <param name="q" useDefender="false" />        
        </params>
    </global>
     
    <url-rule-set>
        <url-rule>
            <url>/search.nhn</url>
            <params>
				<!-- /search.nhn URL에 요청되는 'query' parameter 에 대해서는 filtering을 하지 않음. 서버 코드 내에서 직접 escape 처리를 해야 됨 -->
                <param name="query" useDefender="false" />        
            </params>
        </url-rule>
    </url-rule-set>
</config>
```

- global 파라메터에 대해 prefix로 시작하는 파라메터에 대해 필터링 제외 적용

```XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/request-param">
    <defenders>
        <defender>
            <name>preventer</name>
            <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssPreventerDefender</class>
        </defender>
    </defenders>
 
    <default>
        <defender>preventer</defender>
    </default>
 
    <global>
        <params>
            <param name="globalprefix3" usePrefix="true" useDefender="false" />
        </params>
    </global>
     
    <url-rule-set>
        <url-rule>
            <url disable="true">/disabletest1.nhn</url>
        </url-rule>
    </url-rule-set>
</config>
```

- 설정된 url 내의 모든 파라메터에 대해 필터링 제외 적용

```XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/request-param">
    <defenders>
        <defender>
            <name>preventer</name>
            <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssPreventerDefender</class>
        </defender>
    </defenders>
 
    <default>
        <defender>preventer</defender>
    </default>
 
    <global>
        <params>
            <param name="q" useDefender="false" />        
        </params>
    </global>
     
    <url-rule-set>
        <url-rule>
            <url disable="true">/disabletest1.nhn</url>
        </url-rule>
    </url-rule-set>
</config>
```

- 설정된 url과 prefix로 시작하는 파라메터에 대해 필터링 제외 적용

```XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/request-param">
    <defenders>
        <defender>
            <name>preventer</name>
            <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssPreventerDefender</class>
        </defender>
    </defenders>
 
    <default>
        <defender>preventer</defender>
    </default>
 
    <global>
        <params>
            <param name="q" useDefender="false" />        
        </params>
    </global>
     
    <url-rule-set>
        <url-rule>
        	<!--  prefix parameter 테스트 -->
            <url>/search.nhn</url>
            <params>
                <!-- prefix로 시작하는 모든 파라메터는 필터링 되지 않는다.  -->
                <param name="prefix" usePrefix="true" useDefender="false" /> 
            </params>
        </url-rule>
    </url-rule-set>
</config>
```

- 기본 파리메터 필터링 외에 추가로 컨텐츠 필터링을 적용
	
__컨텐츠 필터링을 사용하려면 lucy-xss-filter 라이브러리에 대한 이해가 필요하다.__

[lucy-xss-filter 문서](http://devcafe.nhncorp.com/index.php?mid=issuetracker&act=dispIssuetrackerDownload&vid=Lucy&package_srl=282220)

```XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/request-param">
    <defenders>
        <defender>
            <name>preventer</name>
            <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssPreventerDefender</class>
        </defender>
        
        <!--  Lucy XSS Dom Filter defender 등록 -->
        <defender>
            <!-- XSS Defender 사용 시에는 Lucy XSS Filter에 대한 기본 설정(lucy-xss-superset, lucy-xss.xml 정의 등)을 미리 해두어야 한다. -->
            <name>xss</name>
            <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssFilterDefender</class>
            <init-param>
            <!-- lucy-xss-filter의 dom filter용 설정 파일 -->
            	<param-value>lucy-xss.xml</param-value>
             <!-- 필터링된 코멘트를 남길지 여부 -->
                <param-value>true</param-value>
            </init-param>
        </defender>
        
        <!--  Lucy XSS Sax Filter defender 등록 -->
        <defender>
            <!-- XSS Sax Defender 사용 시에는 Lucy XSS Sax Filter에 대한 기본 설정(lucy-xss-superset-sax, lucy-xss-sax.xml 정의 등)을 미리 해두어야 한다. -->
            <name>xss_sax</name>
            <class>com.navercorp.lucy.security.xss.servletfilter.defender.XssSaxFilterDefender</class>
            <init-param>
             <!-- lucy-xss-filter의 sax filter용 설정 파일 -->
            	<param-value>lucy-xss-sax.xml</param-value>
             <!-- 필터링된 코멘트를 남길지 여부 -->			                
                <param-value>true</param-value>
            </init-param>
        </defender>
        <!--  Lucy XSS Sax Filter defender 등록 -->
    </defenders>
  
    <default>
        <defender>preventer</defender>
    </default>
  
  	<!-- xml에 설정한 <global>, <url-rule-set> 태그안에 설정된 정보가 필터링 선정 기준이 된다. -->
 	<!-- 서블릿 필터가 인자로 받은 parameter와 url이 xml에 설정한 필터링 선정 기준에 포함되지 않으면 아무런 작업도 수행하지 않는다. -->
    <global>
        <params>
            <param name="q" useDefender="false" />
        </params>
    </global>
      
    <url-rule-set>
        <url-rule>
            <url>/search.nhn</url>
            <params>
                <param name="query" useDefender="false" />
            </params>
        </url-rule>
        <url-rule>
            <url>/tlist/list.nhn</url>
            <params>
                <param name="listId" useDefender="false" />
                <param name="body">
                    <!--  Lucy XSS Dom Filter defender 사용 설정 -->
                    <defender>xss</defender>
                </param>
                <param name="body2">
                    <!--  Lucy XSS Sax Filter defender 사용 설정 -->
                    <defender>xss_sax</defender>
                </param>
            </params>
        </url-rule>
    </url-rule-set>
</config>
```


## 참고정보 
* Filtering 시점 
	URL 호출 시점이 아닌 서버코드에서 parameter 값을 획득하는 ServletRequest의 getParameter(), getParameterValues(), getParameterMap() 호출 시 filtering 진행

* URL Rule 미설정으로 인한 Debug값 확인 
	useDefender="false" 로 설정된 값에 대해 tomcat debug log가 출력됨 
```
2014-09-18 18:59:59 [DEBUG](XssEscapeFilter:62 ) Do not filtered Parameter. Request url: /search.nhn, Parameter name: query, Parameter value: 가>
2014-09-18 19:02:26 [DEBUG](XssEscapeFilter:62 ) Do not filtered Parameter. Request url: /list/list.nhn, Parameter name: listId, Parameter value: 2
```

## Rule 파일 XML 항목별 설명
|항목명                |         |        |          |           |           |속성명                |노출개수   |범위                   |기본값     |내용         |
|-----------|---------|--------|----------|-----------|-----------|-----------|-------|-----------|------|------------|
|config     |         |        |          |           |           |           |1      |           |      |Root Element|
|           |defenders|        |          |           |           |           |1      |           |      |Parameter 값을 변경할 때 사용할 defender 인스턴스의 집합|
|           |         |defender|          |           |           |           |1..n   |           |      |defender 인스턴스| 
|           |         |        |name      |           |           |           |1      |           |      |defender 의 이름 |
|           |         |        |class     |           |           |           |1      |           |      |defender 의 실제 class name |
|           |         |        |init-param|           |           |           |0..1   |           |      |defender 의 생성자 파라메터의 집합 |
|           |         |        |          |param-value|           |           |0..n   |           |      |defender 의 생성자 파라메터  |
|           |default  |        |          |           |           |           |1      |           |      |기본 설정값 |
|           |         |defender|          |           |           |           |1      |           |      |기본 defender 값, defenders > defender > name 값을 입력| 
|           |global   |        |          |           |           |           |0..1   |           |      |전체 설정에 적용되는 값 |
|           |         |params  |          |           |           |           |1      |           |      |전체 설정에 적용될 Parameter 값의 집합 | 
|           |         |        |param     |           |           |           |1..n   |           |      |전체 설정에 적용될 Parameter 값 |
|           |         |        |          |           |           |name       |1      |           |      |Request Parameter 명   |
|           |            |        |          |           |        |usePrefix  |0..1   |true, false|false |파라메터에 prefix 적용 여부|
|           |         |        |          |           |           |useDefender|0..1   |true, false|true  |defender 에 의한 입력값의 변조 여부, <br/><h6>false 로 설정 시에는 반드시 서버 코드 내에서 별도 escape 처리를 하도록 한다.</h6>|
|           |            |        |          |           |defender|           |0..1   |           |      |적용할 defender <br/>defenders > defender > name 값을 입력, 생략될 경우 default defender가 설정된다. |
|           |url-rule-set|        |          |           |        |           |1      |           |      |필터 적용 시 옵션을 설정할 URL Rule 의 집합  |
|           |            |url-rule|          |           |        |           |1..n   |           |      |필터 적용 시 옵션을 설정할 URL Rule | 
|           |            |        |url       |           |        |           |1      |           |      |옵션을 설정할 URL  |
|           |            |        |          |disable    |        |           |0..1   |true, false|false |url 내의 모든 파라메터를 disable 여부|
|           |            |        |params    |           |        |           |1      |           |      |옵션을 설정할 Parameter 값의 집합  |
|           |            |        |          |param      |        |           |0..n   |           |      |옵션을 설정할 Parameter  |
|           |            |        |          |           |        |name       |1      |           |      |Request Parameter 명   |
|           |            |        |          |           |        |usePrefix  |0..1   |true, false|false |파라메터에 prefix 적용 여부|
|           |            |        |          |           |        |useDefender|0..1   |true, false |true  |defender 에 의한 입력값의 변조 여부 <br/><h6>false 로 설정 시에는 반드시 서버 코드 내에서 별도 escape 처리를 하도록 한다.</h6>|
|           |            |        |          |           |defender|           |0..1   |           |      |적용할 defender <br/>defenders > defender > name 값을 입력, 생략할 경우 default defender가 설정된다. |

## FAQ
__Q: Global Params 값은 어떤 경우에 사용하면 되나요?__

_A: 옵션 설정이 필요한 Parameter 값 중 전체 서비스에서 사용되는 Parameter 값을 등록합니다. 예를 들어 모바일 기기에서 PC 페이지로 접근 시 해당 URL 에 일괄적으로 "mobile=Y" 와 같은 값을 붙여주고, interceptor 에서 항상 request.getParameter("mobile") 와 같이 호출하는 로직이 있다면 Global params 에 등록하면 됩니다._

__Q: URL은 대소문자를 구분하나요?__

_A: 네. URL는 대소문자를 구분합니다._

__Q: 이미 자체 escape 나 XSS Filter 적용 등으로 처리된 경우는 해당 코드를 모두 걷어내고 적용해야 하나요?__

_A: 가능하면 해당 코드를 걷어내고 XSS Request Param Filter로 일원화하는 것을 권장합니다. 기존에 적용된 코드량이 방대하여 걷어내는 리소스가 클 경우는 param rule 설정 시 useDefender 속성을 "false"로 설정하여 제외처리 하도록 합니다._

__Q: Defender에서 preventer 만 사용하는 경우에도 Lucy XSS Filter 가 모두 로딩되어 성능에 이슈가 되지는 않나요?__

_A: XSS Filter 는 getInstance() 메소드 호출 시 로딩이 되어 메모리에 설정 정보가 로딩되며, 실제 성능에 영향을 미치는 부분은 해당 파라메터의 값에 필터링을 적용하는 시점의 parsing 동작입니다. Preventer 만 사용하는 경우는 XSS Filter 를 로딩하지 않으며, preventer defender 에서 실제 사용하는 Lucy XssPreventer.escape 는 static 메소드로 apache commons 의 StringEscapeUtils.escapeHtml() 수준이라 성능에 큰 영향을 미치지 않습니다._

__Q: 적용하고 보니 오류가 발생합니다. Caused by: com/nhncorp/lucy/security/xss/XssPreventer java.lang.NoClassDefFoundError__

_A: 게시판 참조해주세요. http://yobi.navercorp.com/lucy-projects/lucy-xss-servlet-filter/post/4_

__Q: XssPreventerDefender와  XssSaxFilterDefender와 XssDomFilterDefender 중 어떤 걸 사용해야 하는지 잘 모르겠어요 ?__

_A: 먼저 필터링 대상 데이터가 컨텐츠 필터링이 필요한지 파라메터 필터링이 필요한지 판단하셔야 합니다. 사용자가 직접 생성한 html이 아니라면 XssSaxFilterDefender, XssDomFilterDefender를 사용할 일이 거의 없으며 XssPreventerDefender를 사용하시면 됩니다. 그리고 컨텐츠 필터링이 필요한 팀들을 인터뷰 해 본 결과 비즈니스의 성격으로 동일한 데이터라고 할 지라도 어떤 경우는 컨텐츠 필터링이 필요하고 어떤 경우는 필요하지 않는 상황이 발생하기 때문에 XssSaxFilterDefender, XssDomFilterDefender를 사용해 일괄적으로 필터링 규칙을 적용하는 방식에 거부감을 나타내었으며 기존 lucy-xss-filter 라이브러리를 직접 코드에서 사용하는 방식을 훨씬 선호하였습니다. 마지막으로 성능상의 이유로 XssSaxFilterDefender와 XssDomFilterDefender 중에서는  XssSaxFilterDefender를 선호합니다._

__Q: 해당 url 전체 파라미터에 필터링 하지 않을 수 있는 방법도 있나요 ?__

_A: 1.0.2 버전에 추가되었습니다. 적용방법에 설정된 xml을 참조해주세요_

__Q: lucy-xss-servlet-filter 적용 후 한글이 깨집니다.__

_A: lucy-xss-filter의 버전이 1.6.3 이상인지 확인해주시고, 계속 같은 증상이 반복된다면 링크를 확인해주세요 (http://devcafe.nhncorp.com/Lucy/forum/307071)_

__Q: XssPreventerDefender의 필터링 규칙이 궁금합니다.__

_A: http://devcafe.nhncorp.com/index.php?mid=forum&vid=Lucy&document_srl=2055777&rnd=2055779#comment_2055779_

__Q: 필터 적용 후 파일 업로드가 되지 않습니다.__

_A: http://devcafe.nhncorp.com/index.php?mid=forum&vid=Lucy&document_srl=2058874_

## 구조
- XSS Request Param Filter Structure
![1.png](/files/18078)

## 용어설명
- 파라메터 필터링
	html이 아닌 단순 텍스트 데이터에 대한 필터링
	lucy-xss-filter의 XssPreventer 라이브러리를 사용해 모든 xss 공격 위험요소를  필터링
	
- 컨텐츠 필터링 
	html로 작성된  데이터에 대한 필터링, 주로 사용자가 html을 작성하여 생성된 컨텐츠에 사용됨 
	lucy-xss-filter의 XssFilter 라이브러리를 사용해  설정파일(lucy-xss, lucy-xss-sax.xml) 기준으로 xss 공격 위험요소를  필터링
	ex) 지식인, 메일, 카페 게시판에서 사용 
