## 개요

기존에는 아래의 사유로 XSS 공격 방어가 누락되거나 비효율적으로 적용되고 있음
- 별도의 preventer 를 구현하여 Controller / BO 코드 내에 적용할 경우, 기능 추가 시 XSS 공격 방어 체크를 누락하여 보안에 허점 발생
- 전체 요청에 대해 용도에 벗어난 XSS Filter 를 적용하여 서비스 성능에 저하가 발생할 수 있으며, White List 방식으로 보안 허점 발생 가능성이 여전히 존재

그래서 서비스 내 URL별 요청 Parameter에 대해 기본으로 모든 태그를 무력화하는 Preventer를 적용하고, 특정 paramater에는 필요에 따라 Preventer를 적용하지 않거나 XSS Filter를 일관된 방식으로 적용할 수 있는 설정 방식을 제공하고자 함

## 구조
- XSS Request Param Filter Structure
![1.png](/files/18078)

## 적용방법 
1. Dependency 설정
``` XML
<dependency>
    <groupId>com.naver.service</groupId>
    <artifactId>request-param-filter</artifactId>
    <version>1.0.0</version>
</dependency>
```

2. Servlet Filter 설정
``` XML
<filter>
    <filter-name>requestParamFilter</filter-name>
    <filter-class>com.naver.service.filter.requestparam.RequestParamFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>requestParamFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
__주의 : requestParamFilter는 encoding 필터 뒤에 위치해야 합니다.__

3. 기본 Rule 파일 설정 예제
- __컨텐츠 필터링을 하지 않고 파라메터 필터링만 수행한다면 기본 XssPreventerDefender만 설정한다.__
- resource 폴더 내에 "request-param-filter-rule.xml" 파일을 생성
- XML 각 항목에 대한 설명은 "Rule 파일 XML 항목별 설명"을 참고한다.    
``` XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/request-param">
    <defenders>
        <defender>
            <name>preventer</name>
            <class>com.naver.service.filter.requestparam.defender.XssPreventerDefender</class>
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

- 특정 Parameter에만 XSS FIlter(컨텐츠 필터링)를 적용하는 Rule 파일 설정 예제
``` XML
<?xml version="1.0" encoding="UTF-8"?>
<config xmlns="http://www.navercorp.com/request-param">
    <defenders>
        <defender>
            <name>preventer</name>
            <class>com.naver.service.filter.requestparam.defender.XssPreventerDefender</class>
        </defender>
        <!-- // Lucy XSS Filter defender 등록 -->
        <defender>
            <!-- XSS Defender 사용 시에는 Lucy XSS Filter에 대한 기본 설정(lucy-xss-superset.xml 정의 등)을 미리 해두어야 한다. -->
            <name>xss</name>
            <class>com.naver.service.filter.requestparam.defender.XssFilterDefender</class>
            <init-param>
                <param-value>true</param-value>
            </init-param>
        </defender>
        <!-- // Lucy XSS Filter defender 등록 -->
        <!-- // Lucy XSS Sax Filter defender 등록 -->
        <defender>
            <!-- XSS Sax Defender 사용 시에는 Lucy XSS Sax Filter에 대한 기본 설정(lucy-xss-superset-sax.xml 정의 등)을 미리 해두어야 한다. -->
            <name>xss_sax</name>
            <class>com.naver.service.filter.requestparam.defender.XssSaxFilterDefender</class>
            <init-param>
                <param-value>true</param-value>
            </init-param>
        </defender>
        <!-- // Lucy XSS Sax Filter defender 등록 -->
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
                    <!-- // Lucy XSS Filter defender 사용 설정 -->
                    <defender>xss</defender>
                    <!-- // Lucy XSS Filter defender 사용 설정 -->
                </param>
                <param name="body2">
                    <!-- // Lucy XSS Sax Filter defender 사용 설정 -->
                    <defender>xss_sax</defender>
                    <!-- // Lucy XSS Sax Filter defender 사용 설정 -->
                </param>
            </params>
        </url-rule>
    </url-rule-set>
</config>
```

