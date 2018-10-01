package com.example.demo.controller;

import com.example.demo.Repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.example.demo.Repository.BoardRepository;
import com.example.demo.model.board;
import com.example.demo.model.user_schema;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@EnableAutoConfiguration
public class SpringController {

//    @Autowired
//    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    BoardRepository BoardRepository;

    @Autowired
    UserRepository UserRepository;

    @Autowired
    public JavaMailSender emailSender;

///////////// 로그인, 로그아웃 + 세션 유지, 파괴 /////////////////////
    @RequestMapping(value = "/process/login", method = RequestMethod.GET)
    public String ProcessLogin(Model model, HttpSession session, HttpServletRequest req) {

        Map<String, Object> data = new HashMap();

        user_schema User = UserRepository.findByid(req.getParameter("id"));
        String encPassword=req.getParameter("password");

        if(User.getPassword()==encPassword&&User.getAuth()==1)
        {

            session.setAttribute("user",User.getNickname());
            data.put("islogin",1);
            model.addAttribute("data",data);
            return "main";
        }
        else {

            data.put("islogin",-1);
            model.addAttribute("data",data);
            return "login";
        }
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String Logout(Model model, HttpSession session, HttpServletRequest req)
    {
        session.setAttribute("user",null);
        return "main";
    }
///////////////////////////////////////////////////////////////////////////


//////////////////회원 가입, 등록, 메일인증, 그에따른 처리///////////////////////////
    @RequestMapping(value="/signup", method = RequestMethod.GET)
    public String Signup(Model model)
    {
        List<user_schema> Userlist=UserRepository.findAll();
        Map<String, Object> data=new HashMap();

        data.put("result",Userlist);
        model.addAttribute("result",data);
        return "signup";
    }

    @RequestMapping(value = "/process/signup", method = RequestMethod.POST)
    public String Adduser(Model model,HttpServletRequest req)
    {
        String id=req.getParameter("id");
        String password=req.getParameter("password");
        String name=req.getParameter("name");
        int age=20;
        String created_at=req.getParameter("create_at");
        String sex=req.getParameter("sex");
        String birth=req.getParameter("birth");
        String phone=req.getParameter("phone");
        String email=req.getParameter("email");
        String nickname=req.getParameter("nickname");

        char[] charaters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};

        StringBuffer sb = new StringBuffer();

        Random rn = new Random();

        for( int i = 0 ; i < 13 ; i++ ){
           sb.append( charaters[ rn.nextInt( charaters.length ) ] );
        }

        String tokken = sb.toString();

        UserRepository.save(new user_schema(id,password,name,age,created_at,sex,birth,phone,tokken,-1,email,nickname));

        MimeMessage msg=emailSender.createMimeMessage();
        MimeMessageHelper helper;
                try {
                    helper = new MimeMessageHelper(msg, false, "UTF-8");
                    String text = "<html><body><p>아래의 링크를 클릭해주세요 !</p>" +
                            "<a href='http://localhost:3000/auth/?id=" + id + "&tokken=" + tokken + "'>여기를 눌러 인증해주세요</a></body></html>";
                    helper.setTo(email);
                    helper.setSubject("소소에서 인증메일 보내드립니다");
                    helper.setText(text,true);
                    emailSender.send(msg);
                }catch (MessagingException e) {
                    e.printStackTrace();
                }

        return "login";

    }

    @RequestMapping(value = "/auth", method=RequestMethod.GET)
    public String Auth(Model model,@RequestParam("id") String id,@RequestParam("tokken") String tokken){

       user_schema User=UserRepository.findByid(id);

       String pivot=User.getTokken();
       //UserRepository.Authuser(id);
        if(tokken.equals(pivot)) {
            UserRepository.Authuser(id);
        }
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String Login(Model model) {

        List<board> sample = BoardRepository.customfunc("HY");
        Map<String, Object> data = new HashMap();
        data.put("name", sample.get(0).getUsername());
        data.put("contents", sample.get(0).getContents());
        model.addAttribute("data", data);
        return "login";
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public String Post(Model model, HttpServletRequest request, @RequestParam("userimage") List<MultipartFile> files) throws IOException {
        try {
            Map<String, Object> data = new HashMap();
            data.put("id", request.getParameter("id"));
            data.put("password", request.getParameter("password"));
            model.addAttribute("data",data);

            for(int i=0;i<files.size();i++){
            String sourceFileName = files.get(i).getOriginalFilename();
            String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
            File destinationFile;
            String destinationFileName;
            String fileUrl = "/Users/HY/IdeaProjects/demo/src/main/webapp/WEB-INF/uploadFiles/";

                do {
                    destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
                    destinationFile = new File(fileUrl + destinationFileName);
                } while (destinationFile.exists());

                destinationFile.getParentFile().mkdirs();
                files.get(i).transferTo(destinationFile);

            }
            return "Post";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Post";
    }
}

