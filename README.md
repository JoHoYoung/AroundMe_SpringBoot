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

#### Query

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
> 전에 짜놓았던 Multipart Data 업로드 Controller를 Entitity, Repository에 적용하여 기본적인 CRUD(Create, Read)를 구현하였다. JPA의 save함수는 Entity를 리턴하므로,
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
 
 ## 2018.10.04 Developing Note
 * * *
 
 #### 1. CRUD(Update)에서의 JPA. 속성의 값을 업데이트 하는 방법
```
variable = userRepository.find....("update%");
variable.set...(parameter)
userRepository.save(variable)
```
게시글, 제목 등은 이런식으로 업데이트 하고, 유저가 새로 업로드한 사진은 DB에 추가, 기존에서 삭제할 사진은 삭제한다.
삭제할 사진들은 프론트에서 사진의 인덱스를 배열로 받아오고, DB에서 삭제한다.

값, 배열값 받아오기
```
req.getParameter("") -> 이 함수의 경우 return 값 String이다.
req.gerParameterValues("")->이 함수의 경우 return 값이 String[]이기 때문에 배열값을 받아 올 수 있다
```
자바스크립트의 splice함수는 자바에서 List를 다룰때 List.delete함수와 기능이 같다고 한다. 이것을 이용해서 실행한다.
Splice를 하면 삭제한 원소 뒤의 원소들의 인덱스가 한칸씩 당겨지므로 내림차순으로 정렬하여 큰 index에 있는것부터 삭제한다.
삭제할 정보가 들어있는 배열을 받아와서 List로 변환 Collection.sort를 하기 위함
```
        List<Integer> todelete=new ArrayList(Arrays.asList(data));
        Ascending ascending = new Ascending();
        Collections.sort(todelete,ascending);
```
Collection.sort 하기 위해서는 Comparator 인터페이스를 상속받는 클래스 , compare함수를 오름, 내림차순 용도에 따라 정의해야함.
```
class Ascending implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }

    }
```
그 후에 정의한 클래스를 Collection.sort의 매개변수로 정렬할 Collection과 함께 전달.

#### 2. Create 부분 부분적 실패.
> 이미지 파일 서버 업로드 , 글 업로드는 성공했으나, db에 Mapping이 되지 않는다. @ManyToOne 설정이나.. 외래키 관련 문제인듯 하다. 하.. 이게 대채 무슨 문제일까.
```
 Caused by: java.sql.SQLSyntaxErrorException: Unknown column 'image_sche0_1_.post_id' in 'field list'
```
전체적인 논리구조, 흐름은 문제가 없다. JPA부분을 제외하면 새 글 업로드가 모두 정상적으로 작동하였다. 이 부분은 나중에 수정하도록하고 성공한 것에대해 정리해야겠다.

#### 3. Create의 흐름.
```
@RequestMapping(value = "/post/create", method = RequestMethod.POST)
    public String CreatePost(Model model, HttpServletRequest req, HttpSession session, @RequestParam("userimage") List<MultipartFile> files) throws IOException {
        try {
            String paramtitle=req.getParameter("title");
            String paramcontent=req.getParameter("content");
            long time = System.currentTimeMillis();
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            String createdat = dayTime.format(new Date(time));
            String writer="hoyoung";

            post_schema parents = PostRepository.save(new post_schema(paramtitle,paramcontent,writer,0,"","",0,0,0,0));

            for(int i=0;i<files.size();i++){
                String sourceFileName = writer;
                File destinationFile;
                String destinationFileName;
                String fileUrl = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/";


                do {
                    destinationFileName = RandomStringUtils.randomAlphanumeric(32) + sourceFileName;
                    destinationFile = new File(fileUrl + destinationFileName);

                    post_schema parents = PostRepository.save(new post_schema(paramtitle,paramcontent,writer,0,"","",0,0,0,0));

                    image_schema image=ImageRepository.save(new image_schema(destinationFileName,parents.getId(),parents));
                    parents.addImage(image);
                } while (destinationFile.exists());

                destinationFile.getParentFile().mkdirs();
                files.get(i).transferTo(destinationFile);

            }

            return "login";
//            return new ModelAndView(new RedirectView("/post/"+parents.getId(),true));
        } catch (Exception e) {
            e.printStackTrace();
            return "login";
  //          return new ModelAndView(new RedirectView("/main",true));
        }

    }
```
데이터, 사진을 받고 그저 저장할 뿐 어려운 점은 없었다.

#### 4.Read의 흐름.
> Read는 너무 쉬워서 말할것도 없었다. 그냥 JPA로 검색하여 프론트로 Parsing

