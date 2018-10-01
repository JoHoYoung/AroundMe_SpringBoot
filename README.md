# AroundMe_SpringBoot
#### 기존 AroundMe의 서버사이드(Nodejs+Mongodb)를 SpringBoot + Mysql로 바꾸어 구현한 프로젝트 입니다.

### Server : SpringBoot2.0

### Database : Mysql + JPA

### View : Thymeleaf

## 2018.07.04 Developing Note
* * *

#### 1. 회원가입 스키마 Mysql적용
> 기존 Mongodb에서 사용하던 스키마를 Mysql에 적용, JPA를 통한 기본적인 기능 구현 Updata의 경우 @Modifying 어노테이션만 추가해주면 되는 줄 알았는데 @Transactional 어노테이션도 추가 해 줘야함.

#### 2. 회원가입 라우트 정의, 라우트에 따른 인증절차 구현
> 인증이 된 계정만 로그인 가능, 회원가입시 입력한 정보를 받아 Mysql에 추가. save()메소드 사용.

#### 3. Express에 NodeMailer를 JavaMailSender,MimeMessageHelper 를 사용하여 구현.
> NodeMailer보다 사용하기 편했다. 메일인증 절차를 위한 메일전송이 성공적으로 동작 하였으며, Nodejs와 달리 Html메일을 보내기 위해서는 MimeMessage라는 것을
사용해야 했다.

#### 4. 회원가입시(인증 전)토큰 부여, 회원가입 후 토큰, 아이디가 포함된 링크 메일을 보내서 인증 구현.
> @Transactional 어노테이션을 추가한 사용자 정의 @Query함수로 아이디를 찾아 특정 칼럼(auth)의 값(-1)을 바꾸는데 성공 했다(1로바꿈.)
이로서 이메일 인증기능 구현 완료.

#### 5. 비밀번호 암호화 실패.
> 단방향 암호와를 위해 Security에 내장된 함수를 사용하려 했으나 Security를 사용하는 법이 너무 어려워 실패하였다. 6시간정도 읽었는데 설정등이 복잡하고, 함수를 사용하는 것에는
성공하였으나 모든 페이지가 의문의 Login html로 잠기고 말았다. 일주일정도 시간을 투자하여 Security에 대해서 철저히 공부를 해야겠다. 어차피 Facebook OAuth도 구현해야 하니 필요한
내용이라 생각된다.
