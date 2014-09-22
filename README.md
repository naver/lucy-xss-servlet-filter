## 개요

기존에는 아래의 사유로 XSS 공격 방어가 누락되거나 비효율적으로 적용되고 있음
- 별도의 preventer 를 구현하여 Controller / BO 코드 내에 적용할 경우, 기능 추가 시 XSS 공격 방어 체크를 누락하여 보안에 허점 발생
- 전체 요청에 대해 용도에 벗어난 XSS Filter 를 적용하여 서비스 성능에 저하가 발생할 수 있으며, White List 방식으로 보안 허점 발생 가능성이 여전히 존재

그래서 서비스 내 URL별 요청 Parameter에 대해 기본으로 모든 태그를 무력화하는 Preventer를 적용하고, 특정 paramater에는 필요에 따라 Preventer를 적용하지 않거나 XSS Filter를 일관된 방식으로 적용할 수 있는 설정 방식을 제공하고자 함

