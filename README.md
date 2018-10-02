# AroundMe_SpringBoot
#### 기존 AroundMe의 서버사이드(Nodejs+Mongodb)를 SpringBoot + Mysql로 바꾸어 구현한 프로젝트 입니다.

### Server : SpringBoot2.0

### Database : Mysql + JPA

### View : Thymeleaf

## 2018.10.01 Developing Note
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

## 2018.10.02 Developing Note
* * *

#### 1. Security사용, 비밀번호 암호화 설정.
> Bean 설정때문에 엄청 애먹었었음. Bean설정을 한 후에도, 모든 페이지애 lock이 걸려 지치고 지쳐 슬퍼하다가 
그만 뒀었음... 하지만 해결. bean 설정 하는법 : Config파일 하나 생성후, @Configuration, @EnableWebSecurity어노테이션 추가.
```
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers("/**");
        web.ignoring().antMatchers("/resources/**");

    }
    @Bean
    public BCryptPasswordEncoder BCryptPasswordEncoder(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }


}

```
이와 같이 WebSecurityConfigurerAdapter를 상속받는 클래스를 하나 생성하여 , BCryptPasswordEncoder를 Bean으로 등록하여 사용한다.
비밀번호 암호화 해결. 정상적으로 hash화 돼서 db에 저장되었다.

#### 2. 정적 리소스 참조 문제.
> 어제 정말 오래 걸린 정적 리소스 설정... 오늘 겨우겨우 Security를 입히고 돌려보니 정적 리소스를 받아오지 못하고 있었다. 2시간정도 삽질하다 어제 push한 프로젝트
와 비교해보니.. 내가 무언가를 지웠었다. 지운걸 모른채로 다시 설정해보려고 여기저기 구글링 해서 이것저것 추가 했는데 왜 안됐었는지 모르겠다.. 결론은
어제 작성한 코드를 참고하여 해결하였다.
```
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/webjars/**",
                "/img/**",
                "/css/**",
                "/js/**")
                .addResourceLocations(
                        "classpath:/META-INF/resources/webjars/",
                        "classpath:/static/img/",
                        "classpath:/static/html/",
                        "classpath:/static/css/",
                        "classpath:/static/js/");
    }

}
```