#### 5. Update의 흐름.
```
@RequestMapping(value="/post/update/:postroot", method=RequestMethod.POST)
    public ModelAndView UpdatePost(Model model,HttpSession session ,@PathVariable(name="postroot")String postroot, @RequestParam("userimage") List<MultipartFile> files, HttpServletRequest req) throws IOException
    {

        String paramtitle=req.getParameter("title");
        String paramcontent=req.getParameter("content");
        String[] data = req.getParameterValues("todelete");


        post_schema post = PostRepository.findByid("postroot");
        List<image_schema> images=post.getImage(); //글에 속한 사진을 가져옴.

        List<Integer> todelete=new ArrayList(Arrays.asList(data));
        Dscending ascending = new Dscending();
        Collections.sort(todelete,ascending);
        for(int i=0;i<todelete.size();i++)
        {
            //서버에 저장된 이미지파일 삭제하는 부분

            String path = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/"; // 삭제할 파일의 경로
            String filepath=path+images.get(todelete.get(i)).getImagename();

            File file = new File(filepath);
            if(file.exists()){
                    file.delete();
            }
             // DB에 저장된 이미지 삭제하는 부분.
            ImageRepository.deleteByImagename(images.get(todelete.get(i)).getImagename());
            images.remove(todelete.get(i));
        }

        //Update하며 새로 업로드한 사진 저장하는 부분.
        for(int i=0;i<files.size();i++){
            String sourceFileName = session.getAttribute("user").toString();
            File destinationFile;
            String destinationFileName;
            String fileUrl = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/";


            do {
                destinationFileName = RandomStringUtils.randomAlphanumeric(32) + sourceFileName;
                destinationFile = new File(fileUrl + destinationFileName);
                image_schema image=ImageRepository.save(new image_schema(destinationFileName,post.getId(),post));
                post.addImage(image);
            } while (destinationFile.exists());

            destinationFile.getParentFile().mkdirs();
            files.get(i).transferTo(destinationFile);
        }

        post.setTitle(paramtitle);
        post.setContent(paramcontent);
        PostRepository.save(post);

        return new ModelAndView(new RedirectView("/post/"+post.getId(),true));
    }
```
> 프론트단은 js, jquery로 사용자가 글 수정시 기존에 삭제할 이미지들의 index를 배열로 받아오는 것을 만들어 두었다. 첫째로 삭제할 이미지들을 서버에서 파일삭제한다.
댓글, 추천인, 사진은 db에서 on delete cascade 설정을 걸어두어서 따로 삭제할 필요는 없어 보인다. 서버에 있는 이미지만 삭제하면 끝. 새롭게 추가할 사진은 기존 post Entitiy에 추가,
사진도 db에 추가. 내가 짠 코드의 가장 핵심은 삭제할 이미지들의 인덱스 정보를 배열로 받아와 내림차순 정렬 하는것에 있다. 자바스크립트의 splice와 같은 능을 하는 List.remove
 이는 인덱스를 앞으로 한칸씩 당겨버리기 때문에 정렬없이 삭제하면 문제가 생긴다. 사용자가 가장 나중에 있는 사진부터 클릭하여 삭제하기를 요청하는 것이 아니기 때문에, 정렬 없이 삭제하면
 없는 파일을 삭제요청하는 에러가 발생한다. 내림차순으로 정렬하여 그 사진부터 삭제하면, 중간에 빈 파일 없이 정상적으로 삭제가 가능하다. Collection.Sort를 위해 클래스를 정의하여 사용.
 
#### 6. Delete의 흐름
> Update보다 쉬운 flow라서 별로 어렵지 않았다.

#### 7. 지금 연관관계, 왜래키 부분에서 오류가 발생한다.
> 무언가 크게 잘못된것 같다. 이부분만 고치면 내 코드의 흐름은 틀린것이 없다고 생각한다. 빨리 공부해서 고쳐야 겠다. 프론트단도 코딩해야 하는데 프로젝트에 많은 시간이 필요할것 같다.

 ## 2018.10.04 Developing Note
 * * *
 
 #### 1. JPA를 수정해서 글작성, 이미지 저장기능 정상화
 > 오류가 많았던 이유는 관계를 양방향으로 유지해서 복잡했기 때문이다. 사진에서 글로의 ManyToOne은 필요없어도 된다고 생각이 들어 제거하고, 글에서 사진으로의 OneToMany만 설정하였다.
 
 #### 2. 기능은 정상화 되었지만 DB에 외래키 제약조건을 삭제함
 > 기능을 수정하고자 이것저것 고치니 외래키 제약조건 때문에 정상적으로 작동하지 않았다. 그래서 외래키 제약조건을 삭제하였다. 제약조건을 유지하고 업로드를 성공하고싶다. JPA책을 사서 공부를 한 다음 구현을 꼭 성공해야 겠다.
 
 #### 3. 기능은 정상화 되었지만 DB에 제대로 저장되지 않음
 > 어떤글이 어떤 사진을 가지고 있는지, 사진들은 어떤글에 속해있는지 DB에 제대로 저장되지 않는다. 정말 머리가 아프다. 빠른 시일내에 공부해서 고쳐야 겠다.
 
 #### 4. JPA에 대한 공부가 부족하다
 > 몇시간동안 제자리걸음을 한것 같다. 결과도 많이 없고 해결도 하지 못했다. 몇시간 동안 쳇바퀴를 돌았다. 정말 진빠진다. 제대로 공부해서 꼭 성공적으로 구현해야 겠다.