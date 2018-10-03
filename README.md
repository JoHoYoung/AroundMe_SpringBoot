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

## 2018.10.03 Developing Note
* * *

#### 1. PostShema 논리적설계, 다중값 속성, 정규화에 대Issue
>  Mongodb로 개발 할때는 PostSchema에서 댓글 속성이 다중값 속성이었다. 게시글 스키마의 댓글 속성에는
그 게시글에 달린 댓글이 모두 다중 값 속성으로 들어갔었는데 , DB시간에 다중값 속성은 릴레이션으로 변환 하라고 배웠던 것이 기억났다.
원래속한 릴레이션의 기본키를 외래키로 가져오라고 했던것 같은데..  PostSchema의 기본키는 Autoincrement값으로 부여하고, 그 값을 댓글
스키마의 외래키로 가져오자. 댓글 스키마의 기본키도 Auto Incremenet , 각 게시글에 속하는 이미지도 릴레이션으로 변환하여 기본키는 이미지 이름, 속성은 속한 글 id,
추천인도 같은 구조로 설계.
> 게시글을 속할 댓글, 이미지, 추천인은 belongsto속성을 post_chema의 id속성을 참조하며, delete on cascade, update on noaction으로 설정하자.
게시글이 삭제되면 그에 속한 이미지, 댓글, 추천인은 같이 사라지는게 맞으므로 적절한 설정이다.
>기본키, 외래키는 text로 설정하니 에러가 났다. 길이를 정확히 정해줘야 하는듯 하다.
> Entity에 @OneToMany, @ManyToOne 설정.

#### 전체적인 DB Relation 구조
<img width="1437" alt="2018-10-03 4 13 06" src="https://user-images.githubusercontent.com/37579650/46405625-c401f900-c743-11e8-9830-57a44d09cd0f.png">

####Query

<img width="574" alt="comment" src="https://user-images.githubusercontent.com/37579650/46405627-c401f900-c743-11e8-98f4-972b2a7f83be.png">
<img width="574" alt="image" src="https://user-images.githubusercontent.com/37579650/46405629-c401f900-c743-11e8-8eee-4a0031a010eb.png">
<img width="571" alt="post" src="https://user-images.githubusercontent.com/37579650/46405631-c49a8f80-c743-11e8-8832-c41ce72a3b6e.png">
<img width="571" alt="recommender" src="https://user-images.githubusercontent.com/37579650/46405632-c49a8f80-c743-11e8-9b9a-a5ddfc564991.png">

#### 2. 그에따른 Entity, Repository 생성
> 전에 연습했던것과 달리 이번에 만든 Entity들은 구조, 관계가 있으므로 어노테이션 지정. Post의 경우 @OneToMany, comment,recommender,image의 경우 @ManyToOne으로 지정

#### 3. lombok 사용.
> Entity들을 정의하다보니 get, set함수를 하나하나 지정하는것이 번거로웠다. 알아보니 lombok을 사용하면 된다고 한다. lombok plugin추가 후 @Getter, @Setter
어노테이션을 통해 자동으로 get, set 함수 생
```

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class image_schema {
....
}
```

#### 4. @ManyToOne, @OneToMany 지정에 따른 CRUD 구조 수정
> 전에 짜놓았던 Multipart Data 업로드 Controller를 Entitity, Repository에 적용하여 기본적인 CRUD를 구현하였다. JPA의 save함수는 Entity를 리턴하므로,
 리턴 값을 이용해 ManyToOne의 외래키 속성에 값 설정, 저장, 튜플추가. 과정은 1. 게시글 튜플을 먼저 추가한다. 2 게시글 튜플의 id값을 가져와 이미지의 외래키 값으로 지정해준다. 
 3. 게시글 튜플을 이미지 Entity의 @ManyToOne 속성인 Post에 Set함수를 사용하여 넣어준다. 이미지 하나를 로컬에 업로드 할때마다 Post Entitiy에 지정한 함수 addImage를 이용해
  Post Entity의 @OneToMany 속성인 List<image_schema> image 속성에 추가 해준다.
 ```
   public void addComment(comment_schema data){
         if( comment == null ){
             comment = new ArrayList<comment_schema>();
         }
         comment.add(data);
     }
 
     public void addImage(image_schema data){
         if( image == null ){
             image = new ArrayList<image_schema>();
         }
         image.add(data);
     }
 
     public void addRecommender(recommender_schema data){
         if( recommender == null ){
             recommender = new ArrayList<recommender_schema>();
         }
         recommender.add(data);
     }

 ```