## 주의사항
* 파라메터 필터링과 컨텐츠 필터링의 두 가지 전부 적용을 고려한다면  아래 링크에서 웹플랫폼 검토의견을 참고한다.
 	(http://yobi.navercorp.com/lucy-projects/lucy-request-param-filter/issue/10)
* web.xml 내에 filter-mapping 선언 시 전체가 아닌 특정 url 만 등록하거나, RequestParamFilter를 Copy 및 재구현하여 특정 url 만 타도록 하는 등 예외를 두지 않도록 한다.
* global params에 서비스 전체에서 사용되는 공통 파라메터 값이 아닌, 서비스되는 URL의 모든 파라메터 값을 useDefender = "false" 로 넣지 않도록 한다.

## 참고정보 
* Filtering 시점 
	URL 호출 시점이 아닌 서버코드에서 parameter 값을 획득하는 ServletRequest의 getParameter(), getParameterValues(), getParameterMap() 호출 시 filtering 진행

* URL Rule 미설정으로 인한 Debug값 확인 
	useDefender="false" 로 설정된 값에 대해 tomcat debug log가 출력됨 
```
2014-09-18 18:59:59 [DEBUG](RequestParamChecker:62 ) Do not filtered Parameter. Request url: /search.nhn, Parameter name: query, Parameter value: 가>
2014-09-18 19:02:26 [DEBUG](RequestParamChecker:62 ) Do not filtered Parameter. Request url: /list/list.nhn, Parameter name: listId, Parameter value: 2
```

## Rule 파일 XML 항목별 설명
|항목명      |         |        |          |           |           |속성명      |노출개수|범위        |기본값|내용         |
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
|           |         |        |          |           |           |useDefender|0..1   |true, false|true  |defender 에 의한 입력값의 변조 여부, <br/><h6>false 로 설정 시에는 반드시 서버 코드 내에서 별도 escape 처리를 하도록 한다.</h6>|
|           |            |        |          |           |defender|           |0..1   |           |      |적용할 defender <br/>defenders > defender > name 값을 입력, 생략될 경우 default defender가 설정된다. |
|           |url-rule-set|        |          |           |        |           |1      |           |      |필터 적용 시 옵션을 설정할 URL Rule 의 집합  |
|           |            |url-rule|          |           |        |           |1..n   |           |      |필터 적용 시 옵션을 설정할 URL Rule | 
|           |            |        |url       |           |        |           |1      |           |      |옵션을 설정할 URL  |
|           |            |        |params    |           |        |           |1      |           |      |옵션을 설정할 Parameter 값의 집합  |
|           |            |        |          |param      |        |           |0..n   |           |      |옵션을 설정할 Parameter  |
|           |            |        |          |           |        |name       |1      |           |      |Request Parameter 명   |
|           |            |        |          |           |        |useDefender|0..1   |true, false |true  |defender 에 의한 입력값의 변조 여부 <br/><h6>false 로 설정 시에는 반드시 서버 코드 내에서 별도 escape 처리를 하도록 한다.</h6>|
|           |            |        |          |           |defender|           |0..1   |           |      |적용할 defender <br/>defenders > defender > name 값을 입력, 생략할 경우 default defender가 설정된다. |

## FAQ
__Q: Global Params 값은 어떤 경우에 사용하면 되나요?__
_A: 옵션 설정이 필요한 Parameter 값 중 전체 서비스에서 사용되는 Parameter 값을 등록합니다. 
       예를 들어 모바일 기기에서 PC 페이지로 접근 시 해당 URL 에 일괄적으로 "mobile=Y" 와 같은 값을 붙여주고, 
   interceptor 에서 항상 request.getParameter("mobile") 와 같이 호출하는 로직이 있다면 Global params 에 등록하면 됩니다._

__Q: URL은 대소문자를 구분하나요?__
_A: 네. URL는 대소문자를 구분합니다._

__Q: 이미 자체 escape 나 XSS Filter 적용 등으로 처리된 경우는 해당 코드를 모두 걷어내고 적용해야 하나요?__
_A: 가능하면 해당 코드를 걷어내고 XSS Request Param Filter로 일원화하는 것을 권장합니다. 
       기존에 적용된 코드량이 방대하여 걷어내는 리소스가 클 경우는 param rule 설정 시 useDefender 속성을 "false"로 설정하여 제외처리 하도록 합니다._

__Q: Defender에서 preventer 만 사용하는 경우에도 Lucy XSS Filter 가 모두 로딩되어 성능에 이슈가 되지는 않나요?__
_A: XSS Filter 는 getInstance() 메소드 호출 시 로딩이 되어 메모리에 설정 정보가 로딩되며, 실제 성능에 영향을 미치는 부분은 해당 파라메터의 값에 필터링을 적용하는 시점의
   parsing 동작입니다. Preventer 만 사용하는 경우는 XSS Filter 를 로딩하지 않으며, preventer defender 에서 실제 사용하는 Lucy XssPreventer.escape 는
   static 메소드로 apache commons 의 StringEscapeUtils.escapeHtml() 수준이라 성능에 큰 영향을 미치지 않습니다._
