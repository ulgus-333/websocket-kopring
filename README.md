### 프로젝트 개요
> 기존 Java + Stomp를 통해 기본적인 기능에 대한 연습 과정에 이어서 Kotlin + WebSocket을 통한 채팅 서버 구현을 목적으로 한다.
> 단일 서버가 아닌 멀티모듈을 통한 모로레포 형식으로 구현하여 각 역할을 분리하여 구현할 수 있도록 한다.

### Stack
> Java 17 (Kotlin)\
> SpringBoot 3.5.9\
> Plain WebSocket\
> Mysql\
> Redis\
> JPA\
> OCI\
> OAuth2\
> Spring Security\

### 특이사항
- Client View에는 크게 중요성을 두지 않고 정상동작을 확인하기 위한 목적으로 가볍게 구현되었으며, 기능의 연결에 대한 기본적인 수정작업 외에 틀을 잡거나, 전체적인 코드 구성은 Gemini를 통해 구축
- MySQL, OCI, OAuth2 등 민감 접속정보는 public repository이기 때문에 로컬에서 .env 파일 구성 후 서비스 실행시 environment에 주입해서 사용
- 2026년 1월 toy 프로젝트 종료
  - 선언해두었던 issue 및 부가적으로 생각했던 기능은 나중에 서비스 사이드프로젝트에서 실행
  - 관련 사이드 프로젝트
    - 점술 채팅 프로그램
      - AI : 사용자 간 1 : N  구조의 채팅 프로그램
        - 1:1 -> 개인 운세
        - 1:N -> 궁합 등
