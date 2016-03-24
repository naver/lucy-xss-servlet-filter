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

## Xml Config attribute 

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


## Xml Config All examples

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

    <!-- default defender 선언, 필터링 시 지정한 defender가 없으면 여기 정의된 default defender를 사용해 필터링 한다. -->
    <default>
        <defender>xssPreventerDefender</defender>
    </default>

    <!-- global 필터링 룰 선언 -->
    <global>
        <!-- 모든 url에서 들어오는 globalParameter 파라메터는 필터링 되지 않으며
                또한 globalPrefixParameter1로 시작하는 파라메터도 필터링 되지 않는다.
                globalPrefixParameter2는 필터링 되며 globalPrefixParameter3은 필터링 되지 않지만
                더 정확한 표현이 가능하므로 globalPrefixParameter2, globalPrefixParameter3과 같은 불분명한 표현은 사용하지 않는 것이 좋다. -->
        <params>
            <param name="globalParameter" useDefender="false" />
            <param name="globalPrefixParameter1" usePrefix="true" useDefender="false" />
            <param name="globalPrefixParameter2" usePrefix="true" />
            <param name="globalPrefixParameter3" usePrefix="false" useDefender="false" />
        </params>
    </global>

    <!-- url 별 필터링 룰 선언 -->
    <url-rule-set>

        <!-- url disable이 true이면 지정한 url 내의 모든 파라메터는 필터링 되지 않는다. -->
        <url-rule>
            <url disable="true">/disableUrl1.do</url>
        </url-rule>

        <!-- url disable이 false인 설정은 기본이기 때문에 불필요하다. 아래와 같은 불필요한 설정은 하지 않는다.-->
        <url-rule>
            <url disable="false">/disableUrl2.do</url>
        </url-rule>

        <!-- url disable이 true이면 지정한 url 내의 모든 파라메터가 필터링 되지 않기 때문에 <params> 로 선언한 설정은 적용되지 않는다. 
               아래와 같은 불필요한 설정은 하지 않는다. -->
        <url-rule>
            <url disable="true">/disableUrl3.do</url>
            <params>
                <param name="query" useDefender="false" />
                <param name="prefix1" usePrefix="true" />
                <param name="prefix2" usePrefix="false" useDefender="false" />
                <param name="prefix3" usePrefix="true" useDefender="true" />
                <param name="prefix4" usePrefix="true" useDefender="false" />
                <param name="prefix5" usePrefix="false" useDefender="true" />
            </params>
        </url-rule>

        <!-- url disable이 false인 설정은 기본이기 때문에 불필요하다. <params> 선언한 설정은 적용이 된다.-->
        <url-rule>
            <url disable="false">/disableUrl4.do</url>
            <params>
                <!-- disableUrl4.do 의 query 파라메터와 prefix4로 시작하는 파라메터들은 필터링 되지 않는다. 
                        usePrefix가 false, useDefender가 true인 설정은 기본이기 때문에 불필요하다. -->
                <param name="query" useDefender="false" />   
                <param name="prefix1" usePrefix="true" />
                <param name="prefix2" usePrefix="false" useDefender="false" />
                <param name="prefix3" usePrefix="true" useDefender="true" />
                <param name="prefix4" usePrefix="true" useDefender="false" />
                <param name="prefix5" usePrefix="false" useDefender="true" />
            </params>
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

## Recommendation

신규로 개발하는 서비스에는 lucy-xss-servlet-filter를 사용하는 것을 추천하지만, 
기존 잘 운영되는 시스템에 lucy-xss-servlet-filter를 사용하는 것은 추천하지 않습니다. 
입력 파라메터가 전부 필터링 되기 때문에 서비스가 잘 동작하지 않는 의도치 않은 결과가 발생할 수 있기 때문입니다.

기존 시스템에 적용하시려면 [lucy-xss-filter](https://github.com/naver/lucy-xss-filter)를 사용해 개발자가 일일히 필터링 하는 방식을 추천드립니다. 
하지만 이 방법은 개발자의 집중력를 믿어야 하기 때문에 언젠가는 뚫릴 수 밖에 없는 방식입니다. 
그렇기 때문에 시간을 충분히 확보하실 수 있으시거나 기존에 서블릿 필터 기반으로 XSS 공격을 방어하셨다면 
이번 기회에 lucy-xss-servlet-filter를 적용해 XSS 공격에서 해방되시는 걸 추천드립니다.   

## FAQ

__Q: global params 값은 어떤 경우에 사용하면 되나요?__

_A: url에 상관없이 필터링 하지 말아야 할 파라메터 명을 정합니다._ 

__Q: url은 대소문자를 구분하나요?__

_A: 네. 대소문자를 구분합니다._

__Q: 이미 lucy-xss-filter 를 적용 중인데 이 경우에도 lucy-xss-servlet-filter를 적용해야 하나요?__

_A: 촛점을 어디에 두냐 차이이겠지만, 초기에 한번 적용하고 xss 공격에 해방되길 원하신다면 기존 방어코드를 제거하고 lucy-xss-servlet-fitler로 일원화하는 것을 권장합니다._

__Q: 해당 url 전체 파라미터에 필터링 하지 않을 수 있는 방법도 있나요 ?__

_A: 네 disableUrl 설정이 있습니다._ 

__Q: lucy-xss-servlet-filter 적용 후 한글이 깨집니다.__

_A: lucy-xss-filter의 버전이 1.6.3 이상인지 확인해주세요_

__Q: Filtering 시점은 언제인가요 ?__ 

-A: URL 호출 시점이 아닌 서버코드에서 parameter 값을 획득하는 ServletRequest의 getParameter(), getParameterValues(), getParameterMap() 호출 시 필터링을 진행합니다._

__Q: XssPreventerDefender의 필터링 규칙이 궁금합니다.__

_A: [HTML entities](https://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/StringEscapeUtils.html#escapeHtml4%28java.lang.String%29) 를 기본적으로 사용하며 "'" → "&#39"; 필터링 규칙을 추가하였습니다._

__Q: XssPreventerDefender, XssSaxFilterDefender, XssFilterDefender 중 어떤 걸 사용해야 하는지 잘 모르겠습니다.__

_A: 위 세 가지 클래스는 lucy-xss-filter의 api를 호출하며 XssPreventerDefender는 XssPreventer를, XssSaxFilterDefender는 XssSaxFilter를, XssFilterDefender는 XssFilter를 사용합니다. 상황별 api 사용 기준은 다음 링크를 참조한다. [selection criterion.md](https://github.com/naver/lucy-xss-filter/blob/master/docs/manual/kr/01.%20summary/1.3%20selection%20criterion.md)_
